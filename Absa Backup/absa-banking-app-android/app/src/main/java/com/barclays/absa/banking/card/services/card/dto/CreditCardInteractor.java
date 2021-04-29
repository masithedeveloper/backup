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
package com.barclays.absa.banking.card.services.card.dto;

import com.barclays.absa.banking.boundary.model.ManageCardConfirmLimit;
import com.barclays.absa.banking.boundary.model.ManageCardLimitDetails;
import com.barclays.absa.banking.boundary.model.OverdraftSnooze;
import com.barclays.absa.banking.boundary.model.creditCardInsurance.CreditProtectionQuote;
import com.barclays.absa.banking.boundary.model.creditCardStopAndReplace.CreditCardReplacementConfirmation;
import com.barclays.absa.banking.boundary.model.creditCardStopAndReplace.CreditCardReplacementReasonsList;
import com.barclays.absa.banking.card.services.card.CardMockFactory;
import com.barclays.absa.banking.card.services.card.dto.creditCard.BureauDataRequest;
import com.barclays.absa.banking.card.services.card.dto.creditCard.CardLimitDetailsConfirmRequest;
import com.barclays.absa.banking.card.services.card.dto.creditCard.CreditCardBureauData;
import com.barclays.absa.banking.card.services.card.dto.creditCard.CreditCardHotLeadApplicationRequest;
import com.barclays.absa.banking.card.services.card.dto.creditCard.CreditCardHotLeadApplicationResponse;
import com.barclays.absa.banking.card.services.card.dto.creditCard.CreditCardInformation;
import com.barclays.absa.banking.card.services.card.dto.creditCard.CreditCardOverdraft;
import com.barclays.absa.banking.card.services.card.dto.creditCard.CreditCardProtectionInsuranceRequest;
import com.barclays.absa.banking.card.services.card.dto.creditCard.CreditCardResponseObject;
import com.barclays.absa.banking.card.services.card.dto.creditCard.CreditCardVCLSnoozeOfferRequest;
import com.barclays.absa.banking.card.services.card.dto.creditCard.CreditLimitApplicationRequest;
import com.barclays.absa.banking.card.services.card.dto.creditCard.CreditLimitApplicationResult;
import com.barclays.absa.banking.card.services.card.dto.creditCard.ExtendedCreditCardInformationRequest;
import com.barclays.absa.banking.card.services.card.dto.creditCard.OverdraftOfferRequest;
import com.barclays.absa.banking.card.services.card.dto.creditCard.SubmitCreditCardProtectionPolicyQuoteRequest;
import com.barclays.absa.banking.card.services.card.dto.creditCard.VCLParcelableModel;
import com.barclays.absa.banking.framework.AbstractInteractor;
import com.barclays.absa.banking.framework.BuildConfigHelper;
import com.barclays.absa.banking.framework.ExtendedResponseListener;
import com.barclays.absa.banking.framework.ServiceClient;

public class CreditCardInteractor extends AbstractInteractor implements CreditCardService {

    private boolean firstCall;

    @Override
    public void fetchCreditCardInformation(String creditCardNumber, ExtendedResponseListener<CreditCardInformation> creditCardInfoResponseListener) {
        ExtendedCreditCardInformationRequest<CreditCardInformation> extendedCreditCardInformationRequest = new ExtendedCreditCardInformationRequest<>(creditCardNumber, creditCardInfoResponseListener);
        ServiceClient serviceClient = new ServiceClient(extendedCreditCardInformationRequest);
        serviceClient.submitRequest();
    }

    @Override
    public void fetchCreditCardDetails(ManageCardLimitDetails manageCardLimitDetails, String posLimitValue, String atmLimitValue, ExtendedResponseListener<ManageCardConfirmLimit> responseListener) {
        CardLimitDetailsConfirmRequest<ManageCardConfirmLimit> manageCardLimitDetailsConfirmRequest
                = new CardLimitDetailsConfirmRequest<>(manageCardLimitDetails, posLimitValue, atmLimitValue, responseListener);
        ServiceClient serviceClient = new ServiceClient(manageCardLimitDetailsConfirmRequest);
        serviceClient.submitRequest();
    }

    @Override
    public void fetchStopAndReplaceCardReasons(String cardNumber, ExtendedResponseListener<CreditCardReplacementReasonsList> responseListener) {
        CreditCardReplacementReasonsRequest<CreditCardReplacementReasonsList> replacementReasonsRequest
                = new CreditCardReplacementReasonsRequest<>(cardNumber, responseListener);
        ServiceClient serviceClient = new ServiceClient(replacementReasonsRequest);
        serviceClient.submitRequest();
    }

