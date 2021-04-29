/*
 *  Copyright (c) 2019 Absa Bank Limited, All Rights Reserved.
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

package styleguide.forms

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.DialogFragment
import za.co.absa.presentation.uilib.R

class SearchableListItemDialogFragment<T : SelectorInterface> : ListItemDisplayDialogFragment<T>(), SearchView.OnQueryTextListener {

    private lateinit var searchView: SearchView
    private lateinit var filteredItemList: ArrayList<T>

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        filteredItemList = itemList
        toolBar.inflateMenu(R.menu.libmonad_search_menu)
        val menuItem: MenuItem? = toolBar.menu.findItem(R.id.actionSearch)
        searchView = menuItem?.actionView as SearchView
        searchView.queryHint = getString(R.string.search)
        searchView.setOnQueryTextListener(this)
    }

    private fun updateAdapter(query: String) {
        listAdapter.updateList(filteredItemList, query)
    }

    override fun onItemClicked(index: Int) {
        val mainItemListIndex = itemList.indexOf(filteredItemList[index])
        valueSelectedObserver?.onValueSelected(mainItemListIndex)
        dismiss()
    }

    fun search(query: String) {
        filteredItemList = itemList.filter {
            val displayValueLine1 = it.displayValue ?: ""
            val displayValueLine2 = it.displayValueLine2 ?: ""

            displayValueLine1.contains(query, ignoreCase = true) || displayValueLine2.contains(query, ignoreCase = true)
        } as ArrayList<T>
        updateAdapter(query)
    }

    override fun onQueryTextChange(query: String?): Boolean {
        query?.let {
            search(it)
        }
        return false
    }

    override fun onQueryTextSubmit(query: String?) = false

    companion object {

        var valueSelectedObserver: ValueSelectedObserver? = null

        @JvmStatic
        fun newInstance(itemList: SelectorList<*>, selectedIndex: Int, toolbarTitle: String): DialogFragment {
            val fragment = SearchableListItemDialogFragment<SelectorInterface>()
            fragment.arguments = Bundle().apply {
                putString(TOOLBAR_TITLE, toolbarTitle)
                putSerializable(ITEM_LIST, itemList)
                putInt(SELECTED_INDEX, selectedIndex)
            }
            return fragment
        }
    }
}