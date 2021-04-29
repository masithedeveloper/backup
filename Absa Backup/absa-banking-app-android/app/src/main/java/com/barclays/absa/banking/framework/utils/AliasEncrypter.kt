/*
 * Copyright (c) 2019 Absa Bank Limited, All Rights Reserved.
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

package com.barclays.absa.banking.framework.utils

import android.os.Looper
import android.os.NetworkOnMainThreadException
import android.util.Base64
import com.barclays.absa.banking.framework.app.BMBConstants
import com.barclays.absa.banking.framework.dagger.getServiceInterface
import com.barclays.absa.banking.framework.services.contracts.IAppCacheService
import com.barclays.absa.crypto.AsymmetricCryptoHelper
import com.barclays.absa.crypto.SecureUtils
import com.barclays.absa.crypto.SymmetricCryptoHelper
import net.sqlcipher.BuildConfig

class AliasEncrypter {

    companion object {
        @JvmField
        val TAG: String = this::class.java.simpleName

        @JvmStatic
        @Throws(NetworkOnMainThreadException::class)
        fun encryptAlias(userProfileId: String?, forAuth: Boolean): String {

            if (Looper.myLooper() == Looper.getMainLooper()) {
                throw NetworkOnMainThreadException()
            }

            val twoFactorSpecialFormIdentifier = "__absa2Factor"
            val authResponsePrefix = "credential:"

            var transportableAlias: String
            val appCacheService: IAppCacheService = getServiceInterface()

            try {
                val symmetricCryptoHelper = SymmetricCryptoHelper.getInstance()
                val zeroKeyEncryptedAlias = symmetricCryptoHelper.retrieveAlias(userProfileId)
                val decryptedAlias = symmetricCryptoHelper.decryptAliasWithZeroKey(zeroKeyEncryptedAlias)
                val alias = String(decryptedAlias)
                BMBLogger.d(TAG, "Alias ID is $alias")
                val credential = appCacheService.getAuthCredential()
                BMBLogger.d(TAG, "Credential is $credential")
                val credentialType = appCacheService.getAuthCredentialType()
                BMBLogger.d(TAG, "Credential type is $credentialType")
                val deviceId = SecureUtils.getDeviceID()
                val deriveCredentialBytes = symmetricCryptoHelper.deriveCredential(alias, credential, deviceId)

                val symmetricKey = symmetricCryptoHelper.generateKey()
                symmetricCryptoHelper.setSymmetricKey(symmetricKey)
                val symmetricKeyBuffer = symmetricCryptoHelper.secretKeyBytes
                val encryptedSymmetricKey = AsymmetricCryptoHelper.getInstance().encryptSymmetricKey(symmetricKeyBuffer)
                val encodedEncryptedSymmetricKey = Base64.encodeToString(encryptedSymmetricKey, Base64.NO_WRAP)

                val encryptedCredentialBytes = symmetricCryptoHelper.encryptCredential(deriveCredentialBytes)
                val encodedEncryptedDerivedCredential = Base64.encodeToString(encryptedCredentialBytes, Base64.NO_WRAP)

                val encryptedAliasIdBuffer = symmetricCryptoHelper.encryptAlias(alias)
                val encodedEncryptedAliasId = Base64.encodeToString(encryptedAliasIdBuffer, Base64.NO_WRAP)

                val encodedDeviceId = Base64.encodeToString(deviceId.toByteArray(), Base64.NO_WRAP)

                val versionNameBytes = Base64.encodeToString(BuildConfig.VERSION_NAME.toByteArray(), Base64.NO_WRAP)
                transportableAlias = "${BMBConstants.CLIENT_APPLICATION_KEY_ID}.$credentialType.${encodedEncryptedSymmetricKey!!}.${encodedEncryptedDerivedCredential!!}.${encodedEncryptedAliasId!!}"
                transportableAlias = if (forAuth) {
                    val authPrefix = authResponsePrefix + twoFactorSpecialFormIdentifier
                    "$authPrefix.$transportableAlias"
                } else {
                    "$transportableAlias.$encodedDeviceId.$versionNameBytes"
                }
                val loggingPrefix: String = if (forAuth) {
                    "Transakt auth response from user was"
                } else {
                    "Digital identity is"
                }
                BMBLogger.d(TAG, "$loggingPrefix $transportableAlias")
                return transportableAlias
            } catch (e: Exception) {
                BMBLogger.e(TAG, e.message)
                e.printStackTrace()
            }
            return "android.alias.notFound"
        }
    }
}