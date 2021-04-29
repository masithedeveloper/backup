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

package com.barclays.absa.banking.fixedDeposit

import android.os.Bundle
import android.view.View
import androidx.navigation.findNavController
import com.barclays.absa.banking.R
import com.barclays.absa.banking.boundary.model.AccountObject
import com.barclays.absa.banking.fixedDeposit.FixedDepositDetailsFragment.Companion.FIXED_DEPOSIT_ACCOUNT_DETAILS
import com.barclays.absa.banking.fixedDeposit.FixedDepositDetailsFragment.Companion.FIXED_DEPOSIT_ACCOUNT_OBJECT
import com.barclays.absa.banking.fixedDeposit.FixedDepositDetailsFragment.FixedDepositFlow
import com.barclays.absa.banking.fixedDeposit.responseListeners.FixedDepositViewModel
import com.barclays.absa.banking.fixedDeposit.services.dto.FixedDepositAccountDetailsResponse
import com.barclays.absa.banking.framework.BaseActivity
import com.barclays.absa.utils.viewModel
import kotlinx.android.synthetic.main.fixed_deposit_maintenance_activity.*

class FixedDepositMaintenanceActivity : BaseActivity(R.layout.fixed_deposit_maintenance_activity) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val fixedDepositViewModel: FixedDepositViewModel = viewModel()
        fixedDepositViewModel.accountDetailsResponse.value = intent.getParcelableExtra<FixedDepositAccountDetailsResponse>(FIXED_DEPOSIT_ACCOUNT_DETAILS) as FixedDepositAccountDetailsResponse
        val flow = intent.getSerializableExtra(FixedDepositFlow::class.java.name) as FixedDepositFlow
        if (flow == FixedDepositFlow.REINVEST) {
            findNavController(R.id.navigationHostFragment).setGraph(R.navigation.fixed_deposit_reinvestment_navigation_graph)
        }
        fixedDepositViewModel.accountObject = intent.getSerializableExtra(FIXED_DEPOSIT_ACCOUNT_OBJECT) as AccountObject
    }

    fun setProgressStep(step: Int) {
        progressIndicatorView.setNextStep(step)
        progressIndicatorView.animateNextStep()
    }

    fun hideProgressIndicatorAndToolbar() {
        hideToolBar()
        progressIndicatorView.visibility = View.GONE
    }
}