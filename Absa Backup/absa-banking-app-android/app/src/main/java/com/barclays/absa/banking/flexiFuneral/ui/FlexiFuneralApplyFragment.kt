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

package com.barclays.absa.banking.flexiFuneral.ui

import android.os.Bundle
import android.view.View
import androidx.lifecycle.MutableLiveData
import com.barclays.absa.banking.R
import com.barclays.absa.utils.AnalyticsUtil
import com.barclays.absa.utils.PdfUtil
import kotlinx.android.synthetic.main.flexi_funeral_apply_fragment.*
import styleguide.utils.extensions.toRandAmount

class FlexiFuneralApplyFragment : FlexiFuneralBaseFragment(R.layout.flexi_funeral_apply_fragment) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpToolBar()
        setUpOnClickListeners()
        initData()

        AnalyticsUtil.trackAction("WIMI_FlexiFuneral", "FlexiFuneral_Benefits_ScreenDisplayed")
    }

    private fun setUpToolBar() {
        hostActivity.hideToolbar()
        hostActivity.hideProgressIndicatorView()
        flexiFuneralToolbar.apply {
            setNavigationIcon(R.drawable.ic_arrow_back_white)
            visibility = View.VISIBLE
            title = getString(R.string.flexi_funeral_cover_title)
            setNavigationOnClickListener {
                hostActivity.onBackPressed()
            }
        }
    }

    private fun initData() {
        flexiFuneralViewModel.flexiFuneralData = hostActivity.flexiFuneralData
        coverPremiumTextView.text = hostActivity.flexiFuneralData.lowestPremium.toRandAmount()
    }

    private fun setUpOnClickListeners() {
        findOutMoreOptionActionButtonView.setOnClickListener {
            if (hostActivity.flexiFuneralData.findOutMorePdfUrl.isNotEmpty()) {
                AnalyticsUtil.trackAction("WIMI_FlexiFuneral", "FlexiFuneral_BenefitsPDF_ScreenDisplayed")
                PdfUtil.showPDFInApp(baseActivity, flexiFuneralViewModel.flexiFuneralData.findOutMorePdfUrl)
            }
        }

        applyNowButton.setOnClickListener {
            if (flexiFuneralViewModel.validationRules.value == null || flexiFuneralViewModel.flexiFuneralValidationRulesDetails.isEmpty()) {
                flexiFuneralViewModel.fetchValidationRules()

                flexiFuneralViewModel.validationRules = MutableLiveData()
                flexiFuneralViewModel.validationRules.observe(viewLifecycleOwner, {
                    if (it.validationRules.isNotEmpty()) {
                        flexiFuneralViewModel.flexiFuneralValidationRulesDetails = it.validationRules
                        navigate(FlexiFuneralApplyFragmentDirections.actionFlexiFuneralApplyFragmentToFlexiFuneralMainMemberFragment())
                        dismissProgressDialog()
                    } else {
                        showGenericErrorMessage()
                    }
                })
            } else {
                navigate(FlexiFuneralApplyFragmentDirections.actionFlexiFuneralApplyFragmentToFlexiFuneralMainMemberFragment())
            }
        }
    }
}