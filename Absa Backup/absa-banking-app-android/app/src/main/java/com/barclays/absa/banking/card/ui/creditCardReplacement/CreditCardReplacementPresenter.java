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
package com.barclays.absa.banking.card.ui.creditCardReplacement;

import com.barclays.absa.banking.boundary.model.creditCardStopAndReplace.CreditCardReplacementReasonsList;
import com.barclays.absa.banking.card.services.card.dto.CreditCardService;
import com.barclays.absa.banking.framework.ExtendedResponseListener;

class CreditCardReplacementPresenter {
    private CreditCardReplacementView view;
    private CreditCardService creditCardInteractor;

    private ExtendedResponseListener<CreditCardReplacementReasonsList> creditCardReplacementReasons = new ExtendedResponseListener<CreditCardReplacementReasonsList>() {
        @Override
        public void onSuccess(final CreditCardReplacementReasonsList successResponse) {
            view = (CreditCardReplacementView) viewWeakReference.get();
            if (view != null) {
                view.dismissProgressDialog();
                view.showCreditCardReplacementReasons(successResponse);
            }
        }
    };

    CreditCardReplacementPresenter(CreditCardReplacementView view, CreditCardService creditCardInteractor) {
        this.creditCardInteractor = creditCardInteractor;
        creditCardReplacementReasons.setView(view);
    }

    void viewLoaded(String cardNumber) {
        creditCardInteractor.fetchStopAndReplaceCardReasons(cardNumber, creditCardReplacementReasons);
    }
}
