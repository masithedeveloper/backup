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
package com.barclays.absa.banking.unitTrusts.ui.switchAndRedeem

import android.os.Bundle
import android.view.View
import com.barclays.absa.banking.R
import com.barclays.absa.banking.framework.app.BMBConstants.NO
import com.barclays.absa.banking.framework.app.BMBConstants.YES
import com.barclays.absa.banking.unitTrusts.services.dto.BuyMoreUnitsLinkedAccount
import com.barclays.absa.banking.unitTrusts.services.dto.SwitchUnitTrustFund
import com.barclays.absa.utils.PdfUtil
import kotlinx.android.synthetic.main.switch_fund_details_fragment.*
import styleguide.forms.SelectorList
import styleguide.forms.StringItem
import styleguide.forms.validation.addRequiredValidationHidingTextWatcher
import styleguide.utils.extensions.toTitleCase

class SwitchFundDetailFragment : SwitchFundBaseFragment(R.layout.switch_fund_details_fragment) {

    private lateinit var listOfAccounts: List<BuyMoreUnitsLinkedAccount>
    private lateinit var listOfFunds: List<SwitchUnitTrustFund>

    companion object {
        private const val SELLING_THRESHOLD = 0.95
        const val RAND_VALUE = "A"
        const val NUMBER_OF_UNITS = "U"
        const val ALL_UNITS = "P"
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setToolBar(R.string.switch_header)
        setUpProgressIndicator()
        buildRadioButtons()
        setupObservers()
        setUpUI()
        viewUnitTrustViewModel.fetchUnitTrustFunds()
    }

    private fun buildRadioButtons() {
        investmentOptionsRadioButton.setDataSource(SelectorList<StringItem>().apply {
            add(StringItem(getString(R.string.buy_unit_trust_tax_reinvest_into_fund)))
            add(StringItem(getString(R.string.buy_unit_trust_tax_pay_into_my_account)))
        }, 0)
    }

    private fun setUpProgressIndicator() {
        hostActivity.showProgressIndicator()
        hostActivity.progressIndicatorStep(1)
    }

    private fun buildSwitchTypeOptions(): SelectorList<StringItem> {
        val switchOptions = resources.getStringArray(R.array.redemptionTypeOptions)
        val switchTypeOptions = SelectorList<StringItem>()
        switchOptions.mapTo(switchTypeOptions) { option -> StringItem(option) }
        return switchTypeOptions
    }

    private fun setUpUI() {
        val switchTypeAllUnits = getString(R.string.switch_redemption_type_all_units)

        switchValueInputView.showDescription(false)

        viewUnitTrustViewModel.unitTrustSwitchAccountData.value?.unitTrustFund?.apply {
            selectedFundTitleAndDescriptionView.title = fundName.toTitleCase()
            availableBalanceLineItemView.setLineItemViewContent(fundAvailableBalance)
            availableUnitsLineItemView.setLineItemViewContent(fundAvailablelUnits)
        }

        switchOptionsInputView.setList(buildSwitchTypeOptions(), getString(R.string.switch_fund_redemption_type))

        accountOptionsInputView.setItemSelectionInterface({
            setSwitchAccountDetails(it)
        })

        switchToFundInputView.setItemSelectionInterface({
            listOfFunds[it].apply {
                viewUnitTrustViewModel.unitTrustSwitchAccountData.value?.apply {
                    toFundCode = fundCode
                    toFundName = fundName
                }
                viewUnitTrustViewModel.switchToFundPdfUrl = fundPdfUrl
            }
        })

        minimumDisclosureDocumentButtonView.setOnClickListener {
            viewUnitTrustViewModel.switchToFundPdfUrl.let { switchToFundPdfUrl -> PdfUtil.showPDFInApp(hostActivity, switchToFundPdfUrl) }
        }

        newDebitOrderForFundCheckBox.setOnCheckedListener { isChecked ->
            viewUnitTrustViewModel.unitTrustSwitchAccountData.value?.newDebitOrder = if (isChecked) YES else NO
        }

        investmentOptionsRadioButton.setItemCheckedInterface({
            viewUnitTrustViewModel.unitTrustSwitchAccountData.value?.apply {
                if (it == 0) {
                    incomeDistributionIndicator = "N"
                    accountOptionsInputView.visibility = View.GONE
                } else {
                    incomeDistributionIndicator = "Y"
                    accountOptionsInputView.visibility = View.VISIBLE
                }
            }
        })

        switchOptionsInputView.addRequiredValidationHidingTextWatcher {
            if (it == switchTypeAllUnits) {
                setSwitchDetails("0.00", switchTypeAllUnits)
            } else {
                switchValueInputView.visibility = View.VISIBLE
            }
        }

        switchToFundInputView.addRequiredValidationHidingTextWatcher {
            minimumDisclosureDocumentButtonView.visibility = View.VISIBLE
        }

        nextButton.setOnClickListener {
            validateFields()
        }

        switchValueInputView.addRequiredValidationHidingTextWatcher { redemptionValue ->
            viewUnitTrustViewModel.unitTrustSwitchAccountData.value?.unitTrustFund?.let {
                val switchOptionsInputView = switchOptionsInputView.selectedValue
                val fundAvailableUnits = it.fundAvailablelUnits
                val fundAvailableBalance = it.fundAvailableBalance
                val switchTypeNumberOfUnits = getString(R.string.switch_redemption_value_number_of_units)
                val switchTypeRandValue = getString(R.string.switch_redemption_type_rand_value)
                if (!fundAvailableBalance.isNullOrEmpty() && !fundAvailableUnits.isNullOrEmpty()) {
                    var availableValue = 0.0
                    when (switchOptionsInputView) {
                        switchTypeNumberOfUnits -> availableValue = fundAvailableUnits.toDouble()
                        switchTypeRandValue -> availableValue = fundAvailableBalance.toDouble()
                    }

                    val isRedemptionValueGreaterThanSellingThreshold = redemptionValue.toDouble() > availableValue.times(SELLING_THRESHOLD)
                    switchValueInputView.showDescription(isRedemptionValueGreaterThanSellingThreshold)
                    if (redemptionValue.toDouble() > availableValue) {
                        switchValueInputView.setError(getString(R.string.redeem_fund_redemption_value_error))
                    } else {
                        switchValueInputView.clearError()
                    }
                }
            }
        }
    }

