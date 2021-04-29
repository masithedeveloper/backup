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

import android.graphics.Color
import android.text.SpannableString
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.barclays.absa.banking.databinding.DebiCheckTransactionItemBinding
import com.barclays.absa.banking.debiCheck.services.dto.DebiCheckTransaction
import com.barclays.absa.banking.framework.app.BMBApplication
import com.barclays.absa.utils.DateUtils
import com.barclays.absa.utils.TextFormatUtils
import java.util.*

class DebiCheckTransactionsAdapter(private val transactionsInterface: DebiCheckTransactionsInterface) : RecyclerView.Adapter<DebiCheckTransactionsAdapter.DebiCheckTransactionsViewHolder>() {

    private var transactionsList: List<DebiCheckTransaction> = mutableListOf()
    private var transactionsListFull: List<DebiCheckTransaction> = mutableListOf()
    private var filterText: String = ""

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DebiCheckTransactionsViewHolder {
        val transactionItemBinding = DebiCheckTransactionItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return DebiCheckTransactionsViewHolder(transactionItemBinding)
    }

    override fun onBindViewHolder(viewHolder: DebiCheckTransactionsViewHolder, position: Int) {
        viewHolder.onBind(transactionsList[position], transactionsInterface, filterText)
    }

    override fun getItemCount() = transactionsList.size

    fun setItems(transactions: List<DebiCheckTransaction>) {
        transactionsList = transactions
        transactionsListFull = transactionsList
        notifyDataSetChanged()
    }

    fun filterItems(query: String) {

        if (query.isEmpty()) {
            clearFilter()
        } else {
            filterText = query
            transactionsList = transactionsListFull
            val filterList = transactionsList.filter { it.creditorName.toUpperCase().contains(query.toUpperCase()) }
            transactionsInterface.hasFilterResult(filterList.isNotEmpty())
            transactionsList = filterList
        }
        transactionsInterface.hasFilterResult(transactionsList.isNotEmpty())
        notifyDataSetChanged()
    }

    private fun clearFilter() {
        transactionsList = transactionsListFull
        filterText = ""
    }

    class DebiCheckTransactionsViewHolder(private val transactionItemBinding: DebiCheckTransactionItemBinding) : RecyclerView.ViewHolder(transactionItemBinding.root) {

        fun onBind(debiCheckTransaction: DebiCheckTransaction, transactionsInterface: DebiCheckTransactionsInterface, filterTextInit: String) {
            transactionItemBinding.debiCheckTransactionView.apply {
                setDescriptionText(debiCheckTransaction.creditorName)
                setAmountText("-${TextFormatUtils.formatBasicAmountAsRand(debiCheckTransaction.collectionAmount)}")
                setDateText(DateUtils.formatDateMonth(debiCheckTransaction.lastPaymentDate))
                setOnClickListener {
                    transactionsInterface.onTransactionSelected(debiCheckTransaction)
                }
                showMoreOption()
            }

            val filterText = filterTextInit.toUpperCase(Locale.ROOT)
            if (filterText.isNotEmpty()) {
                val text = debiCheckTransaction.creditorName
                val spannedText = SpannableString(text)
                if (text.toUpperCase(BMBApplication.getApplicationLocale()).contains(filterText)) {
                    val startPosition = text.indexOf(filterText)
                    val endPosition = startPosition + filterText.length
                    if (startPosition != -1) {
                        spannedText.setSpan(ForegroundColorSpan(Color.RED), startPosition, endPosition, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
                    }
                }
                transactionItemBinding.debiCheckTransactionView.setDescriptionText(spannedText)
            }
        }
    }
}