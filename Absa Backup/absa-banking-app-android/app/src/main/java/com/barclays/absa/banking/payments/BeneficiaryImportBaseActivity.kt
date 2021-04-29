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

import android.app.Activity
import android.content.Intent
import androidx.lifecycle.LifecycleCoroutineScope
import androidx.lifecycle.lifecycleScope
import com.barclays.absa.banking.beneficiaries.ui.beneficiarySwitching.dto.OcrResponse
import com.barclays.absa.banking.framework.BaseActivity
import com.barclays.absa.banking.framework.BaseView

open class BeneficiaryImportBaseActivity : BaseActivity(), BeneficiaryImportView {

    companion object {
        const val ocrResult = "ocr_result"
        const val FAILURE_SCREEN = 15001
        const val EXTRA_CAMERA_IMPORT = "cameraImport"
    }

    override fun sendOcrResult(ocrResponse: OcrResponse?) {
        dismissProgressDialog()
        val dataIntent = Intent()
        dataIntent.putExtra(ocrResult, ocrResponse)
        setResult(Activity.RESULT_OK, dataIntent)
        finish()
    }

    override fun close() {
        onBackPressed()
    }

    override fun showPhotoError(isCameraImport: Boolean) {
        val intent = Intent(this, BeneficiaryImportFailureActivity::class.java)
        intent.putExtra(EXTRA_CAMERA_IMPORT, isCameraImport)
        startActivityForResult(intent, FAILURE_SCREEN)
        finish()
    }

    override fun getLifecycleCoroutineScope() = lifecycleScope
}

interface BeneficiaryImportView : BaseView {
    fun sendOcrResult(ocrResponse: OcrResponse?)
    fun close()
    fun showPhotoError(isCameraImport: Boolean)
    fun getLifecycleCoroutineScope(): LifecycleCoroutineScope
}