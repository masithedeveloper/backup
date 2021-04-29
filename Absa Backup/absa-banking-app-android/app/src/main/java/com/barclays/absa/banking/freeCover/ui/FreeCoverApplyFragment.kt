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
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.barclays.absa.banking.R
import com.barclays.absa.banking.freeCover.ui.FreeCoverActivity.Companion.FREE_COVER_ANALYTICS_TAG
import com.barclays.absa.utils.AnalyticsUtil
import com.barclays.absa.utils.PdfUtil
import kotlinx.android.synthetic.main.free_cover_apply_fragment.*

class FreeCoverApplyFragment : FreeCoverBaseFragment(R.layout.free_cover_apply_fragment) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpToolBar()
        setUpOnClickListeners()

        AnalyticsUtil.trackAction(FREE_COVER_ANALYTICS_TAG, "FreeCover_BenefitScreen_ScreenDisplayed")
    }

    private fun setUpToolBar() {
        hideToolBar()
        freeCoverInterface.hideProgressIndicatorView()

        freeCoverToolbar.apply {
            visibility = View.VISIBLE
            title = getString(R.string.free_cover_title)
            setNavigationIcon(R.drawable.ic_arrow_back_white)
            setNavigationOnClickListener {
                hostActivity.onBackPressed()
            }
        }
    }

    private fun setUpOnClickListeners() {
        applyNowButton.setOnClickListener {
            AnalyticsUtil.trackAction(FREE_COVER_ANALYTICS_TAG, "FreeCover_BenefitScreen_ApplyNowButtonClicked")

            with(freeCoverViewModel) {
                if (coverAmountApplyFreeCoverResponse.value == null) {
                    fetchCoverAmount()

                    coverAmountApplyFreeCoverResponse = MutableLiveData()
                    coverAmountApplyFreeCoverResponse.observe(viewLifecycleOwner, Observer {
                        applyFreeCoverData.coverAmount = it.coverAmount.coverAmount
                        applyFreeCoverData.monthlyPremium = it.coverAmount.monthlyPremium
                        navigate(FreeCoverApplyFragmentDirections.actionFreeCoverApplyFragmentToFreeCoverDetailsFragment())
                        dismissProgressDialog()
                    })
                } else {
                    navigate(FreeCoverApplyFragmentDirections.actionFreeCoverApplyFragmentToFreeCoverDetailsFragment())
                }
            }
        }

        findOutMoreOptionActionButtonView.setOnClickListener {
            AnalyticsUtil.trackAction(FREE_COVER_ANALYTICS_TAG, "FreeCover_BenefitScreen_FindOutMoreButtonClicked")
            PdfUtil.showPDFInApp(hostActivity, freeCoverViewModel.freeCoverData.findOutMoreURL)
        }
    }
}