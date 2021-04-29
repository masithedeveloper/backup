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
package com.barclays.absa.utils.asyncs;

import android.database.sqlite.SQLiteException;
import android.os.AsyncTask;
import android.util.Log;

import com.barclays.absa.banking.BuildConfig;
import com.barclays.absa.banking.boundary.model.UserProfile;
import com.barclays.absa.banking.framework.data.database.UserProfileDAO;
import com.barclays.absa.crypto.SymmetricCryptoHelper;
import com.barclays.absa.utils.ProfileManager;

public class UserProfileUpdateTask extends AsyncTask<Void, Void, Boolean> {
    private static final String TAG = UserProfileUpdateTask.class.getSimpleName();
    private UserProfile userProfile;
    private ProfileManager.OnProfileUpdateListener callback;

    public UserProfileUpdateTask(UserProfile userProfile, ProfileManager.OnProfileUpdateListener callback) {
        this.userProfile = userProfile;
        this.callback = callback;
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        UserProfileDAO userProfileDAO = UserProfileDAO.getInstance();
        SymmetricCryptoHelper symmetricCryptoHelper = SymmetricCryptoHelper.getInstance();
        String userId = userProfile.getUserId();
        if (userProfileDAO.isProfileFound(userId)) {
            try {
                int rowAffected = userProfileDAO.updateUserProfile(userProfile);
                byte[] fingerprintId = userProfile.getFingerprintId();
                byte[] randomAliasId = userProfile.getRandomAliasId();

                if (fingerprintId != null && fingerprintId.length > 0) {
                    byte[] encryptedFingerprintId = symmetricCryptoHelper.encryptAliasWithZeroKey(fingerprintId);
                    symmetricCryptoHelper.storeFingerprintId(userId, encryptedFingerprintId);
                } else {
                    symmetricCryptoHelper.deleteFingerprintId(userId);
                }
                if (randomAliasId != null && randomAliasId.length > 0) {
                    byte[] encryptedRandomAliasId = symmetricCryptoHelper.encryptAliasWithZeroKey(randomAliasId);
                    symmetricCryptoHelper.storeAliasId(userId, encryptedRandomAliasId);
                } else {
                    symmetricCryptoHelper.deleteAliasId(userId);
                }
                return rowAffected > 0;
            } catch (SQLiteException | SymmetricCryptoHelper.KeyStoreEntryAccessException | SymmetricCryptoHelper.EncryptionFailureException e) {
                if (BuildConfig.DEBUG) {
                    Log.e(TAG, "Failed to update the user profile:", e);
                }
                return false;
            }
        } else {
            return false;
        }
    }

    @Override
    protected void onPostExecute(Boolean result) {
        if (callback != null) {
            if (result) {
                //TODO Need a bit of improvement here later
                ProfileManager.getInstance().loadAllUserProfiles();
                callback.onProfileUpdated(userProfile);
            } else {
                callback.onProfileUpdateFailed();
            }
        }
    }
}