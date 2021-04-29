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
import com.barclays.absa.banking.R
import com.barclays.absa.banking.boundary.model.AccessPrivileges
import com.barclays.absa.banking.boundary.model.Amount
import com.barclays.absa.banking.boundary.model.CustomerProfileObject
import com.barclays.absa.banking.express.data.ClientTypePrefix
import com.barclays.absa.banking.express.data.ResidenceType
import com.barclays.absa.banking.framework.app.BMBApplication
import com.barclays.absa.banking.framework.dagger.getServiceInterface
import com.barclays.absa.banking.home.ui.IHomeCacheService
import com.barclays.absa.banking.lotto.services.LottoTicketHistory
import com.barclays.absa.banking.lotto.services.LottoTicketPurchaseDataModel
import com.barclays.absa.banking.lotto.services.dto.LottoGameRules
import com.barclays.absa.utils.DateUtils
import lotto.ticket.Board
import styleguide.forms.SelectorList
import styleguide.forms.StringItem
import styleguide.utils.extensions.convertStringToIso8601Format
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

object LottoUtils {

    const val DATE_NO_FORMAT_PATTERN = "yyyyMMdd"
    const val DATE_DISPLAY_PATTERN = "d MMMM yyyy"
    const val DATE_DISPLAY_NO_YEAR_PATTERN = "d MMMM"
    const val DATE_DISPLAY_SHORT_MONTH_NO_YEAR_PATTERN = "d MMM"
    const val DATE_DISPLAY_SHORT_MONTH_PATTERN = "d MMM yyyy"

    fun getNoAmountTicketDescription(context: Context, boardAmount: Int, drawAmount: Int, date: String): String {
        val board = if (boardAmount > 1) context.getString(R.string.lotto_boards) else context.getString(R.string.lotto_board)
        val draw = if (drawAmount > 1) context.getString(R.string.lotto_draws) else context.getString(R.string.lotto_draw)

        return context.getString(R.string.lotto_ticket_description_no_amount, boardAmount, board, drawAmount, draw, date)
    }

    fun calculateTotalAmount(currentRules: LottoGameRules, purchaseData: LottoTicketPurchaseDataModel, numberOfBoards: Int): Amount {
        var cost = currentRules.basePrice * (numberOfBoards.toBigDecimal())

        if (purchaseData.playPlus1) {
            cost += (currentRules.multiplierPrice * numberOfBoards.toBigDecimal())
            if (purchaseData.playPlus2) {
                cost += (currentRules.multiplierPrice * (numberOfBoards.toBigDecimal()))
            }
        }

        cost *= purchaseData.numberOfDraws.toBigDecimal()

        return Amount(cost.toString())
    }

    fun calculateTotalAmount(currentRules: LottoGameRules, purchaseData: LottoTicketPurchaseDataModel): Amount {
        return calculateTotalAmount(currentRules, purchaseData, purchaseData.numberOfBoards)
    }

    fun getDrawDates(currentRules: LottoGameRules, drawCount: Int): String {
        val calendarNextDrawDate = DateUtils.getCalendarObj(currentRules.nextDrawDate)

        val dateCount = if (currentRules.gameType == LottoViewModel.GAME_TYPE_POWERBALL) {
            when {
                calendarNextDrawDate.get(Calendar.DAY_OF_WEEK) == Calendar.TUESDAY -> calculateDateCount(drawCount, 3, 4)
                calendarNextDrawDate.get(Calendar.DAY_OF_WEEK) == Calendar.FRIDAY -> calculateDateCount(drawCount, 4, 3)
                else -> return ""
            }
        } else {
            when {
                calendarNextDrawDate.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY -> calculateDateCount(drawCount, 4, 3)
                calendarNextDrawDate.get(Calendar.DAY_OF_WEEK) == Calendar.WEDNESDAY -> calculateDateCount(drawCount, 3, 4)
                else -> return ""
            }
        }

        val calendarEndDrawDate = DateUtils.getCalendarObj(currentRules.nextDrawDate)
        calendarEndDrawDate.add(Calendar.DAY_OF_YEAR, dateCount)
        return getFormattedDate(calendarNextDrawDate, calendarEndDrawDate, drawCount)
    }

    fun getDrawDates(gameType: String, firstDrawDate: String, drawCount: Int): List<String> {
        val dateList = mutableListOf<String>()
        dateList.add(DateUtils.formatDate(firstDrawDate, DATE_NO_FORMAT_PATTERN, DATE_DISPLAY_SHORT_MONTH_PATTERN))
        if (drawCount > 1) {
            for (i in 2..drawCount) {
                val calendarNextDrawDate = DateUtils.getCalendarObj(convertStringToIso8601Format(firstDrawDate))
                val dateCount: Int = if (gameType == LottoViewModel.GAME_TYPE_POWERBALL) {
                    if (calendarNextDrawDate.get(Calendar.DAY_OF_WEEK) == Calendar.TUESDAY) {
                        calculateDateCount(i, 3, 4)
                    } else {
                        calculateDateCount(i, 4, 3)
                    }
                } else {
                    if (calendarNextDrawDate.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY) {
                        calculateDateCount(i, 4, 3)
                    } else {
                        calculateDateCount(i, 3, 4)
                    }
                }

                calendarNextDrawDate.add(Calendar.DAY_OF_YEAR, dateCount)
                val nextDate = DateUtils.format(calendarNextDrawDate, DATE_DISPLAY_SHORT_MONTH_PATTERN)
                dateList.add(nextDate)
            }
        }
        return dateList.toList()
    }

