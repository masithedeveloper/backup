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

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Base64
import androidx.activity.viewModels
import com.barclays.absa.banking.R
import com.barclays.absa.banking.account.ui.StatementUtils.STAMPED_STATEMENTS
import com.barclays.absa.banking.express.statements.StatementsViewModel
import com.barclays.absa.banking.express.statements.dto.DownloadStatementRequest
import com.barclays.absa.banking.express.statements.dto.DownloadStatementResponse
import com.barclays.absa.banking.presentation.shared.IntentFactory
import com.barclays.absa.utils.PermissionHelper
import com.barclays.absa.utils.ProfileManager
import com.barclays.absa.utils.fileUtils.GenericStatementsViewModel
import com.barclays.absa.utils.fileUtils.ScopedStorageFileUtils
import com.barclays.absa.utils.viewModel
import kotlinx.android.synthetic.main.choose_dates_activity.*

class StampedStatementActivity : ChooseDatesActivity(), StatementContract.View {
    private lateinit var statementsViewModel: GenericStatementsViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        downloadButton.setText(R.string.download)
        downloadButton.setOnClickListener { showDisclaimer() }
    }

    override fun showPdf(byteArray: ByteArray) {
        statementsViewModel = viewModel()

        pdf = byteArray
        val userProfile = ProfileManager.getInstance().activeUserProfile
        if (userProfile == null) {
            StatementUtils.viewPdf(this, byteArray)
        } else {
            val filePrefix = "${userProfile.customerName}_${fromDate()}_${toDate()}_stamped"
            StatementUtils.viewPdf(this, filePrefix, byteArray)
        }
    }

    override fun showDisclaimer() {
        tagStatementsEvent("AccountHub_DownloadStampedStatement_DoneSelected")
        statementDialogUtils.showDisclaimerDialog(object : StatementDialogUtils.CallBack {
            override fun proceed() {
                if (ScopedStorageFileUtils.determineIfFileCouldBeOpened("application/pdf")) {
                    updateFromAndToDate()
                    requestStampedPdfData()
                    tagStatementsEvent("AccountHub_DownloadStampedStatementDisclaimer_DownloadSelected")
                } else {
                    startActivity(IntentFactory.getNoApplicationToOpenFileError(this@StampedStatementActivity, R.string.no_app_can_perform_this_action, getString(R.string.please_install_pdf)).build())
                }
            }

            override fun cancel() {
                tagStatementsEvent("AccountHub_DownloadStampedStatementDisclaimer_NoThanksSelected")
            }
        })
    }

    private fun requestStampedPdfData() {
        val viewModel: StatementsViewModel by viewModels()
        val request = DownloadStatementRequest(accountNumber, fromDate(), toDate(), true)
        viewModel.downloadStatement(request)
        viewModel.downloadStatementLiveData.observe(this, { downloadStatementResponse: DownloadStatementResponse? ->
            showPdf(Base64.decode(downloadStatementResponse?.fileContent, Base64.DEFAULT))
            dismissProgressDialog()
            viewModel.downloadStatementLiveData.removeObservers(this)
        })
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        if (requestCode == PermissionHelper.PermissionCode.WRITE_TO_EXTERNAL_STORAGE.value) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                showPdf(pdf)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK && requestCode == STAMPED_STATEMENTS) {
            val uri = data?.data ?: Uri.EMPTY

            statementsViewModel.writePdfFileContent(uri, pdf).observe(this, {
                with(Intent(Intent.ACTION_VIEW)) {
                    this.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
                    this.setDataAndType(it, "application/pdf")
                    startActivityIfAvailable(this)
                }
            })
        }
    }

    private fun fromDate() = statementDialogUtils.fromDateString
    private fun toDate() = statementDialogUtils.toDateString
}