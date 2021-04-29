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

package com.barclays.absa.banking.overdraft.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.style.ClickableSpan
import android.view.View
import android.view.ViewTreeObserver
import androidx.fragment.app.activityViewModels
import com.barclays.absa.banking.R
import com.barclays.absa.banking.framework.BaseFragment
import com.barclays.absa.banking.overdraft.ui.BusinessOverdraftActivity.Companion.BUSINESS_OVERDRAFT_ANALYTIC_TAG
import com.barclays.absa.utils.AnalyticsUtil
import com.barclays.absa.utils.CommonUtils
import com.barclays.absa.utils.TextFormatUtils
import com.google.android.material.appbar.AppBarLayout
import kotlinx.android.synthetic.main.fragment_business_overdraft_info.*
import kotlin.math.pow

class BusinessOverdraftInfoFragment : BaseFragment(R.layout.fragment_business_overdraft_info) {
    private var scrollRange: Float = 0.0f
    private val businessOverdraftViewModel by activityViewModels<BusinessOverdraftViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        AnalyticsUtil.trackAction(BUSINESS_OVERDRAFT_ANALYTIC_TAG, "BBVCLOD_EmergencyFunds_ScreenDisplayed")
        setUpToolbar()
        setUpComponentListeners()

        overdraftOfferTextView.text = getString(R.string.sole_prop_vcl_overdraft_intro_qualify_title, TextFormatUtils.formatBasicAmountAsRand(businessOverdraftViewModel.businessBankOverdraftData.vclOfferAmt))

        applyNowButton.setOnClickListener {
            AnalyticsUtil.trackAction(BUSINESS_OVERDRAFT_ANALYTIC_TAG, "BBVCLOD_EmergencyFunds_ApplyNowTapped")
            navigate(BusinessOverdraftInfoFragmentDirections.actionOverdraftIntroFragmentToOverdraftConsentFragment())
        }

        CommonUtils.makeTextClickable(baseActivity, getString(R.string.overdraft_intro_disclaimer), getString(R.string.absa_website_url), object : ClickableSpan() {
            override fun onClick(widget: View) {
                AnalyticsUtil.trackAction(BUSINESS_OVERDRAFT_ANALYTIC_TAG, "BBVCLOD_EmergencyFunds_LearnMoreLinkTapped")
                val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(businessOverdraftViewModel.businessBankOverdraftData.moreVCLInfoUrl))
                startActivity(browserIntent)
            }
        }, overdraftDisclaimerTextView, R.color.graphite)
    }

    private fun setUpComponentListeners() {
        vclOverdraftAppbarLayout.viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                scrollRange = vclOverdraftAppbarLayout.totalScrollRange.toFloat()
                vclOverdraftAppbarLayout.viewTreeObserver.removeOnGlobalLayoutListener(this)
            }
        })

        vclOverdraftAppbarLayout.addOnOffsetChangedListener(AppBarLayout.OnOffsetChangedListener { _, verticalOffset ->
            if (verticalOffset == 0) {
                animateTitleViews(1f)
            } else {
                val invertedVerticalOffset = scrollRange + verticalOffset
                val collapsePercent = (invertedVerticalOffset / scrollRange).toDouble().pow(1.0).toFloat()
                animateTitleViews(collapsePercent)
            }
        })
    }

    private fun setUpToolbar() {
        setToolBar("")
        hideToolBar()
        fragmentToolbar.apply {
            setNavigationIcon(R.drawable.ic_arrow_back_white)
            visibility = View.VISIBLE
            title = getString(R.string.emergency_funds)
            setNavigationOnClickListener {
                AnalyticsUtil.trackAction(BUSINESS_OVERDRAFT_ANALYTIC_TAG, "BBVCLOD_EmergencyFunds_BackButtonTapped")
                baseActivity.onBackPressed()
            }
        }
    }

    private fun animateTitleViews(alphaPercentage: Float) {
        with(overdraftOfferTextView) {
            alpha = alphaPercentage.toDouble().pow(3.0).toFloat()
            scaleX = alphaPercentage
            scaleY = alphaPercentage
        }
    }
}