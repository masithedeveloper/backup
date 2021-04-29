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
package com.barclays.absa.banking.deviceLinking.ui

import android.os.Bundle
import android.widget.Toast
import com.barclays.absa.banking.R
import com.barclays.absa.banking.framework.BaseActivity
import com.barclays.absa.utils.NetworkUtils
import com.barclays.absa.utils.PdfUtil.showTermsAndConditionsClientAgreement
import kotlinx.android.synthetic.main.terms_conditions_selector_activity.*

class TermsAndConditionsSelectorActivity : BaseActivity(R.layout.terms_conditions_selector_activity) {

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setToolBarBack(R.string.terms_and_conditions)
        personalAgreementLink.setOnClickListener { startTermsAndConditionsActivity("I") }
        businessAgreementLink.setOnClickListener { startTermsAndConditionsActivity("B") }
    }

    private fun startTermsAndConditionsActivity(clientType: String) {
        if (NetworkUtils.isNetworkConnected()) {
            showTermsAndConditionsClientAgreement(this, clientType)
        } else {
            Toast.makeText(this, getString(R.string.network_connection_error), Toast.LENGTH_LONG).show()
        }
    }
}