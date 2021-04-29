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

package com.barclays.absa.banking.fixedDeposit

import android.os.Bundle
import android.view.View
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.barclays.absa.banking.R
import com.barclays.absa.banking.framework.BaseFragment
import com.barclays.absa.banking.shared.services.SharedViewModel
import com.barclays.absa.banking.shared.services.dto.LookupItem
import com.barclays.absa.utils.AnalyticsUtil
import com.barclays.absa.utils.viewModel
import kotlinx.android.synthetic.main.fixed_deposit_source_of_funds_fragment.*

class FixedDepositSourceOfFundsFragment : BaseFragment(R.layout.fixed_deposit_source_of_funds_fragment) {

    private lateinit var selectedList: ArrayList<LookupItem>
    private lateinit var sharedViewModel: SharedViewModel

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        AnalyticsUtil.trackAction(FixedDepositActivity.FIXED_DEPOSIT, "Fixed Deposit Source of Funds Screen")

        (activity as FixedDepositActivity).setToolbarTitle(getString(R.string.risk_based_approach_select_source_of_funds))
        (activity as FixedDepositActivity).showToolbar()
        initialiseViewModel()

        selectedList = if (sharedViewModel.selectedSourceOfFunds.value != null && sharedViewModel.selectedSourceOfFunds.value!!.isNotEmpty()) {
            sharedViewModel.selectedSourceOfFunds.value!!
        } else {
            ArrayList()
        }

        if (sharedViewModel.sourceOfFundsResponse.value != null) {
            sourceOfFundsRecyclerView.apply {
                adapter = FixedDepositSourceOfFundsAdapter(sharedViewModel.sourceOfFundsResponse.value!!.items, selectedList)
                layoutManager = LinearLayoutManager(context)
            }
        }

        doneButton.setOnClickListener {
            sharedViewModel.selectedSourceOfFunds.value = selectedList
            Navigation.findNavController(view).navigateUp()
        }
    }

    private fun initialiseViewModel() {
        sharedViewModel = baseActivity.viewModel()
    }
}