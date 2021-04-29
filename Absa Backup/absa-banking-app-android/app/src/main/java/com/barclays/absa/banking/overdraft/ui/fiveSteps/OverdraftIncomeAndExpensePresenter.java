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
package com.barclays.absa.banking.overdraft.ui.fiveSteps;

import androidx.annotation.NonNull;

import com.barclays.absa.banking.boundary.model.overdraft.OverdraftIncomeAndExpensesConfirmationResponse;
import com.barclays.absa.banking.boundary.model.overdraft.OverdraftQuoteDetailsObject;
import com.barclays.absa.banking.boundary.model.overdraft.OverdraftQuoteSummary;
import com.barclays.absa.banking.boundary.model.overdraft.OverdraftResponse;
import com.barclays.absa.banking.framework.AbstractPresenter;
import com.barclays.absa.banking.framework.BaseView;
import com.barclays.absa.banking.framework.dagger.DaggerHelperKt;
import com.barclays.absa.banking.framework.data.cache.IOverdraftCacheService;
import com.barclays.absa.banking.overdraft.services.OverdraftInteractor;
import com.barclays.absa.banking.overdraft.services.OverdraftService;
import com.barclays.absa.banking.overdraft.ui.OverdraftContracts;

import org.jetbrains.annotations.NotNull;

import java.lang.ref.WeakReference;

public class OverdraftIncomeAndExpensePresenter extends AbstractPresenter implements OverdraftContracts.IncomeAndExpensePresenter {

    private OverdraftIncomeAndExpensesExtendedResponseListener incomeAndExpensesExtendedResponseListener;
    private QuoteSummaryExtendedResponseListener quoteSummaryExtendedResponseListener;

    private OverdraftService service;
    private OverdraftQuoteDetailsObject overdraftQuoteDetailsObject;
    private OverdraftResponse overdraftResponse;
    private final IOverdraftCacheService overdraftCacheService = DaggerHelperKt.getServiceInterface(IOverdraftCacheService.class);

    public OverdraftIncomeAndExpensePresenter(@NotNull WeakReference<? extends BaseView> viewWeakReference) {
        super(viewWeakReference);
        service = new OverdraftInteractor();
        incomeAndExpensesExtendedResponseListener = new OverdraftIncomeAndExpensesExtendedResponseListener(this);
        quoteSummaryExtendedResponseListener = new QuoteSummaryExtendedResponseListener(this);
    }

    @Override
    public void onNextButtonClicked(@NonNull OverdraftQuoteDetailsObject detailsObject) {
        this.overdraftQuoteDetailsObject = detailsObject;
        showProgressIndicator();
        service.confirmIncomeAndExpenses(detailsObject, incomeAndExpensesExtendedResponseListener);
    }

    @Override
    public void onIncomeAndExpenseDataReceived(OverdraftIncomeAndExpensesConfirmationResponse response) {
        OverdraftContracts.IncomeAndExpenseView view = (OverdraftContracts.IncomeAndExpenseView) viewWeakReference.get();
        if (response != null && (response.getRespDTO() != null && response.getRespDTO().getQmsDTO() != null)) {
            overdraftResponse = new OverdraftResponse();
            overdraftResponse.setQuoteReferenceNumber(response.getQuoteReferenceNumber());
            overdraftResponse.setApprovedAmount(response.getOverdraftAmount());
            overdraftResponse.setCppAmount(overdraftQuoteDetailsObject.getCppAmount().toString());
            overdraftResponse.setCppChecked(overdraftQuoteDetailsObject.getCheckAgreeQuote());
            overdraftResponse.setSystemDecision(response.getSystemDecision());
            overdraftResponse.setSystemResult(response.getSystemResult());
            if (overdraftResponse != null) {
                overdraftCacheService.setOverdraftResponse(overdraftResponse);
            }
            if (("S".equalsIgnoreCase(response.getSystemDecision()) || "A".equalsIgnoreCase(response.getSystemDecision())) && overdraftResponse.getQuoteReferenceNumber() != null) {
                service.fetchOverdraftQuoteSummary(overdraftResponse.getQuoteReferenceNumber(), quoteSummaryExtendedResponseListener);
            } else if ("V".equalsIgnoreCase(response.getSystemDecision()) || "R".equalsIgnoreCase(response.getSystemDecision()) || "F".equalsIgnoreCase(response.getSystemDecision())) {
                view.navigateToReferralScreen();
            } else {
                view.navigateToPolicyDeclineFailureScreen();
            }
        } else {
            view.navigateToFailureScreen();
        }
    }

    @Override
    public void viewQuoteSummary(OverdraftQuoteSummary quoteSummary) {
        dismissProgressIndicator();
        OverdraftContracts.IncomeAndExpenseView view = (OverdraftContracts.IncomeAndExpenseView) viewWeakReference.get();
        if (view != null) {
            if (quoteSummary.getQuoteDetails() != null) {
                view.navigateToQuoteSummaryScreen(overdraftResponse, quoteSummary);
            } else {
                view.navigateToQuoteWaitingScreen();
            }
        }
    }

    @Override
    public void failureToRetrieveQuote() {
        OverdraftContracts.IncomeAndExpenseView view = (OverdraftContracts.IncomeAndExpenseView) viewWeakReference.get();
        if (view != null) {
            view.navigateToQuoteWaitingScreen();
        }
    }
}