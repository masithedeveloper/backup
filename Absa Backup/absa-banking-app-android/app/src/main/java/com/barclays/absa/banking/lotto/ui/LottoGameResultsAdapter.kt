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

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.barclays.absa.banking.R
import com.barclays.absa.banking.framework.BaseActivity
import com.barclays.absa.banking.framework.app.BMBApplication
import com.barclays.absa.banking.lotto.services.LottoDrawResult
import com.barclays.absa.utils.DateUtils
import kotlinx.android.synthetic.main.lotto_past_ticket_item.view.*
import styleguide.buttons.OptionActionButtonView
import styleguide.content.HeadingView
import styleguide.forms.ItemSelectionInterface
import styleguide.utils.extensions.addDayOfMonthSuffix
import java.util.*

class LottoGameResultsAdapter(private var lottoDrawResults: MutableList<LottoDrawResult>, var itemSelectionInterface: ItemSelectionInterface) : RecyclerView.Adapter<LottoGameResultsAdapter.LottoDrawResultsAdapter>() {

    private lateinit var context: Context
    private var sectionPositions = mutableListOf<Int>()

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        calculateSectionPositions()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LottoDrawResultsAdapter {
        context = parent.context
        val layoutInflater = LayoutInflater.from(parent.context).inflate(R.layout.lotto_past_ticket_item, parent, false)
        return LottoDrawResultsAdapter(layoutInflater)
    }

    override fun getItemCount(): Int {
        return lottoDrawResults.size
    }

    override fun onBindViewHolder(holder: LottoDrawResultsAdapter, position: Int) {
        val lottoDrawResult = lottoDrawResults[position]

        val dayOfWeek = DateUtils.formatDate(lottoDrawResult.drawDate, DateUtils.DASHED_DATE_PATTERN, "EEEE")
        holder.pastResultsOptionActionButtonView.setCaptionText("$dayOfWeek ${lottoDrawResult.drawDate.substringAfterLast("-").addDayOfMonthSuffix(BMBApplication.getApplicationLocale())}")

        if (sectionPositions.contains(position)) {
            holder.headingView.visibility = View.VISIBLE
            holder.dividerView.visibility = if (position != 0) View.VISIBLE else View.GONE
            holder.headingView.setHeadingTextView(DateUtils.formatDate(lottoDrawResult.drawDate, DateUtils.DASHED_DATE_PATTERN, "MMMM"))
        } else {
            holder.headingView.visibility = View.GONE
            holder.dividerView.visibility = View.GONE
        }

        if (lottoDrawResult.hasPurchasedTicket) {
            holder.pastResultsOptionActionButtonView.setImageViewVisibility(View.VISIBLE)
        } else {
            holder.pastResultsOptionActionButtonView.setImageViewVisibility(View.INVISIBLE)
        }

        holder.pastResultsOptionActionButtonView.setOnClickListener {
            BaseActivity.preventDoubleClick(it)
            itemSelectionInterface.onItemClicked(position)
        }
    }

    private fun calculateSectionPositions() {
        sectionPositions.clear()

        if (lottoDrawResults.isNotEmpty()) {
            var calendarCurrentMonth = DateUtils.getCalendar(lottoDrawResults[0].drawDate, DateUtils.DASHED_DATE_PATTERN)
            sectionPositions.add(0)

            for (i in 1 until lottoDrawResults.size) {
                val lottoDrawResult = lottoDrawResults[i]
                val calendar = DateUtils.getCalendar(lottoDrawResult.drawDate, DateUtils.DASHED_DATE_PATTERN)
                if (calendar.get(Calendar.MONTH) != calendarCurrentMonth.get(Calendar.MONTH)) {
                    calendarCurrentMonth = calendar
                    sectionPositions.add(i)
                }
            }
        }
    }

    class LottoDrawResultsAdapter(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val headingView: HeadingView = itemView.headingView
        val pastResultsOptionActionButtonView: OptionActionButtonView = itemView.pastResultOptionActionButtonView
        val dividerView: View = itemView.dividerView
    }
}