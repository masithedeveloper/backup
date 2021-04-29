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
package com.barclays.absa.banking.login.services.dto

import com.barclays.absa.banking.BuildConfig
import com.barclays.absa.banking.boundary.model.SecureHomePageObject
import com.barclays.absa.banking.framework.ExtendedRequest
import com.barclays.absa.banking.framework.ExtendedResponseListener
import com.barclays.absa.banking.framework.MockFactory
import com.barclays.absa.banking.framework.api.request.params.OpCodeParams.OP1000_LOGIN_SECURE_HOME_PAGE
import com.barclays.absa.banking.framework.api.request.params.RequestParams
import com.barclays.absa.banking.framework.api.request.params.TransactionParams.Transaction
import com.barclays.absa.banking.framework.app.BMBApplication
import com.barclays.absa.banking.framework.app.BMBConstants
import com.barclays.absa.banking.framework.dagger.getServiceInterface
import com.barclays.absa.banking.framework.featureSwitching.FeatureSwitchingCache
import com.barclays.absa.banking.framework.featureSwitching.FeatureSwitchingStates
import com.barclays.absa.banking.framework.services.contracts.IAppCacheService
import com.barclays.absa.banking.login.services.LoginService
import com.barclays.absa.crypto.SecureUtils
import com.barclays.absa.utils.DeviceUtils

class AccessAccountLoginRequest<T>(accountNumber: String, credential: String, userNumber: String,
                                   responseListener: ExtendedResponseListener<T>) : ExtendedRequest<T>(responseListener) {

    private val appCacheService: IAppCacheService = getServiceInterface()

    init {
        val deviceID = SecureUtils.getDeviceID()
        val requestParams = RequestParams.Builder()
                .put(OP1000_LOGIN_SECURE_HOME_PAGE)
                .put(Transaction.SERVICE_USER_NO, if (userNumber.isEmpty())
                    BMBConstants.DEFAULT_USER_NUMBER else userNumber)
                .put(Transaction.SERVICE_ACCESS_ACCOUNT, accountNumber)
                .put(Transaction.SERVICE_CHANNEL_IND, DeviceUtils.channelId)
                .put(Transaction.MANUFACTURER, android.os.Build.MANUFACTURER)
                .put(Transaction.MODEL, android.os.Build.MODEL)
                .put(Transaction.IMEI, deviceID)
                .put(Transaction.DEVICE_ID, deviceID)
                .put(Transaction.NICKNAME, "android")
                .put(Transaction.SERVICE_DEVICE_INTEGRITY, BMBApplication.getDeviceIntegrityFlag())
                .put(Transaction.SERVICE_FAILED_LOGIN_ATTEMPTS, BMBApplication.TOTAL_FAILED_LOGIN_ATTEMPTS.toString())
                .put(Transaction.IS_LIVE_PROVING_BUILD, BuildConfig.PRD.toString())
                .put(Transaction.APP_VERSION, LoginService.APP_VERSION)
                .put(Transaction.AUTH_TYPE, BMBConstants.AUTH_TYPE_PINBLOCK)
                .put(Transaction.PIN, credential)
                .put(Transaction.TRUST_TOKEN, appCacheService.getTrustToken())
                .put(Transaction.SIMPLIFIED_LOGIN, BMBConstants.SIMPLIFIED_LOGIN_YES)
                .put("customerSessionId", appCacheService.getCustomerSessionId())

        if (BuildConfig.TOGGLE_DEF_BIOMETRIC_VERIFICATION_ENABLED && FeatureSwitchingCache.featureSwitchingToggles.biometricVerification == FeatureSwitchingStates.ACTIVE.key && !appCacheService.shouldRevertToOldLinkingFlow()) {
            requestParams.put("transactionType", "42")
        }

        params = requestParams.build()
        mockResponseFile = MockFactory.login()
        printRequest()
    }

    @Suppress("UNCHECKED_CAST")
    override fun getResponseClass(): Class<T> = SecureHomePageObject::class.java as Class<T>
    override fun isEncrypted(): Boolean? = false
}