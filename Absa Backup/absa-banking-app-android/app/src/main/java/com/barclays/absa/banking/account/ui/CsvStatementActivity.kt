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
import androidx.lifecycle.Observer
import com.barclays.absa.banking.R
import com.barclays.absa.banking.account.ui.StatementUtils.CSV_STATEMENTS
import com.barclays.absa.banking.boundary.model.AccountObject
import com.barclays.absa.banking.express.statements.export.ExportStatementViewModel
import com.barclays.absa.banking.framework.app.BMBApplication
import com.barclays.absa.banking.presentation.shared.IntentFactory
import com.barclays.absa.banking.presentation.shared.bottomSheet.BottomSheet
import com.barclays.absa.utils.PermissionHelper
import com.barclays.absa.utils.fileUtils.GenericStatementsViewModel
import com.barclays.absa.utils.fileUtils.ScopedStorageFileUtils
import com.barclays.absa.utils.viewModel
import kotlinx.android.synthetic.main.choose_dates_activity.*

class CsvStatementActivity : ChooseDatesActivity(), StatementContract.CsvView {
    private lateinit var presenter: StatementContract.CsvPresenter
    private lateinit var genericStatementsViewModel: GenericStatementsViewModel
    private lateinit var accountObject: AccountObject
    private lateinit var csvContent: String

    private var downloadType = StatementDownloadType.Download
    private var featureTag = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        presenter = CsvStatementPresenter(this)

        genericStatementsViewModel = viewModel()

        accountObject = intent.getSerializableExtra(IntentFactory.ACCOUNT_OBJECT) as AccountObject
        featureTag = intent.getStringExtra(IntentFactory.FEATURE_NAME) ?: ""

        downloadButton.setText(R.string.download)
        downloadButton.setOnClickListener {
            if (ScopedStorageFileUtils.determineIfFileCouldBeOpened("text/csv")) {
                exportCsv()
                tagStatementsEvent("AccountHub_ExportTransactionsCSV_DoneSelected")
            } else {
                startActivity(IntentFactory.getNoApplicationToOpenFileError(this, R.string.no_app_can_perform_this_action, getString(R.string.please_install_csv)).build())
            }
        }
    }

    override fun showCsv(data: String) {
        this.csvContent = data
        showMore()
    }

    private fun showMore() {
        if (StatementUtils.canOpenCSV(this)) {
            BottomSheet.Builder(this, R.style.BottomSheet_Dialog)
                    .title(getString(R.string.csv_statement))
                    .sheet(R.menu.menu_csv_statement)
                    .listener { menuItem ->
                        when (menuItem.itemId) {
                            R.id.item_send -> {
                                tagStatementsEvent("AccountHub_ExportTransactionsCSV_SendSelected")
                                downloadType = StatementDownloadType.Share
                                shareCSVFile()
                            }
                            R.id.item_view -> {
                                tagStatementsEvent("AccountHub_ExportTransactionsCSV_ViewSelected")
                                downloadType = StatementDownloadType.Download
                                StatementUtils.viewCsv(this, fromDate(), toDate())
                            }
                        }
                        false
                    }.show()
        } else {
            shareCSVFile()
        }
    }

    private fun shareCSVFile() {
        StatementUtils.sendCsv(this, fromDate(), toDate())
    }

    private fun exportCsv() {
        val exportStatementViewModel = ExportStatementViewModel()
        exportStatementViewModel.exportStatement("CSV", fromDate(), toDate(), accountObject.accountNumber)

        exportStatementViewModel.exportStatementLiveData.observe(this, { export ->
            dismissProgressDialog()
            showCsv(export.fileContent)
        })
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        if (requestCode == PermissionHelper.PermissionCode.WRITE_TO_EXTERNAL_STORAGE.value) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                showCsv(csvContent)
            }
        }
    }

    override fun createCSV(): StringBuilder {
        return StringBuilder(String.format(BMBApplication.getApplicationLocale(), "%s, %s, %s, %s", getString(R.string.listviewDate), getString(R.string.listviewDesc), getString(R.string.listviewAmount), getString(R.string.balance)))
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK && requestCode == CSV_STATEMENTS) {
            val uri = data?.data ?: Uri.EMPTY

            if (::csvContent.isInitialized) {
                genericStatementsViewModel.writeCsvFileContent(uri, csvContent).observe(this, Observer {
                    if (downloadType == StatementDownloadType.Download) {
                        with(Intent(Intent.ACTION_VIEW)) {
                            this.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
                            this.setDataAndType(it, "text/csv")
                            startActivityIfAvailable(this)
                        }
                    } else {
                        with(Intent(Intent.ACTION_SEND)) {
                            type = "text/csv"
                            putExtra(Intent.EXTRA_SUBJECT, "${fromDate()} ${toDate()}")
                            putExtra(Intent.EXTRA_STREAM, uri)
                            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                            startActivityIfAvailable(this)
                        }
                    }
                })
            }
        }
    }

    private fun fromDate() = statementDialogUtils.fromDateString
    private fun toDate() = statementDialogUtils.toDateString

    private enum class StatementDownloadType {
        Download,
        Share
    }
}