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

package com.barclays.absa.banking.freeCover.ui

import android.os.Bundle
import android.view.View
import com.barclays.absa.banking.R
import com.barclays.absa.banking.freeCover.ui.FreeCoverActivity.Companion.FREE_COVER_ANALYTICS_TAG
import com.barclays.absa.utils.AnalyticsUtil
import com.barclays.absa.utils.DateUtils
import com.barclays.absa.utils.DateUtils.DATE_DISPLAY_PATTERN_FULL
import kotlinx.android.synthetic.main.free_cover_details_fragment.*
import styleguide.utils.extensions.toRandAmount

class FreeCoverDetailsFragment : FreeCoverBaseFragment(R.layout.free_cover_details_fragment) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpToolBar()
        initData()
        setUpOnClickListener()

        AnalyticsUtil.trackAction(FREE_COVER_ANALYTICS_TAG, "FreeCover_CoverDetails_ScreenDisplayed")
    }

    private fun setUpToolBar() {
        setToolBar(getString(R.string.free_cover_cover_title))
        showToolBar()
        with(freeCoverInterface) {
            showProgressIndicatorView()
            setStep(1)
        }
    }

    private fun setUpOnClickListener() {
        continueButton.setOnClickListener {
            AnalyticsUtil.trackAction(FREE_COVER_ANALYTICS_TAG,"FreeCover_CoverDetails_CoverContinueButtonClicked")
            navigate(FreeCoverDetailsFragmentDirections.actionFreeCoverDetailsFragmentToFreeCoverEmploymentDetailsFragment())
        }
    }

    private fun initData() {
        freeCoverViewModel.policyStartDate = DateUtils.getTheFirstOfNextMonthDate(DATE_DISPLAY_PATTERN_FULL)

        freeCoverViewModel.coverAmountApplyFreeCoverResponse.value?.coverAmount?.let {
            coverAmountPrimaryContentAndLabelView.setContentText(it.coverAmount.toRandAmount())
            monthlyPremiumSecondaryContentAndLabelView.setContentText(it.monthlyPremium.toRandAmount())
        }
        startDateSecondaryContentAndLabelView.setContentText(freeCoverViewModel.policyStartDate)
    }
}