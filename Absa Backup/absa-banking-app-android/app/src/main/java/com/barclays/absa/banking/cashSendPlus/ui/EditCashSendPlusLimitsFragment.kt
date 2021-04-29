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
import androidx.fragment.app.activityViewModels
import com.barclays.absa.banking.R
import com.barclays.absa.banking.cashSendPlus.services.CheckCashSendPlusRegistrationStatusResponse
import com.barclays.absa.banking.framework.BaseFragment
import com.barclays.absa.banking.framework.app.BMBConstants
import com.barclays.absa.banking.framework.app.BMBConstants.CASHSEND_CONST
import com.barclays.absa.banking.newToBank.services.dto.ResultAnimations
import com.barclays.absa.banking.presentation.sureCheck.SureCheckDelegate
import com.barclays.absa.utils.AnalyticsUtil
import kotlinx.android.synthetic.main.edit_cash_send_plus_limits_fragment.*
import styleguide.forms.validation.FieldRequiredValidationRule
import styleguide.forms.validation.addValidationRule
import styleguide.screens.GenericResultScreenFragment
import styleguide.screens.GenericResultScreenProperties

class EditCashSendPlusLimitsFragment : BaseFragment(R.layout.edit_cash_send_plus_limits_fragment) {
    private lateinit var sureCheckDelegate: SureCheckDelegate
    private val cashSendPlusViewModel by activityViewModels<CashSendPlusViewModel>()
    private val cachedCashSendPlusData: CheckCashSendPlusRegistrationStatusResponse? = appCacheService.getCashSendPlusRegistrationStatus()
    private var newCashSendPlusLimitAmount = "0"

