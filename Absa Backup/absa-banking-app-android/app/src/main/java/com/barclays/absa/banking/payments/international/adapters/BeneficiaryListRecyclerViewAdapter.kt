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

package com.barclays.absa.banking.payments.international.adapters

import android.graphics.Color
import android.text.SpannableString
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.barclays.absa.banking.R
import com.barclays.absa.banking.databinding.BeneficiaryListItemBinding
import com.barclays.absa.banking.databinding.BeneficiaryListSectionHeaderItemBinding
import com.barclays.absa.banking.payments.international.data.InternationalPaymentBeneficiaryDetails
import styleguide.content.BeneficiaryListItem
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.set

class BeneficiaryListRecyclerViewAdapter(beneficiaryList: ArrayList<InternationalPaymentBeneficiaryDetails>, internationalItemSelectedInterface: InternationalItemSelectedInterface) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var filteredBeneficiaryList = beneficiaryList
    private var sectionNormalizedPosition = 0
    private var filterText: String? = ""
    private var sectionHeaderPositions = ArrayList<Int>()
    private var allItemsList = ArrayList<InternationalPaymentBeneficiaryDetails>()
    private var originalAllItemsList = ArrayList<InternationalPaymentBeneficiaryDetails>()
    private var sectionedList: LinkedHashMap<String, ArrayList<InternationalPaymentBeneficiaryDetails>>? = LinkedHashMap()
    private val viewTypeHeader = 0
    private val viewTypeItem = 1
    private var internationalItemSelectedInterface: InternationalItemSelectedInterface

    init {
        initializeListData(filteredBeneficiaryList)
        this.internationalItemSelectedInterface = internationalItemSelectedInterface
    }

    constructor(beneficiaryList: ArrayList<InternationalPaymentBeneficiaryDetails>, filterText: String, internationalItemSelectedInterface: InternationalItemSelectedInterface) : this(beneficiaryList, internationalItemSelectedInterface) {
        this.filterText = filterText
    }

    private fun initializeListData(beneficiaries: List<InternationalPaymentBeneficiaryDetails>) {
        sectionedList = sortAndGroup(beneficiaries)
        originalAllItemsList = ArrayList()
        originalAllItemsList.addAll(beneficiaries)
    }

    private fun sortAndGroup(beneficiaries: List<InternationalPaymentBeneficiaryDetails>): LinkedHashMap<String, ArrayList<InternationalPaymentBeneficiaryDetails>> {
        val sortedList = sort(beneficiaries)
        return group(sortedList)
    }

    private fun group(beneficiaries: List<InternationalPaymentBeneficiaryDetails>): LinkedHashMap<String, ArrayList<InternationalPaymentBeneficiaryDetails>> {
        var firstLetterOfCurrentBeneficiary: String
        val groupedBeneficiaries = LinkedHashMap<String, ArrayList<InternationalPaymentBeneficiaryDetails>>()
        for (beneficiary in beneficiaries) {
            val beneficiaryName = beneficiary.beneficiaryDisplayName
            firstLetterOfCurrentBeneficiary = beneficiaryName?.toUpperCase()?.substring(0, 1) ?: ""
            var beneficiariesForCurrentSection: ArrayList<InternationalPaymentBeneficiaryDetails>? = groupedBeneficiaries[firstLetterOfCurrentBeneficiary]
            if (beneficiariesForCurrentSection != null) {
                beneficiariesForCurrentSection.add(beneficiary)
                allItemsList.add(beneficiary)
                sectionNormalizedPosition++
            } else {
                sectionHeaderPositions.add(sectionNormalizedPosition)
                beneficiariesForCurrentSection = ArrayList()
                val sectionBeneficiary = InternationalPaymentBeneficiaryDetails()
                sectionBeneficiary.beneficiaryDisplayName = firstLetterOfCurrentBeneficiary
                beneficiariesForCurrentSection.add(sectionBeneficiary)
                allItemsList.add(sectionBeneficiary)
                ++sectionNormalizedPosition

                beneficiariesForCurrentSection.add(beneficiary)
                allItemsList.add(beneficiary)
                groupedBeneficiaries[firstLetterOfCurrentBeneficiary] = beneficiariesForCurrentSection
                sectionNormalizedPosition++
            }
        }
        return groupedBeneficiaries
    }

    private fun sort(beneficiaries: List<InternationalPaymentBeneficiaryDetails>): List<InternationalPaymentBeneficiaryDetails> {
        return beneficiaries.sortedWith(compareBy { it.beneficiaryDisplayName })
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == viewTypeHeader) {
            val beneficiaryListSectionHeaderItemBinding = DataBindingUtil.inflate<BeneficiaryListSectionHeaderItemBinding>(LayoutInflater.from(parent.context), R.layout.beneficiary_list_section_header_item, parent, false)
            BeneficiarySectionItemViewHolder(beneficiaryListSectionHeaderItemBinding)
        } else {
            val beneficiaryListItemBinding = DataBindingUtil.inflate<BeneficiaryListItemBinding>(LayoutInflater.from(parent.context), R.layout.beneficiary_list_item, parent, false)
            BeneficiaryListItemViewHolder(beneficiaryListItemBinding)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val beneficiary = allItemsList[position]

        if (holder is BeneficiarySectionItemViewHolder) {
            val sectionNameTextView = holder.beneficiaryListSectionHeaderItemBinding.sectionNameTextview
            sectionNameTextView.text = beneficiary.beneficiaryDisplayName
        } else if (holder is BeneficiaryListItemViewHolder) {
            val outValue = TypedValue()
            holder.itemView.context.theme.resolveAttribute(android.R.attr.selectableItemBackground, outValue, true)
            holder.itemView.setBackgroundResource(outValue.resourceId)

            val lastTransactionAmount = beneficiary.lastPaidAmount
            var lastPaymentDetail: String? = null
            val placeholderText = ""
            if (lastTransactionAmount != null) {
                lastPaymentDetail = holder.itemView.context.getString(R.string.last_transaction_beneficiary, "", placeholderText, "")
            }
            val displayItem = BeneficiaryListItem(beneficiary.beneficiaryDisplayName, "", lastPaymentDetail)
            holder.beneficiaryListItemBinding.beneficiaryView.setBeneficiary(displayItem)

            applyFilter(holder.beneficiaryListItemBinding, beneficiary)

            holder.beneficiaryListItemBinding.beneficiaryView.setOnClickListener {
                internationalItemSelectedInterface.onBeneficiarySelected(beneficiary)
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (sectionHeaderPositions.contains(position)) viewTypeHeader else viewTypeItem
    }

    override fun getItemCount(): Int {
        var count = 0
        if (sectionedList != null) {
            for (section in sectionedList!!.values) {
                count += section.size
            }
        }
        return count
    }

    private fun applyFilter(viewHolder: BeneficiaryListItemBinding, beneficiaryList: InternationalPaymentBeneficiaryDetails) {
        if (!filterText.isNullOrEmpty()) {
            val beneficiaryName = beneficiaryList.beneficiaryDisplayName
            val searchBeneficiary = SpannableString(beneficiaryName)
            val beneficiaryAccountNumber = beneficiaryList.beneficiaryDisplayName
            val searchAccount = SpannableString(beneficiaryAccountNumber)

            val startBeneficiaryPosition = beneficiaryName?.toLowerCase()?.indexOf(filterText!!.toLowerCase())
                    ?: -1
            val startAccountPosition = beneficiaryAccountNumber?.toLowerCase()?.indexOf(filterText!!.toLowerCase())
                    ?: -1

            val endBeneficiaryPosition = startBeneficiaryPosition + filterText!!.length
            val endAccountPosition = startAccountPosition + filterText!!.length

            var found = false

            if (endBeneficiaryPosition <= searchBeneficiary.length && startBeneficiaryPosition != -1) {
                found = true
                searchBeneficiary.setSpan(ForegroundColorSpan(Color.RED), startBeneficiaryPosition, endBeneficiaryPosition, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
                viewHolder.beneficiaryView.nameTextView.text = searchBeneficiary
            }

            if (endAccountPosition <= searchAccount.length && startAccountPosition != -1) {
                found = true
                searchAccount.setSpan(ForegroundColorSpan(Color.RED), startAccountPosition, endAccountPosition, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
                viewHolder.beneficiaryView.accountNumberTextView.text = searchAccount
            }

            viewHolder.beneficiaryView.visibility = if (!found) View.GONE else View.VISIBLE
        } else {
            viewHolder.beneficiaryView.visibility = View.VISIBLE
        }
    }

    private inner class BeneficiaryListItemViewHolder internal constructor(val beneficiaryListItemBinding: BeneficiaryListItemBinding) : RecyclerView.ViewHolder(beneficiaryListItemBinding.root)

    private inner class BeneficiarySectionItemViewHolder internal constructor(val beneficiaryListSectionHeaderItemBinding: BeneficiaryListSectionHeaderItemBinding) : RecyclerView.ViewHolder(beneficiaryListSectionHeaderItemBinding.root)
}
