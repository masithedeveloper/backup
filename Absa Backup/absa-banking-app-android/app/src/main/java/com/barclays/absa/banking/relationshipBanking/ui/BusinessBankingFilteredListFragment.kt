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
package com.barclays.absa.banking.relationshipBanking.ui

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.Filter
import android.widget.TextView
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import com.barclays.absa.banking.R
import com.barclays.absa.banking.databinding.FragmentBusinessBankingFilteredListBinding
import com.barclays.absa.banking.newToBank.NewToBankView
import com.barclays.absa.banking.presentation.shared.ExtendedFragment
import com.barclays.absa.banking.relationshipBanking.services.dto.FilteredListObject
import styleguide.forms.SelectorInterface
import styleguide.forms.SelectorList

class BusinessBankingFilteredListFragment(private var filteredListObject: FilteredListObject) : ExtendedFragment<FragmentBusinessBankingFilteredListBinding>(), FilteredListAdapter.ItemSelectionListener,
        SearchView.OnQueryTextListener {

    private lateinit var newToBankBusinessView: NewToBankView
    private var searchView: SearchView? = null
    private var filter: Filter? = null

    companion object {
        const val SELECTED_ITEM = "selectedItem"
    }

    override fun onItemSelected(view: View, position: Int) {
        val intent = Intent()
        intent.putExtra(SELECTED_ITEM, (view as TextView).text.toString())
        targetFragment?.onActivityResult(targetRequestCode, RESULT_OK, intent)
        parentFragmentManager.popBackStack()
    }

    override fun getToolbarTitle(): String = filteredListObject.titleText

    override fun getLayoutResourceId(): Int = R.layout.fragment_business_banking_filtered_list

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(filteredListObject.hasOptionsMenu)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        initViews()
    }

    fun initViews() {
        newToBankBusinessView = activity as NewToBankView
        newToBankBusinessView.hideProgressIndicator()
        newToBankBusinessView.setToolbarBackTitle(toolbarTitle)
        val listAdapter = context?.let { FilteredListAdapter(it, getList(filteredListObject.selectorList)) }
        binding.apply {
            headingTextView.text = filteredListObject.description
            recyclerView.layoutManager = LinearLayoutManager(context)
            recyclerView.apply {
                setHasFixedSize(true)
                adapter = listAdapter?.apply {
                    itemSelectionListener = this@BusinessBankingFilteredListFragment
                }
            }
        }
        filter = listAdapter?.filter
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        menu.clear()
        inflater.inflate(R.menu.search_menu, menu)
        val menuItem: MenuItem? = menu.findItem(R.id.action_search)
        searchView = menuItem?.actionView as SearchView
        searchView?.setOnQueryTextListener(this)
    }

    fun startSearch(query: String) {
        filter?.filter(query.trim())
    }

    override fun onQueryTextSubmit(queryText: String?): Boolean {
        queryText?.let { startSearch(it) }
        return false
    }

    override fun onQueryTextChange(queryText: String?): Boolean {
        queryText?.let { startSearch(it) }
        return false
    }

    private fun getList(selectorList: SelectorList<out SelectorInterface>) = ArrayList<String>().apply {
        addAll(selectorList.mapNotNull { it.displayValue })
    }
}