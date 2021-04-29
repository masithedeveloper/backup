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
package com.barclays.absa.banking.account.services;

import com.barclays.absa.banking.account.services.dto.ArchivedStatementListResponse;
import com.barclays.absa.banking.account.services.dto.LinkedAndUnlinkedAccounts;
import com.barclays.absa.banking.account.services.dto.ManageAccountsResponse;
import com.barclays.absa.banking.account.services.dto.PdfStatementResponse;
import com.barclays.absa.banking.boundary.model.AccountDetail;
import com.barclays.absa.banking.boundary.model.AccountObject;
import com.barclays.absa.banking.boundary.model.PayBeneficiaryPaymentObject;
import com.barclays.absa.banking.framework.ExtendedResponseListener;
import com.barclays.absa.banking.framework.data.ApplicationFlowType;
import com.barclays.absa.banking.transfer.TransferType;
import com.barclays.absa.banking.transfer.responseListeners.AccountListExtendedResponseListener;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface AccountService {
    String OP0876_GET_ARCHIVED_STATEMENT = "OP0876";
    String OP2074_LINKED_UNLINKED_ACCOUNTS = "OP2074";
    String OP2075_LINK_UNLINK_ACCOUNTS = "OP2075";

    void fetchArchivedStatementList(String accountNumber, String fromDate, String toDate,
                                    ExtendedResponseListener<ArchivedStatementListResponse> listener);

    void fetchArchivedStatementPdf(String key, ExtendedResponseListener<PdfStatementResponse> listener);

    void fetchTransactionHistory(AccountObject accountDetail, boolean isUnclearedTransactions, String fromDate, String toDate, ExtendedResponseListener<AccountDetail> responseListener);

    void fetchDateBasedUnclearedAccountTransactionHistory(AccountObject accountDetail, String fromDate, String toDate, ExtendedResponseListener<AccountDetail> responseListener);

    void fetchDateBasedClearedAccountTransactionHistory(AccountObject accountDetail, String fromDate, String toDate, ExtendedResponseListener<AccountDetail> responseListener);

    List<AccountObject> getFromAccountsFromCache(PayBeneficiaryPaymentObject paymentObject, ApplicationFlowType applicationFlowType);

    void fetchAccountStats(ExtendedResponseListener<LinkedAndUnlinkedAccounts> responseListener);

    void linkAndUnlinkAccounts(Boolean accountOrderChanges, Boolean accountLinkStatesChanges, String accounts, ExtendedResponseListener<ManageAccountsResponse> responseListener);

    void fetchExpressAccounts(TransferType transferType, @NotNull AccountListExtendedResponseListener accountListExtendedResponseListener);
}