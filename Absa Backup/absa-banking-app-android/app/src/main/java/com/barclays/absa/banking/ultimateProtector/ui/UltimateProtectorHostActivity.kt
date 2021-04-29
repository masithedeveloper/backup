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

package com.barclays.absa.banking.ultimateProtector.ui

import android.os.Bundle
import android.view.View
import androidx.appcompat.widget.Toolbar
import androidx.databinding.DataBindingUtil
import androidx.navigation.NavController
import androidx.navigation.findNavController
import com.barclays.absa.banking.R
import com.barclays.absa.banking.databinding.UltimateProtectorHostActivityBinding
import com.barclays.absa.banking.framework.BaseActivity
import com.barclays.absa.banking.framework.analytics.AnalyticsUtils
import com.barclays.absa.banking.framework.app.BMBConstants
import com.barclays.absa.banking.ultimateProtector.services.dto.UltimateProtectorData
import com.barclays.absa.utils.viewModel

class UltimateProtectorHostActivity : BaseActivity(), UltimateProtectorView {
    private lateinit var binding: UltimateProtectorHostActivityBinding
    private lateinit var ultimateProtectorViewModel: UltimateProtectorViewModel
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.inflate(layoutInflater, R.layout.ultimate_protector_host_activity, null, false)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar as Toolbar?)
        navController = findNavController(R.id.navigationHostFragment)
        ultimateProtectorViewModel = viewModel()
        intent?.extras?.getParcelable<UltimateProtectorData>(ULTIMATE_PROTECTOR_DATA)?.let { ultimateProtectorViewModel.ultimateProtectorData = it }
    }

    fun setToolBar(title: String) {
        setToolBarBack(title)
    }

    fun showToolbar() {
        binding.toolbar.toolbar.visibility = View.VISIBLE
    }

    fun hideToolbar() {
        (binding.toolbar.toolbar.visibility) = View.GONE
    }

    fun showToolBarBackArrow() {
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    fun showProgressIndicator() {
        binding.progressIndicator.visibility = View.VISIBLE
    }

    fun hideProgressIndicator() {
        binding.progressIndicator.visibility = View.GONE
    }

    fun setStep(step: Int) {
        binding.progressIndicator.setNextStep(step)
    }

    override fun trackCurrentFragment(fragmentName: String) {
        AnalyticsUtils.getInstance().trackCustomScreen(fragmentName, ULTIMATE_PROTECTOR_CHANNEL, BMBConstants.TRUE_CONST)
    }

    override fun trackFragmentAction(screenName: String, actionName: String) {
        AnalyticsUtils.getInstance().trackAppActionStart(screenName, actionName, BMBConstants.TRUE_CONST)
    }

    override fun onBackPressed() {
        if (navController.currentDestination?.id != R.id.genericResultFragment) {
            super.onBackPressed()
        }
    }

    companion object {
        const val ULTIMATE_PROTECTOR_DATA = "ultimateProtector"
        const val ULTIMATE_PROTECTOR_CHANNEL = "WIMI_Life_UP"
    }
}