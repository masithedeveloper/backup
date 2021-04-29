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
package com.barclays.absa.banking.dualAuthorisations.ui

import android.os.Bundle
import android.view.View
import com.barclays.absa.banking.R
import com.barclays.absa.banking.boundary.model.AccessPrivileges.Companion.instance
import com.barclays.absa.banking.boundary.model.authorisations.AuthorisationTransactionDetails
import com.barclays.absa.banking.framework.BaseActivity
import com.barclays.absa.utils.extensions.getAccountDisplayDescriptionWithFormattedAccountNumber
import kotlinx.android.synthetic.main.authorisation_cash_send_details_activity.*
import styleguide.utils.extensions.toFormattedCellphoneNumber

class DualAuthTransactionDetailsCashSendActivity : BaseActivity(R.layout.authorisation_cash_send_details_activity) {
    private var handler: DualAuthorisationHandler? = null

    companion object {
        const val TRANSACTION_DETAILS = "transactionDetails"
        const val IS_CASH_SEND_PLUS = "isCashSendPlus"
        const val CASH_SEND_PLUS_UPDATE_LIMIT_CODE = "CSPU"
        const val CASH_SEND_PLUS_CANCELLATION_CODE = "CSPD"
    }

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val isCashSendPlus = intent.getBooleanExtra(IS_CASH_SEND_PLUS, false)
        val transactionDetails = intent.getSerializableExtra(TRANSACTION_DETAILS) as AuthorisationTransactionDetails?
        transactionDetails?.let {
            handler = DualAuthorisationHandler(this, transactionDetails)
            if (isCashSendPlus) {
                setToolBarBack(getString(R.string.transaction_overview))
                typeContentView.setContentText(transactionDetails.transactionType)
            } else {
                setToolBarBack(getString(R.string.onceoff_confirm_title))
            }

            when {
                CASH_SEND_PLUS_UPDATE_LIMIT_CODE.equals(transactionDetails.transactionTypeCode, true) -> {
                    mobileNumberContentView.visibility = View.GONE
                    typeContentView.setContentText(transactionDetails.transactionType)
                    amountContentView.setLabelText(getString(R.string.cash_send_plus_prev_limit_amount))
                    fromAccountContentView.setLabelText(getString(R.string.cash_send_plus_new_limit_amount))
                    transactionDetails.cashSendPlusLimitsData?.let {
                        amountContentView.setContentText(it.cashSendPlusLimitAmtPrev)
                        fromAccountContentView.setContentText(it.cashSendPlusLimitAmt)
                    }
                }
                CASH_SEND_PLUS_CANCELLATION_CODE.equals(transactionDetails.transactionTypeCode, true) -> {
                    mobileNumberContentView.visibility = View.GONE
                    amountContentView.visibility = View.GONE
                    fromAccountContentView.visibility = View.GONE
                    accountDividerView.visibility = View.GONE
                    typeContentView.setContentText(transactionDetails.transactionType)
                }
                else -> {
                    mobileNumberContentView.setContentText(transactionDetails.cellNumber.toFormattedCellphoneNumber())
                    amountContentView.setContentText(transactionDetails.getTransactionAmount().toString())
                    val accountDescription = transactionDetails.fromAccount.getAccountDisplayDescriptionWithFormattedAccountNumber()
                    fromAccountContentView.setContentText(accountDescription)
                }
            }

            initiatedByContentView.setContentText(transactionDetails.operatorName)
            initiatedOnContentView.setContentText(handler?.debitDate)
            if (instance.isOperator) {
                cancelButton.visibility = View.VISIBLE
                rejectButton.visibility = View.GONE
                approveButton.visibility = View.GONE
            }
            approveButton.setOnClickListener { v: View? -> handler?.onAuthoriseClick(v) }
            rejectButton.setOnClickListener { v: View? -> handler?.onRejectionClick(v) }
            cancelButton.setOnClickListener { v: View? -> handler?.onCancelClick(v) }
        } ?: run {
            toastLong(R.string.generic_error)
            finish()
        }
    }

    override fun onBackPressed() {
        handler?.onBackPressed()
    }
}