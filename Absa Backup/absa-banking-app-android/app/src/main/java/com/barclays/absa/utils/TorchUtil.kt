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

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.SurfaceTexture
import android.hardware.camera2.*
import android.view.Surface
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.barclays.absa.banking.R
import com.barclays.absa.banking.framework.app.BMBConstants
import com.barclays.absa.banking.framework.utils.BMBLogger
import za.co.absa.presentation.uilib.BuildConfig

class TorchUtil(private var context: Context, private var callback: TorchConfiguredCallback) {

    private var isTorchOn = false
    private lateinit var builder: CaptureRequest.Builder
    private var cameraSession: CameraCaptureSession? = null
    private var cameraDevice: CameraDevice? = null
    private var surfaceTexture: SurfaceTexture? = null
    private lateinit var surface: Surface
    private val cameraManager = context.getSystemService(Context.CAMERA_SERVICE) as CameraManager

    init {
        try {
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                val cameraId = cameraManager.cameraIdList.first()
                cameraManager.openCamera(cameraId, object : CameraDevice.StateCallback() {
                    override fun onOpened(camera: CameraDevice) {
                        cameraDevice = camera
                        surfaceTexture = SurfaceTexture(1)
                        try {
                            builder = camera.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW)
                            surface = Surface(surfaceTexture)
                            builder.addTarget(surface)
                            camera.createCaptureSession(listOf(surface), cameraCaptureSessionCallback, null)
                        } catch (e: Exception) {
                            BMBLogger.d(e.message)
                        }
                    }

                    override fun onDisconnected(camera: CameraDevice) {}
                    override fun onError(camera: CameraDevice, error: Int) {}
                }, null)
            }
        } catch (e: Exception) {
            if (BuildConfig.DEBUG) {
                Toast.makeText(context, R.string.prepaid_electricity_initialise_camera_error, Toast.LENGTH_LONG).show()
                BMBLogger.e(BMBConstants.LOGTAG, "Failed to access Camera permission$e")
            }
        }
    }

    private fun setTorchModeOn() {
        cameraSession?.apply {
            try {
                builder.set(CaptureRequest.FLASH_MODE, CameraMetadata.FLASH_MODE_TORCH)
                setRepeatingRequest(builder.build(), null, null)
            } catch (e: Exception) {
                Toast.makeText(context, R.string.prepaid_electricity_initialise_camera_error, Toast.LENGTH_LONG).show()
                BMBLogger.d(e.message)
            }
        }
        isTorchOn = true
    }

    fun setTorchModeOff() {
        if (cameraSession != null && isTorchOn) {
            try {
                builder.set(CaptureRequest.FLASH_MODE, CameraMetadata.FLASH_MODE_OFF)
                cameraSession?.setRepeatingRequest(builder.build(), null, null)
            } catch (e: Exception) {
                Toast.makeText(context, R.string.prepaid_electricity_initialise_camera_error, Toast.LENGTH_LONG).show()
                BMBLogger.d(e.message)
            }
        }
        isTorchOn = false
    }

    fun toggleTorch(): Boolean {
        return if (isTorchOn) {
            setTorchModeOff()
            false
        } else {
            setTorchModeOn()
            true
        }
    }

    val cameraCaptureSessionCallback = object : CameraCaptureSession.StateCallback() {
        override fun onConfigureFailed(session: CameraCaptureSession) {
        }

        override fun onConfigured(session: CameraCaptureSession) {
            cameraSession = session
            try {
                builder.build().let { cameraSession?.setRepeatingRequest(it, null, null) }
                callback.configureComplete()
            } catch (e: Exception) {
                Toast.makeText(context, R.string.prepaid_electricity_initialise_camera_error, Toast.LENGTH_LONG).show()
                BMBLogger.d(e.message)
            }
        }
    }

    fun closeCamera() {
        cameraSession?.close()
        cameraDevice?.close()
    }

    interface TorchConfiguredCallback {
        fun configureComplete()
    }
}