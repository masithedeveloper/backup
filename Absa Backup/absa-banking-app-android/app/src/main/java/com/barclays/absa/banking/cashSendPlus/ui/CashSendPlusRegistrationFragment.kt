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

import android.os.Bundle
import android.view.View
import com.barclays.absa.banking.R
import com.barclays.absa.utils.ValidationUtils
import kotlinx.android.synthetic.main.cash_send_plus_registration_fragment.*
import styleguide.forms.validation.FieldRequiredValidationRule
import styleguide.forms.validation.addValidationRule

class CashSendPlusRegistrationFragment : CashSendPlusBaseFragment(R.layout.cash_send_plus_registration_fragment) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with(cashSendPlusActivity) {
            setToolbarTitle(getString(R.string.cash_send_plus_registration))
            showToolbarBackArrow()
            showToolbar()
        }

        addValidationRules()
        nextButton.setOnClickListener {
            if (validateEmailAddress() && cashSendPlusLimitAmountNormalInputView.validate()) {
                updateDataFromViews()
                navigate(CashSendPlusRegistrationFragmentDirections.actionCashSendPlusRegistrationFragmentToCashSendPlusRegistrationOverviewFragment())
            }
        }
    }

    private fun validateEmailAddress(): Boolean {
        if (!ValidationUtils.isValidEmailAddress(emailAddressNormalInputView.selectedValue)) {
            emailAddressNormalInputView.setError(getString(R.string.invalid_email_address))
            return false
        }
        return true
    }

    private fun updateDataFromViews() {
        cashSendPlusViewModel.cashSendPlusRegistration.apply {
            amountLimit = cashSendPlusLimitAmountNormalInputView.selectedValue
            emailAddress = emailAddressNormalInputView.selectedValue
        }
    }

    private fun addValidationRules() {
        cashSendPlusLimitAmountNormalInputView.addValidationRule(FieldRequiredValidationRule(R.string.manage_card_limit_empty_value_error))
        emailAddressNormalInputView.addValidationRule(FieldRequiredValidationRule(R.string.invalid_email_address))
    }
}