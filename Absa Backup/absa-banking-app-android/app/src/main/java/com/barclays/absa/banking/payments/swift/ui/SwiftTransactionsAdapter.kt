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

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.barclays.absa.banking.R
import com.barclays.absa.banking.payments.swift.services.response.dto.SwiftTransaction
import com.barclays.absa.banking.payments.swift.services.response.dto.SwiftTransactionClaimed
import com.barclays.absa.banking.payments.swift.services.response.dto.SwiftTransactionPending
import com.barclays.absa.utils.DateUtils.*
import kotlinx.android.synthetic.main.swift_transaction_item.view.*
import java.util.*

class SwiftTransactionsAdapter(private val transactionsInterface: SwiftTransactionsInterface?, private val transactionsList: List<SwiftTransaction>) : RecyclerView.Adapter<SwiftTransactionsAdapter.SwiftTransactionsViewHolder>() {

    companion object {
        const val DAYS_THRESHOLD = 10
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SwiftTransactionsViewHolder {
        return SwiftTransactionsViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.swift_transaction_item, parent, false).rootView)
    }

    override fun getItemCount(): Int = transactionsList.size

    override fun onBindViewHolder(holder: SwiftTransactionsViewHolder, position: Int) {
        holder.onBind(transactionsList[position], transactionsInterface)
    }

    inner class SwiftTransactionsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val swiftTransactionView = itemView.swiftTransactionView as SwiftTransactionView

        fun onBind(swiftTransaction: SwiftTransaction, transactionInterface: SwiftTransactionsInterface?) {
            when (swiftTransaction) {
                is SwiftTransactionPending -> {
                    swiftTransactionView.apply {
                        setSenderNameText(swiftTransaction.senderFirstName)
                        setAmountText(swiftTransaction.getFormattedSwiftForeignCurrencyAmount())
                        setSwiftTransactionReference(swiftTransaction.caseIDNumber)

                        val daysDifference = getDateDiff(getDate(getTodaysDate(DASHED_DATE_PATTERN), DASHED_DATE_PATTERN), getDate(swiftTransaction.endDate, DASHED_DATE_PATTERN))
                        val claimMessageResId = if (daysDifference == 1L) R.string.swift_day_to_claim_label else R.string.swift_days_to_claim_label
                        setClaimedDateText(context.getString(claimMessageResId, daysDifference))

                        val claimedTextColorResId = if (daysDifference < DAYS_THRESHOLD) R.color.red else R.color.light_orange
                        setClaimedDateTextColour(ContextCompat.getColor(this.context, claimedTextColorResId))

                        setOnClickListener { transactionInterface?.onTransactionSelected(swiftTransaction) }
                    }
                }
                is SwiftTransactionClaimed -> {
                    swiftTransactionView.apply {
                        setSenderNameText(swiftTransaction.getSwiftSenderFullName())
                        setAmountText(swiftTransaction.getFormattedTotalDueAmount())
                        setSwiftTransactionReference(swiftTransaction.transactionNumber)
                        setClaimedDateText(context.getString(R.string.swift_claimed_label, formatDate(swiftTransaction.date, DASHED_DATE_PATTERN, DATE_DISPLAY_PATTERN, Locale.ENGLISH)))
                        setArrowVisibility(false)
                    }
                }
            }
        }
    }
}