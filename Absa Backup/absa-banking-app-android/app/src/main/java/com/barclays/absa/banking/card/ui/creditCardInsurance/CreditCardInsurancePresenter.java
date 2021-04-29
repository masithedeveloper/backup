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
package com.barclays.absa.banking.card.ui.creditCardInsurance;

import com.barclays.absa.banking.boundary.model.creditCardInsurance.CreditProtection;
import com.barclays.absa.banking.boundary.model.creditCardInsurance.CreditProtectionQuote;
import com.barclays.absa.banking.card.services.card.dto.CreditCardInteractor;
import com.barclays.absa.banking.framework.ExtendedResponseListener;
import com.barclays.absa.banking.framework.analytics.AnalyticsUtils;
import com.barclays.absa.banking.framework.app.BMBConstants;
import com.barclays.absa.banking.framework.dagger.DaggerHelperKt;
import com.barclays.absa.banking.framework.data.ResponseObject;
import com.barclays.absa.banking.framework.services.contracts.IAppCacheService;

public class CreditCardInsurancePresenter {
    private CreditCardInsuranceView creditCardInsuranceView;
    private CreditCardInteractor creditCardInteractor;
    private final String SITE_SECTION = "Credit Card Protection";
    private final IAppCacheService appCacheService = DaggerHelperKt.getServiceInterface(IAppCacheService.class);

    private ExtendedResponseListener<CreditProtectionQuote> fetchPolicyQuoteResponseListener = new ExtendedResponseListener<CreditProtectionQuote>() {

        @Override
        public void onRequestStarted() {
        }

        @Override
        public void onSuccess(final CreditProtectionQuote successResponse) {
            creditCardInsuranceView.dismissProgressDialog();
            if (successResponse != null) {
                CreditProtection creditProtection;
                creditProtection = successResponse.getCreditProtection();
                if (BMBConstants.SUCCESS.equalsIgnoreCase(successResponse.getTransactionStatus()) && creditProtection != null) {
                    appCacheService.setCreditProtection(creditProtection);
                } else if (BMBConstants.FAILURE.equalsIgnoreCase(successResponse.getTransactionStatus())) {
                    creditCardInsuranceView.showSomethingWentWrongScreen();
                }
            } else {
                creditCardInsuranceView.showSomethingWentWrongScreen();
            }
        }

        @Override
        public void onFailure(ResponseObject failureResponse) {
            super.onFailure(failureResponse);
            creditCardInsuranceView.showSomethingWentWrongScreen();
        }
    };

    private ExtendedResponseListener<CreditProtectionQuote> submitCreditProtectionPolicyResponseListener = new ExtendedResponseListener<CreditProtectionQuote>() {
        @Override
        public void onSuccess(final CreditProtectionQuote successResponse) {
            creditCardInsuranceView.dismissProgressDialog();
            if (successResponse != null) {
                CreditProtection creditProtection = successResponse.getCreditProtection();
                if (BMBConstants.SUCCESS.equalsIgnoreCase(successResponse.getTransactionStatus()) && creditProtection != null) {
                    creditCardInsuranceView.showSuccessScreen(creditProtection);
                    AnalyticsUtils.getInstance().trackCustomScreenView("Life_CCProt_ApplicationSuccess", SITE_SECTION, BMBConstants.TRUE_CONST);
                } else if (BMBConstants.FAILURE.equalsIgnoreCase(successResponse.getTransactionStatus())) {
                    creditCardInsuranceView.showFailureScreen();
                    AnalyticsUtils.getInstance().trackCustomScreenView("Life_CCProt_ApplicationUnsuccess", SITE_SECTION, BMBConstants.TRUE_CONST);
                }
            } else {
                creditCardInsuranceView.showSomethingWentWrongScreen();
            }
        }

        @Override
        public void onFailure(ResponseObject failureResponse) {
            creditCardInsuranceView.showFailureScreen();
            AnalyticsUtils.getInstance().trackCustomScreenView("Life_CCProt_ApplicationUnsuccess", SITE_SECTION, BMBConstants.TRUE_CONST);
        }
    };

    public CreditCardInsurancePresenter(CreditCardInsuranceView creditCardInsuranceView) {
        creditCardInteractor = new CreditCardInteractor();
        this.creditCardInsuranceView = creditCardInsuranceView;
        fetchPolicyQuoteResponseListener.setView(creditCardInsuranceView);
        submitCreditProtectionPolicyResponseListener.setView(creditCardInsuranceView);
    }

    public void onViewCreated(String creditCardNumber) {
        creditCardInteractor.fetchCreditCardProtectionPolicyQuote(creditCardNumber, fetchPolicyQuoteResponseListener);
    }

    public void onSubmitButtonClicked(String creditCardNumber) {
        creditCardInteractor.submitCreditCardProtectionApplication(creditCardNumber, submitCreditProtectionPolicyResponseListener);
    }
}
