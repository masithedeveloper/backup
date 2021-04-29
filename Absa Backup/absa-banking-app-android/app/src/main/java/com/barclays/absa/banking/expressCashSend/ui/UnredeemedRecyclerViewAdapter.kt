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
 */
package com.barclays.absa.banking.expressCashSend.ui

import android.content.Context
import android.graphics.Color
import android.text.Spannable
import android.text.SpannableString
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.barclays.absa.banking.R
import com.barclays.absa.banking.databinding.UnredeemCardviewViewBinding
import com.barclays.absa.banking.express.cashSend.unredeemedCashSendTransactions.dto.CashSendPaymentTransaction
import com.barclays.absa.banking.framework.app.BMBApplication
import com.barclays.absa.utils.AccessibilityUtils
import com.barclays.absa.utils.DateTimeHelper
import com.barclays.absa.utils.DateTimeHelper.SPACED_PATTERN_DD_MMM_YYYY
import com.barclays.absa.utils.DateUtils
import styleguide.utils.extensions.toFormattedCellphoneNumber
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import java.util.regex.Pattern
import kotlin.collections.ArrayList

class UnredeemedRecyclerViewAdapter(private val unredeemedTransactionsInterface: UnredeemedTransactionsInterface, transactionsList: ArrayList<CashSendPaymentTransaction>) : RecyclerView.Adapter<UnredeemedRecyclerViewAdapter.UnredeemedViewHolder>() {
    private var transactionsList: ArrayList<CashSendPaymentTransaction>
    private var originalList: ArrayList<CashSendPaymentTransaction>
    private var filterText: String = ""

    private lateinit var context: Context

    init {
        this.transactionsList = transactionsList
        originalList = this.transactionsList
    }

    fun setTransactionsList(transactionsList: ArrayList<CashSendPaymentTransaction>) {
        this.transactionsList = transactionsList
        originalList = this.transactionsList
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UnredeemedViewHolder {
        context = parent.context
        val binding = UnredeemCardviewViewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return UnredeemedViewHolder(binding)
    }

    override fun onBindViewHolder(holder: UnredeemedViewHolder, position: Int) {
        val transaction = transactionsList[position]
        with(holder.binding) {
            cardView.setOnClickListener { unredeemedTransactionsInterface.onItemClick(transaction) }

            if (!transaction.beneficiaryShortName.equals("null", ignoreCase = true)) {
                unredeemedBeneficiaryTextView.text = transaction.beneficiaryShortName
            } else {
                unredeemedBeneficiaryTextView.text = ""
            }

            val formattedCellphoneNumber = transaction.recipientCellphoneNumber.toFormattedCellphoneNumber()
            unredeemedBeneficiaryTextView.append(" ($formattedCellphoneNumber)")

            if (filterText.isNotEmpty()) {
                val textToSearch = unredeemedBeneficiaryTextView.text.toString()
                val searchText: Spannable = SpannableString(textToSearch)
                val startPosition = textToSearch.toLowerCase().indexOf(filterText.toLowerCase())
                val endPosition = startPosition + filterText.length
                if (endPosition <= searchText.length && startPosition != -1) {
                    searchText.setSpan(ForegroundColorSpan(Color.RED), startPosition, endPosition, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
                    unredeemedBeneficiaryTextView.text = searchText
                }
            }

            var formattedDate = ""
            var timeLapse = ""

            formattedDate = DateTimeHelper.formatDate(transaction.transactionDateTime)
            val simpleDateFormat = SimpleDateFormat(SPACED_PATTERN_DD_MMM_YYYY, BMBApplication.getApplicationLocale())
            timeLapse = calculateDateTime(formattedDate, simpleDateFormat)

            if (position > 0 && transaction.transactionDateTime == transactionsList[position - 1].transactionDateTime) {
                dateHeaderTextView.visibility = View.GONE
            } else {
                dateHeaderTextView.text = timeLapse
                dateHeaderTextView.visibility = View.VISIBLE
            }
            unredeemedDateTextView.text = formattedDate
            unredeemedAmountTextView.text = transaction.paymentAmount
            setUpAccessibility(this)
        }
    }

    private fun setUpAccessibility(binding: UnredeemCardviewViewBinding) {
        if (AccessibilityUtils.isExploreByTouchEnabled()) {
            val beneficiaryDetails = binding.unredeemedBeneficiaryTextView.text.split(Pattern.quote("(").toRegex()).toTypedArray()
            val contactName = beneficiaryDetails[0]
            val contactNumber = beneficiaryDetails[1]
            val transactionDate = binding.unredeemedDateTextView.text
            val transactionAmount = AccessibilityUtils.getTalkBackRandValueFromString(binding.unredeemedAmountTextView.text.toString())
            val dateHeader = binding.dateHeaderTextView.text
            if (binding.dateHeaderTextView.visibility == View.VISIBLE) {
                binding.dateHeaderTextView.contentDescription = context.getString(R.string.talkback_unredeemed_cashsends_date_view, dateHeader)
            }
            binding.cardView.contentDescription = context.getString(R.string.talkback_unredeemed_cashsends_card_info, contactName, contactNumber, transactionDate, transactionAmount)
        }
    }

    private fun calculateDateTime(transactionDate: String, simpleDateFormat: SimpleDateFormat): String {
        return try {
            val calendar = Calendar.getInstance()
            calendar.time = DateUtils.getDate(transactionDate, simpleDateFormat)
            val today = Calendar.getInstance()
            val yesterday = Calendar.getInstance()
            yesterday.add(Calendar.DATE, -1)
            when {
                calendar[Calendar.YEAR] == today[Calendar.YEAR] && calendar[Calendar.DAY_OF_YEAR] == today[Calendar.DAY_OF_YEAR] -> context.resources.getString(R.string.today)
                calendar[Calendar.YEAR] == yesterday[Calendar.YEAR] && calendar[Calendar.DAY_OF_YEAR] == yesterday[Calendar.DAY_OF_YEAR] -> context.resources.getString(R.string.yesterday)
                else -> transactionDate
            }
        } catch (e: ParseException) {
            ""
        }
    }

    override fun getItemCount(): Int = transactionsList.size

    override fun getItemId(position: Int): Long = position.toLong()

    fun filterText(filter: String) {
        filterText = filter
        transactionsList = ArrayList()
        if (filter.isNotEmpty()) {
            transactionsList.addAll(originalList)
            notifyDataSetChanged()
            unredeemedTransactionsInterface.hideNoResultsView()
            return
        }
        originalList.forEach { transactionUnredeem ->
            val searchString = "${transactionUnredeem.beneficiaryShortName} (${transactionUnredeem.recipientCellphoneNumber.toFormattedCellphoneNumber()})"
            if (searchString.toLowerCase().contains(filter.toLowerCase())) {
                transactionsList.add(transactionUnredeem)
            }
        }
        if (transactionsList.isNotEmpty()) {
            notifyDataSetChanged()
            unredeemedTransactionsInterface.hideNoResultsView()
        } else {
            unredeemedTransactionsInterface.showNoResultsView()
        }
    }

    private fun String.toLowerCase(): String = this.toLowerCase(BMBApplication.getApplicationLocale())

    class UnredeemedViewHolder(var binding: UnredeemCardviewViewBinding) : RecyclerView.ViewHolder(binding.root)
}

interface UnredeemedTransactionsInterface {
    fun hideNoResultsView()
    fun showNoResultsView()
    fun onItemClick(transactionItem: CashSendPaymentTransaction)
}