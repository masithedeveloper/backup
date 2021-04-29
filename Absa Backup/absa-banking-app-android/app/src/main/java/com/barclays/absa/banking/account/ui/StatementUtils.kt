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
package com.barclays.absa.banking.account.ui

import android.content.Intent
import com.barclays.absa.banking.R
import com.barclays.absa.banking.framework.BaseActivity
import com.barclays.absa.banking.presentation.shared.IntentFactory
import com.barclays.absa.utils.PermissionHelper
import com.barclays.absa.utils.fileUtils.FileUtilsInterface
import com.barclays.absa.utils.fileUtils.ScopedStorageFileUtils

object StatementUtils : FileUtilsInterface {
    const val STAMPED_STATEMENTS = 1008
    const val ARCHIVED_STATEMENTS = 1009
    const val CSV_STATEMENTS = 1010
    const val TAX_CERTIFICATES = 1011

    override fun openSaveStampedStatementIntent(activity: BaseActivity, fileName: String, fileByteArray: ByteArray) {
        activity.startActivityForResult(getPDFIntent(fileName, activity), STAMPED_STATEMENTS)
    }

    override fun openSaveArchivedStatementIntent(activity: BaseActivity, fileName: String, fileByteArray: ByteArray) {
        activity.startActivityForResult(getPDFIntent(fileName, activity), ARCHIVED_STATEMENTS)
    }

    override fun openSaveCSVStatementIntent(activity: BaseActivity, fileName: String) {
        val intent = Intent(Intent.ACTION_CREATE_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "text/csv"
            putExtra(Intent.EXTRA_TITLE, fileName)
        }
        activity.startActivityForResult(intent, CSV_STATEMENTS)
    }

    fun viewPdf(activity: BaseActivity, pdf: ByteArray) {
        viewPdf(activity, "" + System.currentTimeMillis(), pdf)
    }

    fun viewPdf(activity: BaseActivity, filePrefix: String, pdf: ByteArray) {
        PermissionHelper.requestExternalStorageWritePermission(activity) {
            if (activity is ArchivedStatementsActivity) {
                openSaveArchivedStatementIntent(activity, "$filePrefix.pdf", pdf)
            } else {
                openSaveStampedStatementIntent(activity, "$filePrefix.pdf", pdf)
            }
        }
    }

    fun viewCsv(activity: BaseActivity, fromDate: String, toDate: String) {
        handleCsv(activity, fromDate, toDate)
    }

    fun sendCsv(activity: BaseActivity, fromDate: String, toDate: String) {
        handleCsv(activity, fromDate, toDate)
    }

    private fun handleCsv(activity: BaseActivity, fromDate: String, toDate: String) {
        PermissionHelper.requestExternalStorageWritePermission(activity) {
            openSaveCSVStatementIntent(activity, fromDate + "_" + toDate + ".csv")
        }
    }

    fun getPDFIntent(fileName: String, activity: BaseActivity): Intent {
        val pdfIntent = Intent(Intent.ACTION_CREATE_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "application/pdf"
            putExtra(Intent.EXTRA_TITLE, fileName)
        }

        return if (ScopedStorageFileUtils.determineIfFileCouldBeOpened("application/pdf")) pdfIntent else IntentFactory.getFailureResultScreenBuilder(activity, R.string.no_app_can_perform_this_action, activity.getString(R.string.please_install_pdf)).build()
    }

    fun canOpenCSV(activity: BaseActivity): Boolean {
        val csvIntent = Intent(Intent.ACTION_VIEW)
        csvIntent.type = "text/csv"
        return csvIntent.resolveActivity(activity.packageManager) != null
    }
}