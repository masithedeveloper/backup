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

package com.barclays.absa.banking.lawForYou.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.barclays.absa.banking.R
import com.barclays.absa.banking.lawForYou.services.dto.CoverAmounts
import kotlinx.android.synthetic.main.law_for_you_offer_item.view.*
import styleguide.utils.extensions.toRandAmount

class LawForYouCoverOptionsAdapter(private val coverOptionsList: List<CoverAmounts>, private var onCoverOptionSelected: OnCoverOptionSelected) : RecyclerView.Adapter<LawForYouCoverOptionsAdapter.OptionsViewHolder>() {

    interface OnCoverOptionSelected {
        fun onCoverSelect(coverOption: CoverAmounts)
    }

    var selectedCoverAmount: CoverAmounts? = null
        set(value) {
            if (field == value) return
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OptionsViewHolder {
        return OptionsViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.law_for_you_offer_item, parent, false))
    }

    override fun getItemCount(): Int = coverOptionsList.size

    override fun onBindViewHolder(holder: OptionsViewHolder, position: Int) {
        val coverOption = coverOptionsList[position]
        val coverAmount = coverOption.coverAmount.toRandAmount()
        val coverPerMonth = coverOption.monthlyPremium.toRandAmount()
        holder.apply {
            val isChecked = coverOption == selectedCoverAmount
            checkBox.isChecked = isChecked
            contentTextView.text = coverOption.cover
            labelTextView.text = holder.view.context.getString(R.string.law_for_you_cover_up_to).format(coverAmount, coverPerMonth)
        }
    }

    inner class OptionsViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        val checkBox: CheckBox = view.secondaryContentCheckBox
        val contentTextView: TextView = view.contentTextView
        val labelTextView: TextView = view.labelTextView

        init {
            view.setOnClickListener {
                val selectedCoverOption = coverOptionsList[adapterPosition]
                selectedCoverAmount = selectedCoverOption
                onCoverOptionSelected.onCoverSelect(selectedCoverOption)
            }
        }
    }
}
