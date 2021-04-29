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
import android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.barclays.absa.banking.R
import com.barclays.absa.banking.account.services.dto.ArchivedStatementListResponse
import com.barclays.absa.banking.account.services.dto.StatementListItem
import com.barclays.absa.banking.account.ui.StatementContract.ArchivedPresenter
import com.barclays.absa.banking.account.ui.StatementContract.ArchivedView
import com.barclays.absa.banking.account.ui.StatementDialogUtils.CallBack
import com.barclays.absa.banking.account.ui.StatementUtils.ARCHIVED_STATEMENTS
import com.barclays.absa.banking.account.ui.StatementUtils.viewPdf
import com.barclays.absa.banking.boundary.model.AccountObject
import com.barclays.absa.banking.framework.BaseActivity
import com.barclays.absa.banking.framework.analytics.AnalyticsUtils
import com.barclays.absa.banking.framework.app.BMBConstants
import com.barclays.absa.banking.framework.utils.BMBLogger
import com.barclays.absa.banking.home.ui.AccountTypes
import com.barclays.absa.banking.presentation.shared.IntentFactory
import com.barclays.absa.utils.AnalyticsUtil.trackAction
import com.barclays.absa.utils.DateUtils
import com.barclays.absa.utils.PermissionHelper
import com.barclays.absa.utils.ProfileManager
import com.barclays.absa.utils.fileUtils.GenericStatementsViewModel
import com.barclays.absa.utils.fileUtils.ScopedStorageFileUtils
import com.barclays.absa.utils.viewModel
import kotlinx.android.synthetic.main.activity_archived_statement.*
import kotlinx.android.synthetic.main.archived_statement_list_item.view.*
import java.util.*

class ArchivedStatementsActivity : BaseActivity(R.layout.activity_archived_statement), ArchivedView {
    private lateinit var presenter: ArchivedPresenter
    private lateinit var statementsViewModel: GenericStatementsViewModel
    private lateinit var statementDialogUtils: StatementDialogUtils
    private lateinit var accountNumber: String
    private lateinit var accountObject: AccountObject
    private lateinit var pdfByteArray: ByteArray

    private var selectedKey: String? = null
    private var featureTag = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setToolBarBack(R.string.historical_statement)

        fromButton.setOnClickListener {
            statementDialogUtils.setChoosingFromDate(true)
            showDatePicker()
        }
        toButton.setOnClickListener {
            statementDialogUtils.setChoosingFromDate(false)
            showDatePicker()
        }

        statementsViewModel = viewModel()

        intent.extras?.let {
            accountObject = it.getSerializable(IntentFactory.ACCOUNT_OBJECT) as? AccountObject ?: AccountObject()
            accountNumber = accountObject.accountNumber
            featureTag = it.getString(IntentFactory.FEATURE_NAME, "")
        }

        val calendar = GregorianCalendar()
        presenter = ArchivedStatementPresenter(this)
        statementDialogUtils = StatementDialogUtils.newInstanceArchived(this)

        var toDate = calendar.time
        calendar.add(Calendar.MONTH, -6)
        var fromDate = calendar.time

        intent.extras?.apply {
            if (getSerializable(IntentFactory.FROM_DATE) != null && getSerializable(IntentFactory.TO_DATE) != null) {
                fromDate = getSerializable(IntentFactory.FROM_DATE) as Date
                toDate = getSerializable(IntentFactory.TO_DATE) as Date
            }
        }

