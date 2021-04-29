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
 *
 */
package com.barclays.absa.banking.express.authentication.login

import android.util.Base64
import com.barclays.absa.banking.R
import com.barclays.absa.banking.boundary.monitoring.MonitoringInteractor
import com.barclays.absa.banking.express.Repository
import com.barclays.absa.banking.framework.app.BMBApplication
import com.barclays.absa.banking.passcode.passcodeLogin.multipleUserLogin.SimplifiedLoginActivity
import com.barclays.absa.crypto.SymmetricCryptoHelper
import com.barclays.absa.utils.ProfileManager
import za.co.absa.networking.ExpressNetworkingConfig
import za.co.absa.networking.dto.ResponseHeader
import za.co.absa.networking.dto.ResultMessage
import za.co.absa.networking.enums.AliasTypeEnum
import za.co.absa.networking.enums.CredentialTypeEnum
import za.co.absa.networking.hmac.service.BaseRequest
import za.co.absa.networking.hmac.service.Request
import za.co.absa.networking.hmac.utils.HmacUtils

class AuthenticationRepository : Repository() {

    private var secret: String = ""
    private var isBiometric: Boolean = false

    override val apiService = createXMMSService()

    override val service = "AuthenticationFacade"
    override val operation = "AuthenticateCredential"

    override var mockResponseFile = "express/authenticate.json"

    private lateinit var symmetricCryptoHelper: SymmetricCryptoHelper
    private var zeroKeyEncryptedAliasId: ByteArray? = null

    suspend fun sendAuthenticateRequest(credential: String, isBiometric: Boolean): AuthenticationResponse? {
        this.secret = credential
        this.isBiometric = isBiometric

        symmetricCryptoHelper = SymmetricCryptoHelper.getInstance()

        zeroKeyEncryptedAliasId = if (symmetricCryptoHelper.hasAliasRegistered()) {
            MonitoringInteractor().logTechnicalEvent(SimplifiedLoginActivity::class.java.simpleName, this.javaClass.simpleName, "Legacy User")
            try {
                val existingUserAliasKey = "alias_key"
                symmetricCryptoHelper.retrieveAlias(existingUserAliasKey)
            } catch (e: SymmetricCryptoHelper.KeyStoreEntryAccessException) {
                symmetricCryptoHelper.retrieveAlias(ProfileManager.getInstance().activeUserProfile.userId)
            }
        } else {
            symmetricCryptoHelper.retrieveAlias(ProfileManager.getInstance().activeUserProfile.userId)
        }

        if (zeroKeyEncryptedAliasId == null) {
            MonitoringInteractor().logTechnicalEvent(SimplifiedLoginActivity::class.java.simpleName, this.javaClass.simpleName, "Encrypted Alias Null")
            failureHeaderLiveData.postValue(ResponseHeader().apply { resultMessages.add(ResultMessage("-1", BMBApplication.getInstance().getString(R.string.profile_lost))) })
            return null
        }

        return submitRequest()
    }

    override fun buildRequest(baseRequest: BaseRequest.Builder): BaseRequest {

        val requestBuilder = Request.Builder(baseRequest)
        val base64EncodedEncryptedSymmetricKey = HmacUtils.generateAndEncodeAesSymmetricKey(ExpressNetworkingConfig.publicKey)

        val decryptedAlias = symmetricCryptoHelper.decryptAliasWithZeroKey(zeroKeyEncryptedAliasId)
        val aliasId = String(decryptedAlias)

        val symmetricKey = HmacUtils.aesSymmetricKey
        val initializationVector = HmacUtils.generateRandomIV()

        val encryptedAliasId = symmetricCryptoHelper.encryptAlias(aliasId, symmetricKey, initializationVector)
        val base64EncodedEncryptedAliasId = Base64.encodeToString(encryptedAliasId, Base64.NO_WRAP)

        val credential = symmetricCryptoHelper.deriveCredential(aliasId, secret, ExpressNetworkingConfig.deviceId)
        val encryptedCredential = symmetricCryptoHelper.encryptCredential(credential, initializationVector, symmetricKey)
        val base64EncodedEncryptedCredential = Base64.encodeToString(encryptedCredential, Base64.NO_WRAP)

        val symmetricKeyInitializationVectorBytes = initializationVector.iv
        val base64EncodedInitializationVector = Base64.encodeToString(symmetricKeyInitializationVectorBytes, Base64.NO_WRAP)

        requestBuilder.apply {
            addParameter("aliasId", base64EncodedEncryptedAliasId)
            addParameter("aliasTypeEnum", AliasTypeEnum.MOBILEAPP_SECONDFACTOR_ENABLED.name)
            addParameter("credential", base64EncodedEncryptedCredential)
            addParameter("deviceId", ExpressNetworkingConfig.deviceId)
            addParameter("publicKeyId", ExpressNetworkingConfig.publicKeyId)
            addParameter("symmetricKey", base64EncodedEncryptedSymmetricKey)
            addParameter("symmetricKeyIV", base64EncodedInitializationVector)

            val credentialType = if (isBiometric) CredentialTypeEnum.MOBILEAPP_TOUCHID else CredentialTypeEnum.MOBILEAPP_5DIGIT_PIN
            addParameter("credentialTypeEnum", credentialType.name)
        }

        return requestBuilder.build()
    }
}