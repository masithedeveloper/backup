/*
 * Copyright (c) 2019 Absa Bank Limited, All Rights Reserved.
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

import com.barclays.absa.banking.framework.AbstractPresenter;
import com.barclays.absa.banking.framework.BaseView;
import com.barclays.absa.banking.model.FicaCheckResponse;
import com.barclays.absa.banking.overdraft.services.OverdraftInteractor;
import com.barclays.absa.banking.overdraft.services.OverdraftService;
import com.barclays.absa.banking.overdraft.ui.OverdraftContracts;

import org.jetbrains.annotations.TestOnly;

import java.lang.ref.WeakReference;

public class OverdraftDeclarationPresenter extends AbstractPresenter implements OverdraftContracts.OverdraftDeclarationPresenter {
    private OverdraftFicaCifStatusResponseListener overdraftFicaCifStatusResponseListener;

    private OverdraftService service;

    OverdraftDeclarationPresenter(WeakReference<? extends BaseView> declarationView) {
        super(declarationView);
        service = new OverdraftInteractor();
        overdraftFicaCifStatusResponseListener = new OverdraftFicaCifStatusResponseListener(this);
    }

    @TestOnly
    public OverdraftDeclarationPresenter(WeakReference<? extends BaseView> declarationView, OverdraftService service) {
        super(declarationView);
        this.service = service;
        overdraftFicaCifStatusResponseListener = new OverdraftFicaCifStatusResponseListener(this);
    }

    @Override
    public void displayNextScreen(FicaCheckResponse successResponse) {
        OverdraftContracts.DeclarationView view = (OverdraftContracts.DeclarationView) viewWeakReference.get();
        dismissProgressIndicator();
        if (view != null && "Y".equalsIgnoreCase(successResponse.getFicaAndCIFStatus())) {
            view.navigateToNextScreen();
        } else {
            view.navigateToFailureScreen(FicaCheckResponse.extractErrorMessage(successResponse));
        }
    }

    @Override
    public void onNextButtonClicked() {
        showProgressIndicator();
        service.fetchFICAAndCIFStatus(overdraftFicaCifStatusResponseListener);
    }
}
