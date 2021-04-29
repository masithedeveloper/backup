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
 */
package com.barclays.absa.banking.accountHub

import android.os.Bundle
import android.view.View
import androidx.fragment.app.activityViewModels
import com.barclays.absa.banking.R
import com.barclays.absa.banking.boundary.model.Amount
import com.barclays.absa.banking.boundary.model.Transaction
import com.barclays.absa.banking.card.ui.creditCard.hub.FilteringOptions
import com.barclays.absa.banking.card.ui.creditCard.hub.TransactionHistoryFilterFragment
import com.barclays.absa.banking.databinding.AccountHubTransactionHistoryFragmentBinding
import com.barclays.absa.banking.express.transactionHistory.TransactionHistoryViewModel
import com.barclays.absa.banking.express.transactionHistory.dto.HistoryRequest
import com.barclays.absa.banking.express.transactionHistory.dto.TransactionHistoryResponse
import com.barclays.absa.banking.framework.BaseFragment
import com.barclays.absa.utils.DateTimeHelper
import com.barclays.absa.utils.DateTimeHelper.DASHED_PATTERN_YYYY_MM_DD
import com.barclays.absa.utils.extensions.viewBinding
import java.util.*
import kotlin.collections.ArrayList

class AccountHubTransactionHistoryFragment : BaseFragment(R.layout.account_hub_transaction_history_fragment), TransactionHistoryFilterFragment.UpdateFilteringOptions {
    private val binding by viewBinding(AccountHubTransactionHistoryFragmentBinding::bind)

    private val transactionHistoryViewModel by activityViewModels<TransactionHistoryViewModel>()
    private val accountHubViewModel by activityViewModels<AccountHubViewModel>()

    private lateinit var transactionsAdapter: AccountTransactionHistoryAdapter
    private lateinit var filterFragment: TransactionHistoryFilterFragment

    companion object {
        private const val ALL_TRANSACTIONS = "A"
        private const val MONEY_IN = "I"
        private const val MONEY_OUT = "O"
        private const val UNCLEARED = "U"
        private const val UNCLEARED_TRANSACTION = 1
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        transactionsAdapter = AccountTransactionHistoryAdapter(arrayListOf())
        binding.accountRecyclerView.setHasFixedSize(true)
        binding.accountRecyclerView.adapter = transactionsAdapter

        if (!transactionHistoryViewModel.isTransactionHistoryInitialized()) {
            setupTransactionHistory()
            requestHistory()
        } else {
            setUpTransactionHistoryModel()
        }

        setUpDateAndSearch()
        setUpObservers()
    }

    private fun setUpDateAndSearch() {
        binding.datePickerFilterAndSearchView.setOnSearchClickListener {
            accountHubViewModel.activateSearchView.value = true
        }

        binding.datePickerFilterAndSearchView.setOnCalendarLayoutClickListener {
            showDateSelector()
        }
    }

    private fun setUpObservers() {
        accountHubViewModel.showSearchAndFilterView.observe(viewLifecycleOwner) { showSearchAndFilterView ->
            binding.datePickerFilterAndSearchView.visibility = if (showSearchAndFilterView) View.VISIBLE else View.GONE
        }
        accountHubViewModel.searchKeyword.observe(viewLifecycleOwner) { searchKeyword ->
            if (::transactionsAdapter.isInitialized) {
                transactionsAdapter.search(searchKeyword)
            }
        }
        accountHubViewModel.shouldReloadHub.observe(viewLifecycleOwner, { shouldReloadHub ->
            if (shouldReloadHub) {
                requestHistory()
            }
        })
    }

    private fun showDateSelector() {
        filterFragment = TransactionHistoryFilterFragment.newInstance(this, transactionHistoryViewModel.filteringOptions, "GenericTransactionsHub")
        baseActivity.supportFragmentManager.let { filterFragment.show(it, "filter_dialog_fragment") }
    }

