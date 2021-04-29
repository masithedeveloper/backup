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

package com.barclays.absa.banking.expressCashSend.ui

import android.app.SearchManager
import android.content.Context
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.barclays.absa.banking.R
import com.barclays.absa.banking.boundary.model.AccountObject
import com.barclays.absa.banking.cashSend.ui.AccountObjectWrapper
import com.barclays.absa.banking.databinding.CashSendUnredeemedTransactionsFragmentBinding
import com.barclays.absa.banking.express.cashSend.unredeemedCashSendTransactions.CashSendUnredeemedTransactionsViewModel
import com.barclays.absa.banking.express.cashSend.unredeemedCashSendTransactions.dto.CashSendPaymentTransaction
import com.barclays.absa.banking.framework.BaseActivity
import com.barclays.absa.banking.framework.BaseFragment
import com.barclays.absa.banking.framework.analytics.AnalyticsUtils
import com.barclays.absa.banking.framework.app.BMBConstants
import com.barclays.absa.banking.framework.app.BMBConstants.*
import com.barclays.absa.banking.shared.BaseAlertDialog.showGenericErrorDialog
import com.barclays.absa.utils.AbsaCacheManager
import com.barclays.absa.utils.AccessibilityUtils
import com.barclays.absa.utils.extensions.viewBinding
import styleguide.forms.SelectorList
import java.util.*

class CashSendUnredeemedTransactionsFragment : BaseFragment(R.layout.cash_send_unredeemed_transactions_fragment), SearchView.OnQueryTextListener, UnredeemedTransactionsInterface {

    private val binding by viewBinding(CashSendUnredeemedTransactionsFragmentBinding::bind)

