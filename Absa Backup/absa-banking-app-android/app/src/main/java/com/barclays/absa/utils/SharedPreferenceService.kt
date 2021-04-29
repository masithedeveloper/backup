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

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager
import android.util.ArraySet
import android.util.Base64
import androidx.appcompat.app.AppCompatDelegate
import com.barclays.absa.banking.BuildConfig
import com.barclays.absa.banking.boundary.model.UserProfile
import com.barclays.absa.banking.framework.app.BMBApplication
import com.barclays.absa.banking.framework.utils.BMBLogger
import com.barclays.absa.crypto.SymmetricCryptoHelper
import com.barclays.absa.crypto.SymmetricCryptoHelper.DecryptionFailureException
import com.barclays.absa.crypto.SymmetricCryptoHelper.EncryptionFailureException
import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.google.gson.Gson

object SharedPreferenceService : ISharedPreferenceService {

    // Logic that doesn't belong in this service and that will be moved in future PR
    private const val MAX_RETRIES_ATTEMPTS = 3

    // Shared preference files
    private const val GLOBAL_PREFS = "GLOBAL_PREFS"
    private const val LAST_LOGIN_VERSION = "LAST_LOGIN_VERSION"
    private const val PROJECT_NAME = "PROJECT_NAME"
    private const val ACCOUNTS_VIEW_OPTION = "accountsViewOption"
    private const val CURRENT_USER_PROFILE = "CURRENT-USER-PROFILE"
    private const val CURRENT_PROFILE_STORE = "CURRENT-PROFILE-STORE"

    // Shared Preference Keys
    private const val FIRST_LOGIN_STATUS = "FIRST_LOGIN_STATUS"
    private const val FIRST_VERSION_LOGIN = "FIRST_VERSION_LOGIN"
    private const val LAST_NAG_DATE = "LAST_NAG_DATE"
    private const val UNSUPPORTED_SDK_COUNT = "UNSUPPORTED_SDK_COUNT"
    private const val DARK_MODE_SETTING = "darkModeSetting"
    private const val VERSION_NUMBER = "VERSION_NUMBER"
    private const val MIGRATION_VERSION = "MIGRATION_VERSION"
    private const val FINGERPRINT_STATUS = "FINGERPRINT_STATUS"
    private const val USER_SHORTCUT_LIST = "userShortcutList"
    private const val IS_SCREENSHOTS_ALLOWED = "isScreenshotsAllowed"
    private const val APP_LAST_RATED_DATE_MILLIS = "appLastRatedDateMillis"
    private const val ALLOW_APP_SOUNDS = "ALLOW_APP_SOUNDS"
    private const val IS_NOTIFICATIONS_ENABLED = "IS_NOTIFICATIONS_ENABLED"
    private const val SHOULD_REGISTER_PUSH_WITH_BACKEND = "SHOULD_REGISTER_PUSH_WITH_BACKEND"
    private const val IN_APP_MESSAGE_HAS_DEREGISTER = "imiHasDeregister"
    private const val IS_PARTIAL_REGISTRATION = "IsPartialRegistration"
    private const val RELOAD_PROFILE_PIC = "ReloadProfilePic"
    private const val APP_HAS_VISITED_DEBICHECK = "appHasSeenDebiCheck"
    private const val ACCOUNT_VIEW_HORIZONTAL_PREFERENCE = "horizontal"
    private const val HIDDEN_OFFERS = "_hidden_offer"
    private const val DONT_KEEP_ACTIVITIES = "dontKeepActivities"
    private const val IS_LAUNCH_PROFILE_SETUP_ENABLED = "isLaunchProfileSetUpEnabled"
    private const val KEYTOOLS_PASSWORD = "userKeyToolsPassword"
    private const val CAN_UPDATE_FUNERAL_COVER_DISPLAY_COUNT = "canUpdateFuneralCoverDisplayCount"
    private const val CAN_ANIMATE_FUNERAL_TILE = "canAnimateFuneralTile"
    private const val FUNERAL_COVER_DISPLAY_COUNT = "FUNERAL_COVER_DISPLAY_COUNT_01"
    private const val KEY_TOOLS_IV = "keyToolsIv"
    private const val USER_KEYSTORE_PASSWORD = "userKeyStorePassword"
    private const val RETRIES = "RETRIES"
    private const val OFFLINE_OTP_LONG_NUMBER = "OfflineOtpLongNumber"
    private const val SHARED_PREF_KEY_LOCALE = "locale_override"
    private const val INTERNATIONAL_PAYMENT_FIRST_VISIT_DONE = "_international_payment_first_time_visit"

