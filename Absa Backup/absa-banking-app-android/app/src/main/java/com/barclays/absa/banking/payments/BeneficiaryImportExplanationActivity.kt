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

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.View
import androidx.core.app.ActivityCompat
import androidx.databinding.DataBindingUtil
import com.barclays.absa.banking.R
import com.barclays.absa.banking.beneficiaries.ui.beneficiarySwitching.OcrHelper
import com.barclays.absa.banking.beneficiaries.ui.beneficiarySwitching.dto.OcrResponse
import com.barclays.absa.banking.boundary.monitoring.MonitoringInteractor
import com.barclays.absa.banking.databinding.ImportBeneficiaryExplanationActivityBinding
import com.barclays.absa.banking.framework.app.BMBApplication
import com.barclays.absa.banking.presentation.genericResult.GenericResultActivity
import com.barclays.absa.utils.CompatibilityUtils
import com.barclays.absa.utils.PermissionHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import styleguide.screens.GenericResultScreenFragment
import styleguide.utils.TextFormattingUtils
import java.io.ByteArrayOutputStream

class BeneficiaryImportExplanationActivity : BeneficiaryImportBaseActivity() {
    private lateinit var binding: ImportBeneficiaryExplanationActivityBinding
    private lateinit var importBeneficiaryDescription: String

    private var justReturnedFromSettings: Boolean = false

    companion object {
        private const val REQUEST_PICK_PICTURE = 200
        private const val REQUEST_CAMERA_TAKE_PICTURE = 300
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.inflate(layoutInflater, R.layout.import_beneficiary_explanation_activity, null, false)
        setContentView(binding.root)
        binding.root.setLayerType(View.LAYER_TYPE_SOFTWARE, null)

        setToolBarBack(R.string.payments_import_beneficiaries_screen_title)
        binding.selectFromGalleryButton.setOnClickListener {
            PermissionHelper.requestExternalStorageWritePermission(this) {
                openPhotoChooserIntent()
            }
        }
        binding.takeAPhotoButton.setOnClickListener {
            requestCameraPermission()
        }

        importBeneficiaryDescription = getString(R.string.payments_import_beneficiaries_explanation_body)
        binding.importBeneficiaryTextView.text = TextFormattingUtils.formatToBulletList(importBeneficiaryDescription, "\n\n")
    }

    override fun onResume() {
        super.onResume()

        if (justReturnedFromSettings) {
            justReturnedFromSettings = false
            requestCameraPermission()
        }
    }

    private fun startBeneficiaryImportActivity() {
        if (CompatibilityUtils.isVersionGreaterThanOrEqualTo(Build.VERSION_CODES.LOLLIPOP)) {
            startActivityForResult(Intent(this, BeneficiaryImportActivity::class.java), REQUEST_CAMERA_TAKE_PICTURE)
        } else {
            startActivityForResult(Intent(this, BeneficiaryImportKitKatActivity::class.java), REQUEST_CAMERA_TAKE_PICTURE)
        }
    }

    private fun requestCameraPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)) {
            PermissionHelper.requestCameraAccessPermission(this) {
                startBeneficiaryImportActivity()
            }
        } else {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                showPermissionResultScreen()
            } else {
                startBeneficiaryImportActivity()
            }
        }
    }

    private fun openPhotoChooserIntent() {
        val pickIntent = Intent(Intent.ACTION_PICK)
        pickIntent.type = "image/*"

        val chooserIntent = Intent.createChooser(pickIntent, getString(R.string.beneficiary_import_select_picture))
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, pickIntent)

        startActivityForResult(chooserIntent, REQUEST_PICK_PICTURE)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            if (requestCode == PermissionHelper.PermissionCode.WRITE_TO_EXTERNAL_STORAGE.value) {
                openPhotoChooserIntent()
            } else if (requestCode == PermissionHelper.PermissionCode.ACCESS_CAMERA.value) {
                startBeneficiaryImportActivity()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            if (data != null) {
                if (requestCode == REQUEST_PICK_PICTURE && data.data != null) {
                    try {
                        showProgressDialog()
                        GlobalScope.launch(Dispatchers.Default) {
                            val inputStream = contentResolver.openInputStream(data.data!!)
                            val galleryPictureByteArrayOutputStream = ByteArrayOutputStream()
                            val bytes = ByteArray(1024)
                            while (inputStream?.read(bytes) != -1) {
                                galleryPictureByteArrayOutputStream.write(bytes)
                            }
                            val finalImageBytes = galleryPictureByteArrayOutputStream.toByteArray()
                            OcrHelper.submitImage(finalImageBytes, this@BeneficiaryImportExplanationActivity, false)
                        }
                    } catch (e: Exception) {
                        MonitoringInteractor().logCaughtExceptionEvent(e)
                    }
                    return
                }
                val ocrResponse = data.getParcelableExtra<OcrResponse>(ocrResult)
                sendOcrResult(ocrResponse)
            }
        }
    }

    private fun showPermissionResultScreen() {
        val intent = Intent(this, GenericResultActivity::class.java)
        intent.apply {
            putExtra(GenericResultActivity.IS_FAILURE, true)
            putExtra(GenericResultActivity.NOTICE_MESSAGE, R.string.branch_help_camera_permission_failed_title)
            putExtra(GenericResultActivity.SUB_MESSAGE, R.string.branch_help_camera_permission_failed_description)
            putExtra(GenericResultActivity.TOP_BUTTON_MESSAGE, R.string.branch_help_camera_open_settings)
            putExtra(GenericResultActivity.BOTTOM_BUTTON_MESSAGE, R.string.ok)
        }

        GenericResultActivity.bottomOnClickListener = View.OnClickListener {
            BMBApplication.getInstance().topMostActivity.finish()
        }

        GenericResultActivity.topOnClickListener = View.OnClickListener {
            BMBApplication.getInstance().topMostActivity.finish()
            justReturnedFromSettings = true
            launchSettingsActivity()
        }

        GenericResultScreenFragment.setPrimaryButtonOnClick { onBackPressed() }

        startActivity(intent)
    }

    private fun launchSettingsActivity() {
        val uri = Uri.parse("package:$packageName")
        val settingsIntent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, uri)
        startActivity(settingsIntent)
    }
}