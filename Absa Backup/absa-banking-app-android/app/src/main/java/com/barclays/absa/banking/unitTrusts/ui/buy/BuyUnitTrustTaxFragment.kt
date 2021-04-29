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
import android.view.View
import com.barclays.absa.banking.R
import com.barclays.absa.banking.framework.app.BMBApplication
import com.barclays.absa.banking.shared.services.SharedViewModel
import com.barclays.absa.banking.shared.services.dto.CIFGroupCode
import com.barclays.absa.banking.shared.services.dto.LookupItem
import com.barclays.absa.banking.unitTrusts.services.dto.LinkedAccount
import com.barclays.absa.banking.unitTrusts.services.dto.TaxInfo
import com.barclays.absa.banking.unitTrusts.services.dto.UnitTrustFund
import com.barclays.absa.utils.viewModel
import kotlinx.android.synthetic.main.buy_unit_trust_tax_fragment.*
import styleguide.forms.SelectorList
import styleguide.forms.StringItem
import styleguide.utils.extensions.toTitleCase

class BuyUnitTrustTaxFragment : BuyUnitTrustBaseFragment(R.layout.buy_unit_trust_tax_fragment) {

    private lateinit var sharedViewModel: SharedViewModel
    private var isPayIntoMyAccountSelected = false

    override fun onAttach(context: Context) {
        super.onAttach(context)
        sharedViewModel = hostActivity.viewModel()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        hostActivity.trackCurrentFragment("WIMI_UT_BuyNew_Step2_MoneyBack")
        setToolBar(getString(R.string.buy_unit_trust_invest_in_fund).toTitleCase())
        if (buyUnitTrustViewModel.isBuyNewFund) {
            hostActivity.trackEvent("UTBuyNewFund_MoneyBackScreen_ScreenDisplayed")
        } else {
            hostActivity.trackCurrentFragment("WIMI_UT_BuyNew_Step2_MoneyBack")
        }

        buildRadioButtons()
        initViews()

        val unitTrustFund = buyUnitTrustViewModel.unitTrustAccountInfo.unitTrustFund
        fundTypesTextView.title = unitTrustFund.fundName

        nextButton.setOnClickListener { onNextButtonClicked(unitTrustFund) }

        buyUnitTrustViewModel.linkedAccountsLiveData.value?.let {
            val selectorAccountList = SelectorList<LinkedAccount>().apply { addAll(it) }
            populateAccounts(selectorAccountList)
        }

        sharedViewModel.codesLiveData.observe(hostActivity, {

            val selectorSourceList = SelectorList<LookupItem>()

            it?.items?.let { items ->
                for (item: LookupItem in items.slice(2..items.lastIndex)) {
                    selectorSourceList.add(item)
                }
                dismissProgressDialog()
                populateCountryList(selectorSourceList)
            }
        })

        if (buyUnitTrustViewModel.isBuyNewFund) {
            dismissProgressDialog()
        } else {
            sharedViewModel.getCIFCodes(CIFGroupCode.COUNTRY_PASSPORT)
        }
    }

    private fun onNextButtonClicked(unitTrustFund: UnitTrustFund) {
        if (isAllFieldsValid()) {
            buyUnitTrustViewModel.unitTrustAccountInfo.incomeFromInvestment = whatToDoRadioButton.selectedValue?.displayValue
                    ?: ""
            buildModel()
            navigate(BuyUnitTrustTaxFragmentDirections.actionBuyUnitTrustTaxFragmentToBuyUnitTrustSummaryFragment(unitTrustFund))
        }
    }

    private fun isAllFieldsValid(): Boolean {
        when {
            isPayIntoMyAccountSelected
                    && intoAccountInputView.selectedValue.isEmpty() -> intoAccountInputView.setError(getString(R.string.account_to_pay_to))
            registeredForTaxRadioButton.selectedIndex == 1
                    && registeredForForeignTaxRadioButton.selectedIndex == 0
                    && foreignTaxCountryInputView.selectedValue.isEmpty() -> foreignTaxCountryInputView.setError(getString(R.string.select_country))
            else -> return true
        }

        return false
    }

    private fun buildModel() {
        val taxInfo = TaxInfo()
        if (registeredForTaxRadioButton.selectedIndex == 0) {
            taxInfo.isRegisteredForSATax = YES
            taxInfo.isRegisteredForForeignTax = NO

            if (saTaxNumberInputView.selectedValue.isNotEmpty()) {
                taxInfo.saTaxNumber = saTaxNumberInputView.selectedValue
            } else {
                taxInfo.reasonNotGivenForSATax = TAX_NUMBER_NOT_GIVEN_REASON
            }
        } else {
            taxInfo.isRegisteredForSATax = NO
            if (registeredForForeignTaxRadioButton.selectedIndex == 0) {
                taxInfo.isRegisteredForForeignTax = YES
                if (foreignTaxNumberInputView.selectedValue.isNotEmpty()) {
                    taxInfo.foreignTaxNumber = foreignTaxNumberInputView.selectedValue
                } else {
                    taxInfo.reasonNotGivenForForeignTax = TAX_NUMBER_NOT_GIVEN_REASON
                }
                taxInfo.foreignTaxCountry = foreignTaxCountryInputView.selectedValue
            } else {
                taxInfo.isRegisteredForForeignTax = NO
            }
        }

        buyUnitTrustViewModel.unitTrustAccountInfo.taxInfo = taxInfo

        val unitTrustInformation = buyUnitTrustViewModel.unitTrustAccountInfo
        val debitOrderInfo = unitTrustInformation.debitOrderInfo
        val lumpSum = unitTrustInformation.lumpSumInfo

        if (isPayIntoMyAccountSelected) {
            if (debitOrderInfo != null) {
                unitTrustInformation.incomeDistributionAccountInfo = unitTrustInformation.buildIncomeDistributionFormDebitOrder(debitOrderInfo, INVEST_INTO_ACCOUNT, buyUnitTrustViewModel.isBuyNewFund)
            } else if (lumpSum != null) {
                unitTrustInformation.incomeDistributionAccountInfo = unitTrustInformation.buildIncomeDistributionFormLumpSum(lumpSum, INVEST_INTO_ACCOUNT, buyUnitTrustViewModel.isBuyNewFund)
            }
        } else {
            if (debitOrderInfo != null) {
                unitTrustInformation.incomeDistributionAccountInfo = unitTrustInformation.buildIncomeDistributionFormDebitOrder(debitOrderInfo, REINVEST, buyUnitTrustViewModel.isBuyNewFund)
            } else if (lumpSum != null) {
                unitTrustInformation.incomeDistributionAccountInfo = unitTrustInformation.buildIncomeDistributionFormLumpSum(lumpSum, REINVEST, buyUnitTrustViewModel.isBuyNewFund)
            }
        }
        unitTrustInformation.redemptionAccountInfo = unitTrustInformation.buildRedeemAccountInformation()
    }

