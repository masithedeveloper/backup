/*
 * Copyright (c) 2020 Absa Bank Limited, All Rights Reserved.
 *
 * This code is confidential to Absa Bank Limited and shall not be disclosed
 * outside the Bank without the prior written permission of the Absa Legal
 *
 * In the event that such disclosure is permitted the code shall not be copied
 * or distributed other than on a need-to-know basis and any recipients may be
 * required to sign a confidentiality undertaking in favor of Absa Bank
 * Limited
 */
package com.barclays.absa.banking.home.ui

import androidx.lifecycle.ViewModelProvider
import com.barclays.absa.banking.R
import com.barclays.absa.banking.boundary.model.authorisations.AuthorisationTransactionList
import com.barclays.absa.banking.boundary.model.policy.Policy
import com.barclays.absa.banking.boundary.model.policy.PolicyList
import com.barclays.absa.banking.debiCheck.services.dto.NumberOfPendingMandatesRequest
import com.barclays.absa.banking.debiCheck.services.dto.NumberOfPendingMandatesResponse
import com.barclays.absa.banking.dualAuthorisations.services.AuthorisationTransactionListRequest
import com.barclays.absa.banking.express.behaviouralRewards.fetchAllChallenges.BehaviouralRewardsFetchAllChallengesViewModel
import com.barclays.absa.banking.express.behaviouralRewards.fetchAllChallenges.dto.BehaviouralRewardsAllChallengesResponse
import com.barclays.absa.banking.framework.BaseActivity
import com.barclays.absa.banking.framework.ExtendedRequest
import com.barclays.absa.banking.framework.ExtendedResponseListener
import com.barclays.absa.banking.framework.app.BMBApplication
import com.barclays.absa.banking.framework.dagger.getServiceInterface
import com.barclays.absa.banking.framework.data.ResponseObject
import com.barclays.absa.banking.framework.data.cache.IRewardsCacheService
import com.barclays.absa.banking.framework.featureSwitching.FeatureSwitchingCache
import com.barclays.absa.banking.framework.featureSwitching.FeatureSwitchingStates
import com.barclays.absa.banking.framework.services.contracts.IAppCacheService
import com.barclays.absa.banking.framework.utils.BMBLogger.d
import com.barclays.absa.banking.home.AccountAdapter.Companion.ADVANTAGE
import com.barclays.absa.banking.home.services.HomeScreenInteractor
import com.barclays.absa.banking.home.services.dto.PolicyListRequest
import com.barclays.absa.banking.lotto.services.LottoSourceAccountsRequest
import com.barclays.absa.banking.lotto.services.SourceAccountsResponse
import com.barclays.absa.banking.moneyMarket.service.MoneyMarketStatusRequest
import com.barclays.absa.banking.moneyMarket.service.dto.MoneyMarketStatus
import com.barclays.absa.banking.personalLoan.services.CreditLimitsRequest
import com.barclays.absa.banking.personalLoan.services.CreditLimitsResponse
import com.barclays.absa.integration.DeviceProfilingInteractor
import com.barclays.absa.utils.AbsaCacheManager
import java.util.*
import java.util.regex.Matcher
import java.util.regex.Pattern

internal class FetchPolicyAuthorizations(private val onRequestComplete: OnRequestComplete) {

    private val appCacheService: IAppCacheService = getServiceInterface()
    private val homeCacheService: IHomeCacheService = getServiceInterface()
    private val rewardsCacheService: IRewardsCacheService = getServiceInterface()

    fun fetchHomeScreenData() {
        val orderedRequestList: MutableList<ExtendedRequest<*>> = ArrayList()
        orderedRequestList.add(PolicyListRequest(policyListResponseListener))
        orderedRequestList.add(AuthorisationTransactionListRequest(authorisationTransactionListResponseListener))
        orderedRequestList.add(NumberOfPendingMandatesRequest(numberOfPendingMandatesResponseExtendedResponseListener))
        orderedRequestList.add(CreditLimitsRequest(creditLimitsExtendedResponseListener))
        orderedRequestList.add(LottoSourceAccountsRequest(lottoSourceAccountResponseListener))
        orderedRequestList.add(MoneyMarketStatusRequest(moneyMarketStatusResponseLister))
        HomeScreenInteractor().fetchHomeScreenData(orderedRequestList)
    }

    private fun setHasAuthorizations(authorisationTransactionList: AuthorisationTransactionList) {
        val hasAuthorizations = !authorisationTransactionList.authorisationTransactionList.isNullOrEmpty()
        homeCacheService.setHasAuthorizations(hasAuthorizations)
    }

    private fun setHasPendingMandates(successResponse: NumberOfPendingMandatesResponse) = homeCacheService.setNumberOfPendingMandates(successResponse.numberOfPendingMandates)

    private fun checkHasFuneralCover(policyList: List<Policy>) {
        val pattern = Pattern.compile(BMBApplication.getInstance().topMostActivity.resources.getString(R.string.policy_class))
        var matcher: Matcher
        var hasFuneralCover = false
        policyList.forEach { policy ->
            policy.description?.let {
                matcher = pattern.matcher(it)
                if ("LI".equals(policy.type, ignoreCase = true) && matcher.find()) {
                    hasFuneralCover = true
                }
            }
        }
        homeCacheService.setHasFuneralCover(hasFuneralCover)
    }

