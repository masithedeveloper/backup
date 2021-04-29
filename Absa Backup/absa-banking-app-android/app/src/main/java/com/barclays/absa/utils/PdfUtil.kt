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
package com.barclays.absa.utils

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import com.barclays.absa.banking.R
import com.barclays.absa.banking.boundary.model.CustomerProfileObject
import com.barclays.absa.banking.express.data.isBusiness
import com.barclays.absa.banking.framework.BaseActivity
import com.barclays.absa.banking.framework.BaseFragment
import com.barclays.absa.banking.framework.app.BMBApplication
import com.barclays.absa.common.GenericPdfViewerActivity
import kotlinx.coroutines.Dispatchers
import java.math.BigInteger
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException

object PdfUtil {
    const val PDF_URL = "PDF_URL"
    const val PDF_EXT = ".pdf"
    const val PDF_FILE_DOWNLOAD = 2020
    const val PDF_MIMETYPE = "application/pdf"

    fun showPDFInApp(baseActivity: BaseActivity, url: String?) {
        if (url.isNullOrEmpty()) {
            return
        }

        baseActivity.startActivity(Intent(baseActivity, GenericPdfViewerActivity::class.java).apply {
            putExtra(PDF_URL, url)
        })
    }

    fun showTermsAndConditionsClientAgreement(baseActivity: BaseActivity, clientTypeGroup: String?) {
        showPDFInApp(baseActivity, getLink(clientTypeGroup))
    }

    fun getLink(clientTypeGroup: String?): String {
        var type = clientTypeGroup ?: CustomerProfileObject.instance.clientTypeGroup

        if (type.isBusiness()) {
            type = "B"
        }

        return "https://ib.absa.co.za/absa-online/reportUnAuth/clientAgreement/docfusion.pdf?clientAgreementIn=%s&language=%s&forceDownload=false".format(type, BMBApplication.getApplicationLocale())
    }

    fun String.hash(): String {
        val messageDigest: MessageDigest = try {
            MessageDigest.getInstance("MD5")
        } catch (e: NoSuchAlgorithmException) {
            try {
                MessageDigest.getInstance("SHA-256")
            } catch (e: NoSuchAlgorithmException) {
                return this.hashCode().toString()
            }
        }

        messageDigest.update(this.toByteArray(), 0, this.length)
        return BigInteger(1, messageDigest.digest()).toString(16)
    }

    fun showCutOffTimesPdf(baseActivity: BaseActivity) =
        showPDFInApp(baseActivity, "https://ib.absa.co.za/absa-online/assets/Assets/Richmedia/PDF/Online/PaymenyCutOffTime_en.pdf")

    fun openPdfFileIntent(activity: BaseActivity, fileNamePrefix: String? = null) {
        val intent = preparePdfIntent(fileNamePrefix)
        activity.startActivityForResult(intent, PDF_FILE_DOWNLOAD)
    }

    fun openPdfFileIntent(baseFragment: BaseFragment, fileNamePrefix: String? = null) {
        val intent = preparePdfIntent(fileNamePrefix)
        baseFragment.startActivityForResult(intent, PDF_FILE_DOWNLOAD)
    }

    private fun preparePdfIntent(fileNamePrefix: String?): Intent {
        val pdfFileName = if (fileNamePrefix.isNullOrBlank()) {
            "${System.currentTimeMillis()}$PDF_EXT"
        } else {
            "$fileNamePrefix$PDF_EXT"
        }

        return Intent(Intent.ACTION_CREATE_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = PDF_MIMETYPE
            putExtra(Intent.EXTRA_TITLE, pdfFileName)
        }
    }

    fun launchPdfViewer(activity: BaseActivity, uri: Uri): Boolean {
        val intent = Intent(Intent.ACTION_VIEW)
        intent.setDataAndType(uri, PDF_MIMETYPE)
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        return if (activity.isIntentAvailable(intent)) {
            activity.startActivityIfAvailable(intent)
            true
        } else {
            activity.toastShort(R.string.please_install_pdf)
            false
        }
    }

    fun savePdfFile(context: Context, fileUri: Uri, fileByteArray: ByteArray): LiveData<Uri?> =
        liveData(Dispatchers.IO) {
            context.contentResolver.openOutputStream(fileUri, "w").use { outputStream ->
                outputStream?.write(fileByteArray)
                outputStream?.close()
            }

            emit(fileUri)
        }
}