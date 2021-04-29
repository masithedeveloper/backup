/*
 * Copyright (c) 2020 Absa Bank Limited, All Rights Reserved.
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
package com.barclays.absa.banking.buy.ui.electricity

import android.app.SearchManager
import android.content.Context
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.appcompat.widget.SearchView
import com.barclays.absa.banking.R
import com.barclays.absa.banking.boundary.model.BeneficiaryListObject
import com.barclays.absa.banking.boundary.model.BeneficiaryObject
import com.barclays.absa.banking.buy.ui.electricity.PrepaidElectricityActivity.BUY_ELECTRICITY
import com.barclays.absa.banking.framework.BaseFragment
import com.barclays.absa.banking.framework.analytics.AnalyticsUtils
import com.barclays.absa.banking.framework.app.BMBConstants
import com.barclays.absa.banking.presentation.shared.GenericGroupingBeneficiaryAdapter
import com.barclays.absa.banking.presentation.shared.GenericRecentBeneficiaryAdapter
import com.barclays.absa.banking.presentation.shared.NoScrollLinearLayoutManager
import com.barclays.absa.utils.AnalyticsUtil
import kotlinx.android.synthetic.main.prepaid_electricity_beneficiary_fragment.*

class PrepaidElectricityBeneficiariesFragment : BaseFragment(R.layout.prepaid_electricity_beneficiary_fragment), SearchView.OnQueryTextListener {
    private lateinit var prepaidElectricityView: PrepaidElectricityView
    private lateinit var latestTransactionsBeneficiaryAdapter: GenericRecentBeneficiaryAdapter
    private lateinit var electricityBeneficiaryAdapter: GenericGroupingBeneficiaryAdapter
    private var beneficiaryListObject: BeneficiaryListObject? = null
    private var searchMenuItem: MenuItem? = null

    companion object {
        private const val BENEFICIARY_LIST_OBJECT = "beneficiaryListObject"

        @JvmStatic
        fun newInstance(): PrepaidElectricityBeneficiariesFragment = PrepaidElectricityBeneficiariesFragment()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        this.setHasOptionsMenu(true)
        setToolBar(getString(R.string.title_prepaid_electricity))
        prepaidElectricityView = activity as PrepaidElectricityView

        if (beneficiaryListObject != null) {
            initViews()
        } else {
            prepaidElectricityView.beneficiariesObservable?.addObserver { _, arg ->
                beneficiaryListObject = arg as BeneficiaryListObject?
                initViews()
            }
        }

        AnalyticsUtil.trackAction(BUY_ELECTRICITY, "BuyElectricity_BuyElectricityScreen_ScreenDisplayed")
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putSerializable(BENEFICIARY_LIST_OBJECT, beneficiaryListObject)
        super.onSaveInstanceState(outState)
    }

    override fun onViewStateRestored(inState: Bundle?) {
        super.onViewStateRestored(inState)
        inState?.getSerializable(BENEFICIARY_LIST_OBJECT)?.let {
            beneficiaryListObject = inState.getSerializable(BENEFICIARY_LIST_OBJECT) as BeneficiaryListObject
            initViews()
        }
    }

    private fun initViews() {
        beneficiaryListObject?.let {
            val latestTransactionsBeneficiaryList = beneficiaryListObject?.latestTransactionBeneficiaryList ?: ArrayList<BeneficiaryObject>()
            val electricityBeneficiaryList = beneficiaryListObject?.electricityBeneficiaryList ?: ArrayList<BeneficiaryObject>()
            val recentTransactionBeneficiaryList: MutableList<BeneficiaryObject?> = ArrayList()

            recentTransactionBeneficiaryList.addAll(latestTransactionsBeneficiaryList.take(3))

            electricityBeneficiaryAdapter = GenericGroupingBeneficiaryAdapter(electricityBeneficiaryList) { beneficiaryObject: BeneficiaryObject ->
                AnalyticsUtil.trackAction(BUY_ELECTRICITY, "BuyElectricity_BuyElectricityScreen_ExistingBeneficiaryClicked")
                prepaidElectricityView.validateExistingMeterNumber(beneficiaryObject.beneficiaryAccountNumber, beneficiaryObject)
            }

            latestTransactionsBeneficiaryAdapter = GenericRecentBeneficiaryAdapter(recentTransactionBeneficiaryList) { beneficiaryObject: BeneficiaryObject ->
                AnalyticsUtil.trackAction(BUY_ELECTRICITY, "BuyElectricity_BuyElectricityScreen_ExistingBeneficiaryClicked")
                prepaidElectricityView.validateExistingMeterNumber(beneficiaryObject.beneficiaryAccountNumber, beneficiaryObject)
            }

            if (electricityBeneficiaryList.isEmpty()) {
                beneficiaryRecyclerView.visibility = View.GONE
                dividerView.visibility = View.GONE
                noBeneficiaryFoundContainer.visibility = View.VISIBLE
            } else {
                with(beneficiaryRecyclerView) {
                    layoutManager = NoScrollLinearLayoutManager(requireContext())
                    isNestedScrollingEnabled = false
                    adapter = electricityBeneficiaryAdapter
                }
            }
            if (latestTransactionsBeneficiaryList.isEmpty()) {
                recentElectricityPurchasesRecyclerView.visibility = View.GONE
                recentPurchasesHeadingView.visibility = View.GONE
                dividerView.visibility = View.GONE
            } else {
                with(recentElectricityPurchasesRecyclerView) {
                    visibility = View.VISIBLE
                    layoutManager = NoScrollLinearLayoutManager(context)
                    isNestedScrollingEnabled = false
                    adapter = latestTransactionsBeneficiaryAdapter
                }
            }
            buySomeoneNewOptionActionButtonView.setOnClickListener {
                prepaidElectricityView.navigateToRecipientMeterNumberFragment()
                AnalyticsUtil.trackAction(BUY_ELECTRICITY, "BuyElectricity_BuyElectricityScreen_BuyForSomeoneNewActionButtonClicked")
            }
            addNewBeneficiaryImageView.setOnClickListener {
                prepaidElectricityView.navigateToRecipientMeterNumberFragment()
                AnalyticsUtil.trackAction(BUY_ELECTRICITY, "BuyElectricity_BuyElectricityScreen_BuyForSomeoneNewActionButtonClicked")
            }
            purchaseHistoryOptionActionButtonView.setOnClickListener {
                prepaidElectricityView.navigateToPurchaseHistoryFragment()
                AnalyticsUtil.trackAction(BUY_ELECTRICITY, "BuyElectricity_BuyElectricityScreen_TokenPurchaseHistoryActionButtonClicked")

            }
            needHelpOptionActionButtonView.setOnClickListener {
                prepaidElectricityView.navigateToNeedHelpFragment()
                AnalyticsUtil.trackAction(BUY_ELECTRICITY, "BuyElectricity_BuyElectricityScreen_NeedHelpActionButtonClicked")
            }
        }
        searchMenuItem?.isVisible = beneficiaryListObject != null && !beneficiaryListObject?.electricityBeneficiaryList.isNullOrEmpty() && searchMenuItem != null
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.search_menu_dark, menu)
        searchMenuItem = menu.findItem(R.id.action_search)
        val searchManager: SearchManager = activity?.getSystemService(Context.SEARCH_SERVICE) as SearchManager
        val searchMenuActionView: SearchView = searchMenuItem?.actionView as SearchView
        searchMenuItem?.let {
            searchMenuActionView.apply {
                setIconifiedByDefault(true)
                setOnQueryTextListener(this@PrepaidElectricityBeneficiariesFragment)
                isSubmitButtonEnabled = false
                isQueryRefinementEnabled = false
            }
            searchMenuItem?.isVisible = beneficiaryListObject != null && !beneficiaryListObject?.electricityBeneficiaryList.isNullOrEmpty() && searchMenuItem != null
        }
        searchMenuActionView.setSearchableInfo(searchManager.getSearchableInfo(activity?.componentName))
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.action_search) {
            AnalyticsUtils.getInstance().trackSearchEvent(BMBConstants.BUY_PREPAID_ELECTRICITY_CONST)
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onQueryTextSubmit(query: String): Boolean {
        startSearch(query)
        return false
    }

    override fun onQueryTextChange(query: String): Boolean {
        startSearch(query)
        return false
    }

    private fun startSearch(query: String) {
        if (query.isBlank()) {
            latestTransactionsBeneficiaryAdapter.filter("")
            filterBeneficiaryList("")
            buySomeoneNewOptionActionButtonView.visibility = View.VISIBLE
            purchaseHistoryOptionActionButtonView.visibility = View.VISIBLE
            needHelpOptionActionButtonView.visibility = View.VISIBLE
        } else {
            latestTransactionsBeneficiaryAdapter.filter(query.trim { it <= ' ' })
            filterBeneficiaryList(query.trim { it <= ' ' })
            buySomeoneNewOptionActionButtonView.visibility = View.GONE
            purchaseHistoryOptionActionButtonView.visibility = View.GONE
            needHelpOptionActionButtonView.visibility = View.GONE
        }
        if (latestTransactionsBeneficiaryAdapter.isEmpty) {
            recentPurchasesHeadingView.visibility = View.GONE
            dividerView.visibility = View.GONE
        } else {
            recentPurchasesHeadingView.visibility = View.VISIBLE
            dividerView.visibility = View.VISIBLE
        }
    }

    private fun filterBeneficiaryList(query: String) {
        val electricityBeneficiaryList = beneficiaryListObject?.electricityBeneficiaryList ?: ArrayList()
        val electricityBeneficiaryListFiltered: MutableList<BeneficiaryObject?> = ArrayList()

        if (query.isNotBlank()) {
            electricityBeneficiaryListFiltered.addAll(electricityBeneficiaryListFiltered.filter { beneficiaryObject -> beneficiaryObject?.beneficiaryName?.contains(query, true) == true || beneficiaryObject?.beneficiaryAccountNumber?.contains(query, true) == true })
        } else {
            electricityBeneficiaryListFiltered.addAll(electricityBeneficiaryList)
        }
        electricityBeneficiaryAdapter = GenericGroupingBeneficiaryAdapter(electricityBeneficiaryListFiltered, query.trim { it <= ' ' }) { beneficiaryObject -> prepaidElectricityView.validateExistingMeterNumber(beneficiaryObject.beneficiaryAccountNumber, beneficiaryObject) }

        if (electricityBeneficiaryListFiltered.isEmpty() && latestTransactionsBeneficiaryAdapter.isEmpty) {
            beneficiariesConstraintLayout.visibility = View.GONE
            listEmptyTextView.visibility = View.VISIBLE
        } else {
            beneficiariesConstraintLayout.visibility = View.VISIBLE
            listEmptyTextView.visibility = View.GONE
        }
        beneficiaryRecyclerView.adapter = electricityBeneficiaryAdapter
        electricityBeneficiaryAdapter.notifyDataSetChanged()
    }
}