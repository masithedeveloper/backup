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

@file:Suppress("DEPRECATION")

package com.barclays.absa.banking.payments

import android.annotation.TargetApi
import android.app.Activity
import android.content.Intent
import android.graphics.SurfaceTexture
import android.hardware.Camera
import android.os.Build
import android.os.Bundle
import android.view.TextureView
import com.barclays.absa.banking.R
import com.barclays.absa.banking.beneficiaries.ui.beneficiarySwitching.OcrHelper
import com.barclays.absa.banking.boundary.monitoring.MonitoringInteractor
import com.barclays.absa.utils.PermissionHelper
import kotlinx.android.synthetic.main.beneficiary_import_activity.*
import java.io.IOException

@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
class BeneficiaryImportKitKatActivity : BeneficiaryImportBaseActivity() {

    private lateinit var previewSurfaceTexture: SurfaceTexture
    private lateinit var camera: Camera
    private lateinit var cameraParameters: Camera.Parameters

    var rawPictureCallback: Camera.PictureCallback = Camera.PictureCallback { data, _ ->
        OcrHelper.submitImage(data, this@BeneficiaryImportKitKatActivity, true)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.beneficiary_import_activity)
        cameraPreviewView?.surfaceTextureListener = TextureListener()
        takePhotoButton?.setOnClickListener {
            PermissionHelper.requestExternalStorageWritePermission(this) {
                if (cameraParameters.supportedFocusModes.contains(Camera.Parameters.FLASH_MODE_AUTO)) {
                    camera.autoFocus { success, camera ->
                        if (success) {
                            camera.takePicture(null, null, rawPictureCallback)
                        }
                    }
                } else {
                    camera.takePicture(null, null, rawPictureCallback)
                }
                showProgressDialog()
            }
        }
    }

    private inner class TextureListener : TextureView.SurfaceTextureListener {

        override fun onSurfaceTextureAvailable(surface: SurfaceTexture, width: Int, height: Int) {
            previewSurfaceTexture = surface
            previewSurfaceTexture.setDefaultBufferSize(width, height)
            PermissionHelper.requestCameraAccessPermission(this@BeneficiaryImportKitKatActivity) {
                initializeCameraAndPreview()
            }

        }

        override fun onSurfaceTextureSizeChanged(surface: SurfaceTexture, width: Int, height: Int) {

        }

        override fun onSurfaceTextureDestroyed(surface: SurfaceTexture): Boolean {
            camera.stopPreview()
            camera.release()
            return true
        }

        override fun onSurfaceTextureUpdated(surface: SurfaceTexture) {

        }

    }

    private fun findRearFacingCamera(): Int {
        val numberOfCameras = Camera.getNumberOfCameras()
        var cameraId = -1
        for (i in 0 until numberOfCameras) {
            val info = Camera.CameraInfo()
            Camera.getCameraInfo(i, info)
            if (info.facing == Camera.CameraInfo.CAMERA_FACING_BACK) {
                cameraId = i
                break
            }
        }
        return cameraId
    }

    private fun initializeCameraAndPreview() {
        var cameraId = findRearFacingCamera()
        if (cameraId == -1) {
            cameraId = 0
        }
        camera = Camera.open(cameraId)
        cameraParameters = camera.parameters
        if (cameraParameters.supportedFocusModes.contains(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE)) {
            cameraParameters.focusMode = Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE
        } else if (cameraParameters.supportedFocusModes.contains(Camera.Parameters.FOCUS_MODE_AUTO)) {
            cameraParameters.focusMode = Camera.Parameters.FOCUS_MODE_AUTO
        }
        if (cameraParameters.supportedFlashModes != null && cameraParameters.supportedFlashModes.contains(Camera.Parameters.FLASH_MODE_AUTO)) {
            cameraParameters.flashMode = Camera.Parameters.FLASH_MODE_AUTO
        }
        camera.parameters = cameraParameters
        try {
            camera.setPreviewTexture(previewSurfaceTexture)
            camera.setDisplayOrientation(90)
            camera.startPreview()
        } catch (e: IOException) {
            MonitoringInteractor().logCaughtExceptionEvent(e)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_CANCELED) {
            finish()
        }
    }

}