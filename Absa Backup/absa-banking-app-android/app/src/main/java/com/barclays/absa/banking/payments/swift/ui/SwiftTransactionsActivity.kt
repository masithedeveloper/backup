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
package com.barclays.absa.banking.payments.swift.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import com.barclays.absa.banking.R
import com.barclays.absa.banking.framework.BaseActivity
import com.barclays.absa.banking.framework.app.BMBApplication
import com.barclays.absa.banking.framework.app.BMBConstants
import com.barclays.absa.banking.framework.utils.TelephoneUtil
import com.barclays.absa.banking.newToBank.services.dto.ResultAnimations
import com.barclays.absa.banking.payments.swift.services.response.dto.SwiftTransaction
import com.barclays.absa.banking.payments.swift.services.response.dto.SwiftTransactionClaimed
import com.barclays.absa.banking.payments.swift.services.response.dto.SwiftTransactionPending
import com.barclays.absa.banking.presentation.genericResult.GenericResultActivity
import com.barclays.absa.utils.AnalyticsUtil
import com.barclays.absa.utils.DateUtils
import kotlinx.android.synthetic.main.swift_transactions_activity.*
import java.util.*

class SwiftTransactionsActivity : BaseActivity(R.layout.swift_transactions_activity) {

    companion object {
        const val TRANSACTION_DETAIL_KEY = "TransactionDetail"
        const val SWIFT = "Swift"
    }

    private val viewModel = SwiftTransactionsViewModel()
    private lateinit var selectedTransaction: SwiftTransaction
    private lateinit var accessAccountNumber: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setToolBarBack(getString(R.string.swift_transactions_english_only))

        setupObservers()
        setListeners()

