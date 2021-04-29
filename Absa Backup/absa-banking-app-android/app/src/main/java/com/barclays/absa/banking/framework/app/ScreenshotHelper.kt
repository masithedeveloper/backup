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

package com.barclays.absa.banking.framework.app

import android.Manifest
import android.app.AlertDialog
import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.*
import android.hardware.Sensor
import android.hardware.SensorManager
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.util.DisplayMetrics
import android.view.View
import android.widget.ScrollView
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.lifecycle.Observer
import com.barclays.absa.banking.R
import com.barclays.absa.banking.boundary.monitoring.MonitoringInteractor
import com.barclays.absa.banking.framework.BaseActivity
import com.barclays.absa.banking.framework.ShakeDetector
import com.barclays.absa.banking.framework.featureSwitching.FeatureSwitchingCache
import com.barclays.absa.banking.framework.featureSwitching.FeatureSwitchingStates
import com.barclays.absa.banking.framework.push.PushNotificationHelper.NOTIFICATION_CHANNELID_IMPORTANT
import com.barclays.absa.banking.shared.BaseAlertDialog
import com.barclays.absa.utils.IUserSettingsManager
import com.barclays.absa.utils.UserSettingsManager
import com.barclays.absa.utils.fileUtils.ScopedStorageFileUtils

object ScreenshotHelper {

