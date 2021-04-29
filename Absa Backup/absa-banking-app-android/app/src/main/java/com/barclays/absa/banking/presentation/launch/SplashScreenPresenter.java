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
package com.barclays.absa.banking.presentation.launch;

import com.barclays.absa.banking.boundary.model.UserProfile;
import com.barclays.absa.banking.framework.utils.BMBLogger;
import com.barclays.absa.utils.ProfileManager;

import java.util.List;

class SplashScreenPresenter implements SplashPresenterInterface {

    private static final String TAG = SplashScreenPresenter.class.getSimpleName();
    private SplashScreenView view;
    private int profileCount = 0;
    private ProfileManager profileManager;
    private ProfileManager.OnProfileLoadListener profileLoadingListener = new ProfileManager.OnProfileLoadListener() {

        @Override
        public void onAllProfilesLoaded(List<UserProfile> userProfiles) {
            BMBLogger.d(TAG, "onAllProfilesLoaded " + userProfiles.size());
            profileManager = ProfileManager.getInstance();
            profileManager.setUserProfiles(userProfiles);
            profileCount = userProfiles.size();

            //check if device has legacy profile
            if (profileManager.isLegacyUser()) {
                migrateLegacyUser(this);
            } else {
                if (userProfiles.size() > 0) {
                    profileManager.setActiveUserProfile(userProfiles.get(0));
                }
                view.onProfilesLoaded();
            }
        }

        @Override
        public void onProfilesLoadFailed() {
            BMBLogger.d(TAG, "onProfilesLoadFailed");
            view.onProfileLoadingFailed();
        }
    };

    SplashScreenPresenter(SplashScreenView view, ProfileManager profileManager) {
        this.view = view;
        this.profileManager = profileManager;
    }

    public void populateUserProfiles() {
        profileManager.loadAllUserProfiles(profileLoadingListener);
    }

    private void migrateLegacyUser(final ProfileManager.OnProfileLoadListener profileLoadingListener) {
        profileManager.loadLegacyAlias(new ProfileManager.GenericCallback<String>() {
            @Override
            public void onSuccess(String alias) {
                UserProfile userProfile = ProfileManager.getInstance().initialiseUserProfileFromLegacyProfile(alias);
                ProfileManager.getInstance().addProfile(userProfile, new ProfileManager.OnProfileCreateListener() {
                    @Override
                    public void onProfileCreated(final UserProfile savedUserProfile) {
                        profileManager.deleteLegacyAlias(new ProfileManager.SimpleCallback() {

                            @Override
                            public void onSuccess() {
                                profileManager.loadAllUserProfiles(profileLoadingListener);
                            }

                            @Override
                            public void onFailure() {
                                BMBLogger.e(TAG, "failed deleting legacy user");
                            }
                        });
                    }

                    @Override
                    public void onProfileCreateFailed() {
                        BMBLogger.e(TAG, "failed creating legacy user");
                    }
                });
            }

            @Override
            public void onFailure() {
                BMBLogger.e(TAG, "failed loading legacy user");
            }
        });
    }

    @Override
    public boolean isDeviceLinked() {
        return profileCount > 0;
    }
}