        viewModel.fetchTransactionsList()
    }

    override fun onDestroy() {
        viewModel.apply {
            swiftTransactionsListMutableLiveData.removeObservers(this@SwiftTransactionsActivity)
            swiftTransactionDetailsMutableLiveData.removeObservers(this@SwiftTransactionsActivity)
            swiftTransactionHistoryMutableLiveData.removeObservers(this@SwiftTransactionsActivity)
            swiftValidateHolidaysAndTimeMutableLiveData.removeObservers(this@SwiftTransactionsActivity)
        }
        super.onDestroy()
    }

    private fun setupObservers() {
        viewModel.swiftValidateHolidaysAndTimeMutableLiveData.observe(this, {
            it.responseDTO?.let { swift ->
                if (swift.allowSwift) {
                    viewModel.fetchTransactionDetails((selectedTransaction as SwiftTransactionPending).caseIDNumber)
                } else {
                    dismissProgressDialog()
                    startActivity(buildOperatingHoursValidationIntent())
                }
            }
        })

        viewModel.swiftTransactionsListMutableLiveData.observe(this, {
            initTransactionsAdapter(it.swiftTransactions)
            viewModel.fetchAccessAccount()
        })

        viewModel.swiftLinkedAndUnlinkedAccounts.observe(this, { linkedUnlinkedAccounts ->
            accessAccountNumber = linkedUnlinkedAccounts.accountList?.find { manageAccounts -> manageAccounts.accessAccount == true }?.accountNumber ?: ""
            if (accessAccountNumber.isNotEmpty()) {
                viewModel.fetchTransactionHistory(accessAccountNumber)
            } else {
                dismissProgressDialog()
            }
        })

        viewModel.swiftTransactionHistoryMutableLiveData.observe(this, {
            dismissProgressDialog()
            initTransactionHistoryAdapter(it.receiptsIFTHistoryLine)
        })

        viewModel.swiftTransactionDetailsMutableLiveData.observe(this, {
            dismissProgressDialog()
            if (BMBConstants.FAILURE.equals(it.transactionStatus, true) && "Transaction is already processed".equals(it.transactionMessage, true)) {
                navigateAlreadyProcessedScreen()
            } else {
                startActivity(Intent(this, SwiftPaymentsActivity::class.java).putExtra(TRANSACTION_DETAIL_KEY, it.swiftTransactions.first()))
            }
        })
    }

    private fun setListeners() {
        swiftShareDetailsOptionActionButtonView.setOnClickListener {
            startActivity(Intent(this, SwiftShareAccountDetailsActivity::class.java))
        }
    }

    private fun initTransactionsAdapter(transactions: List<SwiftTransactionPending>) {
        swiftIncomingPaymentsRecyclerView.apply {
            adapter = SwiftTransactionsAdapter(object : SwiftTransactionsInterface {
                override fun onTransactionSelected(swiftTransaction: SwiftTransaction) {
                    AnalyticsUtil.trackAction(SWIFT, "Swift_TransactionsScreen_SwiftTransactionClicked")
                    selectedTransaction = swiftTransaction
                    viewModel.validateForHolidaysAndTime()
                }
            }, transactions)
            setHasFixedSize(true)
        }

        if (transactions.isNotEmpty()) {
            swiftIncomingPaymentsRecyclerView.visibility = View.VISIBLE
            swiftEmptyStateAnimationView.visibility = View.GONE
            swiftNoIncomingPaymentsTextView.visibility = View.GONE
        } else {
            swiftIncomingPaymentsRecyclerView.visibility = View.GONE
            swiftEmptyStateAnimationView.visibility = View.VISIBLE
            swiftNoIncomingPaymentsTextView.visibility = View.VISIBLE
            AnalyticsUtil.trackAction(SWIFT, "Swift_TransactionsScreen_NoIncomingPayments")
        }
    }

    private fun initTransactionHistoryAdapter(transactionHistory: List<SwiftTransactionClaimed>) {
        if (transactionHistory.isNotEmpty()) {
            dividerView.visibility = View.VISIBLE
            swiftOlderPaymentLabelTextView.visibility = View.VISIBLE
            swiftOlderPaymentRecyclerView.apply {
                visibility = View.VISIBLE
                adapter = SwiftTransactionsAdapter(null, transactionHistory)
                setHasFixedSize(true)
            }
        }
    }

    private fun buildOperatingHoursValidationIntent(): Intent? {
        GenericResultActivity.bottomOnClickListener = View.OnClickListener {
            AnalyticsUtil.trackAction(SWIFT, "Swift_NonOperatingHoursScreen_OkButtonClicked")
            BMBApplication.getInstance().topMostActivity.finish()
        }
        return Intent(this, GenericResultActivity::class.java).apply {
            putExtra(GenericResultActivity.BOTTOM_BUTTON_MESSAGE, R.string.ok)
            putExtra(GenericResultActivity.NOTICE_MESSAGE, R.string.swift_please_note)
            putExtra(GenericResultActivity.SUB_MESSAGE, R.string.swift_operating_hours)
            putExtra(GenericResultActivity.IS_GENERAL_ALERT, true)
        }
    }

    private fun navigateAlreadyProcessedScreen() {
        val reference = (selectedTransaction as SwiftTransactionPending).caseIDNumber
        val dateTime = getDisplayDateTime()
        GenericResultActivity.topOnClickListener = View.OnClickListener { TelephoneUtil.call(this, TelephoneUtil.FRAUD_NUMBER) }
        GenericResultActivity.bottomOnClickListener = View.OnClickListener {
            AnalyticsUtil.trackAction(SWIFT, "Swift_AlreadyProcessedScreen_DoneButtonClicked")
            BMBApplication.getInstance().topMostActivity.finish()
            viewModel.fetchTransactionsList()
        }
        val subMessage = getString(R.string.swift_payment_already_processed_message).format(dateTime[0], dateTime[1], reference)
        val intent = Intent(this, GenericResultActivity::class.java).apply {
            putExtra(GenericResultActivity.CUSTOM_FAILURE_ANIMATION, ResultAnimations.generalAlert)
            putExtra(GenericResultActivity.NOTICE_MESSAGE, R.string.swift_payment_already_processed)
            putExtra(GenericResultActivity.SUB_MESSAGE_STRING, subMessage)
            putExtra(GenericResultActivity.TOP_BUTTON_MESSAGE, R.string.swift_fraud_hotline)
            putExtra(GenericResultActivity.BOTTOM_BUTTON_MESSAGE, R.string.swift_done)
        }
        startActivity(intent)
    }

    private fun getDisplayDateTime(): List<String> {
        val date = Calendar.getInstance().time
        val displayDate = DateUtils.format(date, DateUtils.DATE_DISPLAY_PATTERN, Locale.ENGLISH)
        val displayTime = DateUtils.format(date, "HH:mm:ss")
        return listOf(displayDate, displayTime)
    }
}