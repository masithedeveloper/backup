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

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.barclays.absa.banking.framework.dagger.getServiceInterface
import com.barclays.absa.banking.home.ui.IHomeCacheService
import com.barclays.absa.banking.shared.services.dto.LookupItem
import com.barclays.absa.banking.unitTrusts.services.UnitTrustInteractor
import com.barclays.absa.banking.unitTrusts.services.UnitTrustService
import com.barclays.absa.banking.unitTrusts.services.dto.*
import com.barclays.absa.banking.unitTrusts.ui.ListSorter
import com.barclays.absa.banking.unitTrusts.ui.view.BuyNewFundResponseListener
import styleguide.forms.SelectorList
import styleguide.forms.StringItem

class BuyUnitTrustViewModel : ViewModel() {
    lateinit var unitTrustData: UnitTrustData
    var isBuyNewFund = false
    var isFirstDataLoaded = false
    var selectedUnitTrustAccountIndex = -1
    private var defaultFilterSelected = -1
    private var unitTrustAccountNumber = ""
    var employeeOccupationStatus = ""
    lateinit var listOfAccounts: List<LinkedAccount>
    var minDebitOrderAmount = 0.0
    var minLumpSumAmount = 0.0
    var currentOccupation = ""
    var unitTrustAccountInfo = UnitTrustAccountInfo()
    private val filterOptions = arrayListOf<String>()
    var linkedAccountsLiveData = MutableLiveData<List<LinkedAccount>>()
    var debitDaysLiveData = MutableLiveData<List<String>>()
    var endDebitDateLiveData = MutableLiveData<String>()
    var sourceOfFunds = MutableLiveData<List<LookupItem>>()
    var buyUnitTrustResultResponse = MutableLiveData<UnitTrustAccountCreationResult>()
    var lumpSumAmountValidationLiveData = MutableLiveData<Boolean>()
    val buyMoreFundsResponseLiveData = MutableLiveData<BuyMoreFundsResponse>()
    val isFetchFundsSuccessLiveData = MutableLiveData<Boolean>()
    val selectedFilterOption by lazy { MutableLiveData<Pair<Int, String>>() }

    var accountsSelectorList: SelectorList<StringItem>
    lateinit var predefinedOrderedFilterOptions: Array<String>
    lateinit var splitter: String

    val unitTrustFundsLiveData by lazy { MutableLiveData<List<UnitTrustFund>>() }
    private val unitTrustService: UnitTrustService by lazy { UnitTrustInteractor() }
    private val unitTrustFundsExtendedResponseListener by lazy { UnitTrustFundsExtendedResponseListener(this) }
    private val unitTrustFundsAvailableToBuyExtendedResponseListener by lazy { UnitTrustFundsAvailableToBuyExtendedResponseListener(this) }
    private val lumpSumAmountValidationExtendedResponseListener by lazy { LumpSumValidationExtendedResponseListener(this) }
    private val linkedAccountsExtendedResponseListener by lazy { LinkedAccountsExtendedResponseListener(this) }
    private val debitPeriodExtendedResponseListener by lazy { DebitPeriodExtendedResponseListener(this) }
    private val unitTrustAccountApplicationExtendedResponseListener by lazy { UnitTrustAccountApplicationExtendedResponseListener(this) }
    private val buyNewFundsLinkedAccountsExtendedResponseListener by lazy { BuyNewFundLinkedAccountsExtendedResponseListener(this) }
    private val buyNewFundExtendedResponseListener by lazy { BuyNewFundResponseListener(this) }

    private fun hasCoreFunds(funds: List<UnitTrustFund>) = funds.find { it.fundCore.equals("Y") } != null

    private val homeCacheService: IHomeCacheService = getServiceInterface()

    init {
        accountsSelectorList = createUnitTrustAccountsSelectorList()
    }

    var filteredFundsLiveData: LiveData<List<UnitTrustFund>> = Transformations.switchMap(unitTrustFundsLiveData) {
        val sortedFunds = ListSorter.sortUnitTrustFunds(it)
        createFilterSelectorList()

        when {
            hasCoreFunds(it) -> filterFundsByCriteria(sortedFunds, filterOptions[defaultFilterSelected])
            else -> filterFundsByCriteria(sortedFunds, selectedFilterOption.value?.second)
        }
    }

    fun createFilterOptions(fundFilterOptions: ArrayList<String>) {
        filterOptions.clear()
        if (isBuyNewFund && filterOptions.isNotEmpty()) {
            setUpDynamicFilterOptions(fundFilterOptions)
        } else {
            setUpStaticFilterOptions()
        }
    }

