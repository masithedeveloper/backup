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

import com.barclays.absa.banking.boundary.model.AccountDetail
import com.barclays.absa.banking.boundary.model.AccountObject
import com.barclays.absa.banking.boundary.model.Entry
import com.barclays.absa.banking.boundary.model.policy.Policy
import com.barclays.absa.banking.lotto.services.SourceAccount
import com.barclays.absa.banking.moneyMarket.service.dto.OrbitAccount
import com.barclays.absa.banking.personalLoan.services.CreditLimitsResponse
import com.barclays.absa.banking.unitTrusts.services.dto.UnitTrustAccountsWrapper

interface IHomeCacheService {

    fun getHomeLoanAccountHistoryCleared(): AccountDetail?
    fun setHomeLoanAccountHistoryCleared(accountDetail: AccountDetail)

    fun getHomeLoanAccountHistoryUncleared(): AccountDetail?
    fun setHomeLoanAccountHistoryUncleared(accountDetail: AccountDetail)

    fun getInsurancePolicies(): List<Policy>
    fun setInsurancePolicies(policies: List<Policy>)

    fun hasFuneralCover(): Boolean
    fun setHasFuneralCover(hasFuneralCover: Boolean)

    fun hasAuthorizations(): Boolean
    fun setHasAuthorizations(hasAuthorizations: Boolean)

    fun getNumberOfPendingMandates(): Int
    fun setNumberOfPendingMandates(numberOfManatees: Int)

    fun getFilteredHomeAccounts(): List<Entry>
    fun setFilteredAccounts(accounts: List<Entry>)

    fun getSavingsAndInvestmentsAccounts(): List<Entry>
    fun setSavingsAndInvestmentsAccounts(accounts: List<Entry>)

    fun getSelectedHomeLoanAccount(): AccountObject?
    fun setSelectedHomeLoanAccount(account: AccountObject)

    fun getUnitTrustAccountObject(): AccountObject?
    fun setUnitTrustAccountObject(accountObject: AccountObject)

    fun getUnitTrustResponseModel(): UnitTrustAccountsWrapper?
    fun setUnitTrustResponseModel(unitTrustResponseModel: UnitTrustAccountsWrapper)

    fun getPersonalLoanResponse(): CreditLimitsResponse?
    fun setPersonalLoanResponse(personalLoanResponse: CreditLimitsResponse)

    fun getLottoSourceAccountList(): List<SourceAccount>
    fun setLottoSourceAccountList(lottoSourceAccountList: List<SourceAccount>)

    fun getHomeLoanPerilsAccount(): AccountObject?
    fun setHomeLoanPerilsAccount(account: AccountObject)

    fun getMoneyMarketStatusList(): List<OrbitAccount>
    fun setMoneyMarketStatusList(messageList: List<OrbitAccount>)

    fun clear()
}