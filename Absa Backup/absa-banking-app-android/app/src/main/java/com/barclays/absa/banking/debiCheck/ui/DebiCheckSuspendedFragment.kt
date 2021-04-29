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
import androidx.navigation.findNavController
import com.barclays.absa.banking.R
import com.barclays.absa.banking.boundary.model.AccountObject
import com.barclays.absa.banking.debiCheck.services.dto.DebiCheckMandateDetail
import com.barclays.absa.banking.framework.BaseFragment
import com.barclays.absa.utils.AbsaCacheManager
import com.barclays.absa.utils.viewModel
import kotlinx.android.synthetic.main.debi_check_fragment.*
import styleguide.forms.ItemSelectionInterface
import styleguide.forms.SelectorList


class DebiCheckSuspendedFragment : BaseFragment(R.layout.debi_check_fragment), ItemSelectionInterface {

    private lateinit var viewModel: DebiCheckViewModel
    private lateinit var adapter: DebiCheckAdapter
    private lateinit var debiCheckAccountListItemSelectorList: SelectorList<DebiCheckAccountListItem>
    private lateinit var hostActivity: DebiCheckHostActivity

    private var accountList = emptyList<AccountObject>()
    private var accountListItemSelectedIndex = -1
    private lateinit var searchItem: MenuItem

    override fun onAttach(context: Context) {
        super.onAttach(context)
        hostActivity = context as DebiCheckHostActivity
        viewModel = hostActivity.viewModel()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setToolBar(R.string.debicheck_suspended_title)

        adapter = DebiCheckAdapter()
        adapter.adapterInterface = object : DebiCheckAdapterInterface {
            override fun onDebitOrderSelected(mandate: DebiCheckMandateDetail) {
                viewModel.selectedDebitOrder = mandate
                hostActivity.tagDebiCheckEvent("SuspendedMandates_SuspendedMandateItemClicked")
                view.findNavController().navigate(R.id.action_DebiCheckSuspendedFragment_to_DebiCheckMandateDetailsFragment)
            }

            override fun hasFilterResult(hasFilterResult: Boolean) {
                if (hasFilterResult) {
                    recyclerView.visibility = View.VISIBLE
                    emptyFilterTextView.visibility = View.GONE
                    emptyStateAnimationView.visibility = View.GONE
                } else {
                    recyclerView.visibility = View.GONE
                    if (accountListItemSelectedIndex != -1) {
                        emptyFilterTextView.visibility = View.VISIBLE
                        emptyStateAnimationView.visibility = View.VISIBLE
                    }
                }
            }
        }

        accountList = AbsaCacheManager.getTransactionalAccounts()
        debiCheckAccountListItemSelectorList = SelectorList()
        setAccountSelectorList()

        accountSelectorView.apply {
            setTitleText(R.string.debicheck_select_account_suspended)
            setHintText(R.string.choose_an_account)
            setList(debiCheckAccountListItemSelectorList, getString(R.string.select_account_toolbar_title))
            setItemSelectionInterface(this@DebiCheckSuspendedFragment)
        }
        mandateListTitleView.text = getString(R.string.debicheck_suspended_mandates)

        attachDataObserver()
        setHasOptionsMenu(true)
    }

    private fun attachDataObserver() {
        viewModel.mandateResponse.observe(viewLifecycleOwner, { response ->
            response.let {
                if (it.debitOrders.isNotEmpty()) {
                    searchItem.isVisible = true
                }
                adapter.setItems(it.debitOrders)
                mandateListTitleView.visibility = View.VISIBLE
                recyclerView.visibility = if (it.debitOrders.isEmpty()) View.GONE else View.VISIBLE
                infoImageView.visibility = if (it.debitOrders.isEmpty()) View.VISIBLE else View.GONE
                emptyStateTextView.setText(R.string.debicheck_no_suspended_mandates)
                emptyStateTextView.visibility = if (it.debitOrders.isEmpty()) View.VISIBLE else View.GONE

            }
            dismissProgressDialog()
        })
        recyclerView.adapter = adapter
    }

    override fun onItemClicked(index: Int) {
        if (index != accountListItemSelectedIndex) {
            viewModel.fetchMandates(accountList[index].accountNumber, "Suspended", "")
        }
        accountListItemSelectedIndex = index
        accountSelectorView.selectedValue = accountList[index].accountInformation
    }

    override fun onDestroyView() {
        super.onDestroyView()
        adapter.adapterInterface = null
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.search_menu_dark, menu)
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
        return super.onCreateOptionsMenu(menu, inflater)
    }

    private fun setAccountSelectorList() {
        accountList.forEach {
            debiCheckAccountListItemSelectorList.add(DebiCheckAccountListItem(it))
        }
    }
}