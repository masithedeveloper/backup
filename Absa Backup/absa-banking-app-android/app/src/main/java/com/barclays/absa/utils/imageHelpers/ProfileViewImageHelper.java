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

package com.barclays.absa.utils.imageHelpers;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

import com.barclays.absa.banking.boundary.model.AddBeneficiaryObject;
import com.barclays.absa.banking.boundary.model.CustomerProfileObject;
import com.barclays.absa.banking.boundary.model.UserProfile;
import com.barclays.absa.banking.framework.BaseActivity;
import com.barclays.absa.banking.framework.BaseView;
import com.barclays.absa.banking.framework.ExtendedResponseListener;
import com.barclays.absa.banking.framework.app.BMBConstants;
import com.barclays.absa.banking.home.services.HomeScreenInteractor;
import com.barclays.absa.banking.home.services.HomeScreenService;
import com.barclays.absa.banking.settings.ui.ProfileViewHelper;
import com.barclays.absa.utils.ProfileManager;
import com.barclays.absa.utils.SharedPreferenceService;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import styleguide.content.Profile;
import styleguide.content.ProfileView;
import styleguide.widgets.RoundedImageView;

public class ProfileViewImageHelper extends ProfileViewBaseImageHelper {
    private ProfileView profileView;
    private HomeScreenService homeScreenInteractor;

    private ExtendedResponseListener<AddBeneficiaryObject> fetchProfileImageResponseListener = new ExtendedResponseListener<AddBeneficiaryObject>() {
        @Override
        public void onRequestStarted() {
        }

        @Override
        public void onSuccess(AddBeneficiaryObject successResponse) {
            if (successResponse.getUserNumber() != null) {
                int userNumber = Integer.parseInt(successResponse.getUserNumber());
                UserProfile activeUserProfile = ProfileManager.getInstance().getActiveUserProfile();
                activeUserProfile.setUserNumber(userNumber);
                ProfileManager.getInstance().updateProfile(activeUserProfile, null);
                profileView.setBackgroundImageVisible();
                profileView.setProfile(new Profile(activeUserProfile.getCustomerName(), activeUserProfile.getClientType(), getImageBitmap()));
            }
        }
    };

    public ProfileViewImageHelper(Activity activity, ProfileView profileImageView) {
        super(activity, profileImageView);
    }

    public ProfileViewImageHelper(Activity activity, RoundedImageView roundedImageView) {
        super(activity, roundedImageView);
    }

    public ProfileViewImageHelper(Activity activity) {
        super(activity);
        homeScreenInteractor = new HomeScreenInteractor();
        fetchProfileImageResponseListener.setView((BaseView) activity);
    }

    public void profileView(ProfileView profileView) {
        this.profileView = profileView;
        profileView.setBackgroundImageVisible();
        profileView.setProfile(new Profile(CustomerProfileObject.getInstance().getCustomerName(),
                ProfileViewHelper.INSTANCE.getCustomerAccountType((BaseActivity) activity),
                getImageBitmap()));
        if (SharedPreferenceService.INSTANCE.isReloadProfilePic()) {
            SharedPreferenceService.INSTANCE.setReloadProfilePic(false);
            fetchProfileImage();
        }
    }

    private void fetchProfileImage() {
        SimpleDateFormat df = new SimpleDateFormat(BMBConstants.SERVICE_TIMESTAMP_FORMAT, Locale.getDefault());
        String timestamp = df.format(new Date());
        homeScreenInteractor.fetchProfileImage(timestamp, BMBConstants.MIME_TYPE_JPG, fetchProfileImageResponseListener);
    }

    @Override
    public boolean hasDatabaseImage() {
        return super.hasDatabaseImage();
    }

    @Override
    public byte[] getImageData() {
        String imageName = ProfileManager.getInstance().getActiveUserProfile().getImageName();
        return getDatabaseImage(imageName);
    }

    public Bitmap getImageBitmap() {
        UserProfile activeUserProfile = ProfileManager.getInstance().getActiveUserProfile();
        if (activeUserProfile != null) {
            String imageName = activeUserProfile.getImageName();
            if (imageName != null && !imageName.isEmpty()) {
                byte[] imageData = Base64.decode(imageName, Base64.URL_SAFE);
                if (imageData != null) {
                    bitmap = BitmapFactory.decodeByteArray(imageData, 0, imageData.length);
                    return bitmap;
                }
            }
        }
        return null;
    }
}