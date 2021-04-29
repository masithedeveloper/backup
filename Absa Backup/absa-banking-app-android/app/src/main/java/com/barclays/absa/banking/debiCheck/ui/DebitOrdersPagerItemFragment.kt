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

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Parcelable
import android.view.*
import androidx.appcompat.widget.SearchView
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.barclays.absa.banking.R
import com.barclays.absa.banking.boundary.model.AccountObject
import com.barclays.absa.banking.boundary.model.debitOrder.DebitOrderDetailsResponse
import com.barclays.absa.banking.boundary.model.debitOrder.DebitOrderList
import com.barclays.absa.banking.databinding.DebitOrdersPagerItemFragmentBinding
import com.barclays.absa.banking.debiCheck.DebitOrdersAdapter
import com.barclays.absa.banking.debiCheck.SelectedDebitOrderInterface
import com.barclays.absa.banking.framework.BaseActivity
import com.barclays.absa.banking.framework.dagger.getServiceInterface
import com.barclays.absa.banking.framework.services.contracts.IAppCacheService
import com.barclays.absa.banking.shared.ItemPagerFragment
import com.barclays.absa.banking.transfer.AccountListItem
import com.barclays.absa.utils.*
import com.google.android.material.appbar.AppBarLayout
import styleguide.forms.SelectorList

class DebitOrdersPagerItemFragment : ItemPagerFragment(), SearchView.OnQueryTextListener {

    private lateinit var binding: DebitOrdersPagerItemFragmentBinding
    private lateinit var debitOrderViewModel: DebitOrderViewModel
    private lateinit var selectedAccount: AccountObject
    private lateinit var linearLayoutManager: LinearLayoutManager
    private lateinit var adapter: DebitOrdersAdapter
    private val debitOrderAccountList = SelectorList<AccountListItem>()
    private var searchView: SearchView? = null
    private var shouldUpdateDebitOrders = false
    private val appCacheService: IAppCacheService = getServiceInterface()

