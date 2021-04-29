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
package com.barclays.absa.banking.virtualpayments.scan2Pay.ui.views

import android.os.Bundle
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.core.widget.doAfterTextChanged
import com.barclays.absa.banking.R
import com.barclays.absa.banking.framework.BuildConfigHelper
import com.barclays.absa.banking.framework.app.BMBApplication
import com.barclays.absa.banking.framework.utils.BMBLogger
import com.barclays.absa.banking.virtualpayments.scan2Pay.services.response.dto.ScanToPayAuthResponse.Card
import com.barclays.absa.banking.virtualpayments.scan2Pay.ui.ScanToPayViewModel
import com.barclays.absa.banking.virtualpayments.scan2Pay.ui.ScanToPayViewModel.SplitBillViewOption.*
import com.barclays.absa.banking.virtualpayments.scan2Pay.ui.adapters.SymmetricHorizontalOffsetDecoration
import com.barclays.absa.banking.virtualpayments.scan2Pay.ui.adapters.TipOptionsRecyclerViewAdapter
import com.barclays.absa.utils.AnalyticsUtil
import com.barclays.absa.utils.extensions.toRandAmount
import com.entersekt.scan2pay.Authorization
import com.entersekt.scan2pay.SourceOfFunds
import com.entersekt.scan2pay.Tip
import kotlinx.android.synthetic.main.fragment_scan_to_pay_payment.*
import styleguide.utils.extensions.toFormattedAmountZeroDefault
import styleguide.utils.extensions.toFormattedCardNumber