    private const val BOOLEAN_SAVED_INSTANCE = "Boolean-Save-Instance"
    private const val MANAGE_SAVED_INSTANCE = "Manage-SavedInstance"
    private const val SAVED_INSTANCE = "SavedInstance"

    override fun isFingerprintActive(): Boolean = getPrivateSharedPreferences(PROJECT_NAME).getBoolean(FINGERPRINT_STATUS, false)
    override fun setFingerprintActive(fingerprintActive: Boolean) {
        getPrivateSharedPreferences(PROJECT_NAME).edit().putBoolean(FINGERPRINT_STATUS, fingerprintActive).commit()
    }

    override fun getProfileMigrationVersion(): Int = getPrivateSharedPreferences(GLOBAL_PREFS).getInt(MIGRATION_VERSION, 1)
    override fun setProfileMigrationVersion(migrationVersion: Int) = getPrivateSharedPreferences(GLOBAL_PREFS).edit().putInt(MIGRATION_VERSION, migrationVersion).apply()

    override fun getFirstLoginStatus(): Boolean {
        val userProfileKey = getActiveUserId() + FIRST_LOGIN_STATUS
        return getPrivateSharedPreferences(userProfileKey).getBoolean(FIRST_VERSION_LOGIN, true)
    }

    override fun setFirstLoginStatus(isFirstLoginForVersion: Boolean) {
        val userProfileKey = getActiveUserId() + FIRST_LOGIN_STATUS
        getPrivateSharedPreferences(userProfileKey).edit().putBoolean(FIRST_VERSION_LOGIN, isFirstLoginForVersion).apply()
    }

    override fun getLastLoginVersion(): Int = getPrivateSharedPreferences(LAST_LOGIN_VERSION).getInt(VERSION_NUMBER, 0)
    override fun setLastLoginVersion() = getPrivateSharedPreferences(LAST_LOGIN_VERSION).edit().putInt(VERSION_NUMBER, BuildConfig.VERSION_CODE).apply()

