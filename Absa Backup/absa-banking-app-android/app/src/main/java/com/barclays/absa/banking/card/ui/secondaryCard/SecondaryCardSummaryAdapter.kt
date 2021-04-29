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
package com.barclays.absa.banking.card.ui.secondaryCard

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.barclays.absa.banking.R
import com.barclays.absa.banking.express.secondaryCard.getSecondaryCardMandate.dto.SecondaryCards
import kotlinx.android.synthetic.main.secondary_card_summary_item.view.*
import styleguide.content.SecondaryContentAndLabelView
import styleguide.utils.extensions.toFormattedAccountNumber

class SecondaryCardSummaryAdapter(var secondaryCardMandateDetailsList: ArrayList<SecondaryCards>) : RecyclerView.Adapter<SecondaryCardSummaryAdapter.ViewHolder>() {

    companion object {
        const val SECONDARY_CARD_MANDATE_NO = "N"
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder = ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.secondary_card_summary_item, parent, false))

    override fun getItemCount() = secondaryCardMandateDetailsList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val secondaryCardItem = secondaryCardMandateDetailsList[position]
        holder.secondaryCardNumberContentAndLabelView.setContentText("${secondaryCardItem.additionalEmbossName.replace("/", " ")} - ${secondaryCardItem.additionalPlasticNumber.toFormattedAccountNumber()}")
        holder.mandateContentAndLabelView.setContentText(if (secondaryCardItem.additionalTenantMandate == SECONDARY_CARD_MANDATE_NO) holder.mandateContentAndLabelView.context.getString(R.string.no) else holder.mandateContentAndLabelView.context.getString(R.string.yes))
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val secondaryCardNumberContentAndLabelView: SecondaryContentAndLabelView = itemView.secondaryCardNumberContentAndLabelView as SecondaryContentAndLabelView
        val mandateContentAndLabelView: SecondaryContentAndLabelView = itemView.mandateContentAndLabelView as SecondaryContentAndLabelView
    }
}