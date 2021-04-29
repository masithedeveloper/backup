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

import com.barclays.absa.banking.overdraft.ui.OverdraftContracts;

import org.junit.Before;
import org.junit.Test;
import org.mockito.MockitoAnnotations;

import java.lang.ref.WeakReference;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

public class OverdraftTellUsAboutYourSelfPresenterTest {

    private OverdraftContracts.TellUsAboutYourSelfView view;
    private OverdraftContracts.TellUsAboutYourSelfPresenter presenter;
    private final WeakReference<OverdraftContracts.TellUsAboutYourSelfView> weakReference = new WeakReference<>(mock(OverdraftContracts.TellUsAboutYourSelfView.class));

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        presenter = new OverdraftTellUsAboutYourSelfPresenter(weakReference);
        view = weakReference.get();
    }

    @Test
    public void onDebtCounsellingOrPendingDebtReviewOrInsolventYesClicked() {
        presenter.onDebtCounsellingOrPendingDebtReviewOrInsolventNoSelected();
        verify(view).showInsolventAndDebtReviewInThePastOptions();
        verifyNoMoreInteractions(view);
    }

    @Test
    public void onDebtCounsellingOrPendingDebtReviewOrInsolventNoClicked() {
        presenter.onDebtCounsellingOrPendingDebtReviewOrInsolventYesSelected();
        verify(view).hideInsolventAndDebtReviewInThePastOptions();
        verify(view).hideDebtReviewReasonsOptions();
        verify(view).hideInsolventDateSelector();
        verifyNoMoreInteractions(view);
    }

    @Test
    public void onInsolventOrUnderDebtReviewYesOptionSelected() {
        presenter.onInsolventOrUnderDebtReviewYesOptionSelected();
        verify(view).showInsolventAndDebtReviewReasons();
        verify(view).showInsolventDateSelector();
        verifyNoMoreInteractions(view);
    }

    @Test
    public void onInsolventOrUnderDebtReviewNoOptionSelected() {
        presenter.onInsolventOrUnderDebtReviewNoOptionSelected();
        verify(view).hideDebtReviewReasonsOptions();
        verify(view).hideInsolventDateSelector();
        verifyNoMoreInteractions(view);
    }

    @Test
    public void onNextButtonClicked() {
        presenter.onNextButtonClicked();
        verify(view).navigateToNextScreen();
        verifyNoMoreInteractions(view);
    }
}