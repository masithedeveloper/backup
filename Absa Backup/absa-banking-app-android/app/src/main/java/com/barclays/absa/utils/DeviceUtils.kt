/*
 * Copyright (c) 2018 Absa Bank Limited, All Rights Reserved.
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

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.os.Build
import android.telephony.TelephonyManager
import com.barclays.absa.banking.framework.app.BMBApplication
import com.barclays.absa.banking.framework.app.BMBConstants
import com.barclays.absa.banking.framework.utils.BMBLogger
import com.barclays.absa.banking.manage.devices.services.dto.Device
import com.barclays.absa.crypto.SecureUtils
import java.util.*

object DeviceUtils {
    private const val TABLET_CHANNEL_IND = "T"
    private const val SMARTPHONE_CHANNEL_IND = "S"
    private const val DEVICE_IDENTIFIER = "deviceIdentifier"
    private const val UNIQUE_ID = "UUID"

    private val TAG = DeviceUtils::class.java.simpleName
    private var packageInfo: PackageInfo? = null

    init {
        try {
            val context: Context = BMBApplication.getInstance()
            val packageManager: PackageManager = context.packageManager
            val appPackageName: String = context.packageName
            packageInfo = packageManager.getPackageInfo(appPackageName, 0)
        } catch (e: PackageManager.NameNotFoundException) {
            BMBLogger.e(TAG, e.localizedMessage)
        }
    }

    @JvmStatic
    val channelId: String
        get() = if (BMBConstants.PHONE.equals(deviceType, ignoreCase = true)) SMARTPHONE_CHANNEL_IND else TABLET_CHANNEL_IND

    private val deviceType: String
        get() {
            val screenLayout = BMBApplication.getInstance().resources.configuration.screenLayout
            val layoutSize = screenLayout and Configuration.SCREENLAYOUT_SIZE_MASK
            return when (layoutSize) {
                Configuration.SCREENLAYOUT_SIZE_LARGE -> BMBConstants.TABLET_7_INCH
                0x00000004 -> BMBConstants.TABLET_10_INCH
                else -> BMBConstants.PHONE
            }
        }

    @JvmStatic
    fun isCurrentDevice(device: Device): Boolean {
        val currentDeviceID = SecureUtils.getDeviceID()
        return device.imei != null && device.imei == currentDeviceID
    }

    @JvmStatic
    val buildModel: String = Build.MODEL

    @JvmStatic
    val osName: String = "Android"

    @JvmStatic
    val osVersion: String = Build.VERSION.SDK_INT.toString()

    @JvmStatic
    val versionName: String
        get() = packageInfo?.versionName ?: ""

    @JvmStatic
    val hardwareDeviceId: String
        @SuppressLint("MissingPermission", "HardwareIds")
        get() {
            val context: Context = BMBApplication.getInstance()
            return try {
                if (PermissionHelper.isPermissionReadPhoneStateGranted(context)) {
                    val telephonyManager = context.getSystemService(Context.TELEPHONY_SERVICE) as? TelephonyManager
                    @Suppress("DEPRECATION")
                    telephonyManager?.deviceId ?: ""
                } else ""
            } catch (e: Exception) {
                ""
            }
        }

    fun resetDeviceUuid() {
        SecureUtils.removeEncryptedToken()
        SecureUtils.removeOldKey()
        BMBApplication.getInstance().getSharedPreferences(DEVICE_IDENTIFIER, Context.MODE_PRIVATE).edit().remove(UNIQUE_ID).apply()
    }

    @JvmStatic
    val deviceUuid: String
        get() {
            val context = BMBApplication.getInstance() ?: return ""
            val preferences = context.getSharedPreferences(DEVICE_IDENTIFIER, Context.MODE_PRIVATE)
            var uuid = preferences.getString(UNIQUE_ID, null)
            if (uuid == null) {
                uuid = UUID.randomUUID().toString()
                uuid = uuid.replace("-".toRegex(), "")
                val preferencesEditor = preferences.edit()
                preferencesEditor.putString(UNIQUE_ID, uuid)
                if (!preferencesEditor.commit()) {
                    BMBLogger.e(TAG, "Could not save UUID to SharedPrefs.... This is a fatal error. Will not throw a runtime error")
                    throw RuntimeException("Could not save UUID to SharedPrefs. Could it be the device is out of space?")
                }
            }
            return uuid
        }

    fun setDeviceUuid(deviceIdentifier: String) {
        val preferences = BMBApplication.getInstance().getSharedPreferences(DEVICE_IDENTIFIER, Context.MODE_PRIVATE)
        preferences.edit().putString(UNIQUE_ID, deviceIdentifier).apply()
    }

    @JvmStatic
    fun hasUuid(): Boolean = BMBApplication.getInstance().getSharedPreferences(DEVICE_IDENTIFIER, Context.MODE_PRIVATE).contains(UNIQUE_ID)
}