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

package com.barclays.absa.banking.saveAndInvest

import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import com.barclays.absa.banking.R
import com.barclays.absa.banking.databinding.SaveAndInvestSourceOfFundsFragmentBinding
import com.barclays.absa.utils.extensions.viewBinding

class SaveAndInvestSourceOfFundsFragment : SaveAndInvestBaseFragment(R.layout.save_and_invest_source_of_funds_fragment) {

    private val binding by viewBinding(SaveAndInvestSourceOfFundsFragmentBinding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setToolBar(R.string.depositor_plus_source_of_funds)
        binding.sourceOfFundsRecyclerView.setHasFixedSize(true)
        binding.sourceOfFundsRecyclerView.adapter = SaveAndInvestSourceOfFundsAdapter(saveAndInvestViewModel.sourceOfFundsList, saveAndInvestViewModel.selectedSourceOfFunds)

        binding.confirmButton.setOnClickListener {
            findNavController().navigateUp()
        }
    }
}