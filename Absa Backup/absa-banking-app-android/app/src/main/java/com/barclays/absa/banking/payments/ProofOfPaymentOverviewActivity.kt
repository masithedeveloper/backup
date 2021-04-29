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
package com.barclays.absa.banking.payments

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.style.ClickableSpan
import android.view.View
import com.barclays.absa.banking.R
import com.barclays.absa.banking.beneficiaries.ui.BeneficiaryLandingActivity
import com.barclays.absa.banking.boundary.model.ResendNoticeOfPayment
import com.barclays.absa.banking.boundary.model.ViewTransactionDetails
import com.barclays.absa.banking.framework.BaseActivity
import com.barclays.absa.banking.framework.ExtendedResponseListener
import com.barclays.absa.banking.framework.app.BMBConstants.*
import com.barclays.absa.banking.payments.services.PaymentsInteractor
import com.barclays.absa.banking.presentation.shared.IntentFactoryGenericResult
import com.barclays.absa.utils.CommonUtils
import kotlinx.android.synthetic.main.proof_of_payment_overview_activity.*
import styleguide.utils.extensions.toFormattedCellphoneNumber
import java.util.*

class ProofOfPaymentOverviewActivity : BaseActivity() {

    private val resendNoticeOfPaymentResponseListener = object : ExtendedResponseListener<ResendNoticeOfPayment>() {

        override fun onSuccess(resendNoticeOfPayment: ResendNoticeOfPayment) {
            dismissProgressDialog()
            launchResultActivity(SUCCESS.equals(resendNoticeOfPayment.status, ignoreCase = true))
        }
    }

    private fun launchResultActivity(state: Boolean) {
        val currentTime = Calendar.getInstance().time
        val intentBuilder = if (state) {
            IntentFactoryGenericResult.getSuccessfulResultBuilder(this)
                    .setGenericResultHeaderMessage(getString(R.string.notification_resend_success_header))
                    .setGenericResultSubMessage(getString(R.string.notification_resend_success_detail, currentTime))
        } else {
            IntentFactoryGenericResult.getFailureResultBuilder(this)
                    .setGenericResultHeaderMessage(getString(R.string.notification_resend_failure_header))
                    .setGenericResultSubMessage(getString(R.string.notification_resend_failure_detail, currentTime))
        }
        intentBuilder.setGenericResultDoneButton(this, { loadAccountsAndGoHome() }, state)

        startActivityIfAvailable(intentBuilder.build())
    }

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.proof_of_payment_overview_activity)
        setToolBarBack(R.string.notice_of_payment_overview)

        resendNoticeOfPaymentResponseListener.setView(this)

        val viewTransactionDetails = intent.getSerializableExtra(BeneficiaryLandingActivity::class.java.name) as? ViewTransactionDetails
        viewTransactionDetails?.let { initViews(it) }

        val bundle = intent.getBundleExtra(ProofOfPaymentHistoryActivity.PAYMENT_DETAILS)
        val notificationType = bundle?.getString(ProofOfPaymentHistoryActivity.NOTIFICATION_TYPE) ?: ""
        val notificationDetails = bundle?.getString(ProofOfPaymentHistoryActivity.NOTIFICATION_DETAILS) ?: ""
        val faxNumber = bundle?.getString(ProofOfPaymentHistoryActivity.FAX_NUMBER) ?: ""

        proofOverViewNotificationTypePrimaryContentAndLabelView.apply {
            when {
                NOTICE_TYPE_EMAIL_SHORT.equals(notificationType, ignoreCase = true) -> {
                    setLabelText(getString(R.string.email))
                    setContentText(notificationDetails)
                }
                NOTICE_TYPE_FAX_SHORT.equals(notificationType, ignoreCase = true) -> {
                    setLabelText(getString(R.string.fax_new))
                    setContentText(notificationDetails + faxNumber)
                }
                else -> {
                    setLabelText(getString(R.string.mobile_number))
                    setContentText(notificationDetails.toFormattedCellphoneNumber())
                }
            }
        }

        proofOverviewConfirmSendButton.setOnClickListener {
            PaymentsInteractor().resendProofOfPayment(viewTransactionDetails, bundle, resendNoticeOfPaymentResponseListener)
        }

        mScreenName = RESEND_PROOF_PAYMENT_OVERVIEW
        mSiteSection = MANAGE_BENEFICIARIES_CONST
        trackCustomScreenView(mScreenName, mSiteSection, TRUE_CONST)

        CommonUtils.makeTextClickable(this, R.string.notification_disclaimer_text, "Absa.co.za", object : ClickableSpan() {
            override fun onClick(widget: View) {
                showFeesPageOnWebsite()
            }
        }, proofOverviewDisclaimerTextView)
    }

    private fun initViews(viewTransactionDetails: ViewTransactionDetails) = viewTransactionDetails.let {
        proofOverviewBeneficiaryBankLineItemView.setLineItemViewContent(it.bankName)
        proofOverviewBeneficiaryNamePrimaryContentAndLabelView.setContentText(it.beneficiaryName)
        proofOverviewAccountNumberLineItemView.setLineItemViewContent(it.accountNumber)
        proofOverviewPaymentDateLineItemView.setLineItemViewContent(it.date)
        proofOverviewAmountPrimaryContentAndLabelView.setContentText(it.transactionAmount?.toString())
    }

    private fun showFeesPageOnWebsite() {
        val viewFeesIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.absa.co.za/rates-and-fees/personal-banking/"))
        startActivityIfAvailable(viewFeesIntent)
    }
}