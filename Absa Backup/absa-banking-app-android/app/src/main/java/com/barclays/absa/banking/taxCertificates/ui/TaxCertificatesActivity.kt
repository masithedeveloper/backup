/*
 *  Copyright (c) 2020 Absa Bank Limited, All Rights Reserved.
 *
 *  This code is confidential to Absa Bank Limited and shall not be disclosed
 *  outside the Bank without the prior written permission of the Absa Legal
 *
 *  In the event that such disclosure is permitted the code shall not be copied
 *  or distributed other than on a need-to-know basis and any recipients may be
 *  required to sign a confidentiality undertaking in favor of Absa Bank
 *  Limited
 *
 */

package com.barclays.absa.banking.taxCertificates.ui

import android.os.Bundle
import com.barclays.absa.banking.R
import com.barclays.absa.banking.framework.BaseActivity
import com.barclays.absa.utils.AnalyticsUtil

class TaxCertificatesActivity : BaseActivity(R.layout.tax_certificates_activity) {

    companion object {
        const val TAX_CERTIFICATE_ANALYTIC_TAG = "TaxCertificate"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setToolBar(R.string.tax_certificates_menu_item, null)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        if (isBusinessAccount) {
            AnalyticsUtil.trackAction(TAX_CERTIFICATE_ANALYTIC_TAG, "BBTaxCertificate_SelectionScreen_BackButtonTapped")
        } else {
            AnalyticsUtil.trackAction(TAX_CERTIFICATE_ANALYTIC_TAG, "TaxCertificate_SelectionScreen_BackButtonTapped")
        }
    }
}