class ScanToPayPaymentFragment : ScanToPayBaseFragment(R.layout.fragment_scan_to_pay_payment) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setToolBar(R.string.scan_to_pay_payment_details)
        showToolBar()

        merchantNameTextView.text = scanToPayViewModel.paymentAuth.merchantName
        setupAmount()
        setupTip()
        extraReferenceNormalInputView.visibility = if (scanToPayViewModel.paymentAuth.payerReferenceRequired) View.VISIBLE else View.GONE
        totalAmountNormalInputView.selectedValue = (scanToPayViewModel.payForAmount + scanToPayViewModel.tipAmount).toString().toFormattedAmountZeroDefault()
        val payAmountString = "${getString(R.string.scan_to_pay_pay)} ${totalAmountNormalInputView.selectedValue.toFormattedAmountZeroDefault()}"
        acceptPaymentButton.text = payAmountString

        scanToPayViewModel.partialPaymentPayForAmount.observe(viewLifecycleOwner, {
            scanToPayViewModel.payForAmount = it
            setupAmount()
        })
        scanToPayViewModel.selectedAuthCard.observe(viewLifecycleOwner, { scanToPaySelectedAuthCard -> setupSelectedAuthCard(scanToPaySelectedAuthCard) })

        setupClickListeners()

        amountNormalInputView.editText?.doAfterTextChanged {
            handleAmountChange(amountNormalInputView.selectedValueUnmasked.ifEmpty { "0.00" })
        }
        tipAmountNormalInputView.editText?.doAfterTextChanged {
            handleTipChange(tipAmountNormalInputView.selectedValueUnmasked.ifEmpty { "0.00" })
        }
    }

    private fun setupAmount() {
        amountNormalInputView.setValueEditable(scanToPayViewModel.isAmountEditable)
        amountNormalInputView.selectedValue = if (scanToPayViewModel.payForAmount == 0.00) "" else scanToPayViewModel.payForAmount.toString()
        if (scanToPayViewModel.isPartialPayment()) {
            amountNormalInputView.setDescription(getString(R.string.scan_to_pay_pay_for_amount_of, scanToPayViewModel.payForAmount.toRandAmount(), scanToPayViewModel.amount.toRandAmount()))
        } else {
            scanToPayViewModel.scanToPayPartialPaymentDetails.splitBillViewOption = NO_SPLIT_OPTION
        }

        when (scanToPayViewModel.scanToPayPartialPaymentDetails.splitBillViewOption) {
            NO_SPLIT_OPTION -> splitEditBillButton.visibility = View.GONE
            SPLIT, EDIT_SPLIT -> with(splitEditBillButton) {
                text = if (scanToPayViewModel.payForAmount == scanToPayViewModel.amount) getString(R.string.scan_to_pay_split_bill) else getString(R.string.scan_to_pay_edit_split)
                visibility = View.VISIBLE
                setOnClickListener { navigateScanToPayPaymentSplitFragment() }
            }
        }
    }

    private fun setupTip() {
        when (scanToPayViewModel.paymentAuth.tip) {
            is Tip.None -> hideTipRelatedViews()
            is Tip.Fixed -> {
                scanToPayViewModel.tipOptionList = listOf(TipOption("0%", 0.00), TipOption("10%", 0.10), TipOption("15%", 0.15), TipOption("20%", 0.20), TipOption(getString(R.string.scan_to_pay_own_tip_amount), -1.00))
                scanToPayViewModel.tipOption = scanToPayViewModel.tipOptionList.last()
                handleSelectedTipOption(scanToPayViewModel.tipOptionList.last())
                setupForFixedTip()
            }
            is Tip.InputRequired -> {
                scanToPayViewModel.tipOptionList = listOf(TipOption("0%", 0.00), TipOption("10%", 0.10), TipOption("15%", 0.15), TipOption("20%", 0.20), TipOption(getString(R.string.scan_to_pay_own_tip_amount), -1.00))
                val tipOption = scanToPayViewModel.tipOption ?: if (scanToPayViewModel.tipAmount == 0.00) scanToPayViewModel.tipOptionList.first() else scanToPayViewModel.tipOptionList.last()
                scanToPayViewModel.tipOption = tipOption
                with(tipOptionsRecyclerView) {
                    addItemDecoration(SymmetricHorizontalOffsetDecoration(resources.getDimension(R.dimen.tiny_space).toInt()))
                    adapter = TipOptionsRecyclerViewAdapter(scanToPayViewModel.tipOptionList, tipOption, object : TipOptionsRecyclerViewAdapter.OnTipOptionSelected {
                        override fun onTipSelect(tipOption: TipOption) = handleSelectedTipOption(tipOption)
                    })
                }
                handleSelectedTipOption(tipOption)
                tipAmountNormalInputView.selectedValue = scanToPayViewModel.tipAmount.toRandAmount()
            }
        }
    }

    private fun setupSelectedAuthCard(scanToPaySelectedAuthCard: Card) = with(selectedCardOptionActionButtonView) {
        setCaptionText(scanToPaySelectedAuthCard.cardType)
        setSubCaption(scanToPaySelectedAuthCard.cardNumber.toFormattedCardNumber())
    }

    private fun setupClickListeners() {
        selectedCardOptionActionButtonView.setOnClickListener { navigate(ScanToPayPaymentFragmentDirections.actionScanToPayPaymentFragmentToScanToPayAuthCardSelectionFragment()) }
        acceptPaymentButton.setOnClickListener {
            AnalyticsUtil.trackAction(ScanToPayViewModel.FEATURE_NAME, "ScanToPay_PayDetailsScreen_PayButtonClicked")
            if (hasValidAmount()) {
                handlePayment()
            }
        }

        rejectPaymentButton.setOnClickListener { cancelPayment() }
        scanToPayActivity.onBackPressedDispatcher.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() = cancelPayment()
        })
    }

    private fun cancelPayment() {
        showProgressDialog()
        if (BuildConfigHelper.STUB) {
            navigateToHomeScreenWithoutReloadingAccounts()
        } else {
            scanToPayViewModel.paymentAuth.reject()
        }
    }

    private fun hideTipRelatedViews() {
        tipAmountTextView.visibility = View.GONE
        tipOptionsRecyclerView.visibility = View.GONE
        tipAmountNormalInputView.visibility = View.GONE
        tipAmountNormalInputView.setValueEditable(false)
        totalAmountNormalInputView.visibility = View.GONE
    }

    private fun setupForFixedTip() {
        tipAmountTextView.visibility = View.GONE
        tipOptionsRecyclerView.visibility = View.GONE
        tipAmountNormalInputView.setValueEditable(false)
        tipAmountNormalInputView.selectedValue = scanToPayViewModel.tipAmount.toRandAmount()
    }

    private fun handleSelectedTipOption(tipOption: TipOption) {
        scanToPayViewModel.tipOption = tipOption
        when (tipOption.percentage) {
            -1.00 -> setTipAmountView(scanToPayViewModel.tipAmount, true)
            else -> setTipAmountView(tipOption.percentage * scanToPayViewModel.payForAmount, false)
        }
    }

    private fun setTipAmountView(tipAmount: Double, editable: Boolean) = with(tipAmountNormalInputView) {
        selectedValue = tipAmount.toString().toFormattedAmountZeroDefault()
        setValueEditable(editable)
    }

    private fun handleTipChange(tip: String) {
        scanToPayViewModel.tipAmount = tip.ifEmpty { "0.00" }.toDouble()
        totalAmountNormalInputView.selectedValue = (scanToPayViewModel.payForAmount + scanToPayViewModel.tipAmount).toString().toFormattedAmountZeroDefault()
        val payAmountString = "${getString(R.string.scan_to_pay_pay)} ${totalAmountNormalInputView.selectedValue.toFormattedAmountZeroDefault()}"
        acceptPaymentButton.text = payAmountString
    }

    private fun handleAmountChange(amount: String) {
        scanToPayViewModel.payForAmount = amount.ifEmpty { "0.00" }.toDouble()
        val tipOption = scanToPayViewModel.tipOption
        if (tipOption != null && tipOption != scanToPayViewModel.tipOptionList.last()) {
            handleSelectedTipOption(tipOption)
        } else {
            handleTipChange(tipAmountNormalInputView.selectedValueUnmasked)
        }
    }

    private fun handlePayment() {
        showProgressDialog()
        if (BuildConfigHelper.STUB) {
            val authorization = getAuthentication(object : SourceOfFunds {
                override val index: Int = 0
            })
            BMBLogger.d(ScanToPayViewModel.FEATURE_NAME, authorization.toString())
            dismissProgressDialog()
            return
        }
        BMBLogger.d(ScanToPayViewModel.FEATURE_NAME, "Auth BEFORE: ${BMBApplication.getInstance().auth}")
        val selectedSourceOfFunds = scanToPayViewModel.getSelectedSourceOfFunds() ?: return
        val authorization = getAuthentication(selectedSourceOfFunds)
        BMBLogger.d(ScanToPayViewModel.FEATURE_NAME, authorization.toString())
        scanToPayViewModel.paymentAuth.authorize(authorization)
        BMBLogger.d(ScanToPayViewModel.FEATURE_NAME, "Auth AFTER : ${scanToPayViewModel.paymentAuth}")
    }

    private fun getAuthentication(sourceOfFunds: SourceOfFunds): Authorization = Authorization(sourceOfFunds = sourceOfFunds).apply {
        amount = totalAmountNormalInputView.selectedValueUnmasked.ifEmpty { "0.00" }.toDouble()
        if (scanToPayViewModel.isPayerReferenceRequired()) {
            payerReference = extraReferenceNormalInputView.selectedValue
        }
        if (!scanToPayViewModel.isNoTip()) {
            tip = tipAmountNormalInputView.selectedValueUnmasked.ifEmpty { "0.00" }.toDouble()
        }
    }

    private fun navigateScanToPayPaymentSplitFragment() {
        AnalyticsUtil.trackAction(ScanToPayViewModel.FEATURE_NAME, "ScanToPay_PaymentDetailsScreen_EditSplitButtonClicked")
        navigate(ScanToPayPaymentFragmentDirections.actionScanToPayPaymentFragmentToScanToPayPaymentSplitFragment())
    }

    private fun hasValidAmount(): Boolean {
        val isValidAmount = scanToPayViewModel.isValidPayForAmount()
        if (isValidAmount) {
            amountNormalInputView.clearError()
        } else {
            amountNormalInputView.setError(getString(R.string.scan_to_pay_please_enter_valid_amount))
        }
        return isValidAmount
    }

    class TipOption(val displayValue: String, val percentage: Double)
}