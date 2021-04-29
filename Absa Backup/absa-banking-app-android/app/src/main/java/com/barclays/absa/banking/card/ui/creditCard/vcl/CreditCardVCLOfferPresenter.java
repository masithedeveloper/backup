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
package com.barclays.absa.banking.card.ui.creditCard.vcl;

import com.barclays.absa.banking.boundary.model.OverdraftSnooze;
import com.barclays.absa.banking.card.services.card.dto.CreditCardInteractor;
import com.barclays.absa.banking.card.services.card.dto.CreditCardService;
import com.barclays.absa.banking.framework.ExtendedResponseListener;

import java.lang.ref.WeakReference;

class CreditCardVCLOfferPresenter {
    private WeakReference<CreditCardVCLOfferView> weakReference;
    private CreditCardService creditCardService;

    CreditCardVCLOfferPresenter(CreditCardVCLOfferView view) {
        weakReference = new WeakReference<>(view);
        creditCardService = new CreditCardInteractor();
        snoozeExtendedResponseListener.setView(view);
    }

    private ExtendedResponseListener<OverdraftSnooze> snoozeExtendedResponseListener = new ExtendedResponseListener<OverdraftSnooze>() {

        @Override
        public void onSuccess(OverdraftSnooze successResponse) {

        }

        @Override
        public void onRequestStarted() {
            CreditCardVCLOfferView view = weakReference.get();
            if (view != null) {
                view.dismissProgressDialog();
                view.dismissOfferIndicator();
            }
        }
    };

    public void onHideOfferClicked() {
        String snoozeForSixMonths = "OPTNVR";
        creditCardService.requestVCLOverdraftSnooze(snoozeForSixMonths, true, snoozeExtendedResponseListener);
    }
}