    @Override
    public void confirmStopAndReplaceCreditCard(CreditCardReplacementConfirmation replacementConfirmation, ExtendedResponseListener<CreditCardReplacementConfirmation> cardReplacementConfirmationListener) {
        ReplaceCreditCardConfirmationRequest<CreditCardReplacementConfirmation> confirmCardReplacementRequest = new ReplaceCreditCardConfirmationRequest<>(cardReplacementConfirmationListener, replacementConfirmation);
        ServiceClient serviceClient = new ServiceClient(confirmCardReplacementRequest);
        serviceClient.submitRequest();
    }

    @Override
    public void fetchCreditCardProtectionPolicyQuote(String creditCardNumber, ExtendedResponseListener<CreditProtectionQuote> responseListener) {
        CreditCardProtectionInsuranceRequest<CreditProtectionQuote> creditCardInsuranceCreditCardProtectionInsuranceRequest
                = new CreditCardProtectionInsuranceRequest<>(creditCardNumber, responseListener);
        ServiceClient serviceClient = new ServiceClient(creditCardInsuranceCreditCardProtectionInsuranceRequest);
        serviceClient.submitRequest();
    }

    @Override
    public void submitCreditCardProtectionApplication(String creditCardNumber, ExtendedResponseListener<CreditProtectionQuote> fetchPolicyQuoteResponseListener) {
        SubmitCreditCardProtectionPolicyQuoteRequest<CreditProtectionQuote> creditCardProtectionPolicyQuoteRequest
                = new SubmitCreditCardProtectionPolicyQuoteRequest<>(creditCardNumber, fetchPolicyQuoteResponseListener);
        ServiceClient serviceClient = new ServiceClient(creditCardProtectionPolicyQuoteRequest);
        serviceClient.submitRequest();
    }

    @Override
    public void overdraftOfferRequest(ExtendedResponseListener<CreditCardOverdraft> responseListener) {
        OverdraftOfferRequest<CreditCardOverdraft> creditCardInsuranceRequest = new OverdraftOfferRequest<>(responseListener);
        ServiceClient serviceClient = new ServiceClient(creditCardInsuranceRequest);
        serviceClient.submitRequest();
    }

    @Override
    public void validateStopAndReplaceCreditCard(CreditCardReplacementConfirmation replacementConfirmation, ExtendedResponseListener<CreditCardReplacementConfirmation> cardReplacementValidationListener) {
        ReplaceCreditCardValidationRequest<CreditCardReplacementConfirmation> validateCreditCardReplacementRequest = new ReplaceCreditCardValidationRequest<>(replacementConfirmation, cardReplacementValidationListener);
        ServiceClient serviceClient = new ServiceClient(validateCreditCardReplacementRequest);
        serviceClient.submitRequest();
    }

    @Override
    public void fetchClientBureauData(VCLParcelableModel dataModel, ExtendedResponseListener<CreditCardBureauData> responseListener) {
        BureauDataRequest<CreditCardBureauData> bureauDataRequest = new BureauDataRequest<>(dataModel, responseListener);
        ServiceClient serviceClient = new ServiceClient(bureauDataRequest);
        serviceClient.submitRequest();
    }

    @Override
    public void requestCreditLimitIncrease(VCLParcelableModel requestModel, ExtendedResponseListener<CreditLimitApplicationResult> responseListener) {
        if (BuildConfigHelper.STUB) {
            submitRequest(new CreditLimitApplicationRequest(requestModel, responseListener), CardMockFactory.Companion.creditCardLimitIncrease(firstCall));
        } else {
            submitRequest(new CreditLimitApplicationRequest(requestModel, responseListener));
        }
    }

    @Override
    public void requestVCLOverdraftSnooze(String snoozeOptionValue, boolean isCreditCardVCLSnooze, ExtendedResponseListener<OverdraftSnooze> snoozeResponseListener) {
        CreditCardVCLSnoozeOfferRequest<OverdraftSnooze> overdraftSnooze = new CreditCardVCLSnoozeOfferRequest<>(snoozeOptionValue, isCreditCardVCLSnooze, snoozeResponseListener);
        ServiceClient serviceClient = new ServiceClient(overdraftSnooze);
        serviceClient.submitRequest();
    }

    @Override
    public void setFirstCall(boolean firstCall) {
        this.firstCall = firstCall;
    }

    @Override
    public void requestCreditCardHub(String cardNumber, String fromDate, String toDate, ExtendedResponseListener<CreditCardResponseObject> responseListener) {
        CreditCardHubRequest<CreditCardResponseObject> creditCardHubRequest = new CreditCardHubRequest<>(cardNumber, fromDate, toDate, responseListener);
        ServiceClient serviceClient = new ServiceClient(creditCardHubRequest);
        serviceClient.submitRequest();
    }

    @Override
    public void applyForCreditCardHotLead(String cellphoneNumber, ExtendedResponseListener<CreditCardHotLeadApplicationResponse> responseListener) {
        submitRequest(new CreditCardHotLeadApplicationRequest(cellphoneNumber, responseListener));
    }
}
