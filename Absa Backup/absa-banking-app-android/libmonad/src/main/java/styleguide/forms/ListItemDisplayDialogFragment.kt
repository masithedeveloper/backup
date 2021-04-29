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
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.list_item_display_activity.*
import styleguide.utils.extensions.toTitleCase
import za.co.absa.presentation.uilib.R

open class ListItemDisplayDialogFragment<T : SelectorInterface> : DialogFragment(), ItemSelectionInterface {

    lateinit var itemList: ArrayList<T>
    lateinit var listAdapter: GenericAdapter<T>
    lateinit var toolBar: Toolbar

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        dialog?.window?.attributes?.windowAnimations = R.style.DialogAnimation
        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.list_item_display_activity, container, false)
    }

    override fun getTheme(): Int = R.style.FullScreenDialog

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        toolBar = toolbar as Toolbar
        toolBar.setNavigationIcon(R.drawable.ic_left_arrow_dark)
        toolBar.setNavigationOnClickListener { dismiss() }

        setUpAdapter()
    }

    @Suppress("UNCHECKED_CAST")
    private fun setUpAdapter() {
        recyclerView.layoutManager = LinearLayoutManager(context)
        arguments?.let {
            itemList = it.getSerializable(ITEM_LIST) as ArrayList<T>? ?: ArrayList()
            val selectedIndex = it.getInt(SELECTED_INDEX, -1)
            toolBar.title = it.getString(TOOLBAR_TITLE).toTitleCase()
            listAdapter = GenericAdapter(itemList, selectedIndex, this)
            recyclerView.adapter = listAdapter
        }
    }

    override fun onItemClicked(index: Int) {
        valueSelectedObserver?.onValueSelected(index)
        dismiss()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            dismiss()
        }
        return super.onOptionsItemSelected(item)
    }

    companion object {

        const val TOOLBAR_TITLE = "toolbar_title"
        const val ITEM_LIST = "itemList"
        const val SELECTED_INDEX = "selectedIndex"
        const val SELECTED_ITEM = "selectedItem";

        var valueSelectedObserver: ValueSelectedObserver? = null

        @JvmStatic
        fun newInstance(itemList: SelectorList<*>, selectedIndex: Int, toolbarTitle: String): DialogFragment {
            val fragment = ListItemDisplayDialogFragment<SelectorInterface>()
            fragment.arguments = Bundle().apply {
                putString(TOOLBAR_TITLE, toolbarTitle)
                putSerializable(ITEM_LIST, itemList)
                putInt(SELECTED_INDEX, selectedIndex)
            }
            return fragment
        }
    }

    override fun onPause() {
        super.onPause()
        this.arguments?.clear()
    }
}