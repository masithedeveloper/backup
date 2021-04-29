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
 */

package com.barclays.absa.utils

import androidx.annotation.NonNull
import androidx.annotation.Nullable
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment

object ListenerUtil {
    @Nullable
    fun <T> getListener(@NonNull fragment: Fragment?, @NonNull listenerClass: Class<T>): T? {
        var listener: T? = null
        when {
            fragment == null -> return null
            listenerClass.isInstance(fragment.parentFragment) -> listener = listenerClass.cast(fragment.parentFragment)
            listenerClass.isInstance(fragment.activity) -> listener = listenerClass.cast(fragment.activity)
            listenerClass.isInstance(fragment) -> listener = listenerClass.cast(fragment)
        }
        return listener
    }

    @Suppress("UNCHECKED_CAST")
    fun <F : Fragment> Fragment.getFragment(fragmentClass: Class<F>): Fragment? {
        val navHostFragment = activity?.supportFragmentManager?.fragments?.first() as? NavHostFragment
        navHostFragment?.let { hostFragment ->
            return hostFragment.childFragmentManager.fragments.find { fragmentClass.isAssignableFrom(it.javaClass) }
        }
        return null
    }
}