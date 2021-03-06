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
package com.barclays.absa.banking.businessBanking.services;

import com.barclays.absa.banking.boundary.model.AccountDetail;
import com.barclays.absa.banking.boundary.model.AccountObject;
import com.barclays.absa.banking.framework.ExtendedResponseListener;
import com.barclays.absa.banking.framework.ServiceClient;

public class AccountInteractor implements AccountService {
    @Override
    public void fetchTransactionHistory(AccountObject accountDetail, boolean isUnclearedTransactions, String fromDate, String toDate, ExtendedResponseListener<AccountDetail> responseListener) {
        TransactionHistoryRequest<AccountDetail> extendedAccountHistoryRequest = new TransactionHistoryRequest<>(accountDetail, isUnclearedTransactions, fromDate, toDate, responseListener);
        ServiceClient serviceClient = new ServiceClient(extendedAccountHistoryRequest);
        serviceClient.submitRequest();
    }

    @Override
    public void fetchDateBasedUnclearedAccountTransactionHistory(AccountObject accountDetail, String fromDate, String toDate, ExtendedResponseListener<AccountDetail> responseListener) {
        fetchTransactionHistory(accountDetail, true, fromDate, toDate, responseListener);
    }
}