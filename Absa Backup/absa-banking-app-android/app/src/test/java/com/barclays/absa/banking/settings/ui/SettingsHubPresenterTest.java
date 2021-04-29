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

package com.barclays.absa.banking.settings.ui;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.preference.PreferenceManager;

import com.barclays.absa.DaggerTest;
import com.barclays.absa.banking.account.services.AccountInteractor;
import com.barclays.absa.banking.boundary.model.AddBeneficiaryObject;
import com.barclays.absa.banking.boundary.model.CustomerProfileObject;
import com.barclays.absa.banking.boundary.model.ErrorResponseObject;
import com.barclays.absa.banking.boundary.model.ProfileSetupResult;
import com.barclays.absa.banking.boundary.model.SecureHomePageObject;
import com.barclays.absa.banking.boundary.model.policy.Policy;
import com.barclays.absa.banking.boundary.pushNotifications.PushNotificationInteractor;
import com.barclays.absa.banking.boundary.shared.dto.TransactionResponse;
import com.barclays.absa.banking.framework.app.BMBConstants;
import com.barclays.absa.banking.framework.data.ResponseObject;
import com.barclays.absa.banking.settings.services.responseListeners.ChangeLanguageExtendedResponseListener;
import com.barclays.absa.banking.settings.services.responseListeners.ProfileSetupExtendedResponseListener;
import com.barclays.absa.banking.settings.services.responseListeners.PushNotificationsToggleExtendedResponseListener;
import com.barclays.absa.utils.imageHelpers.ProfileViewImageHelper;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

@RunWith(RobolectricTestRunner.class)
@Config(manifest = Config.NONE)
public class SettingsHubPresenterTest extends DaggerTest {

    @Mock
    private SettingsHubView settingsHubViewMock;
    @Mock
    private SettingsHubInteractor settingsHubInteractor;
    @Mock
    private AccountInteractor accountInteractor;
    @Mock
    private PushNotificationInteractor pushNotificationInteractor;
    @Mock
    private ProfileViewImageHelper profileViewHelper;
    @Captor
    private ArgumentCaptor<ChangeLanguageExtendedResponseListener> changeLanguageResponseListener;
    @Captor
    private ArgumentCaptor<ProfileSetupExtendedResponseListener> profileSetupExtendedResponseListenerArgumentCaptor;
    @Captor
    private ArgumentCaptor<PushNotificationsToggleExtendedResponseListener> pushNotificationsToggleExtendedResponseListenerArgumentCaptor;
    private SettingsHubPresenter settingsHubPresenter;
    private char englishLanguageCode;
    private char afrikaansLanguageCode;
    private CustomerProfileObject customerProfileObject;
    private String oldProfilePicture;
    private String newProfilePicture;
    private boolean isPhotoUpdated;
    private boolean isPhotoDeleted;
    private String customerName;
    private String backgroundImageId;
    private Bitmap bitmap;
    private ProfileSetupResult profileSetupResult;
    private AddBeneficiaryObject addBeneficiaryObject;
    private SecureHomePageObject secureHomePageObject;
    private ArrayList<Policy> policyArrayList;
    private SharedPreferences sharedPreferences;

