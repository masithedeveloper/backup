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
 *
 */
package com.barclays.absa.banking.cashSendPlus.ui

import android.content.Context
import android.os.Bundle
import android.view.View
import com.barclays.absa.banking.R
import com.barclays.absa.banking.framework.app.BMBConstants
import com.barclays.absa.banking.framework.app.BMBConstants.CASHSEND_CONST
import com.barclays.absa.banking.newToBank.services.dto.ResultAnimations
import com.barclays.absa.banking.presentation.sureCheck.SureCheckDelegate
import com.barclays.absa.utils.AnalyticsUtil
import com.barclays.absa.utils.TextFormatUtils
import kotlinx.android.synthetic.main.cash_send_plus_registration_overview_fragment.*
import styleguide.screens.GenericResultScreenFragment
import styleguide.screens.GenericResultScreenProperties

class CashSendPlusRegistrationOverviewFragment : CashSendPlusBaseFragment(R.layout.cash_send_plus_registration_overview_fragment) {
    private lateinit var sureCheckDelegate: SureCheckDelegate

    override fun onAttach(context: Context) {
        super.onAttach(context)
        sureCheckDelegate = object : SureCheckDelegate(baseActivity) {
            override fun onSureCheckProcessed() {
                cashSendPlusViewModel.cashSendPlusRegistration.apply {
                    cashSendPlusViewModel.sendCashSendPlusRegistration(amountLimit, emailAddress)
                }
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        cashSendPlusActivity.apply {
            setToolbarTitle(getString(R.string.cash_send_plus_registration_overview))
            showToolbarBackArrow()
            showToolbar()
        }

        confirmRegistrationButton.setOnClickListener {
            cashSendPlusViewModel.cashSendPlusRegistration.apply {
                cashSendPlusViewModel.sendCashSendPlusRegistration(amountLimit, emailAddress)
            }
        }
        populateData()
        setupObserver()
    }

    private fun populateData() {
        cashSendPlusViewModel.cashSendPlusRegistration.apply {
            amountLimitPrimaryContentAndLabelView.setContentText(TextFormatUtils.formatBasicAmountAsRand(amountLimit))
            emailAddressSecondaryContentAndLabelView.setContentText(emailAddress)
        }
    }

    private fun setupObserver() {
        cashSendPlusViewModel.cashSendPlusRegistrationResponse.observe(viewLifecycleOwner, { successResponse ->
            dismissProgressDialog()
            if (BMBConstants.FAILURE.equals(successResponse.transactionStatus, true)) {
                navigateToFailedResultScreen()
            } else {
                val responseData = successResponse.registerForCashSendPlusDTO
                sureCheckDelegate.processSureCheck(baseActivity, successResponse) {
                    when {
                        CashSendPlusUtils.isSuccess(responseData) -> {
                            appCacheService.getCashSendPlusRegistrationStatus()?.apply {
                                cashSendPlusResponseData = responseData
                                appCacheService.setCashSendPlusRegistrationStatus(this)
                            }
                            navigateToSuccessResultScreen()
                        }
                        CashSendPlusUtils.isPendingAuthorization(responseData) -> navigateToPendingResultScreen()
                        CashSendPlusUtils.inProgress(responseData) -> navigateInProgressResultScreen()
                        CashSendPlusUtils.isFailure(responseData) -> navigateToFailedResultScreen()
                        else -> navigateToErrorResultScreen()
                    }
                }
            }
        })
    }

    private fun navigateToSuccessResultScreen() {
        AnalyticsUtil.trackAction(CASHSEND_CONST, "BBCashSendPlus_RegisterSuccess_ScreenDisplayed")
        hideToolBar()
        GenericResultScreenProperties.PropertiesBuilder().apply {
            setResultScreenAnimation(ResultAnimations.generalSuccess)
            setTitle(getString(R.string.cash_send_plus_registration_successful))
            setDescription(getString(R.string.make_multiple_cash_send_payment))
            setPrimaryButtonLabel(getString(R.string.done))
            GenericResultScreenFragment.setPrimaryButtonOnClick {
                loadAccountsAndGoHome()
            }
            navigate(CashSendPlusRegistrationOverviewFragmentDirections.actionCashSendPlusRegistrationOverviewFragmentToGenericResultFragment(build(true)))
        }
    }

    private fun navigateToPendingResultScreen() {
        AnalyticsUtil.trackAction(CASHSEND_CONST, "BBCashSendPlus_RegisterAuthorisationPending_ScreenDisplayed")
        hideToolBar()
        GenericResultScreenProperties.PropertiesBuilder().apply {
            setResultScreenAnimation(ResultAnimations.generalSuccess)
            setTitle(getString(R.string.cash_send_plus_registration_pending_authorization))
            setDescription(getString(R.string.cash_send_plus_call_contact_centre))
            setContactViewContactName(getString(R.string.contact_centre))
            setContactViewContactNumber(getString(R.string.business_banking_contact_centre_number))
            setPrimaryButtonLabel(getString(R.string.done))
            GenericResultScreenFragment.setPrimaryButtonOnClick {
                loadAccountsAndGoHome()
            }
            navigate(CashSendPlusRegistrationOverviewFragmentDirections.actionCashSendPlusRegistrationOverviewFragmentToGenericResultFragment(build(true)))
        }
    }

    private fun navigateInProgressResultScreen() {
        AnalyticsUtil.trackAction(CASHSEND_CONST, "BBCashSendPlus_RegisterInProgress_ScreenDisplayed")
        hideToolBar()
        GenericResultScreenProperties.PropertiesBuilder().apply {
            setResultScreenAnimation(ResultAnimations.generalSuccess)
            setTitle(getString(R.string.cash_send_plus_registration_already_in_progress))
            setDescription(getString(R.string.cash_send_plus_registration_is_pending_authorization))
            setPrimaryButtonLabel(getString(R.string.done))
            GenericResultScreenFragment.setPrimaryButtonOnClick {
                navigateToHomeScreenWithoutReloadingAccounts()
            }
            navigate(CashSendPlusRegistrationOverviewFragmentDirections.actionCashSendPlusRegistrationOverviewFragmentToGenericResultFragment(build(true)))
        }
    }

    private fun navigateToFailedResultScreen() {
        hideToolBar()
        GenericResultScreenProperties.PropertiesBuilder().apply {
            setResultScreenAnimation(ResultAnimations.generalFailure)
            setTitle(getString(R.string.cash_send_plus_registration_unsuccessful))
            setDescription(getString(R.string.transaction_rejected))
            setPrimaryButtonLabel(getString(R.string.done))
            GenericResultScreenFragment.setPrimaryButtonOnClick {
                navigateToHomeScreenWithoutReloadingAccounts()
            }
            navigate(CashSendPlusRegistrationOverviewFragmentDirections.actionCashSendPlusRegistrationOverviewFragmentToGenericResultFragment(build(false)))
        }
    }

    private fun navigateToErrorResultScreen() {
        AnalyticsUtil.trackAction(CASHSEND_CONST, "BBCashSendPlus_RegisterError_ScreenDisplayed")
        hideToolBar()
        GenericResultScreenProperties.PropertiesBuilder().apply {
            setResultScreenAnimation(ResultAnimations.generalError)
            setTitle(getString(R.string.something_went_wrong))
            setDescription(getString(R.string.cash_send_plus_technical_error_occurred))
            setPrimaryButtonLabel(getString(R.string.done))
            setContactViewContactName(getString(R.string.contact_centre))
            setContactViewContactNumber(getString(R.string.business_banking_contact_centre_number))
            GenericResultScreenFragment.setPrimaryButtonOnClick {
                navigateToHomeScreenWithoutReloadingAccounts()
            }
            navigate(CashSendPlusRegistrationOverviewFragmentDirections.actionCashSendPlusRegistrationOverviewFragmentToGenericResultFragment(build(false)))
        }
    }
}