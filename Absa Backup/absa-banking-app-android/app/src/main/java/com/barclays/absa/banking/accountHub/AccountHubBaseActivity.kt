/*
 *  Copyright (c) 2021 Absa Bank Limited, All Rights Reserved.
 *
 *  This code is confidential to Absa Bank Limited and shall not be disclosed
 *  outside the Bank without the prior written permission of the Absa Legal
 *
 *  In the event that such disclosure is permitted the code shall not be copied
 *  or distributed other than on a need-to-know basis and any recipients may be
 *  required to sign a confidentiality undertaking in favor of Absa Bank
 *  Limited
 *
 */
package com.barclays.absa.banking.accountHub

import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.widget.SearchView
import androidx.core.view.isVisible
import com.barclays.absa.banking.boundary.model.AccountObject
import com.barclays.absa.banking.framework.BaseActivity
import com.barclays.absa.utils.KeyboardUtils

const val ACCOUNT_OBJECT = "ACCOUNT_OBJECT"

abstract class AccountHubBaseActivity : BaseActivity() {
    private val accountHubViewModel by viewModels<AccountHubViewModel>()

    abstract val searchView: SearchView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        accountHubViewModel.accountObject = intent?.getSerializableExtra(ACCOUNT_OBJECT) as AccountObject
        setUpObservers()
    }

    private fun setUpObservers() {
        accountHubViewModel.activateSearchView.observe(this) { activateSearchView ->
            if (activateSearchView) {
                showSearchView()
            } else {
                hideSearchView()
            }
        }
    }

    private fun showSearchView() {
        with(searchView) {
            if (!isVisible) {
                visibility = View.VISIBLE
                requestFocus()
                KeyboardUtils.showSoftKeyboard()
            }
        }
        accountHubViewModel.isSearchActive = true
        accountHubViewModel.showSearchAndFilterView.value = false

        attachSearchViewCallbacks()
    }

    private fun hideSearchView() {
        with(searchView) {
            if (isVisible) {
                setQuery("", true)
                visibility = View.GONE
                KeyboardUtils.hideKeyboard(this@AccountHubBaseActivity)
            }
        }
        accountHubViewModel.isSearchActive = false
        accountHubViewModel.showSearchAndFilterView.value = true
    }

    private fun attachSearchViewCallbacks() {
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean = false

            override fun onQueryTextChange(newText: String?): Boolean {
                accountHubViewModel.searchKeyword.value = newText
                return true
            }
        })
        searchView.setOnCloseListener {
            hideSearchView()
            false
        }
    }

    override fun onBackPressed() {
        if (accountHubViewModel.isSearchActive) {
            hideSearchView()
        } else {
            super.onBackPressed()
        }
    }
}