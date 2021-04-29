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

package com.barclays.absa.banking.settings.ui

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.InputFilter
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.EditorInfo
import com.barclays.absa.banking.R
import com.barclays.absa.banking.boundary.model.limits.DigitalLimit
import com.barclays.absa.banking.framework.BaseFragment
import com.barclays.absa.banking.shared.AlertDialogProperties
import com.barclays.absa.banking.shared.BaseAlertDialog.showAlertDialog
import com.barclays.absa.utils.viewModel
import kotlinx.android.synthetic.main.edit_manage_digital_limits_fragment.*
import styleguide.forms.NormalInputView
import styleguide.forms.maskedEditText.MaskedEditText
import styleguide.utils.extensions.removeCurrency

class EditManageDigitalLimitsFragment : BaseFragment(R.layout.edit_manage_digital_limits_fragment) {

    companion object {
        fun newInstance() = EditManageDigitalLimitsFragment()
    }

    private lateinit var activity: ManageDigitalLimitsActivity
    private lateinit var digitalLimitViewModel: DigitalLimitViewModel

    override fun onAttach(context: Context) {
        super.onAttach(context)
        activity = context as ManageDigitalLimitsActivity
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        digitalLimitViewModel = activity.viewModel()
        digitalLimitViewModel.digitalLimit?.let { setupViews(it) }
    }

    private fun setupViews(digitalLimit: DigitalLimit) {
        val dailyPaymentLimit = digitalLimit.dailyPaymentLimit
        val dailyInterAccountTransferLimit = digitalLimit.dailyInterAccountTransferLimit
        val recurringPaymentTransactionLimit = digitalLimit.recurringPaymentTransactionLimit
        val futureDatedPaymentTransactionLimit = digitalLimit.futureDatedPaymentTransactionLimit

        editPaymentDayLimitView.apply {
            selectedValue = dailyPaymentLimit.actualLimit.toString()
            addValueViewTextWatcher(EditTextWatcher(editPaymentDayLimitView))
            setImeOptions(EditorInfo.IME_ACTION_DONE)
        }

        editInteraccountDailyLimitView.apply {
            selectedValue = dailyInterAccountTransferLimit.actualLimit.toString()
            addValueViewTextWatcher(EditTextWatcher(editInteraccountDailyLimitView))
            setImeOptions(EditorInfo.IME_ACTION_DONE)
        }

        editRecurringTransactionLimitView.apply {
            selectedValue = recurringPaymentTransactionLimit.actualLimit.toString()
            addValueViewTextWatcher(EditTextWatcher(editRecurringTransactionLimitView))
            setImeOptions(EditorInfo.IME_ACTION_DONE)
        }

        editFutureDatedTransactionLimitView.apply {
            selectedValue = futureDatedPaymentTransactionLimit.actualLimit.toString()
            addValueViewTextWatcher(EditTextWatcher(editFutureDatedTransactionLimitView))
            setImeOptions(EditorInfo.IME_ACTION_DONE)
        }

        saveButton.setOnClickListener { view -> handleSaveButtonClicked(view) }

        val onFocusChangeListener = View.OnFocusChangeListener { view, hasFocus -> handleFocusChange(view, hasFocus) }
        editPaymentDayLimitView.setValueViewFocusChangedListener(onFocusChangeListener)
        editInteraccountDailyLimitView.setValueViewFocusChangedListener(onFocusChangeListener)
        editRecurringTransactionLimitView.setValueViewFocusChangedListener(onFocusChangeListener)
        editFutureDatedTransactionLimitView.setValueViewFocusChangedListener(onFocusChangeListener)
    }

    private fun setMaxTextLength(maskedEditText: MaskedEditText, length: Int) {
        maskedEditText.filters = arrayOf<InputFilter>(InputFilter.LengthFilter(length))
    }

    private fun showLimitsDialog() {
        showAlertDialog(AlertDialogProperties.Builder()
                .title(getString(R.string.are_you_sure))
                .message(getString(R.string.setting_digital_limits_to_zero))
                .positiveButton(getString(R.string.ok))
                .negativeButton(getString(R.string.cancel))
                .positiveDismissListener { _, _ ->
                    digitalLimitViewModel.changeDigitalLimits(
                            editPaymentDayLimitView.selectedValue,
                            editInteraccountDailyLimitView.selectedValue,
                            editRecurringTransactionLimitView.selectedValue,
                            editFutureDatedTransactionLimitView.selectedValue)
                }
                .build())
    }

    internal inner class EditTextWatcher(private val inputView: NormalInputView<*>) : TextWatcher {

        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}

        override fun afterTextChanged(editable: Editable) {
            inputView.showError(false)
            val enteredText = editable.toString().replace("R", "").trim { it <= ' ' }
            if (enteredText.isEmpty()) {
                inputView.showError(true)
                inputView.setError(getString(R.string.manage_card_limit_empty_error))
                saveButton.isEnabled = false
            } else {
                inputView.showError(false)
                saveButton.isEnabled = true
            }
        }
    }

    private fun handleSaveButtonClicked(view: View) {
        preventDoubleClick(view)
        val dailyPaymentLimitValue = editPaymentDayLimitView.selectedValue
        val dailyInterAccountTransferLimitValue = editInteraccountDailyLimitView.selectedValue
        val recurringPaymentTransactionLimitValue = editRecurringTransactionLimitView.selectedValue
        val futureDatedPaymentTransactionLimitValue = editFutureDatedTransactionLimitView.selectedValue

        if (digitalLimitViewModel.hasEnteredValidAmounts(
                        dailyPaymentLimitValue,
                        dailyInterAccountTransferLimitValue,
                        recurringPaymentTransactionLimitValue,
                        futureDatedPaymentTransactionLimitValue)) {
            digitalLimitViewModel.changeDigitalLimits(
                    dailyPaymentLimitValue,
                    dailyInterAccountTransferLimitValue,
                    recurringPaymentTransactionLimitValue,
                    futureDatedPaymentTransactionLimitValue)
        } else {
            showLimitsDialog()
        }
    }

    private fun handleFocusChange(view: View, hasFocus: Boolean) {
        try {
            val maskedEditText = view as MaskedEditText

            if (hasFocus) {
                maskedEditText.setText(maskedEditText.text.toString().replace(".00", ""))
                setMaxTextLength(maskedEditText, 13)
            } else {
                if (maskedEditText.text.toString().removeCurrency() == "") {
                    maskedEditText.setText("R 0")
                }
                setMaxTextLength(maskedEditText, 16)
                maskedEditText.setText(String.format("%s.00", maskedEditText.text.toString()))
            }
        } catch (e: ClassCastException) {
            e.printStackTrace()
        }
    }
}