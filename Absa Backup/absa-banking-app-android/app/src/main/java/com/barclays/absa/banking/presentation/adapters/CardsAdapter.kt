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
package com.barclays.absa.banking.presentation.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.barclays.absa.banking.R
import com.barclays.absa.banking.card.services.card.dto.ManageCardResponseObject
import com.barclays.absa.banking.card.services.card.dto.PauseStates
import kotlinx.android.synthetic.main.card_list_item_rebrand.view.*
import styleguide.cards.LargeCard
import styleguide.cards.LargeCardView
import styleguide.utils.extensions.toFormattedAccountNumber
import styleguide.utils.extensions.toTitleCase

class CardsAdapter(private val manageCardResponseObject: ManageCardResponseObject, private val cardClickListener: CardClickListener) : RecyclerView.Adapter<CardsAdapter.CardViewHolder>() {

    companion object {
        const val CREDIT_CARD = "CC"
        private const val PAUSED = "Y"
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): CardViewHolder {
        val view = LayoutInflater.from(viewGroup.context).inflate(R.layout.card_list_item_rebrand, viewGroup, false)
        return CardViewHolder(view)
    }

    override fun onBindViewHolder(viewHolder: CardViewHolder, position: Int) {
        val context = viewHolder.itemView.context

        val cards = manageCardResponseObject.listOfCards()[position]
        val cardType = if (CREDIT_CARD.equals(cards.cardType, ignoreCase = true) || context.getString(R.string.credit_card_type).equals(cards.cardType, ignoreCase = true)) context.getString(R.string.credit_card).toTitleCase() else context.getString(R.string.debit_card)
        cards.cardNumber?.let { cardNumber ->
            viewHolder.apply {
                cardView.setAccount(LargeCard(cardType, cards.cardNumber.toFormattedAccountNumber()))
                if (isCardPaused(manageCardResponseObject.getCardPauseStates(cardNumber))) {
                    lockImageView.visibility = View.VISIBLE
                    lockTextView.visibility = View.VISIBLE
                }
                itemView.setOnClickListener {
                    cardClickListener.onCardClickListener(manageCardResponseObject.getCardItem(cardNumber), cardType)
                }
            }
        }
    }

    private fun isCardPaused(pauseStates: PauseStates): Boolean {
        return PAUSED.equals(pauseStates.internationalAtmTransactions, ignoreCase = true) || PAUSED.equals(pauseStates.internationalPointOfSaleTransactions, ignoreCase = true) || PAUSED.equals(pauseStates.localAtmTransactions, ignoreCase = true) || PAUSED.equals(pauseStates.localPointOfSaleTransactions, ignoreCase = true) || PAUSED.equals(pauseStates.onlinePurchases, ignoreCase = true)
    }

    override fun getItemCount(): Int = manageCardResponseObject.cardLimits?.size ?: 0

    inner class CardViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val cardView: LargeCardView = itemView.cardLargeCardView
        val lockImageView: ImageView = itemView.lockImageView
        val lockTextView: TextView = itemView.lockTextView
    }
}