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


import com.barclays.absa.DaggerTest;
import com.barclays.absa.banking.boundary.model.overdraft.OverdraftResponse;
import com.barclays.absa.banking.boundary.model.overdraft.OverdraftSetup;
import com.barclays.absa.banking.framework.ExtendedResponseListener;
import com.barclays.absa.banking.overdraft.services.OverdraftService;
import com.barclays.absa.banking.overdraft.ui.OverdraftContracts;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.lang.ref.WeakReference;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.validateMockitoUsage;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

public class OverdraftSetUpConfirmationPresenterTest extends DaggerTest {
    private OverdraftContracts.OverdraftSetupConfirmationPresenter presenter;
    private final WeakReference<OverdraftContracts.OverdraftSetupConfirmationView> weakReferenceView = new WeakReference<>(mock(OverdraftContracts.OverdraftSetupConfirmationView.class));
    private OverdraftSetup overdraftSetup;
    private OverdraftResponse overdraftResponse;

    @Mock
    OverdraftService interactor;
    @Captor
    private ArgumentCaptor<ExtendedResponseListener<OverdraftResponse>> overdraftScoreResponseListener;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        presenter = new OverdraftSetUpConfirmationPresenter(weakReferenceView, interactor);
        overdraftSetup = Mockito.mock(OverdraftSetup.class);
        overdraftResponse = Mockito.mock(OverdraftResponse.class);
    }

    @Test
    public void shouldShowFailureScreenIfServiceReturnsFailureResponse() {
        OverdraftContracts.OverdraftSetupConfirmationView view = weakReferenceView.get();
        presenter.failureResponse();
        verify(view).navigateToFailureScreen();
        verifyNoMoreInteractions(view);
    }

    @Test
    public void shouldDoServiceCallForOverdraftScoringWhenNextButtonIsClicked() {
        OverdraftContracts.OverdraftSetupConfirmationView view = weakReferenceView.get();
        presenter.onNextButtonClicked(overdraftSetup);
        verify(interactor).fetchOverdraftScore(eq(overdraftSetup), overdraftScoreResponseListener.capture());
        verify(view).showProgressDialog();
        verifyNoMoreInteractions(view);
    }

    @Test
    public void shouldShowPreApprovedScreenIfServiceReturnsSuccessfulResponse() {
        overdraftResponse = new OverdraftResponse();
        overdraftResponse.setPreApprovedInd("Y");
        presenter.setOverdraftSetup(overdraftSetup);
        presenter.onIncomeAndExpenseDataReceived(overdraftResponse);
        OverdraftContracts.OverdraftSetupConfirmationView view = weakReferenceView.get();
        verify(view).dismissProgressDialog();
        verify(view).navigateToIncomeAndExpenseScreen();
        verifyNoMoreInteractions(view);
    }

    @After
    public void tearDown() {
        validateMockitoUsage();
    }
}
