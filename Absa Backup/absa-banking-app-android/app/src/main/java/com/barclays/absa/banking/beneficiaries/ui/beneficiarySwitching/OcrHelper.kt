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
package com.barclays.absa.banking.beneficiaries.ui.beneficiarySwitching

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.os.Environment
import android.widget.Toast
import androidx.exifinterface.media.ExifInterface
import com.barclays.absa.banking.BuildConfig
import com.barclays.absa.banking.beneficiaries.ui.beneficiarySwitching.dto.OcrResponse
import com.barclays.absa.banking.boundary.monitoring.MonitoringInteractor
import com.barclays.absa.banking.framework.BuildConfigHelper
import com.barclays.absa.banking.framework.utils.BMBLogger
import com.barclays.absa.banking.payments.BeneficiaryImportKitKatActivity
import com.barclays.absa.banking.payments.BeneficiaryImportView
import com.barclays.absa.banking.shared.BaseAlertDialog
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import okhttp3.ConnectionSpec
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.*
import java.util.concurrent.TimeUnit


class OcrHelper {

    companion object {
        private const val MAX_DIMENSION: Float = 900f

        @JvmStatic
        fun resizeBitmap(bitmap: Bitmap): Bitmap? {
            val width = bitmap.width.toFloat()
            val height = bitmap.height.toFloat()
            val scaledWidth: Float
            val scaledHeight: Float

            //determine longest side
            if (width > height) {
                //base calculations on width
                if (width < MAX_DIMENSION) {
                    scaledWidth = width
                } else {
                    scaledWidth = MAX_DIMENSION
                }
                scaledHeight = (height / width) * scaledWidth
            } else {
                //base calculations on height
                if (height < MAX_DIMENSION) {
                    scaledHeight = height
                } else {
                    scaledHeight = MAX_DIMENSION
                }
                scaledWidth = (width / height) * scaledHeight
            }

            return Bitmap.createScaledBitmap(bitmap, scaledWidth.toInt(), scaledHeight.toInt(), false)
        }

        @JvmStatic
        fun submitImageToOcrService(view: BeneficiaryImportView, photoBitmap: Bitmap?, isCameraImport: Boolean) {
            val httpClient = createServiceClient()
            val requestBody = createRequestBody(photoBitmap)
            if (requestBody != null) {
                BMBLogger.d("OCR Request to URL: ", BuildConfigHelper.ocrServiceUrl)
                val httpRequest = createHttpRequest(requestBody)
                GlobalScope.launch(Dispatchers.Default) {
                    try {
                        val response = httpClient.newCall(httpRequest).execute()
                        BMBLogger.d("OCR Test", response.message)
                        val result = response.body?.string()
                        GlobalScope.launch(Dispatchers.Main) {
                            view.dismissProgressDialog()
                            BMBLogger.d("OCR Result", result)
                            try {
                                val ocrResponse = ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false).readValue(result, OcrResponse::class.java)
                                view.sendOcrResult(ocrResponse)
                            } catch (e: Exception) {
                                MonitoringInteractor().logCaughtExceptionEvent(e)
                                if (BuildConfig.DEBUG || BuildConfigHelper.STUB) {
                                    result?.let { BaseAlertDialog.showErrorAlertDialog(it) { _, _ -> view.close() } }
                                } else {
                                    view.showPhotoError(isCameraImport)
                                }
                            }
                        }
                        photoBitmap?.recycle()
                    } catch (e: IOException) {
                        view.dismissProgressDialog()
                        BMBLogger.d("HTTP error", e.message)
                        GlobalScope.launch(Dispatchers.Main) {
                            Toast.makeText(view as Context, "Connection timeout. Please try again...", Toast.LENGTH_LONG).show()
                        }
                    }
                }
            }
        }

