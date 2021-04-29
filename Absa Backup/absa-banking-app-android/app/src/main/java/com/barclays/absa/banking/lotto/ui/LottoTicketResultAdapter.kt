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
package com.barclays.absa.banking.lotto.ui

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import liblotto.lotto.R.dimen.medium_space
import liblotto.lotto.R.dimen.small_space
import lotto.ticket.TicketView
import styleguide.forms.ItemSelectionInterface

class LottoTicketResultAdapter(private var tickets: ArrayList<LottoTicket>, private var ticketClickListener: ItemSelectionInterface) : RecyclerView.Adapter<LottoTicketResultAdapter.LottoTicketViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LottoTicketViewHolder {
        val ticketView = TicketView(parent.context)
        val layoutParams = RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT, RecyclerView.LayoutParams.WRAP_CONTENT).apply {
            topMargin = parent.context.resources.getDimensionPixelSize(small_space)
            marginStart = parent.context.resources.getDimensionPixelSize(medium_space)
            marginEnd = parent.context.resources.getDimensionPixelSize(medium_space)
        }
        ticketView.layoutParams = layoutParams
        return LottoTicketViewHolder(ticketView)
    }

    override fun getItemCount(): Int = tickets.size

    override fun onBindViewHolder(holder: LottoTicketViewHolder, position: Int) {
        val lottoTicket = tickets[position]
        holder.apply {
            ticketView.setTicketTitle(lottoTicket.boardTitle)
            ticketView.setWinningTicket(lottoTicket.isWinningTicket)
            ticketView.setTicketBoardsDescription(lottoTicket.ticketDescription)
            ticketView.setTicketDrawDateRange(lottoTicket.dateRange)
            ticketView.setOnClickListener { ticketClickListener.onItemClicked(adapterPosition) }
        }
    }

    class LottoTicketViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val ticketView: TicketView = itemView as TicketView
    }
}