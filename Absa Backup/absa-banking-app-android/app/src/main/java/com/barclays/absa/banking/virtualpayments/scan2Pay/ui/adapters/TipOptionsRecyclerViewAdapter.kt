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
package com.barclays.absa.banking.virtualpayments.scan2Pay.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.barclays.absa.banking.R
import com.barclays.absa.banking.virtualpayments.scan2Pay.ui.views.ScanToPayPaymentFragment.TipOption
import kotlinx.android.synthetic.main.fragment_scan_to_pay_tip_list_item.view.*

class TipOptionsRecyclerViewAdapter(private val tipOptionsList: List<TipOption>, initialTipOption: TipOption, private var onTipOptionSelected: OnTipOptionSelected) : RecyclerView.Adapter<TipOptionsRecyclerViewAdapter.OptionsViewHolder>() {

    interface OnTipOptionSelected {
        fun onTipSelect(tipOption: TipOption)
    }

    var selectedTipOption: TipOption = initialTipOption
        set(value) {
            if (field == value) {
                return
            }
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OptionsViewHolder = OptionsViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.fragment_scan_to_pay_tip_list_item, parent, false))

    override fun getItemCount(): Int = tipOptionsList.size

    override fun onBindViewHolder(holder: OptionsViewHolder, position: Int) {
        val tipOption = tipOptionsList[position]
        val context = holder.view.context
        holder.apply {
            tipTextView.text = tipOption.displayValue
            val isChecked = tipOption.percentage == selectedTipOption.percentage
            if (isChecked) {
                tipCardView.background = ContextCompat.getDrawable(context, R.drawable.tip_view_selected)
                tipTextView.setTextColor(ContextCompat.getColor(context, R.color.white))
            } else {
                tipCardView.background = ContextCompat.getDrawable(context, R.drawable.tip_view_unselected)
                tipTextView.setTextColor(ContextCompat.getColor(context, R.color.silver))
            }
        }
    }

    inner class OptionsViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        val tipCardView: CardView = view.tipCardView
        val tipTextView: TextView = view.tipTextView

        init {
            view.setOnClickListener {
                val selectedTipOption = tipOptionsList[adapterPosition]
                this@TipOptionsRecyclerViewAdapter.selectedTipOption = selectedTipOption
                onTipOptionSelected.onTipSelect(selectedTipOption)
            }
        }
    }
}