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

import android.os.Bundle
import android.util.SparseArray
import com.barclays.absa.banking.R
import com.barclays.absa.banking.boundary.model.AccountObject
import com.barclays.absa.banking.personalLoan.ui.PersonalLoanHubInformationFragment.Companion.newInstance
import com.barclays.absa.banking.presentation.shared.IntentFactory.PERSONAL_LOAN_OBJECT
import com.barclays.absa.banking.shared.GenericStatementFragment
import com.barclays.absa.banking.shared.genericTransactionHistory.ui.GenericStatementView
import com.barclays.absa.banking.shared.genericTransactionHistory.ui.GenericTransactionHistoryViewModel
import com.barclays.absa.banking.shared.genericTransactionHistory.ui.GenericTransactionHubActivity
import com.barclays.absa.utils.viewModel
import kotlinx.android.synthetic.main.generic_transaction_hub_activity.*
import styleguide.bars.FragmentPagerItem

open class PersonalLoanHubActivity : GenericTransactionHubActivity(), GenericStatementView {

    private lateinit var personalLoanVCLViewModel: PersonalLoanVCLViewModel
    private lateinit var genericTransactionHistoryViewModel: GenericTransactionHistoryViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setToolBarBack(getString(R.string.personal_loan_title))

        showProgressDialog()
        personalLoanVCLViewModel = viewModel()
        setTransactionHistoryInitialDateRange(-30)
        genericTransactionHistoryViewModel = viewModel()
        genericTransactionHistoryViewModel.accountDetail = intent?.extras?.get(ACCOUNT_OBJECT) as AccountObject
        personalLoanVCLViewModel.personalLoanHubExtendedResponse.value = intent?.extras?.get(PERSONAL_LOAN_OBJECT) as PersonalLoanHubInformation
        collapsingAppbarView.addHeaderView(PersonalLoanHeaderFragment())
        setUpObserverForHubData()
    }

    private fun setUpObserverForHubData() {
        val sparseArray: SparseArray<FragmentPagerItem> = SparseArray()
        sparseArray.apply {
            append(0, transactionHistoryFragment)
            append(1, newInstance(getString(R.string.personal_loan_hub_info_tab)))
            append(2, GenericStatementFragment.newInstance(getString(R.string.personal_loan_hub_statements), "PersonalLoan"))
        }
        collapsingAppbarView.setUpTabs(this, sparseArray)
    }

    override fun statementViewModel(): GenericTransactionHistoryViewModel {
        if (!::genericTransactionHistoryViewModel.isInitialized) {
            genericTransactionHistoryViewModel = viewModel()
        }
        return genericTransactionHistoryViewModel
    }
}