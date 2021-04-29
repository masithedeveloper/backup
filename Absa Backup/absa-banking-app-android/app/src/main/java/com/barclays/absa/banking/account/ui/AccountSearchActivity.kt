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
package com.barclays.absa.banking.account.ui

import android.app.SearchManager
import android.content.Context
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.widget.SearchView
import com.barclays.absa.banking.R
import com.barclays.absa.banking.boundary.model.Transaction
import com.barclays.absa.banking.framework.BaseActivity
import com.barclays.absa.banking.presentation.shared.IntentFactory
import kotlinx.android.synthetic.main.activity_account_search.*
import java.util.*

class AccountSearchActivity : BaseActivity(R.layout.activity_account_search), TransactionsView {

    private var transactionAdapter: TransactionsAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        @Suppress("UNCHECKED_CAST")
        (intent.getSerializableExtra(IntentFactory.TRANSACTION_ARRAY_LIST) as? ArrayList<Transaction>)?.let {
            transactionAdapter = TransactionsAdapter(this, it)
            recyclerView.adapter = transactionAdapter
        }
        setToolBarBack("")
    }

    override fun setNoTransactions(hasNoTransactions: Boolean) {
        noTransactionsTextView.visibility = if (hasNoTransactions) View.VISIBLE else View.GONE
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.search_menu, menu)
        setSearchAction(menu)
        return super.onCreateOptionsMenu(menu)
    }

    private fun setSearchAction(menu: Menu) {
        val menuItemSearch = menu.findItem(R.id.action_search).apply {
            isVisible = false
            expandActionView()
        }
        val searchManager = getSystemService(Context.SEARCH_SERVICE) as? SearchManager ?: return
        val searchView = menuItemSearch.actionView as? SearchView ?: return

        searchView.apply {
            setSearchableInfo(searchManager.getSearchableInfo(this@AccountSearchActivity.componentName))
            setIconifiedByDefault(true)
            isSubmitButtonEnabled = false
            isQueryRefinementEnabled = false
            setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(s: String): Boolean {
                    transactionAdapter?.search(s)
                    return false
                }

                override fun onQueryTextChange(s: String): Boolean {
                    transactionAdapter?.search(s)
                    return false
                }
            })
        }

        menuItemSearch.setOnActionExpandListener(object : MenuItem.OnActionExpandListener {
            override fun onMenuItemActionExpand(menuItem: MenuItem): Boolean = true
            override fun onMenuItemActionCollapse(menuItem: MenuItem): Boolean {
                finish()
                return true
            }
        })
    }
}