    private fun validateFields() {
        viewUnitTrustViewModel.unitTrustSwitchAccountData.value?.unitTrustFund?.apply {
            val switchFund = switchToFundInputView.selectedValue
            val switchType = switchOptionsInputView.selectedValue
            val switchValue = switchValueInputView.selectedValue
            val fundAvailableUnits = fundAvailablelUnits
            val fundAvailableBalance = fundAvailableBalance
            val switchTypeNumberOfUnits = getString(R.string.switch_redemption_value_number_of_units)
            val switchTypeRandValue = getString(R.string.switch_redemption_type_rand_value)
            val switchTypeAllUnits = getString(R.string.switch_redemption_type_all_units)
            val newDebitOrder = viewUnitTrustViewModel.unitTrustSwitchAccountData.value?.newDebitOrder ?: ""

            if (!fundAvailableBalance.isNullOrEmpty() && !fundAvailableUnits.isNullOrEmpty()) {
                when {
                    switchType == switchTypeAllUnits -> {
                        navigateOnNewDebitOrder(newDebitOrder)
                    }
                    switchFund.isEmpty() -> switchToFundInputView.setError(getString(R.string.switch_fund_switch_to_fund_required))
                    switchType.isEmpty() -> switchOptionsInputView.setError(getString(R.string.switch_fund_switch_type_required))
                    switchValue.isEmpty() -> if (switchType != switchTypeAllUnits) switchValueInputView.setError(getString(R.string.redeem_fund_redemption_value_required))
                    switchType == switchTypeNumberOfUnits -> {
                        if (!switchValueInputView.hasError()) {
                            setSwitchDetails(switchValue, switchType)
                            navigateOnNewDebitOrder(newDebitOrder)
                        }
                    }

                    switchType == switchTypeRandValue -> {
                        if (switchValue.toDouble() > fundAvailableBalance.toDouble()) {
                            switchValueInputView.setError(getString(R.string.redeem_fund_redemption_value_error))
                        } else {
                            setSwitchDetails(switchValue, switchType)
                            navigateOnNewDebitOrder(newDebitOrder)
                        }
                    }
                }
            }
        }
    }

    private fun navigateOnNewDebitOrder(newDebitOrder: String) {
        if (newDebitOrder == YES) {
            navigate(SwitchFundDetailFragmentDirections.actionSwitchFundDetailFragmentToSwitchFundDebitOrderDetailsFragment())
        } else {
            navigate(SwitchFundDetailFragmentDirections.actionSwitchFundDetailFragmentToSwitchFundSummaryFragment())
        }
    }

