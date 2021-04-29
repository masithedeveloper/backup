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
 */
package com.barclays.absa.banking.overdraft.ui.fiveSteps;

import com.barclays.absa.banking.boundary.model.overdraft.AcceptOverdraftObject;
import com.barclays.absa.banking.boundary.model.overdraft.OverdraftQuoteDetailsObject;
import com.barclays.absa.banking.boundary.model.overdraft.OverdraftResponse;
import com.barclays.absa.banking.boundary.model.overdraft.RejectOverdraftObject;
import com.barclays.absa.banking.directMarketing.services.DirectMarketingInteractor;
import com.barclays.absa.banking.directMarketing.services.dto.MarketingIndicatorResponse;
import com.barclays.absa.banking.explore.ui.DirectMarketingExtendedResponseListener;
import com.barclays.absa.banking.framework.AbstractPresenter;
import com.barclays.absa.banking.framework.BaseView;
import com.barclays.absa.banking.framework.dagger.DaggerHelperKt;
import com.barclays.absa.banking.framework.data.cache.IOverdraftCacheService;
import com.barclays.absa.banking.overdraft.services.OverdraftInteractor;
import com.barclays.absa.banking.overdraft.services.OverdraftService;
import com.barclays.absa.banking.overdraft.ui.OverdraftContracts;

import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.TestOnly;

import java.lang.ref.WeakReference;

public class OverdraftSummaryConfirmationPresenter extends AbstractPresenter implements OverdraftContracts.OverdraftSummaryConfirmationPresenter {

    private OverdraftService service;
    private AcceptQuoteExtendedResponseListener acceptQuoteExtendedResponseListener;
    private RejectQuoteExtendedResponseListener rejectQuoteExtendedResponseListener;
    private DirectMarketingExtendedResponseListener directMarketingExtendedResponseListener;
    OverdraftResponse overdraftResponse;
    private OverdraftQuoteDetailsObject overdraftQuoteDetailsObject;
    private final IOverdraftCacheService overdraftCacheService = DaggerHelperKt.getServiceInterface(IOverdraftCacheService.class);

    OverdraftSummaryConfirmationPresenter(WeakReference<? extends BaseView> overdraftConfirmationView) {
        super(overdraftConfirmationView);
        service = new OverdraftInteractor();
        acceptQuoteExtendedResponseListener = new AcceptQuoteExtendedResponseListener(this);
        rejectQuoteExtendedResponseListener = new RejectQuoteExtendedResponseListener(this);
        directMarketingExtendedResponseListener = new DirectMarketingExtendedResponseListener(this);
        overdraftResponse = overdraftCacheService.getOverdraftResponse();
        overdraftQuoteDetailsObject = overdraftCacheService.getOverdraftQuoteDetails();
    }

    @TestOnly
    public OverdraftSummaryConfirmationPresenter(WeakReference<? extends BaseView> overdraftConfirmationView, OverdraftService service) {
        super(overdraftConfirmationView);
        this.service = service;
        acceptQuoteExtendedResponseListener = new AcceptQuoteExtendedResponseListener(this);
        rejectQuoteExtendedResponseListener = new RejectQuoteExtendedResponseListener(this);
    }

    @Override
    public void acceptOverdraftQuoteButtonClicked() {
        String accountNumber = overdraftQuoteDetailsObject.getAccountNumber();
        String cppAmount = overdraftResponse.getCppAmount();
        String isCppChecked = overdraftResponse.isCppChecked();
        String quoteNumber = overdraftResponse.getQuoteReferenceNumber();
        String overdraftAmount = overdraftResponse.getApprovedAmount();
        String systemDecision = overdraftResponse.getSystemDecision();
        String systemResult = overdraftResponse.getSystemResult();
        AcceptOverdraftObject acceptOverdraftObject = new AcceptOverdraftObject(accountNumber, cppAmount, isCppChecked, quoteNumber,
                overdraftAmount, systemDecision, systemResult);

        showProgressIndicator();
        service.acceptOverdraftQuote(acceptOverdraftObject, acceptQuoteExtendedResponseListener);
    }

    @Override
    public void rejectOverdraftQuoteButtonClicked() {
        String accountNumber = overdraftQuoteDetailsObject.getAccountNumber();
        String cppAmount = overdraftResponse.getCppAmount();
        String quoteNumber = overdraftResponse.getQuoteReferenceNumber();
        String overdraftAmount = overdraftResponse.getApprovedAmount();
        RejectOverdraftObject rejectOverdraftObject = new RejectOverdraftObject(accountNumber, cppAmount, quoteNumber, overdraftAmount);
        showProgressIndicator();
        service.rejectOverdraftQuote(rejectOverdraftObject, rejectQuoteExtendedResponseListener);
    }

    @Override
    public void decideLaterButtonClicked() {
        OverdraftContracts.OverdraftConfirmationView view = (OverdraftContracts.OverdraftConfirmationView) viewWeakReference.get();
        if (view != null) {
            view.navigateToOfferPostponedScreen();
        }
    }

    @Override
    public void quoteRejectedResponse(@Nullable OverdraftResponse overdraftResponse) {
        OverdraftContracts.OverdraftConfirmationView view = (OverdraftContracts.OverdraftConfirmationView) viewWeakReference.get();
        dismissProgressIndicator();
        if (view != null) {
            if (overdraftResponse != null) {
                view.navigateToOfferRejectedScreen();
            } else {
                view.navigateToFailureScreen();
            }
        }
    }

    public void marketingIndicatorResponse(@Nullable MarketingIndicatorResponse marketingIndicatorResponse) {
        OverdraftContracts.OverdraftConfirmationView view = (OverdraftContracts.OverdraftConfirmationView) viewWeakReference.get();
        dismissProgressIndicator();
        if (view != null && marketingIndicatorResponse != null) {
            view.navigateToDirectMarketingScreen(marketingIndicatorResponse);
        }
    }

    @Override
    public void quoteAcceptedResponse(@Nullable OverdraftResponse overdraftResponse) {
        OverdraftContracts.OverdraftConfirmationView view = (OverdraftContracts.OverdraftConfirmationView) viewWeakReference.get();
        dismissProgressIndicator();
        if (view != null) {
            if (overdraftResponse != null && "S".equalsIgnoreCase(overdraftResponse.getSystemDecision())) {
                new DirectMarketingInteractor().getMarketingIndicators(directMarketingExtendedResponseListener);
            } else {
                view.navigateToFailureScreen();
            }
        }
    }
}