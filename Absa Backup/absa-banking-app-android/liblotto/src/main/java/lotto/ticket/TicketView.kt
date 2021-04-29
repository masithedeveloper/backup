/*
 *  Copyright (c) 2019 Absa Bank Limited, All Rights Reserved.
 *
 *  This code is confidential to Absa Bank Limited and shall not be disclosed
 *  outside the Bank without the prior written permission of the Absa Legal
 *
 *  In the event that such disclosure is permitted the code shall not be copied
 *  or distributed other than on a need-to-know basis and any recipients may be
 *  required to sign a confidentiality undertaking in favor of Absa Bank
 *  Limited
 *
 */
package lotto.ticket

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.ticket_view.view.*
import liblotto.lotto.R
import lotto.LottoBoard
import lotto.LottoDisplayBoardView

class TicketView : ConstraintLayout {

    constructor(context: Context) : super(context) {
        init(context)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init(context)
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init(context)
    }

    fun init(context: Context) {
        View.inflate(context, R.layout.ticket_view, this)
        quickPickLineItemView.setViewMargins(resources.getDimensionPixelSize(R.dimen.normal_space))
    }

    fun setTicketTitle(ticketTitle: String) {
        ticketTitleTextView.text = ticketTitle
    }

    fun setTicketBoardsDescription(boardDescription: String) {
        ticketBoardCounterTextView.text = boardDescription
    }

    fun setTicketDrawDateRange(drawDateRange: String) {
        ticketDrawDateTextView.text = drawDateRange
    }

    fun setQuickPickBoard(numberOfBoards: String, quickPickText: String) {
        dividerView.visibility = View.VISIBLE
        quickPickLineItemView.visibility = View.VISIBLE
        quickPickLineItemView.setLineItemContentToBold()
        quickPickLineItemView.setLineItemViewLabel(numberOfBoards)
        quickPickLineItemView.setLineItemViewContent(quickPickText)
    }

    fun setSelectedLottoBoards(lottoBoards: ArrayList<LottoBoard>) {
        dividerView.visibility = View.VISIBLE
        lottoBoardsLinearLayout.visibility = View.VISIBLE
        lottoBoardsLinearLayout.removeAllViews()
        lottoBoards.forEach { lottoBoard ->
            val lottoBoardView = LottoDisplayBoardView(context, lottoBoard)
            lottoBoardView.layoutParams = ViewGroup.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
            lottoBoardsLinearLayout.addView(lottoBoardView)
        }
    }

    fun setWinningTicket(isWinningTicket: Boolean) {
        if (isWinningTicket) {
            ticketTitleTextView.setTextColor(ContextCompat.getColor(context, R.color.pink))
        } else {
            ticketTitleTextView.setTextColor(ContextCompat.getColor(context, R.color.graphite_light_theme_item_color))
        }
    }
}