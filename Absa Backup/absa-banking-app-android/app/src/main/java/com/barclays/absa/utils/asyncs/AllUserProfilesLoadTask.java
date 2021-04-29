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

import java.util.List;

public class AllUserProfilesLoadTask extends AsyncTask<Void, Void, List<UserProfile>> {
    private static final String TAG = AllUserProfilesLoadTask.class.getSimpleName();
    private ProfileManager.OnProfileLoadListener callback;

    public AllUserProfilesLoadTask(ProfileManager.OnProfileLoadListener callback) {
        this.callback = callback;
    }

    @Override
    protected List<UserProfile> doInBackground(Void... params) {
        UserProfileDAO userProfileDAO = UserProfileDAO.getInstance();
        SymmetricCryptoHelper symmetricCryptoHelper = SymmetricCryptoHelper.getInstance();
        try {
            List<UserProfile> userProfiles = userProfileDAO.loadAllUserProfiles();
            for (UserProfile userProfile : userProfiles) {
                byte[] alias = symmetricCryptoHelper.retrieveAlias(userProfile.getUserId());
                byte[] aliasId = symmetricCryptoHelper.retrieveAliasId(userProfile.getUserId());
                byte[] fingerprintId = symmetricCryptoHelper.retrieveFingerprintId(userProfile.getUserId());

                userProfile.setAlias(alias);
                userProfile.setRandomAliasId(aliasId);
                userProfile.setFingerprintId(fingerprintId);
            }
            return userProfiles;
        } catch (SQLiteException | SymmetricCryptoHelper.KeyStoreEntryAccessException | SymmetricCryptoHelper.DecryptionFailureException e) {
            if (BuildConfig.DEBUG) {
                Log.e(TAG, "Failed to load user profiles", e);
            }
            return null;
        }
    }

    @Override
    protected void onPostExecute(List<UserProfile> userProfiles) {
        if (callback != null) {
            if (userProfiles == null) {
                callback.onProfilesLoadFailed();
            } else {
                ProfileManager.getInstance().setUserProfiles(userProfiles);
                callback.onAllProfilesLoaded(userProfiles);
            }
        }
    }
}
