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

package com.barclays.absa.banking.payments.international

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import com.barclays.absa.banking.R
import com.barclays.absa.banking.databinding.InternationalPaymentsFirstTimeUseFragmentBinding
import com.barclays.absa.banking.payments.international.InternationalPaymentsConstants.Companion.INTERNATIONAL_PAYMENTS
import com.barclays.absa.utils.AnalyticsUtil
import kotlinx.android.synthetic.main.international_payments_first_time_use_fragment.*
import styleguide.content.CarouselPage

class InternationalPaymentsFirstTimeFragment : InternationalPaymentsAbstractBaseFragment(R.layout.international_payments_first_time_use_fragment) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setToolBar(R.string.how_this_works)
        AnalyticsUtil.trackAction(INTERNATIONAL_PAYMENTS, "InternationalPayments_HowThisWorksOnboardingScreen_ScreenDisplayed")
        initData()

        gotItButton.setOnClickListener {
            AnalyticsUtil.trackAction(INTERNATIONAL_PAYMENTS, "InternationalPayments_HowThisWorksOnboardingScreen_OkGotItButtonClicked")
            navigate(InternationalPaymentsFirstTimeFragmentDirections.actionInternationalPaymentsFirstTimeFragmentToInternationalPaymentHubFragment())
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        internationalPaymentsActivity.finish()
        return super.onOptionsItemSelected(item)
    }

    private fun initData() = howThisWorksViewPager.populateExtendedCarouselPager(ArrayList<CarouselPage>().apply {
        add(CarouselPage(getString(R.string.how_does_this_work_step_one_title), getString(R.string.how_does_this_work_step_one_description), R.drawable.ic_international_payments_how_this_works_1))
        add(CarouselPage(getString(R.string.how_does_this_work_step_two_title), getString(R.string.how_does_this_work_step_two_description), R.drawable.ic_international_payments_how_this_works_2))
        add(CarouselPage(getString(R.string.how_does_this_work_step_three_title), getString(R.string.how_does_this_work_step_three_description), R.drawable.ic_international_payments_how_this_works_3))
        add(CarouselPage(getString(R.string.how_does_this_work_step_four_title), getString(R.string.how_does_this_work_step_four_description), R.drawable.ic_international_payments_how_this_works_4))

    })
}
