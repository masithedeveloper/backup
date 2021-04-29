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

import android.os.Bundle
import android.view.View
import androidx.appcompat.widget.SearchView
import com.barclays.absa.banking.R
import com.barclays.absa.banking.boundary.model.AccountObject
import com.barclays.absa.banking.boundary.model.Transaction
import com.barclays.absa.banking.card.ui.creditCard.hub.FilteringOptions
import com.barclays.absa.banking.card.ui.creditCard.hub.TransactionHistoryFilterFragment
import com.barclays.absa.banking.framework.BaseActivity
import com.barclays.absa.banking.framework.app.BMBApplication
import com.barclays.absa.banking.framework.dagger.getServiceInterface
import com.barclays.absa.banking.home.ui.IHomeCacheService
import com.barclays.absa.banking.presentation.shared.IntentFactory
import styleguide.bars.CollapsingAppBarView

open class GenericTransactionHubActivity(layoutId: Int = R.layout.generic_transaction_hub_activity) : BaseActivity(layoutId), GenericTransactionHistoryView, TransactionHistoryFilterFragment.UpdateFilteringOptions {
    private lateinit var transactionHistory: List<Transaction>
    private val homeCacheService: IHomeCacheService = getServiceInterface()
    lateinit var transactionHistoryFragment: GenericTransactionHistoryFragment
    lateinit var collapsingAppbarView: CollapsingAppBarView
    open var featureName = ""

    companion object {
        const val ACCOUNT_OBJECT = "ACCOUNT_OBJECT"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        collapsingAppbarView = findViewById(R.id.collapsingAppbarView)
        attachSearchCallBack()
        collapsingAppbarView.setOnPageSelectionListener { description, _ ->
            if (description.equals(getString(R.string.generic_transactions), true)) {
                attachSearchCallBack()
            } else {
                detachSearchViewCallbacks()
            }
        }
        var account = intent?.extras?.get(IntentFactory.ACCOUNT_OBJECT) as AccountObject?
        if (account == null) {
            account = homeCacheService.getHomeLoanPerilsAccount()
        }
        if (account == null) {
            BMBApplication.getInstance().topMostActivity.finish()
            showGenericErrorMessageThenFinish()
            return
        }
        transactionHistoryFragment = GenericTransactionHistoryFragment.newInstance(getString(R.string.generic_transactions), account)
        transactionHistoryFragment.setupView(this)
    }

    private fun attachSearchCallBack() {
        collapsingAppbarView.setOnViewPropertiesChangeListener { state ->
            if (state == CollapsingAppBarView.State.EXPANDED) {
                if (::transactionHistoryFragment.isInitialized) {
                    transactionHistoryFragment.showCalenderFilter()
                }
                collapsingAppbarView.hideSearchView()
            }
        }

        collapsingAppbarView.setOnSearchQueryListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {
                transactionHistoryFragment.searchTransactionHistory(newText)
                return true
            }
        })
    }

    private fun detachSearchViewCallbacks() {
        if (::transactionHistoryFragment.isInitialized) {
            transactionHistoryFragment.showCalenderFilter()
        }
        collapsingAppbarView.hideSearchView()
        collapsingAppbarView.setOnViewPropertiesChangeListener(null)
        collapsingAppbarView.setOnSearchQueryListener(null)
    }

    override fun collapseAppBarView() {
        collapsingAppbarView.showSearchView()
        collapsingAppbarView.collapseAppBar()
    }

    override fun collapseAppBar() {
        collapsingAppbarView.collapseAppBar()
    }

    override fun getTransactions(): List<Transaction> = transactionHistory

    override fun updateFilteringOptions(filteringOptions: FilteringOptions) {
        transactionHistoryFragment.updateFilteringOptions(filteringOptions)
    }

    override fun onBackPressed() {
        navigateToPreviousScreen()
    }

    fun navigateToPreviousScreen() {
        if (collapsingAppbarView.searchView.visibility == View.VISIBLE && !collapsingAppbarView.searchView.isIconified) {
            collapsingAppbarView.searchView.isIconified = true
            collapsingAppbarView.expandAppBar()
            collapsingAppbarView.hideSearchView()
            transactionHistoryFragment.showCalenderFilter()
        } else {
            finish()
        }
    }

    fun setTransactionHistoryInitialDateRange(dateRange: Int) {
        transactionHistoryFragment.initialTransactionHistoryDateRange = dateRange
    }
}