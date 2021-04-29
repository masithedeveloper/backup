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
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.findNavController
import com.barclays.absa.banking.R
import com.barclays.absa.banking.flexiFuneral.services.dto.FlexiFuneralData
import com.barclays.absa.banking.framework.BaseActivity
import kotlinx.android.synthetic.main.flexi_funeral_activity.*

class FlexiFuneralActivity : BaseActivity(R.layout.flexi_funeral_activity) {
    lateinit var flexiFuneralData: FlexiFuneralData
    private lateinit var navController: NavController

    companion object {
        const val FLEXI_FUNERAL_DATA = "flexiFuneralData"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        navController = findNavController(R.id.flexiFuneralNavHostFragment)
        setSupportActionBar(toolbar as Toolbar?)
        (intent.getParcelableExtra(FLEXI_FUNERAL_DATA) as? FlexiFuneralData)?.let { flexiFuneralData = it }
    }

    fun setToolBar(title: String) {
        setToolBarBack(title)
    }

    fun showToolbar() {
        toolbar.visibility = View.VISIBLE
    }

    fun hideToolbar() {
        toolbar.visibility = View.GONE
    }

    fun setStep(step: Int) {
        mainMemberProgressIndicatorView.setNextStep(step)
        mainMemberProgressIndicatorView.animateNextStep()
    }

    fun hideProgressIndicatorView() {
        mainMemberProgressIndicatorView.visibility = View.GONE
    }

    fun showProgressIndicatorView() {
        mainMemberProgressIndicatorView.visibility = View.VISIBLE
    }

    override fun onBackPressed() {
        if (navController.currentDestination?.id == R.id.flexiFuneralMainMemberFragment) {
            (currentFragment as FlexiFuneralMainMemberFragment).onBackPressed()
        } else if (navController.currentDestination?.id != R.id.flexiFuneralGenericResultScreenFragment) {
            super.onBackPressed()
        }
    }

    override val currentFragment: Fragment?
        get() {
            val navHostFragment = supportFragmentManager.findFragmentById(R.id.flexiFuneralNavHostFragment)
            return navHostFragment?.childFragmentManager?.fragments?.get(0)
        }

    fun superOnBackPressed() {
        super.onBackPressed()
    }
}