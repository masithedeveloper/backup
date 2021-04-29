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

package com.barclays.absa.banking.cashSendPlus.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.barclays.absa.banking.R
import com.barclays.absa.banking.boundary.model.BeneficiaryObject
import com.barclays.absa.banking.databinding.MultipleBeneficiarySelectedItemBinding
import styleguide.utils.extensions.extractTwoLetterAbbreviation
import styleguide.utils.extensions.toMaskedAccountNumber

class CashSendPlusSelectedBeneficiaryAdapter(private val selectedBeneficiaryList: MutableList<BeneficiaryObject>, private val removeBeneficiaryListener: OnRemoveSelectedBeneficiaryListener) : RecyclerView.Adapter<CashSendPlusSelectedBeneficiaryAdapter.SelectedBeneficiaryItemHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, i: Int): SelectedBeneficiaryItemHolder {
        val multipleBeneficiarySelectedItemBinding: MultipleBeneficiarySelectedItemBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.multiple_beneficiary_selected_item, parent, false)
        return SelectedBeneficiaryItemHolder(multipleBeneficiarySelectedItemBinding)
    }

    override fun onBindViewHolder(bindingHolder: SelectedBeneficiaryItemHolder, position: Int) {
        val item = selectedBeneficiaryList[position]
        bindingHolder.binding.apply {
            selectedBeneficiaryNameTextView.text = item.beneficiaryName
            selectedBeneficiaryAccountNumberTextView.importantForAccessibility = View.IMPORTANT_FOR_ACCESSIBILITY_NO
            selectedBeneficiaryAccountNumberTextView.text = item.beneficiaryAccountNumber?.toMaskedAccountNumber()
            initialsTextView.text = item.beneficiaryName.extractTwoLetterAbbreviation()
            selectedBeneficiaryContainerConstraintLayout.setOnClickListener { removeBeneficiaryListener.onRemoveSelectedBeneficiary(item) }
        }
    }

    override fun getItemCount(): Int = selectedBeneficiaryList.size

    fun addItem(newItem: BeneficiaryObject) {
        selectedBeneficiaryList.add(newItem)
        notifyDataSetChanged()
    }

    fun removeItem(item: BeneficiaryObject) {
        selectedBeneficiaryList.removeIf { it.beneficiaryAccountNumber == item.beneficiaryAccountNumber }
        notifyDataSetChanged()
    }

    class SelectedBeneficiaryItemHolder(val binding: MultipleBeneficiarySelectedItemBinding) : RecyclerView.ViewHolder(binding.selectedBeneficiaryContainerConstraintLayout)

    interface OnRemoveSelectedBeneficiaryListener {
        fun onRemoveSelectedBeneficiary(item: BeneficiaryObject)
    }
}