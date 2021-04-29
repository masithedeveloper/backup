/*
 *  Copyright (c) 2020 Absa Bank Limited, All Rights Reserved.
 *
 *  This code is confidential to Absa Bank Limited and shall not be disclosed
 *  outside the Bank without the prior written permission of the Absa Legal
 *
 *  In the event that such disclosure is permitted the code shall not be copied
 *  or distributed other than on a need-to-know basis and any recipients may be
 *  required to sign a confidentiality undertaking in favor of Absa Bank
 *  Limited
 *
 */
package com.barclays.absa.banking.cashSendPlus.ui

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.fragment.app.activityViewModels
import com.barclays.absa.banking.R
import com.barclays.absa.banking.framework.BaseFragment
import com.barclays.absa.banking.framework.app.BMBConstants
import com.barclays.absa.banking.framework.app.BMBConstants.CASHSEND_CONST
import com.barclays.absa.banking.newToBank.services.dto.ResultAnimations
import com.barclays.absa.banking.presentation.sureCheck.SureCheckDelegate
import com.barclays.absa.utils.AnalyticsUtil
import kotlinx.android.synthetic.main.cash_send_plus_cancellation_fragment.*
import styleguide.screens.GenericResultScreenFragment
import styleguide.screens.GenericResultScreenProperties

class CashSendPlusCancellationFragment : BaseFragment(R.layout.cash_send_plus_cancellation_fragment) {
    private lateinit var sureCheckDelegate: SureCheckDelegate
    private val cashSendPlusViewModel by activityViewModels<CashSendPlusViewModel>()

