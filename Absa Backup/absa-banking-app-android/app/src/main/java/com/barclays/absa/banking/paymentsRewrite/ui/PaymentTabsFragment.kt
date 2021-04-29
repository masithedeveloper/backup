/*
 * Copyright (c) 2021 Absa Bank Limited, All Rights Reserved.
 *
 * This code is confidential to Absa Bank Limited and shall not be disclosed
 * outside the Bank without the prior written permission of the Absa Legal
 *
 * In the event that such disclosure is permitted the code shall not be copied
 * or distributed other than on a need-to-know basis and any recipients may be
 * required to sign a confidentiality undertaking in favor of Absa Bank
 * Limited
 */
package com.barclays.absa.banking.paymentsRewrite.ui

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.viewpager.widget.PagerAdapter
import com.barclays.absa.banking.R
import com.barclays.absa.banking.databinding.PaymentTabsFragmentBinding
import com.barclays.absa.banking.framework.app.BMBApplication
import com.barclays.absa.utils.extensions.viewBinding

class PaymentTabsFragment : PaymentsBaseFragment(R.layout.payment_tabs_fragment) {
    private val binding by viewBinding(PaymentTabsFragmentBinding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.viewPager.adapter = PaymentTabsViewPagerAdapter(childFragmentManager)
        binding.paymentTabsTabLayout.setupWithViewPager(binding.viewPager)
        paymentsActivity.setToolbarBackground(R.color.purple)
        paymentsActivity.updateToolbarForTabs()
    }
}

class PaymentTabsViewPagerAdapter(fragmentManager: FragmentManager) : FragmentStatePagerAdapter(fragmentManager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

    override fun getItem(position: Int): Fragment = if (position == 0) {
        PrivateBeneficiaryDetailsFragment.newInstance()
    } else {
        BillPaymentBeneficiaryDetailsFragment.newInstance()
    }

    override fun getPageTitle(position: Int): CharSequence = if (position == 0) {
        BMBApplication.getInstance().getString(R.string.pay_someone)
    } else {
        BMBApplication.getInstance().getString(R.string.pay_bill)
    }

    override fun getCount() = 2

    override fun getItemPosition(`object`: Any) = PagerAdapter.POSITION_NONE
}