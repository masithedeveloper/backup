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

package com.barclays.absa.banking.will.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.barclays.absa.banking.R
import com.barclays.absa.banking.framework.BaseActivity

class WillActivity : BaseActivity() {
    private lateinit var willViewModel: WillViewModel
    private lateinit var navHostFragment: Fragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.will_host_activity)
        willViewModel = WillViewModel()
        navHostFragment = supportFragmentManager.findFragmentById(R.id.willsNavigationHostFragment)!!
    }

    override fun onBackPressed() {
        val navController = findNavController(R.id.willsNavigationHostFragment)
        if (navController.currentDestination?.id != R.id.genericResultScreenFragment) {
            super.onBackPressed()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        val fragments = currentFragment?.childFragmentManager?.fragments
        if (fragments?.isNotEmpty()!!) {
            fragments[0].onRequestPermissionsResult(requestCode, permissions, grantResults)
        }
    }
}