    private fun calculateDateCount(drawCount: Int, firstIncrement: Int, secondIncrement: Int): Int {
        var currentDrawCount = 1
        var daysToAdd = 0
        while (currentDrawCount < drawCount) {
            daysToAdd += firstIncrement
            currentDrawCount++
            if (currentDrawCount < drawCount) {
                daysToAdd += secondIncrement
                currentDrawCount++
            }
        }
        return daysToAdd
    }

    private fun getFormattedDate(calendarNextDrawDate: Calendar, calendarEndDrawDate: Calendar, drawCount: Int): String {
        if (drawCount == 1) {
            return DateUtils.format(calendarNextDrawDate, DATE_DISPLAY_NO_YEAR_PATTERN)
        }

        return if (calendarEndDrawDate.get(Calendar.MONTH) == calendarNextDrawDate.get(Calendar.MONTH)) {
            when (drawCount) {
                2 -> "${calendarNextDrawDate.get(Calendar.DAY_OF_MONTH)}, ${DateUtils.format(calendarEndDrawDate, DATE_DISPLAY_PATTERN)}"
                else -> "${calendarNextDrawDate.get(Calendar.DAY_OF_MONTH)} - ${DateUtils.format(calendarEndDrawDate, DATE_DISPLAY_PATTERN)}"
            }
        } else {
            when (drawCount) {
                2 -> "${DateUtils.format(calendarNextDrawDate, DATE_DISPLAY_NO_YEAR_PATTERN)}, ${DateUtils.format(calendarEndDrawDate, DATE_DISPLAY_PATTERN)}"
                else -> "${DateUtils.format(calendarNextDrawDate, DATE_DISPLAY_NO_YEAR_PATTERN)} - ${DateUtils.format(calendarEndDrawDate, DATE_DISPLAY_PATTERN)}"
            }
        }
    }

    fun getDrawDateFromDates(firstDrawDate: String, lastDrawDate: String, drawCount: Int): String {
        val calendarNextDrawDate = DateUtils.getCalendar(firstDrawDate, DATE_NO_FORMAT_PATTERN)
        val calendarEndDrawDate = DateUtils.getCalendar(lastDrawDate, DATE_NO_FORMAT_PATTERN)

        return getFormattedDate(calendarNextDrawDate, calendarEndDrawDate, drawCount)
    }

    fun formatDrawText(drawList: SelectorList<StringItem>, index: Int): String {
        if (drawList[index].displayValueLine2.isNullOrEmpty()) {
            return drawList[index].displayValue.toString()
        }

        val separator = drawList[index].displayValueLine2!!.firstOrNull { it == '-' || it == ',' }
        val dates = drawList[index].displayValueLine2!!.split("-", ",").toMutableList()
        dates.forEachIndexed { dateIndex, date ->
            when {
                dates.size == 1 -> dates[dateIndex] = DateUtils.formatDate(date, DATE_DISPLAY_PATTERN, DATE_DISPLAY_SHORT_MONTH_PATTERN)
                dateIndex == 0 -> dates[dateIndex] = DateUtils.formatDate(date, DATE_DISPLAY_NO_YEAR_PATTERN, DATE_DISPLAY_SHORT_MONTH_NO_YEAR_PATTERN)
                dateIndex == 1 -> dates[dateIndex] = DateUtils.formatDate(date, DATE_DISPLAY_PATTERN, DATE_DISPLAY_SHORT_MONTH_PATTERN)
            }
        }

        if (separator != null) {
            return if (separator == '-') "${drawList[index].displayValue} (${dates[0]} $separator ${dates[1]})" else "${drawList[index].displayValue} (${dates[0]}$separator ${dates[1]})"
        } else if (dates.size > 0) {
            return "${drawList[index].displayValue} (${dates[0]})"
        }

        return drawList[index].displayValue.toString()
    }

    fun getCharForNumber(i: Int): Char {
        return if (i in 0..25) (i + 65).toChar() else '?'
    }

    fun isEligibleToPlayLotto(): Boolean {
        val homeCacheService: IHomeCacheService = getServiceInterface()
        return when {
            homeCacheService.getLottoSourceAccountList().isEmpty() || AccessPrivileges.instance.isOperator -> false
            else -> true
        }
    }

    fun isSouthAfricanResident(): Boolean {
        return when (CustomerProfileObject.instance.clientType) {
            "${ClientTypePrefix.PRIVATE_INDIVIDUAL.clientTypeCode}${ResidenceType.SA_RESIDENT.residenceCode}",
            "${ClientTypePrefix.STAFF.clientTypeCode}${ResidenceType.SA_RESIDENT.residenceCode}" -> true
            else -> false
        }
    }

