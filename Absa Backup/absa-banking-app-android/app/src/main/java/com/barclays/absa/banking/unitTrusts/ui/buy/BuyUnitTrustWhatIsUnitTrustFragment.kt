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

package com.barclays.absa.banking.unitTrusts.ui.buy

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.style.ClickableSpan
import android.view.View
import com.barclays.absa.banking.R
import com.barclays.absa.utils.CommonUtils
import kotlinx.android.synthetic.main.buy_unit_trust_what_is_unit_trust_fragment.*
import styleguide.utils.extensions.toTitleCase

class BuyUnitTrustWhatIsUnitTrustFragment : BuyUnitTrustBaseFragment(R.layout.buy_unit_trust_what_is_unit_trust_fragment) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setToolBar(getString(R.string.buy_unit_trust_what_is_unit_trust).toTitleCase())
        hostActivity.trackCurrentFragment("WIMI_UT_BuyNew_Learn_WhatisUT")
        makeTextClickable()
    }

    fun makeTextClickable() {
        CommonUtils.makeTextClickable(context, R.string.buy_unit_trust_what_is_unit_trust_description, getString(R.string.unit_trusts_link), object : ClickableSpan() {
            override fun onClick(widget: View) {
                val intent = Intent(Intent.ACTION_VIEW).setData(Uri.parse("https://www.absainvestmentmanagement.co.za/wealth-and-investment-management/personal/investment-products/absa-unit-trusts/"))
                startActivity(intent)
            }
        }, descriptionTextView, R.color.graphite)
    }
}