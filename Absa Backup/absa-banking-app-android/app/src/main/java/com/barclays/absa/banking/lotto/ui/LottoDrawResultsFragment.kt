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
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import androidx.lifecycle.MutableLiveData
import com.barclays.absa.banking.R
import com.barclays.absa.banking.framework.BaseFragment
import com.barclays.absa.banking.lotto.services.LottoDrawResult
import com.barclays.absa.utils.DateUtils
import com.barclays.absa.utils.viewModel
import com.google.android.material.tabs.TabLayout
import kotlinx.android.synthetic.main.lotto_draw_results_fragment.*
import lotto.LottoBallView
import lotto.jackpot.*
import lotto.ticket.TicketView
import styleguide.content.LineItemView
import styleguide.utils.extensions.toRandAmount

class LottoDrawResultsFragment : BaseFragment(R.layout.lotto_draw_results_fragment) {

    private lateinit var lottoViewModel: LottoViewModel
    private lateinit var drawResult: LottoDrawResult

    private var gameSubType = LottoViewModel.GAME_TYPE_LOTTO
    private var divCounter = 1

    override fun onAttach(context: Context) {
        super.onAttach(context)
        lottoViewModel = (context as LottoActivity).viewModel()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        lottoViewModel.currentGameSubType?.let {
            gameSubType = it
        }

        drawResult = lottoViewModel.currentDrawResult

        setupTabLayout()

        val gameTitle = if (lottoViewModel.currentGameRules.gameType == LottoViewModel.GAME_TYPE_LOTTO) getString(R.string.lotto) else getString(R.string.lotto_powerball)

        setToolBar(getString(R.string.lotto_results_date, gameTitle, DateUtils.formatDate(drawResult.drawDate, DateUtils.DASHED_DATE_PATTERN, LottoUtils.DATE_DISPLAY_NO_YEAR_PATTERN)), true)

        drawDatePrimaryContentAndLabelView.setContentText(DateUtils.formatDate(drawResult.drawDate, DateUtils.DASHED_DATE_PATTERN, LottoUtils.DATE_DISPLAY_PATTERN))
        drawDatePrimaryContentAndLabelView.setLabelText(getString(R.string.lotto_draw_no, drawResult.drawId))

        setUpDrawResultsBoard()
        setUpTickets()
        setUpWinnerInformation()
    }

    private fun setUpLottoDrawResultsObserver() {
        if (!lottoViewModel.lottoDrawResultsLiveData.hasActiveObservers()) {
            lottoViewModel.lottoDrawResultsLiveData.observe(this, { drawResultsResponse ->
                dismissProgressDialog()
                if (drawResultsResponse.lottoDrawResults.isNotEmpty()) {
                    drawResult = drawResultsResponse.lottoDrawResults.find { it.drawDate == lottoViewModel.currentDrawResult.drawDate } ?: lottoViewModel.currentDrawResult
                    drawResult.hasPurchasedTicket = true
                    drawResult.purchasedTickets.clear()

                    val normalizedDrawDate = DateUtils.formatDate(drawResult.drawDate, DateUtils.DASHED_DATE_PATTERN, LottoUtils.DATE_NO_FORMAT_PATTERN)

                    lottoViewModel.lottoTicketHistory.filter { it.lottoGameType == lottoViewModel.currentGameRules.gameType }.forEach { ticket ->
                        if (normalizedDrawDate == ticket.firstDrawDate || normalizedDrawDate == ticket.lastDrawDate) {
                            val isValidTicket = when {
                                drawResult.drawType == DRAW_TYPE_LOTTO_PLUS1 && !ticket.lottoPlus1 -> false
                                drawResult.drawType == DRAW_TYPE_LOTTO_PLUS2 && !ticket.lottoPlus2 -> false
                                drawResult.drawType == DRAW_TYPE_POWERBALL_PLUS && !ticket.lottoPlus1 -> false
                                else -> true
                            }

                            if (isValidTicket) {
                                drawResult.purchasedTickets.add(LottoUtils.createLottoTicket(ticket, lottoViewModel.currentGameRules.gameType, baseActivity))
                            }
                        }
                    }
                }

                drawDatePrimaryContentAndLabelView.setContentText(DateUtils.formatDate(drawResult.drawDate, DateUtils.DASHED_DATE_PATTERN, LottoUtils.DATE_DISPLAY_PATTERN))
                drawDatePrimaryContentAndLabelView.setLabelText(getString(R.string.lotto_draw_no, drawResult.drawId))

                setUpDrawResultsBoard()
                setUpTickets()
                setUpWinnerInformation()

                lottoViewModel.currentDrawResult = drawResult
                lottoViewModel.lottoDrawResultsLiveData.removeObservers(this)
            })
        }
    }

