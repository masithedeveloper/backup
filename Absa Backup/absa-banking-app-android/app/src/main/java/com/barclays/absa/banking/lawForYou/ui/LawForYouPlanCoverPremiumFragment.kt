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
import android.view.View
import com.barclays.absa.banking.R
import com.barclays.absa.banking.lawForYou.services.dto.CoverAmounts
import com.barclays.absa.banking.lawForYou.services.dto.CoverAmountsResponse
import com.barclays.absa.utils.AnalyticsUtil
import com.barclays.absa.utils.AnimationHelper
import com.barclays.absa.utils.PdfUtil
import kotlinx.android.synthetic.main.law_for_you_plan_cover_premium_fragment.*

class LawForYouPlanCoverPremiumFragment : LawForYouBaseFragment(R.layout.law_for_you_plan_cover_premium_fragment) {

    private lateinit var lawForYouCoverOptionsAdapter: LawForYouCoverOptionsAdapter

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        showToolBar()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setToolBar(R.string.plan_cover_premium)
        lawForYouActivity.setProgressStep(1)

        lawForYouActivity.setProgressIndicatorVisibility(View.VISIBLE)

        lawForYouViewModel.coverOptionsMutableLifeData.observe(viewLifecycleOwner, { setupCoverOptions(it) })

        continueButton.setOnClickListener {
            if (lawForYouViewModel.selectedCoverOption.coverAmount.isEmpty()) {
                AnimationHelper.shakeShakeAnimate(coverAmountsRecyclerView)
            } else {
                lawForYouViewModel.lawForYouDetails.coverPlan = lawForYouViewModel.selectedCoverOption.cover
                navigate(LawForYouPlanCoverPremiumFragmentDirections.actionLawForYouPlanCoverPremiumFragmentToLawForYouContactDetailsFragment())
            }
        }

        comparePlansOptionActionButtonView.setOnClickListener {
            AnalyticsUtil.trackAction("Law For You", "LawForYou_ComparePlansPDF_ScreenDisplayed")
            PdfUtil.showPDFInApp(lawForYouActivity, lawForYouViewModel.applyLawForYou.coverFactsheetPdfUrl)
        }
        AnalyticsUtil.trackAction("Law For You", "LawForYou_SelectPlan_ScreenDisplayed")
    }

    private fun setupCoverOptions(coverAmountsResponse: CoverAmountsResponse) {
        lawForYouCoverOptionsAdapter = LawForYouCoverOptionsAdapter(coverAmountsResponse.coverAmounts,
                object : LawForYouCoverOptionsAdapter.OnCoverOptionSelected {
                    override fun onCoverSelect(coverOption: CoverAmounts) {
                        lawForYouViewModel.selectedCoverOption = coverOption
                        lawForYouViewModel.lawForYouDetails.apply {
                            coverPremiumAmount = coverOption.monthlyPremium
                            coverAssuredAmount = coverOption.coverAmount
                        }
                    }
                })
        lawForYouCoverOptionsAdapter.selectedCoverAmount = lawForYouViewModel.selectedCoverOption
        coverAmountsRecyclerView.apply {
            setHasFixedSize(true)
            adapter = lawForYouCoverOptionsAdapter
        }
    }

    override fun onDestroyView() {
        lawForYouViewModel.coverOptionsMutableLifeData.removeObservers(viewLifecycleOwner)
        super.onDestroyView()
    }
}