    private fun initViews() {
        hostActivity.setCurrentProgress(2)
        intoAccountInputView.setDescription(getString(R.string.buy_unit_trust_tax_into_account_description, AUTOMATIC_INVESTMENT_AMOUNT))
        whatToDoRadioButton.setItemCheckedInterface {
            isPayIntoMyAccountSelected = whatToDoRadioButton.selectedValue?.displayValue ?: "" == getString(R.string.buy_unit_trust_tax_pay_into_my_account)
            intoAccountInputView.visibility = if (isPayIntoMyAccountSelected) View.VISIBLE else View.GONE
        }

        if (buyUnitTrustViewModel.isBuyNewFund) {
            registeredForTaxTextView.visibility = View.GONE
            registeredForTaxRadioButton.visibility = View.GONE
            saTaxNumberInputView.visibility = View.GONE
            intoAccountInputView.visibility = View.GONE
        }

        registeredForTaxRadioButton.setItemCheckedInterface {
            if (it == 0) {
                registeredForForeignTaxRadioButton.selectedIndex = 1
                saTaxNumberInputView.visibility = View.VISIBLE
                registeredForForeignTaxTextView.visibility = View.GONE
                registeredForForeignTaxRadioButton.visibility = View.GONE
                foreignTaxNumberInputView.visibility = View.GONE
                foreignTaxCountryInputView.visibility = View.GONE
            } else {
                saTaxNumberInputView.visibility = View.GONE
                registeredForForeignTaxTextView.visibility = View.VISIBLE
                registeredForForeignTaxRadioButton.visibility = View.VISIBLE
                foreignTaxNumberInputView.visibility = View.VISIBLE
                foreignTaxCountryInputView.visibility = View.VISIBLE

            }
        }

        registeredForForeignTaxRadioButton.setItemCheckedInterface {
            if (it == 0) {
                foreignTaxNumberInputView.visibility = View.VISIBLE
                foreignTaxCountryInputView.visibility = View.VISIBLE
            } else {
                foreignTaxNumberInputView.visibility = View.GONE
                foreignTaxCountryInputView.visibility = View.GONE
            }
        }
    }

    private fun buildRadioButtons() {
        val gainIncomeMethodSelectorList = SelectorList<StringItem>().apply {
            add(StringItem(getString(R.string.buy_unit_trust_tax_pay_into_my_account)))
            add(StringItem(getString(R.string.buy_unit_trust_tax_reinvest_into_fund)))
        }
        isPayIntoMyAccountSelected = !buyUnitTrustViewModel.isBuyNewFund
        if (buyUnitTrustViewModel.isBuyNewFund) {
            gainIncomeMethodSelectorList.reverse()
        }

        whatToDoRadioButton.setDataSource(gainIncomeMethodSelectorList, 0)

        val yesNoSelectorList = SelectorList<StringItem>().apply {
            add(StringItem(getString(R.string.yes)))
            add(StringItem(getString(R.string.no)))
        }
        registeredForTaxRadioButton.setDataSource(yesNoSelectorList, 0)
        registeredForForeignTaxRadioButton.setDataSource(yesNoSelectorList, 0)
    }

    private fun populateAccounts(linkedAccounts: SelectorList<LinkedAccount>) {
        intoAccountInputView.setList(linkedAccounts, BMBApplication.getInstance().getString(R.string.select_account_toolbar_title))
        if (linkedAccounts.size == 1) {
            intoAccountInputView.selectedIndex = 0
            intoAccountInputView.selectedValue = linkedAccounts[0].displayValue.toString()
        }
        intoAccountInputView.setCustomOnClickListener(null)
    }

    private fun populateCountryList(linkedAccounts: SelectorList<LookupItem>) {
        foreignTaxCountryInputView.setList(linkedAccounts, BMBApplication.getInstance().getString(R.string.select_country))
        foreignTaxCountryInputView.setCustomOnClickListener(null)
    }

    companion object {
        const val AUTOMATIC_INVESTMENT_AMOUNT = "R 50.00"
        const val TAX_NUMBER_NOT_GIVEN_REASON = "4"
        const val YES = "Yes"
        const val NO = "No"
        const val INVEST_INTO_ACCOUNT = "I"
        const val REINVEST = "R"
    }
}