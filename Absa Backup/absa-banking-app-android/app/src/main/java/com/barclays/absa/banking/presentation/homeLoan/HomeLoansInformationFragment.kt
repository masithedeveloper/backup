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
 */

package com.barclays.absa.banking.presentation.homeLoan

import android.content.Context
import android.os.Bundle
import android.view.View
import com.barclays.absa.banking.R
import com.barclays.absa.banking.shared.ItemPagerFragment
import com.barclays.absa.banking.shared.genericTransactionHistory.ui.GenericTransactionHistoryViewModel
import com.barclays.absa.utils.DateUtils
import com.barclays.absa.utils.viewModel
import kotlinx.android.synthetic.main.home_loans_information_fragment.*
import styleguide.utils.extensions.formatAmountAsRand

class HomeLoansInformationFragment : ItemPagerFragment(R.layout.home_loans_information_fragment) {
    private lateinit var homeLoanViewModel: HomeLoanViewModel
    private lateinit var homeLoanPerilsHubActivity: HomeLoanPerilsHubActivity
    private lateinit var genericTransactionHistoryViewModel: GenericTransactionHistoryViewModel

    companion object {
        @JvmStatic
        fun newInstance(description: String): HomeLoansInformationFragment =
                HomeLoansInformationFragment().apply {
                    arguments = Bundle().apply {
                        putString(TAB_DESCRIPTION_KEY, description)
                    }
                }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        homeLoanPerilsHubActivity = (context as HomeLoanPerilsHubActivity)
        homeLoanViewModel = homeLoanPerilsHubActivity.viewModel()
        genericTransactionHistoryViewModel = homeLoanPerilsHubActivity.statementViewModel()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        homeLoanViewModel.homeLoanDetails.observe(viewLifecycleOwner, {
            interestRateLineItemView.setLineItemViewContent("${it.interestRate}%")

            if (it.term.isNotBlank()) {
                loanTermLineItemView.setLineItemViewContent(resources.getQuantityString(R.plurals.numberOfMonths, it.term.toInt(), it.term.toInt()))
            }
            if (it.remainingTerm.isNotBlank()) {
                remainingTermLineItemView.setLineItemViewContent(resources.getQuantityString(R.plurals.numberOfMonths, it.remainingTerm.toInt(), it.remainingTerm.toInt()))
            }

            contractStartDateLineItemView.setLineItemViewContent(DateUtils.formatDateMonth(it.contractDate))
            monthlyInstallmentLineItemView.setLineItemViewContent(it.instalment.formatAmountAsRand())
            nextInstallmentDateLineItemView.setLineItemViewContent(DateUtils.formatDateMonth(it.paymentDate))
            amountInArrearsLineItemView.setLineItemViewContent(it.arrearsAmount.formatAmountAsRand())
            advanceAmountLineItemView.setLineItemViewContent(it.advanceAmount.formatAmountAsRand())
            paymentDueLineItemView.setLineItemViewContent(it.paymentDue.formatAmountAsRand())
            homeLoanPerilsHubActivity.dismissProgressDialog()
        })
    }

    override fun getTabDescription(): String {
        return arguments?.getString(TAB_DESCRIPTION_KEY).toString()
    }
}