        statementDialogUtils.toDate = toDate
        statementDialogUtils.fromDate = fromDate
        setFromAndToDateViews()
        requestList()
    }

    fun setFromAndToDateViews() {
        val fromDate = statementDialogUtils.fromDate
        val toDate = statementDialogUtils.toDate

        val fromDateText = "${getString(R.string.from_date)}\n${DateUtils.format(fromDate, StatementDialogUtils.DAY_DATE_FORMAT)}"
        fromButton.text = fromDateText

        val toDateText = "${getString(R.string.to_date)}\n${DateUtils.format(toDate, StatementDialogUtils.DAY_DATE_FORMAT)}"
        toButton.text = toDateText
    }

    override fun onResume() {
        super.onResume()
        AnalyticsUtils.getInstance().trackCustomScreenView(BMBConstants.ANALYTICS_SCREEN_NAME, AccountActivity.ANALYTIC_SCREEN_NAME, BMBConstants.TRUE_CONST)
    }

    override fun showList(data: List<StatementListItem>?) {
        BMBLogger.d(javaClass.simpleName, "now in showList services is ${data ?: emptyList()}")
        if (!data.isNullOrEmpty()) {
            BMBLogger.d(javaClass.simpleName, "now in showList services is " + data.size)
        }
        animate(archivedStatementRecyclerView, R.anim.expand_linear)
        archivedStatementRecyclerView.adapter = ArchiveAdapter(data ?: emptyList())
        noStatementsConstraintLayout.visibility = View.GONE
    }

    override fun handleArchivedStatementError(archivedStatementListResponse: ArchivedStatementListResponse) {
        if (getString(R.string.no_statements_service_response).equals(archivedStatementListResponse.transactionMessage, ignoreCase = true) || archivedStatementListResponse.transactionMessage.contains("Invalid account number", ignoreCase = true)) {
            noStatementsConstraintLayout.visibility = View.VISIBLE
        } else {
            showMessageError(archivedStatementListResponse.responseMessage)
        }
    }

    override fun showPdf(pdfByteArray: ByteArray) {
        this.pdfByteArray = pdfByteArray

        val userProfile = ProfileManager.getInstance().activeUserProfile
        if (userProfile == null) {
            viewPdf(this, pdfByteArray)
        } else {
            val customerName = userProfile.customerName
            viewPdf(this, customerName + "_" + selectedKey?.replace("|", "_") + "_archived", pdfByteArray)
        }
    }

    private fun showDatePicker() {
        statementDialogUtils.showDatePicker(object : CallBack {
            override fun proceed() {
                animate(fromButton, R.anim.expand_linear)
                animate(toButton, R.anim.expand_linear)
                setFromAndToDateViews()
                requestList()
            }

            override fun cancel() {
                tagStatementsEvent("AccountHub_DownloadBankStatement_DatePickerCancelled")
            }
        })
    }

    override fun showDisclaimer() {
        if (shouldShowDisclaimer()) {
            statementDialogUtils.showDisclaimerDialog(object : CallBack {
                override fun proceed() {
                    checkIfUserCanOpenPdfFileAndRequestPdfData()
                }

                override fun cancel() {
                    tagStatementsEvent("AccountHub_DownloadBankStatement_NoThanksClicked")
                }
            })
        } else {
            checkIfUserCanOpenPdfFileAndRequestPdfData()
        }
    }

    private fun checkIfUserCanOpenPdfFileAndRequestPdfData() {
        if (ScopedStorageFileUtils.determineIfFileCouldBeOpened("application/pdf")) {
            requestPdfData()
            AnalyticsUtils.getInstance().trackCustomScreenView(BMBConstants.ANALYTICS_SCREEN_NAME, "Download Archived Statements PDF Viewer", BMBConstants.TRUE_CONST)
        } else {
            startActivity(IntentFactory.getNoApplicationToOpenFileError(this, R.string.no_app_can_perform_this_action, getString(R.string.please_install_pdf)).build())
        }
    }

    private fun shouldShowDisclaimer(): Boolean {
        val accountType = accountObject.accountType
        return (!AccountTypes.CREDIT_CARD.equals(accountType, ignoreCase = true)
                && !AccountTypes.PERSONAL_LOAN.equals(accountType, ignoreCase = true)
                && !AccountTypes.ABSA_VEHICLE_AND_ASSET_FINANCE.equals(accountType, ignoreCase = true)
                && !AccountTypes.HOME_LOAN.equals(accountType, ignoreCase = true))
    }

    private fun tagStatementsEvent(screenName: String) {
        trackAction(featureTag, featureTag + screenName)
    }

    fun requestList() {
        if (::accountNumber.isInitialized) {
            val fromDate = statementDialogUtils.fromDateString
            val toDate = statementDialogUtils.toDateString
            presenter.fetchList(accountNumber, fromDate, toDate)
        }
    }

    fun requestPdfData() {
        selectedKey.let {
            presenter.fetchPdf(it)
        }
    }

    private inner class ArchiveAdapter(val data: List<StatementListItem>) : RecyclerView.Adapter<ArchiveAdapter.ViewHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val itemView = LayoutInflater.from(parent.context).inflate(R.layout.archived_statement_list_item, parent, false)
            val viewHolder = ViewHolder(itemView)
            itemView.setOnClickListener {
                val position = viewHolder.adapterPosition
                if (0 <= position && position < data.size) {
                    selectedKey = data[position].key
                    showDisclaimer()
                }
            }
            return viewHolder
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            if (data.isNotEmpty()) {
                holder.dateTextView.text = data[position].date
            }
        }

        override fun getItemCount(): Int = data.size

        inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val dateTextView: TextView = view.dateTextView
        }
    }

    override fun onDestroy() {
        statementDialogUtils.destroy()
        super.onDestroy()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        if (requestCode == PermissionHelper.PermissionCode.WRITE_TO_EXTERNAL_STORAGE.value) {
            if (grantResults.isNotEmpty()) {
                when (grantResults[0]) {
                    PackageManager.PERMISSION_GRANTED -> showPdf(pdfByteArray)
                    PackageManager.PERMISSION_DENIED -> {
                    }
                    else -> {
                    }
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK && requestCode == ARCHIVED_STATEMENTS) {
            val uri = data?.data ?: Uri.EMPTY

            statementsViewModel.writePdfFileContent(uri, pdfByteArray).observe(this, {
                with(Intent(Intent.ACTION_VIEW)) {
                    this.flags = FLAG_GRANT_READ_URI_PERMISSION
                    this.setDataAndType(it, "application/pdf")
                    startActivityIfAvailable(this)
                }
            })
        }
    }
}