    companion object {
        const val STOPPED_DEBIT_ORDER_LIST = "StoppedDebitOrderList"
        const val IS_FROM_DEBIT_ORDER_HUB = "isFromDebitOrderHub"
        const val DEBIT_ORDER = "debitOrder"
        const val DEBIT_ORDER_DATA_MODEL = "debitOrderDataModel"
        const val DISPUTE_DEBIT_ORDER = 101

        @JvmStatic
        fun newInstance(description: String): DebitOrdersPagerItemFragment {
            return DebitOrdersPagerItemFragment().apply {
                arguments = Bundle().apply { putString(TAB_DESCRIPTION_KEY, description) }
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.debit_orders_pager_item_fragment, container, false)
        return binding.root
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        val baseActivity = context as DebiCheckActivity
        debitOrderViewModel = baseActivity.viewModel()
    }

    override fun onResume() {
        super.onResume()
        if (shouldUpdateDebitOrders) {
            debitOrderViewModel.fetchDebitOrderTransactionList()
            shouldUpdateDebitOrders = false
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)
        setAccountSelected()
        setUpObservers()

        binding.stoppedDebitOrderOptionActionButton.setOnClickListener {
            debitOrderViewModel.fetchStoppedDebitOrderList(debitOrderViewModel.debitOrderDataModel)
        }
    }

    private fun populateRecyclerview(debitOrderList: DebitOrderList) {
        activity?.let {
            binding.apply {
                if (!debitOrderList.debitOrders.isNullOrEmpty()) {
                    noDebitOrderTextView.visibility = View.GONE
                    linearLayoutManager = LinearLayoutManager(activity)
                    binding.debitOrderTransactionsRecyclerView.layoutManager = linearLayoutManager
                    adapter = DebitOrdersAdapter(ArrayList(debitOrderList.debitOrders.asReversed()), object : SelectedDebitOrderInterface {
                        override fun viewDebitOrderDetails(debitOrder: DebitOrderDetailsResponse) {
                            AnalyticsUtil.trackAction("Debit Orders", "DebitOrder_DebitOrderHubScreen_TransactionCardClicked")
                            val intent = Intent(activity, DebitOrdersActivity::class.java).apply {
                                putExtra(DEBIT_ORDER, debitOrder as Parcelable)
                                putExtra(DEBIT_ORDER_DATA_MODEL, debitOrderViewModel.debitOrderDataModel as Parcelable)
                                putExtra(IS_FROM_DEBIT_ORDER_HUB, true)
                            }
                            startActivityForResult(intent, DISPUTE_DEBIT_ORDER)
                        }
                    })
                    adapter.notifyDataSetChanged()
                    debitOrderTransactionsRecyclerView.adapter = adapter
                    debitOrderTransactionsRecyclerView.visibility = View.VISIBLE
                    setAppBarScrolling(true, AppBarLayout.LayoutParams.SCROLL_FLAG_SCROLL)
                } else {
                    noDebitOrderTextView.visibility = View.VISIBLE
                    debitOrderTransactionsRecyclerView.visibility = View.GONE
                    setAppBarScrolling(false, 0)
                }
            }
        }
    }

    private fun setAppBarScrolling(scrollingEnabled: Boolean, scrollFlag: Int) {
        val params = binding.collapsingToolbarLayout.layoutParams as AppBarLayout.LayoutParams
        params.scrollFlags = scrollFlag
        binding.collapsingToolbarLayout.layoutParams = params
        binding.apply {
            debitOrderNestedScrollView.isNestedScrollingEnabled = scrollingEnabled
            debitOrderNestedScrollView.isActivated = scrollingEnabled
            if (CompatibilityUtils.isVersionGreaterThanOrEqualTo(Build.VERSION_CODES.LOLLIPOP)) {
                appBarLayout.outlineProvider = null
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        menu.clear()
        inflater.inflate(R.menu.search_menu_light, menu)
        val menuItem: MenuItem? = menu.findItem(R.id.action_search)
        searchView = menuItem?.actionView as SearchView
        searchView?.setOnQueryTextListener(this)

        menuItem.setOnActionExpandListener(object : MenuItem.OnActionExpandListener {
            override fun onMenuItemActionExpand(item: MenuItem): Boolean {
                return true
            }

            override fun onMenuItemActionCollapse(item: MenuItem): Boolean {
                binding.apply {
                    appBarLayout.setExpanded(true)
                }
                return true
            }
        })
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.action_search) {
            binding.apply {
                appBarLayout.setExpanded(false)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun setAccountSelected() {

        val secureHomePageObject = appCacheService.getSecureHomePageObject()
        val transactionalAccounts = if (secureHomePageObject?.accounts != null) {
            FilterAccountList.getTransactionalAndCreditAccounts(secureHomePageObject.accounts)
        } else {
            FilterAccountList.getTransactionalAndCreditAccounts(AbsaCacheManager.getInstance().cachedAccountListObject?.accountsList)
        }

        transactionalAccounts.forEach {
            val accountListItem = AccountListItem()
            accountListItem.accountNumber = it.accountNumber
            accountListItem.accountType = it.description
            accountListItem.accountBalance = it.availableBalance.toString()
            debitOrderAccountList.add(accountListItem)
        }
        binding.apply {
            if (transactionalAccounts.isNotEmpty()) {
                selectedAccount = transactionalAccounts[0]
                debitOrderViewModel.debitOrderDataModel.fromAccountNumber = selectedAccount.accountNumber
                selectAnAccountNormalInputView.selectedValue = TextFormatUtils.formatAccountNumberAndDescription(selectedAccount.description, selectedAccount.accountNumber)

                selectAnAccountNormalInputView.setList(debitOrderAccountList, getString(R.string.debit_order_select_account_number))
                selectAnAccountNormalInputView.setItemSelectionInterface { index ->
                    selectedAccount = transactionalAccounts[index]
                    selectAnAccountNormalInputView.selectedValue = TextFormatUtils.formatAccountNumberAndDescription(selectedAccount.description, selectedAccount.accountNumber)
                    debitOrderViewModel.debitOrderDataModel.fromAccountNumber = selectedAccount.accountNumber
                    debitOrderViewModel.fetchDebitOrderTransactionList()
                }
            }
        }
    }

    private fun setUpObservers() {
        debitOrderViewModel.debitOrdersList.observe(this, {
            it?.let {
                val baseActivity = activity as? DebiCheckActivity
                baseActivity?.let { base -> base.debitOrderResponse = it }
                populateRecyclerview(it)
                baseActivity?.dismissProgressDialog()
            }
        })

        debitOrderViewModel.stoppedDebitOrderList.observe(this, {
            (activity as? BaseActivity)?.dismissProgressDialog()
            val intent = Intent(activity, DebitOrdersActivity::class.java).apply {
                putExtra(STOPPED_DEBIT_ORDER_LIST, it)
            }
            startActivity(intent)
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

    override fun getTabDescription(): String {
        return arguments?.getString(TAB_DESCRIPTION_KEY) ?: ""
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        shouldUpdateDebitOrders = resultCode == Activity.RESULT_OK && requestCode == DISPUTE_DEBIT_ORDER
    }
}