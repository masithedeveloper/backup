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
package com.barclays.absa.banking.overdraft.services;

import com.barclays.absa.banking.boundary.model.OverdraftStatus;
import com.barclays.absa.banking.boundary.model.overdraft.AcceptOverdraftObject;
import com.barclays.absa.banking.boundary.model.overdraft.OverdraftIncomeAndExpensesConfirmationResponse;
import com.barclays.absa.banking.boundary.model.overdraft.OverdraftQuoteDetailsObject;
import com.barclays.absa.banking.boundary.model.overdraft.OverdraftQuoteSummary;
import com.barclays.absa.banking.boundary.model.overdraft.OverdraftResponse;
import com.barclays.absa.banking.boundary.model.overdraft.OverdraftSetup;
import com.barclays.absa.banking.boundary.model.overdraft.RejectOverdraftObject;
import com.barclays.absa.banking.framework.AbstractInteractor;
import com.barclays.absa.banking.framework.ExtendedResponseListener;
import com.barclays.absa.banking.framework.ServiceClient;
import com.barclays.absa.banking.model.FicaCheckResponse;
import com.barclays.absa.banking.overdraft.services.dto.BusinessOverdraftRequest;
import com.barclays.absa.banking.overdraft.services.dto.OverdraftAcceptQuoteRequest;
import com.barclays.absa.banking.overdraft.services.dto.OverdraftConfirmIncomeAndExpensesRequest;
import com.barclays.absa.banking.overdraft.services.dto.OverdraftFICAAndCIFStatusRequest;
import com.barclays.absa.banking.overdraft.services.dto.OverdraftQuoteSummaryRequest;
import com.barclays.absa.banking.overdraft.services.dto.OverdraftRejectQuoteRequest;
import com.barclays.absa.banking.overdraft.services.dto.OverdraftScoreRequest;
import com.barclays.absa.banking.overdraft.services.dto.OverdraftStatusRequest;

public class OverdraftInteractor extends AbstractInteractor implements OverdraftService {

    @Override
    public void fetchFICAAndCIFStatus(ExtendedResponseListener<FicaCheckResponse> ficaAndCifStatusResponseListener) {
        OverdraftFICAAndCIFStatusRequest<FicaCheckResponse> overdraftFICAAndCIFStatusRequest = new OverdraftFICAAndCIFStatusRequest<>(ficaAndCifStatusResponseListener);
        ServiceClient serviceClient = new ServiceClient(overdraftFICAAndCIFStatusRequest);
        serviceClient.submitRequest();
    }

    @Override
    public void fetchOverdraftQuoteSummary(String quoteNumber, ExtendedResponseListener<OverdraftQuoteSummary> overdraftQuoteSummaryResponseListener) {
        OverdraftQuoteSummaryRequest<OverdraftQuoteSummary> overdraftQuoteSummaryRequest = new OverdraftQuoteSummaryRequest<>(quoteNumber, overdraftQuoteSummaryResponseListener);
        ServiceClient serviceClient = new ServiceClient(overdraftQuoteSummaryRequest);
        serviceClient.submitRequest();
    }

    @Override
    public void fetchOverdraftScore(OverdraftSetup overdraftsetup, ExtendedResponseListener<OverdraftResponse> overdraftScoreResponseListener) {
        OverdraftScoreRequest<OverdraftResponse> overdraftScoreRequest = new OverdraftScoreRequest<>(overdraftsetup, overdraftScoreResponseListener);
        submitRequest(overdraftScoreRequest, OverdraftMockFactory.Companion.overdraftScore());
    }

    @Override
    public void acceptOverdraftQuote(AcceptOverdraftObject acceptOverdraftQuote, ExtendedResponseListener<OverdraftResponse> overdraftAcceptQuoteResponseListener) {
        OverdraftAcceptQuoteRequest<OverdraftResponse> overdraftAcceptQuoteRequest = new OverdraftAcceptQuoteRequest<>(acceptOverdraftQuote, overdraftAcceptQuoteResponseListener);
        submitRequest(overdraftAcceptQuoteRequest, OverdraftMockFactory.Companion.overdraftAcceptQuote());
    }

    @Override
    public void rejectOverdraftQuote(RejectOverdraftObject rejectOverdraftQuote, ExtendedResponseListener<OverdraftResponse> overdraftRejectQuoteResponseListener) {
        OverdraftRejectQuoteRequest<OverdraftResponse> overdraftRejectQuoteRequest = new OverdraftRejectQuoteRequest<>(rejectOverdraftQuote, overdraftRejectQuoteResponseListener);
        ServiceClient serviceClient = new ServiceClient(overdraftRejectQuoteRequest);
        serviceClient.submitRequest();
    }

    @Override
    public void confirmIncomeAndExpenses(OverdraftQuoteDetailsObject overdraftDetails, ExtendedResponseListener<OverdraftIncomeAndExpensesConfirmationResponse> overdraftQuoteDetailsResponseListener) {
        OverdraftConfirmIncomeAndExpensesRequest<OverdraftIncomeAndExpensesConfirmationResponse> overdraftQuoteDetailsRequest = new OverdraftConfirmIncomeAndExpensesRequest<>(overdraftDetails, overdraftQuoteDetailsResponseListener);
        ServiceClient serviceClient = new ServiceClient(overdraftQuoteDetailsRequest);
        serviceClient.submitRequest();
    }

    @Override
    public void fetchOverdraftStatus(ExtendedResponseListener<OverdraftStatus> extendedResponseListener) {
        submitRequest(new OverdraftStatusRequest<>(extendedResponseListener));
    }

    @Override
    public void applyBusinessOverdraft(String offersBusinessBankProduct, String existingOverdraftLimit, String newOverdraftLimit, ExtendedResponseListener<ApplyBusinessOverdraftResponse> extendedResponseListener) {
        submitRequest(new BusinessOverdraftRequest<>(offersBusinessBankProduct, existingOverdraftLimit, newOverdraftLimit, extendedResponseListener));
    }
}