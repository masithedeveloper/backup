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

package com.barclays.absa.banking.presentation.launch;

import com.barclays.absa.banking.presentation.launch.versionCheck.IValNValInteractor;
import com.barclays.absa.utils.ProfileManager;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Mockito.verify;

public class SplashScreenPresenterTest {

    @Mock
    private SplashScreenView splashScreenView;

    @Mock
    private IValNValInteractor appVersionService;

    @Mock
    private ProfileManager profileManager;

    private SplashScreenPresenter splashScreenPresenter;

    @Captor
    private ArgumentCaptor<ProfileManager.OnProfileLoadListener> profileLoadingListener;

    public SplashScreenPresenterTest() {
    }

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        splashScreenPresenter = new SplashScreenPresenter(splashScreenView, profileManager);
    }

    @Test
    public void testUserProfilesPopulatedFailed() {
        splashScreenPresenter.populateUserProfiles();
        verify(profileManager).loadAllUserProfiles(profileLoadingListener.capture());
        profileLoadingListener.getValue().onProfilesLoadFailed();
        verify(splashScreenView).onProfileLoadingFailed();
    }
}
