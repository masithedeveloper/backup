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
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.DefaultItemAnimator
import com.barclays.absa.banking.R
import com.barclays.absa.banking.cashSendPlus.services.CashSendPlusSendMultiplePaymentDetails
import com.barclays.absa.banking.cashSendPlus.services.CashSendPlusSendMultipleRequestDataModel
import com.barclays.absa.banking.cashSendPlus.services.CashSendPlusSendMultipleResponse
import com.barclays.absa.banking.framework.BaseFragment
import com.barclays.absa.banking.framework.app.BMBConstants
import com.barclays.absa.banking.newToBank.services.dto.ResultAnimations
import com.barclays.absa.banking.presentation.sureCheck.SureCheckDelegate
import com.barclays.absa.utils.AnalyticsUtil
import com.barclays.absa.utils.TextFormatUtils
import kotlinx.android.synthetic.main.cash_send_plus_confirm_multiple_payment_fragment.*
import styleguide.screens.GenericResultScreenFragment
import styleguide.screens.GenericResultScreenProperties
import kotlin.math.roundToInt

class CashSendPlusConfirmMultiplePaymentFragment : BaseFragment(R.layout.cash_send_plus_confirm_multiple_payment_fragment) {
    private val cashSendPlusViewModel by activityViewModels<CashSendPlusViewModel>()
    private lateinit var cashSendPlusSendMultipleActivity: CashSendPlusSendMultipleActivity
    private lateinit var cashSendPlusConfirmMultiplePaymentAdapter: CashSendPlusConfirmMultiplePaymentAdapter
    private val selectedBeneficiaryPaymentDetails: MutableList<CashSendPlusSendMultiplePaymentDetails> = appCacheService.getCashSendPlusSendMultipleBeneficiariesPaymentDetails().toMutableList()
    private val cashSendPlusSendMultipleRequestData = CashSendPlusSendMultipleRequestDataModel()
    private lateinit var sureCheckDelegate: SureCheckDelegate

