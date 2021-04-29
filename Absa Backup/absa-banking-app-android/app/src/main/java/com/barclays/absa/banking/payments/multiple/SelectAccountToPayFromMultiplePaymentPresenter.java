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
package com.barclays.absa.banking.payments.multiple;

import com.barclays.absa.banking.boundary.model.ClientAgreementDetails;
import com.barclays.absa.banking.framework.ExtendedResponseListener;
import com.barclays.absa.banking.payments.services.multiple.MultipleBeneficiaryPaymentInteractor;

import java.lang.ref.WeakReference;

class SelectAccountToPayFromMultiplePaymentPresenter {
    private final MultipleBeneficiaryPaymentInteractor interactor;
    private WeakReference<SelectAccountToPayFromMultiplePaymentView> selectAccountToPayFromMultiplePaymentViewWeakReference;

    private final ExtendedResponseListener<ClientAgreementDetails> clientAgreementDetailsResponseListener = new ExtendedResponseListener<ClientAgreementDetails>() {
        SelectAccountToPayFromMultiplePaymentView view;

        @Override
        public void onSuccess(ClientAgreementDetails successResponse) {
            view = selectAccountToPayFromMultiplePaymentViewWeakReference.get();
            if (view != null) {
                view.dismissProgressDialog();
                view.navigateToMultipleBeneficiaryDetailScreen(successResponse);
            }
        }
    };

    SelectAccountToPayFromMultiplePaymentPresenter(SelectAccountToPayFromMultiplePaymentView selectAccountToPayFromMultiplePaymentView) {
        selectAccountToPayFromMultiplePaymentViewWeakReference = new WeakReference<>(selectAccountToPayFromMultiplePaymentView);
        interactor = new MultipleBeneficiaryPaymentInteractor();
        clientAgreementDetailsResponseListener.setView(selectAccountToPayFromMultiplePaymentView);
    }

    void clientAgreementDetails() {
        interactor.clientAgreementDetails(clientAgreementDetailsResponseListener);
    }

    public void onNextButtonClicked() {
        SelectAccountToPayFromMultiplePaymentView view = selectAccountToPayFromMultiplePaymentViewWeakReference.get();
        if (view != null) {
            view.navigateToMultipleBeneficiaryDetailScreen();
        }
    }
}
