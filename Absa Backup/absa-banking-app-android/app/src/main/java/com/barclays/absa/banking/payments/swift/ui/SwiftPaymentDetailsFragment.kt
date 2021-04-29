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

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.text.style.ClickableSpan
import android.view.View
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import com.barclays.absa.banking.R
import com.barclays.absa.banking.payments.swift.ui.SwiftTransactionsActivity.Companion.SWIFT
import com.barclays.absa.utils.AnalyticsUtil
import com.barclays.absa.utils.DateUtils
import com.barclays.absa.utils.extensions.toRandAmount
import kotlinx.android.synthetic.main.swift_payment_details_fragment.*
import styleguide.utils.extensions.toSpecialFormattedAccountNumber
import styleguide.utils.extensions.toTitleCase
import java.util.*

class SwiftPaymentDetailsFragment : SwiftPaymentsBaseFragment(R.layout.swift_payment_details_fragment) {

    private var drawableUp: Drawable? = null
    private var drawableDown: Drawable? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setToolBar(R.string.swift_payment_details)
        showToolBar()
        swiftPaymentsActivity.setProgressIndicatorVisibility(View.VISIBLE)
        swiftPaymentsActivity.setProgressStep(2)
        drawableDown = ContextCompat.getDrawable(swiftPaymentsActivity, R.drawable.arrow_down)
        drawableUp = ContextCompat.getDrawable(swiftPaymentsActivity, R.drawable.arrow_up)
        setupClickableReserveBankDeclaration()
        acceptTransactionButton.setOnClickListener {
            if (acceptReserveBankDeclarationCheckBox.isChecked) {
                AnalyticsUtil.trackAction(SWIFT, "Swift_PaymentDetailsScreen_AcceptTransactionButtonClicked")
                swiftPaymentViewModel.requestProcessTransaction()
            } else {
                acceptReserveBankDeclarationCheckBox.setErrorMessage(getString(R.string.swift_please_accept_reserve_bank_declaration))
            }
        }
        swiftPaymentViewModel.swiftQuoteResponse.observe(viewLifecycleOwner, Observer { swiftQuoteResponse ->
            dismissProgressDialog()
            val swiftTransactionDetails = swiftPaymentViewModel.swiftTransaction.value ?: return@Observer
            val swiftQuote = swiftQuoteResponse.swiftQuoteDetailsResponse
            amountSentPrimaryContentAndLabelView.setContentText(swiftTransactionDetails.getFormattedSwiftForeignCurrencyAmount())
            amountReceivedPrimaryContentAndLabelView.setContentText(swiftQuote.formattedLocalCurrencyAmount())
            conversionRateTextView.text = getConversionRateDetails()

            val accountDetails = swiftPaymentViewModel.getAccountDetails()
            val formattedAccountNumber = (accountDetails?.accountNumber ?: swiftTransactionDetails.toAccount).toSpecialFormattedAccountNumber()
            val accountDisplayValue = if (accountDetails == null) formattedAccountNumber else "${accountDetails.description} ($formattedAccountNumber)"
            toAccountSecondaryContentAndLabelView.setContentText(accountDisplayValue)

            swiftPaymentViewModel.apply {
                if (hasCharges()) {
                    populateCharges()
                    seeMoreOrLessTextView.setOnClickListener { toggleSeeMore() }
                } else {
                    populateNoCharges()
                }
                populateSenderDetails()
            }
        })
        swiftPaymentViewModel.processTransactionResponse.observe(viewLifecycleOwner, {
            dismissProgressDialog()
            val referenceNumber = swiftPaymentViewModel.swiftTransaction.value?.caseIDNumber ?: ""
            val resultScreenProperties = if (it.transactionStatus.equals("Success", true)) {
                getPaymentProcessingResultScreenProperties(referenceNumber)
            } else {
                val isProcessedTransaction = it.transactionMessage?.contains("already processed", true) ?: false
                if (isProcessedTransaction) {
                    getPaymentProcessedGenericResultScreenProperties(referenceNumber)
                } else {
                    getUnableToProcessTransactionGenericResultScreenProperties(it.transactionMessage ?: "")
                }
            }
            navigate(SwiftPaymentDetailsFragmentDirections.actionSwiftPaymentDetailsFragmentToGenericResultScreenFragment(resultScreenProperties))
        })
    }

    private fun toggleSeeMore() {
        when (showMoreConstraintLayout.visibility) {
            View.VISIBLE -> setSeeMoreView(drawableDown, R.string.swift_show_more, View.GONE)
            View.GONE -> setSeeMoreView(drawableUp, R.string.swift_show_less, View.VISIBLE)
        }
    }

    private fun setSeeMoreView(drawable: Drawable?, @StringRes stringResId: Int, visibility: Int) {
        seeMoreOrLessTextView.apply {
            setCompoundDrawablesWithIntrinsicBounds(drawable, null, null, null)
            text = getString(stringResId)
        }
        showMoreConstraintLayout.visibility = visibility
    }

    private fun populateCharges() {
        swiftPaymentViewModel.apply {
            totalChargesPrimaryContentAndLabelView.setContentText(getTotalCharges().toRandAmount())
            absaChargesSecondaryContentAndLabelView.setContentText(getAbsaCharges().toRandAmount())
            swiftChargesSecondaryContentAndLabelView.setContentText(getSwiftCharges().toRandAmount())
            recoveryChargesSecondaryContentAndLabelView.setContentText(getRecoveryCharges().toRandAmount())
            vatSecondaryContentAndLabelView.setContentText(getVatCharges().toRandAmount())
        }
    }

    private fun populateNoCharges() {
        noChargesTextView.visibility = View.VISIBLE
        chargesIncurredConstraintLayout.visibility = View.GONE
    }

    private fun populateSenderDetails() {
        swiftPaymentViewModel.apply {
            senderDetailsSecondaryContentAndLabelView.setContentText(swiftTransaction.value?.senderFirstName.toTitleCase())
            val senderTypeResourceId = if (senderType == SwiftPaymentsViewModel.SenderType.INDIVIDUAL) R.string.swift_an_individual else R.string.swift_a_company
            senderTypeSecondaryContentAndLabelView.setContentText(getString(senderTypeResourceId))
            reasonForPaymentSecondaryContentAndLabelView.setContentText(selectedLevelOneCategory)
            subCategoryForPaymentSecondaryContentAndLabelView.setContentText(selectedLevelTwoCategory?.subCategoryDescription)
        }
    }

    private fun getConversionRateDetails(): String {
        val swiftTransactionDetails = swiftPaymentViewModel.swiftTransaction.value ?: return ""
        val swiftQuoteDetails = swiftPaymentViewModel.swiftQuoteResponse.value?.swiftQuoteDetailsResponse ?: return ""
        val dateTime = DateUtils.format(Calendar.getInstance().time, DateUtils.DATE_TIME_PATTERN, Locale.ENGLISH)
        val foreignCurrency = "1 ${swiftTransactionDetails.foreignCurrencyCode}"
        val localCurrency = "${swiftQuoteDetails.getFormattedDestinationCurrencyRate()} ${swiftQuoteDetails.localCurrency}"
        return getString(R.string.swift_conversion_rate_text, "$foreignCurrency = $localCurrency", dateTime)
    }

    private fun setupClickableReserveBankDeclaration() {
        acceptReserveBankDeclarationCheckBox.setClickableLinkTitle(R.string.swift_read_and_accepted_reserve_bank_declaration,
                R.string.swift_reserve_bank_declaration, object : ClickableSpan() {
            override fun onClick(widget: View) {
                swiftPaymentsActivity.setProgressIndicatorVisibility(View.GONE)
                navigate(SwiftPaymentDetailsFragmentDirections.actionSwiftPaymentDetailsFragmentToSwiftReserveBankDeclarationFragment())
            }
        }, R.color.graphite)
    }
}