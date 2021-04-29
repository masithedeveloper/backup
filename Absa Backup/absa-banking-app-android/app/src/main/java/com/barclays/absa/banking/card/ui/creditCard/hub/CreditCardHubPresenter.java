/*
 *  Copyright (c) 2018 Absa Bank Limited, All Rights Reserved.
 *
 *  This code is confidential to Absa Bank Limited and shall not be disclosed
 *  outside the Bank without the prior written permission of the Absa Legal
 *
 *  In the event that such disclosure is permitted the code shall not be copied
 *  or distributed other than on a need-to-know basis and any recipients may be
 *  required to sign a confidentiality undertaking in favor of Absa Bank
 *  Limited
 *
 */
package com.barclays.absa.banking.card.ui.creditCard.hub;

import com.barclays.absa.banking.card.services.card.dto.CreditCardInteractor;
import com.barclays.absa.banking.card.services.card.dto.CreditCardService;
import com.barclays.absa.banking.card.services.card.dto.creditCard.CreditCardResponseObject;
import com.barclays.absa.banking.card.ui.creditCard.hub.extendedresponselisteners.CreditCardHubExtendedResponseListener;
import com.barclays.absa.banking.framework.AbstractPresenter;

import java.lang.ref.WeakReference;

public class CreditCardHubPresenter extends AbstractPresenter {
    private CreditCardService creditCardService;
    private boolean isUpdatedDate;

    private CreditCardHubExtendedResponseListener creditCarHubExtendedResponseListener;

    CreditCardHubPresenter(WeakReference<CreditCardHubView> weakReference) {
        super(weakReference);
        creditCardService = new CreditCardInteractor();
        creditCarHubExtendedResponseListener = new CreditCardHubExtendedResponseListener(this);
    }

    public void onCreditCardHubSuccessfulResponse(CreditCardResponseObject successResponse) {
        CreditCardHubView view = (CreditCardHubView) viewWeakReference.get();
        if (view != null) {
            if (isUpdatedDate) {
                view.onSearchRequestCompleted(successResponse.buildListOfTransactions());
            } else {
                view.updateCreditCardInformation(successResponse);
            }
        }
    }

    public void onViewLoaded(String accountNumber, FilteringOptions filteringOptions) {
        showProgressIndicator();
        requestCreditCardInformation(accountNumber, filteringOptions.getFromDate(), filteringOptions.getToDate(), false);
    }

    void requestCreditCardInformation(String accountNumber, String fromDate, String toDate, boolean isUpdatedDate) {
        if (isUpdatedDate) {
            showProgressIndicator();
        }
        creditCardService.requestCreditCardHub(accountNumber, fromDate, toDate, creditCarHubExtendedResponseListener);
        this.isUpdatedDate = isUpdatedDate;
    }
}
