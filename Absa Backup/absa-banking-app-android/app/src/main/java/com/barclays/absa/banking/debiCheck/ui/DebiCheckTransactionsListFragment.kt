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
package com.barclays.absa.banking.debiCheck.ui

import android.content.Context
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.appcompat.widget.SearchView
import androidx.navigation.fragment.findNavController
import com.barclays.absa.banking.R
import com.barclays.absa.banking.account.ui.AccountObjectWrapper
import com.barclays.absa.banking.boundary.model.AccountObject
import com.barclays.absa.banking.debiCheck.services.dto.DebiCheckTransaction
import com.barclays.absa.banking.framework.BaseFragment
import com.barclays.absa.utils.AbsaCacheManager
import com.barclays.absa.utils.viewModel
import kotlinx.android.synthetic.main.debi_check_transactions_list_fragment.*
import styleguide.forms.SelectorList

class DebiCheckTransactionsListFragment : BaseFragment(R.layout.debi_check_transactions_list_fragment) {

    private var accountList: ArrayList<AccountObject> = arrayListOf()

    private lateinit var viewModel: DebiCheckViewModel
    private lateinit var adapter: DebiCheckTransactionsAdapter
    private lateinit var searchItem: MenuItem
    private lateinit var hostActivity: DebiCheckHostActivity

    override fun onAttach(context: Context) {
        super.onAttach(context)
        hostActivity = context as DebiCheckHostActivity
        viewModel = hostActivity.viewModel()
        if (viewModel.selectedTransaction == null) {
            accountList = AbsaCacheManager.getTransactionalAccounts()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        hostActivity.tagDebiCheckEvent("TransactionsListScreen_Displayed")
        hostActivity.setToolBarBack(R.string.debicheck_transactions_title)
        attachDataObservers()
        initAdapter()
        setHasOptionsMenu(true)

        if (accountList.isNotEmpty()) {
            val selectorAccountList = SelectorList<AccountObjectWrapper>().apply { addAll(accountList.map { AccountObjectWrapper(it) }) }

            with(accountSelectorNormalInputView) {
                setList(selectorAccountList, getString(R.string.select_account_toolbar_title))
                setItemSelectionInterface {
                    viewModel.fetchDebiCheckTransactions(accountList[it].accountNumber)
                }
            }

            if (accountList.size == 1) {
                accountSelectorNormalInputView.selectedIndex = 0
                viewModel.fetchDebiCheckTransactions(accountList.first().accountNumber)
            }
        } else {
            accountSelectorNormalInputView.visibility = View.GONE
            emptyStateAnimationView.visibility = View.VISIBLE
            noTransactionsTextView.visibility = View.VISIBLE
        }
    }

    private fun initAdapter() {
        adapter = DebiCheckTransactionsAdapter(object : DebiCheckTransactionsInterface {
            override fun onTransactionSelected(debiCheckTransaction: DebiCheckTransaction) {
                viewModel.selectedTransaction = debiCheckTransaction
                hostActivity.tagDebiCheckEvent("TransactionsListScreen_TransactionItemClicked")
                findNavController().navigate(R.id.action_debiCheckTransactionListFragment_to_debiCheckTransactionDetailFragment)
            }

            override fun hasFilterResult(hasFilterResult: Boolean) {
                if (hasFilterResult) {
                    transactionsRecyclerView.visibility = View.VISIBLE
                    emptyFilterTextView.visibility = View.GONE
                    emptyStateAnimationView.visibility = View.GONE
                } else {
                    transactionsRecyclerView.visibility = View.GONE
                    emptyFilterTextView.visibility = View.VISIBLE
                    emptyStateAnimationView.visibility = View.VISIBLE
                }
                noTransactionsTextView.visibility = View.GONE
            }
        })

        transactionsRecyclerView.adapter = adapter
    }

    private fun attachDataObservers() {
        viewModel.debiCheckTransactionsResponse.observe(viewLifecycleOwner, { transactionsResponse ->
            recentTransactionsTitleTextView.visibility = View.VISIBLE
            val transactionList = arrayListOf<DebiCheckTransaction>()
            if (transactionsResponse.transactions.isNotEmpty()) {
                searchItem.isVisible = true
                transactionsResponse.transactions.forEach { transaction ->
                    transactionList.add(transaction)
                }
            }
            adapter.setItems(transactionList.sortedByDescending { transactionItem -> transactionItem.lastPaymentDate })
            apply {
                transactionsRecyclerView.visibility = if (transactionList.isNotEmpty()) View.VISIBLE else View.GONE
                noTransactionsTextView.visibility = if (transactionList.isNotEmpty()) View.GONE else View.VISIBLE
                emptyStateAnimationView.visibility = if (transactionList.isNotEmpty()) View.GONE else View.VISIBLE
            }
            dismissProgressDialog()
        })
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.search_menu_dark, menu)
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        searchItem = menu.findItem(R.id.action_search)
        if (adapter.itemCount == 0) {
            searchItem.isVisible = false
        }

        (searchItem.actionView as SearchView).apply {
            queryHint = getString(R.string.search)
            setOnQueryTextListener(object : SearchView.OnQueryTextListener {

                override fun onQueryTextChange(newText: String): Boolean {
                    adapter.filterItems(newText)
                    return false
                }

                override fun onQueryTextSubmit(query: String): Boolean {
                    adapter.filterItems(query)
                    return false
                }
            })
        }
        super.onPrepareOptionsMenu(menu)
    }
}