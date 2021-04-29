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
 */

package com.barclays.absa.banking.manage.profile.ui.widgets

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.appcompat.widget.SearchView
import androidx.navigation.fragment.findNavController
import com.barclays.absa.banking.R
import com.barclays.absa.banking.framework.BaseFragment
import com.barclays.absa.banking.manage.profile.ui.ManageProfileActivity
import com.barclays.absa.banking.manage.profile.ui.addressDetails.ManageProfileAddressDetailsViewModel
import com.barclays.absa.banking.manage.profile.ui.addressDetails.PostalCodeLookupAdapter
import com.barclays.absa.utils.viewModel
import kotlinx.android.synthetic.main.manage_profile_postal_code_lookup_fragment.*

class ManageProfilePostalCodeLookupFragment : BaseFragment(R.layout.manage_profile_postal_code_lookup_fragment), SearchView.OnQueryTextListener {
    private lateinit var postalCodeLookupAdapter: PostalCodeLookupAdapter
    private lateinit var manageProfileAddressDetailsViewModel: ManageProfileAddressDetailsViewModel
    private val handler = Handler(Looper.getMainLooper())
    private var runnable: Runnable? = null
    private var isPoBox: Boolean = false

    override fun onAttach(context: Context) {
        super.onAttach(context)
        manageProfileAddressDetailsViewModel = (context as ManageProfileActivity).viewModel()
        setHasOptionsMenu(true)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        arguments?.let {
            isPoBox = ManageProfilePostalCodeLookupFragmentArgs.fromBundle(it).isPoBox
        }

        manageProfileAddressDetailsViewModel.suburbLookupResults.observe(viewLifecycleOwner, { arrayList ->
            postalCodeLookupAdapter = PostalCodeLookupAdapter(arrayList) { index ->
                manageProfileAddressDetailsViewModel.selectedPostSuburb = arrayList[index]
                findNavController().popBackStack()
            }

            suburbsRecyclerView.adapter = postalCodeLookupAdapter
            dismissProgressDialog()
        })
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)

        inflater.inflate(R.menu.search_always_expanded_menu, menu)
        val searchMenu = menu.findItem(R.id.action_search)
        val searchView = searchMenu.actionView as SearchView
        searchMenu.expandActionView()

        searchView.setOnQueryTextListener(this)

        searchMenu.setOnActionExpandListener(object : MenuItem.OnActionExpandListener {
            override fun onMenuItemActionExpand(item: MenuItem?) = true

            override fun onMenuItemActionCollapse(item: MenuItem?): Boolean {
                runnable?.let { handler.removeCallbacks(it) }
                findNavController().popBackStack()
                return true
            }
        })
    }

    override fun onQueryTextSubmit(query: String?) = false

    override fun onQueryTextChange(newText: String?): Boolean {
        runnable?.let { handler.removeCallbacks(it) }

        if (!newText.isNullOrEmpty() && newText.length >= 3) {
            runnable = Runnable { manageProfileAddressDetailsViewModel.fetchPostalCodes(isPoBox, newText) }.apply {
                handler.postDelayed(this, 1000)
            }
        }
        return true
    }
}