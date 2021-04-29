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

import com.barclays.absa.banking.card.services.card.CardMockFactory;
import com.barclays.absa.banking.framework.ExtendedRequest;
import com.barclays.absa.banking.framework.ExtendedResponseListener;
import com.barclays.absa.banking.framework.api.request.params.RequestParams;
import com.barclays.absa.banking.framework.api.request.params.TransactionParams;

import static com.barclays.absa.banking.card.services.card.dto.CreditCardService.OP0842_CREDIT_CARD_INFO;

public class ExtendedCreditCardInformationRequest<T> extends ExtendedRequest<T> {

    private String creditCardNumber;

    public ExtendedCreditCardInformationRequest(String creditCardNumber, ExtendedResponseListener<T> responseListener) {
        super(responseListener);
        setMockResponseFile(CardMockFactory.Companion.creditCardInfo());
        this.creditCardNumber = creditCardNumber;
        printRequest();
    }

    @Override
    public RequestParams getRequestParams() {
        return new RequestParams.Builder()
                .put(OP0842_CREDIT_CARD_INFO)
                .put(TransactionParams.Transaction.CREDIT_CARD_ACCOUNT_NUMBER, creditCardNumber)
                .build();
    }

    @Override
    public Class<T> getResponseClass() {
        return (Class<T>) CreditCardInformation.class;
    }

    @Override
    public Boolean isEncrypted() {
        return true;
    }
}