    private val policyListResponseListener: ExtendedResponseListener<PolicyList> = object : ExtendedResponseListener<PolicyList>() {
        override fun onRequestStarted() {}

        override fun onSuccess(policyList: PolicyList) {
            val policies = policyList.policies
            if (policies.isNotEmpty()) {
                homeCacheService.setInsurancePolicies(policies)
                checkHasFuneralCover(policies)
            } else {
                checkHasFuneralCover(ArrayList())
            }
        }

        override fun onFailure(response: ResponseObject) {
            // onFailure is called when there is no insurance account
            // with message: For Absa idirect motor and household insurance, SMS "insure" to 43755.
            // For life and funeral insurance, SMS 'Life' to 31513.
            // so we ignore it
            d("x-policy", ResponseObject.extractErrorMessage(response))
            checkHasFuneralCover(ArrayList())
        }
    }

    private val authorisationTransactionListResponseListener: ExtendedResponseListener<AuthorisationTransactionList> = object : ExtendedResponseListener<AuthorisationTransactionList>() {
        override fun onRequestStarted() {}
        override fun onSuccess(responseObject: AuthorisationTransactionList) {
            setHasAuthorizations(responseObject)
        }

        override fun onFailure(failureResponse: ResponseObject) {}
    }

    private val numberOfPendingMandatesResponseExtendedResponseListener: ExtendedResponseListener<NumberOfPendingMandatesResponse> = object : ExtendedResponseListener<NumberOfPendingMandatesResponse>() {
        override fun onSuccess(successResponse: NumberOfPendingMandatesResponse) {
            setHasPendingMandates(successResponse)
        }

        override fun onFailure(failureResponse: ResponseObject) {}
    }

    private val creditLimitsExtendedResponseListener: ExtendedResponseListener<CreditLimitsResponse> = object : ExtendedResponseListener<CreditLimitsResponse>() {
        override fun onSuccess(successResponse: CreditLimitsResponse) = homeCacheService.setPersonalLoanResponse(successResponse)
        override fun onFailure(failureResponse: ResponseObject) {}
    }

    private val lottoSourceAccountResponseListener: ExtendedResponseListener<SourceAccountsResponse> = object : ExtendedResponseListener<SourceAccountsResponse>() {
        override fun onSuccess(successResponse: SourceAccountsResponse) = homeCacheService.setLottoSourceAccountList(successResponse.sourceAccounts)
        override fun onFailure(failureResponse: ResponseObject) {}
    }

    private val moneyMarketStatusResponseLister: ExtendedResponseListener<MoneyMarketStatus> = object : ExtendedResponseListener<MoneyMarketStatus>() {
        override fun onSuccess(successResponse: MoneyMarketStatus) {
            homeCacheService.setMoneyMarketStatusList(successResponse.orbitAccountStatus.orbitAccounts)
            callScoringForSession()
        }

        override fun onFailure(failureResponse: ResponseObject) {
            callScoringForSession()
        }
    }

    private fun callScoringForSession() {
        if (BMBApplication.getInstance().isDeviceProfilingActive && !appCacheService.hasCalledForScoreInThisSession()) {
            appCacheService.setAlreadyCalledForScoreInThisSession(true)
            BMBApplication.getInstance().deviceProfilingInteractor.callForDeviceProfilingScoreForLogin(object : DeviceProfilingInteractor.NextActionCallback {
                override fun onNextAction() {
                    fetchBehaviouralRewardsIfAvailable()
                }
            })
        } else {
            fetchBehaviouralRewardsIfAvailable()
        }
    }

    private fun fetchBehaviouralRewardsIfAvailable() {
        if (FeatureSwitchingCache.featureSwitchingToggles.behaviouralRewards == FeatureSwitchingStates.ACTIVE.key) {
            fetchBehaviouralRewards()
        } else {
            onRequestComplete.onSuccess()
        }
    }

    private fun fetchBehaviouralRewards() {
        val topMostActivity = BMBApplication.getInstance().topMostActivity as BaseActivity
        val fetchAllChallengesViewModel = ViewModelProvider(topMostActivity).get(BehaviouralRewardsFetchAllChallengesViewModel::class.java)

        fetchAllChallengesViewModel.failureLiveData.observe(topMostActivity, {
            removeAdvantageCard()
            onRequestComplete.onSuccess()
        })

        fetchAllChallengesViewModel.fetchAllChallenges()

        fetchAllChallengesViewModel.allChallengesLiveData.observe(topMostActivity, { behaviouralRewardsAllChallengesResponse: BehaviouralRewardsAllChallengesResponse ->
            rewardsCacheService.setBehaviouralRewardsChallenges(behaviouralRewardsAllChallengesResponse)

            if (behaviouralRewardsAllChallengesResponse.challenges.all { it.customerChallengeStatus.status.isEmpty() }) {
                removeAdvantageCard()
            }

            onRequestComplete.onSuccess()
        })
    }

    private fun removeAdvantageCard() {
        with(AbsaCacheManager.getInstance()) {
            accountsList.accountsList.firstOrNull { it.accountType == ADVANTAGE }?.let { removeAccountFromList(it) }
        }
    }

    interface OnRequestComplete {
        fun onSuccess()
    }
}