    lateinit var accounts: SelectorList<AccountObjectWrapper>
    private lateinit var unredeemedRecyclerViewAdapter: UnredeemedRecyclerViewAdapter
    private lateinit var selectAccountObject: AccountObject
    private val cashSendUnredeemedTransactionsViewModel by activityViewModels<CashSendUnredeemedTransactionsViewModel>()
    private val cashSendViewModel by activityViewModels<CashSendViewModel>()
    private var lastSelectedIndex = 0

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setToolBar(R.string.title_unredeemed_cash_send)
        BaseActivity.mScreenName = BMBConstants.UNREDEEMED_CASHSEND_TRANSACTIONS_CONST
        BaseActivity.mSiteSection = BMBConstants.CASHSEND_CONST
        AnalyticsUtils.getInstance().trackCustomScreenView(BMBConstants.UNREDEEMED_CASHSEND_TRANSACTIONS_CONST, BMBConstants.CASHSEND_CONST, BMBConstants.TRUE_CONST)
        setupTalkBack()
        setObservers()
    }

    private fun setupTalkBack() {
        if (AccessibilityUtils.isExploreByTouchEnabled()) {
            binding.unredeemedAccountSelectionView.contentDescription = getString(R.string.talkback_unredeemed_cashsends_account_info, binding.unredeemedAccountSelectionView.editText?.text.toString())
        }
    }

    private fun setObservers() {
        with(cashSendUnredeemedTransactionsViewModel) {
            populateAccounts()
            if(accounts.isNotEmpty()) {
                selectAccountObject = accounts[0].accountObject
                populateHeaderData(selectAccountObject)
                cashSendUnredeemedTransactionsViewModel.fetchUnredeemedTransactions(selectAccountObject.accountNumber)
                cashSendUnredeemedTransactionsResponse.observe(viewLifecycleOwner) { response ->
                    if (response.unredeemedCashSendTransactions.isNotEmpty() || response.unredeemedCashSendTransactions.size == 1) {
                            if (response.unredeemedCashSendTransactions.first().beneficiaryNumber == 0 && response.unredeemedCashSendTransactions.first().paymentNumber == 0) {
                                binding.unredeemedCashSendsRecyclerView.visibility = View.GONE
                                binding.unredeemedNoResultsTextView.visibility = View.VISIBLE
                                baseActivity.animate(binding.unredeemedNoResultsTextView, R.anim.bounce_long)
                            } else {
                                binding.unredeemedCashSendsRecyclerView.visibility = View.VISIBLE
                                binding.unredeemedNoResultsTextView.visibility = View.GONE
                                binding.unredeemedCashSendsRecyclerView.apply {
                                    unredeemedRecyclerViewAdapter = UnredeemedRecyclerViewAdapter(this@CashSendUnredeemedTransactionsFragment, response.unredeemedCashSendTransactions)
                                    layoutManager = LinearLayoutManager(context)
                                    adapter = unredeemedRecyclerViewAdapter
                            }
                        }
                    }
                    dismissProgressDialog()
                }
            }
        }

        cashSendUnredeemedTransactionsViewModel.failureLiveData.observe(viewLifecycleOwner) {
            dismissProgressDialog()
            binding.unredeemedNoResultsTextView.visibility = View.VISIBLE
        }
    }

    private fun populateHeaderData(accountObject: AccountObject) {
        binding.unredeemedAccountSelectionView.selectedValue = accountObject.accountInformation
    }

    private fun populateAccounts() {
        if (AbsaCacheManager.getTransactionalAccounts().isNotEmpty()) {
            accounts = SelectorList<AccountObjectWrapper>().apply { addAll(AbsaCacheManager.getTransactionalAccounts().map { AccountObjectWrapper(it) }) }
            binding.unredeemedAccountSelectionView.setList(accounts, getString(R.string.select_account_toolbar_title))
            binding.unredeemedAccountSelectionView.setItemSelectionInterface { index: Int ->
                lastSelectedIndex = index
                val accountObject = accounts[index].accountObject
                binding.unredeemedCashSendsRecyclerView.postDelayed({
                    selectAccountObject = accountObject
                    populateHeaderData(selectAccountObject)
                    cashSendUnredeemedTransactionsViewModel.fetchUnredeemedTransactions(accountObject.accountNumber)
                }, 100)
            }
        } else {
            showGenericErrorDialog()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, menuInflater: MenuInflater) {
        menuInflater.inflate(R.menu.search_menu, menu)
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        if (cashSendUnredeemedTransactionsViewModel.cashSendUnredeemedTransactionsResponse.value?.unredeemedCashSendTransactions?.isNullOrEmpty() == true && AbsaCacheManager.getTransactionalAccounts().isEmpty()) {
            return super.onPrepareOptionsMenu(menu)
        }

        val menuInflater: MenuInflater? = activity?.menuInflater
        menuInflater?.inflate(R.menu.search_menu, menu)
        val searchItem = menu.findItem(R.id.action_search)

        val searchManager = activity?.getSystemService(Context.SEARCH_SERVICE) as SearchManager?

        var searchView: SearchView? = null
        if (searchItem != null) {
            searchView = searchItem.actionView as SearchView
            searchView.isSubmitButtonEnabled = true
            setupSearchView(searchView)
        }
        if (searchView != null && searchManager != null) {
            searchView.setSearchableInfo(searchManager.getSearchableInfo(activity?.componentName))
        }
        return super.onPrepareOptionsMenu(menu)
    }

    private fun setupSearchView(searchView: SearchView) {
        searchView.setIconifiedByDefault(true)
        searchView.setOnQueryTextListener(this)
        searchView.isSubmitButtonEnabled = false
        searchView.isQueryRefinementEnabled = false
    }

    private fun startSearch(query: String) {
        // TODO: isBlank should work but not exactly the same
        if (query.isBlank()) {
            unredeemedRecyclerViewAdapter.filterText("")
        } else {
            unredeemedRecyclerViewAdapter.filterText(query.trim { it <= ' ' })
        }
    }

    override fun onQueryTextSubmit(query: String): Boolean {
        startSearch(query)
        return false
    }

    override fun onQueryTextChange(query: String): Boolean {
        startSearch(query)
        return false
    }

    override fun onItemClick(transactionItem: CashSendPaymentTransaction) {
        Bundle().apply {
            putParcelable(UNREDEEMED_TRANSACTION, transactionItem)
            putString(ACCOUNT_NUMBER_TO_DISPLAY, selectAccountObject.description)
            cashSendViewModel.isCashSendPlus.value?.let { putBoolean(CashSendActivity.IS_CASH_SEND_PLUS, it) }
            val unredeemTransactionList = cashSendUnredeemedTransactionsViewModel.cashSendUnredeemedTransactionsResponse.value?.unredeemedCashSendTransactions
            putString(ACCOUNT_NUMBER, unredeemTransactionList?.first()?.sourceAccount)
            putString(MY_REFERENCE, "")
            findNavController().navigate(R.id.action_cashSendFragment_to_caseSendUnredeemedDetailsFragment, this)
        }
    }

    override fun showNoResultsView() {
        binding.unredeemedNoResultsTextView.visibility = View.VISIBLE
        binding.unredeemedCashSendsRecyclerView.visibility = View.GONE
    }

    override fun hideNoResultsView() {
        binding.unredeemedNoResultsTextView.visibility = View.GONE
        binding.unredeemedCashSendsRecyclerView.visibility = View.VISIBLE
    }
}