    override fun onAttach(context: Context) {
        super.onAttach(context)
        setHasOptionsMenu(true)
        cashSendPlusSendMultipleActivity = context as CashSendPlusSendMultipleActivity
        sureCheckDelegate = object : SureCheckDelegate(cashSendPlusSendMultipleActivity) {
            override fun onSureCheckProcessed() {
                cashSendPlusViewModel.sendCashSendPlusSendMultiple(cashSendPlusSendMultipleRequestData)
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setToolBar(R.string.cash_send_plus_confirm_multiple_payment)
        selectedBeneficiaryPaymentDetails.let {
            cashSendPlusConfirmMultiplePaymentAdapter = CashSendPlusConfirmMultiplePaymentAdapter(it)
        }
        confirmMultiplePaymentRecyclerView.apply {
            setHasFixedSize(true)
            itemAnimator = DefaultItemAnimator()
            adapter = cashSendPlusConfirmMultiplePaymentAdapter
        }

        calculateTotalAmount()

        selectedBeneficiaryPaymentDetails.forEach { paymentDetails ->
            val requestData = CashSendPlusSendMultipleRequestDataModel.CashSendPlusSendMultipleData().apply {
                paymentDetails.beneficiaryInfo.also {
                    this.beneficiaryName = it.beneficiaryName ?: ""
                    this.beneficiarySurname = it.beneficiarySurname ?: ""
                    this.beneficiaryShortName = it.beneficiaryName ?: ""
                    this.beneficiaryNumber = it.beneficiaryID ?: ""
                    this.accessCode = paymentDetails.encryptedAccessPin
                    this.amount = paymentDetails.amount
                    this.cashSendPlus = true.toString()
                    this.cellNo = it.beneficiaryAccountNumber ?: ""
                    this.fromAccountNo = paymentDetails.accountDetail.accountNumber
                    this.sessionKeyID = paymentDetails.virtualSessId
                    this.uniqueEFT = it.uniqueEFT ?: ""
                    if (paymentDetails.reference.isNotEmpty()) {
                        this.statementDescription = paymentDetails.reference
                    }
                    this.virtualServerID = paymentDetails.mapId
                }
            }
            cashSendPlusSendMultipleRequestData.cashSendData.add(requestData)
        }

        sendCashButton.setOnClickListener {
            cashSendPlusViewModel.sendCashSendPlusValidateSendMultiple(cashSendPlusSendMultipleRequestData)
        }

        cashSendPlusViewModel.validateCashSendPlusSendMultipleResponse.observe(viewLifecycleOwner, {
            if (CashSendPlusUtils.isSendMultipleSuccess(it)) {
                cashSendPlusViewModel.sendCashSendPlusSendMultiple(cashSendPlusSendMultipleRequestData)
            } else {
                dismissProgressDialog()
                it.transactionMessage?.let { message ->
                    navigateToFailureResultScreen(message)
                } ?: run {
                    navigateToFailureResultScreen(getString(R.string.cash_send_plus_having_trouble_connecting))
                }
            }
        })

        cashSendPlusViewModel.cashSendPlusSendMultipleResponse.observe(viewLifecycleOwner, { successResponse ->
            dismissProgressDialog()
            if (CashSendPlusUtils.isSendMultipleFailure(successResponse)) {
                navigateToUnsuccessfulResultScreen()
            } else {
                sureCheckDelegate.processSureCheck(cashSendPlusSendMultipleActivity, successResponse) {
                    when {
                        CashSendPlusUtils.isSendMultipleSuccess(successResponse) -> navigateToSuccessfulResultScreen(successResponse.cashSendDetails)
                        CashSendPlusUtils.isSendMultiplePendingAuthorization(successResponse) -> navigateToPendingAuthorizationResultScreen()
                        else -> navigateToErrorResultScreen()
                    }
                }
            }
        })
    }

    override fun onCreateOptionsMenu(menu: Menu, menuInflater: MenuInflater) {
        menuInflater.inflate(R.menu.cancel_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.cancel_menu_item) {
            cashSendPlusSendMultipleActivity.showCancelCashSendPlusSendMultipleDialog()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun calculateTotalAmount() {
        val totalAmount = selectedBeneficiaryPaymentDetails.sumBy { if (it.amount.isNotEmpty()) it.amount.toDouble().roundToInt() else 0 }
        totalAmountPrimaryContentAndLabelView.setContentText(TextFormatUtils.formatBasicAmountAsRand(totalAmount))
    }

    private fun navigateToSuccessfulResultScreen(cashSendPlusSendMultipleDetails: MutableList<CashSendPlusSendMultipleResponse.CashSendPlusSendMultipleDetails>) {
        hideToolBar()
        AnalyticsUtil.trackAction(BMBConstants.CASHSEND_CONST, "BBCashSendPlus_SendMultipleSuccess_ScreenDisplayed")
        appCacheService.setCashSendPlusSendMultipleResponseDetails(cashSendPlusSendMultipleDetails)
        navigate(CashSendPlusConfirmMultiplePaymentFragmentDirections.actionCashSendPlusConfirmMultiplePaymentFragmentToCashSendPlusSendMultipleShareAccessPinFragment())
    }

    private fun navigateToPendingAuthorizationResultScreen() {
        hideToolBar()
        GenericResultScreenProperties.PropertiesBuilder().apply {
            setResultScreenAnimation(ResultAnimations.generalSuccess)
            setTitle(getString(R.string.cash_send_plus_send_to_multiple_pending_athorization))
            setDescription(getString(R.string.cash_send_plus_call_contact_centre))
            setContactViewContactName(getString(R.string.contact_centre))
            setContactViewContactNumber(getString(R.string.business_banking_contact_centre_number))
            setPrimaryButtonLabel(getString(R.string.done))
            GenericResultScreenFragment.setPrimaryButtonOnClick {
                navigateToHomeScreenWithoutReloadingAccounts()
            }
            AnalyticsUtil.trackAction(BMBConstants.CASHSEND_CONST, "BBCashSendPlus_SendMultipleAuthorisationPending_ScreenDisplayed")
            navigate(CashSendPlusConfirmMultiplePaymentFragmentDirections.actionCashSendPlusConfirmMultiplePaymentFragmentToGenericResultFragment(build(true)))
        }
    }

    private fun navigateToUnsuccessfulResultScreen() {
        hideToolBar()
        GenericResultScreenProperties.PropertiesBuilder().apply {
            setResultScreenAnimation(ResultAnimations.generalFailure)
            setTitle(getString(R.string.cash_send_plus_send_to_multiple_unsuccessful))
            setPrimaryButtonLabel(getString(R.string.done))
            GenericResultScreenFragment.setPrimaryButtonOnClick {
                navigateToHomeScreenWithoutReloadingAccounts()
            }
            AnalyticsUtil.trackAction(BMBConstants.CASHSEND_CONST, "BBCashSendPlus_SendMultipleFailure_ScreenDisplayed")
            navigate(CashSendPlusConfirmMultiplePaymentFragmentDirections.actionCashSendPlusConfirmMultiplePaymentFragmentToGenericResultFragment(build(false)))
        }
    }

    private fun navigateToFailureResultScreen(message: String) {
        hideToolBar()
        GenericResultScreenProperties.PropertiesBuilder().apply {
            setResultScreenAnimation(ResultAnimations.generalFailure)
            setTitle(getString(R.string.cash_send_plus_unable_to_complete))
            setDescription(message)
            setContactViewContactName(getString(R.string.contact_centre))
            setContactViewContactNumber(getString(R.string.business_banking_contact_centre_number))
            setPrimaryButtonLabel(getString(R.string.done))
            GenericResultScreenFragment.setPrimaryButtonOnClick {
                navigateToHomeScreenWithoutReloadingAccounts()
            }
            AnalyticsUtil.trackAction(BMBConstants.CASHSEND_CONST, "BBCashSendPlus_SendMultipleFailure_ScreenDisplayed")
            navigate(CashSendPlusConfirmMultiplePaymentFragmentDirections.actionCashSendPlusConfirmMultiplePaymentFragmentToGenericResultFragment(build(false)))
        }
    }

    private fun navigateToErrorResultScreen() {
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
            AnalyticsUtil.trackAction(BMBConstants.CASHSEND_CONST, "BBCashSendPlus_SendMultipleError_ScreenDisplayed")
            navigate(CashSendPlusConfirmMultiplePaymentFragmentDirections.actionCashSendPlusConfirmMultiplePaymentFragmentToGenericResultFragment(build(false)))
        }
    }
}