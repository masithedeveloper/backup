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
package com.barclays.absa.banking.shared.genericTransactionHistory.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.barclays.absa.banking.R
import com.barclays.absa.banking.boundary.model.Transaction
import com.barclays.absa.utils.CommonUtils
import styleguide.cards.TransactionView
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import java.util.regex.Pattern

internal class GenericTransactionsAdapter(private val allTransactions: ArrayList<Transaction>) : RecyclerView.Adapter<GenericTransactionsAdapter.TransactionsViewHolder>() {

    private val dateFormatWithMonthString = SimpleDateFormat("dd MMM yyyy", CommonUtils.getCurrentApplicationLocale())
    private var transactions: MutableList<Transaction> = ArrayList()
    private var keyword: String? = null

    init {
        transactions.addAll(allTransactions)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransactionsViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.account_activity_transaction_item, parent, false)
        return TransactionsViewHolder(view)
    }

    override fun onBindViewHolder(holder: TransactionsViewHolder, position: Int) {
        val transactionItem: styleguide.cards.Transaction = styleguide.cards.Transaction()
        val transaction = transactions[position]
        val formatter = SimpleDateFormat("yyyy-MM-dd", CommonUtils.getCurrentApplicationLocale())

        transactionItem.transaction = transaction.description

        if (transaction.creditAmount?.getAmount() != "0.00") {
            transactionItem.amount = transaction.creditAmount.toString()
        } else if (transaction.debitAmount != null) {
            transactionItem.amount = transaction.debitAmount.toString()
        }

        if (transaction.transactionDate != null) {
            try {
                val yesterdayDate = Calendar.getInstance()
                yesterdayDate.add(Calendar.DATE, -1)

                val dateFormatted = formatter.parse(transaction.transactionDate) ?: Date()
                val transactionDate = dateFormatWithMonthString.format(dateFormatted)
                transactionItem.date = transactionDate
                when (transaction.transactionDate) {
                    formatter.format(Calendar.getInstance().time) -> holder.dateTextView.setText(R.string.credit_card_hub_today)
                    formatter.format(yesterdayDate.time) -> holder.dateTextView.setText(R.string.credit_card_hub_yesterday)
                    else -> holder.dateTextView.text = transactionDate
                }
            } catch (e: ParseException) {
                e.printStackTrace()
            }

        } else {
            transactionItem.date = ""
        }

        if (transaction.isUnclearedTransaction) {
            holder.transactionView.setUnclearedLabelText(holder.dateTextView.context.getString(R.string.uncleared))
            transactionItem.isUncleared = true
        } else {
            holder.transactionView.setUnclearedLabelText(null)
            transactionItem.isUncleared = false

            if (transaction.creditAmount.amountDouble > 0) {
                holder.transactionView.setIncomingColor()
            }
        }
        holder.dateTextView.visibility = if (position > 0 && transactions[position - 1].transactionDate == transaction.transactionDate) {
            View.GONE
        } else {
            View.VISIBLE
        }

        if (keyword.isNullOrEmpty()) {
            holder.transactionView.setTransaction(transactionItem)
        } else {
            holder.transactionView.highlight(transactionItem, keyword)
        }
    }

    override fun getItemViewType(position: Int) = position

    internal fun refreshItemList(transactions: List<Transaction>) {
        this.transactions.clear()
        this.transactions.addAll(transactions)
        notifyDataSetChanged()
    }

    fun filter(keyword: String) {
        this.keyword = keyword
        val filteredTransactionItems = ArrayList<Transaction>()
        if (keyword.isEmpty()) {
            refreshItemList(allTransactions)
        } else {
            for (transactionItem in allTransactions) {
                val description = transactionItem.description
                if (!description.isNullOrEmpty()) {
                    val pattern = Pattern.compile(keyword.toLowerCase(Locale.getDefault()))
                    val matcher = pattern.matcher(description.toLowerCase(Locale.getDefault()))
                    if (matcher.find()) {
                        filteredTransactionItems.add(transactionItem)
                    }
                }
            }
            refreshItemList(filteredTransactionItems)
        }
    }

    override fun getItemCount() = transactions.size

    internal inner class TransactionsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var transactionView: TransactionView = itemView.findViewById(R.id.cardTransactionView)
        var dateTextView: TextView = itemView.findViewById(R.id.dateTextView)
    }
}