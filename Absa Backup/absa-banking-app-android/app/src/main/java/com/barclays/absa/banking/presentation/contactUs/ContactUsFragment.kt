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
package com.barclays.absa.banking.presentation.contactUs

import android.os.Bundle
import android.util.SparseArray
import android.view.View
import com.barclays.absa.banking.R
import com.barclays.absa.banking.framework.BaseFragment
import com.google.android.material.tabs.TabLayout
import kotlinx.android.synthetic.main.contact_us_main_fragment.*
import styleguide.bars.FragmentPagerItem
import styleguide.bars.TabPager

class ContactUsFragment : BaseFragment(R.layout.contact_us_main_fragment) {

    private lateinit var contactUsSouthAfricaFragment: ContactUsSouthAfricaFragment

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        contactUsSouthAfricaFragment = ContactUsSouthAfricaFragment.newInstance(getString(R.string.south_africa))
        val tabs = SparseArray<FragmentPagerItem>().apply {
            append(0, contactUsSouthAfricaFragment)
            append(1, ContactUsInternationalFragment.newInstance(getString(R.string.international)))
        }

        with(tabBarView) {
            addTabs(tabs)
            addOnTabSelectedListener(TabLayout.ViewPagerOnTabSelectedListener(viewPager))
        }

        with(viewPager) {
            adapter = TabPager(parentFragmentManager, tabs)
            addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(tabBarView))
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        contactUsSouthAfricaFragment.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }
}