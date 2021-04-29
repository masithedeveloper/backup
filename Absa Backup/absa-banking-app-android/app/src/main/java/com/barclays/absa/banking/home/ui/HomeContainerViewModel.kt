/*
 * Copyright (c) 2019 Absa Bank Limited, All Rights Reserved.
 *
 * This code is confidential to Absa Bank Limited and shall not be disclosed
 * outside the Bank without the prior written permission of the Absa Legal
 *
 * In the event that such disclosure is permitted the code shall not be copied
 * or distributed other than on a need-to-know basis and any recipients may be
 * required to sign a confidentiality undertaking in favor of Absa Bank
 * Limited
 *
 */

package com.barclays.absa.banking.home.ui

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.barclays.absa.banking.boundary.model.*
import com.barclays.absa.banking.boundary.model.policy.Policy
import com.barclays.absa.banking.boundary.model.policy.PolicyDetail
import com.barclays.absa.banking.boundary.model.policy.PolicyList
import com.barclays.absa.banking.framework.SessionManager
import com.barclays.absa.banking.framework.app.BMBApplication
import com.barclays.absa.banking.framework.dagger.getServiceInterface
import com.barclays.absa.banking.framework.services.contracts.IAppCacheService
import com.barclays.absa.banking.funeralCover.services.InsurancePolicyInteractor
import com.barclays.absa.banking.home.services.HomeScreenInteractor
import com.barclays.absa.banking.home.services.clickToCall.dto.CallBackVerificationDataModel
import com.barclays.absa.banking.home.services.clickToCall.dto.CallBackVerificationResponse
import com.barclays.absa.banking.home.services.dto.AccountHistoryRequest
import com.barclays.absa.banking.home.services.dto.PolicyListRequest
import com.barclays.absa.utils.ProfileManager
import com.barclays.absa.utils.SharedPreferenceService
import java.util.*

class HomeContainerViewModel : ViewModel() {

    private val application by lazy { BMBApplication.getInstance() }
    private val profileManager by lazy { ProfileManager.getInstance() }
    private val homeScreenInteractor by lazy { HomeScreenInteractor() }
    private val insurancePolicyInteractor by lazy { InsurancePolicyInteractor() }
    private lateinit var policy: Policy
    private lateinit var currentUserProfile: UserProfile

    val callBackLiveData by lazy { MutableLiveData<String>() }
    val callBackVerificationLiveData by lazy { MutableLiveData<CallBackVerificationResponse>() }
    val accountHistoryClearedLiveData by lazy { MutableLiveData<AccountDetail>() }
    val accountHistoryUnClearedLiveData by lazy { MutableLiveData<AccountDetail>() }
    var policyDetailLiveData = MutableLiveData<PolicyDetail>()
    val failureLiveData by lazy { MutableLiveData<Pair<FailureType, String>>() }

    private val callBackResponseListener by lazy { CallBackExtendedResponseListener(callBackLiveData, failureLiveData) }
    private val callBackVerificationResponseResponseListener by lazy { CallBackVerificationExtendedResponseListener(callBackVerificationLiveData, failureLiveData) }
    private val accountHistoryClearedResponseListener by lazy { AccountHistoryClearedExtendedResponseListener() }
    private val accountHistoryUnclearedResponseListener by lazy { AccountHistoryUnclearedExtendedResponseListener() }
    private val policyInformationResponseListener by lazy { PolicyInformationExtendedResponseListener(policyDetailLiveData, failureLiveData) }
    private val insurancePolicyListResponseListener by lazy { InsurancePolicyListExtendedResponseListener(this) }
    private val appCacheService: IAppCacheService = getServiceInterface()
    private val homeCacheService: IHomeCacheService = getServiceInterface()

    fun requestCallBack(secretCode: String, callBackDateTime: String) {
        homeScreenInteractor.requestCallBack(secretCode, callBackDateTime, callBackResponseListener)
    }

    fun requestVerificationCallBack(callBackVerificationDataModel: CallBackVerificationDataModel) {
        homeScreenInteractor.verifyCallBack(callBackVerificationDataModel, callBackVerificationResponseResponseListener)
    }

