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
package com.barclays.absa.utils;

import com.barclays.absa.banking.boundary.model.CustomerProfileObject;
import com.barclays.absa.banking.boundary.model.SecureHomePageObject;
import com.barclays.absa.banking.boundary.model.UserProfile;
import com.barclays.absa.banking.boundary.monitoring.MonitoringInteractor;
import com.barclays.absa.banking.framework.app.BMBConstants;
import com.barclays.absa.banking.framework.dagger.DaggerHelperKt;
import com.barclays.absa.banking.framework.services.contracts.IAppCacheService;
import com.barclays.absa.banking.framework.utils.BMBLogger;
import com.barclays.absa.crypto.SymmetricCryptoHelper;
import com.barclays.absa.utils.asyncs.AllUserProfilesLoadTask;
import com.barclays.absa.utils.asyncs.LegacyAliasDeleteTask;
import com.barclays.absa.utils.asyncs.LegacyAliasLoadTask;
import com.barclays.absa.utils.asyncs.UserProfileCreateTask;
import com.barclays.absa.utils.asyncs.UserProfileRemovalTask;
import com.barclays.absa.utils.asyncs.UserProfileUpdateTask;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

public class ProfileManager {
    private static final String TAG = ProfileManager.class.getSimpleName();
    private static ProfileManager PROFILE_MANAGER;
    private List<UserProfile> userProfiles = new ArrayList<>();
    private UserProfile activeUserProfile;
    private SymmetricCryptoHelper symmetricCryptoHelper;
    private int profileCount;
    private final IAppCacheService appCacheService = DaggerHelperKt.getServiceInterface(IAppCacheService.class);

    private ProfileManager() {
        loadAllUserProfiles();
    }

    public static ProfileManager getInstance() {
        if (PROFILE_MANAGER == null) {
            PROFILE_MANAGER = new ProfileManager();
        }
        return PROFILE_MANAGER;
    }

    public void loadAllUserProfiles() {
        symmetricCryptoHelper = SymmetricCryptoHelper.getInstance();
        loadAllUserProfiles(new ProfileManager.OnProfileLoadListener() {

            @Override
            public void onAllProfilesLoaded(List<UserProfile> userProfiles) {
                ProfileManager.this.userProfiles = userProfiles;
                ProfileManager.this.profileCount = userProfiles.size();
            }

            @Override
            public void onProfilesLoadFailed() {

            }
        });
    }

    public int getProfileCount() {
        return profileCount;
    }

    public List<UserProfile> getUserProfiles() {
        return userProfiles;
    }

    public void setUserProfiles(List<UserProfile> userProfiles) {
        this.userProfiles = userProfiles;
        profileCount = userProfiles.size();
    }

    public UserProfile getActiveUserProfile() {
        if (activeUserProfile == null) {
            new MonitoringInteractor().logCustomErrorEvent("User Profile is Null");
        }
        return activeUserProfile;
    }

    public void setActiveUserProfile(UserProfile activeUserProfile) {
        this.activeUserProfile = activeUserProfile;
    }

    public UserProfile initialiseUserProfile(String alias, SecureHomePageObject secureHomePageObject) throws SymmetricCryptoHelper.EncryptionFailureException, SymmetricCryptoHelper.KeyStoreEntryAccessException {
        UserProfile userProfile = new UserProfile();
        userProfile.setAlias(alias.getBytes());

        CustomerProfileObject customerProfileObject = CustomerProfileObject.getInstance();
        String profileAlias = customerProfileObject.getAlias();
        if (profileAlias != null) {
            byte[] aliasId = profileAlias.getBytes();
            userProfile.setRandomAliasId(aliasId);
            BMBLogger.d(TAG, "Alias created -> " + alias);
        }
        userProfile.setCustomerName(customerProfileObject.getCustomerName() != null ? customerProfileObject.getCustomerName() : "");
        userProfile.setImageName(secureHomePageObject.getImageName() != null ? secureHomePageObject.getImageName() : "");
        final String userId = generateRandomUserId();
        userProfile.setUserId(userId);
        //zero-key encrypt the enrolling user's alias & store it in the keystore
        byte[] zeroKeyEnryptedAliasId = symmetricCryptoHelper.encryptAliasWithZeroKey(alias.getBytes());
        symmetricCryptoHelper.storeAlias(userId, zeroKeyEnryptedAliasId);
        String randomAliasId = generateRandomUserId();
        userProfile.setRandomAliasId(randomAliasId.getBytes());
        userProfile.setFingerprintId(randomAliasId.getBytes());
        byte[] zeroKeyEnryptedRandomAliasId = symmetricCryptoHelper.encryptAliasWithZeroKey(randomAliasId.getBytes());
        symmetricCryptoHelper.storeAlias(randomAliasId, zeroKeyEnryptedRandomAliasId);
        userProfile.setDateTimestamp(getTodayTimestamp());
        userProfile.setCustomerName(customerProfileObject.getCustomerName());
        userProfile.setTwoFAEnabled(true);
        if (customerProfileObject.getLanguageCode() != null) {
            userProfile.setLanguageCode(customerProfileObject.getLanguageCode());
        }
        if (profileCount == 0) {
            SharedPreferenceService.INSTANCE.setProfileMigrationVersion(userProfile.getMigrationVersion());
        }
        return userProfile;
    }

