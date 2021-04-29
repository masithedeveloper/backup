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
package com.barclays.absa.banking.payments

import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.ImageFormat
import android.graphics.SurfaceTexture
import android.hardware.camera2.*
import android.media.ImageReader
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Size
import android.util.SparseIntArray
import android.view.OrientationEventListener.ORIENTATION_UNKNOWN
import android.view.Surface
import android.view.TextureView
import android.view.TextureView.SurfaceTextureListener
import android.view.View
import android.widget.Button
import android.widget.ImageView
import androidx.constraintlayout.widget.ConstraintLayout
import com.barclays.absa.banking.R
import com.barclays.absa.banking.beneficiaries.ui.beneficiarySwitching.OcrHelper
import com.barclays.absa.banking.boundary.monitoring.MonitoringInteractor
import com.barclays.absa.banking.framework.utils.BMBLogger
import com.barclays.absa.utils.PermissionHelper
import com.barclays.absa.utils.ProfileManager
import java.util.*

@TargetApi(Build.VERSION_CODES.LOLLIPOP)
class BeneficiaryImportActivity : BeneficiaryImportBaseActivity() {

    private var camera: CameraDevice? = null
    private lateinit var cameraManager: CameraManager
    private var preview: TextureView? = null
    private var previewSurfaceTexture: SurfaceTexture? = null
    private var previewSurface: Surface? = null
    private var jpegCaptureSurface: Surface? = null
    private var takePhotoButton: Button? = null
    private var okButton: Button? = null
    private var cameraId: String? = ""
    private var previewSize: Size? = null
    private var jpegImageReader: ImageReader? = null
    private lateinit var cameraCharacteristics: CameraCharacteristics
    private var sensorOrientation: Int? = -1

    companion object {
        private val ORIENTATIONS = SparseIntArray(4)

        init {
            ORIENTATIONS.append(Surface.ROTATION_0, 90)
            ORIENTATIONS.append(Surface.ROTATION_90, 0)
            ORIENTATIONS.append(Surface.ROTATION_180, 270)
            ORIENTATIONS.append(Surface.ROTATION_270, 180)
        }
    }

    private inner class TextureListener : SurfaceTextureListener {

        override fun onSurfaceTextureAvailable(surface: SurfaceTexture, width: Int, height: Int) {
            previewSurfaceTexture = surface
            startCamera()
        }

        override fun onSurfaceTextureSizeChanged(surface: SurfaceTexture, width: Int, height: Int) {}
        override fun onSurfaceTextureUpdated(surface: SurfaceTexture) {}

        override fun onSurfaceTextureDestroyed(surface: SurfaceTexture): Boolean {
            return false
        }
    }

    private inner class CameraDeviceStateCallback : CameraDevice.StateCallback() {

        override fun onOpened(cameraDevice: CameraDevice) {
            camera = cameraDevice
            val cameraSurfaces: List<Surface?> = listOf(jpegCaptureSurface, previewSurface)
            val cameraCaptureSessionStateCallback = CameraCaptureSessionStateCallback()
            camera?.createCaptureSession(cameraSurfaces, cameraCaptureSessionStateCallback, null)
        }

        override fun onClosed(camera: CameraDevice) {}
        override fun onDisconnected(camera: CameraDevice) {}
        override fun onError(camera: CameraDevice, error: Int) {}
    }

    private val tag = javaClass.simpleName

    private inner class CameraCaptureSessionCaptureCallback : CameraCaptureSession.CaptureCallback() {

        override fun onCaptureStarted(session: CameraCaptureSession, request: CaptureRequest, timestamp: Long, frameNumber: Long) {
            //do nothing as yet
            BMBLogger.d(tag, "Capture started...")
        }

        override fun onCaptureCompleted(session: CameraCaptureSession, request: CaptureRequest, result: TotalCaptureResult) {
            //super.onCaptureCompleted(session, request, result)
            BMBLogger.d(tag, "Capture completed...")
        }
    }

