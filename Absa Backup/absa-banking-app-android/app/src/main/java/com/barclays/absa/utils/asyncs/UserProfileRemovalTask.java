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
import com.barclays.absa.banking.boundary.model.AddBeneficiaryObject;
import com.barclays.absa.banking.boundary.model.UserProfile;
import com.barclays.absa.banking.framework.app.BMBApplication;
import com.barclays.absa.banking.framework.app.BMBConstants;
import com.barclays.absa.banking.framework.data.cache.AddBeneficiaryDAO;
import com.barclays.absa.banking.framework.data.database.UserProfileDAO;
import com.barclays.absa.banking.framework.utils.BMBLogger;
import com.barclays.absa.crypto.SymmetricCryptoHelper;
import com.barclays.absa.utils.ProfileManager;
import com.barclays.absa.utils.SharedPreferenceService;

public class UserProfileRemovalTask extends AsyncTask<Void, Void, Boolean> {
    private static final String TAG = UserProfileRemovalTask.class.getSimpleName();
    private UserProfile userProfile;
    private ProfileManager.SimpleCallback callback;

    public UserProfileRemovalTask(UserProfile userProfile, ProfileManager.SimpleCallback callback) {
        this.userProfile = userProfile;
        this.callback = callback;
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        SymmetricCryptoHelper symmetricCryptoHelper = SymmetricCryptoHelper.getInstance();
        UserProfileDAO userProfileDAO = UserProfileDAO.getInstance();
        if (userProfile != null) {
            String userId = userProfile.getUserId();
            if (userProfileDAO.isProfileFound(userId)) {
                try {
                    symmetricCryptoHelper.deleteAlias(userId);
                    symmetricCryptoHelper.deleteAliasId(userId);
                    symmetricCryptoHelper.deleteFingerprintId(userId);
                } catch (SymmetricCryptoHelper.KeyStoreEntryAccessException e) {
                    if (BuildConfig.DEBUG) {
                        Log.e(TAG, "Failed to delete alias from keystore:", e);
                    }
                    return false;
                }
                try {
                    AddBeneficiaryDAO deleteProfileImgDAO = new AddBeneficiaryDAO(BMBApplication.getInstance());
                    AddBeneficiaryObject deleteBenObject = deleteProfileImgDAO.getBeneficiary(userProfile.getImageName());
                    if (deleteBenObject != null) {
                        deleteProfileImgDAO.deleteBeneficiaryFromId(deleteBenObject.getImageName(), BMBConstants.PROFILE_BEN_TYPE);
                    }
                    int rowsAffected = userProfileDAO.deleteUserProfile(userProfile.getUserId());
                    if (userProfile.getUserId() != null) {
                        SharedPreferenceService.INSTANCE.removeInternationalPaymentsHowItWorksFirstVisitDone(userProfile.getUserId());
                    }
                    return rowsAffected > 0;
                } catch (SQLiteException e) {
                    if (BuildConfig.DEBUG) {
                        BMBLogger.e(TAG, "Failed to delete user profile: " + e);
                    }
                    return false;
                }
            } else {
                return false;
            }
        }
        return false;
    }

    @Override
    protected void onPostExecute(Boolean result) {
        if (callback != null) {
            if (result) {
                callback.onSuccess();
            } else {
                callback.onFailure();
            }
        }
    }
}