    override fun onAttach(context: Context) {
        super.onAttach(context)
        sureCheckDelegate = object : SureCheckDelegate(baseActivity) {
            override fun onSureCheckProcessed() {
                cachedCashSendPlusData?.cashSendPlusResponseData?.let {
                    cashSendPlusViewModel.sendUpdateCashSendPlusLimit(newCashSendPlusLimitAmount, it.cashSendPlusLimitAmount)
                } ?: run {
                    onSureCheckCancelled()
                    showGenericErrorMessage()
                }
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setToolBar(R.string.cash_send_plus_edit_limit)
        addValidationRules()
        setupObserver()

        cachedCashSendPlusData?.cashSendPlusResponseData?.let {
            editCashSendPlusLimitNormalInputView.text = it.cashSendPlusLimitAmount
        }

        saveButton.setOnClickListener {
            if (editCashSendPlusLimitNormalInputView.validate()) {
                newCashSendPlusLimitAmount = editCashSendPlusLimitNormalInputView.selectedValueUnmasked
                cachedCashSendPlusData?.cashSendPlusResponseData?.let {
                    cashSendPlusViewModel.sendUpdateCashSendPlusLimit(newCashSendPlusLimitAmount, it.cashSendPlusLimitAmount)
                } ?: run {
                    showGenericErrorMessage()
                }
            }
        }
    }

    private fun setupObserver() {
        cashSendPlusViewModel.updateCashSendPlusLimitResponse.observe(viewLifecycleOwner, { successResponse ->
            dismissProgressDialog()
            if (BMBConstants.FAILURE.equals(successResponse.transactionStatus, true)) {
                navigateToFailedResultScreen()
            } else {
                val responseData = successResponse.cashSendPlusLimit
                sureCheckDelegate.processSureCheck(baseActivity, successResponse) {
                    when {
                        CashSendPlusUtils.isSuccess(responseData) -> navigateToSuccessResultScreen()
                        CashSendPlusUtils.isPendingAuthorization(responseData) -> navigateToPendingResultScreen()
                        CashSendPlusUtils.inProgress(responseData) -> navigateInProgressResultScreen()
                        CashSendPlusUtils.isFailure(responseData) -> navigateToFailedResultScreen()
                        else -> navigateToErrorResultScreen()
                    }
                }
            }
        })
    }

    private fun addValidationRules() {
        editCashSendPlusLimitNormalInputView.addValidationRule(FieldRequiredValidationRule(R.string.manage_card_limit_empty_error))
    }

    private fun navigateToSuccessResultScreen() {
        AnalyticsUtil.trackAction(CASHSEND_CONST, "BBCashSendPlus_EditCashSendPlusLimitSuccess_ScreenDisplayed")
        hideToolBar()
        GenericResultScreenProperties.PropertiesBuilder().apply {
            setResultScreenAnimation(ResultAnimations.generalSuccess)
            setTitle(getString(R.string.cash_send_plus_limit_change_successful))
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
            navigate(EditCashSendPlusLimitsFragmentDirections.actionCashSendPlusLimitsFragmentToGenericResultFragment(build(true)))
        }
    }

    private fun navigateToPendingResultScreen() {
        AnalyticsUtil.trackAction(CASHSEND_CONST, "BBCashSendPlus_EditCashSendPlusLimitAuthorisationPending_ScreenDisplayed")
        hideToolBar()
        GenericResultScreenProperties.PropertiesBuilder().apply {
            setResultScreenAnimation(ResultAnimations.generalSuccess)
            setTitle(getString(R.string.cash_send_plus_limit_change_pending_authorization))
            setDescription(getString(R.string.cash_send_plus_transaction_expire_midnight))
            setPrimaryButtonLabel(getString(R.string.done))
            GenericResultScreenFragment.setPrimaryButtonOnClick {
                navigateToHomeScreenWithoutReloadingAccounts()
            }
            navigate(EditCashSendPlusLimitsFragmentDirections.actionCashSendPlusLimitsFragmentToGenericResultFragment(build(true)))
        }
    }

    private fun navigateInProgressResultScreen() {
        hideToolBar()
        GenericResultScreenProperties.PropertiesBuilder().apply {
            setResultScreenAnimation(ResultAnimations.generalSuccess)
            setTitle(getString(R.string.cash_send_plus_limit_change_already_in_progress))
            setDescription(getString(R.string.cash_send_plus_registration_is_pending_authorization))
            setPrimaryButtonLabel(getString(R.string.done))
            GenericResultScreenFragment.setPrimaryButtonOnClick {
                navigateToHomeScreenWithoutReloadingAccounts()
            }
            navigate(EditCashSendPlusLimitsFragmentDirections.actionCashSendPlusLimitsFragmentToGenericResultFragment(build(true)))
        }
    }

    private fun navigateToFailedResultScreen() {
        AnalyticsUtil.trackAction(CASHSEND_CONST, "BBCashSendPlus_EditCashSendPlusLimitFailure_ScreenDisplayed")
        hideToolBar()
        GenericResultScreenProperties.PropertiesBuilder().apply {
            setResultScreenAnimation(ResultAnimations.generalFailure)
            setTitle(getString(R.string.cash_send_plus_limit_change_unsuccessful))
            setDescription(getString(R.string.cash_send_plus_call_our_contact_centre))
            setContactViewContactName(getString(R.string.contact_centre))
            setContactViewContactNumber(getString(R.string.business_banking_contact_centre_number))
            setPrimaryButtonLabel(getString(R.string.done))
            GenericResultScreenFragment.setPrimaryButtonOnClick {
                navigateToHomeScreenWithoutReloadingAccounts()
            }
            navigate(EditCashSendPlusLimitsFragmentDirections.actionCashSendPlusLimitsFragmentToGenericResultFragment(build(false)))
        }
    }

    private fun navigateToErrorResultScreen() {
        AnalyticsUtil.trackAction(CASHSEND_CONST, "BBCashSendPlus_EditCashSendPlusLimitError_ScreenDisplayed")
        hideToolBar()
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
            navigate(EditCashSendPlusLimitsFragmentDirections.actionCashSendPlusLimitsFragmentToGenericResultFragment(build(false)))
        }
    }
}