    override fun getDarkModeSetting(): Int = getPrivateSharedPreferences(GLOBAL_PREFS).getInt(DARK_MODE_SETTING, AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
    override fun setDarkModeSetting(darkModeSelected: Int) = getPrivateSharedPreferences(GLOBAL_PREFS).edit().putInt(DARK_MODE_SETTING, darkModeSelected).apply()

    private fun getActiveUserId(): String = ProfileManager.getInstance().activeUserProfile?.userId ?: ""

    override fun setLastNagDate(date: Long) = getPrivateSharedPreferences(GLOBAL_PREFS).edit().putLong(LAST_NAG_DATE, date).apply()
    override fun getLastNagDate(): Long = getPrivateSharedPreferences(GLOBAL_PREFS).getLong(LAST_NAG_DATE, -1)

    override fun incrementUnsupportedSDKCount() = getPrivateSharedPreferences(GLOBAL_PREFS).edit().putInt(UNSUPPORTED_SDK_COUNT, getUnsupportedSDKCount() + 1).apply()
    override fun getUnsupportedSDKCount(): Int = getPrivateSharedPreferences(GLOBAL_PREFS).getInt(UNSUPPORTED_SDK_COUNT, 0)

    override fun getUserShortcutList(userId: String?): List<Shortcut> {
        val selectedUserId = userId ?: getActiveUserId()
        val userShortcutList = getPrivateSharedPreferences(GLOBAL_PREFS).getStringSet(USER_SHORTCUT_LIST + selectedUserId, null) ?: return emptyList()
        return userShortcutList.toList().map { s -> Gson().fromJson(s, Shortcut::class.java) }
    }

    override fun setUserShortcutList(userShortcutList: List<Shortcut>) {
        val shortcutStringList = userShortcutList.map { Gson().toJson(it) }
        val shortcutSet = ArraySet<String>().apply { addAll(shortcutStringList) }
        getPrivateSharedPreferences(GLOBAL_PREFS).edit().putStringSet(USER_SHORTCUT_LIST + getActiveUserId(), shortcutSet).apply()
    }

    override fun getCurrentAppLanguage(): String? = PreferenceManager.getDefaultSharedPreferences(BMBApplication.getInstance()).getString(SHARED_PREF_KEY_LOCALE, "en")

    override fun isScreenshotsAllowed(): Boolean {
        val activeUserId = getActiveUserId()
        return activeUserId.isNotEmpty() && getPrivateSharedPreferences(GLOBAL_PREFS).getBoolean(IS_SCREENSHOTS_ALLOWED + activeUserId, false)
    }

    override fun setScreenshotsAllowed(isScreenshotsAllowed: Boolean) {
        val activeUserId = getActiveUserId()
        if (activeUserId.isNotEmpty()) {
            getPrivateSharedPreferences(GLOBAL_PREFS).edit().putBoolean(IS_SCREENSHOTS_ALLOWED + activeUserId, isScreenshotsAllowed).apply()
        }
    }

    override fun getAppLastRatedOn(): Long {
        val activeUserId = getActiveUserId()
        return if (activeUserId.isNotEmpty()) getPrivateSharedPreferences(GLOBAL_PREFS).getLong(APP_LAST_RATED_DATE_MILLIS + activeUserId, -1) else -1
    }

    override fun setAppLastRatedOn(ratedOnDateMillis: Long) {
        val activeUserId = getActiveUserId()
        if (activeUserId.isNotEmpty()) {
            getPrivateSharedPreferences(GLOBAL_PREFS).edit().putLong(APP_LAST_RATED_DATE_MILLIS + activeUserId, ratedOnDateMillis).apply()
        }
    }

    override fun isSoundEnabled(): Boolean {
        val userProfileKey = getActiveUserId() + ALLOW_APP_SOUNDS
        return getPrivateSharedPreferences(userProfileKey).getBoolean(userProfileKey, true)
    }

    override fun setSoundEnabled(allow: Boolean) {
        val userProfileKey = getActiveUserId() + ALLOW_APP_SOUNDS
        getPrivateSharedPreferences(userProfileKey).edit().putBoolean(userProfileKey, allow).apply()
    }

    override fun isNotificationServicesEnabled(): Boolean = getPrivateSharedPreferences(IS_NOTIFICATIONS_ENABLED).getBoolean(IS_NOTIFICATIONS_ENABLED, true)
    override fun setNotificationServicesEnabled(isNotificationEnabled: Boolean) = getPrivateSharedPreferences(IS_NOTIFICATIONS_ENABLED).edit().putBoolean(IS_NOTIFICATIONS_ENABLED, isNotificationEnabled).apply()

    override fun shouldRegisterPushNotificationIdWithBackend(): Boolean = getPrivateSharedPreferences(SHOULD_REGISTER_PUSH_WITH_BACKEND).getBoolean(SHOULD_REGISTER_PUSH_WITH_BACKEND, true)
    override fun setShouldRegisterPushNotificationIdWithBackend(shouldRegisterPushNotificationIdWithBackend: Boolean) = getPrivateSharedPreferences(SHOULD_REGISTER_PUSH_WITH_BACKEND).edit().putBoolean(SHOULD_REGISTER_PUSH_WITH_BACKEND, shouldRegisterPushNotificationIdWithBackend).apply()

    override fun hasDeviceUnregisteredFromIMI(): Boolean = getBoolean(IN_APP_MESSAGE_HAS_DEREGISTER)
    override fun unregisterDeviceFromIMI() = setBoolean(IN_APP_MESSAGE_HAS_DEREGISTER, true)

    override fun isPartialRegistration(): Boolean = getSharedPreferences().getBoolean(IS_PARTIAL_REGISTRATION, false)
    override fun setIsPartialRegistration(isPartialRegistration: Boolean) {
        getSharedPreferences().edit().putBoolean(IS_PARTIAL_REGISTRATION, isPartialRegistration).commit()
    }

    override fun isReloadProfilePic(): Boolean = getBoolean(RELOAD_PROFILE_PIC)
    override fun setReloadProfilePic(reloadProfilePic: Boolean) = setBoolean(RELOAD_PROFILE_PIC, reloadProfilePic)

    override fun getDebiCheckFirstVisitDone(): Boolean = getBoolean(getActiveUserId() + APP_HAS_VISITED_DEBICHECK)
    override fun setDebiCheckFirstVisitDone(hasVisited: Boolean) = setBoolean(getActiveUserId() + APP_HAS_VISITED_DEBICHECK, hasVisited)

    override fun isOfferHidden(accountNumber: String): Boolean = getBoolean(accountNumber + HIDDEN_OFFERS)
    override fun setOfferHidden(accountNumber: String) = setBoolean(accountNumber + HIDDEN_OFFERS, true)

    override fun getOfflineOtpLongNumber(userId: String): Long = getSharedPreferences().getLong("$userId-$OFFLINE_OTP_LONG_NUMBER", -1)
    override fun setOfflineOtpLongNumber(userId: String, longNumber1: Long) {
        getSharedPreferences().edit().putLong("$userId-$OFFLINE_OTP_LONG_NUMBER", longNumber1).commit()
    }

    override fun getKeyToolsPassword(): String? {
        val activeUserId = getActiveUserId()
        return if (activeUserId.isNotEmpty()) getPrivateSharedPreferences(GLOBAL_PREFS).getString(KEYTOOLS_PASSWORD + activeUserId, null) else null
    }

    override fun setKeyToolsPassword(keyToolsPassword: String) {
        val activeUserId = getActiveUserId()
        if (activeUserId.isNotEmpty()) {
            getPrivateSharedPreferences(GLOBAL_PREFS).edit().putString(KEYTOOLS_PASSWORD + activeUserId, keyToolsPassword).apply()
        }
    }

    override fun getDontKeepActivitiesCount(): Int = getInteger(DONT_KEEP_ACTIVITIES, 0)
    override fun setDontKeepActivitiesCount(dontKeepActivitiesCount: Int) = setInteger(DONT_KEEP_ACTIVITIES, dontKeepActivitiesCount)

    override fun getFuneralCoverDisplayCount(): Int {
        val activeUserId = getActiveUserId()
        return if (activeUserId.isNotEmpty()) getPrivateSharedPreferences(GLOBAL_PREFS).getInt(FUNERAL_COVER_DISPLAY_COUNT + activeUserId, 0) else 0
    }

    override fun setCanUpdateFuneralCoverDisplayCount(canUpdate: Boolean) {
        val activeUserId = getActiveUserId()
        if (activeUserId.isNotEmpty()) {
            setBoolean(CAN_UPDATE_FUNERAL_COVER_DISPLAY_COUNT + activeUserId, canUpdate)
        }
    }

    override fun setCanAnimateFuneralTile(canUpdate: Boolean) = setBoolean(CAN_ANIMATE_FUNERAL_TILE, canUpdate)

    override fun getKeyToolsIv(): ByteArray? {
        val ivString = getPrivateSharedPreferences(GLOBAL_PREFS).getString(KEY_TOOLS_IV, null)
        BMBLogger.i("SharedPrefs::getKeyToolsIV base64: ", ivString)
        val iv = if (ivString != null) Base64.decode(ivString, Base64.DEFAULT) else return null
        BMBLogger.i("SharedPrefs::getKeyToolsIV raw: ", String(iv))
        return iv
    }

    override fun setKeyToolsIv(iv: ByteArray) {
        BMBLogger.i("SharedPrefs::setKeyToolsIV raw: ", String(iv))
        val keyToolsIv = Base64.encodeToString(iv, Base64.DEFAULT)
        BMBLogger.i("SharedPrefs::setKeyToolsIV base64 set to: ", keyToolsIv)
        getPrivateSharedPreferences(GLOBAL_PREFS).edit().putString(KEY_TOOLS_IV, keyToolsIv).apply()
    }

    override fun enableLaunchProfileSetUp() = getSharedPreferences().edit().putBoolean(IS_LAUNCH_PROFILE_SETUP_ENABLED, true).apply()

    override fun getBannerCount(bannerName: String): Int = getPrivateSharedPreferences(GLOBAL_PREFS).getInt(bannerName + getActiveUserId(), 0)

    override fun setBannerCount(bannerName: String, bannerCount: Int) {
        val activeUserId = getActiveUserId()
        if (activeUserId.isNotEmpty()) {
            getPrivateSharedPreferences(GLOBAL_PREFS).edit().putInt(bannerName + activeUserId, bannerCount).apply()
        }
    }

    override fun getKeystorePassword(): String? = getPrivateSharedPreferences(GLOBAL_PREFS).getString(USER_KEYSTORE_PASSWORD, null)

    override fun setKeystorePassword(keystorePass: CharArray) = getPrivateSharedPreferences(GLOBAL_PREFS).edit().putString(USER_KEYSTORE_PASSWORD, String(keystorePass)).apply()

    override fun resetRetries(userId: String?) {
        userId?.let { setInteger(it + RETRIES, MAX_RETRIES_ATTEMPTS) }
    }

    override fun countRetries(userId: String?): Int = if (userId != null) {
        var retries = getInteger(userId + RETRIES, MAX_RETRIES_ATTEMPTS)
        setInteger(userId + RETRIES, --retries)
        retries
    } else {
        MAX_RETRIES_ATTEMPTS
    }

    override fun getBoolean(key: String): Boolean = getSharedPreferences().getBoolean(key + BOOLEAN_SAVED_INSTANCE, false)
    override fun setBoolean(key: String, value: Boolean) {
        getSharedPreferences().edit().putBoolean(key + BOOLEAN_SAVED_INSTANCE, value).commit()
    }

    override fun getInteger(key: String, defaultValue: Int): Int = getSharedPreferences().getInt(key + MANAGE_SAVED_INSTANCE, defaultValue)
    override fun setInteger(key: String, value: Int) {
        getSharedPreferences().edit().putInt(key + MANAGE_SAVED_INSTANCE, value).commit()
    }

    override fun getString(key: String): String? {
        val helper = SymmetricCryptoHelper.getInstance()
        try {
            val data = getSharedPreferences().getString(key + SAVED_INSTANCE, null)
            if (data != null) {
                return String(helper.decryptAliasWithZeroKey(Base64.decode(data, Base64.DEFAULT)))
            }
        } catch (e: DecryptionFailureException) {
            if (BuildConfig.DEBUG) {
                e.printStackTrace()
            }
        }
        return null
    }

    override fun setString(key: String, value: String?) {
        val helper = SymmetricCryptoHelper.getInstance()
        var data: String? = null
        if (value != null) {
            try {
                data = Base64.encodeToString(helper.encryptAliasWithZeroKey(value.toByteArray()), Base64.NO_WRAP)
            } catch (e: EncryptionFailureException) {
                if (BuildConfig.DEBUG) e.printStackTrace()
            }
        }
        getSharedPreferences().edit().putString(key + "SavedInstance", data).commit()
    }

    override fun getCurrentUserProfileSavedInstance(): UserProfile? {
        val json = getString(CURRENT_USER_PROFILE)
        if (json.isNullOrEmpty()) return null
        return try {
            ObjectMapper().apply { configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false) }.readValue(json, UserProfile::class.java)
        } catch (e: Exception) {
            BMBLogger.e("SharedPreferenceUtils#getCurrentUserProfileSavedInstance " + e.message)
            null
        }
    }

    override fun setCurrentUserProfileSavedInstance(userProfile: UserProfile) {
        try {
            setString(CURRENT_USER_PROFILE, ObjectMapper().writeValueAsString(userProfile))
        } catch (e: JsonProcessingException) {
            e.printStackTrace()
        }
    }

    override fun removeInternationalPaymentsHowItWorksFirstVisitDone(userId: String) {
        if (getSharedPreferences().contains(userId + INTERNATIONAL_PAYMENT_FIRST_VISIT_DONE)) {
            getSharedPreferences().edit().remove(ProfileManager.getInstance().activeUserProfile.userId + INTERNATIONAL_PAYMENT_FIRST_VISIT_DONE).commit()
        }
    }

    private fun getPrivateSharedPreferences(name: String): SharedPreferences = BMBApplication.getInstance().getSharedPreferences(name, Context.MODE_PRIVATE)
    fun getSharedPreferences(): SharedPreferences = getPrivateSharedPreferences(CURRENT_PROFILE_STORE)
}