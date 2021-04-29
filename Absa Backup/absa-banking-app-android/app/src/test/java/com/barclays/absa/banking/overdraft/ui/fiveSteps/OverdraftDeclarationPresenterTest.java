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

import com.barclays.absa.banking.framework.ExtendedResponseListener;
import com.barclays.absa.banking.framework.data.ResponseObject;
import com.barclays.absa.banking.model.FicaCheckResponse;
import com.barclays.absa.banking.overdraft.services.OverdraftService;
import com.barclays.absa.banking.overdraft.ui.OverdraftContracts;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.lang.ref.WeakReference;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.validateMockitoUsage;
import static org.mockito.Mockito.verify;


public class OverdraftDeclarationPresenterTest {
    @Mock
    private OverdraftService overdraftInteractor;
    @Captor
    private ArgumentCaptor<ExtendedResponseListener<FicaCheckResponse>> ficaCifStatusExtendedResponseListener;

    private final WeakReference<OverdraftContracts.DeclarationView> weakReferenceView = new WeakReference<>(mock(OverdraftContracts.DeclarationView.class));
    private OverdraftDeclarationPresenter presenter;
    private FicaCheckResponse response = new FicaCheckResponse();

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        presenter = new OverdraftDeclarationPresenter(weakReferenceView, overdraftInteractor);
    }

    @Test
    public void shouldShowFailureScreenIfFicaCifStatusIsNotYes() {
        presenter.onNextButtonClicked();
        OverdraftContracts.DeclarationView view = weakReferenceView.get();
        verify(view).showProgressDialog();
        verify(overdraftInteractor).fetchFICAAndCIFStatus(ficaCifStatusExtendedResponseListener.capture());
        response.setFicaAndCIFStatus("N");
        ficaCifStatusExtendedResponseListener.getValue().onSuccess(response);
        verify(view).dismissProgressDialog();
        verify(view).navigateToFailureScreen(ResponseObject.extractErrorMessage(response));
    }

    @Test
    public void shouldProceedToNextScreenIfFicaCifStatusIsYes() {
        presenter.onNextButtonClicked();
        OverdraftContracts.DeclarationView view = weakReferenceView.get();
        verify(view).showProgressDialog();
        verify(overdraftInteractor).fetchFICAAndCIFStatus(ficaCifStatusExtendedResponseListener.capture());
        response.setFicaAndCIFStatus("Y");
        ficaCifStatusExtendedResponseListener.getValue().onSuccess(response);
        verify(view).dismissProgressDialog();
        verify(view).navigateToNextScreen();
    }

    @After
    public void tearDown() {
        validateMockitoUsage();
    }
}
