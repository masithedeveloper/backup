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
import com.barclays.absa.banking.R
import com.entersekt.scan2pay.PullPayment
import kotlinx.android.synthetic.main.fragment_unique_code_payment.*
import styleguide.forms.validation.FieldRequiredValidationRule
import styleguide.forms.validation.validate

class ScanToPayUniqueCodeFragment : ScanToPayAuthBaseFragment(R.layout.fragment_unique_code_payment) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        scanToPayActivity.supportActionBar?.setDisplayShowCustomEnabled(false)
        setToolBar(R.string.scan_to_pay_enter_unique_code)
        showToolBar()
        nextButton.setOnClickListener { onNextClicked() }
    }

    override fun onPaymentAuthorizationRequestReceived(payment: PullPayment) {
        super.onPaymentAuthorizationRequestReceived(payment)
        navigate(ScanToPayUniqueCodeFragmentDirections.actionScanToPayUniqueCodeFragmentToScanToPayPaymentFragment())
    }

    private fun onNextClicked() {
        if (!uniqueCodeNormalInputView.validate(FieldRequiredValidationRule(R.string.scan_to_pay_code_required))) return
        scanToPayViewModel.qrCode = uniqueCodeNormalInputView.selectedValue
        requestToken()
    }
}