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
package com.barclays.absa.banking.settings.ui;

import com.barclays.absa.banking.boundary.model.ManageCardConfirmLimit;
import com.barclays.absa.banking.boundary.model.ManageCardLimitDetails;
import com.barclays.absa.banking.card.services.card.dto.CreditCardService;
import com.barclays.absa.banking.card.services.card.dto.creditCard.CreditCardInformation;
import com.barclays.absa.banking.framework.ExtendedResponseListener;

class SettingsChangeCardLimitPresenter {

    public static final String CREDIT_CARD = "Credit card";
    public static final String CREDIT = "Credit";
    private SettingsCardLimitsView view;
    private CreditCardService creditCardService;
    private ManageCardConfirmLimit manageCardConfirmLimit;
    private ManageCardLimitDetails cardLimitDetails;

    SettingsChangeCardLimitPresenter(SettingsCardLimitsView view, CreditCardService creditCardService) {
        this.view = view;
        this.creditCardService = creditCardService;
        creditCardInformationExtendedResponseListener.setView(view);
        manageCardConfirmLimitResponseListener.setView(view);
        manageCardLimitDetailsResponseListener.setView(view);
    }

    private ExtendedResponseListener<ManageCardLimitDetails> manageCardLimitDetailsResponseListener = new ExtendedResponseListener<ManageCardLimitDetails>() {
        @Override
        public void onSuccess(ManageCardLimitDetails successResponse) {
            if (successResponse != null && (CREDIT.equalsIgnoreCase(successResponse.getCardType()) || CREDIT_CARD.equalsIgnoreCase(successResponse.getCardType()))) {
                cardLimitDetails = successResponse;
                creditCardService.fetchCreditCardInformation(successResponse.getCardNumber().trim(), creditCardInformationExtendedResponseListener);
            } else {
                view.dismissProgressDialog();
                view.initViews(successResponse);
            }
        }
    };

    private ExtendedResponseListener<CreditCardInformation> creditCardInformationExtendedResponseListener = new ExtendedResponseListener<CreditCardInformation>() {
        @Override
        public void onRequestStarted() {

        }

        @Override
        public void onSuccess(CreditCardInformation successResponse) {
            view.dismissProgressDialog();
            view.initViews(successResponse, cardLimitDetails);
        }
    };

    private ExtendedResponseListener<ManageCardConfirmLimit> manageCardConfirmLimitResponseListener = new ExtendedResponseListener<ManageCardConfirmLimit>() {

        @Override
        public void onSuccess(ManageCardConfirmLimit manageCardConfirmLimitResponse) {
            manageCardConfirmLimit = manageCardConfirmLimitResponse;
            view.dismissProgressDialog();
            view.navigateToConfirmationScreen(manageCardConfirmLimit);
        }
    };

    void saveLimitSelected(ManageCardLimitDetails cardDetailsObject, String currentPOSDailyLimit, String currentATMDailyLimit) {
        cardDetailsObject.setCardType("credit".equalsIgnoreCase(cardDetailsObject.getCardType()) || "CC".equalsIgnoreCase(cardDetailsObject.getCardType()) ? "credit" : "debit");
        creditCardService.fetchCreditCardDetails(cardDetailsObject, currentPOSDailyLimit, currentATMDailyLimit, manageCardConfirmLimitResponseListener);
    }

}
