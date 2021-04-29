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
import za.co.absa.networking.enums.AliasTypeEnum
import za.co.absa.networking.hmac.service.BaseRequest
import za.co.absa.networking.hmac.utils.HmacUtils

class DisableBiometricAuthenticationRepository : Repository() {

    override var apiService = createXMMSService()

    override val service = "AuthenticationFacade"
    override val operation = "DisableBiometricAuthentication"

    override var mockResponseFile: String = "express/base_response_success.json"

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

        activeUserProfile.apply {
            randomAliasId = null
            fingerprintId = null
            ProfileManager.getInstance().updateProfile(this, null)
        }

        val symmetricKeyInitializationVectorBytes = initializationVector.iv
        val base64EncodedInitializationVector = Base64.encodeToString(symmetricKeyInitializationVectorBytes, Base64.NO_WRAP)

        baseRequest.apply {
            addParameter("aliasTypeEnum", AliasTypeEnum.MOBILEAPP_SECONDFACTOR_ENABLED.name)
            addParameter("aliasId", base64EncodedEncryptedAliasId)

            addParameter("symmetricKey", base64EncodedEncryptedSymmetricKey)
            addParameter("symmetricKeyIV", base64EncodedInitializationVector)
            addParameter("deviceId", ExpressNetworkingConfig.deviceId)
            addParameter("publicKeyId", ExpressNetworkingConfig.publicKeyId)
        }

        return baseRequest.build()
    }
}