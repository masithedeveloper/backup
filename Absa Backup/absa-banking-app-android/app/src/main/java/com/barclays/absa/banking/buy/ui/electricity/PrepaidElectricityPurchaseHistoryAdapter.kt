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
package com.barclays.absa.banking.buy.ui.electricity

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.barclays.absa.banking.R
import com.barclays.absa.banking.boundary.model.TransactionObject
import com.barclays.absa.utils.DateUtils
import kotlinx.android.synthetic.main.line_item_electricity_purchase.view.*
import kotlinx.android.synthetic.main.prepaid_electricity_header_item.view.*
import styleguide.utils.extensions.formatAmountAsRand
import styleguide.utils.extensions.toTitleCaseRemovingCommas

class PrepaidElectricityPurchaseHistoryAdapter(private val transactionHistory: List<PrePaidElectricityTransaction>, private val itemSelectionInterface: OnBeneficiaryClickListener) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val PREPAID_HEADER_ITEM = 0
        private const val PREPAID_TRANSACTION_ITEM = 1
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder = if (viewType == PREPAID_HEADER_ITEM) {
        HeaderViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.prepaid_electricity_header_item, parent, false))
    } else {
        ItemViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.line_item_electricity_purchase, parent, false))
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (val item = transactionHistory[position]) {
            is PrePaidElectricityTransaction.HeaderItem -> (holder as HeaderViewHolder).onBind(item)
            is PrePaidElectricityTransaction.TransactionItem -> {
                with(holder as ItemViewHolder) {
                    when {
                        itemView.context.getString(R.string.status_success).equals(item.transactionObject.transactionStatus, ignoreCase = true) -> {
                            statusTextView.text = itemView.context.getString(R.string.prepaid_electricity_successful_purchase)
                            documentImageView.visibility = View.VISIBLE
                        }
                        "UNSUCCESSFUL".equals(item.transactionObject.transactionStatus, ignoreCase = true) -> {
                            statusTextView.text = itemView.context.getString(R.string.prepaid_electricity_unsuccessful_purchase)
                            documentImageView.visibility = View.GONE
                        }
                        else -> {
                            statusTextView.text = item.transactionObject.transactionStatus.toTitleCaseRemovingCommas()
                            documentImageView.visibility = View.GONE
                        }
                    }

                    dateTimeTextView.text = DateUtils.formatDate(item.transactionObject.date, "yyyy-MM-dd HH:mm:ss", DateUtils.DATE_TIME_PATTERN)
                    amountTextView.text = item.transactionObject.amount?.getAmount().formatAmountAsRand()
                    itemView.setOnClickListener { itemSelectionInterface.onClickListener(item.transactionObject) }
                }
            }
        }
    }

    override fun getItemCount(): Int = transactionHistory.size

    override fun getItemViewType(position: Int) = when (transactionHistory[position]) {
        is PrePaidElectricityTransaction.HeaderItem -> PREPAID_HEADER_ITEM
        is PrePaidElectricityTransaction.TransactionItem -> PREPAID_TRANSACTION_ITEM
    }

    class ItemViewHolder(viewItem: View) : RecyclerView.ViewHolder(viewItem) {
        val statusTextView = itemView.statusTextView as TextView
        val documentImageView = itemView.documentImageView as ImageView
        val dateTimeTextView = itemView.dateTimeTextView as TextView
        val tokenTextView = itemView.tokenTextView as TextView //todo verify what to do with multiple tokens as that is a possibility looking at the code.
        val amountTextView = itemView.amountTextView as TextView
    }

    class HeaderViewHolder(viewItem: View) : RecyclerView.ViewHolder(viewItem) {
        val amountTextView = itemView.headerTextView as TextView

        fun onBind(item: PrePaidElectricityTransaction.HeaderItem) {
            amountTextView.text = item.header
        }
    }

    interface OnBeneficiaryClickListener {
        fun onClickListener(transactionObject: TransactionObject)
    }
}