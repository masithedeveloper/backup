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

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.RecyclerView
import com.barclays.absa.banking.databinding.CashSendBeneficiaryItemBinding
import com.barclays.absa.banking.express.cashSend.cashSendListBeneficiaries.dto.CashSendBeneficiary
import styleguide.content.BeneficiaryListItem
import styleguide.utils.extensions.toFormattedCellphoneNumber
import styleguide.utils.extensions.toLowerCaseWithLocale

class CashSendBeneficiariesAdapter(private val originalBeneficiaryList: ArrayList<CashSendBeneficiary>, private val onBeneficiaryClickListener: OnBeneficiaryClickListener, private val isGroupIndicatorVisible: Boolean = true) : RecyclerView.Adapter<CashSendBeneficiariesAdapter.BeneficiaryViewHolder>(), Filterable {
    private var beneficiaryList: ArrayList<CashSendBeneficiary> = ArrayList()
    private val customFilter: CustomFilter = CustomFilter()
    private var searchText: String = ""

    init {
        beneficiaryList.addAll(originalBeneficiaryList)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BeneficiaryViewHolder = BeneficiaryViewHolder(CashSendBeneficiaryItemBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun onBindViewHolder(holder: BeneficiaryViewHolder, position: Int) {
        val cashSendBeneficiary = beneficiaryList[position]
        holder.bind(cashSendBeneficiary, position)
        holder.binding.root.setOnClickListener { onBeneficiaryClickListener.onBeneficiaryClicked(cashSendBeneficiary) }
    }

    override fun getItemCount(): Int = beneficiaryList.size

    override fun getFilter(): Filter = customFilter

    fun search(text: String?) {
        customFilter.filter(text)
    }

    fun updateTransactionList(updatedBeneficiaries: List<CashSendBeneficiary>) {
        beneficiaryList.clear()
        originalBeneficiaryList.clear()

        beneficiaryList.addAll(updatedBeneficiaries)
        originalBeneficiaryList.addAll(updatedBeneficiaries)
        notifyDataSetChanged()
    }

    @Suppress("UNCHECKED_CAST")
    internal inner class CustomFilter : Filter() {
        override fun performFiltering(charSequence: CharSequence): FilterResults {
            searchText = charSequence.toString().toLowerCaseWithLocale()
            val filteredList: MutableList<CashSendBeneficiary> = ArrayList()
            filteredList.addAll(originalBeneficiaryList.filter { it.beneficiaryName.toLowerCaseWithLocale().contains(searchText) || it.targetAccountNumber.toLowerCaseWithLocale().contains(searchText) })

            return FilterResults().apply {
                values = filteredList
                count = filteredList.size
            }
        }

        override fun publishResults(constraint: CharSequence, results: FilterResults) {
            beneficiaryList.clear()
            beneficiaryList.addAll(results.values as ArrayList<CashSendBeneficiary>)
            notifyDataSetChanged()
        }
    }

    inner class BeneficiaryViewHolder(val binding: CashSendBeneficiaryItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(cashSendBeneficiary: CashSendBeneficiary, position: Int) {
            binding.beneficiaryView.setBeneficiary(BeneficiaryListItem(cashSendBeneficiary.beneficiaryName, cashSendBeneficiary.targetAccountNumber.toFormattedCellphoneNumber(), ""))
            binding.letterTextView.text = (cashSendBeneficiary.beneficiaryName.firstOrNull() ?: "").toString()

            binding.letterTextView.visibility = if (!isGroupIndicatorVisible || (isGroupIndicatorVisible && position > 0 && beneficiaryList[position].beneficiaryName.firstOrNull() ?: "" == cashSendBeneficiary.beneficiaryName.firstOrNull() ?: "")) View.GONE else View.VISIBLE
        }
    }
}

interface OnBeneficiaryClickListener {
    fun onBeneficiaryClicked(beneficiary: CashSendBeneficiary)
}