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

import com.barclays.absa.banking.boundary.model.UserProfile

interface ISharedPreferenceService {

    fun isFingerprintActive(): Boolean
    fun setFingerprintActive(fingerprintActive: Boolean)

    fun getProfileMigrationVersion(): Int
    fun setProfileMigrationVersion(migrationVersion: Int)

    fun getFirstLoginStatus(): Boolean
    fun setFirstLoginStatus(isFirstLoginForVersion: Boolean)

    fun getLastLoginVersion(): Int
    fun setLastLoginVersion()

    fun getDarkModeSetting(): Int
    fun setDarkModeSetting(darkModeSelected: Int)

    fun setLastNagDate(date: Long)
    fun getLastNagDate(): Long

    fun incrementUnsupportedSDKCount()
    fun getUnsupportedSDKCount(): Int

    fun getUserShortcutList(userId: String? = null): List<Shortcut>
    fun setUserShortcutList(userShortcutList: List<Shortcut>)

    fun getCurrentAppLanguage(): String?

    fun isScreenshotsAllowed(): Boolean
    fun setScreenshotsAllowed(isScreenshotsAllowed: Boolean)

    fun getAppLastRatedOn(): Long
    fun setAppLastRatedOn(ratedOnDateMillis: Long)

    fun isSoundEnabled(): Boolean
    fun setSoundEnabled(allow: Boolean)

    fun isNotificationServicesEnabled(): Boolean
    fun setNotificationServicesEnabled(isNotificationEnabled: Boolean)

    fun shouldRegisterPushNotificationIdWithBackend(): Boolean
    fun setShouldRegisterPushNotificationIdWithBackend(shouldRegisterPushNotificationIdWithBackend: Boolean)

    fun hasDeviceUnregisteredFromIMI(): Boolean
    fun unregisterDeviceFromIMI()

    fun isPartialRegistration(): Boolean
    fun setIsPartialRegistration(isPartialRegistration: Boolean)

    fun isReloadProfilePic(): Boolean
    fun setReloadProfilePic(reloadProfilePic: Boolean)

    fun getDebiCheckFirstVisitDone(): Boolean
    fun setDebiCheckFirstVisitDone(hasVisited: Boolean)

    fun isOfferHidden(accountNumber: String): Boolean
    fun setOfferHidden(accountNumber: String)

    fun getOfflineOtpLongNumber(userId: String): Long
    fun setOfflineOtpLongNumber(userId: String, longNumber1: Long)

    fun getKeyToolsPassword(): String?
    fun setKeyToolsPassword(keyToolsPassword: String)

    fun getDontKeepActivitiesCount(): Int
    fun setDontKeepActivitiesCount(dontKeepActivitiesCount: Int)

    fun getFuneralCoverDisplayCount(): Int
    fun setCanUpdateFuneralCoverDisplayCount(canUpdate: Boolean)
    fun setCanAnimateFuneralTile(canUpdate: Boolean)

    @Throws(IllegalArgumentException::class)
    fun getKeyToolsIv(): ByteArray?
    fun setKeyToolsIv(iv: ByteArray)

    fun enableLaunchProfileSetUp()

    fun getBannerCount(bannerName: String): Int
    fun setBannerCount(bannerName: String, bannerCount: Int)

    fun getKeystorePassword(): String?
    fun setKeystorePassword(keystorePass: CharArray)

    fun resetRetries(userId: String?)
    fun countRetries(userId: String?): Int

    fun getBoolean(key: String): Boolean
    fun setBoolean(key: String, value: Boolean)

    fun getInteger(key: String, defaultValue: Int): Int
    fun setInteger(key: String, value: Int)

    fun getString(key: String): String?
    fun setString(key: String, value: String?)

    fun getCurrentUserProfileSavedInstance(): UserProfile?
    fun setCurrentUserProfileSavedInstance(userProfile: UserProfile)

    fun removeInternationalPaymentsHowItWorksFirstVisitDone(userId: String)
}