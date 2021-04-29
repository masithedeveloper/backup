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

package com.barclays.absa.banking.express.biometricAuthentication

import android.util.Base64
import com.barclays.absa.banking.express.Repository
import com.barclays.absa.crypto.SymmetricCryptoHelper
import com.barclays.absa.utils.ProfileManager
import za.co.absa.networking.ExpressNetworkingConfig
import za.co.absa.networking.dto.BaseResponse
import za.co.absa.networking.enums.AliasTypeEnum
import za.co.absa.networking.hmac.service.BaseRequest
import za.co.absa.networking.hmac.utils.HmacUtils

class EnableBiometricAuthenticationRepository : Repository() {

    private var passcode: String = ""
    private lateinit var randomAliasId: ByteArray

    override var apiService = createXMMSService()

    override val service = "AuthenticationFacade"
    override val operation = "EnableBiometricAuthentication"

    override var showProgressDialog: Boolean = true

    override var mockResponseFile: String = "express/base_response_success.json"

    suspend fun enableBiometricRequest(passcode: String, userRandomAliasId: ByteArray): BaseResponse? {
        this.passcode = passcode
        this.randomAliasId = userRandomAliasId
        return submitRequest()
    }

    override fun buildRequest(baseRequest: BaseRequest.Builder): BaseRequest {
        val base64EncodedEncryptedSymmetricKey = HmacUtils.generateAndEncodeAesSymmetricKey(ExpressNetworkingConfig.publicKey)
        val symmetricCryptoHelper = SymmetricCryptoHelper.getInstance()
        val activeUserProfile = ProfileManager.getInstance().activeUserProfile
        val zeroKeyEncryptedAliasId = symmetricCryptoHelper.retrieveAlias(activeUserProfile.userId)
        val decryptedAlias = symmetricCryptoHelper.decryptAliasWithZeroKey(zeroKeyEncryptedAliasId)
        val aliasId = String(decryptedAlias)
        val symmetricKey = HmacUtils.aesSymmetricKey
        val initializationVector = HmacUtils.generateRandomIV()
        val encryptedAliasId = symmetricCryptoHelper.encryptAlias(aliasId, symmetricKey, initializationVector)

        val base64EncodedEncryptedAliasId = Base64.encodeToString(encryptedAliasId, Base64.NO_WRAP)

        val fingerprintRandomId = String(randomAliasId)
        val passcodeCredential = symmetricCryptoHelper.deriveCredential(aliasId, passcode, ExpressNetworkingConfig.deviceId)
        val biometricCredential = symmetricCryptoHelper.deriveCredential(aliasId, fingerprintRandomId, ExpressNetworkingConfig.deviceId)

        val encryptedBiometricCredential = symmetricCryptoHelper.encryptCredential(biometricCredential, initializationVector, symmetricKey)
        val encryptedPasscodeCredential = symmetricCryptoHelper.encryptCredential(passcodeCredential, initializationVector, symmetricKey)
        val base64EncodedEncryptedBiometricCredential = Base64.encodeToString(encryptedBiometricCredential, Base64.NO_WRAP)
        val base64EncodedEncryptedPasscodeCredential = Base64.encodeToString(encryptedPasscodeCredential, Base64.NO_WRAP)

        val symmetricKeyInitializationVectorBytes = initializationVector.iv
        val base64EncodedInitializationVector = Base64.encodeToString(symmetricKeyInitializationVectorBytes, Base64.NO_WRAP)

        baseRequest.apply {
            addParameter("aliasTypeEnum", AliasTypeEnum.MOBILEAPP_SECONDFACTOR_ENABLED.name)
            addParameter("aliasId", base64EncodedEncryptedAliasId)

            addParameter("passcodeCredential", base64EncodedEncryptedPasscodeCredential)
            addParameter("biometricCredential", base64EncodedEncryptedBiometricCredential)

            addParameter("symmetricKey", base64EncodedEncryptedSymmetricKey)
            addParameter("symmetricKeyIV", base64EncodedInitializationVector)
            addParameter("deviceId", ExpressNetworkingConfig.deviceId)
            addParameter("publicKeyId", ExpressNetworkingConfig.publicKeyId)
        }

        return baseRequest.build()
    }
}