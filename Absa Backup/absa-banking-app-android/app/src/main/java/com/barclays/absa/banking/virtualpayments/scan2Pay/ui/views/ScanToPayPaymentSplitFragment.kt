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

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.EditorInfo
import androidx.navigation.fragment.findNavController
import com.barclays.absa.banking.R
import com.barclays.absa.banking.virtualpayments.scan2Pay.ui.ScanToPayPaymentSplitViewModel
import com.barclays.absa.banking.virtualpayments.scan2Pay.ui.ScanToPayViewModel
import com.barclays.absa.utils.AnalyticsUtil
import com.barclays.absa.utils.KeyboardUtils
import com.barclays.absa.utils.extensions.toRandAmount
import com.barclays.absa.utils.viewModel
import kotlinx.android.synthetic.main.fragment_scan_to_pay_payment_split.*
import styleguide.forms.IncreaseDecreaseView

class ScanToPayPaymentSplitFragment : ScanToPayBaseFragment(R.layout.fragment_scan_to_pay_payment_split) {

    private lateinit var scanToPaySplitViewModel: ScanToPayPaymentSplitViewModel

    private val amountValueTextWatcher: TextWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        override fun afterTextChanged(s: Editable?) {
            scanToPaySplitViewModel.payForAmount = amountNormalInputView.selectedValueUnmasked.ifEmpty { "0.00" }.toDouble()
            amountNormalInputView.setDescription(getString(R.string.scan_to_pay_pay_for_amount_of, scanToPaySplitViewModel.payForAmount.toRandAmount(), scanToPaySplitViewModel.amount.toRandAmount()))
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        scanToPaySplitViewModel = viewModel<ScanToPayPaymentSplitViewModel>().apply {
            amount = scanToPayViewModel.amount
            payFor = scanToPayViewModel.scanToPayPartialPaymentDetails.payFor
            splitBy = scanToPayViewModel.scanToPayPartialPaymentDetails.splitBy
            payForAmount = scanToPayViewModel.payForAmount
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        merchantNameTextView.text = scanToPayViewModel.paymentAuth.merchantName
        amountNormalInputView.setImeOptions(EditorInfo.IME_ACTION_DONE)
        if (scanToPayViewModel.splitByPeople) {
            setupForPeopleSplit()
        } else {
            setupForAmountSplit()
        }

        splitTypeRadioGroup.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                peopleSplitRadioButton.id -> resetForPeopleSplit()
                amountSplitRadioButton.id -> resetForAmountSplit()
            }
        }

        applySplitButton.setOnClickListener { handleSplit() }
    }

    private fun handleSplit() {
        if (isErrorAmountHandled()) return

        scanToPayViewModel.partialPaymentPayForAmount.value = scanToPaySplitViewModel.payForAmount
        scanToPayViewModel.payForAmount = scanToPaySplitViewModel.payForAmount
        scanToPayViewModel.splitByPeople = peopleSplitRadioButton.isChecked
        scanToPayViewModel.scanToPayPartialPaymentDetails.apply {
            payFor = scanToPaySplitViewModel.payFor
            splitBy = scanToPaySplitViewModel.splitBy
            AnalyticsUtil.trackAction(ScanToPayViewModel.FEATURE_NAME, "ScanToPay_SplitBillScreen_ApplySplitButtonClicked")
            findNavController().navigateUp()
        }
    }

    private fun setupForPeopleSplit() {
        peopleSplitRadioButton.isChecked = true
        amountNormalInputView.setValueEditable(false)
        amountNormalInputView.removeValueViewTextWatcher(amountValueTextWatcher)
        setupSplitBy()
        setupPayFor()
        updateAmountDescription()
        splitByIncreaseDecreaseView.visibility = View.VISIBLE
        payForIncreaseDecreaseView.visibility = View.VISIBLE
    }

    private fun resetForPeopleSplit() {
        KeyboardUtils.hideKeyboard(this)
        amountNormalInputView.setValueEditable(false)
        amountNormalInputView.removeValueViewTextWatcher(amountValueTextWatcher)
        scanToPaySplitViewModel.payFor = 1
        scanToPaySplitViewModel.splitBy = 1
        setupSplitBy()
        setupPayFor()
        updateAmountDescription()
        splitByIncreaseDecreaseView.visibility = View.VISIBLE
        payForIncreaseDecreaseView.visibility = View.VISIBLE
    }

    private fun setupForAmountSplit() {
        amountSplitRadioButton.isChecked = true
        amountNormalInputView.setValueEditable(true)
        updateAmountDescription()
        amountNormalInputView.addValueViewTextWatcher(amountValueTextWatcher)
        splitByIncreaseDecreaseView.visibility = View.GONE
        payForIncreaseDecreaseView.visibility = View.GONE
    }

    private fun resetForAmountSplit() {
        amountNormalInputView.setValueEditable(true)
        amountNormalInputView.addValueViewTextWatcher(amountValueTextWatcher)
        amountNormalInputView.clear()
        amountNormalInputView.setDescription(getString(R.string.scan_to_pay_pay_for_amount_of, "R 0.00", scanToPayViewModel.amount.toRandAmount()))
        splitByIncreaseDecreaseView.visibility = View.GONE
        payForIncreaseDecreaseView.visibility = View.GONE
    }

    private fun setupSplitBy() = with(splitByIncreaseDecreaseView) {
        setupCurrentValue(scanToPaySplitViewModel.splitBy)
        onValueChangeListener = object : IncreaseDecreaseView.OnValueChangeListener {
            override fun onValueChange(value: Int) {
                scanToPaySplitViewModel.splitBy = value
                if (value < payForIncreaseDecreaseView.currentValue) {
                    payForIncreaseDecreaseView.decreaseValue()
                }
                payForIncreaseDecreaseView.setMaximumValue(value)
                updateAmountDescription()
            }
        }
    }

    private fun setupPayFor() = with(payForIncreaseDecreaseView) {
        setMaximumValue(scanToPaySplitViewModel.splitBy)
        setupCurrentValue(scanToPaySplitViewModel.payFor)
        onValueChangeListener = object : IncreaseDecreaseView.OnValueChangeListener {
            override fun onValueChange(value: Int) {
                scanToPaySplitViewModel.payFor = value
                updateAmountDescription()
            }
        }
    }

    private fun updateAmountDescription() = with(amountNormalInputView) {
        selectedValue = scanToPaySplitViewModel.payForAmount.toRandAmount()
        setDescription(getString(R.string.scan_to_pay_pay_for_amount_of, scanToPaySplitViewModel.payForAmount.toRandAmount(), scanToPaySplitViewModel.amount.toRandAmount()))
    }

    override fun onDestroyView() {
        amountNormalInputView.removeValueViewTextWatcher(amountValueTextWatcher)
        splitByIncreaseDecreaseView.onValueChangeListener = null
        payForIncreaseDecreaseView.onValueChangeListener = null
        super.onDestroyView()
    }

    private fun isErrorAmountHandled(): Boolean {
        when {
            scanToPaySplitViewModel.payForAmount == 0.00 -> amountNormalInputView.setError(getString(R.string.scan_to_pay_amount_greater_than_zero))
            scanToPaySplitViewModel.payForAmount > scanToPaySplitViewModel.amount -> amountNormalInputView.setError(getString(R.string.scan_to_pay_amount_less_or_equal_full_amount))
            else -> amountNormalInputView.clearError()
        }
        return amountNormalInputView.hasError()
    }
}