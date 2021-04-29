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
package com.barclays.absa.banking.home.ui

import com.barclays.absa.DaggerTest
import com.barclays.absa.banking.boundary.model.AccountDetail
import com.barclays.absa.banking.boundary.model.AccountObject
import com.barclays.absa.banking.boundary.model.Entry
import com.barclays.absa.banking.boundary.model.policy.Policy
import com.barclays.absa.banking.framework.app.BMBApplication
import com.barclays.absa.banking.lotto.services.SourceAccount
import com.barclays.absa.banking.moneyMarket.service.dto.OrbitAccount
import com.barclays.absa.banking.personalLoan.services.CreditLimitsResponse
import com.barclays.absa.banking.unitTrusts.services.dto.UnitTrustAccountsWrapper
import org.junit.Before
import org.junit.Test
import org.junit.jupiter.api.Assertions.*

class HomeCacheServiceTest : DaggerTest() {

    private lateinit var homeCacheService: IHomeCacheService
    private val defaultValueChangedMessage = "Default value changed"
    private val valueUpdateFailedMessage = "Value not updated successfully"

    @Before
    fun setup() {
        homeCacheService = BMBApplication.applicationComponent.getHomeCacheService()
    }

    @Test
    fun getHomeLoanAccountHistoryCleared() {
        val accountDetail = AccountDetail()
        assertNull(homeCacheService.getHomeLoanAccountHistoryCleared()) { defaultValueChangedMessage }
        homeCacheService.setHomeLoanAccountHistoryCleared(accountDetail)
        assertTrue(homeCacheService.getHomeLoanAccountHistoryCleared() == accountDetail) { valueUpdateFailedMessage }
    }

    @Test
    fun getHomeLoanAccountHistoryUncleared() {
        val accountDetail = AccountDetail()
        assertNull(homeCacheService.getHomeLoanAccountHistoryUncleared()) { defaultValueChangedMessage }
        homeCacheService.setHomeLoanAccountHistoryUncleared(accountDetail)
        assertTrue(homeCacheService.getHomeLoanAccountHistoryUncleared() == accountDetail) { valueUpdateFailedMessage }
    }

    @Test
    fun getInsurancePolicies() {
        val insurancePolicies : List<Policy> = listOf(Policy())
        assertTrue(homeCacheService.getInsurancePolicies().isEmpty()) { defaultValueChangedMessage }
        homeCacheService.setInsurancePolicies(insurancePolicies)
        assertTrue(homeCacheService.getInsurancePolicies() == insurancePolicies) { valueUpdateFailedMessage }
    }

    @Test
    fun hasFuneralCover() {
        assertFalse(homeCacheService.hasFuneralCover()) { defaultValueChangedMessage }
        homeCacheService.setHasFuneralCover(true)
        assertTrue(homeCacheService.hasFuneralCover()) { valueUpdateFailedMessage }
    }

    @Test
    fun hasAuthorizations() {
        assertFalse(homeCacheService.hasAuthorizations()) { defaultValueChangedMessage }
        homeCacheService.setHasAuthorizations(true)
        assertTrue(homeCacheService.hasAuthorizations()) { valueUpdateFailedMessage }
    }

    @Test
    fun getNumberOfPendingMandates() {
        assertTrue(homeCacheService.getNumberOfPendingMandates() == 0) { defaultValueChangedMessage }
        homeCacheService.setNumberOfPendingMandates(10)
        assertTrue(homeCacheService.getNumberOfPendingMandates() == 10) { valueUpdateFailedMessage }
    }

    @Test
    fun getFilteredHomeAccounts() {
        val entryList : List<Entry> = listOf(object :Entry {
            override fun getEntryType(): Int = 0
        })
        assertTrue(homeCacheService.getFilteredHomeAccounts().isEmpty()) { defaultValueChangedMessage }
        homeCacheService.setFilteredAccounts(entryList)
        assertTrue(homeCacheService.getFilteredHomeAccounts() == entryList) { valueUpdateFailedMessage }
    }

    @Test
    fun getSavingsAndInvestmentsAccounts() {
        val entryList : List<Entry> = listOf(object :Entry {
            override fun getEntryType(): Int = 0
        })
        assertTrue(homeCacheService.getSavingsAndInvestmentsAccounts().isEmpty()) { defaultValueChangedMessage }
        homeCacheService.setSavingsAndInvestmentsAccounts(entryList)
        assertTrue(homeCacheService.getSavingsAndInvestmentsAccounts() == entryList) { valueUpdateFailedMessage }
    }

