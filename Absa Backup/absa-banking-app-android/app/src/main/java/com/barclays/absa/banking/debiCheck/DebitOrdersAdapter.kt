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
package com.barclays.absa.banking.debiCheck

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.barclays.absa.banking.R
import com.barclays.absa.banking.boundary.model.debitOrder.DebitOrderDetailsResponse
import com.barclays.absa.banking.framework.BaseActivity
import com.barclays.absa.banking.framework.app.BMBApplication
import com.barclays.absa.utils.TextFormatUtils.formatBasicAmountAsRand
import kotlinx.android.synthetic.main.debit_order_transaction_item.view.*
import java.util.*

class DebitOrdersAdapter(private val debitOrders: ArrayList<DebitOrderDetailsResponse>, private var selectedDebitOrderInterface: SelectedDebitOrderInterface) : RecyclerView.Adapter<DebitOrdersAdapter.DebitOrderHolder>(), Filterable {

    private var originalDebitOrders: ArrayList<DebitOrderDetailsResponse> = arrayListOf()

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        originalDebitOrders.addAll(debitOrders)
    }

    var searchText: String = ""
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DebitOrderHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.debit_order_transaction_item, parent, false)
        return DebitOrderHolder(view)
    }

    override fun getItemCount(): Int = debitOrders.size

    override fun onBindViewHolder(holder: DebitOrderHolder, position: Int) {
        val debitOrder = debitOrders[position]
        holder.apply {
            transactionDescriptionTextView.text = debitOrder.userReference
            dateTextView.text = debitOrder.actionDate
            amountTextView.text = formatBasicAmountAsRand(debitOrder.amount)
            itemView.setOnClickListener {
                BaseActivity.preventDoubleClick(it)
                selectedDebitOrderInterface.viewDebitOrderDetails(debitOrder)
            }
        }
    }

    class DebitOrderHolder(view: View) : RecyclerView.ViewHolder(view) {
        var transactionDescriptionTextView: TextView = view.descriptionTextView
        var dateTextView: TextView = view.dateTextView
        var amountTextView: TextView = view.amountTextView
    }

    fun searchDebitOrders(searchText: String) {
        filter.filter(searchText)
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(charSequence: CharSequence?): FilterResults {
                val results = FilterResults()
                searchText = charSequence.toString().toLowerCase(BMBApplication.getApplicationLocale()).trim()
                val filteredDebitOrders = arrayListOf<DebitOrderDetailsResponse>()
                if (searchText.isEmpty()) {
                    filteredDebitOrders.addAll(originalDebitOrders)
                } else {
                    originalDebitOrders.forEach {
                        if (it.userReference.toLowerCase(BMBApplication.getApplicationLocale()).contains(searchText, ignoreCase = false) || it.actionDate.toLowerCase(BMBApplication.getApplicationLocale()).contains(searchText)) {
                            filteredDebitOrders.add(it)
                        }
                    }
                }
                results.values = filteredDebitOrders
                results.count = filteredDebitOrders.size
                return results
            }

            override fun publishResults(constraint: CharSequence, results: FilterResults) {
                debitOrders.clear()
                @Suppress("UNCHECKED_CAST")
                debitOrders.addAll(results.values as ArrayList<DebitOrderDetailsResponse>)
                notifyDataSetChanged()
            }
        }
    }
}
