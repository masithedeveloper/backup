/*
 * Copyright (c) 2020 Absa Bank Limited, All Rights Reserved.
 *
 * This code is confidential to Absa Bank Limited and shall not be disclosed
 * outside the Bank without the prior written permission of the Absa Legal
 *
 * In the event that such disclosure is permitted the code shall not be copied
 * or distributed other than on a need-to-know basis and any recipients may be
 * required to sign a confidentiality undertaking in favor of Absa Bank
 * Limited
 */
package com.barclays.absa.banking.account.services

import com.barclays.absa.banking.account.services.dto.*
import com.barclays.absa.banking.boundary.model.AccountDetail
import com.barclays.absa.banking.boundary.model.AccountList
import com.barclays.absa.banking.boundary.model.AccountObject
import com.barclays.absa.banking.boundary.model.PayBeneficiaryPaymentObject
import com.barclays.absa.banking.boundary.model.policy.PolicyList
import com.barclays.absa.banking.businessBanking.services.TransactionHistoryRequest
import com.barclays.absa.banking.framework.AbstractInteractor
import com.barclays.absa.banking.framework.ExtendedResponseListener
import com.barclays.absa.banking.framework.dagger.getServiceInterface
import com.barclays.absa.banking.framework.data.ApplicationFlowType
import com.barclays.absa.banking.framework.services.contracts.IAppCacheService
import com.barclays.absa.banking.home.AccountAdapter
import com.barclays.absa.banking.home.services.dto.PolicyListRequest
import com.barclays.absa.banking.home.ui.AccountTypes
import com.barclays.absa.banking.transfer.TransferType
import com.barclays.absa.banking.transfer.responseListeners.AccountListExtendedResponseListener
import com.barclays.absa.utils.AbsaCacheManager

class AccountInteractor : AbstractInteractor(), AccountService {

    private val appCacheService: IAppCacheService = getServiceInterface()

    override fun fetchArchivedStatementList(accountNumber: String, fromDate: String, toDate: String, responseListener: ExtendedResponseListener<ArchivedStatementListResponse>) {
        submitRequest(ArchivedStatementListRequest(accountNumber, fromDate, toDate, responseListener), "account/op0874_archived_statement_list.json")
    }

    override fun fetchArchivedStatementPdf(key: String, responseListener: ExtendedResponseListener<PdfStatementResponse>) {
        submitRequest(ArchivedPdfStatementRequest(key, responseListener), "account/op0876_archived_statement_pdf.json")
    }

    override fun fetchTransactionHistory(accountDetail: AccountObject, isUnclearedTransactions: Boolean, fromDate: String, toDate: String, responseListener: ExtendedResponseListener<AccountDetail>) {
        submitRequest(TransactionHistoryRequest(accountDetail, isUnclearedTransactions, fromDate, toDate, responseListener))
    }

    override fun fetchDateBasedUnclearedAccountTransactionHistory(accountDetail: AccountObject, fromDate: String, toDate: String, responseListener: ExtendedResponseListener<AccountDetail>) {
        fetchTransactionHistory(accountDetail, true, fromDate, toDate, responseListener)
    }

    override fun fetchDateBasedClearedAccountTransactionHistory(accountDetail: AccountObject, fromDate: String, toDate: String, responseListener: ExtendedResponseListener<AccountDetail>) {
        fetchTransactionHistory(accountDetail, false, fromDate, toDate, responseListener)
    }

    override fun getFromAccountsFromCache(paymentObject: PayBeneficiaryPaymentObject, applicationFlowType: ApplicationFlowType): List<AccountObject>? {
        AbsaCacheManager.getInstance().getModelForAccounts(paymentObject, applicationFlowType)
        return paymentObject.fromAccounts
    }

    override fun fetchAccountStats(responseListener: ExtendedResponseListener<LinkedAndUnlinkedAccounts>) {
        submitRequest(ManageAccountsRequest(responseListener))
    }

    override fun linkAndUnlinkAccounts(accountOrderChanges: Boolean, accountLinkStatesChanges: Boolean, accounts: String, responseListener: ExtendedResponseListener<ManageAccountsResponse>) {
        submitRequest(LinkAndUnlinkingAccountsRequest(accountOrderChanges, accountLinkStatesChanges, accounts, responseListener))
    }

    override fun fetchExpressAccounts(transferType: TransferType, accountListExtendedResponseListener: AccountListExtendedResponseListener) {
        val accountList = AccountList()
        appCacheService.getSecureHomePageObject()?.let { secureHomePageObject ->
            accountList.accountsList = AbsaCacheManager.getInstance().accountsList.accountsList
            val fromAccounts: List<AccountObject>?
            var toAccounts = secureHomePageObject.toAccounts

            if (TransferType.AVAF_INTERACCOUNT_TRANSFER == transferType) {
                fromAccounts = AbsaCacheManager.getTransactionalAccounts()
                toAccounts = toAccounts?.filter { AccountTypes.ABSA_VEHICLE_AND_ASSET_FINANCE.equals(it.accountType, true) }
            } else {
                fromAccounts = secureHomePageObject.fromAccounts
                toAccounts = secureHomePageObject.toAccounts?.filter { AccountAdapter.ADVANTAGE != it.accountType }
            }

            if (fromAccounts != null) {
                accountList.fromAccountList = fromAccounts as ArrayList<AccountObject>
            }
            if (toAccounts != null) {
                accountList.toAccountList = toAccounts as ArrayList<AccountObject>
            }
        }
        accountListExtendedResponseListener.onSuccess(accountList)
    }

    fun prefetchHomeScreenData(insurancePolicyListResponseListener: ExtendedResponseListener<PolicyList>) {
        submitRequest(PolicyListRequest(insurancePolicyListResponseListener))
    }
}