    public UserProfile initialiseUserProfile(String alias) throws SymmetricCryptoHelper.KeyStoreEntryAccessException, SymmetricCryptoHelper.EncryptionFailureException {
        UserProfile userProfile = new UserProfile();
        userProfile.setAlias(alias.getBytes());

        SecureHomePageObject secureHomePageObject = appCacheService.getSecureHomePageObject();
        if (secureHomePageObject != null) {
            CustomerProfileObject customerProfileObject = secureHomePageObject.getCustomerProfile();
            userProfile.setCustomerName(customerProfileObject.getCustomerName() != null ? customerProfileObject.getCustomerName() : "");
            userProfile.setImageName(secureHomePageObject.getImageName());
            userProfile.setCustomerName(customerProfileObject.getCustomerName());
            if (customerProfileObject.getLanguageCode() != null) {
                userProfile.setLanguageCode(customerProfileObject.getLanguageCode());
            }
            userProfile.setClientType(customerProfileObject.getClientTypeGroup());
        }
        final String userId = generateRandomUserId();
        userProfile.setUserId(userId);
        //zero-key encrypt the enrolling user's alias & store it in the keystore
        byte[] zeroKeyEnryptedAliasId = symmetricCryptoHelper.encryptAliasWithZeroKey(alias.getBytes());
        symmetricCryptoHelper.storeAlias(userId, zeroKeyEnryptedAliasId);
        String randomAliasId = generateRandomUserId();
        userProfile.setRandomAliasId(randomAliasId.getBytes());
        userProfile.setFingerprintId(randomAliasId.getBytes());
        byte[] zeroKeyEnryptedRandomAliasId = symmetricCryptoHelper.encryptAliasWithZeroKey(randomAliasId.getBytes());
        symmetricCryptoHelper.storeAlias(randomAliasId, zeroKeyEnryptedRandomAliasId);
        userProfile.setDateTimestamp(getTodayTimestamp());
        userProfile.setTwoFAEnabled(true);
        if (profileCount == 0) {
            SharedPreferenceService.INSTANCE.setProfileMigrationVersion(userProfile.getMigrationVersion());
        }
        return userProfile;
    }

    public UserProfile initialiseUserProfileFromLegacyProfile(String alias) {
        UserProfile userProfile = new UserProfile();
        userProfile.setAlias(alias.getBytes());
        userProfile.setImageName("");
        userProfile.setUserId(generateRandomUserId());
        userProfile.setDateTimestamp(getTodayTimestamp());
        userProfile.setCustomerName("");
        userProfile.setRandomAliasId(null);

        return userProfile;
    }

    public UserProfile getSelectedUser(int userPosition) {
        if (userProfiles != null && userPosition < userProfiles.size()) {
            return userProfiles.get(userPosition);
        }
        return null;
    }

    public void addProfile(UserProfile userProfile, OnProfileCreateListener callback) {
        UserProfileCreateTask profileCreateTask = new UserProfileCreateTask(userProfile, callback);
        profileCreateTask.execute();
    }

    public void updateProfile(UserProfile userProfile, OnProfileUpdateListener callback) {
        userProfile.setDateTimestamp(getTodayTimestamp());
        new UserProfileUpdateTask(userProfile, callback).execute();
    }

    public void updateProfile(UserProfile userProfile) {
        updateProfile(userProfile, null);
    }

    public void loadLegacyAlias(GenericCallback<String> onAliasLoadListener) {
        new LegacyAliasLoadTask(onAliasLoadListener).execute();
    }

    public void deleteProfile(UserProfile userProfile, SimpleCallback callback) {
        new UserProfileRemovalTask(userProfile, callback).execute();
    }

    public void deleteAllProfiles() {
        for (UserProfile userProfile : userProfiles) {
            new UserProfileRemovalTask(userProfile, null).execute();
        }
    }

    public void loadAllUserProfiles(OnProfileLoadListener callback) {
        new AllUserProfilesLoadTask(callback).execute();
    }

    public void deleteLegacyAlias(SimpleCallback onAliasDeleteListener) {
        new LegacyAliasDeleteTask(onAliasDeleteListener).execute();
    }

    private String getTodayTimestamp() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(BMBConstants.SERVICE_TIMESTAMP_FORMAT, Locale.getDefault());
        return simpleDateFormat.format(new Date());
    }

    public String generateRandomUserId() {
        return UUID.randomUUID().toString();
    }

    public boolean isLegacyUser() {
        return symmetricCryptoHelper.hasAliasRegistered();
    }

    public boolean isFingerprintRegistered() {
        return symmetricCryptoHelper.hasFingerprintRegistered(activeUserProfile.getUserId());
    }

    public interface SimpleCallback {
        void onSuccess();

        void onFailure();
    }

    public interface GenericCallback<T> {
        void onSuccess(T response);

        void onFailure();
    }

    public interface OnProfileCreateListener {
        void onProfileCreated(UserProfile userProfile);

        void onProfileCreateFailed();
    }

    public interface OnProfileUpdateListener {
        void onProfileUpdated(UserProfile userProfile);

        void onProfileUpdateFailed();
    }

    public interface OnProfileLoadListener {
        void onAllProfilesLoaded(List<UserProfile> userProfiles);

        void onProfilesLoadFailed();
    }
}