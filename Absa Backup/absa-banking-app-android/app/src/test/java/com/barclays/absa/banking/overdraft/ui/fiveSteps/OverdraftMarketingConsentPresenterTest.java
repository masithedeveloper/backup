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

import com.barclays.absa.banking.overdraft.ui.OverdraftContracts;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.MockitoAnnotations;

import java.lang.ref.WeakReference;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.validateMockitoUsage;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

public class OverdraftMarketingConsentPresenterTest {

    private OverdraftMarketingConsentPresenter presenter;
    private final WeakReference<OverdraftContracts.MarketingConsentView> weakReferenceView = new WeakReference<>(mock(OverdraftContracts.MarketingConsentView.class));

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        presenter = new OverdraftMarketingConsentPresenter(weakReferenceView);
    }

    @Test
    public void shouldNavigateToNextScreen() {
        presenter.onNextButtonClicked();
        OverdraftContracts.MarketingConsentView view = weakReferenceView.get();
        verify(view).navigateToOverdraftSetupConfirmationScreen();
        verifyNoMoreInteractions(view);
    }

    @Test
    public void shouldShowMarketingConsentPreferredCommunicationChannelsIfMarketingConsentIsClicked() {
        OverdraftContracts.MarketingConsentView view = weakReferenceView.get();
        presenter.marketingConsentChecked();
        verify(view).showMarketingConsentChannelOptions();
        verifyNoMoreInteractions(view);
    }

    @Test
    public void shouldHideMarketingConsentPreferredCommunicationChannelsIfMarketingConsentIsNotClicked() {
        OverdraftContracts.MarketingConsentView view = weakReferenceView.get();
        presenter.marketingConsentNotChecked();
        verify(view).hideMarketingConsentChannelOptions();
        verifyNoMoreInteractions(view);
    }

    @After
    public void tearDown() {
        validateMockitoUsage();
    }
}
