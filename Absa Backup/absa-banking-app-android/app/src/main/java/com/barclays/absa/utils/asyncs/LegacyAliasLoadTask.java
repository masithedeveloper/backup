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

public class LegacyAliasLoadTask extends AsyncTask<Void, Void, byte[]> {
    private static final String TAG = LegacyAliasLoadTask.class.getSimpleName();
    private ProfileManager.GenericCallback<String> aliasLoadListener;

    public LegacyAliasLoadTask(ProfileManager.GenericCallback<String> aliasLoadListener) {
        this.aliasLoadListener = aliasLoadListener;
    }

    @Override
    protected byte[] doInBackground(Void... voids) {
        SymmetricCryptoHelper symmetricCryptoHelper = SymmetricCryptoHelper.getInstance();
        try {
            return symmetricCryptoHelper.retrieveLegacyAlias();
        } catch (SymmetricCryptoHelper.KeyStoreEntryAccessException | SymmetricCryptoHelper.DecryptionFailureException e) {
            if (BuildConfig.DEBUG) {
                Log.e(TAG, "Failed to load the legacy alias", e);
            }
            return null;
        }
    }

    @Override
    protected void onPostExecute(byte[] alias) {
        if (alias != null && alias.length > 0) {
            aliasLoadListener.onSuccess(new String(alias));
        } else {
            aliasLoadListener.onFailure();
        }
    }
}