    private fun setupObservers() {
        viewUnitTrustViewModel.switchUnitTrustFundsLiveData.observe(hostActivity, {
            val selectedFundCode = viewUnitTrustViewModel.unitTrustSwitchAccountData.value?.unitTrustFund?.fundCode
            val selectorSourceList = SelectorList<SwitchUnitTrustFund>()
            val removeFundsRegex = "$selectedFundCode|FUN|FUB|NAA|NAB|NAC|BMM".toRegex()
            val unitTrustFundList = it.unitTrustFundList.filter { fund ->
                !removeFundsRegex.matches(fund.fundCode)
            }

            listOfFunds = unitTrustFundList
            selectorSourceList.addAll(unitTrustFundList)
            switchToFundInputView.setList(selectorSourceList, getString(R.string.switch_select_fund).toTitleCase())
            viewUnitTrustViewModel.fetchLinkedAccounts()
        })

        viewUnitTrustViewModel.buyMoreUnitsLinkedAccountsLiveData.observe(this, {
            val selectorSourceList = SelectorList<BuyMoreUnitsLinkedAccount>()
            it?.buyMoreUnitsLinkedAccounts?.accounts?.let { sourceOfFunds ->
                listOfAccounts = sourceOfFunds
                selectorSourceList.addAll(sourceOfFunds)
            }
            populateSource(selectorSourceList)
        })
    }

    private fun isPercentageGreaterThanBalance(amount: String, switchType: String): Boolean {
        var validAmount = false
        val switchTypeRandValue = getString(R.string.switch_redemption_type_rand_value)
        viewUnitTrustViewModel.unitTrustSwitchAccountData.value?.unitTrustFund?.apply {
            val fundAvailableUnits = fundAvailablelUnits
            val fundAvailableBalance = fundAvailableBalance

            if (!fundAvailableBalance.isNullOrEmpty() && switchType == switchTypeRandValue) {
                validAmount = fundAvailableBalance.toDouble().times(SELLING_THRESHOLD) > amount.toDouble()
            } else {
                fundAvailableUnits?.let {
                    validAmount = fundAvailableUnits.toDouble().times(SELLING_THRESHOLD) > amount.toDouble()
                }
            }
        }

        return validAmount
    }

    private fun setSwitchDetails(switchValue: String, switchType: String) {
        viewUnitTrustViewModel.unitTrustSwitchAccountData.value?.apply {
            val fundAvailableUnits = unitTrustFund.fundAvailablelUnits
            val fundAvailableBalance = unitTrustFund.fundAvailableBalance
            val switchTypeRandValue = getString(R.string.switch_redemption_type_rand_value)
            val switchTypeNumberOfUnits = getString(R.string.switch_redemption_value_number_of_units)

            if (!fundAvailableBalance.isNullOrEmpty() && !fundAvailableUnits.isNullOrEmpty()) {
                when (switchType) {
                    switchTypeRandValue -> {
                        switchOption = if (isPercentageGreaterThanBalance(switchValue, switchType)) RAND_VALUE else ALL_UNITS
                        unitsToSwitch = if (isPercentageGreaterThanBalance(switchValue, switchType)) switchValue else "100"
                    }
                    switchTypeNumberOfUnits -> {
                        switchOption = if (isPercentageGreaterThanBalance(switchValue, switchType)) NUMBER_OF_UNITS else ALL_UNITS
                        unitsToSwitch = if (isPercentageGreaterThanBalance(switchValue, switchType)) switchValue else "100"
                    }
                    else -> {
                        switchValueInputView.visibility = View.GONE
                        switchValueInputView.selectedValue = ""
                        switchOption = ALL_UNITS
                        unitsToSwitch = "100"
                    }
                }

                fromFundCode = unitTrustFund.fundCode.orEmpty()
                fromFundName = unitTrustFund.fundName.orEmpty()
            }
        }
    }

    private fun populateSource(sourceOfFunds: SelectorList<BuyMoreUnitsLinkedAccount>) {
        accountOptionsInputView.setList(sourceOfFunds, getString(R.string.account_to_debit))

        if (listOfAccounts.size == 1) {
            accountOptionsInputView.selectedIndex = 0
            setSwitchAccountDetails(0)
        }

        dismissProgressDialog()
    }

    private fun setSwitchAccountDetails(selectedAccountIndex: Int) {
        listOfAccounts[selectedAccountIndex].apply {
            viewUnitTrustViewModel.unitTrustSwitchAccountData.value?.apply {
                incomeDistributionBankAccount = accountNumber.orEmpty()
                incomeDistributionAccountType = accountType.orEmpty()
                incomeDistributionAccountName = accountHolderName.orEmpty()
                incomeDistributionBankname = bankName.orEmpty()
                incomeDistributionBankCode = branchCode.orEmpty()
            }
        }
    }
}