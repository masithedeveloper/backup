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

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import com.barclays.absa.banking.R
import com.barclays.absa.banking.boundary.model.AccountObject
import com.barclays.absa.banking.boundary.model.debitOrder.DebitOrderDataModel
import com.barclays.absa.banking.boundary.model.debitOrder.DebitOrderDetailsResponse
import com.barclays.absa.banking.boundary.model.debitOrder.DebitOrderList
import com.barclays.absa.banking.debiCheck.DebitOrdersAdapter
import com.barclays.absa.banking.debiCheck.SelectedDebitOrderInterface
import com.barclays.absa.banking.debiCheck.ui.DebitOrdersPagerItemFragment.Companion.STOPPED_DEBIT_ORDER_LIST
import com.barclays.absa.banking.transfer.AccountListItem
import com.barclays.absa.utils.AnalyticsUtil
import com.barclays.absa.utils.DateUtils
import com.barclays.absa.utils.FilterAccountList
import com.barclays.absa.utils.TextFormatUtils
import kotlinx.android.synthetic.main.stopped_debit_orders_fragment.*
import styleguide.forms.SelectorList
import java.util.*

class StoppedDebitOrdersFragment : DebitOrderBaseFragment(R.layout.stopped_debit_orders_fragment), SearchView.OnQueryTextListener {

    private lateinit var adapter: DebitOrdersAdapter
    private lateinit var stoppedDebitOrderList: DebitOrderList
    private var debitOrderDataModel = DebitOrderDataModel()
    private var searchView: SearchView? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)
        setToolBar(R.string.stopped_debit_orders)
        showToolBar()

        setAccountSelected()
        setUpObservers()
        debitOrdersActivity.intent.extras?.let { bundle ->
            stoppedDebitOrderList = (bundle.get(STOPPED_DEBIT_ORDER_LIST) as? DebitOrderList)!!
            populateRecyclerView(stoppedDebitOrderList)
        }
    }

    private fun setAccountStoppedDebitOrdersForTheLastFortyDays() {
        val today = Date()
        val fortyDaysAgo = GregorianCalendar().apply {
            time = today
            add(Calendar.DAY_OF_MONTH, -39)
        }.time

        val fromDateFormatted = DateUtils.format(fortyDaysAgo, "yyyy-MM-dd")
        val toDateFormatted = DateUtils.format(today, "yyyy-MM-dd")
        debitOrderDataModel.apply {
            fromDate = fromDateFormatted
            toDate = toDateFormatted
        }
    }

    private fun setAccountSelected() {
        setAccountStoppedDebitOrdersForTheLastFortyDays()
        val accountList = FilterAccountList.getTransactionalAndCreditAccounts(appCacheService.getSecureHomePageObject()?.accounts)
        val debitOrderAccountList = SelectorList<AccountListItem>()
        accountList?.mapTo(debitOrderAccountList) { account ->
            AccountListItem().apply {
                accountNumber = account.accountNumber
                accountType = account.description
                accountBalance = account.availableBalance.toString()
            }
        }

        var selectedAccount: AccountObject
        if (!accountList.isNullOrEmpty()) {
            selectedAccount = accountList.first()
            debitOrderDataModel.fromAccountNumber = selectedAccount.accountNumber
            selectAnAccountNormalInputView.selectedValue = TextFormatUtils.formatAccountNumberAndDescription(selectedAccount.description, selectedAccount.accountNumber)
        }

        selectAnAccountNormalInputView.setList(debitOrderAccountList, getString(R.string.debit_order_select_account_number))
        selectAnAccountNormalInputView.setItemSelectionInterface { index ->
            debitOrdersActivity.showProgressDialog()
            selectedAccount = accountList[index]
            selectAnAccountNormalInputView.selectedValue = TextFormatUtils.formatAccountNumberAndDescription(selectedAccount.description, selectedAccount.accountNumber)
            debitOrderDataModel.fromAccountNumber = selectedAccount.accountNumber
            debitOrderViewModel.fetchStoppedDebitOrderList(debitOrderDataModel)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        menu.clear()
        inflater.inflate(R.menu.search_menu_dark, menu)
        val menuItem: MenuItem? = menu.findItem(R.id.action_search)
        searchView = menuItem?.actionView as SearchView
        searchView?.setOnQueryTextListener(this)

        menuItem.setOnActionExpandListener(object : MenuItem.OnActionExpandListener {
            override fun onMenuItemActionExpand(item: MenuItem): Boolean = true

            override fun onMenuItemActionCollapse(item: MenuItem): Boolean {
                selectAnAccountHeadingView.visibility = View.VISIBLE
                selectAnAccountNormalInputView.visibility = View.VISIBLE
                return true
            }
        })
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.action_search) {
            selectAnAccountHeadingView.visibility = View.GONE
            selectAnAccountNormalInputView.visibility = View.GONE
        }
        return super.onOptionsItemSelected(item)
    }

    private fun populateRecyclerView(stoppedDebitOrderList: DebitOrderList) {
        if (!stoppedDebitOrderList.stopPayments.isNullOrEmpty()) {
            noStoppedDebitOrderTextView.visibility = View.GONE
            stoppedDebitOrdersRecyclerView.layoutManager = LinearLayoutManager(debitOrdersActivity)
            adapter = DebitOrdersAdapter(ArrayList(stoppedDebitOrderList.stopPayments.asReversed()), object : SelectedDebitOrderInterface {
                override fun viewDebitOrderDetails(debitOrder: DebitOrderDetailsResponse) {
                    navigate(StoppedDebitOrdersFragmentDirections.actionStoppedDebitOrdersFragmentToDebitOrderTransactionDetailsFragment(debitOrder, true))
                    AnalyticsUtil.trackAction("Debit Orders", "DebitOrder_StoppedDebitOrderHub_TransactionCardClicked")
                }
            })
            stoppedDebitOrdersRecyclerView.adapter = adapter
            stoppedDebitOrdersRecyclerView.visibility = View.VISIBLE
        } else {
            noStoppedDebitOrderTextView.visibility = View.VISIBLE
            stoppedDebitOrdersRecyclerView.visibility = View.GONE
        }
    }

    private fun setUpObservers() {
        debitOrderViewModel.stoppedDebitOrderList.observe(viewLifecycleOwner, { debitOrderList ->
            debitOrdersActivity.dismissProgressDialog()
            debitOrderList?.let { populateRecyclerView(it) }
        })
    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        query?.let {
            if (::adapter.isInitialized) {
                adapter.searchDebitOrders(it)
            }
        }
        return false
    }

    override fun onQueryTextChange(newText: String?): Boolean {
        newText?.let {
            if (::adapter.isInitialized) {
                adapter.searchDebitOrders(it)
            }
        }
        return false
    }
}