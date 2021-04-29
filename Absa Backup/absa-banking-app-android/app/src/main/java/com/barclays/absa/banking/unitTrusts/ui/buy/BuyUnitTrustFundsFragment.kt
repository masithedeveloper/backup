/*
 * Copyright (c) 2018 Absa Bank Limited, All Rights Reserved.
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

package com.barclays.absa.banking.unitTrusts.ui.buy

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.barclays.absa.banking.R
import com.barclays.absa.banking.newToBank.services.dto.ResultAnimations
import com.barclays.absa.banking.unitTrusts.services.dto.UnitTrustFund
import kotlinx.android.synthetic.main.buy_unit_trust_funds_fragment.*
import styleguide.screens.GenericResultScreenFragment
import styleguide.screens.GenericResultScreenProperties
import styleguide.utils.extensions.toSentenceCase

class BuyUnitTrustFundsFragment : BuyUnitTrustBaseFragment(R.layout.buy_unit_trust_funds_fragment) {

    private lateinit var unitTrustFundsAdapter: UnitTrustFundsAdapter
    private val fundsSearchViewTextWatcher = FundsSearchViewTextWatcher()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val toolbarTitleId = if (buyUnitTrustViewModel.isBuyNewFund)
            R.string.unit_trust_buy_new_funds_title else R.string.unit_trust_funds_title
        setToolBar(toolbarTitleId)

        hostActivity.showToolbar()
        buyUnitTrustViewModel.predefinedOrderedFilterOptions = resources.getStringArray(R.array.unitTrustFilterOptions)
        buyUnitTrustViewModel.splitter = getString(R.string.unit_trust_risk_funds_splitter)

        attachDataObservers()
        attachEventHandlers()
        setUpFundsRecyclerView()
        buyUnitTrustViewModel.initializeWithFunds()

        if (buyUnitTrustViewModel.isBuyNewFund) {
            hostActivity.trackEvent("UTBuyNewFund_FundsScreen_AllFundsDisplayed")
        } else {
            hostActivity.trackCurrentFragment("WIMI_UT_BuyNew_SeeFunds")
        }
    }

    private fun setUpFundsRecyclerView() {
        unitTrustFundsAdapter = UnitTrustFundsAdapter(hostActivity as Context, this)
        fundsRecyclerView.apply {
            adapter = unitTrustFundsAdapter
            layoutManager = LinearLayoutManager(hostActivity)
            setHasFixedSize(true)
        }
    }

    private fun attachDataObservers() {
        buyUnitTrustViewModel.selectedFilterOption.observe(this, {
            fundsSearchView.apply {
                removeSearchTextWatcher(fundsSearchViewTextWatcher)
                searchedString = it.second
                addSearchTextWatcher(fundsSearchViewTextWatcher)
                fundsSearchView.clearFocus()
            }
        })

        buyUnitTrustViewModel.filteredFundsLiveData.observe(this, { funds ->
            funds.let {
                unitTrustFundsAdapter.update(funds)

                if (buyUnitTrustViewModel.isFirstDataLoaded) {
                    buyUnitTrustViewModel.swapObservableDataHolder()
                }
            }
            dismissProgressDialog()
        })

        buyUnitTrustViewModel.isFetchFundsSuccessLiveData.observe(this, { hasFunds ->
            dismissProgressDialog()
            if (!hasFunds) {
                navigateToFailureScreen()
            }
        })
    }

    private fun navigateToFailureScreen() {
        hideToolBar()
        val resultScreenProperties = GenericResultScreenProperties.PropertiesBuilder()
                .setResultScreenAnimation(ResultAnimations.generalFailure)
                .setTitle(getString(R.string.unable_to_continue).toSentenceCase())
                .setDescription(getString(R.string.please_call_unit_trust_call_centre))
                .setPrimaryButtonLabel(getString(R.string.close))
                .build(false)

        GenericResultScreenFragment.setPrimaryButtonOnClick { hostActivity.finish() }

        val bundle = Bundle()
        bundle.putSerializable(GenericResultScreenFragment.GENERIC_RESULT_PROPERTIES_KEY, resultScreenProperties)

        findNavController().navigate(R.id.action_buyUnitTrustFundsFragment_to_genericResultScreenFragment, bundle)
    }

    private fun attachEventHandlers() {
        fundsSearchView.setOnFiltersClickListener {
            navigate(BuyUnitTrustFundsFragmentDirections.showPicker())
        }
    }

    fun onFundListItemSelected(unitTrustFund: UnitTrustFund) {
        buyUnitTrustViewModel.unitTrustAccountInfo.unitTrustFund = unitTrustFund
        navigate(BuyUnitTrustFundsFragmentDirections.actionBuyUnitTrustFundsFragmentToBuyUnitTrustFundInformation())
    }

    inner class FundsSearchViewTextWatcher : TextWatcher {
        override fun afterTextChanged(searchText: Editable?) {
            if (!searchText.isNullOrEmpty()) {
                unitTrustFundsAdapter.search(searchText.toString())
            }
        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
    }
}