    fun getGameTypeBadgeDrawable(ticket: LottoTicket) = when {
        ticket.lottoPlus1 && ticket.isPowerball -> R.drawable.lotto_power_ball_plus
        ticket.isPowerball -> R.drawable.power_ball
        ticket.lottoPlus2 -> R.drawable.lotto_plus_2
        ticket.lottoPlus1 -> R.drawable.lotto_plus_1
        else -> R.drawable.lotto
    }

    fun getGameTypeBadgeDrawable(gameType: String) = when {
        gameType.equals(LottoViewModel.GAME_TYPE_POWERBALL, ignoreCase = true) -> R.drawable.power_ball
        else -> R.drawable.lotto
    }

    fun getTicketsInNextDraw(context: Context, currentGameRules: LottoGameRules, lottoTicketHistory: MutableList<LottoTicketHistory>): List<LottoTicket> {
        val nextDrawDate = DateUtils.formatDate(currentGameRules.nextDrawDate, DateUtils.DASHED_DATE_PATTERN, DATE_NO_FORMAT_PATTERN)
        val gameType = currentGameRules.gameType

        val filteredTicketHistory = lottoTicketHistory.filter {
            it.lottoGameType == gameType && nextDrawDate <= it.lastDrawDate
        }
        val ticketList = mutableListOf<LottoTicket>()
        filteredTicketHistory.forEach {
            ticketList.add(createLottoTicket(it, gameType, context))
        }
        return ticketList
    }

    fun createLottoTicket(ticketHistory: LottoTicketHistory, gameType: String, context: Context): LottoTicket {
        val ticketLottoTicket = LottoTicket()
        ticketLottoTicket.apply {
            if (ticketHistory.lottoGameType == gameType) {
                firstDrawNumber = ticketHistory.firstDrawId.toInt()
                lottoPlus1 = ticketHistory.lottoPlus1
                lottoPlus2 = ticketHistory.lottoPlus2
                quickPickInd = ticketHistory.quickPickInd
                firstDrawDate = ticketHistory.firstDrawDate
                lastDrawDate = ticketHistory.lastDrawDate
                dateRange = ticketHistory.firstDrawDate + " - " + ticketHistory.lastDrawDate
                numberOfDraws = ticketHistory.numberOfDraws
                isPowerball = ticketHistory.lottoGameType == LottoViewModel.GAME_TYPE_POWERBALL
                isWinningTicket = false
                referenceNumber = ticketHistory.lottoReferenceNumber.takeLast(20)

                boardTitle = when {
                    isPowerball && lottoPlus1 -> "${context.getString(R.string.lotto_powerball_plus)} ${context.getString(R.string.lotto_ticket)}"
                    isPowerball -> "${context.getString(R.string.lotto_powerball)} ${context.getString(R.string.lotto_ticket)}"
                    lottoPlus2 -> "${context.getString(R.string.lotto_plus_two)} ${context.getString(R.string.lotto_ticket)}"
                    lottoPlus1 -> "${context.getString(R.string.lotto_plus_one)} ${context.getString(R.string.lotto_ticket)}"
                    else -> "${context.getString(R.string.lotto)} ${context.getString(R.string.lotto_ticket)}"
                }

                val board = if (ticketHistory.lottoBoardList.size > 1) context.getString(R.string.lotto_boards) else context.getString(R.string.lotto_board)
                ticketHistory.lottoBoardList.forEachIndexed { index, boardList ->
                    val lottoBoard = Board()
                    lottoBoard.selectedNumbers.addAll(boardList.lottoBoardNumbers)
                    lottoBoard.powerBall = boardList.powerBall
                    lottoBoard.boardLetter = getCharForNumber(index)
                    boardsPlayed.add(lottoBoard)
                }

                @Suppress("ConstantConditionIf")
                val draw = if (ticketHistory.numberOfDraws > 1) context.getString(R.string.lotto_draws) else context.getString(R.string.lotto_draw)
                val drawDate = getDrawDateFromDates(ticketHistory.firstDrawDate, ticketHistory.lastDrawDate, ticketHistory.numberOfDraws)

                ticketDescription = context.getString(R.string.lotto_ticket_description, Amount(ticketHistory.purchaseAmount).toString(), ticketHistory.lottoBoardList.size.toString(), board, ticketHistory.numberOfDraws.toString(), draw, drawDate)
            }
        }
        return ticketLottoTicket
    }

    fun changeDateFormat(date: String, fromPattern: String, toPattern: String): String? {
        return try {
            val sourceDateFormat = SimpleDateFormat(fromPattern, BMBApplication.getApplicationLocale())
            val displayDateFormat = SimpleDateFormat(toPattern, BMBApplication.getApplicationLocale())
            val sourceDate = sourceDateFormat.parse(date)
            sourceDate?.let { displayDateFormat.format(it) }
        } catch (e: ParseException) {
            return null
        }
    }
}