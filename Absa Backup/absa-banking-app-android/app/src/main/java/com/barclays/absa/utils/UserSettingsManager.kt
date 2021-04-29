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
package com.barclays.absa.utils

object UserSettingsManager : IUserSettingsManager {

    // The sharedPreferenceService will eventually be injected within the manager
    private val sharedPreferenceService : ISharedPreferenceService = SharedPreferenceService

    override fun shouldRegisterPushNotificationIdWithBackend(): Boolean = sharedPreferenceService.shouldRegisterPushNotificationIdWithBackend()
    override fun setShouldRegisterPushNotificationIdWithBackend(shouldRegisterPushNotificationIdWithBackend: Boolean) = sharedPreferenceService.setShouldRegisterPushNotificationIdWithBackend(shouldRegisterPushNotificationIdWithBackend)

    override fun isNotificationServicesEnabled(): Boolean = sharedPreferenceService.isNotificationServicesEnabled()
    override fun setNotificationServicesEnabled(isNotificationEnabled: Boolean) = sharedPreferenceService.setNotificationServicesEnabled(isNotificationEnabled)

    override fun isFingerprintActive(): Boolean = sharedPreferenceService.isFingerprintActive()
    override fun setFingerprintActive(fingerprintActive: Boolean) = sharedPreferenceService.setFingerprintActive(fingerprintActive)

    override fun isSoundEnabled(): Boolean = sharedPreferenceService.isSoundEnabled()
    override fun setSoundEnabled(allow: Boolean) = sharedPreferenceService.setSoundEnabled(allow)

    override fun isScreenshotsAllowed(): Boolean = sharedPreferenceService.isScreenshotsAllowed()
    override fun setScreenshotsAllowed(isScreenshotsAllowed: Boolean) = sharedPreferenceService.setScreenshotsAllowed(isScreenshotsAllowed)

    override fun getDarkModeSetting(): Int = sharedPreferenceService.getDarkModeSetting()
    override fun setDarkModeSetting(darkModeSelected: Int) = sharedPreferenceService.setDarkModeSetting(darkModeSelected)
}