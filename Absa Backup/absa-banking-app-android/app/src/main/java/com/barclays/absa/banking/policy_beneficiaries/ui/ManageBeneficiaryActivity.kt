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

package com.barclays.absa.banking.policy_beneficiaries.ui

import android.os.Bundle
import android.view.View
import androidx.navigation.findNavController
import com.barclays.absa.banking.R
import com.barclays.absa.banking.framework.BaseActivity
import com.barclays.absa.utils.viewModel
import kotlinx.android.synthetic.main.manage_beneficiaries_activity.*

class ManageBeneficiaryActivity : BaseActivity() {

    private lateinit var manageBeneficiaryViewModel: ManageBeneficiaryViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.manage_beneficiaries_activity)
        setToolBarBack(R.string.manage_beneficiary_title)

        manageBeneficiaryViewModel = viewModel()
        InsuranceBeneficiaryHelper.buildRelationships(resources.getStringArray(R.array.relationships))
    }

    fun showProgressIndicator() {
        progressIndicator.visibility = View.VISIBLE
    }

    fun hideProgressIndicator() {
        progressIndicator.visibility = View.GONE
    }

    fun setStep(step: Int) = progressIndicator.setNextStep(step)

    override fun onBackPressed() {
        val navController = findNavController(R.id.navigationHostFragment)
        if (navController.currentDestination?.id != R.id.genericResultFragment) {
            super.onBackPressed()
        }
    }
}