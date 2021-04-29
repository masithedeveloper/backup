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
package com.barclays.absa.banking.presentation.shared

import android.text.Spannable
import android.text.SpannableString
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.barclays.absa.banking.R
import com.barclays.absa.banking.boundary.model.Amount
import com.barclays.absa.banking.databinding.BeneficiaryListItemBinding
import com.barclays.absa.banking.databinding.BeneficiaryListSectionHeaderItemBinding
import com.barclays.absa.banking.express.beneficiaries.dto.RegularBeneficiary
import com.barclays.absa.banking.framework.app.BMBApplication
import styleguide.content.BeneficiaryListItem
import java.util.*

class GenericGroupingRegularBeneficiaryAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private val beneficiarySelectionInterface: RegularBeneficiarySelectionInterface
    private var sectionNormalizedPosition = 0
    private val sectionHeaderPositions = ArrayList<Int>()
    private val allItemsList = ArrayList<RegularBeneficiary>()
    private var originalAllItemsList = ArrayList<RegularBeneficiary>()
    private var sectionedList: LinkedHashMap<String, ArrayList<RegularBeneficiary>> = linkedMapOf()
    private var filterText: String = ""

    companion object {
        private const val VIEW_TYPE_HEADER = 0
        private const val VIEW_TYPE_ITEM = 1
    }

    constructor(beneficiaries: List<RegularBeneficiary>, beneficiarySelectionInterface: RegularBeneficiarySelectionInterface) {
        initializeListData(beneficiaries)
        this.beneficiarySelectionInterface = beneficiarySelectionInterface
    }

    constructor(beneficiaryListFiltered: List<RegularBeneficiary>, filter: String, beneficiarySelectionInterface: RegularBeneficiarySelectionInterface) {
        initializeListData(beneficiaryListFiltered)
        filterText = filter
        this.beneficiarySelectionInterface = beneficiarySelectionInterface
    }

    private fun initializeListData(beneficiaries: List<RegularBeneficiary>) {
        sectionedList = sortAndGroup(beneficiaries)
        originalAllItemsList = ArrayList()
        originalAllItemsList.addAll(beneficiaries)
    }

    private fun sortAndGroup(beneficiaries: List<RegularBeneficiary>): LinkedHashMap<String, ArrayList<RegularBeneficiary>> {
        val sortedList = sort(beneficiaries)
        return group(sortedList)
    }

    private fun group(beneficiaries: List<RegularBeneficiary>): LinkedHashMap<String, ArrayList<RegularBeneficiary>> {
        var firstLetterOfCurrentBeneficiary: String
        val groupedBeneficiaries = LinkedHashMap<String, ArrayList<RegularBeneficiary>>()
        beneficiaries.forEach { beneficiary ->
            val beneficiaryName = beneficiary.beneficiaryName
            firstLetterOfCurrentBeneficiary = if (beneficiaryName.isNotEmpty()) beneficiaryName.toUpperCase().substring(0, 1) else ""
            val currentSectionName = firstLetterOfCurrentBeneficiary
            var beneficiariesForCurrentSection = groupedBeneficiaries[currentSectionName]
            if (beneficiariesForCurrentSection != null) {
                beneficiariesForCurrentSection.add(beneficiary)
            } else {
                sectionHeaderPositions.add(sectionNormalizedPosition)
                beneficiariesForCurrentSection = ArrayList()
                val sectionBeneficiary = RegularBeneficiary()
                sectionBeneficiary.beneficiaryName = currentSectionName
                beneficiariesForCurrentSection.add(sectionBeneficiary)
                allItemsList.add(sectionBeneficiary)
                ++sectionNormalizedPosition
                beneficiariesForCurrentSection.add(beneficiary)
                groupedBeneficiaries[currentSectionName] = beneficiariesForCurrentSection
            }
            allItemsList.add(beneficiary)
            sectionNormalizedPosition++
        }
        return groupedBeneficiaries
    }

    private fun sort(beneficiaries: List<RegularBeneficiary>): List<RegularBeneficiary> = beneficiaries.sortedBy { it.beneficiaryName }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == VIEW_TYPE_HEADER) {
            val beneficiaryListSectionHeaderItemBinding: BeneficiaryListSectionHeaderItemBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.beneficiary_list_section_header_item, parent, false)
            BeneficiarySectionItemViewHolder(beneficiaryListSectionHeaderItemBinding)
        } else {
            val beneficiaryListItemBinding: BeneficiaryListItemBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.beneficiary_list_item, parent, false)
            BeneficiaryListItemViewHolder(beneficiaryListItemBinding)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val beneficiary = allItemsList[position]
        if (holder is BeneficiarySectionItemViewHolder) {
            holder.sectionNameTextView.text = beneficiary.beneficiaryName
        } else if (holder is BeneficiaryListItemViewHolder) {
            val outValue = TypedValue()
            holder.itemView.context.theme.resolveAttribute(android.R.attr.selectableItemBackground, outValue, true)
            holder.itemView.setBackgroundResource(outValue.resourceId)

            val lastPaymentAmount = if (beneficiary.processedTransactions.isNotEmpty()) {
                val lastTransactionAmount = Amount(beneficiary.processedTransactions.first().paymentAmount)
                holder.itemView.context.getString(R.string.last_transaction_beneficiary, lastTransactionAmount.toString(), "", beneficiary.transactionDate)
            } else {
                ""
            }

            val bankAndAccountNumber = "${beneficiary.beneficiaryDetails.bankOrInstitutionName} | ${beneficiary.targetAccountNumber}"
            val displayItem = BeneficiaryListItem(beneficiary.beneficiaryName, bankAndAccountNumber, lastPaymentAmount)
            holder.beneficiaryView.setBeneficiary(displayItem)
            applyFilter(holder, beneficiary)
            holder.beneficiaryView.setOnClickListener { beneficiarySelectionInterface.onBeneficiarySelected(beneficiary) }
        }
    }

    override fun getItemCount(): Int {
        var count = 0
        sectionedList.forEach { (_, value) -> count += value.size }
        return count
    }

    override fun getItemViewType(position: Int): Int {
        return if (sectionHeaderPositions.contains(position)) VIEW_TYPE_HEADER else VIEW_TYPE_ITEM
    }

    private fun applyFilter(viewHolder: BeneficiaryListItemViewHolder, RegularBeneficiary: RegularBeneficiary) {
        if (filterText.isNotEmpty()) {
            val beneficiaryName = RegularBeneficiary.beneficiaryName
            val searchBeneficiary: Spannable = SpannableString(beneficiaryName)
            val beneficiaryAccountNumber: String = RegularBeneficiary.targetAccountNumber
            val searchAccount: Spannable = SpannableString(beneficiaryAccountNumber)
            val startBeneficiaryPosition = beneficiaryName.toLowerCase().indexOf(filterText.toLowerCase())
            val startAccountPosition = beneficiaryAccountNumber.toLowerCase().indexOf(filterText.toLowerCase())
            val endBeneficiaryPosition = startBeneficiaryPosition + filterText.length
            val endAccountPosition = startAccountPosition + filterText.length
            var found = false
            if (endBeneficiaryPosition <= searchBeneficiary.length && startBeneficiaryPosition != -1) {
                found = true
                searchBeneficiary.setSpan(ForegroundColorSpan(ContextCompat.getColor(viewHolder.beneficiaryView.context, R.color.dark_red)), startBeneficiaryPosition, endBeneficiaryPosition, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
                viewHolder.beneficiaryView.nameTextView.text = searchBeneficiary
            }
            if (endAccountPosition <= searchAccount.length && startAccountPosition != -1) {
                found = true
                searchAccount.setSpan(ForegroundColorSpan(ContextCompat.getColor(viewHolder.beneficiaryView.context, R.color.dark_red)), startAccountPosition, endAccountPosition, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
                viewHolder.beneficiaryView.accountNumberTextView.text = searchAccount
            }
            viewHolder.beneficiaryView.visibility = if (!found) View.GONE else View.VISIBLE
        } else {
            viewHolder.beneficiaryView.visibility = View.VISIBLE
        }
    }

    fun String.toLowerCase() = this.toLowerCase(BMBApplication.getApplicationLocale())

    fun String.toUpperCase() = this.toUpperCase(BMBApplication.getApplicationLocale())

    fun setDataSetAndFilterText(beneficiaryList: List<RegularBeneficiary>, query: String) {
        allItemsList.clear()
        sectionHeaderPositions.clear()
        sectionNormalizedPosition = 0
        originalAllItemsList.clear()
        initializeListData(beneficiaryList)
        filterText = query
    }

    private class BeneficiaryListItemViewHolder(beneficiaryListItemBinding: BeneficiaryListItemBinding) : RecyclerView.ViewHolder(beneficiaryListItemBinding.root) {
        val beneficiaryView = beneficiaryListItemBinding.beneficiaryView
    }

    private class BeneficiarySectionItemViewHolder(beneficiaryListSectionHeaderItemBinding: BeneficiaryListSectionHeaderItemBinding) : RecyclerView.ViewHolder(beneficiaryListSectionHeaderItemBinding.root) {
        val sectionNameTextView = beneficiaryListSectionHeaderItemBinding.sectionNameTextview
    }
}

interface RegularBeneficiarySelectionInterface {
    fun onBeneficiarySelected(regularBeneficiary: RegularBeneficiary)
}