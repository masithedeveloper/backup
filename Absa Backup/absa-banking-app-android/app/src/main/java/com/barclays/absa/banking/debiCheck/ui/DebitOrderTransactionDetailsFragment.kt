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
package com.barclays.absa.banking.debiCheck.ui

import android.content.Context
import android.os.Bundle
import android.view.View
import com.barclays.absa.banking.R
import com.barclays.absa.banking.boundary.model.debitOrder.DebitOrderDetailsResponse
import com.barclays.absa.banking.express.configurationsRetrieval.ConfigurationCategory
import com.barclays.absa.banking.express.configurationsRetrieval.ConfigurationsRetrievalViewModel
import com.barclays.absa.banking.framework.utils.TelephoneUtil
import com.barclays.absa.utils.AnalyticsUtil
import com.barclays.absa.utils.CommonUtils
import com.barclays.absa.utils.TextFormatUtils
import com.barclays.absa.utils.viewModel
import kotlinx.android.synthetic.main.debit_order_transaction_details_fragment.*
import styleguide.utils.extensions.removeCurrencyDefaultZero
import java.text.SimpleDateFormat
import java.util.*

class DebitOrderTransactionDetailsFragment : DebitOrderBaseFragment(R.layout.debit_order_transaction_details_fragment) {

    private lateinit var configurationsRetrievalViewModel: ConfigurationsRetrievalViewModel
    private var disputeLimit: String = ""

    override fun onAttach(context: Context) {
        super.onAttach(context)
        configurationsRetrievalViewModel = viewModel()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setToolBar(R.string.transaction_details)
        showToolBar()

        configurationsRetrievalViewModel.fetchSingleConfigValue(ConfigurationCategory.XOB_CONFIGS.name, "DEBIT_ORDER_MAX_AMOUNT")
        configurationsRetrievalViewModel.configSingleLiveData.observe(viewLifecycleOwner, {
            dismissProgressDialog()
            disputeLimit = it.configDetailValue.configValue

            arguments?.let {
                val bundle = DebitOrderTransactionDetailsFragmentArgs.fromBundle(it)
                initializeViews(bundle.debitOrder, bundle.isStoppedDebitOrder)
            }
        })
    }

    private fun initializeViews(debitOrder: DebitOrderDetailsResponse, isStoppedDebitOrder: Boolean) {
        companyNameContentView.setContentTextStyle(R.style.LargeTextMediumDark)
        amountContentView.setContentTextStyle(R.style.LargeTextMediumDark)
        paymentDateContentView.setContentTextStyle(R.style.LargeTextMediumDark)
        companyNameContentView.setContentText(debitOrder.userReference)

        val inputDate = debitOrder.actionDate
        val inputDateFormat = SimpleDateFormat("yyyy-MM-dd", CommonUtils.getCurrentApplicationLocale())
        val date = inputDateFormat.parse(inputDate) ?: Date()
        val outputDateFormat = SimpleDateFormat("d MMMM yyyy", CommonUtils.getCurrentApplicationLocale())
        val formattedDate = outputDateFormat.format(date)
        paymentDateContentView.setContentText(formattedDate)

        if (!isStoppedDebitOrder) {
            stopDebitOrderButton.visibility = View.VISIBLE
            reversePaymentButton.visibility = View.VISIBLE
            stopDebitOrderButton.setOnClickListener {
                AnalyticsUtil.trackAction("Debit Orders", "DebitOrder_TransactionDetailScreen_StopDebitOrderButtonClicked")
                navigate(DebitOrderTransactionDetailsFragmentDirections.actionDebitOrderTransactionDetailsFragmentToStoppedDebitOrderInformationFragment())
            }
        }
        reversePaymentButton.setOnClickListener {
            AnalyticsUtil.trackAction("Debit Orders", "DebitOrder_TransactionDetailScreen_ReversePaymentButtonClicked")
            navigate(DebitOrderTransactionDetailsFragmentDirections.actionDebitOrderTransactionDetailsFragmentToReverseDebitOrderInformationFragment())
        }

        recordAnalytics(debitOrder.amount.removeCurrencyDefaultZero().toInt())

        if (debitOrder.amount.removeCurrencyDefaultZero() <= disputeLimit.removeCurrencyDefaultZero() && !isStoppedDebitOrder) {
            stopDebitOrderButton.visibility = View.VISIBLE
            reversePaymentButton.visibility = View.VISIBLE
        } else {
            stopDebitOrderButton.visibility = View.GONE
            reversePaymentButton.visibility = View.GONE

            if (isStoppedDebitOrder) {
                debitOrderExceedsLimitTextView.visibility = View.GONE
            } else {
                AnalyticsUtil.trackAction("Debit order", "DebitOrder_TransactionDetailScreen_LimitReached")
                callCenterContactView.visibility = View.VISIBLE
                callCenterContactView.setOnClickListener {
                    TelephoneUtil.call(baseActivity, getString(R.string.support_center_number))
                    AnalyticsUtil.trackAction("Debit order", "DebitOrder_TransactionDetailScreen_CallCenterButtonClicked")
                }

                debitOrderExceedsLimitTextView.visibility = View.VISIBLE
                contactDividerView.visibility = View.VISIBLE
                val debitOrderReversalLimit = TextFormatUtils.formatBasicAmountAsRand(disputeLimit)
                debitOrderExceedsLimitTextView.text = getString(R.string.debit_order_dispute_amount, debitOrderReversalLimit)
            }
        }
        amountContentView.setContentText(TextFormatUtils.formatBasicAmountAsRand(debitOrder.amount))
    }

    private fun recordAnalytics(selectedDebitOrderAmount: Int) {
        val char = 'A' + selectedDebitOrderAmount.div(100)

        if (selectedDebitOrderAmount < 1000) {
            AnalyticsUtil.trackAction("Debit order", "DebitOrder_TransactionDetailScreen_DebitOrderAmount$char")
        } else {
            AnalyticsUtil.trackAction("Debit order", "DebitOrder_TransactionDetailScreen_DebitOrderAmountK")
        }
    }
}