    private const val REQUEST_EXTERNAL_STORAGE = 101
    private val PERMISSIONS_STORAGE = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)
    private var sensorManager: SensorManager? = null
    private lateinit var shakeDetector: ShakeDetector
    private lateinit var sensor: Sensor
    private var alertDialog: AlertDialog? = null

    // This will eventually be injected into this class
    private val userSettingsManager: IUserSettingsManager = UserSettingsManager

    fun hasAccelerometer(): Boolean = (getAccelerometer() != null)

    fun setupShakeDetection() {
        val featureSwitching = FeatureSwitchingCache.featureSwitchingToggles
        if (featureSwitching.screenshots == FeatureSwitchingStates.ACTIVE.key && userSettingsManager.isScreenshotsAllowed()) {
            getAccelerometer()?.let {
                sensor = it
                shakeDetector = ShakeDetector()
                shakeDetector.setOnShakeListener(object : ShakeDetector.OnShakeListener {
                    override fun onShake() {
                        saveImage()
                    }
                })
            }
        }
    }

    private fun hapticVibrate() {
        val topMostActivity = BMBApplication.getInstance().topMostActivity
        if (topMostActivity is BaseActivity) {
            val vibrator = topMostActivity.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator?
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator?.vibrate(VibrationEffect.createOneShot(20, 60))
            } else {
                @Suppress("DEPRECATION")
                vibrator?.vibrate(10)
            }
        }
    }

    fun unregisterShakeListener() {
        if (userSettingsManager.isScreenshotsAllowed() && ::shakeDetector.isInitialized) {
            sensorManager?.unregisterListener(shakeDetector)
        }
    }

    fun registerShakeListener() {
        if (userSettingsManager.isScreenshotsAllowed() && ::shakeDetector.isInitialized) {
            sensorManager?.registerListener(shakeDetector, sensor, SensorManager.SENSOR_DELAY_NORMAL)
        }
    }

    fun saveImage() {
        if (userSettingsManager.isScreenshotsAllowed()) {
            showScreenshotAlertDialog()
            hapticVibrate()
        }
    }

    private fun hasStoragePermissions(): Boolean {
        val topMostActivity = BMBApplication.getInstance().topMostActivity
        if (topMostActivity is BaseActivity) {
            val permission: Int = ActivityCompat.checkSelfPermission(topMostActivity, Manifest.permission.WRITE_EXTERNAL_STORAGE)

            if (permission != PackageManager.PERMISSION_GRANTED) {
                topMostActivity.requestPermissions(PERMISSIONS_STORAGE, REQUEST_EXTERNAL_STORAGE)
                return false
            }
        }
        return true
    }

    fun screenShot(view: View): Bitmap {
        return screenShot(view, false)
    }

    fun screenShot(view: View, shouldScroll: Boolean): Bitmap {
        val baseBitmap = drawBaseBitmap(view, shouldScroll)

        if (BaseAlertDialog.isDialogShowing() && BaseAlertDialog.alertDialog.window?.decorView?.rootView != null) {
            val dialogView: View = BaseAlertDialog.alertDialog.window?.decorView?.rootView!!

            val location = IntArray(2)
            val location2 = IntArray(2)
            view.getLocationOnScreen(location)
            dialogView.getLocationOnScreen(location2)

            val paint = Paint()
            val colorFilter = LightingColorFilter(0xFF999999.toInt(), 0x00000000)
            paint.colorFilter = colorFilter

            Canvas(baseBitmap).apply {
                drawBitmap(drawBaseBitmap(view, shouldScroll), 0f, 0f, paint)
                drawDialogBitmap(dialogView)?.let {
                    drawBitmap(it, (location2[0] - location[0]).toFloat(), (location2[1] - location[1]).toFloat(), Paint())
                }
            }
        } else {
            Canvas(baseBitmap).apply {
                drawBitmap(drawBaseBitmap(view, shouldScroll), 0f, 0f, Paint())
            }
        }

        return baseBitmap
    }

    private fun drawDialogBitmap(dialogView: View): Bitmap? {
        return try {
            val bitmap = Bitmap.createBitmap(dialogView.width, dialogView.height, Bitmap.Config.ARGB_8888)
            Canvas(bitmap).apply {
                dialogView.draw(this)
            }
            bitmap
        } catch (e: java.lang.Exception) {
            null
        }
    }

    private fun drawBaseBitmap(view: View, shouldScroll: Boolean): Bitmap {
        val metrics: Rect
        var height: Int
        var width: Int

        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.Q) {
            metrics = BMBApplication.getInstance().topMostActivity.windowManager.currentWindowMetrics.bounds
            height = metrics.height()
            width = metrics.width()
        } else {
            val displayMetrics = DisplayMetrics()
            BMBApplication.getInstance().topMostActivity.windowManager.defaultDisplay.getMetrics(displayMetrics)
            height = displayMetrics.heightPixels
            width = displayMetrics.widthPixels
        }

        if (view is ScrollView && shouldScroll) {
            if (view.childCount > 0) {
                height = view.getChildAt(0).height
                width = view.getChildAt(0).width
            }
        } else {
            width = view.width
            height = view.height
        }

        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        Canvas(bitmap).apply {
            view.draw(this)
        }
        return bitmap
    }

    fun saveImage(finalBitmap: Bitmap) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val uri = ScopedStorageFileUtils().saveImageToGalleryAsyncAndroidQ(BMBApplication.getInstance().topMostActivity, finalBitmap)
            uri.observe(BMBApplication.getInstance().topMostActivity as BaseActivity, Observer {
                showPushNotification(it)
            })
        } else {
            val uri = ScopedStorageFileUtils().saveImageToGalleryAsyncOlderApi(BMBApplication.getInstance().topMostActivity, finalBitmap)
            uri.observe(BMBApplication.getInstance().topMostActivity as BaseActivity, Observer {
                showPushNotification(it)
            })
        }
    }

    private fun showPushNotification(uri: Uri?) {
        val appContext = BMBApplication.getInstance().applicationContext

        val intent = Intent().apply {
            action = Intent.ACTION_VIEW
            setDataAndType(uri, "image/*")
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }

        val pendingIntent = PendingIntent.getActivity(appContext, 0, intent, 0)
        val body = appContext.getString(R.string.screenshot_click_for_screenshot)

        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val mBuilder = NotificationCompat.Builder(appContext, NOTIFICATION_CHANNELID_IMPORTANT)
        val title = appContext.getString(R.string.screenshot_saved)
        val notification = mBuilder.setSmallIcon(R.drawable.logo_white_full).setTicker(title).setWhen(0)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setDefaults(NotificationCompat.DEFAULT_VIBRATE or NotificationCompat.DEFAULT_LIGHTS)
                .setAutoCancel(true)
                .setContentTitle(title)
                .setStyle(NotificationCompat.BigTextStyle().bigText(String.format("%s\n%s", body, "")))
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent)
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                .setLargeIcon(BitmapFactory.decodeResource(appContext.resources, R.drawable.logo_white_full))
                .setTicker(title)
                .setContentText(body + "\n" + "").build()

        notification.flags = notification.flags or Notification.FLAG_AUTO_CANCEL
        val notificationManager = appContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(1, notification)
    }

    private fun showScreenshotAlertDialog() {
        val topMostActivity = BMBApplication.getInstance().topMostActivity
        val alertDialogShowing = alertDialog?.isShowing ?: false
        if (alertDialogShowing) return
        if (topMostActivity is BaseActivity && !topMostActivity.isFinishing) {
            alertDialog = AlertDialog.Builder(topMostActivity, R.style.MyDialogTheme)
                    .setTitle(topMostActivity.getString(R.string.screenshot_dialog_title))
                    .setMessage(topMostActivity.getString(R.string.screenshot_dialog_content))
                    .setPositiveButton(R.string.yes) { dialog, _ ->
                        if (hasStoragePermissions()) {
                            val bitmap = screenShot(topMostActivity.window.decorView.rootView)
                            saveImage(bitmap)
                            dialog.dismiss()
                        }
                    }
                    .setNegativeButton(R.string.no) { dialog, _ -> dialog.dismiss() }
                    .setCancelable(false)
                    .create()
            alertDialog?.show()
        }
    }

    private fun getAccelerometer(): Sensor? = try {
        val appContext = BMBApplication.getInstance().applicationContext
        sensorManager = appContext.getSystemService(Context.SENSOR_SERVICE) as? SensorManager
        sensorManager?.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
    } catch (e: Exception) {
        MonitoringInteractor().logCaughtExceptionEvent(e)
        null
    }
}