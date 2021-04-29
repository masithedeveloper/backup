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

import android.content.Context
import android.os.Bundle
import android.view.View
import com.barclays.absa.banking.R
import com.barclays.absa.banking.unitTrusts.services.dto.BuyMoreUnitsLinkedAccount
import com.barclays.absa.banking.unitTrusts.ui.buy.BuyUnitTrustViewModel
import com.barclays.absa.utils.TextFormatUtils
import com.barclays.absa.utils.viewModel
import kotlinx.android.synthetic.main.switch_fund_debit_order_details_fragment.*
import styleguide.forms.SelectorList
import styleguide.forms.StringItem
import styleguide.utils.extensions.removeCurrency
import styleguide.utils.extensions.toTwoDigitDay

class SwitchFundDebitOrderDetailsFragment : SwitchFundBaseFragment(R.layout.switch_fund_debit_order_details_fragment) {

    private lateinit var buyUnitTrustViewModel: BuyUnitTrustViewModel
    private lateinit var listOfAccounts: List<BuyMoreUnitsLinkedAccount>

    override fun onAttach(context: Context) {
        super.onAttach(context)
        buyUnitTrustViewModel = hostActivity.viewModel()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setToolBar(R.string.switch_debit_order_details_header)
        setupObservers()
        retrieveAccountInformation()
        populateDebitOrderIncrease()
        setUpUI()
        buyUnitTrustViewModel.loadDebitDays()
    }

    private fun populateSource(sourceOfFunds: SelectorList<BuyMoreUnitsLinkedAccount>) {
        debitAccountInputView.setList(sourceOfFunds, getString(R.string.account_to_debit))

        if (listOfAccounts.size == 1) {
            debitAccountInputView.selectedIndex = 0
            setDebitOrderAccountDetails(0)
        }
    }

    private fun retrieveAccountInformation() {
        val selectorSourceList = SelectorList<BuyMoreUnitsLinkedAccount>()
        viewUnitTrustViewModel.buyMoreUnitsLinkedAccountsLiveData.value?.buyMoreUnitsLinkedAccounts?.accounts?.let { linkedAccounts ->
            listOfAccounts = linkedAccounts
            selectorSourceList.addAll(linkedAccounts)
        }
        populateSource(selectorSourceList)
    }

    private fun setupObservers() {
        buyUnitTrustViewModel.debitDaysLiveData.apply {
            removeObservers(hostActivity)
            observe(hostActivity, {
                val selectorDebitDayList = SelectorList<StringItem>()
                it?.forEach { item -> selectorDebitDayList.add(StringItem(item)) }

                populateDebitDays(selectorDebitDayList)
                dismissProgressDialog()
            })
        }
    }

    private fun populateDebitOrderIncrease() {
        val debitOrderIncreaseOptions = SelectorList<StringItem>().apply {
            add(StringItem("0%"))
            add(StringItem("10%"))
            add(StringItem("15%"))
            add(StringItem("20%"))
        }
        increaseDebitAmountInputView.setList(debitOrderIncreaseOptions, getString(R.string.unit_trust_debit_order_title))
        increaseDebitAmountInputView.selectedIndex = 1
    }

    private fun populateDebitDays(debitDays: SelectorList<StringItem>) {
        debitDayInputView.setList(debitDays, getString(R.string.debit_day))
        debitDayInputView.selectedIndex = 0
    }

    private fun setUpUI() {
        val validMinimumAmount = viewUnitTrustViewModel.unitTrustSwitchAccountData.value?.unitTrustFund?.minDebitOrderAmount
        debitAmountInputView.setHintText(getString(R.string.switch_debit_min_amount, TextFormatUtils.formatBasicAmount(validMinimumAmount)))

        debitAccountInputView.setItemSelectionInterface {
            setDebitOrderAccountDetails(it)
        }

        nextButton.setOnClickListener { validateFields() }
    }

    private fun validateFields() {
        val debitAccountValue = debitAccountInputView.selectedValue
        val debitAmountValue = debitAmountInputView.selectedValue.removeCurrency()
        val validMinimumAmount = viewUnitTrustViewModel.unitTrustSwitchAccountData.value?.unitTrustFund?.minDebitOrderAmount

        if (!validMinimumAmount.isNullOrEmpty()) {
            when {
                debitAccountValue.isEmpty() -> debitAccountInputView.setError(getString(R.string.switch_fund_switch_type_required))
                debitAmountValue.isEmpty() -> debitAmountInputView.setError(getString(R.string.switch_fund_debit_amount_required, TextFormatUtils.formatBasicAmount(validMinimumAmount)))
                debitAmountValue.toDouble() < validMinimumAmount.toDouble() -> debitAmountInputView.setError(getString(R.string.switch_debit_min_amount_error_message, TextFormatUtils.formatBasicAmount(validMinimumAmount)))
                else -> setDebitOrderDetails()
            }
        } else {
            hostActivity.showGenericErrorMessageThenFinish()
        }
    }

    private fun setDebitOrderDetails() {
        val endDebitDate = buyUnitTrustViewModel.endDebitDateLiveData.value
        viewUnitTrustViewModel.unitTrustSwitchAccountData.value?.apply {
            debitOrderOperation = "Add"
            debitOrderAmount = debitAmountInputView.selectedValue.removeCurrency()
            debitOrderDay = debitDayInputView.selectedValue
            debitOrderPercentageIncrease = increaseDebitAmountInputView.selectedValue.dropLast(1)
            debitOrderIncreaseEffectiveDate = "$endDebitDate${debitOrderDay.toTwoDigitDay()}"
            endDebitDate?.dropLast(1)?.takeLast(2)?.let {
                debitOrderStartMonth = it
            }
        }
        navigate(SwitchFundDebitOrderDetailsFragmentDirections.actionSwitchFundDebitOrderDetailsFragmentToSwitchFundSummaryFragment())
    }

    private fun setDebitOrderAccountDetails(selectedAccountIndex: Int) {
        listOfAccounts[selectedAccountIndex].apply {
            viewUnitTrustViewModel.unitTrustSwitchAccountData.value?.apply {
                debitOrderAccount = accountNumber.orEmpty()
                debitOrderAccountType = accountType.orEmpty()
                debitOrderAccountName = accountHolderName.orEmpty()
                debitOrderBank = bankName.orEmpty()
                debitOrderBankCode = branchCode.orEmpty()
            }
        }
    }
}