        fun submitImage(data: ByteArray, view: BeneficiaryImportView, isCameraImport: Boolean) {
            view.showProgressDialog()

            view.getLifecycleCoroutineScope().launch {
                var fileErroredOut = false
                var finalImageOutput: FileOutputStream? = null
                val externalStorageDirectory = Environment.getExternalStorageDirectory()
                val dir = File("$externalStorageDirectory/DCIM/Absa")
                val fileName = "image_" + Date().time + ".jpg"
                var file: File? = null
                try {
                    if (!dir.exists()) dir.mkdirs()
                    file = File(dir, fileName)

                    finalImageOutput = FileOutputStream(file)
                    finalImageOutput.write(data)
                } catch (e: IOException) {
                    fileErroredOut = true
                    e.printStackTrace()
                } finally {
                    finalImageOutput?.close()
                }
                if (!fileErroredOut) {
                    var capturedBitmap: Bitmap = convertBytesToBitmap(data)
                    val fileNameString = file?.toString()
                    if (fileNameString != null) {
                        if (view is BeneficiaryImportKitKatActivity) {
                            capturedBitmap = rotateImage(capturedBitmap, 90)
                        }

                        val exifInformation = ExifInterface(fileNameString)
                        val imageOrientation = exifInformation.getAttribute(ExifInterface.TAG_ORIENTATION)?.toInt()
                        if (imageOrientation != null && imageOrientation != ExifInterface.ORIENTATION_NORMAL) {
                            capturedBitmap = fixImageOrientation(capturedBitmap, imageOrientation)
                        }
                        val capturedBitmapHasExceedsPermissibleDimensions = capturedBitmap.width > MAX_DIMENSION || capturedBitmap.height > MAX_DIMENSION
                        if (data.size > 4 * 1024 * 1024 || capturedBitmapHasExceedsPermissibleDimensions) {
                            GlobalScope.launch(Dispatchers.Default) {
                                val resizedBitmap = resizeBitmap(capturedBitmap)
                                submitImageToOcrService(view, resizedBitmap, isCameraImport)
                            }
                        } else {
                            submitImageToOcrService(view, capturedBitmap, isCameraImport)
                        }
                    }
                } else {
                    view.showGenericErrorMessage()
                }
            }
        }

        private fun createRequestBody(photo: Bitmap?): RequestBody? {
            if (photo != null) {
                val bos = ByteArrayOutputStream()
                photo.compress(Bitmap.CompressFormat.JPEG, 90, bos)
                val imageData = bos.toByteArray()
                return imageData.toRequestBody(null, 0, imageData.size)
            }
            return null
        }

        private fun createHttpRequest(body: RequestBody): Request {
            return Request.Builder()
                    .addHeader("Code", BuildConfig.ocrServiceCodeHeader)
                    .url(BuildConfigHelper.ocrServiceUrl)
                    .post(body)
                    .build()
        }

        private fun createServiceClient(): OkHttpClient {
            val connectionSpecs = ConnectionSpec.Builder(ConnectionSpec.MODERN_TLS).build()

            return OkHttpClient.Builder()
                    .connectionSpecs(arrayListOf(connectionSpecs))
                    .connectTimeout(45, TimeUnit.SECONDS)
                    .readTimeout(20, TimeUnit.SECONDS)
                    .writeTimeout(45, TimeUnit.SECONDS)
                    .build()
        }

        private fun convertBytesToBitmap(imageBytes: ByteArray): Bitmap {
            return BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
        }

        private fun fixImageOrientation(image: Bitmap, imageOrientation: Int): Bitmap {
            when (imageOrientation) {
                ExifInterface.ORIENTATION_ROTATE_90 ->
                    return rotateImage(image, 90)
                ExifInterface.ORIENTATION_ROTATE_180 ->
                    return rotateImage(image, 180)
                ExifInterface.ORIENTATION_ROTATE_270 ->
                    return rotateImage(image, 270)
            }
            return image
        }

        private fun rotateImage(image: Bitmap, angle: Int): Bitmap {
            val matrix = Matrix()
            matrix.postRotate(angle.toFloat())
            return Bitmap.createBitmap(image, 0, 0, image.width, image.height, matrix, true)
        }

    }

}