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
import com.barclays.absa.banking.databinding.DebiCheckListItemBinding
import com.barclays.absa.banking.databinding.SectionViewBinding
import com.barclays.absa.banking.debiCheck.services.dto.DebiCheckMandateDetail
import com.barclays.absa.utils.TextFormatUtils
import java.text.Collator
import java.util.*
import kotlin.collections.ArrayList

class DebiCheckAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var debiCheckMandateList: ArrayList<DebiCheckMandateDetail> = ArrayList()
    private var debiCheckMandateListFull: ArrayList<DebiCheckMandateDetail> = ArrayList()
    private var sectionedList: ArrayList<Any> = ArrayList()
    private var sections: ArrayList<Char> = ArrayList()
    private var filterText: String = ""
    var adapterInterface: DebiCheckAdapterInterface? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == ViewType.DETAIL.value) {
            DebiCheckViewHolder(DebiCheckListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false))
        } else {
            DebiCheckTransactionsHeaderViewHolder(SectionViewBinding.inflate(LayoutInflater.from(parent.context), parent, false))
        }
    }

    override fun onBindViewHolder(viewHolder: RecyclerView.ViewHolder, position: Int) {
        if (viewHolder is DebiCheckViewHolder) {
            viewHolder.onBind(sectionedList[position] as DebiCheckMandateDetail, filterText)
        } else if (viewHolder is DebiCheckTransactionsHeaderViewHolder) {
            viewHolder.onBind(sectionedList[position].toString())
        }
    }

    override fun getItemCount(): Int = if (filterText.isEmpty()) {
        sectionedList.size
    } else {
        debiCheckMandateList.size + sections.size
    }

    fun setItems(mandates: List<DebiCheckMandateDetail>) {
        debiCheckMandateList = sortList(ArrayList(mandates))
        debiCheckMandateListFull = debiCheckMandateList
        resetLists()
        notifyDataSetChanged()
    }

    private fun sortList(list: ArrayList<DebiCheckMandateDetail>): ArrayList<DebiCheckMandateDetail> {
        list.sortWith { teamMember1, teamMember2 -> Collator.getInstance(Locale.US).compare(teamMember1.creditorName, teamMember2.creditorName) }
        return list
    }

    fun filterItems(query: String) {
        filterText = query
        debiCheckMandateList = debiCheckMandateListFull
        val filterList = debiCheckMandateList.filter { it.creditorName.toUpperCase(Locale.ROOT).contains(query.toUpperCase(Locale.ROOT)) }
        adapterInterface?.hasFilterResult(filterList.isNotEmpty())
        debiCheckMandateList = ArrayList(filterList)
        resetLists()

        if (query.isEmpty()) {
            clearFilter()
        }
        notifyDataSetChanged()
    }

    private fun resetLists() {
        sections.clear()
        sectionedList.clear()
        debiCheckMandateList.forEach { mandateDetail: DebiCheckMandateDetail ->
            val creditorName = mandateDetail.creditorName[0]
            if (!sections.contains(creditorName)) {
                sectionedList.add(creditorName)
                sectionedList.add(mandateDetail)
                sections.add(creditorName)
            } else {
                sectionedList.add(mandateDetail)
            }
        }
    }

    private fun clearFilter() {
        debiCheckMandateList = debiCheckMandateListFull
        filterText = ""
    }

    override fun getItemViewType(position: Int): Int = if (sectionedList[position] is DebiCheckMandateDetail) ViewType.DETAIL.value else ViewType.HEADER.value

    inner class DebiCheckViewHolder(private val debiCheckListItemBinding: DebiCheckListItemBinding) : RecyclerView.ViewHolder(debiCheckListItemBinding.root) {

        fun onBind(mandate: DebiCheckMandateDetail, filterTextInit: String) {
            val filterText = filterTextInit.toUpperCase(Locale.ROOT)
            debiCheckListItemBinding.creditorNameContentView.setLabelText("${TextFormatUtils.formatBasicAmountAsRand(mandate.installmentAmount)} ${mandate.frequency.toLowerCase()}")
            if (filterText.isEmpty()) {
                debiCheckListItemBinding.creditorNameContentView.contentTextView.text = mandate.creditorName
            } else {
                var text = mandate.creditorName
                val spannedText = SpannableString(text)
                text = text.toUpperCase(Locale.ROOT)
                if (text.contains(filterText)) {
                    val startPosition = text.indexOf(filterText)
                    val endPosition = startPosition + filterText.length
                    if (startPosition != -1) {
                        spannedText.setSpan(ForegroundColorSpan(Color.RED), startPosition, endPosition, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
                    }
                }
                debiCheckListItemBinding.creditorNameContentView.contentTextView.text = spannedText
            }
            debiCheckListItemBinding.debicheckListItemConstraintLayout.setOnClickListener {
                adapterInterface?.onDebitOrderSelected(mandate)
            }
        }
    }

    class DebiCheckTransactionsHeaderViewHolder(private val sectionViewBinding: SectionViewBinding) : RecyclerView.ViewHolder(sectionViewBinding.root) {
        fun onBind(text: String) {
            sectionViewBinding.sectionListAlphabetView.text = text
        }
    }

    enum class ViewType(val value: Int) {
        HEADER(0),
        DETAIL(1)
    }
}