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
package com.barclays.absa.banking.overdraft.ui;

import org.junit.Before;
import org.junit.Test;
import org.mockito.MockitoAnnotations;

import java.lang.ref.WeakReference;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class OverdraftIntroPresenterTest {

    private OverdraftContracts.OverdraftIntroView view;
    private OverdraftContracts.OverdraftIntroPresenter presenter;
    private final WeakReference<OverdraftContracts.OverdraftIntroView> weakReference = new WeakReference<>(mock(OverdraftContracts.OverdraftIntroView.class));

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        presenter = new OverdraftIntroPresenter(weakReference);
        view = weakReference.get();
    }

    @Test
    public void shouldOpenAbsaWebsite() {
        presenter.onAbsaWebsiteClicked();
        verify(view).navigateToAbsaWebsite();
    }

    @Test
    public void shouldOpenOverdraftStep1() {
        presenter.applyNowButtonClicked();
        verify(view).navigateToApplyOverdraftStep1();
    }
}