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
 */

package com.barclays.absa.banking.covid

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import com.barclays.absa.banking.R
import com.barclays.absa.banking.framework.BaseActivity
import com.barclays.absa.utils.AnalyticsUtil.trackAction
import kotlinx.android.synthetic.main.covid_alert_activity.*


class CovidAlertActivity : BaseActivity(R.layout.covid_alert_activity) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setToolBarBack(R.string.covid_banner_heading, true)
        trackAction("Covid Alert", "COVIDAlertSA_DetailsScreen_ScreenDisplayed")

        googleImageView.setOnClickListener {
            logoutAndGoToStartScreen()
            trackAction("Covid Alert", "COVIDAlertSA_DetailsScreen_DownloadButtonClicked")
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=za.gov.health.covidconnect&hl=en_ZA")))
        }
    }

}