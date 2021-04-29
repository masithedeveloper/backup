/*
 * Copyright (c) 2020 Absa Bank Limited, All Rights Reserved.
 *
 * This code is confidential to Absa Bank Limited and shall not be disclosed
 * outside the Bank without the prior written permission of the Absa Legal
 *
 * In the event that such disclosure is permitted the code shall not be copied
 * or distributed other than on a need-to-know basis and any recipients may be
 * required to sign a confidentiality undertaking in favor of Absa Bank
 * Limited
 */

package com.barclays.absa.crypto

import android.util.Log
import androidx.preference.PreferenceManager
import com.barclays.absa.banking.BuildConfig
import com.barclays.absa.banking.framework.app.BMBApplication
import com.barclays.absa.banking.framework.app.BMBConstants
import com.barclays.absa.utils.CommonUtils
import com.barclays.absa.utils.DeviceUtils
import com.barclays.absa.utils.DeviceUtils.deviceUuid
import com.barclays.absa.utils.DeviceUtils.hasUuid
import com.barclays.absa.utils.SharedPreferenceService
import za.co.absa.networking.crypto.SHA256Encrytion

object SecureUtils {

    private val TAG = SecureUtils::class.java.simpleName

    const val NEW_DEVICE_ID = "newDeviceID"
    private val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(BMBApplication.getInstance())

    fun getDeviceID(): String {
        val oldKey = sharedPreferences.getString(BMBConstants.STORED_KEY, "") ?: ""
        val encryptedToken = getDeviceLinkingEncryptedToken()

        val newKey = if (hasUuid() || encryptedToken.isEmpty()) deviceUuid else readDeviceIdentifier()
        val token: String

        //if there is an old key use it to decrypt the token, then re-encrypt it using the new key; then save it
        if (oldKey.isNotEmpty()) {
            token = decryptTokenWithOldKey(oldKey, encryptedToken)
            encryptAndSaveToken(newKey, token)
            removeOldKey()
        } else {
            token = generateMessageDigestUsingKey(newKey, encryptedToken)
        }
        return token
    }

    private fun generateMessageDigestUsingKey(key: String, encryptedToken: String): String {
        var token: String
        require(key.isNotEmpty()) { "Key cannot be null or empty..." }

        if (encryptedToken.isEmpty()) {
            token = generateFreshToken()
            encryptAndSaveToken(key, token)
            sharedPreferences.edit().putBoolean(BMBConstants.USES_NEW_CRYPTO, true).apply()
        } else {
            try {
                val shouldUseNewCrypto = sharedPreferences.getBoolean(BMBConstants.USES_NEW_CRYPTO, false)
                token = AESEncryption.decrypt(key, encryptedToken, shouldUseNewCrypto)
                if (!shouldUseNewCrypto) {
                    encryptAndSaveToken(key, token)
                    sharedPreferences.edit().putBoolean(BMBConstants.USES_NEW_CRYPTO, true).apply()
                }
            } catch (e: Exception) {
                token = generateFreshToken()
                encryptAndSaveToken(key, token)
                if (BuildConfig.DEBUG) {
                    Log.e(TAG, "Failed to decrypt stored token, generating new token")
                }
            }
        }
        return token
    }

    fun setHasNewDeviceID(hasNewDeviceID: Boolean) = SharedPreferenceService.setBoolean(NEW_DEVICE_ID, hasNewDeviceID)

    private fun hasNewDeviceID(): Boolean = SharedPreferenceService.getBoolean(NEW_DEVICE_ID)

    private fun saveToken(token: String) {
        sharedPreferences.edit().putString(BMBConstants.STORED_DETAILS, token).apply()
    }

    fun removeEncryptedToken() {
        if (sharedPreferences.contains(BMBConstants.STORED_DETAILS)) {
            sharedPreferences.edit().remove(BMBConstants.STORED_DETAILS).apply()
        }
    }

    fun removeOldKey() {
        if (sharedPreferences.contains(BMBConstants.STORED_KEY)) {
            sharedPreferences.edit().remove(BMBConstants.STORED_KEY).apply()
        }
    }

    private fun getDeviceLinkingEncryptedToken(): String {
        return sharedPreferences.getString(BMBConstants.STORED_DETAILS, "") ?: ""
    }

    fun hasDeviceLinkingEncryptedToken(): Boolean {
        val deviceLinkingEncryptedToken = getDeviceLinkingEncryptedToken()
        return deviceLinkingEncryptedToken.isNotEmpty()
    }

    private fun generateFreshToken(): String {
        return SHA256Encrytion.getHash(deviceUuid)
    }

    private fun encryptAndSaveToken(key: String, unencryptedToken: String) {
        require(key.isNotEmpty()) { "Key cannot be null" }
        require(unencryptedToken.isNotEmpty()) { "Token cannot be null" }

        try {
            val encryptedToken = AESEncryption.encrypt(key, unencryptedToken)
            saveToken(encryptedToken)
        } catch (e: Exception) {
            if (BuildConfig.DEBUG) {
                Log.e(TAG, "Failed to encrypt and persist the token", e)
            }
        }
    }

    private fun decryptTokenWithOldKey(oldKey: String, storedToken: String): String {
        return if (storedToken.isEmpty()) {
            ""
        } else try {
            AESEncryption.decrypt(oldKey, storedToken, false)
        } catch (e: Exception) {
            if (BuildConfig.DEBUG) {
                Log.e(TAG, "Failed to decrypt the stored token", e)
            }
            ""
        }
    }

    fun readDeviceIdentifier(): String {
        val imei = CommonUtils.getDeviceSpecificData(BMBConstants.IMEI_KEY)
        if (imei.isNotEmpty()) {
            DeviceUtils.setDeviceUuid(imei)
            return imei
        }

        val serialNumber = CommonUtils.getDeviceSpecificData(BMBConstants.SERIAL_NUMBER_KEY)
        if (serialNumber.isNotEmpty()) {
            DeviceUtils.setDeviceUuid(serialNumber)
            return serialNumber
        }

        val udid = CommonUtils.getDeviceSpecificData(BMBConstants.UDID_KEY)
        if (udid.isNotEmpty()) {
            DeviceUtils.setDeviceUuid(udid)
            return udid
        }

        val macID = CommonUtils.getDeviceSpecificData(BMBConstants.MAC_ID_KEY)
        return if (macID.isNotEmpty()) {
            DeviceUtils.setDeviceUuid(macID)
            macID
        } else ""
    }
}