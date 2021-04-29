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

package com.barclays.absa.banking.home.ui

import android.app.SearchManager
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.Filter
import android.widget.Filterable
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.barclays.absa.banking.R
import com.barclays.absa.banking.boundary.model.AccountObject
import com.barclays.absa.banking.framework.BaseActivity
import com.barclays.absa.banking.framework.app.BMBApplication
import com.barclays.absa.banking.home.ui.HomeContainerActivity.CIA_ACCOUNT_LIST
import com.barclays.absa.banking.presentation.shared.IntentFactory
import com.barclays.absa.banking.shared.BaseAlertDialog
import com.barclays.absa.utils.AccessibilityUtils
import com.barclays.absa.utils.CommonUtils
import com.barclays.absa.utils.OperatorPermissionUtils
import kotlinx.android.synthetic.main.select_cia_account_activity.*
import styleguide.cards.Account
import styleguide.cards.AccountView
import java.util.*

class SelectCiaAccountActivity : BaseActivity(), SearchView.OnQueryTextListener {

    private lateinit var ciaAccounts: MutableList<AccountObject>
    private lateinit var ciaAccountsOriginal: List<AccountObject>
    private val ciaAccountsCardAdapter = CiaAccountsCardAdapter()
    private val appLocale = BMBApplication.getApplicationLocale()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.select_cia_account_activity)
        setToolBarBack("Currency Investment Accounts")
        initViews()
        initCiaAccounts()
        createCiaAccountsAdapter()
    }

    private fun initViews() {
        listTitleTextView.setText(R.string.all_accounts)
    }

    @Suppress("UNCHECKED_CAST")
    private fun initCiaAccounts() {
        val extras = intent.extras?.getBundle(CIA_ACCOUNT_LIST)
        ciaAccounts = extras?.getSerializable(CIA_ACCOUNT_LIST) as MutableList<AccountObject>
        ciaAccountsOriginal = ArrayList(ciaAccounts)
    }

    private fun createCiaAccountsAdapter() {
        val layoutManager = LinearLayoutManager(this)
        ciaAccountsRecyclerView.layoutManager = layoutManager
        ciaAccountsRecyclerView.adapter = ciaAccountsCardAdapter
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val menuInflater = menuInflater
        menuInflater.inflate(R.menu.search_menu, menu)
        val searchItem = menu.findItem(R.id.action_search)

        searchItem?.let {
            it.isVisible = true
            val searchManager = getSystemService(Context.SEARCH_SERVICE) as SearchManager
            val searchView = it.actionView as SearchView
            setupSearchView(searchView)
            searchView.setSearchableInfo(searchManager.getSearchableInfo(componentName))
        }
        return super.onCreateOptionsMenu(menu)
    }

    private fun setupSearchView(searchView: SearchView) {
        searchView.setIconifiedByDefault(true)
        searchView.setOnQueryTextListener(this)
        searchView.isSubmitButtonEnabled = false
        searchView.isQueryRefinementEnabled = false
    }

    override fun onQueryTextSubmit(query: String): Boolean {
        performSearch(query)
        return false
    }

    override fun onQueryTextChange(newText: String): Boolean {
        performSearch(newText)
        return false
    }

    private fun performSearch(searchText: String) {
        ciaAccountsCardAdapter.search(searchText)
    }

    private inner class CiaAccountsCardAdapter : RecyclerView.Adapter<CiaAccountsCardAdapter.ViewHolder>(), Filterable {

        private val customFilter: CustomFilter

        init {
            customFilter = CustomFilter()
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.account_small_card_fragment, parent, false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.accountView.visibility = View.VISIBLE
            val ciaAccount = ciaAccounts[position]

            val amount = AccountCardHelper.getAccountAmount(ciaAccount, true)
            val accountName = AccountCardHelper.getAccountDescription(ciaAccount)
            val cardNumber = AccountCardHelper.getCardNumber(ciaAccount)

            holder.accountView.setAccount(Account(amount, cardNumber, accountName, getString(R.string.available_balance)))
            holder.accountView.tag = ciaAccounts[position]

            holder.accountView.setSingleCiaAccountAppearance()
            setupTalkBack(ciaAccounts[position].currency, holder)

            holder.accountView.setOnClickListener { v ->
                val animation = AnimationUtils.loadAnimation(holder.accountView.context, R.anim.scale_in_out_small)
                holder.accountView.startAnimation(animation)
                navigateToAccountInformation(v.tag as AccountObject)
            }
        }

        override fun getItemCount(): Int = ciaAccounts.size

        override fun getFilter(): Filter = customFilter

        fun search(text: String) = filter.filter(text)

        internal inner class CustomFilter : Filter() {

            override fun performFiltering(charSequence: CharSequence): FilterResults {
                val results = FilterResults()
                val searchText = charSequence.toString().toLowerCase(appLocale).trim { it <= ' ' }
                val filteredList = ArrayList<AccountObject>()
                if (searchText.isEmpty()) {
                    filteredList.addAll(ciaAccountsOriginal)
                } else {
                    ciaAccountsOriginal.forEach {
                        val currencyFilter = it.currency.toLowerCase(appLocale)
                        val descFilter = it.description.toLowerCase(appLocale)
                        if (currencyFilter.contains(searchText) || descFilter.contains(searchText)) {
                            filteredList.add(it)
                        }
                    }
                }
                results.values = filteredList
                results.count = filteredList.size
                return results
            }

            @Suppress("UNCHECKED_CAST")
            override fun publishResults(charSequence: CharSequence, filterResults: FilterResults) {
                ciaAccounts.clear()
                ciaAccounts.addAll(filterResults.values as MutableList<AccountObject>)
                notifyDataSetChanged()
            }
        }

        internal inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            var accountView: AccountView = itemView.findViewById(R.id.accountView)
        }

        private fun setupTalkBack(currency: String, holder: ViewHolder) {
            if (AccessibilityUtils.isExploreByTouchEnabled()) {
                holder.accountView.contentDescription = BMBApplication.getInstance().getString(R.string.talkback_home_cia_account_view, currency)
            }
        }

        private fun navigateToAccountInformation(accountObject: AccountObject) {
            if (!OperatorPermissionUtils.canViewTransactions(accountObject)) {
                BaseAlertDialog.showRequestAccessAlertDialog(getString(R.string.account_summary))
                return
            }

            if (!CommonUtils.isAccountSupported(accountObject)) {
                return
            }
            startActivity(IntentFactory.getAccountActivity(this@SelectCiaAccountActivity, accountObject))
        }
    }
}