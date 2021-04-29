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
package com.barclays.absa.banking.lawForYou.ui

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.appcompat.widget.Toolbar
import com.barclays.absa.banking.R
import com.barclays.absa.banking.lawForYou.services.dto.CoverAmounts
import com.barclays.absa.utils.AnalyticsUtil
import com.barclays.absa.utils.PdfUtil
import kotlinx.android.synthetic.main.law_for_you_apply_now_fragment.*
import styleguide.utils.extensions.toRandAmount

class LawForYouApplyNowFragment : LawForYouBaseFragment(R.layout.law_for_you_apply_now_fragment) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        hideToolBar()

        val lawForYouToolbar = applyNowToolbar as Toolbar
        lawForYouToolbar.title = getString(R.string.law_for_you_title)

        lawForYouActivity.apply {
            setSupportActionBar(lawForYouToolbar)
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
            setProgressIndicatorVisibility(View.GONE)
        }
        setHasOptionsMenu(true)

        coverPremiumTextView.text = lawForYouViewModel.applyLawForYou.lowestPremium.toRandAmount()
        lawForYouViewModel.selectedCoverOption = CoverAmounts()

        findOutMoreOptionActionButtonView.setOnClickListener {
            AnalyticsUtil.trackAction("Law For You", "LawForYou_BenefitsPDF_ScreenDisplayed")
            PdfUtil.showPDFInApp(lawForYouActivity, lawForYouViewModel.applyLawForYou.findOutMorePdfUrl)
        }
        applyNowButton.setOnClickListener {
            lawForYouViewModel.fetchCoverAmounts()

            if (lawForYouViewModel.coverOptionsMutableLifeData.hasObservers()) {
                return@setOnClickListener
            }

            lawForYouViewModel.coverOptionsMutableLifeData.observe(viewLifecycleOwner, {
                navigate(LawForYouApplyNowFragmentDirections.actionLawForYouApplyNowFragmentToLawForYouPlanCoverPremiumFragment())
                dismissProgressDialog()
            })
        }
        AnalyticsUtil.trackAction("Law For You", "LawForYou_Benefits_ScreenDisplayed")
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        lawForYouActivity.onBackPressed()
        return super.onOptionsItemSelected(item)
    }

    override fun onDestroyView() {
        lawForYouViewModel.coverOptionsMutableLifeData.removeObservers(viewLifecycleOwner)
        super.onDestroyView()
    }
}