    private fun setUpWinnerInformation() {
        rolloverAmountLineItemView.setLineItemViewContent(drawResult.rolloverAmount.toRandAmount())
        totalPoolLineItemView.setLineItemViewContent(drawResult.totalPrizePool.toRandAmount())

        divCounter = 1
        setDivContent(div1LineItemView, drawResult.div1NoOfWinners, drawResult.div1Payout)
        setDivContent(div2LineItemView, drawResult.div2NoOfWinners, drawResult.div2Payout)
        setDivContent(div3LineItemView, drawResult.div3NoOfWinners, drawResult.div3Payout)
        setDivContent(div4LineItemView, drawResult.div4NoOfWinners, drawResult.div4Payout)
        setDivContent(div5LineItemView, drawResult.div5NoOfWinners, drawResult.div5Payout)
        setDivContent(div6LineItemView, drawResult.div6NoOfWinners, drawResult.div6Payout)
        setDivContent(div7LineItemView, drawResult.div7NoOfWinners, drawResult.div7Payout)
        setDivContent(div8LineItemView, drawResult.div8NoOfWinners, drawResult.div8Payout)

        if (lottoViewModel.currentGameRules.gameType == LottoViewModel.GAME_TYPE_POWERBALL) {
            lottoWinnersPrimaryContentAndLabelView.setContentText(getString(R.string.lotto_powerball_winners))
            div9LineItemView.visibility = View.VISIBLE
            setDivContent(div9LineItemView, drawResult.div9NoOfWinners, drawResult.div9Payout)
        } else {
            lottoWinnersPrimaryContentAndLabelView.setContentText(getString(R.string.lotto_winners))
            div9LineItemView.visibility = View.GONE
        }
    }

    private fun setDivContent(lineItem: LineItemView, winners: String, payout: String) {
        val divLabel = if (lottoViewModel.currentGameRules.gameType == LottoViewModel.GAME_TYPE_POWERBALL) {
            getPowerBallDivLabel(divCounter)
        } else {
            getLottoDivLabel(divCounter)
        }

        lineItem.setLineItemViewLabel(divLabel)
        when (winners) {
            "0" -> lineItem.setLineItemViewContent(getString(R.string.lotto_no_winner))
            "1" -> lineItem.setLineItemViewContent(getString(R.string.lotto_one_winner, winners, payout.toRandAmount()))
            else -> lineItem.setLineItemViewContent(getString(R.string.lotto_multiple_winners, winners, payout.toRandAmount()))
        }

        divCounter++
    }

    private fun getPowerBallDivLabel(divCounter: Int): String {
        val currentMatch = 5 - ((divCounter - 1) / 2)

        var divLabel = StringBuilder(getString(R.string.lotto_div_label, divCounter, currentMatch))
        when {
            divCounter < 8 && divCounter % 2 == 1 -> divLabel.append(" ").append(getString(R.string.lotto_plus_powerball))
            divCounter == 8 -> divLabel = StringBuilder(getString(R.string.lotto_div_label, divCounter, 1)).append(" ").append(getString(R.string.lotto_plus_powerball))
            divCounter == 9 -> divLabel = StringBuilder(getString(R.string.lotto_div_label, divCounter, getString(R.string.lotto_powerball)))
        }
        return divLabel.toString()
    }

    private fun getLottoDivLabel(divCounter: Int): String {
        val currentMatch = 6 - (divCounter / 2)
        val divLabel = StringBuilder(getString(R.string.lotto_div_label, divCounter, currentMatch))

        if (divCounter % 2 == 0) {
            divLabel.append(" ").append(getString(R.string.lotto_plus_bonus))
        }

        return divLabel.toString()
    }

    private fun setUpTickets() {
        if (drawResult.purchasedTickets.size > 0) {
            entriesDividerView.visibility = View.VISIBLE
            drawTicketsLinearLayout.visibility = View.VISIBLE
            drawTicketsPrimaryContentAndLabelView.visibility = View.VISIBLE
            drawTicketsPrimaryContentAndLabelView.setContentText(getString(R.string.lotto_active_lotto_entries, drawResult.purchasedTickets.size.toString()))
            addTicketViews()
        } else {
            drawTicketsLinearLayout.visibility = View.GONE
            entriesDividerView.visibility = View.GONE
            drawTicketsPrimaryContentAndLabelView.visibility = View.GONE
        }
    }

    private fun setUpDrawResultsBoard() {
        val drawResultLinearLayout = drawResultsView as LinearLayout
        drawResultLinearLayout.removeAllViews()
        drawResult.lottoResultNumbers.forEach {
            val lottoBallView = createLottoBall(it, false)
            drawResultLinearLayout.addView(lottoBallView)
        }

        if (drawResult.bonusOrPowerball.isNotEmpty()) {
            val lottoBallView = createLottoBall(drawResult.bonusOrPowerball.toInt(), true)
            drawResultLinearLayout.addView(lottoBallView)
        }
    }