    @Before
    public void setUp() {

        MockitoAnnotations.initMocks(this);
        WeakReference<SettingsHubView> settingsHubViewWeakReference;
        settingsHubViewWeakReference = new WeakReference<>(settingsHubViewMock);
        settingsHubPresenter = new SettingsHubPresenter(settingsHubViewWeakReference, settingsHubInteractor, accountInteractor, pushNotificationInteractor);
        englishLanguageCode = 'E';
        afrikaansLanguageCode = 'A';
        oldProfilePicture = "oldProfilePicture01";
        newProfilePicture = "newProfilePicture01";
        isPhotoUpdated = false;
        isPhotoDeleted = false;
        customerName = "Thabo Lebelo";
        backgroundImageId = "1";
        customerProfileObject = CustomerProfileObject.getInstance();
        customerProfileObject.setLanguageCode(String.valueOf(englishLanguageCode));
        secureHomePageObject = new SecureHomePageObject();
        secureHomePageObject.setCustomerProfile(customerProfileObject);
        addBeneficiaryObject = new AddBeneficiaryObject();
        profileSetupResult = new ProfileSetupResult();
        bitmap = profileViewHelper.getBitmap();
        addBeneficiaryObject.setImageName(oldProfilePicture);
        Policy testPolicy = new Policy();
        policyArrayList = new ArrayList<>();
        policyArrayList.add(testPolicy);
        Context context = RuntimeEnvironment.application.getApplicationContext();
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    @Test
    public void shouldNotChangeLanguagePreferenceOnLanguageUpdateFailure() {
        Assert.assertEquals(String.valueOf(englishLanguageCode), customerProfileObject.getLanguageCode());
        settingsHubPresenter.requestLanguageUpdate(afrikaansLanguageCode);
        verify(settingsHubViewMock).showProgressDialog();
        verify(settingsHubInteractor).requestLanguageUpdate(eq(afrikaansLanguageCode), changeLanguageResponseListener.capture());
        ResponseObject failure = new ErrorResponseObject();
        failure.setErrorMessage(BMBConstants.FAILURE);
        changeLanguageResponseListener.getValue().onFailure(failure);
        Assert.assertEquals(String.valueOf(englishLanguageCode), customerProfileObject.getLanguageCode());
        verify(settingsHubViewMock).responseLanguageUpdate(false);
        verify(settingsHubViewMock).dismissProgressDialog();
        verifyNoMoreInteractions(settingsHubViewMock, settingsHubInteractor);
    }

    @Test
    public void shouldSetupProfilePictureOnFireProfileSetupRequestSuccess() {
        Assert.assertEquals(String.valueOf(oldProfilePicture), addBeneficiaryObject.getImageName());
        bitmap = profileViewHelper.getBitmap();
        profileSetupResult.setStatus(BMBConstants.SUCCESS);
        settingsHubPresenter.fireProfileSetupRequest(String.valueOf(englishLanguageCode), customerName, backgroundImageId, profileViewHelper);
        verify(settingsHubViewMock).showProgressDialog();
        verify(settingsHubInteractor).requestProfileSetup(eq(isPhotoUpdated), eq(isPhotoDeleted), eq(bitmap), eq(String.valueOf(englishLanguageCode)), eq(customerName), eq(backgroundImageId), profileSetupExtendedResponseListenerArgumentCaptor.capture());
        profileSetupResult.setImageName(newProfilePicture);
        addBeneficiaryObject.setImageName(newProfilePicture);
        profileSetupExtendedResponseListenerArgumentCaptor.getValue().onSuccess(profileSetupResult);
        verify(settingsHubViewMock).onProfileImageViewInvalidate(newProfilePicture);
        verify(settingsHubViewMock).dismissProgressDialog();
        Assert.assertEquals(newProfilePicture, addBeneficiaryObject.getImageName());
        verifyNoMoreInteractions(settingsHubViewMock);
    }

    @Test
    public void shouldDisablePushNotificationsOnSwitchPushNotificationsOff() {
        sharedPreferences.edit().putBoolean("IS_NOTIFICATIONS_ENABLED", true).commit();
        Assert.assertTrue(sharedPreferences.getBoolean("IS_NOTIFICATIONS_ENABLED", false));
        TransactionResponse transactionResponse = new TransactionResponse();
        transactionResponse.setResponseMessage(BMBConstants.SUCCESS);
        sharedPreferences.edit().putBoolean("IS_NOTIFICATIONS_ENABLED", false).commit();
        settingsHubPresenter.switchOffPushNotifications("firebase");
        verify(pushNotificationInteractor).removePushNotificationRecord(anyString(), pushNotificationsToggleExtendedResponseListenerArgumentCaptor.capture());
        pushNotificationsToggleExtendedResponseListenerArgumentCaptor.getValue().onSuccess(transactionResponse);
        Assert.assertFalse(sharedPreferences.getBoolean("IS_NOTIFICATIONS_ENABLED", false));
        verifyNoMoreInteractions(pushNotificationInteractor);
    }
}