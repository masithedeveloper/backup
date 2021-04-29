/*
 * Copyright (c) 2018 Absa Bank Limited, All Rights Reserved.
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
package com.barclays.absa.banking.rewards.ui.rewardsHub

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.RecyclerView
import com.barclays.absa.banking.R
import com.barclays.absa.banking.boundary.model.Amount
import com.barclays.absa.banking.framework.app.BMBApplication
import com.barclays.absa.banking.rewards.services.dto.TransactionItem
import com.barclays.absa.utils.DateUtils
import com.barclays.absa.utils.DateUtils.DASHED_DATE_PATTERN
import com.barclays.absa.utils.DateUtils.DATE_DISPLAY_PATTERN
import styleguide.cards.Transaction
import styleguide.cards.TransactionView
import java.util.*
import kotlin.collections.ArrayList

open class RewardsTransactionsAdapter(var allTransactions: List<TransactionItem>) : RecyclerView.Adapter<RewardsTransactionsAdapter.TransactionItemViewHolder>(), Filterable {
    private var transactions: ArrayList<TransactionItem> = ArrayList()
    private lateinit var keyword: String
    private val customFilter: CustomFilter = CustomFilter()
    private lateinit var context: Context

    companion object {
        const val TELKOM = "telkom"
        const val MTN = "mtn"
        const val CASH = "cash"
        const val VODACOM = "vodacom"
        const val CELLC = "cellc"
        const val BACKDATED_REWARDS = "backdated rewards"
        const val REVERSAL_REDEMPTION = "reversal - redemption"
        const val REDEMPTION = "redemption:"
    }

    init {
        transactions.addAll(allTransactions)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransactionItemViewHolder {
        val transactionView = TransactionView(parent.context)
        context = parent.context
        val layoutParams = RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT, RecyclerView.LayoutParams.WRAP_CONTENT).apply {
            topMargin = parent.context.resources.getDimensionPixelSize(R.dimen.small_space)
            bottomMargin = parent.context.resources.getDimensionPixelSize(R.dimen.normal_space)
            marginStart = parent.context.resources.getDimensionPixelSize(R.dimen.medium_space)
            marginEnd = parent.context.resources.getDimensionPixelSize(R.dimen.medium_space)
        }
        transactionView.layoutParams = layoutParams
        return TransactionItemViewHolder(transactionView)
    }

    private fun getTransactionDescriptionForRedeemedRewards(description: String?): String {
        return if (description.isNullOrBlank()) {
            context.getString(R.string.rewards_redeemed)
        } else if (description.toLowerCase().startsWith(REDEMPTION)) {
            val normalizedDescription = description.toLowerCase().replace(REDEMPTION, "")
            val displayDescription = description.replace("Redemption:", "")
            if (normalizedDescription.contains(CASH)) {
                context.getString(R.string.cash_redeemed)
            } else if (normalizedDescription.contains(VODACOM) || normalizedDescription.contains(MTN) || normalizedDescription.contains(CELLC) || normalizedDescription.contains(TELKOM)) {
                "${context.getString(R.string.airtime_redeemed)} - $displayDescription"
            } else {
                "${context.getString(R.string.rewards_redeemed)} - $displayDescription"
            }
        } else {
            description
        }
    }

    private fun getTransactionDescriptionForEarnedRewards(transactionDescription: String?, transactionAmount: Amount?): String {
        return if (transactionDescription.isNullOrBlank()) {
            context.getString(R.string.rewards_earned)
        } else if (transactionDescription.toLowerCase().equals(context.getString(R.string.cash_rewards_from_absa), ignoreCase = true) ||
                transactionDescription.toLowerCase().equals(BACKDATED_REWARDS, ignoreCase = true)) {
            transactionDescription
        } else if (transactionDescription.toLowerCase().startsWith(REVERSAL_REDEMPTION)) {
            transactionDescription.replace("-", "").replace(":", " - ")
        } else {
            "${context.getString(R.string.cash_spent)} - $transactionAmount at $transactionDescription - ${context.getString(R.string.rewards_earned)}"
        }
    }

    override fun onBindViewHolder(holder: TransactionItemViewHolder, position: Int) {
        val transactionItem = transactions[position]
        val rewardsEarned = transactionItem.rewardsEarned
        rewardsEarned?.let {
            val transaction = Transaction()
            val transactionDescription = if (it.amountValue.toDouble() < 0.0) getTransactionDescriptionForRedeemedRewards(transactionItem.transactionDescription) else getTransactionDescriptionForEarnedRewards(transactionItem.transactionDescription, transactionItem.transactionAmount)
            transaction.transaction = transactionDescription
            transaction.amount = it.toString()
            val date = DateUtils.formatDate(transactionItem.transactionDate, DASHED_DATE_PATTERN, DATE_DISPLAY_PATTERN)
            transaction.date = date
            if (!::keyword.isInitialized || (::keyword.isInitialized && keyword.isBlank())) {
                holder.transactionView.setTransaction(transaction)
            } else {
                holder.transactionView.highlight(transaction, keyword)
            }
        }
    }

    override fun getItemCount(): Int = transactions.size

    class TransactionItemViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var transactionView: TransactionView = view as TransactionView
    }

    fun updateAllTransactions(transactions: List<TransactionItem>) {
        this.allTransactions = ArrayList(transactions)
        this.transactions = ArrayList(transactions)
        notifyDataSetChanged()
    }

    override fun getFilter(): Filter = customFilter

    fun search(text: String?) {
        customFilter.filter(text)
    }

    @Suppress("UNCHECKED_CAST")
    internal inner class CustomFilter : Filter() {
        override fun performFiltering(charSequence: CharSequence): FilterResults {
            keyword = charSequence.toString().toLowerCase().trim()
            val filteredList = arrayListOf<TransactionItem>()
            if (keyword.isEmpty()) {
                filteredList.addAll(allTransactions)
            } else {
                allTransactions.forEach { transactionItem ->
                    val normalizedDescription = if (transactionItem.rewardsEarned!!.amountValue.toDouble() < 0.0) getTransactionDescriptionForRedeemedRewards(transactionItem.transactionDescription) else getTransactionDescriptionForEarnedRewards(transactionItem.transactionDescription, transactionItem.transactionAmount)
                    if (normalizedDescription.toLowerCase(BMBApplication.getApplicationLocale()).contains(keyword, ignoreCase = false)) {
                        filteredList.add(transactionItem)
                    }
                }
            }
            return FilterResults().apply {
                values = filteredList
                count = filteredList.size
            }
        }

        override fun publishResults(charSequence: CharSequence, filterResults: FilterResults) {
            transactions.clear()
            transactions.addAll(filterResults.values as ArrayList<TransactionItem>)
            notifyDataSetChanged()
        }
    }

    fun String.toLowerCase() = this.toLowerCase(Locale.getDefault())
}