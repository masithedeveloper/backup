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

import android.util.Base64;

import com.barclays.absa.banking.framework.BuildConfigHelper;
import com.barclays.absa.banking.framework.dagger.DaggerHelperKt;
import com.barclays.absa.banking.framework.services.contracts.IAppCacheService;
import com.barclays.absa.crypto.SymmetricCryptoHelper;

import java.io.IOException;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

public final class AliasReader {

    public static String readLinkingAlias() throws SymmetricCryptoHelper.KeyStoreEntryAccessException, SymmetricCryptoHelper.DecryptionFailureException, IOException {
        IAppCacheService appCacheService = DaggerHelperKt.getServiceInterface(IAppCacheService.class);
        if (BuildConfigHelper.STUB) {
            return "StubAlias";
        }
        String alias = null;
        final String base64EncodedEncryptedAliasId = appCacheService.getEnrollingUserAliasID();
        SymmetricCryptoHelper symmetricCryptoHelper = SymmetricCryptoHelper.getInstance();
        byte[] bytesOfSymmetricKeyThatWasUsedForCreatingAlias = symmetricCryptoHelper.retrieveAlias(SymmetricCryptoHelper.CREATE_ALIAS_SYMMETRIC_KEY);
        if (bytesOfSymmetricKeyThatWasUsedForCreatingAlias != null) {
            SecretKey createAliasSymmetricKey = new SecretKeySpec(bytesOfSymmetricKeyThatWasUsedForCreatingAlias, "AES");
            if (base64EncodedEncryptedAliasId != null) {
                byte[] encryptedAliasId = Base64.decode(base64EncodedEncryptedAliasId, Base64.DEFAULT);
                if (encryptedAliasId != null) {
                    byte[] decryptedAlias = symmetricCryptoHelper.decryptAlias(createAliasSymmetricKey, encryptedAliasId);
                    if (decryptedAlias != null) {
                        alias = new String(decryptedAlias);
                    }
                }
            }
        }
        return alias;
    }
}