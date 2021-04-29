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
package com.barclays.absa.banking.paymentsRewrite.ui.multiple

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.barclays.absa.banking.R
import com.barclays.absa.banking.databinding.MultipleBeneficiarySelectedItemBinding
import com.barclays.absa.banking.express.beneficiaries.dto.RegularBeneficiary
import com.barclays.absa.banking.framework.app.BMBApplication
import styleguide.utils.extensions.extractTwoLetterAbbreviation
import styleguide.utils.extensions.toMaskedAccountNumber

internal class SelectedBeneficiaryAdapter(private val selectedBeneficiaryList: List<RegularBeneficiary>, private val beneficiaryAdapterInterface: BeneficiaryAdapterInterface) : RecyclerView.Adapter<SelectedBeneficiaryAdapter.BeneficiaryItemHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, i: Int): BeneficiaryItemHolder = BeneficiaryItemHolder(MultipleBeneficiarySelectedItemBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun onBindViewHolder(bindingHolder: BeneficiaryItemHolder, position: Int) {
        val regularBeneficiary = selectedBeneficiaryList[position]
        val beneficiaryAccountNumber = regularBeneficiary.beneficiaryDetails.targetAccountNumber.toMaskedAccountNumber()
        val talkBackAccountNumber = beneficiaryAccountNumber.replace("", ",")
        with(bindingHolder.multipleBeneficiarySelectedItemBinding) {
            selectedBeneficiaryAccountNumberTextView.text = beneficiaryAccountNumber
            selectedBeneficiaryNameTextView.text = regularBeneficiary.beneficiaryName
            selectedBeneficiaryAccountNumberTextView.importantForAccessibility = View.IMPORTANT_FOR_ACCESSIBILITY_NO
            selectedBeneficiaryNameTextView.contentDescription = BMBApplication.getInstance().getString(R.string.talkback_multipay_beneficiary_list_item, regularBeneficiary.beneficiaryName, talkBackAccountNumber)
            initialsTextView.text = regularBeneficiary.beneficiaryName.extractTwoLetterAbbreviation()
        }
    }

    override fun getItemCount(): Int = selectedBeneficiaryList.size

    internal inner class BeneficiaryItemHolder(internal val multipleBeneficiarySelectedItemBinding: MultipleBeneficiarySelectedItemBinding) : RecyclerView.ViewHolder(multipleBeneficiarySelectedItemBinding.selectedBeneficiaryContainerConstraintLayout) {

        init {
            multipleBeneficiarySelectedItemBinding.selectedBeneficiaryContainerConstraintLayout.setOnClickListener { beneficiaryAdapterInterface.beneficiaryRemoveIconClicked(adapterPosition) }
        }
    }

    interface BeneficiaryAdapterInterface {
        fun beneficiaryRemoveIconClicked(index: Int)
    }
}