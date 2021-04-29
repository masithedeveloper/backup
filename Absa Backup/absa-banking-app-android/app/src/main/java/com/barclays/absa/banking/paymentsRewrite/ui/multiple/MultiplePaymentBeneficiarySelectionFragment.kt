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
package com.barclays.absa.banking.paymentsRewrite.ui.multiple

import android.app.SearchManager
import android.content.Context
import android.os.Bundle
import android.text.InputType
import android.view.*
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.Filter
import android.widget.TextView
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.barclays.absa.banking.R
import com.barclays.absa.banking.databinding.MultiplePaymentBeneficiarySelectionFragmentBinding
import com.barclays.absa.banking.express.beneficiaries.dto.RegularBeneficiary
import com.barclays.absa.banking.framework.analytics.AnalyticsUtils
import com.barclays.absa.banking.framework.app.BMBConstants
import com.barclays.absa.banking.payments.services.multiple.MultipleBeneficiaryPaymentService
import com.barclays.absa.banking.presentation.utils.ToolBarUtils.setToolBarBack
import com.barclays.absa.integration.DeviceProfilingInteractor.NextActionCallback
import com.barclays.absa.utils.extensions.viewBinding

class MultiplePaymentBeneficiarySelectionFragment : MultiplePaymentsBaseFragment(R.layout.multiple_payment_beneficiary_selection_fragment), MultipleBeneficiarySelectionView, SearchView.OnQueryTextListener {

    private val binding by viewBinding(MultiplePaymentBeneficiarySelectionFragmentBinding::bind)

    private val maximumAllowedBeneficiaries: Int
        get() = if (isBusinessAccount) MultiplePaymentsViewModel.MAX_ALLOWED_BUSINESS_BENEFICIARIES else MultiplePaymentsViewModel.MAX_ALLOWED_RETAIL_BENEFICIARIES

    private var searchItem: MenuItem? = null

