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
package com.barclays.absa.banking.debiCheck.ui

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import com.barclays.absa.banking.R
import com.barclays.absa.banking.debiCheck.ui.DebitOrdersActivity.Companion.debitOrder
import com.barclays.absa.banking.debiCheck.ui.DebitOrdersActivity.Companion.debitOrderDataModel
import com.barclays.absa.banking.framework.app.BMBConstants
import com.barclays.absa.banking.newToBank.services.dto.ResultAnimations
import com.barclays.absa.banking.presentation.sureCheck.SureCheckDelegate
import com.barclays.absa.banking.presentation.transactions.AccountRefreshInterface
import com.barclays.absa.utils.AccountBalanceUpdateHelper
import com.barclays.absa.utils.AnalyticsUtil
import kotlinx.android.synthetic.main.debit_order_reverse_payment_fragment.*
import styleguide.forms.SelectorList
import styleguide.forms.StringItem
import styleguide.screens.GenericResultScreenFragment
import styleguide.screens.GenericResultScreenProperties

class DebitOrderReversePaymentFragment : DebitOrderBaseFragment(R.layout.debit_order_reverse_payment_fragment) {

    private lateinit var sureCheckDelegate: SureCheckDelegate

    enum class Reason(val code: String) {
        DID_NOT_AUTHORIZE_DEBIT_ORDER("30"),
        AMOUNT_DIFFERS("32"),
        TOLD_CREDITOR_TO_CANCEL("34")
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        sureCheckDelegate = object : SureCheckDelegate(debitOrdersActivity) {
            override fun onSureCheckProcessed() {
                Handler(Looper.getMainLooper()).postDelayed({
                    debitOrderViewModel.reverseDebitOrderPayment(debitOrderDataModel)
                }, 250)
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setToolBar(R.string.reverse_payment_title)
        showToolBar()

        val debitOrderReversalReasons = SelectorList<StringItem>().apply {
            add(StringItem(getString(R.string.debit_order_reversal_reason_one)))
            add(StringItem(getString(R.string.debit_order_reversal_reason_two)))
            add(StringItem(getString(R.string.debit_order_reversal_reason_third)))
        }

        reasonForReversalNormalInputView.setList(debitOrderReversalReasons, getString(R.string.select_reason_for_reversal))
        reasonForReversalNormalInputView.setItemSelectionInterface { index ->
            when (index) {
                0 -> {
                    debitOrderDataModel.reasonCode = Reason.DID_NOT_AUTHORIZE_DEBIT_ORDER.code
                    debitOrderDataModel.reasonDescription = reasonForReversalNormalInputView.selectedValue
                }
                1 -> {
                    debitOrderDataModel.reasonCode = Reason.AMOUNT_DIFFERS.code
                    debitOrderDataModel.reasonDescription = reasonForReversalNormalInputView.selectedValue
                }
                2 -> {
                    debitOrderDataModel.reasonCode = Reason.TOLD_CREDITOR_TO_CANCEL.code
                    debitOrderDataModel.reasonDescription = reasonForReversalNormalInputView.selectedValue
                }
            }
            debitOrderTermsCheckBoxView.visibility = View.VISIBLE
        }

        debitOrderDataModel.apply {
            actionDate = debitOrder.actionDate
            userReference = debitOrder.userReference
            amount = debitOrder.amount
            debitType = debitOrder.debitType
            debitOrderStatus = debitOrder.debitOrderStatus
            userCode = debitOrder.userCode
            userReference = debitOrder.userReference
            tiebNumber = debitOrder.tiebNumber
            instructionNumber = debitOrder.instructionNumber
            userSequence = debitOrder.userSequence
        }

        reversePaymentButton.setOnClickListener {
            if (validateFields()) {
                tagReasonForReversal()
                debitOrderViewModel.reverseDebitOrderPayment(debitOrderDataModel)
                handleSureCheck()
            }
        }
    }

    private fun tagReasonForReversal() {
        when (debitOrderDataModel.reasonCode) {
            Reason.DID_NOT_AUTHORIZE_DEBIT_ORDER.code -> AnalyticsUtil.trackAction("Debit Orders", "DebitOrder_ReasonForReversalScreen_IDidNotAuthoriseThisDebitOrderClicked")
            Reason.AMOUNT_DIFFERS.code -> AnalyticsUtil.trackAction("Debit Orders", "DebitOrder_ReasonForReversalScreen_TheAmountDiffersClicked")
            Reason.TOLD_CREDITOR_TO_CANCEL.code -> AnalyticsUtil.trackAction("Debit Orders", "DebitOrder_ReasonForReversalScreen_IToldTheCreditorToCancelClicked")
        }
    }

    private fun validateFields(): Boolean {
        when {
            reasonForReversalNormalInputView.selectedValue.isEmpty() -> reasonForReversalNormalInputView.setError(R.string.select_reason_for_reversal_error)
            !debitOrderTermsCheckBoxView.isChecked -> debitOrderTermsCheckBoxView.setErrorMessage(R.string.accept_declaration_error)
            else -> return true
        }
        return false
    }

    private fun handleSureCheck() {
        debitOrderViewModel.reverseDebitOrderSureCheckResponse.observe(viewLifecycleOwner, {
            dismissProgressDialog()
            it?.let {
                sureCheckDelegate.processSureCheck(debitOrdersActivity, it) {
                    if (BMBConstants.SUCCESS.equals(it.transactionStatus, ignoreCase = true)) {
                        navigateToSuccessfulResultScreen()
                    } else {
                        navigateToUnableToContinueResultScreen()
                    }
                }
            }
        })
    }

    private fun updateAccountsAndNavigateBack() {
        AccountBalanceUpdateHelper(baseActivity).refreshHomeScreenAccountsAndBalances(object : AccountRefreshInterface {
            override fun onSuccess() = navigateBackToDebitOrders()
            override fun onFailure() = navigateBackToDebitOrders()
        })
    }

    private fun navigateBackToDebitOrders() {
        dismissProgressDialog()
        activity?.apply {
            setResult(Activity.RESULT_OK)
            finish()
        }
    }

    private fun navigateToSuccessfulResultScreen() {
        hideToolBar()
        AnalyticsUtil.trackAction("Debit Orders", "DebitOrder_TransactionSuccessfullyReversedScreen_ScreenDisplayed")
        val resultScreenProperties = GenericResultScreenProperties.PropertiesBuilder()
                .setResultScreenAnimation(ResultAnimations.generalSuccess)
                .setTitle(getString(R.string.debit_order_reversed))
                .setDescription(getString(R.string.debit_order_reversed_success_message, debitOrderDataModel.amount))
                .setPrimaryButtonLabel(getString(R.string.done))
                .build(true)

        GenericResultScreenFragment.setPrimaryButtonOnClick {
            updateAccountsAndNavigateBack()
        }
        navigate(DebitOrderReversePaymentFragmentDirections.actionDebitOrderReversePaymentFragmentToDebitOrderResultsFragment(resultScreenProperties, true))
    }

    private fun navigateToUnableToContinueResultScreen() {
        hideToolBar()
        val resultScreenProperties = GenericResultScreenProperties.PropertiesBuilder()
                .setResultScreenAnimation(ResultAnimations.generalError)
                .setTitle(getString(R.string.unable_to_continue))
                .setPrimaryButtonLabel(getString(R.string.done))
                .build(true)

        GenericResultScreenFragment.setPrimaryButtonOnClick { debitOrdersActivity.finish() }
        navigate(DebitOrderReversePaymentFragmentDirections.actionDebitOrderReversePaymentFragmentToDebitOrderResultsFragment(resultScreenProperties, true))
    }
}