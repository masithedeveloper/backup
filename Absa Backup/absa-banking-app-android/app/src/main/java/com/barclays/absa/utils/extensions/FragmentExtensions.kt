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
 *
 */

package com.barclays.absa.utils.extensions

import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment
import androidx.viewbinding.ViewBinding
import com.barclays.absa.banking.framework.FragmentViewBindingDelegate

fun <T : ViewBinding> Fragment.viewBinding(viewBindingFactory: (View) -> T) = FragmentViewBindingDelegate(this, viewBindingFactory)

fun AppCompatActivity.getNavHostFragment(): NavHostFragment = this.supportFragmentManager.fragments.find { it is NavHostFragment } as NavHostFragment

@Suppress("UNCHECKED_CAST")
fun <F : Fragment> Fragment.getNavGraphFragment(fragmentClass: Class<F>): Fragment? {
    val navHostFragment = (activity as AppCompatActivity).getNavHostFragment()
    return navHostFragment.childFragmentManager.fragments.find { fragmentClass.isAssignableFrom(it.javaClass) }
}