    private lateinit var filter: Filter
    private lateinit var menuInflater: MenuInflater
    private lateinit var selectedBeneficiaryAdapter: SelectedBeneficiaryAdapter
    private lateinit var availableBeneficiarySectionAdapter: MultipleBeneficiarySectionAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)
        setupTalkBack()
        setToolBarBack(baseActivity, getString(R.string.select_beneficiaries))
        getDeviceProfilingInteractor().notifyTransaction()

        AnalyticsUtils.getInstance().trackCustomScreenView("Select Beneficiaries inactive state", MultipleBeneficiaryPaymentService.ANALYTICS_CHANNEL_NAME, BMBConstants.TRUE_CONST)
        populateSavedBeneficiaryLists()
    }

    override fun onCreateOptionsMenu(menu: Menu, menuInflater: MenuInflater) {
        this.menuInflater = menuInflater
        return super.onCreateOptionsMenu(menu, menuInflater)
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        super.onPrepareOptionsMenu(menu)
        menuInflater.inflate(R.menu.search_menu, menu)
        searchItem = menu.findItem(R.id.action_search)
        val searchView: SearchView
        if (searchItem != null) {
            searchView = searchItem?.actionView as SearchView
            val searchManager = baseActivity.getSystemService(Context.SEARCH_SERVICE) as SearchManager?
            searchView.setOnQueryTextListener(this)
            searchView.isSubmitButtonEnabled = false
            searchView.isQueryRefinementEnabled = false
            searchView.imeOptions = EditorInfo.IME_ACTION_DONE
            if (searchManager != null) {
                searchView.setSearchableInfo(searchManager.getSearchableInfo(baseActivity.componentName))
            }
            val searchPlate = searchView.findViewById<EditText>(R.id.search_src_text)
            if (searchPlate != null) {
                searchPlate.inputType = EditorInfo.TYPE_TEXT_VARIATION_FILTER or InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD or InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS
                searchPlate.privateImeOptions = "nm"
                searchPlate.setOnEditorActionListener { _: TextView?, actionId: Int, _: KeyEvent? ->
                    if (actionId == EditorInfo.IME_ACTION_DONE) {
                        searchItem?.collapseActionView()
                    }
                    false
                }
            }
            searchItem?.setOnActionExpandListener(object : MenuItem.OnActionExpandListener {
                override fun onMenuItemActionExpand(menuItem: MenuItem): Boolean {
                    return true
                }

                override fun onMenuItemActionCollapse(menuItem: MenuItem): Boolean {
                    if (multiplePaymentsViewModel.selectedBeneficiaryList.isNotEmpty()) {
                        binding.selectedBeneficiaryRecyclerView.visibility = View.VISIBLE
                    } else {
                        binding.selectedBeneficiaryAvailableBeneficiaryRecyclerViewDivider.visibility = View.GONE
                    }
                    return true
                }
            })
        }
    }

    override fun onResume() {
        super.onResume()
        searchItem?.isVisible = paymentsViewModel.beneficiaryList.isNotEmpty()
    }

    private fun setupTalkBack() {
        binding.beneficiaryNotFoundTextView.contentDescription = getString(R.string.talkback_multipay_beneficiary_not_found)
    }

    private fun populateSavedBeneficiaryLists() {
        binding.selectSavedBeneficiaryTextView.text = getString(R.string.select_saved_beneficiaries, maximumAllowedBeneficiaries)

        selectedBeneficiaryAdapter = SelectedBeneficiaryAdapter(multiplePaymentsViewModel.selectedBeneficiaryList, object : SelectedBeneficiaryAdapter.BeneficiaryAdapterInterface {
            override fun beneficiaryRemoveIconClicked(index: Int) {
                if (index >= 0 && index < multiplePaymentsViewModel.selectedBeneficiaryList.size) {
                    val regularBeneficiary: RegularBeneficiary = multiplePaymentsViewModel.selectedBeneficiaryList[index]
                    if (multiplePaymentsViewModel.selectedBeneficiaryList.contains(regularBeneficiary)) {
                        multiplePaymentsViewModel.selectedBeneficiaryList.remove(regularBeneficiary)
                        notifyOnItemDeselection(index)
                    }
                    toggleContinueButton(multiplePaymentsViewModel.selectedBeneficiaryList.isNotEmpty())
                }
            }
        })

        with(binding.selectedBeneficiaryRecyclerView) {
            adapter = selectedBeneficiaryAdapter
            itemAnimator = DefaultItemAnimator()
            layoutManager = LinearLayoutManager(baseActivity, LinearLayoutManager.HORIZONTAL, false)
        }

        if (multiplePaymentsViewModel.selectedBeneficiaryList.isNotEmpty()) {
            binding.selectedBeneficiaryAvailableBeneficiaryRecyclerViewDivider.visibility = View.VISIBLE
            binding.continueButton.isEnabled = true
            binding.selectedBeneficiaryRecyclerView.visibility = View.VISIBLE
        }

        availableBeneficiarySectionAdapter = MultipleBeneficiarySectionAdapter(baseActivity, this, paymentsViewModel.beneficiaryList)

        filter = availableBeneficiarySectionAdapter.filter
        baseActivity.invalidateOptionsMenu()
        binding.availableBeneficiaryRecyclerView.setHasFixedSize(true)
        binding.availableBeneficiaryRecyclerView.adapter = availableBeneficiarySectionAdapter
        binding.continueButton.setOnClickListener {
            if (multiplePaymentsViewModel.selectedBeneficiaryList.isNotEmpty()) {
                getDeviceProfilingInteractor().callForDeviceProfilingScoreForPayments(object : NextActionCallback {
                    override fun onNextAction() {
                        dismissProgressDialog()
                        multiplePaymentsViewModel.multiplePaymentBeneficiaryWrapperList.clear()
                        navigateToChooseAccountScreen()
                    }
                })
            }
        }
    }

    override fun onQueryTextSubmit(query: String?): Boolean = false

    override fun onQueryTextChange(query: String?): Boolean {
        if (paymentsViewModel.beneficiaryList.isNotEmpty()) {
            binding.selectedBeneficiaryRecyclerView.visibility = if ((query ?: "").isEmpty() && multiplePaymentsViewModel.selectedBeneficiaryList.isNotEmpty()) View.VISIBLE else View.GONE
            startSearch(query ?: "")
        }
        return false
    }

    private fun startSearch(query: String) {
        val trimmedQuery = query.trim { it <= ' ' }
        filter.filter(trimmedQuery)
    }

    override fun onBeneficiaryClicked(adapterPosition: Int) {
        getSectionListBeneficiary(adapterPosition)?.let { regularBeneficiary ->
            if (!multiplePaymentsViewModel.selectedBeneficiaryList.contains(regularBeneficiary)) {
                if (multiplePaymentsViewModel.selectedBeneficiaryList.size < maximumAllowedBeneficiaries) {
                    multiplePaymentsViewModel.selectedBeneficiaryList.add(regularBeneficiary)
                }
                notifyOnItemSelection()
            } else {
                val position = multiplePaymentsViewModel.selectedBeneficiaryList.indexOf(regularBeneficiary)
                multiplePaymentsViewModel.selectedBeneficiaryList.remove(regularBeneficiary)
                notifyOnItemDeselection(position)
            }

            toggleContinueButton(multiplePaymentsViewModel.selectedBeneficiaryList.isNotEmpty())
        }
    }

    override val selectedBeneficiaries: List<RegularBeneficiary>
        get() = multiplePaymentsViewModel.selectedBeneficiaryList

    override fun getSectionListBeneficiary(selectedPosition: Int): RegularBeneficiary? = availableBeneficiarySectionAdapter.getSectionListBeneficiary(selectedPosition)

    override fun notifyOnItemSelection() {
        AnalyticsUtils.getInstance().trackCustomScreenView("Select beneficiaries active state", MultipleBeneficiaryPaymentService.ANALYTICS_CHANNEL_NAME, BMBConstants.TRUE_CONST)
        selectedBeneficiaryAdapter.notifyItemInserted(multiplePaymentsViewModel.selectedBeneficiaryList.size)
        availableBeneficiarySectionAdapter.notifyDataSetChanged()
        binding.selectSavedBeneficiaryTextView.text = getString(R.string.number_of_select_saved_beneficiaries, multiplePaymentsViewModel.selectedBeneficiaryList.size, maximumAllowedBeneficiaries)
        toggleSelectedBeneficiariesVisibility()
    }

    private fun toggleSelectedBeneficiariesVisibility() {
        with(binding) {
            if (multiplePaymentsViewModel.selectedBeneficiaryList.isEmpty()) {
                selectedBeneficiaryRecyclerView.visibility = View.GONE
                selectedBeneficiaryAvailableBeneficiaryRecyclerViewDivider.visibility = View.GONE
            } else {
                selectedBeneficiaryRecyclerView.visibility = View.VISIBLE
                selectedBeneficiaryAvailableBeneficiaryRecyclerViewDivider.visibility = View.VISIBLE
            }
        }
    }

    override fun toggleContinueButton(enable: Boolean) {
        binding.continueButton.isEnabled = enable
    }

    override fun onBeneficiaryListFiltered(paymentBeneficiaryList: List<RegularBeneficiary>) {
        with(binding) {
            if (paymentBeneficiaryList.isEmpty()) {
                AnalyticsUtils.getInstance().trackCustomScreenView("No search result", MultipleBeneficiaryPaymentService.ANALYTICS_CHANNEL_NAME, BMBConstants.TRUE_CONST)
                beneficiaryNotFoundTextView.visibility = View.VISIBLE
                availableBeneficiaryRecyclerView.visibility = View.GONE
            } else {
                beneficiaryNotFoundTextView.visibility = View.GONE
                availableBeneficiaryRecyclerView.visibility = View.VISIBLE
            }
        }
    }

    override fun notifyOnItemDeselection(position: Int) {
        AnalyticsUtils.getInstance().trackCustomScreenView("Select Beneficiaries inactive state", MultipleBeneficiaryPaymentService.ANALYTICS_CHANNEL_NAME, BMBConstants.TRUE_CONST)
        selectedBeneficiaryAdapter.notifyItemRemoved(position)
        availableBeneficiarySectionAdapter.notifyDataSetChanged()
        binding.selectSavedBeneficiaryTextView.text = getString(R.string.number_of_select_saved_beneficiaries, multiplePaymentsViewModel.selectedBeneficiaryList.size, maximumAllowedBeneficiaries)
        toggleSelectedBeneficiariesVisibility()
    }

    override fun stopSearch() {
        searchItem?.collapseActionView()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        AnalyticsUtils.getInstance().trackSearchEvent("Search")
        return super.onOptionsItemSelected(item)
    }

    override fun navigateToChooseAccountScreen() {
        navigate(MultiplePaymentBeneficiarySelectionFragmentDirections.actionMultiplePaymentBeneficiarySelectionFragmentToMultiplePaymentSelectedBeneficiariesFragment())
    }
}