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
package com.barclays.absa.banking.shared.genericTransactionHistory.ui

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.barclays.absa.banking.R
import com.barclays.absa.banking.boundary.model.AccountObject
import com.barclays.absa.banking.boundary.model.Amount
import com.barclays.absa.banking.boundary.model.Transaction
import com.barclays.absa.banking.card.ui.creditCard.hub.CreditCardHubActivity.*
import com.barclays.absa.banking.card.ui.creditCard.hub.FilteringOptions
import com.barclays.absa.banking.card.ui.creditCard.hub.TransactionHistoryFilterFragment
import com.barclays.absa.banking.express.transactionHistory.TransactionHistoryViewModel
import com.barclays.absa.banking.express.transactionHistory.dto.HistoryRequest
import com.barclays.absa.banking.express.transactionHistory.dto.TransactionHistoryResponse
import com.barclays.absa.banking.framework.BaseActivity
import com.barclays.absa.banking.framework.app.BMBApplication
import com.barclays.absa.banking.shared.genericTransactionHistory.ui.GenericTransactionHubActivity.Companion.ACCOUNT_OBJECT
import com.barclays.absa.utils.DateUtils
import com.barclays.absa.utils.viewModel
import kotlinx.android.synthetic.main.generic_transaction_history_fragment.*
import styleguide.bars.FragmentPagerItem
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class GenericTransactionHistoryFragment : FragmentPagerItem(), TransactionHistoryFilterFragment.UpdateFilteringOptions {

    private val formatter = SimpleDateFormat(DateUtils.DASHED_DATE_PATTERN, BMBApplication.getApplicationLocale())
    private var transactions: List<Transaction> = listOf()

    private lateinit var hostActivity: BaseActivity
    private lateinit var toDate: String
    private lateinit var fromDate: String
    private lateinit var accountDetail: AccountObject
    private lateinit var transactionsAdapter: GenericTransactionsAdapter
    private lateinit var filterFragment: TransactionHistoryFilterFragment
    private lateinit var transactionHistoryViewModel: TransactionHistoryViewModel
    private lateinit var genericTransactionHistoryView: GenericTransactionHistoryView

    var initialTransactionHistoryDateRange = -7

    companion object {

        fun newInstance(description: String, accountObject: AccountObject): GenericTransactionHistoryFragment {
            return GenericTransactionHistoryFragment().apply {
                arguments = Bundle().apply {
                    putString(TAB_DESCRIPTION_KEY, description)
                    putSerializable(ACCOUNT_OBJECT, accountObject)
                }
            }
        }
    }

    fun setupView(genericTransactionHistoryView: GenericTransactionHistoryView) {
        this.genericTransactionHistoryView = genericTransactionHistoryView;
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        hostActivity = context as BaseActivity
        transactionHistoryViewModel = hostActivity.viewModel()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.generic_transaction_history_fragment, container, false)
        accountDetail = arguments?.getSerializable(ACCOUNT_OBJECT) as AccountObject
        return view.rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (!transactionHistoryViewModel.isTransactionHistoryInitialized()) {
            setupTransactionHistory()
            requestHistory()
        } else {
            setUpTransactionHistoryModel()
        }

        setUpDateAndSearch()
    }

    private fun setUpDateAndSearch() {
        datePickerFilterAndSearchView.setOnSearchClickListener {
            hideCalenderFilter()
            genericTransactionHistoryView.collapseAppBarView()
        }

        datePickerFilterAndSearchView.setOnCalendarLayoutClickListener {
            showDateSelector()
        }

        if (transactions.isNotEmpty()) {
            adapterSetUp(transactions)
        } else {
            accountRecyclerView.visibility = View.GONE
            noTransactionsTextView.visibility = View.VISIBLE
        }
    }

    private fun showDateSelector() {
        filterFragment = TransactionHistoryFilterFragment.newInstance(this, transactionHistoryViewModel.filteringOptions, "GenericTransactionsHub")
        hostActivity.supportFragmentManager.let { filterFragment.show(it, "filter_dialog_fragment") }
    }

    private fun adapterSetUp(transactionHistory: List<Transaction>) {
        transactionsAdapter = GenericTransactionsAdapter(ArrayList(transactionHistory))
        accountRecyclerView.setHasFixedSize(true)
        accountRecyclerView.adapter = transactionsAdapter
    }

    private fun setNoTransactionsAvailable() {
        accountRecyclerView.visibility = View.GONE
        noTransactionsTextView.visibility = View.VISIBLE
    }

    private fun setTransactions(transactions: List<Transaction>) {
        if (transactions.isNotEmpty()) {
            accountRecyclerView.visibility = View.VISIBLE
            noTransactionsTextView.visibility = View.GONE
        } else {
            setNoTransactionsAvailable()
        }

        transactionsAdapter.refreshItemList(transactions)
    }

    fun showCalenderFilter() {
        datePickerFilterAndSearchView?.visibility = View.VISIBLE
    }

    private fun hideCalenderFilter() {
        datePickerFilterAndSearchView?.visibility = View.GONE
    }

    private fun setupTransactionHistory() {
        val calendar = Calendar.getInstance()
        formatter.timeZone = TimeZone.getTimeZone("GMT+02:00")
        toDate = formatter.format(calendar.time)
        transactionHistoryViewModel.filteringOptions.toDate = toDate
        calendar.add(Calendar.DAY_OF_MONTH, initialTransactionHistoryDateRange)
        fromDate = formatter.format(calendar.time)
        transactionHistoryViewModel.filteringOptions.fromDate = fromDate
    }

    private fun requestHistory() {
        val historyRequest = HistoryRequest()
        historyRequest.fromAccountNumber = accountDetail.accountNumber
        historyRequest.fromDate = fromDate
        historyRequest.toDate = toDate
        historyRequest.accountType = accountDetail.accountType
        transactionHistoryViewModel.fetchTransactionHistory(historyRequest)
        setUpTransactionHistoryModel()
    }

    private fun setUpTransactionHistoryModel() {
        transactionHistoryViewModel.transactionHistoryLiveData.observe(viewLifecycleOwner, { transactionHistoryResponse: TransactionHistoryResponse ->
            val transactions: MutableList<Transaction> = java.util.ArrayList()
            for (accountHistoryLine in transactionHistoryResponse.transactionHistory.accountHistoryLines) {
                Transaction().apply {
                    balance = Amount(accountHistoryLine.balanceAmount)
                    description = accountHistoryLine.transactionDescription
                    referenceNumber = accountHistoryLine.transactionDescription
                    transactionCategory = accountHistoryLine.transactionCategory.toString()
                    transactionDate = accountHistoryLine.transactionDate
                    isUnclearedTransaction = accountHistoryLine.transactionCategory == 1

                    val amount = Amount(accountHistoryLine.transactionAmount)
                    if (amount.amountDouble > 0) {
                        creditAmount = amount
                    } else {
                        creditAmount = Amount("0.00")
                        debitAmount = amount
                    }
                    transactions.add(this)
                }
            }

            updateTransactions(transactions)
            hostActivity.dismissProgressDialog()
        })
        updateDateSearchFields()
    }

    private fun filterMoneyIn(): List<Transaction> {
        val tempTransactions = ArrayList<Transaction>()
        transactions.forEach {
            if (it.creditAmount?.getAmount() != "0.00" && !it.isUnclearedTransaction) {
                tempTransactions.add(it)
            }
        }
        setNoTransactionsVisible(transactions)
        return tempTransactions
    }

    private fun filterMoneyOut(): List<Transaction> {
        val tempTransactions = ArrayList<Transaction>()
        transactions.forEach {
            if (it.debitAmount?.getAmount() != "0.00" && !it.isUnclearedTransaction) {
                tempTransactions.add(it)
            }
        }
        setNoTransactionsVisible(transactions)
        return tempTransactions
    }

    private fun filterMoneyUncleared(): List<Transaction> {
        val tempTransactions = transactions.filter { transaction -> transaction.isUnclearedTransaction }
        setNoTransactionsVisible(transactions)
        return tempTransactions
    }

    private fun setNoTransactionsVisible(transactions: List<Transaction>) {
        if (transactions.isEmpty()) {
            accountRecyclerView.visibility = View.GONE
            noTransactionsTextView.visibility = View.VISIBLE
        } else {
            accountRecyclerView.visibility = View.VISIBLE
            noTransactionsTextView.visibility = View.GONE
        }
    }

    override fun getTabDescription(): String {
        return arguments?.getString(TAB_DESCRIPTION_KEY) ?: ""
    }

    override fun updateFilteringOptions(filteringOptions: FilteringOptions) {
        if (filteringOptions.fromDate != transactionHistoryViewModel.filteringOptions.fromDate || filteringOptions.toDate != transactionHistoryViewModel.filteringOptions.toDate) {
            transactionHistoryViewModel.filteringOptions = filteringOptions
            fromDate = filteringOptions.fromDate
            toDate = filteringOptions.toDate
            requestHistory()
        } else {
            transactionHistoryViewModel.filteringOptions = filteringOptions
            updateTransactions(transactions)
        }
    }

    private fun updateTransactions(transactions: List<Transaction>) {
        this.transactions = transactions
        setNoTransactionsVisible(transactions)
        if (!::transactionsAdapter.isInitialized) {
            adapterSetUp(transactions)
        }
        transactionsAdapter.refreshItemList(transactions)
        updateFilter()
    }

    private fun updateFilter() {
        setTransactions(transactions)
        updateDateSearchFields()
        when (transactionHistoryViewModel.filteringOptions.filterType) {
            ALL_TRANSACTIONS -> transactionsAdapter.refreshItemList(transactions)
            MONEY_IN -> transactionsAdapter.refreshItemList(filterMoneyIn())
            MONEY_OUT -> transactionsAdapter.refreshItemList(filterMoneyOut())
            UNCLEARED -> transactionsAdapter.refreshItemList(filterMoneyUncleared())
        }
    }

    private fun updateDateSearchFields() {
        val searchDate = DateUtils.formatDate(transactionHistoryViewModel.filteringOptions.fromDate, "yyyy-MM-dd", "dd MMM yyyy") + " - " + DateUtils.formatDate(transactionHistoryViewModel.filteringOptions.toDate, "yyyy-MM-dd", "dd MMM yyyy")
        datePickerFilterAndSearchView.setSearchText(searchDate)
    }

    fun searchTransactionHistory(newText: String) {
        if (::transactionsAdapter.isInitialized) {
            transactionsAdapter.filter(newText)
        }
    }
}