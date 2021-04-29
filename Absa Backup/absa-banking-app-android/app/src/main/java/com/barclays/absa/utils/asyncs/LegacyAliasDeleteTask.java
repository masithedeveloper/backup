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

import android.os.AsyncTask;
import android.util.Log;

import com.barclays.absa.banking.BuildConfig;
import com.barclays.absa.utils.ProfileManager;
import com.barclays.absa.crypto.SymmetricCryptoHelper;

public class LegacyAliasDeleteTask extends AsyncTask<Void, Void, Boolean> {
    private static final String TAG = LegacyAliasDeleteTask.class.getSimpleName();
    private ProfileManager.SimpleCallback onAliasDeleteListener;

    public LegacyAliasDeleteTask(ProfileManager.SimpleCallback onAliasDeleteListener) {
        this.onAliasDeleteListener = onAliasDeleteListener;
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        try {
            SymmetricCryptoHelper symmetricCryptoHelper = SymmetricCryptoHelper.getInstance();
            symmetricCryptoHelper.deleteLegacyAlias();
            return !symmetricCryptoHelper.hasAliasRegistered();
        } catch (SymmetricCryptoHelper.KeyStoreEntryAccessException e) {
            if (BuildConfig.DEBUG) {
                Log.e(TAG, "Failed to delete the legacy alias", e);
            }
            return false;
        }
    }

    @Override
    protected void onPostExecute(Boolean result) {
        if (result) {
            onAliasDeleteListener.onSuccess();
        } else {
            onAliasDeleteListener.onFailure();
        }
    }
}
