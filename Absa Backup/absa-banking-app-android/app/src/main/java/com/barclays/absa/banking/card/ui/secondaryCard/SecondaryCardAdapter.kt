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
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.barclays.absa.banking.R
import com.barclays.absa.banking.card.ui.secondaryCard.SecondaryCardBaseFragment.Companion.SECONDARY_CARD_TENANT_MANDATE_ACTIVE
import com.barclays.absa.banking.card.ui.secondaryCard.SecondaryCardBaseFragment.Companion.SECONDARY_CARD_TENANT_MANDATE_DEACTIVATED
import com.barclays.absa.banking.express.secondaryCard.getSecondaryCardMandate.dto.SecondaryCards
import kotlinx.android.synthetic.main.secondary_card_item.view.*
import styleguide.utils.extensions.toFormattedAccountNumber

class SecondaryCardAdapter(private var secondaryCards: ArrayList<SecondaryCards>, private val secondaryCardClickListener: SecondaryCardClickListener) : RecyclerView.Adapter<SecondaryCardAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder = ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.secondary_card_item, parent, false))

    override fun getItemCount(): Int = secondaryCards.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val secondaryCard = secondaryCards[position]
        with(holder) {
            secondaryCardHolderNameTextView.text = secondaryCard.additionalEmbossName.replace("/", " ")
            secondaryCardNumberTextView.text = secondaryCard.additionalPlasticNumber.toFormattedAccountNumber()
            secondaryCardCheckBox.isChecked = secondaryCard.additionalTenantMandate.equals(SECONDARY_CARD_TENANT_MANDATE_ACTIVE, true)
            secondaryCardCheckBox.setOnCheckedChangeListener { _, isChecked ->
                secondaryCards[position].additionalTenantMandate = if (isChecked) SECONDARY_CARD_TENANT_MANDATE_ACTIVE else SECONDARY_CARD_TENANT_MANDATE_DEACTIVATED
                secondaryCardClickListener.onSecondaryCardCheckChangeListener(secondaryCards)
            }
            itemView.setOnClickListener { secondaryCardCheckBox.isChecked = !secondaryCardCheckBox.isChecked }
        }
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val secondaryCardCheckBox: CheckBox = itemView.secondaryCardCheckBox as CheckBox
        val secondaryCardHolderNameTextView: TextView = itemView.secondaryCardHolderNameTextView as TextView
        val secondaryCardNumberTextView: TextView = itemView.secondaryCardNumberTextView as TextView
    }

    interface SecondaryCardClickListener {
        fun onSecondaryCardCheckChangeListener(updatedSecondaryCards: ArrayList<SecondaryCards>)
    }
}