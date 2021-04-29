/*
 * Copyright (c) 2019 Absa Bank Limited, All Rights Reserved.
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

package com.barclays.absa.banking.card.ui.hotLeads

import android.content.Context
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.view.ViewTreeObserver
import androidx.navigation.fragment.findNavController
import com.barclays.absa.banking.R
import com.barclays.absa.banking.card.ui.creditCardHotLeads.HotLeadsHostActivity
import com.barclays.absa.banking.card.ui.creditCardHotLeads.HotLeadsViewModel
import com.barclays.absa.banking.framework.BaseFragment
import com.barclays.absa.utils.AnalyticsUtil
import com.barclays.absa.utils.viewModel
import com.google.android.material.appbar.AppBarLayout
import kotlinx.android.synthetic.main.hot_leads_details_fragment.*
import styleguide.utils.extensions.toTitleCase
import kotlin.math.pow

class HotLeadsDetailsFragment : BaseFragment(R.layout.hot_leads_details_fragment) {

    private var scrollRange: Float = 0.0f
    private lateinit var hotLeadsHostActivity: HotLeadsHostActivity
    private lateinit var hotLeadsViewModel: HotLeadsViewModel

    override fun onAttach(context: Context) {
        super.onAttach(context)
        hotLeadsHostActivity = activity as HotLeadsHostActivity
        hotLeadsViewModel = viewModel()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)
        setUpToolbar()
        setUpComponentListeners()

        continueButton.setOnClickListener {
            if (termsAndConditionsCheckBox.isChecked) {
                navigate(HotLeadsDetailsFragmentDirections.actionHotLeadsDetailsFragmentToHotLeadsContactDetailsFragment())
            } else {
                termsAndConditionsCheckBox.setErrorMessage(getString(R.string.plz_accept_conditions))
            }
        }

        termsAndConditionsCheckBox.setOnCheckedListener { termsAndConditionsCheckBox.clearError() }
    }

    private fun setUpToolbar() {
        fragmentToolbar.apply {
            setNavigationIcon(R.drawable.ic_arrow_back_white)
            visibility = View.VISIBLE
            title = getString(R.string.hot_leads_call_me_back).toTitleCase()
            setNavigationOnClickListener {
                hotLeadsHostActivity.onBackPressed()
            }
        }
    }

    private fun setUpComponentListeners() {
        appbarLayout.viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                scrollRange = appbarLayout.totalScrollRange.toFloat()
                appbarLayout.viewTreeObserver.removeOnGlobalLayoutListener(this)
            }
        })

        appbarLayout.addOnOffsetChangedListener(AppBarLayout.OnOffsetChangedListener { _, verticalOffset ->
            if (verticalOffset == 0) {
                animateTitleViews(1f)
            } else {
                val invertedVerticalOffset = scrollRange + verticalOffset
                val collapsePercent = (invertedVerticalOffset / scrollRange).toDouble().pow(1.0).toFloat()
                animateTitleViews(collapsePercent)
            }
        })

        continueButton.setOnClickListener { findNavController().navigate(R.id.action_hotLeadsDetailsFragment_to_hotLeadsContactDetailsFragment) }
    }

    private fun animateTitleViews(alphaPercentage: Float) {
        creditCardQualifyTitleTextView?.apply {
            alpha = alphaPercentage.toDouble().pow(3.0).toFloat()
            scaleX = alphaPercentage
            scaleY = alphaPercentage
        }

        creditCardAffordabilityCheckTitleTextView?.apply {
            alpha = alphaPercentage.toDouble().pow(3.0).toFloat()
            scaleX = alphaPercentage
            scaleY = alphaPercentage
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        hotLeadsHostActivity.onBackPressed()
        AnalyticsUtil.trackAction("Credit Card Hot Leads", "AcquisitionsHotLeads_BeforeWeBeginScreen_CloseButtonClicked")
        return super.onOptionsItemSelected(item)
    }
}