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
import kotlin.collections.set

class HomeCacheService : IHomeCacheService {

    private var BACKING_STORE = HashMap<String, Any>()

    companion object {
        private const val ACCOUNT_HISTORY_UNCLEARED = "account_history_uncleared"
        private const val ACCOUNT_HISTORY_CLEARED = "account_history_cleared"
        private const val INSURANCE_POLICIES = "insurance_policy_list"
        private const val HAS_FUNERAL_COVER = "has_funeral_cover"
        private const val HAS_AUTHORIZATIONS = "has_authorizations"
        private const val HAS_PENDING_MANDATES = "has_pending_mandates"
        private const val HOME_SCREEN_ACCOUNTS = "home_screen_accounts"
        private const val SELECTED_HOME_LOAN_ACCOUNT = "selected_home_loan_account"
        private const val ACCOUNT_OBJECT = "account_object"
        private const val UNIT_TRUST_RESPONSE_MODEL_OBJECT = "unit_trust_response_model_object"
        private const val PERSONAL_LOAN = "personal_loan_response"
        private const val LOTTO_SOURCE_ACCOUNTS = "lotto_source_accounts"
        private const val HOME_LOAN_PERILS_ACCOUNT = "homeLoanPerilsAccount"
        private const val SAVINGS_AND_INVESTMENTS_ACCOUNTS = "savingsAndInvestmentsAccounts"
        private const val MONEY_MARKET_ACCOUNT_STATUS = "moneyMarkerAccountStatus"
    }

    override fun getHomeLoanAccountHistoryCleared(): AccountDetail? = BACKING_STORE[ACCOUNT_HISTORY_CLEARED] as? AccountDetail
    override fun setHomeLoanAccountHistoryCleared(accountDetail: AccountDetail) {
        BACKING_STORE[ACCOUNT_HISTORY_CLEARED] = accountDetail
    }

    override fun getHomeLoanAccountHistoryUncleared(): AccountDetail? = BACKING_STORE[ACCOUNT_HISTORY_UNCLEARED] as? AccountDetail
    override fun setHomeLoanAccountHistoryUncleared(accountDetail: AccountDetail) {
        BACKING_STORE[ACCOUNT_HISTORY_UNCLEARED] = accountDetail
    }

    @Suppress("UNCHECKED_CAST")
    override fun getInsurancePolicies(): List<Policy> = BACKING_STORE[INSURANCE_POLICIES] as? List<Policy> ?: emptyList()
    override fun setInsurancePolicies(policies: List<Policy>) {
        BACKING_STORE[INSURANCE_POLICIES] = policies
    }

    override fun hasFuneralCover(): Boolean = (BACKING_STORE[HAS_FUNERAL_COVER] as? Boolean) ?: false
    override fun setHasFuneralCover(hasFuneralCover: Boolean) {
        BACKING_STORE[HAS_FUNERAL_COVER] = hasFuneralCover
    }

    override fun hasAuthorizations(): Boolean = (BACKING_STORE[HAS_AUTHORIZATIONS] as? Boolean) ?: false
    override fun setHasAuthorizations(hasAuthorizations: Boolean) {
        BACKING_STORE[HAS_AUTHORIZATIONS] = hasAuthorizations
    }

    override fun getNumberOfPendingMandates(): Int = (BACKING_STORE[HAS_PENDING_MANDATES] as? Int) ?: 0
    override fun setNumberOfPendingMandates(numberOfManatees: Int) {
        BACKING_STORE[HAS_PENDING_MANDATES] = numberOfManatees
    }

    @Suppress("UNCHECKED_CAST")
    override fun getFilteredHomeAccounts(): List<Entry> = (BACKING_STORE[HOME_SCREEN_ACCOUNTS] as? List<Entry>) ?: emptyList()
    override fun setFilteredAccounts(accounts: List<Entry>) {
        BACKING_STORE[HOME_SCREEN_ACCOUNTS] = accounts
    }

    @Suppress("UNCHECKED_CAST")
    override fun getSavingsAndInvestmentsAccounts(): List<Entry> = (BACKING_STORE[SAVINGS_AND_INVESTMENTS_ACCOUNTS] as? List<Entry>) ?: emptyList()
    override fun setSavingsAndInvestmentsAccounts(accounts: List<Entry>) {
        BACKING_STORE[SAVINGS_AND_INVESTMENTS_ACCOUNTS] = accounts
    }

    override fun getSelectedHomeLoanAccount(): AccountObject? = BACKING_STORE[SELECTED_HOME_LOAN_ACCOUNT] as? AccountObject
    override fun setSelectedHomeLoanAccount(account: AccountObject) {
        BACKING_STORE[SELECTED_HOME_LOAN_ACCOUNT] = account
    }

    override fun getUnitTrustAccountObject(): AccountObject? = BACKING_STORE[ACCOUNT_OBJECT] as? AccountObject
    override fun setUnitTrustAccountObject(accountObject: AccountObject) {
        BACKING_STORE[ACCOUNT_OBJECT] = accountObject
    }

    override fun getUnitTrustResponseModel(): UnitTrustAccountsWrapper? = BACKING_STORE[UNIT_TRUST_RESPONSE_MODEL_OBJECT] as? UnitTrustAccountsWrapper
    override fun setUnitTrustResponseModel(unitTrustResponseModel: UnitTrustAccountsWrapper) {
        BACKING_STORE[UNIT_TRUST_RESPONSE_MODEL_OBJECT] = unitTrustResponseModel
    }

    override fun getPersonalLoanResponse(): CreditLimitsResponse? = BACKING_STORE[PERSONAL_LOAN] as? CreditLimitsResponse
    override fun setPersonalLoanResponse(personalLoanResponse: CreditLimitsResponse) {
        BACKING_STORE[PERSONAL_LOAN] = personalLoanResponse
    }

    @Suppress("UNCHECKED_CAST")
    override fun getLottoSourceAccountList(): List<SourceAccount> = (BACKING_STORE[LOTTO_SOURCE_ACCOUNTS] as? List<SourceAccount>) ?: emptyList()
    override fun setLottoSourceAccountList(lottoSourceAccountList: List<SourceAccount>) {
        BACKING_STORE[LOTTO_SOURCE_ACCOUNTS] = lottoSourceAccountList
    }

    override fun getHomeLoanPerilsAccount(): AccountObject? = BACKING_STORE[HOME_LOAN_PERILS_ACCOUNT] as? AccountObject
    override fun setHomeLoanPerilsAccount(account: AccountObject) {
        BACKING_STORE[HOME_LOAN_PERILS_ACCOUNT] = account
    }

    @Suppress("UNCHECKED_CAST")
    override fun getMoneyMarketStatusList(): List<OrbitAccount> = (BACKING_STORE[MONEY_MARKET_ACCOUNT_STATUS] as? List<OrbitAccount>) ?: emptyList()
    override fun setMoneyMarketStatusList(messageList: List<OrbitAccount>) {
        BACKING_STORE[MONEY_MARKET_ACCOUNT_STATUS] = messageList
    }

    override fun clear() {
        BACKING_STORE = HashMap()
    }
}