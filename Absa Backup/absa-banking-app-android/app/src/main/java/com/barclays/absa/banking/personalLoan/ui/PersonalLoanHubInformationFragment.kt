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
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.barclays.absa.banking.R
import com.barclays.absa.banking.shared.genericTransactionHistory.ui.GenericTransactionHistoryViewModel
import com.barclays.absa.utils.DateUtils
import com.barclays.absa.utils.TextFormatUtils
import com.barclays.absa.utils.viewModel
import kotlinx.android.synthetic.main.personal_loan_hub_fragment.*
import styleguide.bars.FragmentPagerItem
import styleguide.utils.extensions.toFormattedAccountNumber

class PersonalLoanHubInformationFragment : FragmentPagerItem() {

    private lateinit var personalLoanVCLViewModel: PersonalLoanVCLViewModel
    private lateinit var genericTransactionHistoryViewModel: GenericTransactionHistoryViewModel
    private lateinit var hostActivity: PersonalLoanHubActivity

    override fun onAttach(context: Context) {
        super.onAttach(context)
        hostActivity = context as PersonalLoanHubActivity
        personalLoanVCLViewModel = hostActivity.viewModel()
        genericTransactionHistoryViewModel = hostActivity.viewModel()
    }

    companion object {

        @JvmStatic
        fun newInstance(description: String): PersonalLoanHubInformationFragment {
            val personalLoanHubInformationFragment = PersonalLoanHubInformationFragment()
            return personalLoanHubInformationFragment.apply {
                arguments = Bundle().apply { putString(TAB_DESCRIPTION_KEY, description) }
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.personal_loan_hub_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getPersonalLoanHubInformation()
    }

    private fun getPersonalLoanHubInformation() {
        personalLoanVCLViewModel.personalLoanHubExtendedResponse.value?.apply {
            personalLoan.let {
                accountNumberLineItemView.setLineItemViewContent(genericTransactionHistoryViewModel.accountDetail?.accountNumber.toFormattedAccountNumber())
                instalmentLineItemView.setLineItemViewContent(TextFormatUtils.formatBasicAmountAsRand(it.instalment))
                interestRateLineItemView.setLineItemViewContent("${it.interestRate}%")
                contractDateLineItemView.setLineItemViewContent(DateUtils.formatDateMonth(it.contractDate))
                termLineItemView.setLineItemViewContent("${it.term} ${getString(R.string.personal_loan_hub_months)}")
                remainingTermLineItemView.setLineItemViewContent("${it.remainingTerm} ${getString(R.string.personal_loan_hub_months)}")
                repaymentDayLineItemView.setLineItemViewContent(it.repaymentDay)

                val availableAdvanceOrArrearsAmountMessage = if (it.arrearsAdvanceAmount >= 0) R.string.personal_loan_hub_available_advance else R.string.personal_loan_hub_arrears_amount
                availableAdvanceOrArrearsAmountLineItemView.setLineItemViewLabel(getString(availableAdvanceOrArrearsAmountMessage))
                availableAdvanceOrArrearsAmountLineItemView.setLineItemViewContent(TextFormatUtils.formatBasicAmountAsRand(it.arrearsAdvanceAmount.toString()))
            }
        }
    }

    override fun getTabDescription(): String {
        return arguments?.getString(TAB_DESCRIPTION_KEY) ?: ""
    }
}