    private inner class CameraCaptureSessionStateCallback : CameraCaptureSession.StateCallback() {

        override fun onConfigureFailed(session: CameraCaptureSession) {
            BMBLogger.d(tag, "Capture session config failed...")
        }

        override fun onConfigured(session: CameraCaptureSession) {
            try {
                previewSurfaceTexture?.setDefaultBufferSize(previewSize?.width!!, previewSize?.height!!)
                val cameraPreviewRequestBuilder = camera?.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW)
                cameraPreviewRequestBuilder?.addTarget(previewSurface!!)

                val cameraJpegCaptureBuilder = camera?.createCaptureRequest(CameraDevice.TEMPLATE_STILL_CAPTURE)
                cameraJpegCaptureBuilder?.addTarget(jpegCaptureSurface!!)
                cameraJpegCaptureBuilder?.set(CaptureRequest.CONTROL_AE_MODE, CaptureRequest.CONTROL_AE_MODE_ON_AUTO_FLASH)
                cameraJpegCaptureBuilder?.set(CaptureRequest.CONTROL_AF_MODE, CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE)

                val cameraCapturePreviewRequest = cameraPreviewRequestBuilder?.build()
                if (cameraCapturePreviewRequest != null) {
                    session.setRepeatingRequest(cameraCapturePreviewRequest, CameraCaptureSessionCaptureCallback(), null)
                }

                takePhotoButton?.let { photoButton ->
                    photoButton.setOnClickListener {
                        preventDoubleClick(photoButton)
                        PermissionHelper.requestExternalStorageWritePermission(this@BeneficiaryImportActivity) {
                            //NB: because I have not set the CaptureRequest flags the same, the image captured on the JPEG buffer
                            //could be different from the one seen in the preview;
                            //this code should be in a function that gets called when you click the 'take picture' button
                            session.stopRepeating()
                            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
                                session.abortCaptures()
                            }
                            val cameraCaptureJpegRequest: CaptureRequest.Builder? = cameraJpegCaptureBuilder
                            if (cameraCaptureJpegRequest != null) {
                                val deviceOrientation: Int = display?.rotation ?: ORIENTATION_UNKNOWN
                                cameraCaptureJpegRequest[CaptureRequest.JPEG_ORIENTATION] = getJpegOrientation(deviceOrientation)
                                session.capture(cameraCaptureJpegRequest.build(), CameraCaptureSessionCaptureCallback(), Handler(Looper.getMainLooper()))
                            }
                        }
                    }
                }
            } catch (e: Exception) {
                MonitoringInteractor().logCaughtExceptionEvent(e)
            }
        }

        private fun getJpegOrientation(deviceOrientation: Int): Int {
            if (deviceOrientation == ORIENTATION_UNKNOWN) return 0
            var orientation = (deviceOrientation + 45) / 90 * 90
            val isFrontFacingCamera = cameraCharacteristics.get(CameraCharacteristics.LENS_FACING) == CameraCharacteristics.LENS_FACING_FRONT
            if (isFrontFacingCamera) orientation = -orientation
            return (sensorOrientation!! + orientation + 360) % 360
        }
    }

    @SuppressLint("MissingPermission")
    private fun initiateCameraCaptureSession() {
        try {
            for (camId in cameraManager.cameraIdList) {
                cameraCharacteristics = cameraManager.getCameraCharacteristics(camId)
                if (cameraCharacteristics.get(CameraCharacteristics.LENS_FACING) == CameraCharacteristics.LENS_FACING_BACK) {
                    cameraId = camId
                    val streamConfigurationMap = cameraCharacteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP)
                    previewSize = if (streamConfigurationMap?.getOutputSizes(ImageFormat.RAW_SENSOR) != null) {
                        decideBestSizeToUseForPreview(streamConfigurationMap.getOutputSizes(ImageFormat.RAW_SENSOR)!!, preview?.width!!, preview?.height!!)
                    } else {
                        Size(preview?.width!!, preview?.height!!)
                    }
                    val jpegSizes = streamConfigurationMap?.getOutputSizes(ImageFormat.JPEG)
                    if (!jpegSizes.isNullOrEmpty()) {
                        previewSize = if (streamConfigurationMap.getOutputSizes(ImageFormat.JPEG) != null) {
                            decideBestSizeToUseForPreview(streamConfigurationMap.getOutputSizes(ImageFormat.JPEG)!!, preview?.width!!, preview?.height!!)
                        } else {
                            Size(preview?.width!!, preview?.height!!)
                        }
                        sensorOrientation = cameraCharacteristics.get(CameraCharacteristics.SENSOR_ORIENTATION)
                        jpegImageReader = ImageReader.newInstance(previewSize?.width!!, previewSize?.height!!, ImageFormat.JPEG, 1)
                        jpegImageReader?.setOnImageAvailableListener({
                            val finalImage = it.acquireLatestImage()
                            val finalImageByteBuffer = finalImage.planes[0].buffer
                            val finalImageBytes = ByteArray(finalImageByteBuffer.remaining())
                            finalImageByteBuffer.get(finalImageBytes)
                            OcrHelper.submitImage(finalImageBytes, this@BeneficiaryImportActivity, true)
                            finalImage.close()
                        }, Handler(Looper.getMainLooper()))
                        jpegCaptureSurface = jpegImageReader?.surface
                    }

                    previewSurface = Surface(previewSurfaceTexture)

                    val cameraDeviceStateCallback = CameraDeviceStateCallback()
                    cameraManager.openCamera(cameraId!!, cameraDeviceStateCallback, Handler(Looper.getMainLooper()))
                    break
                }
            }
        } catch (e: CameraAccessException) {
            MonitoringInteractor().logCaughtExceptionEvent(e)
            BMBLogger.d(BeneficiaryImportActivity::class.simpleName, e.message)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.beneficiary_import_activity)
        preview = findViewById(R.id.cameraPreviewView)
        preview?.surfaceTextureListener = TextureListener()
        takePhotoButton = findViewById(R.id.takePhotoButton)
        okButton = findViewById(R.id.okButton)

        val takePhotoView = findViewById<ConstraintLayout>(R.id.takePhotoView)
        val closeImageView = findViewById<ImageView>(R.id.closeImageView)
        val questionMarkImageView = findViewById<ImageView>(R.id.questionMarkImageView)

        if (!seenTakePhotoView()) {
            takePhotoView!!.visibility = View.VISIBLE
            takePhotoButton!!.visibility = View.GONE
        }

        okButton!!.setOnClickListener {
            setFirstTakePhoto()
            takePhotoView.visibility = View.GONE
            takePhotoButton!!.visibility = View.VISIBLE
        }

        questionMarkImageView.setOnClickListener {
            takePhotoView!!.visibility = View.VISIBLE
            takePhotoButton!!.visibility = View.GONE
        }

        closeImageView.setOnClickListener {
            onBackPressed()
        }
    }

    override fun onStart() {
        super.onStart()
        if (previewSurfaceTexture != null) {
            startCamera()
        }
    }

    private fun seenTakePhotoView(): Boolean {
        val activeUserProfile = ProfileManager.getInstance().activeUserProfile
        var userId: String? = ""
        if (activeUserProfile != null) {
            userId = activeUserProfile.userId
        }
        val userProfileKey = userId!! + "FIRST_TAKE_PHOTO"
        val sharedPreferences = applicationContext.getSharedPreferences(userProfileKey, Context.MODE_PRIVATE)
        return sharedPreferences.getBoolean("FIRST_TAKE_PHOTO", false)
    }

    private fun setFirstTakePhoto() {
        val activeUserProfile = ProfileManager.getInstance().activeUserProfile
        var userId: String? = ""
        if (activeUserProfile != null) {
            userId = activeUserProfile.userId
        }
        val userProfileKey = userId!! + "FIRST_TAKE_PHOTO"
        val sharedPreferences = applicationContext.getSharedPreferences(userProfileKey, Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putBoolean("FIRST_TAKE_PHOTO", true)
        editor.apply()
    }

    @SuppressLint("MissingPermission")
    private fun startCamera() {
        cameraManager = getSystemService(Context.CAMERA_SERVICE) as CameraManager
        initiateCameraCaptureSession()
    }

    private fun decideBestSizeToUseForPreview(supportedSizes: Array<Size>, previewSurfaceWidth: Int, previewSurfaceHeight: Int): Size? {
        val shortListedSizes: MutableList<Size> = ArrayList()
        if (!supportedSizes.isNullOrEmpty()) {
            supportedSizes.forEach { supportedSize ->
                if (previewSurfaceWidth > previewSurfaceHeight) {
                    if (supportedSize.width > previewSurfaceWidth && supportedSize.height > previewSurfaceHeight) {
                        shortListedSizes.add(supportedSize)
                    }
                } else {
                    if (supportedSize.width > previewSurfaceHeight && supportedSize.height > previewSurfaceWidth) {
                        shortListedSizes.add(supportedSize)
                    }
                }
            }
        }

        if (shortListedSizes.isNotEmpty()) {
            return Collections.min(shortListedSizes) { o1, o2 -> java.lang.Long.signum((o1.width * o2.height - o2.width * o2.height).toLong()) }
        } else {
            shortListedSizes.add(Size(previewSurfaceWidth, previewSurfaceHeight))
        }
        return shortListedSizes[0]
    }

    override fun onStop() {
        camera?.close()
        jpegCaptureSurface?.release()
        previewSurface?.release()
        super.onStop()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_CANCELED) {
            finish()
        }
    }
}