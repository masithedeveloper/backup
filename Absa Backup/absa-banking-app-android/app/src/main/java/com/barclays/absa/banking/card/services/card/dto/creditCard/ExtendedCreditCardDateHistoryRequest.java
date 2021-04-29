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
package com.barclays.absa.banking.card.services.card.dto.creditCard;

import com.barclays.absa.banking.boundary.model.AccountDetail;
import com.barclays.absa.banking.framework.ExtendedRequest;
import com.barclays.absa.banking.framework.ExtendedResponseListener;
import com.barclays.absa.banking.framework.MockFactory;
import com.barclays.absa.banking.framework.api.request.params.RequestParams;
import com.barclays.absa.banking.framework.api.request.params.TransactionParams;
import com.barclays.absa.banking.framework.app.BMBConstants;

import static com.barclays.absa.banking.home.services.HomeScreenService.OP1301_ACCOUNT_DETAILS;

public class ExtendedCreditCardDateHistoryRequest<T> extends ExtendedRequest<T> {
    private CreditCard creditCard;
    private String fromDate;
    private String toDate;
    private boolean isUnclearedTransactions;

    public ExtendedCreditCardDateHistoryRequest(CreditCard creditCard, boolean isUnclearedTransactions, String fromDate, String toDate, ExtendedResponseListener responseListener) {
        super(responseListener);
        setMockResponseFile(MockFactory.accountDetails());
        this.creditCard = creditCard;
        this.fromDate = fromDate;
        this.toDate = toDate;
        this.isUnclearedTransactions = isUnclearedTransactions;
        printRequest();
    }

    @Override
    public RequestParams getRequestParams() {
        RequestParams.Builder requestParamsBuilder = new RequestParams.Builder()
                .put(OP1301_ACCOUNT_DETAILS)
                .put(TransactionParams.Transaction.REDEMPTION_ACCOUNT_NO, creditCard.getAccountNo())
                .put(TransactionParams.Transaction.SERVICE_ACCOUNT_TYPE, creditCard.getAccountName())
                .put(TransactionParams.Transaction.SERVICE_FRM_DT, fromDate)
                .put(TransactionParams.Transaction.SERVICE_TO_DT, toDate)
                .put(TransactionParams.Transaction.SERVICE_SORT_PARAM, BMBConstants.SORT_ON_DATE)
                .put(TransactionParams.Transaction.SERVICE_SORT_ORDER, BMBConstants.SORT_IN_DESC)
                .put(TransactionParams.Transaction.ACCOUNT_DESCRIPTION, creditCard.getAccountTypeDescription())
                .put(TransactionParams.Transaction.IS_BALANCED_MASKED, "false");

        if (isUnclearedTransactions) {
            requestParamsBuilder.put(TransactionParams.Transaction.UNCLEARED_EFFECTS_ENABLED_FLAG, "true");
        } else {
            requestParamsBuilder.put(TransactionParams.Transaction.UNCLEARED_EFFECTS_ENABLED_FLAG, "false");
        }
        return requestParamsBuilder.build();
    }

    @Override
    public Class<T> getResponseClass() {
        return (Class<T>) AccountDetail.class;
    }

    @Override
    public Boolean isEncrypted() {
        return true;
    }
}
