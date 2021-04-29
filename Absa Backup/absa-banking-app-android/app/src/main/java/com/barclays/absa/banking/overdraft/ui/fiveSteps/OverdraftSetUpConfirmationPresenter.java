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
package com.barclays.absa.banking.overdraft.ui.fiveSteps;

import com.barclays.absa.banking.boundary.model.overdraft.OverdraftQuoteDetailsObject;
import com.barclays.absa.banking.boundary.model.overdraft.OverdraftResponse;
import com.barclays.absa.banking.boundary.model.overdraft.OverdraftSetup;
import com.barclays.absa.banking.framework.AbstractPresenter;
import com.barclays.absa.banking.framework.BaseView;
import com.barclays.absa.banking.framework.dagger.DaggerHelperKt;
import com.barclays.absa.banking.framework.data.cache.IOverdraftCacheService;
import com.barclays.absa.banking.framework.utils.BMBLogger;
import com.barclays.absa.banking.overdraft.services.OverdraftInteractor;
import com.barclays.absa.banking.overdraft.services.OverdraftService;
import com.barclays.absa.banking.overdraft.ui.OverdraftContracts;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.TestOnly;

import java.lang.ref.WeakReference;

public class OverdraftSetUpConfirmationPresenter extends AbstractPresenter implements OverdraftContracts.OverdraftSetupConfirmationPresenter {
    private OverdraftScoreResponseListener overdraftScoreResponseListener;
    private OverdraftService overdraftService;
    private OverdraftSetup overdraftSetup;
    private OverdraftQuoteDetailsObject overdraftQuoteDetailsObject;
    private final IOverdraftCacheService overdraftCacheService = DaggerHelperKt.getServiceInterface(IOverdraftCacheService.class);

    OverdraftSetUpConfirmationPresenter(WeakReference<? extends BaseView> overdraftSetupConfirmationView) {
        super(overdraftSetupConfirmationView);
        overdraftScoreResponseListener = new OverdraftScoreResponseListener(this);
        overdraftService = new OverdraftInteractor();
    }

    @TestOnly
    public OverdraftSetUpConfirmationPresenter(WeakReference<? extends BaseView> overdraftSetupConfirmationView, OverdraftService mockedInteractor) {
        super(overdraftSetupConfirmationView);
        overdraftScoreResponseListener = new OverdraftScoreResponseListener(this);
        this.overdraftService = mockedInteractor;
    }

    @Override
    public void onNextButtonClicked(OverdraftSetup overdraftSetup) {
        overdraftService.fetchOverdraftScore(overdraftSetup, overdraftScoreResponseListener);
        this.overdraftSetup = overdraftSetup;
        showProgressIndicator();
    }

    @TestOnly
    @Override
    public void setOverdraftSetup(@NotNull OverdraftSetup overdraftSetup) {
        this.overdraftSetup = overdraftSetup;
    }

    @Override
    public void onIncomeAndExpenseDataReceived(OverdraftResponse overdraftResponse) {
        dismissProgressIndicator();
        if ((overdraftResponse != null) && (overdraftResponse.getPreApprovedInd() != null) && ("Y".equalsIgnoreCase(overdraftResponse.getPreApprovedInd()))) {
            double cppAmount = 0.0;
            try {
                if (overdraftSetup.getCreditProtection()) {
                    cppAmount = Double.parseDouble(overdraftResponse.getCppAmount());
                }
            } catch (NumberFormatException e) {
                BMBLogger.d(e);
            }
            overdraftQuoteDetailsObject = new OverdraftQuoteDetailsObject(
                    overdraftSetup.getAccountNumber(),
                    overdraftResponse.getTotalMonthlyGrossIncome(),
                    overdraftResponse.getTotalMonthlyNetIncome(),
                    overdraftResponse.getTotalMonthlyLivingExpenses(),
                    overdraftResponse.getCustomerBureauCommitments(),
                    overdraftResponse.getCustomerMaintenanceExpenses(),
                    overdraftResponse.getTotalMonthlyDisableIncome(),
                    overdraftSetup.getCreditProtection() ? "yes" : "no", cppAmount);
            overdraftCacheService.setOverdraftQuoteDetails(overdraftQuoteDetailsObject);
            navigateToNextScreen();
        } else {
            navigateToFailureScreen();
        }
    }

    @Override
    public void failureResponse() {
        OverdraftContracts.OverdraftSetupConfirmationView view = (OverdraftContracts.OverdraftSetupConfirmationView) viewWeakReference.get();
        if (view != null) {
            view.navigateToFailureScreen();
        }
    }

    public void navigateToNextScreen() {
        OverdraftContracts.OverdraftSetupConfirmationView view = (OverdraftContracts.OverdraftSetupConfirmationView) viewWeakReference.get();
        if (view != null) {
            view.navigateToIncomeAndExpenseScreen();
        }
    }

    public void navigateToFailureScreen() {
        OverdraftContracts.OverdraftSetupConfirmationView view = (OverdraftContracts.OverdraftSetupConfirmationView) viewWeakReference.get();
        if (view != null) {
            view.navigateToFailureDueScoringScreen();
        }
    }
}