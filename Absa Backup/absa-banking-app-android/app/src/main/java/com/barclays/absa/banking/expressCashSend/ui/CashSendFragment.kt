/*
 * Copyright (c) 2021 Absa Bank Limited, All Rights Reserved.
 *
 * This code is confidential to Absa Bank Limited and shall not be disclosed
 * outside the Bank without the prior written permission of the Absa Legal
 *
 * In the event that such disclosure is permitted the code shall not be copied
 * or distributed other than on a need-to-know basis and any recipients may be
 * required to sign a confidentiality undertaking in favor of Absa Bank
 * Limited
 */
package com.barclays.absa.banking.expressCashSend.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import com.barclays.absa.banking.R
import com.barclays.absa.banking.beneficiaries.services.IBeneficiaryCacheService
import com.barclays.absa.banking.boundary.model.cashSend.CashSendUnredeemedAccounts
import com.barclays.absa.banking.cashSend.ui.UnredeemedTransactionActivity
import com.barclays.absa.banking.cashSendPlus.services.CashSendPlusInteractor
import com.barclays.absa.banking.cashSendPlus.services.CheckCashSendPlusRegistrationStatusResponse
import com.barclays.absa.banking.cashSendPlus.ui.CashSendPlusRegistrationActivity
import com.barclays.absa.banking.cashSendPlus.ui.CashSendPlusSendMultipleActivity
import com.barclays.absa.banking.cashSendPlus.ui.CashSendPlusUtils.isNotFound
import com.barclays.absa.banking.databinding.CashSendFragmentBinding
import com.barclays.absa.banking.express.cashSend.cashSendListBeneficiaries.CashSendBeneficiariesViewModel
import com.barclays.absa.banking.express.cashSend.cashSendListBeneficiaries.dto.CashSendBeneficiariesResponse
import com.barclays.absa.banking.express.cashSend.cashSendListBeneficiaries.dto.CashSendBeneficiary
import com.barclays.absa.banking.expressCashSend.ui.CashSendActivity.Companion.IS_CASH_SEND_PLUS
import com.barclays.absa.banking.framework.BaseFragment
import com.barclays.absa.banking.framework.ExtendedResponseListener
import com.barclays.absa.banking.framework.analytics.AnalyticsUtils
import com.barclays.absa.banking.framework.app.BMBConstants
import com.barclays.absa.banking.framework.dagger.getServiceInterface
import com.barclays.absa.banking.framework.featureSwitching.FeatureSwitchingCache.featureSwitchingToggles
import com.barclays.absa.banking.framework.featureSwitching.FeatureSwitchingStates
import com.barclays.absa.utils.AnalyticsUtil.tagCashSend
import com.barclays.absa.utils.AnalyticsUtil.trackAction
import com.barclays.absa.utils.OperatorPermissionUtils
import com.barclays.absa.utils.extensions.viewBinding
import styleguide.cards.Alert

class CashSendFragment : BaseFragment(R.layout.cash_send_fragment) {
    private val binding by viewBinding(CashSendFragmentBinding::bind)
    private val cashSendBeneficiariesViewModel by viewModels<CashSendBeneficiariesViewModel>()
    private val cashSendViewModel by activityViewModels<CashSendViewModel>()
    private val beneficiaryCache : IBeneficiaryCacheService = getServiceInterface()
    private val cashSendPlusService = CashSendPlusInteractor()

    private lateinit var cashSendBeneficiariesAdapter: CashSendBeneficiariesAdapter
    private var isCashSendPlus = false

    // TODO: 2021/01/13 listeners should be removed when services ready
    private val viewUnredeemedExtendedResponseListener: ExtendedResponseListener<CashSendUnredeemedAccounts> = object : ExtendedResponseListener<CashSendUnredeemedAccounts>() {
        override fun onSuccess(cashSendUnredeemedAccounts: CashSendUnredeemedAccounts) {
            Intent(context, UnredeemedTransactionActivity::class.java).apply {
                putExtra(BMBConstants.RESULT, cashSendUnredeemedAccounts)
                putExtra(IS_CASH_SEND_PLUS, isCashSendPlus)
                startActivity(this)
                dismissProgressDialog()
            }
        }
    }

