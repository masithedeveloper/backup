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
package com.barclays.absa.banking.overdraft.ui.fiveSteps

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.style.ClickableSpan
import android.view.View
import com.barclays.absa.banking.R
import com.barclays.absa.banking.databinding.OverdraftOffersContentFragmentBinding
import com.barclays.absa.banking.framework.AbsaBaseFragment
import com.barclays.absa.banking.overdraft.ui.OverdraftIntroActivity.OVERDRAFT
import com.barclays.absa.utils.AnalyticsUtil
import com.barclays.absa.utils.CommonUtils

class OverdraftOffersFragment : AbsaBaseFragment<OverdraftOffersContentFragmentBinding>() {

    override fun getLayoutResourceId(): Int = R.layout.overdraft_offers_content_fragment

    private val performActionUponTextClicked = object : ClickableSpan() {
        override fun onClick(widget: View) {
            val overdraftOfferLink = "https://www.absa.co.za/personal/loans/for-myself/overdraft/"
            val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(overdraftOfferLink))
            startActivity(browserIntent)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        AnalyticsUtil.trackAction(OVERDRAFT, "Overdraft_NeedEmergancyFundsScreen_ScreenDisplayed")
        CommonUtils.makeTextClickable(context, R.string.overdraft_intro_disclaimer, getString(R.string.overdraft_intro_hyperlink), performActionUponTextClicked, binding.overdraftDisclaimer, R.color.graphite)
    }

    companion object {
        fun newInstance(): OverdraftOffersFragment {
            return OverdraftOffersFragment()
        }
    }
}
