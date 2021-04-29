/*
 * Copyright (c) 2021 Absa Bank Limited, All Rights Reserved.
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
package com.barclays.absa.banking.accountHub

import android.content.res.ColorStateList
import android.graphics.Typeface
import android.text.Spannable
import android.text.SpannableString
import android.text.style.TextAppearanceSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.barclays.absa.banking.R
import com.barclays.absa.banking.boundary.model.Transaction
import com.barclays.absa.banking.databinding.AccountTransactionHistoryItemBinding
import com.barclays.absa.banking.framework.app.BMBApplication
import com.barclays.absa.utils.DateTimeHelper
import com.barclays.absa.utils.DateTimeHelper.DASHED_PATTERN_YYYY_MM_DD
import java.util.*
import kotlin.collections.ArrayList

class AccountTransactionHistoryAdapter(private val originalTransactions: ArrayList<Transaction>) : RecyclerView.Adapter<AccountTransactionHistoryAdapter.TransactionsViewHolder>(), Filterable {
    private var transactions: ArrayList<Transaction> = ArrayList()
    private val customFilter: CustomFilter = CustomFilter()
    private lateinit var searchText: String

    init {
        transactions.addAll(originalTransactions)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = TransactionsViewHolder(AccountTransactionHistoryItemBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun onBindViewHolder(holder: TransactionsViewHolder, position: Int) {
        holder.onBind(transactions[position])
        holder.onBindDateSection(transactions[position], position)
    }

    override fun getItemCount() = transactions.size

    fun updateTransactionList(updatedTransactions: List<Transaction>) {
        this.transactions.clear()
        this.originalTransactions.clear()

        this.transactions.addAll(updatedTransactions)
        this.originalTransactions.addAll(updatedTransactions)
        notifyDataSetChanged()
    }

    override fun getFilter(): Filter = customFilter

    fun search(text: String?) {
        customFilter.filter(text)
    }

    @Suppress("UNCHECKED_CAST")
    internal inner class CustomFilter : Filter() {
        override fun performFiltering(charSequence: CharSequence): FilterResults {
            searchText = charSequence.toString().toLowerCase()
            val filteredList: MutableList<Transaction> = ArrayList()
            filteredList.addAll(originalTransactions.filter { it.description.toLowerCase().contains(searchText) })

            return FilterResults().apply {
                values = filteredList
                count = filteredList.size
            }
        }

        override fun publishResults(constraint: CharSequence, results: FilterResults) {
            transactions.clear()
            transactions.addAll(results.values as ArrayList<Transaction>)
            notifyDataSetChanged()
        }
    }

    inner class TransactionsViewHolder(val binding: AccountTransactionHistoryItemBinding) : RecyclerView.ViewHolder(binding.root) {

        fun onBind(transaction: Transaction) {
            with(binding) {
                transaction.description?.let { description ->
                    if (::searchText.isInitialized && searchText.isNotEmpty()) {
                        val startPosition = description.toLowerCase().indexOf(searchText)
                        val endPosition = startPosition + searchText.length
                        if (startPosition != -1) {
                            val spannable: Spannable = SpannableString(description)
                            val highlightColor = ColorStateList(arrayOf(intArrayOf()), intArrayOf(ContextCompat.getColor(root.context, R.color.filter_color)))
                            val highlightSpan = TextAppearanceSpan(null, Typeface.BOLD, -1, highlightColor, null)
                            spannable.setSpan(highlightSpan, startPosition, endPosition, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                            transactionDescriptionTextView.text = spannable
                        } else {
                            transactionDescriptionTextView.text = description
                        }
                    } else {
                        transactionDescriptionTextView.text = description
                    }
                }

                transactionAmountTextView.text = when {
                    transaction.creditAmount.getAmount() != "0.00" -> transaction.creditAmount.toString()
                    else -> transaction.debitAmount.toString()
                }

                if (transaction.isUnclearedTransaction) {
                    unclearedTextView.visibility = View.VISIBLE
                    transactionAmountTextView.setTextColor(ContextCompat.getColor(root.context, R.color.grey_light_theme_color))
                } else {
                    if (transaction.creditAmount.amountDouble > 0) {
                        transactionAmountTextView.setTextColor(ContextCompat.getColor(root.context, R.color.green))
                    } else {
                        unclearedTextView.visibility = View.GONE
                        transactionAmountTextView.setTextColor(ContextCompat.getColor(root.context, R.color.graphite))
                    }
                }

                transactionDateTextView.text = DateTimeHelper.formatDate(transaction.transactionDate)
            }
        }

        fun onBindDateSection(transaction: Transaction, position: Int) {
            val yesterdayDate = Calendar.getInstance()
            yesterdayDate.add(Calendar.DATE, -1)

            with(binding) {
                when (transaction.transactionDate) {
                    DateTimeHelper.formatDate(Calendar.getInstance().time, DASHED_PATTERN_YYYY_MM_DD) -> dateTextView.setText(R.string.credit_card_hub_today)
                    DateTimeHelper.formatDate(yesterdayDate.time, DASHED_PATTERN_YYYY_MM_DD) -> dateTextView.setText(R.string.credit_card_hub_yesterday)
                    else -> dateTextView.text = DateTimeHelper.formatDate(transaction.transactionDate ?: "")
                }

                dateTextView.visibility = if (position > 0 && transactions[position - 1].transactionDate == transaction.transactionDate) View.GONE else View.VISIBLE
            }
        }
    }

    private fun String.toLowerCase() = this.toLowerCase(BMBApplication.getApplicationLocale())
}