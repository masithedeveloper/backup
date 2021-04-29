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
import androidx.lifecycle.MutableLiveData
import com.barclays.absa.banking.R
import com.barclays.absa.banking.boundary.model.Amount
import com.barclays.absa.banking.manage.profile.ui.OccupationStatus
import com.barclays.absa.banking.presentation.shared.IntentFactory
import com.barclays.absa.banking.riskBasedApproach.services.dto.RiskBasedApproachViewModel
import com.barclays.absa.banking.riskBasedApproach.services.dto.RiskProfileDetails
import com.barclays.absa.banking.shared.DigitalLimitState
import com.barclays.absa.banking.shared.DigitalLimitsHelper
import com.barclays.absa.banking.shared.services.SharedViewModel
import com.barclays.absa.banking.shared.services.dto.CIFGroupCode
import com.barclays.absa.banking.shared.services.dto.LookupItem
import com.barclays.absa.banking.unitTrusts.services.dto.DebitOrderInfo
import com.barclays.absa.banking.unitTrusts.services.dto.LinkedAccount
import com.barclays.absa.banking.unitTrusts.services.dto.LumpSumInfo
import com.barclays.absa.utils.TextFormatUtils
import com.barclays.absa.utils.viewModel
import kotlinx.android.synthetic.main.buy_unit_trust_investment_method_fragment.*
import styleguide.forms.NormalInputView
import styleguide.forms.SelectorList
import styleguide.forms.StringItem
import styleguide.forms.validation.addRequiredValidationHidingTextWatcher
import styleguide.utils.extensions.removeCurrency
import styleguide.utils.extensions.toTitleCase
import styleguide.utils.extensions.toTwoDigitDay
import java.math.BigDecimal

class BuyUnitTrustInvestmentMethodFragment : BuyUnitTrustBaseFragment(R.layout.buy_unit_trust_investment_method_fragment) {

    private lateinit var riskBasedApproachViewModel: RiskBasedApproachViewModel
    private lateinit var sharedViewModel: SharedViewModel