    fun fetchHomeLoanInformation(account: AccountObject) {
        homeCacheService.setSelectedHomeLoanAccount(account)
        val accountHistoryClearedTransactionsRequest = AccountHistoryRequest(account, accountHistoryClearedResponseListener, false)
        val accountHistoryUnclearedTransactionsRequest = AccountHistoryRequest(account, accountHistoryUnclearedResponseListener, true)
        val policyListRequest = PolicyListRequest(insurancePolicyListResponseListener)
        homeScreenInteractor.fetchHomeLoanAccountData(accountHistoryClearedTransactionsRequest, accountHistoryUnclearedTransactionsRequest, policyListRequest)
    }

    fun fetchPolicyInformation(policy: Policy) {
        this.policy = policy
        policyInformationResponseListener.policy = policy
        homeScreenInteractor.fetchPolicyInformation(policy.type, policy.number, policyInformationResponseListener)
    }

    fun setupSession() {
        appCacheService.setLinkingFlow(false)
        application.userLoggedInStatus = true

        initializeUserProfile()
        if (::currentUserProfile.isInitialized) {
            profileManager.updateProfile(currentUserProfile, null)
        }

        val secureHomePageObject = appCacheService.getSecureHomePageObject()
        var customerProfileObject = CustomerProfileObject.instance
        if ((customerProfileObject.customerType == null) && secureHomePageObject != null) {
            customerProfileObject = secureHomePageObject.customerProfile
        }

        if (!SessionManager.isSessionStarted) {
            SessionManager.startSession()
        }

        customerProfileObject.let {
            application.updateClientType(it.clientTypeGroup)
        }
    }

    private fun initializeUserProfile() {
        if (profileManager.activeUserProfile != null) {
            return
        }

        profileManager.loadAllUserProfiles(object : ProfileManager.OnProfileLoadListener {
            override fun onAllProfilesLoaded(userProfiles: List<UserProfile>) {
                val currentUserProfileSavedInstance = SharedPreferenceService.getCurrentUserProfileSavedInstance()
                currentUserProfileSavedInstance?.let {
                    currentUserProfile = it
                    profileManager.activeUserProfile = it
                }
            }

            override fun onProfilesLoadFailed() {

            }
        })
    }

    fun onFetchPolicyListSuccess(policyList: PolicyList) {
        homeCacheService.setInsurancePolicies(policyList.policies)
        val homeOwnerPolicy = InsuranceUtils.getHomeOwnerPolicy()
        if (homeOwnerPolicy != null) {
            policy = homeOwnerPolicy
            policyInformationResponseListener.policy = policy
            insurancePolicyInteractor.fetchPolicyDetails(policy.type, policy.number, policyInformationResponseListener)
        } else {
            //pull out home loan summary services
            homeCacheService.getHomeLoanAccountHistoryCleared()?.let { homeLoanAccountHistoryCleared ->
                appCacheService.setAccountDetail(homeLoanAccountHistoryCleared)
                accountHistoryClearedLiveData.value = homeLoanAccountHistoryCleared
            }
        }
    }

    /** onFailure is called when there is no insurance account
     *  with message: For Absa idirect motor and household insurance, SMS "insure" to 43755.
     *  For life and funeral insurance, SMS 'Life' to 31513.
     *  so we ignore it
     */
    fun onFetchPolicyListFailed(errorMessage: String) {
        if (errorMessage.toLowerCase(Locale.ROOT).contains("idirect")) {
            homeCacheService.getHomeLoanAccountHistoryCleared()?.let { homeLoanAccountHistoryCleared ->
                accountHistoryClearedLiveData.value = homeLoanAccountHistoryCleared
            }
        } else {
            failureLiveData.value = Pair(FailureType.POLICY_LIST, errorMessage)
        }
    }

    fun refreshAccountList() {
        val secureHomePageObject = appCacheService.getSecureHomePageObject()
        val entries = mutableListOf<Entry>()
        val filteredEntries = mutableListOf<Entry>()
        val accounts = secureHomePageObject?.accounts

        if (!accounts.isNullOrEmpty()) {
            entries.addAll(accounts)
        }

        entries.forEach {
            val isValidAccount = when (it.entryType) {
                Entry.ACCOUNT -> true
                Entry.POLICY -> true
                else -> false
            }

            if (isValidAccount) {
                filteredEntries.add(it as AccountObject)
            }
        }
        homeCacheService.setFilteredAccounts(filteredEntries)
    }

    fun stopListeningForAuth() {
        if (!appCacheService.isPrimarySecondFactorDevice()) {
            application.stopListeningForAuth()
        }
    }
}