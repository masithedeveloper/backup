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
package com.barclays.absa.banking.personalLoan.ui

import android.content.Context
import android.os.Bundle
import android.view.View
import com.barclays.absa.banking.R
import com.barclays.absa.banking.boundary.model.AccountObject
import com.barclays.absa.banking.framework.BaseFragment
import com.barclays.absa.banking.shared.genericTransactionHistory.ui.GenericTransactionHistoryViewModel
import com.barclays.absa.utils.TextFormatUtils
import com.barclays.absa.utils.viewModel
import kotlinx.android.synthetic.main.personal_loan_hub_header_fragment.*

class PersonalLoanHeaderFragment : BaseFragment(R.layout.personal_loan_hub_header_fragment) {

    private lateinit var personalLoanVCLViewModel: PersonalLoanVCLViewModel
    private lateinit var genericTransactionHistoryViewModel: GenericTransactionHistoryViewModel

    override fun onAttach(context: Context) {
        super.onAttach(context)
        val hostActivity: PersonalLoanHubActivity = context as PersonalLoanHubActivity
        personalLoanVCLViewModel = hostActivity.viewModel()
        genericTransactionHistoryViewModel = hostActivity.viewModel()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        genericTransactionHistoryViewModel.accountDetail?.let {
            getCurrentBalance(it)
        }
        setUpObserverForAvailableAdvanceOrArrearsAmount()
    }

    private fun setUpObserverForAvailableAdvanceOrArrearsAmount() {
        personalLoanVCLViewModel.personalLoanHubExtendedResponse.value?.personalLoan?.apply {
            availableAdvanceLabelTextView.text = if (arrearsAdvanceAmount >= 0) {
                getString(R.string.personal_loan_hub_available_advance)
            } else {
                getString(R.string.personal_loan_hub_arrears_amount)
            }
            availableAdvanceOrArrearsAmountTextView.text = TextFormatUtils.formatBasicAmountAsRand((personalLoanVCLViewModel.personalLoanHubExtendedResponse.value?.personalLoan?.arrearsAdvanceAmount.toString()))
        }
    }

    private fun getCurrentBalance(accountObject: AccountObject) {
        currentBalanceTextView.text = if (accountObject.currentBalance != null) {
            TextFormatUtils.formatBasicAmountAsRand(accountObject.currentBalance?.getAmount().toString())
        } else {
            getString(R.string.personal_loan_hub_current_balance_not_available)
        }
    }
}