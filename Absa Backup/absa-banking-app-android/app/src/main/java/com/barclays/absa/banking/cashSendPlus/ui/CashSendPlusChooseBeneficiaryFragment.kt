/*
 *  Copyright (c) 2020 Absa Bank Limited, All Rights Reserved.
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
package com.barclays.absa.banking.cashSendPlus.ui

import android.app.SearchManager
import android.content.Context
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.Filter
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.barclays.absa.banking.R
import com.barclays.absa.banking.boundary.model.AccountList
import com.barclays.absa.banking.boundary.model.AccountObject
import com.barclays.absa.banking.boundary.model.BeneficiaryListObject
import com.barclays.absa.banking.boundary.model.BeneficiaryObject
import com.barclays.absa.banking.cashSendPlus.services.CashSendPlusSendMultiplePaymentDetails
import com.barclays.absa.banking.express.getAllBalances.dto.AccountTypesBMG
import com.barclays.absa.banking.framework.BaseFragment
import com.barclays.absa.utils.AbsaCacheManager
import com.barclays.absa.utils.CommonUtils
import kotlinx.android.synthetic.main.cash_send_plus_choose_beneficiary_fragment.*

class CashSendPlusChooseBeneficiaryFragment : BaseFragment(R.layout.cash_send_plus_choose_beneficiary_fragment), SearchView.OnQueryTextListener, CashSendPlusChooseBeneficiaryAdapter.OnChooseBeneficiaryItemSelectedListener, CashSendPlusSelectedBeneficiaryAdapter.OnRemoveSelectedBeneficiaryListener {
    private val cashSendPlusViewModel by activityViewModels<CashSendPlusViewModel>()
    private lateinit var filter: Filter
    private val selectedBeneficiaryList = mutableListOf<BeneficiaryObject>()
    private var beneficiaryObjectList = mutableListOf<BeneficiaryObject>()
    private lateinit var selectedBeneficiaryAdapter: CashSendPlusSelectedBeneficiaryAdapter
    private lateinit var cashSendPlusChooseBeneficiaryAdapter: CashSendPlusChooseBeneficiaryAdapter
    private val accountsList: AccountList? by lazy { AbsaCacheManager.getInstance().accountsList }
    private val maximumAllowedBeneficiaries = CashSendPlusSendMultipleActivity.MAX_ALLOWED_BENEFICIARIES

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setToolBar(R.string.cash_send_plus_choose_beneficiaries)

        if (cashSendPlusViewModel.cashSendPlusBeneficiaryListResponse.value == null) {
            cashSendPlusViewModel.fetchBeneficiaryList()

            cashSendPlusViewModel.cashSendPlusBeneficiaryListResponse.observe(viewLifecycleOwner, {
                populateAvailableNewBeneficiariesList(it)
                dismissProgressDialog()
            })
        } else {
            populateAvailableBeneficiariesList()
        }

        populateSelectedBeneficiariesList()

        nextButton.setOnClickListener {
            if (selectedBeneficiaryList.isNotEmpty()) {
                val cashSendPlusSendMultiplePaymentDetailsList = mutableListOf<CashSendPlusSendMultiplePaymentDetails>()
                val defaultAccountDetail = accountsList?.accountsList?.firstOrNull { it.accountType == AccountTypesBMG.savingsAccount.name || it.accountType == AccountTypesBMG.currentAccount.name }

                selectedBeneficiaryList.forEachIndexed { index, beneficiaryObject ->
                    val paymentDetailsList = CashSendPlusSendMultiplePaymentDetails().apply {
                        id = index.toLong()
                        accountDetail = defaultAccountDetail ?: AccountObject()
                        beneficiaryInfo = beneficiaryObject
                        reference = beneficiaryObject.myReference ?: ""
                    }
                    cashSendPlusSendMultiplePaymentDetailsList.add(paymentDetailsList)
                }

                appCacheService.setCashSendPlusSendMultipleBeneficiariesPaymentDetails(cashSendPlusSendMultiplePaymentDetailsList)
                navigate(CashSendPlusChooseBeneficiaryFragmentDirections.actionCashSendPlusChooseBeneficiaryFragmentToCashSendPlusBeneficiaryPaymentDetailsFragment())
            }
        }
    }

    private fun populateAvailableBeneficiariesList() {
        if (beneficiaryObjectList.isNotEmpty()) {
            CommonUtils.sortBeneficiaryData(beneficiaryObjectList)
            cashSendPlusChooseBeneficiaryAdapter = CashSendPlusChooseBeneficiaryAdapter(beneficiaryObjectList, this)
            filter = cashSendPlusChooseBeneficiaryAdapter.filter
            availableBeneficiaryRecyclerView.apply {
                setHasFixedSize(true)
                adapter = cashSendPlusChooseBeneficiaryAdapter
            }
            baseActivity.invalidateOptionsMenu()
        }
    }

    private fun populateAvailableNewBeneficiariesList(beneficiaryListObject: BeneficiaryListObject) {
        beneficiaryObjectList.clear()
        selectedBeneficiaryList.clear()
        beneficiaryListObject.cashsendBeneficiaryList?.forEach { beneficiaryObjectList.add(it) }
        populateAvailableBeneficiariesList()
    }

    private fun populateSelectedBeneficiariesList() {
        selectedBeneficiaryAdapter = CashSendPlusSelectedBeneficiaryAdapter(selectedBeneficiaryList, this)
        selectedBeneficiaryRecyclerView.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(baseActivity, LinearLayoutManager.HORIZONTAL, false)
            itemAnimator = DefaultItemAnimator()
            adapter = selectedBeneficiaryAdapter
        }

        if (selectedBeneficiaryList.isNotEmpty()) {
            selectedBeneficiaryRecyclerView.visibility = View.VISIBLE
            cashSendPlusChooseBeneficiaryAdapter.updateSelectedBeneficiaries(selectedBeneficiaryList)
        }
        selectSavedBeneficiaryTextView.text = getString(R.string.select_saved_beneficiaries, maximumAllowedBeneficiaries)
    }

    override fun onCreateOptionsMenu(menu: Menu, menuInflater: MenuInflater) {
        if (beneficiaryObjectList.isNotEmpty()) {
            menuInflater.inflate(R.menu.search_menu, menu)
            val searchItem = menu.findItem(R.id.action_search)
            if (searchItem != null) {
                val searchManager = baseActivity.getSystemService(Context.SEARCH_SERVICE) as SearchManager?
                with(searchItem.actionView as SearchView) {
                    setOnQueryTextListener(this@CashSendPlusChooseBeneficiaryFragment)
                    isSubmitButtonEnabled = false
                    isQueryRefinementEnabled = false
                    imeOptions = EditorInfo.IME_ACTION_DONE
                    searchManager?.let {
                        setSearchableInfo(it.getSearchableInfo(baseActivity.componentName))
                    }
                }

                searchItem.setOnActionExpandListener(object : MenuItem.OnActionExpandListener {
                    override fun onMenuItemActionExpand(menuItem: MenuItem): Boolean {
                        return true
                    }

                    override fun onMenuItemActionCollapse(menuItem: MenuItem): Boolean {
                        if (selectedBeneficiaryList.isNotEmpty()) {
                            selectedBeneficiaryRecyclerView.visibility = View.VISIBLE
                        } else {
                            selectedBeneficiaryAvailableBeneficiaryListViewDividerView.visibility = View.GONE
                        }
                        return true
                    }
                })
            }
        }
        return super.onCreateOptionsMenu(menu, menuInflater)
    }

    override fun onRemoveSelectedBeneficiary(item: BeneficiaryObject) {
        if (selectedBeneficiaryList.any { it.beneficiaryAccountNumber == item.beneficiaryAccountNumber }) {
            selectedBeneficiaryList.removeIf { it.beneficiaryAccountNumber == item.beneficiaryAccountNumber }
            selectSavedBeneficiaryTextView.text = getString(R.string.number_of_select_saved_beneficiaries, selectedBeneficiaryList.size, maximumAllowedBeneficiaries)
            if (selectedBeneficiaryList.isEmpty()) {
                selectedBeneficiaryRecyclerView.visibility = View.GONE
                selectSavedBeneficiaryTextView.text = getString(R.string.select_saved_beneficiaries, maximumAllowedBeneficiaries)
            }
            cashSendPlusChooseBeneficiaryAdapter.updateSelectedBeneficiaries(selectedBeneficiaryList)
            selectedBeneficiaryAdapter.notifyDataSetChanged()
        }
    }

    override fun onBeneficiarySelected(isChecked: Boolean, item: BeneficiaryObject) {
        if (isChecked && selectedBeneficiaryList.size < maximumAllowedBeneficiaries) {
            selectedBeneficiaryList.add(item)
            selectedBeneficiaryRecyclerView.visibility = View.VISIBLE
            selectSavedBeneficiaryTextView.text = getString(R.string.number_of_select_saved_beneficiaries, selectedBeneficiaryList.size, maximumAllowedBeneficiaries)
            cashSendPlusChooseBeneficiaryAdapter.updateSelectedBeneficiaries(selectedBeneficiaryList)
            selectedBeneficiaryAdapter.notifyDataSetChanged()
        } else {
            onRemoveSelectedBeneficiary(item)
        }
    }

    override fun onBeneficiaryListFiltered(results: MutableList<BeneficiaryObject>) {
        if (results.isEmpty()) {
            beneficiaryNotFoundTextView.visibility = View.VISIBLE
            availableBeneficiaryRecyclerView.visibility = View.GONE
        } else {
            beneficiaryNotFoundTextView.visibility = View.GONE
            availableBeneficiaryRecyclerView.visibility = View.VISIBLE
        }
    }

    override fun onQueryTextSubmit(query: String): Boolean {
        filter.filter(query.trim())
        return false
    }

    override fun onQueryTextChange(newText: String): Boolean {
        filter.filter(newText.trim())
        return false
    }
}