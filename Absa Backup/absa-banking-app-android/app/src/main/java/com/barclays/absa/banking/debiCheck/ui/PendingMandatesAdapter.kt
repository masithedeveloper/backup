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

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.barclays.absa.banking.R
import com.barclays.absa.banking.databinding.DebiCheckItemBinding
import com.barclays.absa.banking.debiCheck.services.dto.DebiCheckMandateDetail
import styleguide.utils.extensions.toFormattedAmountZeroDefault

class PendingMandatesAdapter(private val context: Context, private val debitOrders: List<DebiCheckMandateDetail>) : RecyclerView.Adapter<PendingMandatesAdapter.MandatesViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MandatesViewHolder {
        val debicheckItemBinding = DebiCheckItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MandatesViewHolder(context, debicheckItemBinding)
    }

    override fun onBindViewHolder(mandatesViewHolder: MandatesViewHolder, position: Int) {
        mandatesViewHolder.onBind(debitOrders[position])
    }

    override fun getItemCount() = debitOrders.size

    class MandatesViewHolder(private val context: Context, private val debiCheckItemBinding: DebiCheckItemBinding) : RecyclerView.ViewHolder(debiCheckItemBinding.root) {

        fun onBind(debitOrder: DebiCheckMandateDetail) {
            debiCheckItemBinding.apply {
                companyTextView.text = debitOrder.creditorName.ifEmpty { debitOrder.creditorShortName }
                amountTextView.text = itemView.context.getString(R.string.debicheck_instalment_amount, "R ${debitOrder.installmentAmount.toString().toFormattedAmountZeroDefault()}", debitOrder.frequency.toLowerCase())

                if (debitOrder.amendmentChangedItems.isNotEmpty()) {
                    amendedTextView.visibility = View.VISIBLE
                    amountTextView.visibility = View.GONE
                } else {
                    amendedTextView.visibility = View.GONE
                    amountTextView.visibility = View.VISIBLE
                }

                contentConstraintLayout.setOnClickListener {
                    val intent = Intent(companyTextView.context, DebiCheckHostActivity::class.java).apply {
                        putExtra(DebiCheckHostActivity.CURRENT_FLOW, DebiCheckViewModel.Flow.PENDING.name)
                        putExtra(DebiCheckHostActivity.CURRENT_DEBIT_ORDER, debitOrder)
                    }
                    companyTextView.context.startActivity(intent)
                }
            }
        }
    }
}
