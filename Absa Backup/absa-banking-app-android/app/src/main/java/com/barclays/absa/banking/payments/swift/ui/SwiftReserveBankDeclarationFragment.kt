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
package com.barclays.absa.banking.payments.swift.ui

import android.os.Bundle
import android.text.style.ClickableSpan
import android.view.View
import androidx.navigation.fragment.findNavController
import com.barclays.absa.banking.R
import com.barclays.absa.banking.payments.swift.ui.SwiftTransactionsActivity.Companion.SWIFT
import com.barclays.absa.utils.AnalyticsUtil
import com.barclays.absa.utils.CommonUtils
import com.barclays.absa.utils.PdfUtil
import kotlinx.android.synthetic.main.swift_reserve_bank_declaration_fragment.*

class SwiftReserveBankDeclarationFragment : SwiftPaymentsBaseFragment(R.layout.swift_reserve_bank_declaration_fragment) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        AnalyticsUtil.trackAction(SWIFT, "Swift_DeclarationScreenDisplayed")
        setToolBar(R.string.swift_declaration)
        CommonUtils.makeMultipleTextClickable(swiftPaymentsActivity, R.string.swift_reserve_bank_declaration_message,
                arrayOf(swiftPaymentsActivity.getString(R.string.swift_terms_of_use), swiftPaymentsActivity.getString(R.string.swift_legal_terms)),
                arrayOf(object : ClickableSpan() {
                    override fun onClick(widget: View) = PdfUtil.showPDFInApp(swiftPaymentsActivity, "https://ib.absa.co.za/absa-online/assets/Assets/Richmedia/Absaonline/PDF/Terms_of_use_SWIFT_EN_2020.pdf")
                }, object : ClickableSpan() {
                    override fun onClick(widget: View) = PdfUtil.showPDFInApp(swiftPaymentsActivity, "https://ib.absa.co.za/absa-online/assets/Assets/Richmedia/PDF/Online/Disclaimer_IFT_en.pdf")
                }),
                declarationTextView,
                R.color.graphite)
        doneButton.setOnClickListener { findNavController().navigateUp() }
    }
}