    override fun onAttach(context: Context) {
        super.onAttach(context)
        sureCheckDelegate = object : SureCheckDelegate(baseActivity) {
            override fun onSureCheckProcessed() {
                cashSendPlusViewModel.sendCashSendPlusRegistrationCancellation()
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        cancelCashSendPlusButton.setOnClickListener {
            cashSendPlusViewModel.sendCashSendPlusRegistrationCancellation()
        }
        goBackButton.setOnClickListener { baseActivity.finish() }
        setupObserver()
    }

    private fun setupObserver() {
        cashSendPlusViewModel.cancelCashSendPlusRegistrationResponse.observe(viewLifecycleOwner, { successResponse ->
            dismissProgressDialog()
            if (BMBConstants.CONST_FAILURE.equals(successResponse.transactionStatus, true)) {
                navigateToFailedResultScreen()
            } else {
                sureCheckDelegate.processSureCheck(baseActivity, successResponse) {
                    val responseData = successResponse.cashSendPlusResponseData
                    when {
                        CashSendPlusUtils.isSuccess(responseData) -> navigateToSuccessResultScreen()
                        CashSendPlusUtils.isPendingAuthorization(responseData) -> navigateToPendingResultScreen()
                        CashSendPlusUtils.cancelInProgress(responseData) -> navigateInProgressResultScreen()
                        CashSendPlusUtils.isFailure(responseData) -> navigateToFailedResultScreen()
                        else -> navigateToErrorResultScreen()
                    }
                }
            }
        })
    }

    private fun navigateToSuccessResultScreen() {
        AnalyticsUtil.trackAction(CASHSEND_CONST, "BBCashSendPlus_CancelSuccess_ScreenDisplayed")
        GenericResultScreenProperties.PropertiesBuilder().apply {
            setResultScreenAnimation(ResultAnimations.generalSuccess)
            setTitle(getString(R.string.cash_send_plus_cancellation_successful))
            setPrimaryButtonLabel(getString(R.string.done))
            GenericResultScreenFragment.setPrimaryButtonOnClick {
                cashSendPlusViewModel.sendCheckCashSendPlusRegistration()

                cashSendPlusViewModel.failureResponse.observe(requireActivity(), {
                    dismissProgressDialog()
                    navigateToHomeScreenWithoutReloadingAccounts()
                })

                cashSendPlusViewModel.checkCashSendPlusRegistrationStatusResponse.observe(requireActivity(), {
                    dismissProgressDialog()
                    appCacheService.setCashSendPlusRegistrationStatus(it)
                    navigateToHomeScreenWithoutReloadingAccounts()
                })
            }
            navigate(CashSendPlusCancellationFragmentDirections.actionCashSendPlusCancellationFragmentToGenericResultFragment(build(true)))
        }
    }

    private fun navigateToPendingResultScreen() {
        AnalyticsUtil.trackAction(CASHSEND_CONST, "BBCashSendPlus_CancelAuthorisationPending_ScreenDisplayed")
        GenericResultScreenProperties.PropertiesBuilder().apply {
            setResultScreenAnimation(ResultAnimations.generalSuccess)
            setTitle(getString(R.string.cash_send_plus_cancellation_pending_authorization))
            setDescription(getString(R.string.cash_send_plus_transaction_expire_midnight))
            setContactViewContactName(getString(R.string.contact_centre))
            setContactViewContactNumber(getString(R.string.business_banking_contact_centre_number))
            setPrimaryButtonLabel(getString(R.string.done))
            GenericResultScreenFragment.setPrimaryButtonOnClick {
                loadAccountsAndGoHome()
            }
            navigate(CashSendPlusCancellationFragmentDirections.actionCashSendPlusCancellationFragmentToGenericResultFragment(build(true)))
        }
    }

    private fun navigateInProgressResultScreen() {
        hideToolBar()
        GenericResultScreenProperties.PropertiesBuilder().apply {
            setResultScreenAnimation(ResultAnimations.generalSuccess)
            setTitle(getString(R.string.cash_send_plus_cancellation_already_in_progress))
            setDescription(getString(R.string.cash_send_plus_cancellation_is_pending_authorization))
            setPrimaryButtonLabel(getString(R.string.done))
            GenericResultScreenFragment.setPrimaryButtonOnClick {
                navigateToHomeScreenWithoutReloadingAccounts()
            }
            navigate(CashSendPlusCancellationFragmentDirections.actionCashSendPlusCancellationFragmentToGenericResultFragment(build(true)))
        }
    }

    private fun navigateToFailedResultScreen() {
        AnalyticsUtil.trackAction(CASHSEND_CONST, "BBCashSendPlus_CancelFailure_ScreenDisplayed")
        GenericResultScreenProperties.PropertiesBuilder().apply {
            setResultScreenAnimation(ResultAnimations.generalFailure)
            setTitle(getString(R.string.cash_send_plus_cancellation_unsuccessful))
            setPrimaryButtonLabel(getString(R.string.done))
            GenericResultScreenFragment.setPrimaryButtonOnClick {
                navigateToHomeScreenWithoutReloadingAccounts()
            }
            navigate(CashSendPlusCancellationFragmentDirections.actionCashSendPlusCancellationFragmentToGenericResultFragment(build(false)))
        }
    }

    private fun navigateToErrorResultScreen() {
        AnalyticsUtil.trackAction(CASHSEND_CONST, "BBCashSendPlus_CancelError_ScreenDisplayed")
        GenericResultScreenProperties.PropertiesBuilder().apply {
            setResultScreenAnimation(ResultAnimations.generalError)
            setTitle(getString(R.string.something_went_wrong))
            setDescription(getString(R.string.cash_send_plus_technical_error_occurred))
            setContactViewContactName(getString(R.string.contact_centre))
            setContactViewContactNumber(getString(R.string.business_banking_contact_centre_number))
            setPrimaryButtonLabel(getString(R.string.done))
            GenericResultScreenFragment.setPrimaryButtonOnClick {
                navigateToHomeScreenWithoutReloadingAccounts()
            }
            navigate(CashSendPlusCancellationFragmentDirections.actionCashSendPlusCancellationFragmentToGenericResultFragment(build(false)))
        }
    }
}