    @Test
    fun getSelectedHomeLoanAccount() {
        val accountObject = AccountObject()
        assertNull(homeCacheService.getSelectedHomeLoanAccount()) { defaultValueChangedMessage }
        homeCacheService.setSelectedHomeLoanAccount(accountObject)
        assertTrue(homeCacheService.getSelectedHomeLoanAccount() == accountObject) { valueUpdateFailedMessage }
    }

    @Test
    fun getUnitTrustAccountObject() {
        val accountObject = AccountObject()
        assertNull(homeCacheService.getUnitTrustAccountObject()) { defaultValueChangedMessage }
        homeCacheService.setUnitTrustAccountObject(accountObject)
        assertTrue(homeCacheService.getUnitTrustAccountObject() == accountObject) { valueUpdateFailedMessage }
    }

    @Test
    fun getUnitTrustResponseModel() {
        val unitTrustAccountsWrapper = UnitTrustAccountsWrapper()
        assertNull(homeCacheService.getUnitTrustResponseModel()) { defaultValueChangedMessage }
        homeCacheService.setUnitTrustResponseModel(unitTrustAccountsWrapper)
        assertTrue(homeCacheService.getUnitTrustResponseModel() == unitTrustAccountsWrapper) { valueUpdateFailedMessage }
    }

    @Test
    fun getPersonalLoanResponse() {
        val creditLimitsResponse = CreditLimitsResponse()
        assertNull(homeCacheService.getPersonalLoanResponse()) { defaultValueChangedMessage }
        homeCacheService.setPersonalLoanResponse(creditLimitsResponse)
        assertTrue(homeCacheService.getPersonalLoanResponse() == creditLimitsResponse) { valueUpdateFailedMessage }
    }

    @Test
    fun getLottoSourceAccountList() {
        val sourceAccountList = listOf(SourceAccount())
        assertTrue(homeCacheService.getLottoSourceAccountList().isEmpty()) { defaultValueChangedMessage }
        homeCacheService.setLottoSourceAccountList(sourceAccountList)
        assertTrue(homeCacheService.getLottoSourceAccountList() == sourceAccountList) { valueUpdateFailedMessage }
    }

    @Test
    fun getHomeLoanPerilsAccount() {
        val accountObject = AccountObject()
        assertNull(homeCacheService.getHomeLoanPerilsAccount()) { defaultValueChangedMessage }
        homeCacheService.setHomeLoanPerilsAccount(accountObject)
        assertTrue(homeCacheService.getHomeLoanPerilsAccount() == accountObject) { valueUpdateFailedMessage }
    }

    @Test
    fun getMoneyMarketStatusList() {
        val orbitAccounts = listOf(OrbitAccount())
        assertTrue(homeCacheService.getMoneyMarketStatusList().isEmpty()) { defaultValueChangedMessage }
        homeCacheService.setMoneyMarketStatusList(orbitAccounts)
        assertTrue(homeCacheService.getMoneyMarketStatusList() == orbitAccounts) { valueUpdateFailedMessage }
    }

    @Test
    fun clear() {
        assertNull(homeCacheService.getHomeLoanAccountHistoryCleared()) { defaultValueChangedMessage }
        assertNull(homeCacheService.getHomeLoanAccountHistoryUncleared()) { defaultValueChangedMessage }
        assertTrue(homeCacheService.getInsurancePolicies().isEmpty()) { defaultValueChangedMessage }
        assertFalse(homeCacheService.hasFuneralCover()) { defaultValueChangedMessage }
        assertFalse(homeCacheService.hasAuthorizations()) { defaultValueChangedMessage }
        assertTrue(homeCacheService.getNumberOfPendingMandates() == 0) { defaultValueChangedMessage }
        assertTrue(homeCacheService.getFilteredHomeAccounts().isEmpty()) { defaultValueChangedMessage }
        assertTrue(homeCacheService.getSavingsAndInvestmentsAccounts().isEmpty()) { defaultValueChangedMessage }
        assertNull(homeCacheService.getSelectedHomeLoanAccount()) { defaultValueChangedMessage }
        assertNull(homeCacheService.getUnitTrustAccountObject()) { defaultValueChangedMessage }
        assertNull(homeCacheService.getUnitTrustResponseModel()) { defaultValueChangedMessage }
        assertNull(homeCacheService.getPersonalLoanResponse()) { defaultValueChangedMessage }
        assertTrue(homeCacheService.getLottoSourceAccountList().isEmpty()) { defaultValueChangedMessage }
        assertNull(homeCacheService.getHomeLoanPerilsAccount()) { defaultValueChangedMessage }
        assertTrue(homeCacheService.getMoneyMarketStatusList().isEmpty()) { defaultValueChangedMessage }
    }
}