    override fun onAttach(context: Context) {
        super.onAttach(context)
        with(hostActivity) {
            riskBasedApproachViewModel = viewModel()
            sharedViewModel = viewModel()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setToolBar(R.string.buy_unit_trust_details)

        if (buyUnitTrustViewModel.isBuyNewFund) {
            hostActivity.trackEvent("UTBuyNewFund_InvestMethodScreen_ScreenDisplayed")
        } else {
            hostActivity.trackCurrentFragment("WIMI_UT_BuyNew_Step1_InvestMethod")
        }

        initView()
        setUpObservers()
        attachEventHandlers()
        populateDebitOrderIncrease()
        buyUnitTrustViewModel.loadLinkedAccounts()
    }

    private fun attachEventHandlers() {
        nextButton.setOnClickListener {
            if (investmentMethodRadioButton.selectedIndex == 0 && isValidDebit() && isValidRbaInput()) {
                if (buyUnitTrustViewModel.isBuyNewFund) {
                    navigateToUnitTrustTaxScreen()
                } else {
                    getRiskLevel()
                }
            } else if (investmentMethodRadioButton.selectedIndex == 1 && isValidLumpSum() && isValidRbaInput()) {
                if (buyUnitTrustViewModel.listOfAccounts.isNotEmpty() && accountInputView.selectedIndex >= 0) {
                    buildLumpSumModel(buyUnitTrustViewModel.listOfAccounts[accountInputView.selectedIndex])
                    if (buyUnitTrustViewModel.isBuyNewFund) {
                        checkDigitalLimitsForUnitTrust()
                    } else {
                        getRiskLevel()
                    }
                }
            }
        }
    }

    private fun isValidRbaInput(): Boolean {
        when {
            buyUnitTrustViewModel.isBuyNewFund -> return true
            employeeStatusInputView.selectedValue.isEmpty() -> employeeStatusInputView.setError(R.string.risk_based_approach_employment_status_error_message)
            occupationInputView.visibility == View.VISIBLE && occupationInputView.selectedValue.isEmpty() -> occupationInputView.setError(R.string.risk_based_approach_occupation_error_message)
            sourceOfFundsInputView.selectedValue.isEmpty() -> sourceOfFundsInputView.setError(R.string.risk_based_approach_source_of_funds_error_message)
            else -> return true
        }
        return false
    }

    private fun isValidDebit(): Boolean {
        when {
            accountInputView.selectedValueUnmasked.isEmpty() -> accountInputView.setError(getString(R.string.please_select_account))
            amountInputView.selectedValueUnmasked.isNotEmpty() && amountInputView.selectedValueUnmasked.toBigDecimal() > MAX_INVESTMENT_VALUE -> amountInputView.setError(getString(R.string.buy_unit_trust_investment_method_max_amount, "R$MAX_INVESTMENT_VALUE"))
            amountInputView.selectedValueUnmasked.isEmpty() || amountInputView.selectedValueUnmasked.toDouble() < buyUnitTrustViewModel.minDebitOrderAmount -> amountInputView.setError(getString(R.string.buy_unit_trust_investment_method_min_amount, TextFormatUtils.formatBasicAmount(buyUnitTrustViewModel.minDebitOrderAmount)))
            else -> {
                return true
            }
        }
        return false
    }

    private fun isValidLumpSum(): Boolean {
        when {
            accountInputView.selectedValueUnmasked.isEmpty() -> accountInputView.setError(getString(R.string.please_select_account))
            amountInputView.selectedValueUnmasked.isNotEmpty() && amountInputView.selectedValueUnmasked.toBigDecimal() > MAX_INVESTMENT_VALUE -> amountInputView.setError(getString(R.string.buy_unit_trust_investment_method_max_amount_lumps_sum, "R$MAX_INVESTMENT_VALUE"))
            amountInputView.selectedValueUnmasked.isEmpty() || amountInputView.selectedValueUnmasked.toDouble() < buyUnitTrustViewModel.minLumpSumAmount -> amountInputView.setError(getString(R.string.buy_unit_trust_investment_method_min_amount_lump_sum, TextFormatUtils.formatBasicAmount(buyUnitTrustViewModel.minLumpSumAmount)))
            else -> {
                return true
            }
        }
        return false
    }

    private fun navigateToUnitTrustTaxScreen() {
        buildInvestmentMethodObjects()
        navigate(BuyUnitTrustInvestmentMethodFragmentDirections.actionBuyUnitTrustInvestmentMethodFragmentToBuyUnitTrustTaxFragment())
    }

    @Suppress("UNCHECKED_CAST")
    private fun getRiskLevel() {
        riskBasedApproachViewModel.riskProfileResponse = MutableLiveData()
        riskBasedApproachViewModel.riskProfileResponse.observe(this, {
            if ("VH".equals(it?.riskRating, true) || "H".equals(it?.riskRating, true)) {
                startActivity(IntentFactory.getUnableToContinueScreen(hostActivity, R.string.unable_to_continue, R.string.buy_unit_trust_unable_to_continue_message_rba))
            } else {
                if (investmentMethodRadioButton.selectedIndex == 0) {
                    navigateToUnitTrustTaxScreen()
                } else {
                    checkDigitalLimitsForUnitTrust()
                }
            }
        })

        val employmentStatus = (employeeStatusInputView as NormalInputView<LookupItem>).selectedItem?.itemCode
        val sourceOfFunds = (sourceOfFundsInputView as NormalInputView<LookupItem>).selectedItem?.itemCode

        var occupation: String? = null
        if (occupationInputView.visibility == View.VISIBLE) {
            occupation = (occupationInputView as NormalInputView<LookupItem>).selectedItem?.itemCode
        }
        if (occupation == null) {
            occupation = ""
        }
        if (employmentStatus != null && sourceOfFunds != null) {
            val riskProfileDetails = RiskProfileDetails("0", occupation, employmentStatus, PRODUCT_CODE, PRODUCT_CODE, SBU, sourceOfFunds)
            riskBasedApproachViewModel.fetchRiskProfile(riskProfileDetails)
        } else {
            showGenericErrorMessageThenFinish()
        }
    }

    private fun buildInvestmentMethodObjects() {
        var selectedAccounts = LinkedAccount()
        val unitTrustAccountInfo = buyUnitTrustViewModel.unitTrustAccountInfo
        if (buyUnitTrustViewModel.listOfAccounts.isNotEmpty() && accountInputView.selectedIndex >= 0) {
            selectedAccounts = buyUnitTrustViewModel.listOfAccounts[accountInputView.selectedIndex]
        }

        if (investmentMethodRadioButton.selectedIndex == 0) {
            unitTrustAccountInfo.debitOrderInfo = DebitOrderInfo().apply {
                accountInfo = selectedAccounts
                amount = amountInputView.selectedValue.removeCurrency()
                debitDate = buyUnitTrustViewModel.endDebitDateLiveData.value + debitDayInputView.selectedValue.toTwoDigitDay()
                debitDay = debitDayInputView.selectedValue
                autoIncreasePercentage = increaseYearlyInputView.selectedValue.replace("%", "")
                indicator = YES_INDICATOR
            }
            unitTrustAccountInfo.lumpSumInfo = null
        } else {
            buildLumpSumModel(selectedAccounts)
        }

        unitTrustAccountInfo.apply {
            sourceOfFunds = buyUnitTrustViewModel.sourceOfFunds.value?.get(sourceOfFundsInputView.selectedIndex)
            employmentStatus = employeeStatusInputView.selectedValue
            occupation = occupationInputView.selectedValue
        }
    }

    private fun buildLumpSumModel(selectedAccounts: LinkedAccount) {
        val unitTrustAccountInfo = buyUnitTrustViewModel.unitTrustAccountInfo
        unitTrustAccountInfo.lumpSumInfo = LumpSumInfo().apply {
            accountInfo = selectedAccounts
            amount = amountInputView.selectedValue.removeCurrency()
            indicator = YES_INDICATOR
        }
        unitTrustAccountInfo.debitOrderInfo = null
    }

    private fun populateDebitAccounts(linkedAccounts: SelectorList<LinkedAccount>) {
        accountInputView.setList(linkedAccounts, getString(R.string.buy_unit_trust_summary_debit_account))
        if (linkedAccounts.size == 1) {
            accountInputView.selectedIndex = 0
            accountInputView.selectedValue = linkedAccounts[0].displayValue.toString()
        }
        accountInputView.setCustomOnClickListener(null)
    }

    private fun populateDebitDays(debitDays: SelectorList<StringItem>) {
        debitDayInputView.setList(debitDays, getString(R.string.debit_day))
        debitDayInputView.setCustomOnClickListener(null)
        debitDayInputView.selectedIndex = 0
    }

    private fun populateSource(sourceOfFunds: SelectorList<LookupItem>, defaultIndex: Int) {
        sourceOfFundsInputView.setList(sourceOfFunds, getString(R.string.source_of_funds))
        sourceOfFundsInputView.selectedIndex = defaultIndex
        sourceOfFundsInputView.setCustomOnClickListener(null)
    }

    private fun populateDebitOrderIncrease() {
        val debitOrderIncreaseOptions = SelectorList<StringItem>().apply {
            add(StringItem("0%"))
            add(StringItem("10%"))
            add(StringItem("15%"))
            add(StringItem("20%"))
        }
        increaseYearlyInputView.setList(debitOrderIncreaseOptions, getString(R.string.unit_trust_debit_order_title))
        increaseYearlyInputView.setCustomOnClickListener(null)
        increaseYearlyInputView.selectedIndex = 1
    }

    private fun initView() {
        hostActivity.setCurrentProgress(1)
        val investmentMethodSelectorList = SelectorList<StringItem>().apply {
            add(StringItem(getString(R.string.buy_unit_trust_investment_method_debit_order)))
            add(StringItem(getString(R.string.buy_unit_trust_investment_method_lump_sum)))
        }

        val unitTrustFund = buyUnitTrustViewModel.unitTrustAccountInfo.unitTrustFund
        fundTypesTextView.title = unitTrustFund.fundName
        unitTrustFund.minDebitOrderAmount?.let {
            buyUnitTrustViewModel.minDebitOrderAmount = it.toDouble()
        }
        unitTrustFund.minLumpSumAmount?.let {
            buyUnitTrustViewModel.minLumpSumAmount = it.toDouble()
        }

        amountInputView.addRequiredValidationHidingTextWatcher()

        investmentMethodRadioButton.setDataSource(investmentMethodSelectorList, 0)
        investmentMethodRadioButton.setItemCheckedInterface {
            amountInputView.clearDescription()
            accountInputView.clearDescription()
            amountInputView.clearError()
            accountInputView.clearError()
            if (it == 1) {
                increaseYearlyInputView.visibility = View.GONE
                debitDayInputView.visibility = View.GONE
                amountInputView.setHintText(getString(R.string.buy_unit_trust_investment_method_minimum_amount_hint, String.format("R %s", TextFormatUtils.formatBasicAmount(buyUnitTrustViewModel.minLumpSumAmount))))
            } else {
                accountInputView.setDescription(getString(R.string.buy_unit_trust_debit_description))
                increaseYearlyInputView.visibility = View.VISIBLE
                debitDayInputView.visibility = View.VISIBLE
                amountInputView.setHintText(getString(R.string.buy_unit_trust_investment_method_minimum_amount_hint, String.format("R %s", TextFormatUtils.formatBasicAmount(buyUnitTrustViewModel.minDebitOrderAmount))))
            }
        }
        amountInputView.setHintText(getString(R.string.buy_unit_trust_investment_method_minimum_amount_hint, String.format("R %s", TextFormatUtils.formatBasicAmount(buyUnitTrustViewModel.minDebitOrderAmount))))

        if (buyUnitTrustViewModel.isBuyNewFund) {
            employeeStatusInputView.visibility = View.GONE
            occupationInputView.visibility = View.GONE
            sourceOfFundsInputView.visibility = View.GONE
        }
    }

    private fun populateEmploymentStatus(employmentStatus: SelectorList<LookupItem>, selectedIndex: Int) {
        employeeStatusInputView.setList(employmentStatus, getString(R.string.risk_based_approach_employment_status))
        employeeStatusInputView.selectedIndex = selectedIndex

        setOccupationVisibility(employmentStatus[selectedIndex].itemCode.toString())

        employeeStatusInputView.setItemSelectionInterface {
            setOccupationVisibility(employmentStatus[it].itemCode.toString())
        }
    }

    private fun populateOccupation(occupation: SelectorList<LookupItem>, selectedIndex: Int) {
        occupationInputView.setList(occupation, getString(R.string.buy_unit_trust_occupation_header))
        occupationInputView.selectedIndex = selectedIndex
        dismissProgressDialog()
    }

    private fun setOccupationVisibility(occupationItemCode: String) {
        val hideOccupationList = listOf(OccupationStatus.UNEMPLOYED, OccupationStatus.PENSIONER, OccupationStatus.SCHOLAR, OccupationStatus.STUDENT, OccupationStatus.HOUSEWIFE)
        if (hideOccupationList.any { it.occupationCode == occupationItemCode }) {
            occupationInputView.selectedValue = ""
            occupationInputView.visibility = View.GONE
        } else {
            occupationInputView.visibility = View.VISIBLE
        }
    }

    private fun checkDigitalLimitsForUnitTrust() {
        buyUnitTrustViewModel.unitTrustAccountInfo.lumpSumInfo?.amount?.let {
            DigitalLimitsHelper.checkPaymentAmount(hostActivity, Amount(it), false)

            DigitalLimitsHelper.digitalLimitState.observe(this, { digitalLimitState ->
                dismissProgressDialog()
                if (digitalLimitState == DigitalLimitState.CHANGED || digitalLimitState == DigitalLimitState.UNCHANGED) {
                    buyUnitTrustViewModel.performLumpSumValidation()
                    DigitalLimitsHelper.digitalLimitState.removeObservers(this)
                }
            })
        }
    }

    private fun removeObservers() {
        buyUnitTrustViewModel.linkedAccountsLiveData.removeObservers(hostActivity)
        buyUnitTrustViewModel.debitDaysLiveData.removeObservers(hostActivity)
        buyUnitTrustViewModel.sourceOfFunds.removeObservers(hostActivity)
        buyUnitTrustViewModel.lumpSumAmountValidationLiveData.removeObservers(hostActivity)
        riskBasedApproachViewModel.personalInformationResponse.removeObservers(hostActivity)
        riskBasedApproachViewModel.riskProfileResponse.removeObservers(hostActivity)
        sharedViewModel.codesLiveData.removeObservers(hostActivity)
        sharedViewModel.sourceOfFundsResponse.removeObservers(hostActivity)
    }

    private fun resetViewModelFields() {
        buyUnitTrustViewModel.linkedAccountsLiveData = MutableLiveData()
        buyUnitTrustViewModel.debitDaysLiveData = MutableLiveData()
        sharedViewModel.codesLiveData = MutableLiveData()
        buyUnitTrustViewModel.sourceOfFunds = MutableLiveData()
        riskBasedApproachViewModel.personalInformationResponse = MutableLiveData()
        buyUnitTrustViewModel.lumpSumAmountValidationLiveData = MutableLiveData()
        riskBasedApproachViewModel.riskProfileResponse = MutableLiveData()
    }

    private fun setUpObservers() {
        removeObservers()
        resetViewModelFields()

        buyUnitTrustViewModel.linkedAccountsLiveData.observe(hostActivity, {
            val selectorAccountList = SelectorList<LinkedAccount>()
            it?.let { linkedAccounts ->
                buyUnitTrustViewModel.listOfAccounts = linkedAccounts
                for (item: LinkedAccount in buyUnitTrustViewModel.listOfAccounts) {
                    selectorAccountList.add(item)
                }
            }

            populateDebitAccounts(selectorAccountList)
            buyUnitTrustViewModel.loadDebitDays()
        })

        buyUnitTrustViewModel.debitDaysLiveData.observe(hostActivity, { debitDays ->
            val selectorDebitDayList = SelectorList<StringItem>().apply {
                addAll(debitDays.map { debitDay -> StringItem(debitDay) })
            }

            populateDebitDays(selectorDebitDayList)
            if (buyUnitTrustViewModel.isBuyNewFund) {
                dismissProgressDialog()
            } else {
                sharedViewModel.getCIFCodes(CIFGroupCode.SOURCE_OF_FUNDS)
            }
        })

        sharedViewModel.codesLiveData.observe(this, {

            val selectorSourceList = SelectorList<LookupItem>()
            it?.items?.let { items ->
                for (item: LookupItem in items) {
                    item.defaultLabel = item.defaultLabel.toTitleCase()
                    selectorSourceList.add(item)
                }
                selectorSourceList.sortBy { item -> item.defaultLabel }

                when (items[0].groupCode) {
                    CIFGroupCode.OCCUPATION_STATUS.key -> {
                        populateEmploymentStatus(selectorSourceList, sharedViewModel.getMatchingLookupIndex(buyUnitTrustViewModel.employeeOccupationStatus, selectorSourceList))
                        sharedViewModel.getCIFCodes(CIFGroupCode.OCCUPATION)
                    }
                    CIFGroupCode.OCCUPATION.key -> populateOccupation(selectorSourceList, sharedViewModel.getMatchingLookupIndex(buyUnitTrustViewModel.currentOccupation, selectorSourceList))
                    CIFGroupCode.SOURCE_OF_FUNDS.key -> {
                        populateSource(selectorSourceList, sharedViewModel.getMatchingLookupIndex("20", selectorSourceList))
                        buyUnitTrustViewModel.sourceOfFunds.value = selectorSourceList
                        riskBasedApproachViewModel.fetchPersonalInformation()
                    }
                }
            }
        })

        riskBasedApproachViewModel.personalInformationResponse.observe(this, {
            it?.customerInformation?.employmentInformation?.occupationStatus?.let { occupationStatus ->
                buyUnitTrustViewModel.employeeOccupationStatus = occupationStatus
                sharedViewModel.getCIFCodes(CIFGroupCode.OCCUPATION_STATUS)
            }

            it?.customerInformation?.employmentInformation?.occupation?.let { occupation ->
                buyUnitTrustViewModel.currentOccupation = occupation
            }
        })

        buyUnitTrustViewModel.lumpSumAmountValidationLiveData.observe(hostActivity, {
            dismissProgressDialog()
            it?.let { validateLumpSum ->
                if (validateLumpSum) {
                    navigateToUnitTrustTaxScreen()
                    buyUnitTrustViewModel.lumpSumAmountValidationLiveData.removeObservers(hostActivity)
                } else {
                    amountInputView.setError(R.string.buy_unit_trust_insufficient_fund)
                }
                buyUnitTrustViewModel.lumpSumAmountValidationLiveData.removeObservers(hostActivity)
            }
        })

        riskBasedApproachViewModel.riskProfileResponse.observe(this, {
            if ("VH".equals(it?.riskRating, true) || "H".equals(it?.riskRating, true)) {
                dismissProgressDialog()
                startActivity(IntentFactory.getUnableToContinueScreen(hostActivity, R.string.unable_to_continue, R.string.buy_unit_trust_unable_to_continue_message_rba))
            } else {
                if (investmentMethodRadioButton.selectedIndex == 0) {
                    dismissProgressDialog()
                    navigateToUnitTrustTaxScreen()
                } else {
                    checkDigitalLimitsForUnitTrust()
                }
            }
        })
    }

    companion object {
        const val YES_INDICATOR = "Y"
        val MAX_INVESTMENT_VALUE: BigDecimal = BigDecimal.valueOf(99999999999.99)
        const val PRODUCT_CODE = "UNITS"
        const val SBU = "AFM"
    }
}