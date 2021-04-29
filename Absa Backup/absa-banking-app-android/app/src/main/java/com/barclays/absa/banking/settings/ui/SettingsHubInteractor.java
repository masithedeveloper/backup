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

package com.barclays.absa.banking.settings.ui;

import android.graphics.Bitmap;

import com.barclays.absa.banking.boundary.model.ProfileSetupResult;
import com.barclays.absa.banking.boundary.model.SecureHomePageObject;
import com.barclays.absa.banking.boundary.model.UserProfile;
import com.barclays.absa.banking.framework.ExtendedResponseListener;
import com.barclays.absa.banking.framework.ServiceClient;
import com.barclays.absa.banking.settings.services.LanguageUpdateRequest;
import com.barclays.absa.banking.settings.services.ProfileSetupResultRequest;
import com.barclays.absa.utils.ImageUtils;
import com.barclays.absa.utils.ProfileManager;

public class SettingsHubInteractor implements SettingsHubService {

    public void requestLanguageUpdate(char languageCode, ExtendedResponseListener<SecureHomePageObject> changeLanguageExtendedResponseListener) {
        LanguageUpdateRequest<SecureHomePageObject> languageUpdateRequest = new LanguageUpdateRequest<>(languageCode, changeLanguageExtendedResponseListener);
        ServiceClient serviceClient = new ServiceClient(languageUpdateRequest);
        serviceClient.submitRequest();
    }

    @Override
    public void requestProfileSetup(boolean isPhotoRemove, boolean isUpdateProfile, Bitmap profileBitmap,
                                    String languageCode, String setupProfileName,
                                    String backgroundImageId,
                                    ExtendedResponseListener<ProfileSetupResult> extendedResponseListener) {
        String encryptedImage = "";
        try {
            encryptedImage = ImageUtils.convertToBase64(profileBitmap);
        } catch (Exception e) {
            e.printStackTrace();
        }

        UserProfile activeUserProfile = ProfileManager.getInstance().getActiveUserProfile();
        if (activeUserProfile != null) {
            activeUserProfile.setImageName(encryptedImage);
            ProfileManager.getInstance().updateProfile(activeUserProfile, null);
        }

        ProfileSetupResultRequest request = new ProfileSetupResultRequest<>(isPhotoRemove, isUpdateProfile, encryptedImage, languageCode, setupProfileName, backgroundImageId, extendedResponseListener);
        ServiceClient serviceClient = new ServiceClient(request);
        serviceClient.submitRequest();
    }
}