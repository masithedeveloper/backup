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
package com.barclays.absa.banking.linking.ui

import android.os.Bundle
import androidx.activity.viewModels
import androidx.navigation.fragment.NavHostFragment
import com.barclays.absa.banking.R
import com.barclays.absa.banking.databinding.LinkingActivityBinding
import com.barclays.absa.banking.framework.BaseActivity
import com.barclays.absa.banking.policy_beneficiaries.services.ManageBeneficiaryService.Companion.ID_NUMBER
import styleguide.screens.GenericResultScreenFragment

class LinkingActivity : BaseActivity() {
    private val viewModel by viewModels<LinkingViewModel>()
    private val binding by viewBinding(LinkingActivityBinding::inflate)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val navHostFragment = supportFragmentManager.findFragmentById(binding.linkingNavHostFragmentContainer.id) as NavHostFragment
        val navController = navHostFragment.navController

        intent.extras?.getString(ID_NUMBER)?.let {
            appCacheService.getSureCheckDelegate()?.let { sureCheckDelegate ->
                appCacheService.setLastSureCheckDelegateBeforeChangingPrimary(sureCheckDelegate)
            }

            if (it.isNotBlank()) {
                viewModel.idNumber = it
                appCacheService.setCustomerIdNumber(it)
                val bundle = LinkingIdVerificationRequestFragmentArgs.Builder(it).build().toBundle()
                navController.navigate(R.id.idVerificationRequestFragment, bundle)
            }
        }

        if (appCacheService.isBioAuthenticated()) {
            navController.navigate(R.id.linkingVerificationInProgressFragment)
        }
    }

    fun superOnBackPressed() {
        super.onBackPressed()
    }

    override fun onBackPressed() {
        if (currentFragment !is GenericResultScreenFragment) {
            super.onBackPressed()
        }
    }
}