    private fun addTicketViews() {
        val purchasedTickets = drawResult.purchasedTickets
        drawTicketsLinearLayout.removeAllViews()
        purchasedTickets.forEach { lottoTicket ->
            val ticketView = TicketView(requireContext())
            val layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply {
                topMargin = resources.getDimensionPixelSize(liblotto.lotto.R.dimen.normal_space)
                marginStart = resources.getDimensionPixelSize(liblotto.lotto.R.dimen.medium_space)
                marginEnd = resources.getDimensionPixelSize(liblotto.lotto.R.dimen.medium_space)
            }
            ticketView.layoutParams = layoutParams

            val ticketTitle = if (lottoTicket.isPowerball) {
                if (lottoTicket.lottoPlus1) getString(R.string.lotto_powerball_plus) else getString(R.string.lotto_powerball)
            } else {
                if (lottoTicket.lottoPlus2) getString(R.string.lotto_plus_two) else if (lottoTicket.lottoPlus1) getString(R.string.lotto_plus_one) else getString(R.string.lotto)
            }

            ticketView.setTicketTitle(ticketTitle)
            ticketView.setTicketBoardsDescription(lottoTicket.ticketDescription)

            ticketView.setOnClickListener {
                lottoViewModel.currentTicket = lottoTicket
                navigate(LottoDrawResultsFragmentDirections.actionLottoDrawResultsFragmentToLottoTicketFragment())
            }

            drawTicketsLinearLayout.addView(ticketView)
        }
    }

    private fun createLottoBall(ballValue: Int, isBonusBall: Boolean): LottoBallView {
        val lottoBallView = LottoBallView(requireContext())
        lottoBallView.apply {
            val layoutParam = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply {
                if (isBonusBall) {
                    marginStart = resources.getDimensionPixelOffset(R.dimen.small_space)
                    marginEnd = resources.getDimensionPixelOffset(R.dimen.tiny_space)
                }
                weight = 1f
            }
            layoutParams = layoutParam
            isPowerBall = isBonusBall
            colorEnabled = true
            setValue(ballValue)
        }

        return lottoBallView
    }

    private fun setupTabLayout() {
        val selectedTabPosition = when (lottoViewModel.currentGameRules.gameType) {
            LottoViewModel.GAME_TYPE_LOTTO -> if (gameSubType == LottoViewModel.GAME_SUBTYPE_LOTTO_PLUS2) 2 else if (gameSubType == LottoViewModel.GAME_SUBTYPE_LOTTO_PLUS1) 1 else 0
            else -> if (gameSubType == LottoViewModel.GAME_SUBTYPE_POWERBALL_PLUS) 1 else 0
        }

        if (lottoViewModel.currentGameRules.gameType == LottoViewModel.GAME_TYPE_POWERBALL) {
            lottoTabLayout.removeAllTabs()
            val powerballTab = lottoTabLayout.newTab()
            powerballTab.text = getString(R.string.lotto_powerball)
            powerballTab.contentDescription = LottoViewModel.GAME_TYPE_POWERBALL
            lottoTabLayout.addTab(powerballTab)
            val powerballPlusTab = lottoTabLayout.newTab()
            powerballPlusTab.text = getString(R.string.lotto_powerball_plus)
            powerballPlusTab.contentDescription = LottoViewModel.GAME_SUBTYPE_POWERBALL_PLUS
            lottoTabLayout.addTab(powerballPlusTab)
        }

        lottoTabLayout.getTabAt(selectedTabPosition)?.select()

        lottoTabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                tab?.apply {
                    var drawType = ""

                    val currentGameType = lottoViewModel.currentGameRules.gameType
                    if (currentGameType == LottoViewModel.GAME_TYPE_LOTTO) {
                        gameSubType = when (position) {
                            1 -> {
                                drawType = DRAW_TYPE_LOTTO_PLUS1
                                LottoViewModel.GAME_SUBTYPE_LOTTO_PLUS1
                            }
                            2 -> {
                                drawType = DRAW_TYPE_LOTTO_PLUS2
                                LottoViewModel.GAME_SUBTYPE_LOTTO_PLUS2
                            }
                            else -> {
                                drawType = DRAW_TYPE_LOTTO
                                LottoViewModel.GAME_TYPE_LOTTO
                            }
                        }

                    } else if (currentGameType == LottoViewModel.GAME_TYPE_POWERBALL) {
                        gameSubType = if (position == 0) {
                            drawType = DRAW_TYPE_POWERBALL
                            LottoViewModel.GAME_TYPE_POWERBALL
                        } else {
                            drawType = DRAW_TYPE_POWERBALL_PLUS
                            LottoViewModel.GAME_SUBTYPE_POWERBALL_PLUS
                        }
                    }

                    lottoViewModel.currentGameSubType = gameSubType
                    lottoViewModel.lottoDrawResultsLiveData = MutableLiveData()
                    setUpLottoDrawResultsObserver()

                    lottoViewModel.fetchLastLottoDrawResultsRange(drawType)
                }
            }

            override fun onTabReselected(p0: TabLayout.Tab?) {}
            override fun onTabUnselected(p0: TabLayout.Tab?) {}
        })
    }
}