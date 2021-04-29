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
package com.barclays.absa.banking.buy.ui.electricity

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import com.barclays.absa.banking.R
import com.barclays.absa.banking.framework.BaseFragment
import com.barclays.absa.utils.AnalyticsUtil
import kotlinx.android.synthetic.main.prepaid_electricity_need_help_fragment.*

class PrepaidElectricityNeedHelpFragment : BaseFragment(R.layout.prepaid_electricity_need_help_fragment) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setToolBar(R.string.prepaid_electricity_need_help)

        prepaidElectricityQueriesContactView.setOnClickListener {
            val openDialerIntent = Intent(Intent.ACTION_DIAL)
            openDialerIntent.data = Uri.parse("tel:" + getString(R.string.prepaid_electricity_queries_number))
            startActivity(openDialerIntent)
        }

        AnalyticsUtil.trackAction(PrepaidElectricityActivity.BUY_ELECTRICITY, "BuyElectricity_NeedHelpScreen_ScreenDisplayed")
    }
}