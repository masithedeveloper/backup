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
package com.barclays.absa.banking.unitTrusts.ui.view

import android.content.Context
import android.os.Bundle
import android.view.View
import com.barclays.absa.banking.R
import com.barclays.absa.banking.boundary.model.Amount
import com.barclays.absa.banking.databinding.ViewUnitTrustBuyMoreUnitStepOneFragmentBinding
import com.barclays.absa.banking.framework.AbsaBaseFragment
import com.barclays.absa.banking.shared.DigitalLimitState
import com.barclays.absa.banking.shared.DigitalLimitsHelper
import com.barclays.absa.banking.unitTrusts.services.dto.BuyMoreUnitsLinkedAccount
import com.barclays.absa.banking.unitTrusts.ui.buy.BuyUnitTrustInvestmentMethodFragment
import com.barclays.absa.banking.unitTrusts.ui.view.ViewUnitTrustHostActivity.Companion.BUY_MORE_UNITS_CHANNEL
import com.barclays.absa.utils.AnalyticsUtil
import com.barclays.absa.utils.viewModel
import styleguide.forms.SelectorList
import styleguide.utils.extensions.removeStringCurrencyDefaultZero
import styleguide.utils.extensions.toTitleCase

class ViewUnitTrustBuyMoreUnitStepOneFragment : AbsaBaseFragment<ViewUnitTrustBuyMoreUnitStepOneFragmentBinding>() {

    private lateinit var viewUnitTrustViewModel: ViewUnitTrustViewModel
    private lateinit var listOfAccounts: List<BuyMoreUnitsLinkedAccount>
    private lateinit var hostActivity: ViewUnitTrustHostActivity

    override fun getLayoutResourceId(): Int = R.layout.view_unit_trust_buy_more_unit_step_one_fragment

    override fun onAttach(context: Context) {
        super.onAttach(context)
        hostActivity = context as ViewUnitTrustHostActivity
        viewUnitTrustViewModel = hostActivity.viewModel()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        AnalyticsUtil.trackAction(BUY_MORE_UNITS_CHANNEL, "WIMI_UT_MoreUnits_Step1")
        setUpToolbar()
        binding.selectedFundTitleAndDescriptionView.title = viewUnitTrustViewModel.buyMoreUnitsData.value?.fund?.fundName.toTitleCase()
        viewUnitTrustViewModel.fetchLinkedAccounts()

        binding.accountInputView.setItemSelectionInterface {
            fetchSelectedFund(it)
        }

        setUpObservers()

        binding.nextButton.setOnClickListener {
            if (isValidData()) {
                viewUnitTrustViewModel.buyMoreUnitsData.value?.buyMoreUnitsLumpSumInfo?.amount = binding.amountInputView.selectedValue.removeStringCurrencyDefaultZero()
                viewUnitTrustViewModel.fetchLumpSumStatus()
            }
        }
    }

    private fun isValidData(): Boolean {
        val selectedAmountUnmasked = binding.amountInputView.selectedValueUnmasked
        val selectedAccountUnmasked = binding.accountInputView.selectedValueUnmasked
        when {
            selectedAccountUnmasked.isEmpty() -> binding.accountInputView.setError(getString(R.string.is_required, getString(R.string.buy_more_unit_select_account)))
            selectedAmountUnmasked.isEmpty() -> binding.amountInputView.setError(getString(R.string.is_required, getString(R.string.amount)))
            selectedAmountUnmasked.toDouble() == 0.0 -> binding.amountInputView.setError(getString(R.string.min_amount_error_message, MIN_AMOUNT))
            else -> return true
        }

        return false
    }

    private fun setUpToolbar() {
        hostActivity.apply {
            setToolBar(getString(R.string.view_unit_trust_buy_details))
            showProgressIndicator()
            setStep(1)
        }
    }

    private fun fetchSelectedFund(selectedIndex: Int) {
        val selectedAccounts: BuyMoreUnitsLinkedAccount
        if (!listOfAccounts.isNullOrEmpty()) {
            selectedAccounts = listOfAccounts[selectedIndex]
            buildLumpSumModel(selectedAccounts)
        }
    }

    private fun setUpObservers() {
        viewUnitTrustViewModel.buyMoreUnitsLinkedAccountsLiveData.observe(viewLifecycleOwner, {

            val selectorSourceList = SelectorList<BuyMoreUnitsLinkedAccount>()
            it?.buyMoreUnitsLinkedAccounts?.accounts?.let { sourceOfFunds ->

                listOfAccounts = sourceOfFunds.filter { linkedAccount ->
                    var accountTypeToCheck = ""
                    linkedAccount.accountType?.let { accountTypeToCheck = it }
                    !accountTypeToCheck.contains("absareward", true)
                }

                listOfAccounts.forEach { linkedAccount ->
                    selectorSourceList.add(linkedAccount)
                }
            }
            populateSource(selectorSourceList)
        })

        viewUnitTrustViewModel.buyMoreUnitsValidLumpSumLiveData.observe(viewLifecycleOwner, { validationStatus ->
            if (validationStatus?.status == true) {
                navigate(ViewUnitTrustBuyMoreUnitStepOneFragmentDirections.actionViewUnitTrustBuyMoreUnitStepOneFragmentToViewUnitTrustBuyMoreUnitStepTwoFragment())
                viewUnitTrustViewModel.resetBuyMoreUnitsValidLumpSumLiveData()
                viewUnitTrustViewModel.buyMoreUnitsValidLumpSumLiveData.removeObservers(hostActivity)
                dismissProgressDialog()
            } else if (validationStatus?.status == false && validationStatus.transactionMessage == "Payment limit reached") {
                performDigitalLimitUpdate()
            } else {
                binding.amountInputView.setError(R.string.buy_unit_trust_insufficient_fund)
                dismissProgressDialog()
            }
        })
    }

    private fun buildLumpSumModel(selectedAccount: BuyMoreUnitsLinkedAccount) {
        viewUnitTrustViewModel.buyMoreUnitsData.value?.buyMoreUnitsLumpSumInfo?.accountInfo = selectedAccount
        viewUnitTrustViewModel.buyMoreUnitsData.value?.buyMoreUnitsLumpSumInfo?.indicator = BuyUnitTrustInvestmentMethodFragment.YES_INDICATOR
    }

    private fun populateSource(sourceOfFunds: SelectorList<BuyMoreUnitsLinkedAccount>?) {
        binding.accountInputView.setList(sourceOfFunds, getString(R.string.unit_trust_select_account))

        if (::listOfAccounts.isInitialized && listOfAccounts.size == 1) {
            binding.accountInputView.selectedIndex = 0
            binding.accountInputView.selectedValue = listOfAccounts[0].displayValue.toString()
            fetchSelectedFund(0)
        }

        dismissProgressDialog()
    }

    private fun performDigitalLimitUpdate() {
        DigitalLimitsHelper.checkPaymentAmount(hostActivity, Amount(viewUnitTrustViewModel.buyMoreUnitsData.value?.buyMoreUnitsLumpSumInfo?.amount.toString()), false)

        DigitalLimitsHelper.digitalLimitState.observe(viewLifecycleOwner, { digitalLimitState ->
            dismissProgressDialog()
            if (digitalLimitState == DigitalLimitState.CHANGED || digitalLimitState == DigitalLimitState.UNCHANGED) {
                viewUnitTrustViewModel.fetchLumpSumStatus()
                DigitalLimitsHelper.digitalLimitState.removeObservers(this)
            }
        })
    }

    companion object {
        const val MIN_AMOUNT = 1.0
        const val MAX_AMOUNT = 999999999999.0
    }
}