    fun setUpStaticFilterOptions() {
        filterOptions.addAll(predefinedOrderedFilterOptions)
    }

    private fun setUpDynamicFilterOptions(fundFilterOptions: ArrayList<String>) {
        filterOptions.addAll(fundFilterOptions)
    }

    private fun filterFundsByCriteria(allFunds: List<UnitTrustFund>, key: String?): LiveData<List<UnitTrustFund>> {
        val fundsLiveData by lazy { MutableLiveData<List<UnitTrustFund>>() }
        key?.let {
            fundsLiveData.value = when (key) {
                filterOptions[0] -> allFunds
                filterOptions[1] -> allFunds.filter { it.fundCore.equals("Y", ignoreCase = true) } as ArrayList<UnitTrustFund>
                else -> allFunds.filter { it.fundRisk.equals(key.removeSuffix(splitter).trimEnd(), ignoreCase = true) } as ArrayList<UnitTrustFund>
            }
        }
        return fundsLiveData
    }

    fun swapObservableDataHolder() {
        filteredFundsLiveData = Transformations.switchMap(selectedFilterOption) {
            unitTrustFundsLiveData.value?.run {
                filterFundsByCriteria(this, it.second)
            }
        }
    }

    private fun loadUnitTrustFunds() {
        if (isBuyNewFund) {
            unitTrustService.fetchUnitTrustFundsAvailableToBuy(unitTrustAccountNumber, unitTrustFundsAvailableToBuyExtendedResponseListener)
        } else {
            unitTrustService.fetchUnitTrustFunds(unitTrustFundsExtendedResponseListener)
        }
    }

    fun isAccountsExceedingOne(): Boolean = (homeCacheService.getUnitTrustResponseModel()?.unitTrustAccounts?.size ?: 0) > 1

    fun selectFirstUnitTrustAccountNumber() {
        unitTrustAccountNumber = homeCacheService.getUnitTrustResponseModel()?.unitTrustAccounts?.firstOrNull()?.accountNumber ?: ""
        unitTrustAccountInfo.unitTrustAccountNumber = unitTrustAccountNumber
    }

    fun createFilterSelectorList(): SelectorList<StringItem> {
        val filterOptionsSelectorList = SelectorList<StringItem>()
        for (option in filterOptions) {
            filterOptionsSelectorList.add(StringItem(option))
        }
        return filterOptionsSelectorList
    }

    private fun createUnitTrustAccountsSelectorList(): SelectorList<StringItem> {
        val selectorItems = SelectorList<StringItem>()
        homeCacheService.getUnitTrustResponseModel()?.unitTrustAccounts?.let { accounts ->
            accounts.forEach { account ->
                selectorItems.add(StringItem(account.accountNumber))
            }
        }

        return selectorItems
    }

    fun onUnitTrustAccountSelected(index: Int) {
        selectedUnitTrustAccountIndex = index
        unitTrustAccountNumber = accountsSelectorList[index].item!!
        unitTrustAccountInfo.unitTrustAccountNumber = unitTrustAccountNumber
    }

    fun performLumpSumValidation() {
        unitTrustAccountInfo.lumpSumInfo?.let {
            unitTrustService.validateLumpSumAmount(it, lumpSumAmountValidationExtendedResponseListener)
        }
    }

    fun loadLinkedAccounts() {
        if (isBuyNewFund) {
            unitTrustService.fetchBuyMoreUnitsLinkedAccounts(buyNewFundsLinkedAccountsExtendedResponseListener)
        } else {
            unitTrustService.fetchLinkedAccounts(linkedAccountsExtendedResponseListener)
        }
    }

    fun loadDebitDays() {
        unitTrustService.fetchDebitDays(debitPeriodExtendedResponseListener)
    }

    fun initializeWithFunds() {
        if (!isFirstDataLoaded) {
            defaultFilterSelected = if (isBuyNewFund) 0 else 1
            selectedFilterOption.value = Pair(defaultFilterSelected, predefinedOrderedFilterOptions[defaultFilterSelected])
            loadUnitTrustFunds()
            isFirstDataLoaded = true
        }
    }

    fun performUnitTrustAccountApplication() {
        unitTrustService.createUnitTrustAccount(unitTrustAccountInfo, unitTrustAccountApplicationExtendedResponseListener)
    }

    fun buyNewFund() {
        unitTrustService.buyNewFund(unitTrustAccountInfo, buyNewFundExtendedResponseListener)
    }
}