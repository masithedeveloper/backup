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
package com.barclays.absa.banking.express.biometricAuthentication

import androidx.lifecycle.LifecycleOwner
import com.barclays.absa.banking.framework.dagger.getServiceInterface
import com.barclays.absa.banking.framework.services.contracts.IAppCacheService
import com.barclays.absa.utils.UserSettingsManager

object BiometricAuthenticationToggler {

    private val appCacheService: IAppCacheService = getServiceInterface()

    @JvmStatic
    fun enableBiometrics(lifecycleOwner: LifecycleOwner, randomAliasId: ByteArray, successBlock: (() -> Unit)?, failureBlock: (() -> Unit)?) {
        val biometricViewModel = EnableBiometricAuthenticationViewModel()
        biometricViewModel.enableBiometricAuthentication(appCacheService.getPasscode() ?: "", randomAliasId)
        biometricViewModel.baseResponseLiveData.observe(lifecycleOwner, {
            successBlock?.let { successBlock() }
        })
        biometricViewModel.failureLiveData.observe(lifecycleOwner, {
            failureBlock?.let { failureBlock() }
        })
    }

    @JvmStatic
    fun disableBiometrics(lifecycleOwner: LifecycleOwner, thenDo: (() -> Unit)?) {
        val biometricViewModel = DisableBiometricAuthenticationViewModel()
        biometricViewModel.disableBiometricAuthentication()

        biometricViewModel.failureLiveData.observe(lifecycleOwner, {
            thenDo?.let { thenDoBlock -> thenDoBlock() }
        })

        biometricViewModel.baseResponseLiveData.observe(lifecycleOwner, {
            UserSettingsManager.setFingerprintActive(false)
            thenDo?.let { thenDoBlock -> thenDoBlock() }
        })
    }
}