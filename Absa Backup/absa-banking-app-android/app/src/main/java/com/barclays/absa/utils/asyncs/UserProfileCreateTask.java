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
import com.barclays.absa.utils.ProfileManager;
import com.barclays.absa.crypto.SymmetricCryptoHelper;

public class UserProfileCreateTask extends AsyncTask<Void, Void, Boolean> {
    private static final String TAG = UserProfileCreateTask.class.getSimpleName();
    private UserProfile userProfile;
    private ProfileManager.OnProfileCreateListener callback;

    public UserProfileCreateTask(UserProfile userProfile, ProfileManager.OnProfileCreateListener callback) {
        this.userProfile = userProfile;
        this.callback = callback;
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        SymmetricCryptoHelper symmetricCryptoHelper = SymmetricCryptoHelper.getInstance();
        UserProfileDAO userProfileDAO = UserProfileDAO.getInstance();
        String userId = userProfile.getUserId();
        if (userProfileDAO.isProfileFound(userId)) {
            return true;
        } else {
            try {

                byte[] alias = userProfile.getAlias();
                byte[] aliasId = userProfile.getRandomAliasId();

                if (alias != null && alias.length > 0) {
                    byte[] encryptedAlias = symmetricCryptoHelper.encryptAliasWithZeroKey(alias);
                    symmetricCryptoHelper.storeAlias(userId, encryptedAlias);
                }

                if (aliasId != null && aliasId.length > 0) {
                    byte[] encryptedAliasid = symmetricCryptoHelper.encryptAliasWithZeroKey(aliasId);
                    symmetricCryptoHelper.storeAliasId(userId, encryptedAliasid);
                }

            } catch (SymmetricCryptoHelper.KeyStoreEntryAccessException | SymmetricCryptoHelper.EncryptionFailureException e) {
                if (BuildConfig.DEBUG) {
                    Log.e(TAG, "Failed to save alias/aliasid in keystore", e);
                }
                return false;
            }

            try {
                long rowId = userProfileDAO.createUserProfile(userProfile);
                return rowId > -1;
            } catch (SQLiteException e) {
                if (BuildConfig.DEBUG) {
                    Log.e(TAG, "Failed to create a user profile:", e);
                }
                return false;
            }
        }
    }

    @Override
    protected void onPostExecute(Boolean result) {
        super.onPostExecute(result);
        if (result) {
            callback.onProfileCreated(userProfile);
        } else {
            callback.onProfileCreateFailed();
        }
    }
}
