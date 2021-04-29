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

package com.barclays.absa.banking.newToBank

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.barclays.absa.banking.R
import com.barclays.absa.banking.newToBank.services.dto.BusinessEvolveOptionalExtrasResponse
import kotlinx.android.synthetic.main.new_to_bank_optional_account_extra_item.view.*

class NewToBankOptionalAccountExtrasRecyclerViewAdapter(private val accountExtrasList: MutableList<BusinessEvolveOptionalExtrasResponse.OptionalExtras>) : RecyclerView.Adapter<NewToBankOptionalAccountExtrasRecyclerViewAdapter.ViewHolder>() {

    var selectedAccountExtras = mutableListOf<BusinessEvolveOptionalExtrasResponse.OptionalExtras>()

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        internal val optionalAccountExtrasCheckBoxView = itemView.optionalAccountExtrasCheckBoxView
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.new_to_bank_optional_account_extra_item, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount() = accountExtrasList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentOptionalAccountExtra = accountExtrasList[position]
        currentOptionalAccountExtra.apply {
            if ("true".equals(visible, true)) {
                holder.optionalAccountExtrasCheckBoxView.setDescription("$title$desc")
                holder.optionalAccountExtrasCheckBoxView.setOnCheckedListener { isChecked ->
                    if (isChecked) {
                        selectedAccountExtras.add(currentOptionalAccountExtra)
                    }
                }
            } else {
                holder.optionalAccountExtrasCheckBoxView.visibility = View.GONE
            }
        }
    }
}