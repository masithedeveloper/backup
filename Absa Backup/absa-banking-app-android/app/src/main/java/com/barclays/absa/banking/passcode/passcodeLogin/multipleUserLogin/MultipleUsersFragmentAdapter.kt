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
package com.barclays.absa.banking.passcode.passcodeLogin.multipleUserLogin

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.barclays.absa.banking.boundary.model.UserProfile
import com.barclays.absa.banking.passcode.passcodeLogin.multipleUserLogin.userFragments.ExistingUserFragment
import com.barclays.absa.banking.passcode.passcodeLogin.multipleUserLogin.userFragments.NewUserFragment

class MultipleUsersFragmentAdapter(fragmentManager: FragmentManager, users: List<UserProfile>) : FragmentPagerAdapter(fragmentManager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

    companion object {
        lateinit var userProfiles: List<UserProfile>
        private fun isNewUserAllowed(): Boolean = userProfiles.size < SimplifiedLoginActivity.MAXIMUM_PER_DEVICE_USER_PROFILES
        private fun isNewUserPosition(position: Int): Boolean = position == userProfiles.size
    }

    init {
        userProfiles = users
    }

    override fun getItem(position: Int): Fragment = if (isNewUserAllowed() && isNewUserPosition(position)) NewUserFragment.newInstance(position) else ExistingUserFragment.newInstance(position)

    override fun getCount(): Int = if (isNewUserAllowed()) userProfiles.size + 1 else userProfiles.size
}