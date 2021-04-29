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
package com.barclays.absa.banking.presentation.termsAndConditions

import com.barclays.absa.banking.login.ui.TermsAndConditionsActivity

open class BaseTermsAndConditionsActivity : TermsAndConditionsActivity() {

    private var url: String? = ""

    companion object {
        const val link = "url"
    }

    override fun loadFromUrl() {
        url = intent.getStringExtra(link)
        termsWebView?.loadUrl("https://docs.google.com/viewer?embedded=true&url=$url")
    }
}