    private fun setupTransactionHistory() {
        val calendar = Calendar.getInstance()
        transactionHistoryViewModel.filteringOptions.toDate = DateTimeHelper.formatDate(calendar.time, DASHED_PATTERN_YYYY_MM_DD)
        calendar.add(Calendar.DAY_OF_MONTH, accountHubViewModel.initialTransactionHistoryDateRange)
        transactionHistoryViewModel.filteringOptions.fromDate = DateTimeHelper.formatDate(calendar.time, DASHED_PATTERN_YYYY_MM_DD)
    }

    private fun requestHistory() {
        val transactionHistoryRequest = HistoryRequest().apply {
            fromAccountNumber = accountHubViewModel.accountObject.accountNumber
            fromDate = transactionHistoryViewModel.filteringOptions.fromDate
            toDate = transactionHistoryViewModel.filteringOptions.toDate
            accountType = accountHubViewModel.accountObject.accountType
        }
        transactionHistoryViewModel.fetchTransactionHistory(transactionHistoryRequest)
        setUpTransactionHistoryModel()
    }

    private fun setUpTransactionHistoryModel() {
        transactionHistoryViewModel.transactionHistoryLiveData.observe(viewLifecycleOwner, { transactionHistoryResponse: TransactionHistoryResponse ->
            val transactions: ArrayList<Transaction> = ArrayList()
            transactionHistoryResponse.transactionHistory.accountHistoryLines.forEach { accountHistoryLine ->
                val transaction = Transaction().apply {
                    balance = Amount(accountHistoryLine.balanceAmount)
                    description = accountHistoryLine.transactionDescription
                    referenceNumber = accountHistoryLine.transactionDescription
                    transactionCategory = accountHistoryLine.transactionCategory.toString()
                    transactionDate = accountHistoryLine.transactionDate
                    isUnclearedTransaction = accountHistoryLine.transactionCategory == UNCLEARED_TRANSACTION

                    val amount = Amount(accountHistoryLine.transactionAmount)
                    if (amount.amountDouble > 0) {
                        creditAmount = amount
                    } else {
                        creditAmount = Amount("0.00")
                        debitAmount = amount
                    }
                }
                transactions.add(transaction)
            }
            accountHubViewModel.allTransactions = transactions

            updateFilteredTransactions(transactionHistoryViewModel.filteringOptions)
            dismissProgressDialog()
        })

        updateDateSearchFields()
    }

    private fun setNoTransactionsViews(transactions: List<Transaction>) {
        if (transactions.isEmpty()) {
            binding.accountRecyclerView.visibility = View.GONE
            binding.emptyStateAnimationView.visibility = View.VISIBLE
        } else {
            binding.accountRecyclerView.visibility = View.VISIBLE
            binding.emptyStateAnimationView.visibility = View.GONE
        }
    }

    private fun updateTransactions(transactions: List<Transaction>) {
        setNoTransactionsViews(transactions)
        transactionsAdapter.updateTransactionList(transactions)
        updateDateSearchFields()
    }

    override fun updateFilteringOptions(filteringOptions: FilteringOptions) {
        if (filteringOptions.fromDate != transactionHistoryViewModel.filteringOptions.fromDate || filteringOptions.toDate != transactionHistoryViewModel.filteringOptions.toDate) {
            transactionHistoryViewModel.filteringOptions = filteringOptions
            requestHistory()
        } else {
            transactionHistoryViewModel.filteringOptions = filteringOptions
            updateTransactions(accountHubViewModel.allTransactions)
        }

        updateFilteredTransactions(filteringOptions)
    }

    private fun updateFilteredTransactions(filteringOptions: FilteringOptions) {
        with(accountHubViewModel) {
            when (filteringOptions.filterType) {
                ALL_TRANSACTIONS -> updateTransactions(allTransactions)
                MONEY_IN -> updateTransactions(filterMoneyIn())
                MONEY_OUT -> updateTransactions(filterMoneyOut())
                UNCLEARED -> updateTransactions(filterMoneyUncleared())
            }
        }
    }

    private fun updateDateSearchFields() {
        binding.datePickerFilterAndSearchView.setSearchText("${DateTimeHelper.formatDate(transactionHistoryViewModel.filteringOptions.fromDate)} - ${DateTimeHelper.formatDate(transactionHistoryViewModel.filteringOptions.toDate)}")
    }
}