    private val checkCashPlusRegStatusExtendedResponseListener: ExtendedResponseListener<CheckCashSendPlusRegistrationStatusResponse> = object : ExtendedResponseListener<CheckCashSendPlusRegistrationStatusResponse>() {
        override fun onSuccess(registrationStatusResponse: CheckCashSendPlusRegistrationStatusResponse) {
            navigateToCashSendPlusRegistration(registrationStatusResponse)
            appCacheService.setCashSendPlusRegistrationStatus(registrationStatusResponse)
            dismissProgressDialog()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewUnredeemedExtendedResponseListener.setView(this)

        cashSendViewModel.isCashSendPlus.observe(viewLifecycleOwner, {
            isCashSendPlus = it
            if (isCashSendPlus) {
                setToolBar(R.string.cash_send_plus_title, true)
            } else {
                setToolBar(R.string.cashsend, true)
            }
            cashSendBeneficiariesViewModel.fetchCashSendBeneficiaries(false)
            setUpObservers()

            if (isBusinessAccount && !OperatorPermissionUtils.isOperator() && featureSwitchingToggles.businessBankingCashSendPlus == FeatureSwitchingStates.ACTIVE.key) {
                val cachedRegistrationStatus = appCacheService.getCashSendPlusRegistrationStatus()
                if (cachedRegistrationStatus != null) {
                    navigateToCashSendPlusRegistration(cachedRegistrationStatus)
                } else {
                    checkCashSendPlusStatus()
                }
            }
        })

        setupListeners()
        AnalyticsUtils.getInstance().trackCustomScreenView(BMBConstants.CASHSEND_HOME_CONST, BMBConstants.CASHSEND_CONST, BMBConstants.TRUE_CONST)
    }

    private fun setUpObservers() {
        cashSendBeneficiariesViewModel.cashSendBeneficiariesResponse.observe(viewLifecycleOwner) { cashSendBeneficiaries ->
            buildBeneficiaryList(cashSendBeneficiaries)
            buildRecentBeneficiaryList(cashSendBeneficiaries)
            dismissProgressDialog()
            cashSendViewModel.beneficiaryList = cashSendBeneficiaries.beneficiaryList
        }

        cashSendViewModel.searchString.observe(viewLifecycleOwner) {
            if (::cashSendBeneficiariesAdapter.isInitialized && it.isNotEmpty()) {
                cashSendBeneficiariesAdapter.search(it)
            }
        }
    }

    private fun buildRecentBeneficiaryList(cashSendBeneficiaries: CashSendBeneficiariesResponse) {
        if (cashSendBeneficiaries.beneficiaryList.isEmpty()) {
            binding.noBeneficiariesGroup.visibility = View.VISIBLE
            binding.beneficiariesRecyclerView.visibility = View.GONE
        } else {
            binding.noBeneficiariesGroup.visibility = View.GONE
            binding.beneficiariesRecyclerView.visibility = View.VISIBLE
            cashSendBeneficiariesAdapter = CashSendBeneficiariesAdapter(cashSendBeneficiaries.beneficiaryList, object : OnBeneficiaryClickListener {
                override fun onBeneficiaryClicked(beneficiary: CashSendBeneficiary) {
                    cashSendViewModel.cashSendFlow = CashSendFlow.CASH_SEND_TO_BENEFICIARY
                    cashSendViewModel.beneficiaryDetail = beneficiary
                    navigate(CashSendFragmentDirections.actionCashSendFragmentToCashSendDetailFragment())
                }
            })
            binding.beneficiariesRecyclerView.adapter = cashSendBeneficiariesAdapter
        }
    }

    private fun buildBeneficiaryList(cashSendBeneficiaries: CashSendBeneficiariesResponse) {
        if (cashSendBeneficiaries.recentlyPaidBeneficiaryList.isEmpty()) {
            binding.recentBeneficiariesGroup.visibility = View.VISIBLE
            binding.recentBeneficiariesRecyclerView.visibility = View.GONE
        } else {
            binding.recentBeneficiariesGroup.visibility = View.GONE
            binding.recentBeneficiariesRecyclerView.visibility = View.VISIBLE

            binding.recentBeneficiariesRecyclerView.adapter = CashSendBeneficiariesAdapter(cashSendBeneficiaries.recentlyPaidBeneficiaryList, object : OnBeneficiaryClickListener {
                override fun onBeneficiaryClicked(beneficiary: CashSendBeneficiary) {
                    cashSendViewModel.cashSendFlow = CashSendFlow.CASH_SEND_TO_BENEFICIARY
                    cashSendViewModel.beneficiaryDetail = beneficiary
                    navigate(CashSendFragmentDirections.actionCashSendFragmentToCashSendDetailFragment())
                }
            }, false)
        }
    }

    private fun navigateToCashSendPlusRegistration(registrationStatusResponse: CheckCashSendPlusRegistrationStatusResponse) {
        if (isNotFound(registrationStatusResponse.cashSendPlusResponseData)) {
            showCashSendPlusBanner()
        }
    }

    private fun checkCashSendPlusStatus() {
        cashSendPlusService.sendCheckCashSendPlusRegistration(checkCashPlusRegStatusExtendedResponseListener)
    }

    private fun setupListeners() {
        with(binding) {
            unredeemedView.setOnClickListener { navigateToCashSendUnredeemedTransactions() }
            sendCashToMyselfView.setOnClickListener {
                cashSendViewModel.cashSendFlow = CashSendFlow.CASH_SEND_TO_SELF
                navigate(CashSendFragmentDirections.actionCashSendFragmentToCashSendDetailFragment())
            }
            onceOffView.setOnClickListener { navigateToSendOnceOffCashSend() }
            sendCashToSomeoneNewView.setOnClickListener { navigateToAddNewCashSendBeneficiary() }
            sendMultipleButtonView.setOnClickListener { navigateToCashSendPlusSendMultiple() }
            addNewBeneficiaryImageView.setOnClickListener { navigateToAddNewCashSendBeneficiary() }
            if (isCashSendPlus && beneficiaryCache.getCashSendBeneficiaries().isNotEmpty()) {
                sendMultipleButtonView.visibility = View.VISIBLE
            }
        }
    }

    private fun navigateToAddNewCashSendBeneficiary() {
        tagCashSend("BeneficiaryCashSendPlusTab_AddBeneficiaryClicked")
        navigate(CashSendFragmentDirections.actionCashSendFragmentToCashSendToNewBeneficiaryFragment())
    }

    private fun navigateToCashSendPlusRegistration() {
        trackAction(BMBConstants.CASHSEND_CONST, "CSPRegister_CashSendMenu_RegisterClicked")
        startActivity(Intent(baseActivity, CashSendPlusRegistrationActivity::class.java))
    }

    private fun navigateToCashSendPlusSendMultiple() {
        startActivity(Intent(baseActivity, CashSendPlusSendMultipleActivity::class.java))
    }

    private fun navigateToSendOnceOffCashSend() {
        cashSendViewModel.cashSendFlow = CashSendFlow.ONCE_OFF_CASH_SEND
        navigate(CashSendFragmentDirections.actionCashSendFragmentToCashSendDetailFragment())
    }

    private fun navigateToCashSendUnredeemedTransactions() {
        navigate(CashSendFragmentDirections.actionCashSendFragmentToCashSendUnredeemedTransactionsFragment())
    }

    private fun showCashSendPlusBanner() {
        with(binding.bannerAlertView) {
            visibility = View.VISIBLE
            setAlert(Alert("", getString(R.string.register_for_cash_send_now)))
            hideRightImage()
            setOnClickListener { navigateToCashSendPlusRegistration() }
        }
    }
}