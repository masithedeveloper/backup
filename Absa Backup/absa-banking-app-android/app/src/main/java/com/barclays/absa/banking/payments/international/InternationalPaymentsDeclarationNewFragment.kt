/*
 * Copyright (c) 2018 Absa Bank Limited, All Rights Reserved.
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

package com.barclays.absa.banking.payments.international

import android.os.Bundle
import android.text.style.ClickableSpan
import android.view.View
import com.barclays.absa.banking.R
import com.barclays.absa.banking.framework.app.BMBApplication
import com.barclays.absa.banking.payments.international.InternationalPaymentsConstants.Companion.INTERNATIONAL_PAYMENTS
import com.barclays.absa.utils.AnalyticsUtil
import com.barclays.absa.utils.CommonUtils
import com.barclays.absa.utils.PdfUtil
import kotlinx.android.synthetic.main.international_payments_declaration_new_fragment.*

class InternationalPaymentsDeclarationNewFragment : InternationalPaymentsAbstractBaseFragment(R.layout.international_payments_declaration_new_fragment) {

    private lateinit var textToMakeClickable: Array<String>

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        AnalyticsUtil.trackAction(INTERNATIONAL_PAYMENTS, "InternationalPayments_DeclarationScreen_ScreenDisplayed")
        setToolBar(R.string.declaration)
        internationalPaymentsActivity.hideProgressIndicator()

        textToMakeClickable = if (BMBApplication.getApplicationLocale().language == "en")
            arrayOf("Terms of Use", "legal terms") else
            arrayOf("gebruiksvoorwaardes", "regulatoriese en voldoeningsvrywaring")

        val clickableSpans = arrayOf(object : ClickableSpan() {
            override fun onClick(widget: View) {
                AnalyticsUtil.trackAction("International Payments Terms and Conditions Clicked")
                PdfUtil.showPDFInApp(internationalPaymentsActivity, getTermsAndConditionsPdfUrl(true))
            }
        }, object : ClickableSpan() {
            override fun onClick(widget: View) {
                AnalyticsUtil.trackAction("International Payments Legal Terms Clicked")
                PdfUtil.showPDFInApp(internationalPaymentsActivity, getTermsAndConditionsPdfUrl(false))
            }
        })

        CommonUtils.makeMultipleTextClickable(internationalPaymentsActivity, R.string.declaration_text,
                textToMakeClickable, clickableSpans, declarationTextView, R.color.graphite)

        doneButton.setOnClickListener {
            AnalyticsUtil.trackAction(INTERNATIONAL_PAYMENTS, "InternationalPayments_DeclarationScreen_DoneButtonClicked")
            internationalPaymentsActivity.onBackPressed()
        }
    }

    fun getTermsAndConditionsPdfUrl(showTermsAndConditions: Boolean): String = if (showTermsAndConditions)
        "https://ib.absa.co.za/absa-online/assets/Assets/Richmedia/Absaonline/PDF/Western_Union_Terms_of_use.pdf?forceDownload=false" else
        "https://ib.absa.co.za/absa-online/assets/Assets/Richmedia/PDF/Online/Disclaimer_IFT_${BMBApplication.getApplicationLocale()}.pdf?forceDownload=false"
}