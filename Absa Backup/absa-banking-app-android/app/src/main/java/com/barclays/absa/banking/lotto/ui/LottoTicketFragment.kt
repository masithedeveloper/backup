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
import com.barclays.absa.banking.framework.app.BMBApplication
import com.barclays.absa.banking.lotto.services.LottoBoardNumbers
import com.barclays.absa.banking.lotto.services.LottoDrawResult
import com.barclays.absa.banking.lotto.ui.LottoUtils.DATE_DISPLAY_SHORT_MONTH_PATTERN
import com.barclays.absa.banking.lotto.ui.LottoUtils.DATE_NO_FORMAT_PATTERN
import com.barclays.absa.utils.DateUtils
import com.barclays.absa.utils.viewModel
import com.google.android.material.tabs.TabLayout
import kotlinx.android.synthetic.main.lotto_ticket_fragment.*
import lotto.LottoBallView
import lotto.LottoBoard
import styleguide.forms.SelectorList
import styleguide.forms.StringItem
import styleguide.utils.extensions.addDayOfMonthSuffix
import styleguide.utils.extensions.removeSpaces
import styleguide.utils.extensions.separateThousands

class LottoTicketFragment : BaseFragment(R.layout.lotto_ticket_fragment) {

    private lateinit var lottoViewModel: LottoViewModel
    private lateinit var drawList: SelectorList<StringItem>
    private lateinit var lottoTicketAdapter: LottoTicketAdapter

    private var boardList = mutableListOf<LottoBoard>()
    private var gameSubType = LottoViewModel.GAME_TYPE_LOTTO
    private var originalResults = mutableListOf<LottoDrawResult>()
    private var filteredDrawResults: MutableList<LottoDrawResult> = mutableListOf()
    private var currentDrawDate = ""
    private var selectedIndex = 0

    override fun onAttach(context: Context) {
        super.onAttach(context)
        lottoViewModel = (context as LottoActivity).viewModel()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        lottoViewModel.currentGameSubType?.let {
            gameSubType = it
        }

        if (lottoViewModel.currentTicket == null) {
            val drawTickets = LottoUtils.getTicketsInNextDraw(baseActivity, lottoViewModel.currentGameRules, lottoViewModel.lottoTicketHistory)
            if (drawTickets.isNotEmpty()) {
                lottoViewModel.currentTicket = drawTickets[0]
            }
        }

        setupTabLayout()

        lottoViewModel.currentTicket?.apply {
            when {
                isPowerball && lottoPlus1 -> {
                    setToolBar("${getString(R.string.lotto_powerball_plus)} ${getString(R.string.lotto_ticket)}", true)
                    ticketBoardsContentAndLabelView.setLabelText(getString(R.string.lotto_lotto_ticket, getString(R.string.lotto_powerball_plus)))
                }
                isPowerball -> {
                    setToolBar("${getString(R.string.lotto_powerball)} ${getString(R.string.lotto_ticket)}", true)
                    ticketBoardsContentAndLabelView.setLabelText(getString(R.string.lotto_lotto_ticket, getString(R.string.lotto_powerball)))
                }
                lottoPlus2 -> {
                    setToolBar("${getString(R.string.lotto_plus_two)} ${getString(R.string.lotto_ticket)}")
                    ticketBoardsContentAndLabelView.setLabelText(getString(R.string.lotto_lotto_ticket, getString(R.string.lotto_plus_two)))
                }
                lottoPlus1 -> {
                    setToolBar("${getString(R.string.lotto_plus_one)} ${getString(R.string.lotto_ticket)}")
                    ticketBoardsContentAndLabelView.setLabelText(getString(R.string.lotto_lotto_ticket, getString(R.string.lotto_plus_one)))
                }
                else -> {
                    setToolBar("${getString(R.string.lotto)} ${getString(R.string.lotto_ticket)}")
                    ticketBoardsContentAndLabelView.setLabelText(getString(R.string.lotto_lotto_ticket, getString(R.string.lotto)))
                }
            }

            ticketBoardsContentAndLabelView.setContentText(getString(R.string.lotto_number_of_boards, boardsPlayed.size))
            setupDrawList()

            boardsPlayed.forEach { board ->
                board.selectedNumbers.forEach {
                    if (it == 0) {
                        board.selectedNumbers.remove(it)
                    }
                }
            }
        }

        setupJackpotView(gameSubType)
        boardRecyclerView.isNestedScrollingEnabled = false
        setupBoards()

        if (lottoViewModel.serviceAvailabilityLiveData.value?.lottoGameAvailable != "Y") {
            replayTicketButton.visibility = View.GONE
        }

        lottoViewModel.lottoDrawResultsLiveData.value?.let {
            originalResults.clear()
            originalResults.addAll(it.lottoDrawResults)
        }

        updateDrawResults()

        replayTicketButton.setOnClickListener {
            lottoViewModel.currentTicket?.let { currentTicket ->
                lottoViewModel.purchaseData.apply {
                    lottoGameType = lottoViewModel.currentGameRules.gameType
                    numberOfDraws = currentTicket.numberOfDraws
                    numberOfBoards = currentTicket.boardsPlayed.size
                    playPlus1 = currentTicket.lottoPlus1
                    playPlus2 = currentTicket.lottoPlus2

                    val lottoBoardList = mutableListOf<LottoBoardNumbers>()

                    currentTicket.boardsPlayed.forEach { board ->
                        LottoBoardNumbers().apply {
                            lottoBoardNumbers = board.selectedNumbers

                            if (isPowerBall()) {
                                powerBall = board.powerBall
                            }
                            lottoBoardList.add(this)
                        }
                    }

                    lottoBoardNumbers = lottoBoardList
                }
            }

            navigate(LottoTicketFragmentDirections.actionLottoTicketFragmentToLottoPlayTheLottoFragment())
            (activity as LottoActivity).tagLottoAndPowerBallEvent("ViewTicketDetailScreen_ReplayTicketClicked")
        }

        ticketReferenceContentAndLabelView.setContentText(lottoViewModel.currentTicket?.referenceNumber)
    }

    private fun isPowerBall(): Boolean = lottoViewModel.currentGameRules.gameType == LottoViewModel.GAME_TYPE_POWERBALL

    private fun setupTabLayout() {
        val selectedTabIndex = when (lottoViewModel.currentGameRules.gameType) {
            LottoViewModel.GAME_TYPE_LOTTO -> if (gameSubType == LottoViewModel.GAME_SUBTYPE_LOTTO_PLUS2) 2 else if (gameSubType == LottoViewModel.GAME_SUBTYPE_LOTTO_PLUS1) 1 else 0
            else -> if (gameSubType == LottoViewModel.GAME_SUBTYPE_POWERBALL_PLUS) 1 else 0
        }

        if (lottoViewModel.currentGameRules.gameType == LottoViewModel.GAME_TYPE_POWERBALL) {
            lottoTabLayout.removeAllTabs()
            val powerballTab = lottoTabLayout.newTab()
            powerballTab.text = getString(R.string.lotto_powerball)
            powerballTab.contentDescription = LottoViewModel.GAME_TYPE_POWERBALL
            lottoTabLayout.addTab(powerballTab)
            lottoViewModel.currentTicket?.apply {
                if (lottoPlus1) {
                    val powerballPlusTab = lottoTabLayout.newTab()
                    powerballPlusTab.text = getString(R.string.lotto_powerball_plus)
                    powerballPlusTab.contentDescription = LottoViewModel.GAME_SUBTYPE_POWERBALL_PLUS
                    lottoTabLayout.addTab(powerballPlusTab)
                } else {
                    lottoTabLayout.visibility = View.GONE
                }
            }
        } else {
            lottoViewModel.currentTicket?.apply {
                if (!lottoPlus1) {
                    lottoTabLayout.visibility = View.GONE
                } else if (!lottoPlus2 && lottoTabLayout.tabCount > 2) {
                    lottoTabLayout.removeTabAt(2)
                }
            }
        }

        lottoTabLayout.getTabAt(selectedTabIndex)?.select()

        lottoTabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                tab?.apply {
                    val currentGameType = lottoViewModel.currentGameRules.gameType
                    if (currentGameType == LottoViewModel.GAME_TYPE_LOTTO) {
                        gameSubType = when (position) {
                            1 -> LottoViewModel.GAME_SUBTYPE_LOTTO_PLUS1
                            2 -> LottoViewModel.GAME_SUBTYPE_LOTTO_PLUS2
                            else -> LottoViewModel.GAME_TYPE_LOTTO
                        }
                        setupJackpotView(currentGameType)
                    } else if (currentGameType == LottoViewModel.GAME_TYPE_POWERBALL) {
                        gameSubType = if (position == 0) {
                            LottoViewModel.GAME_TYPE_POWERBALL
                        } else {
                            LottoViewModel.GAME_SUBTYPE_POWERBALL_PLUS
                        }
                    }
                    lottoViewModel.currentGameSubType = gameSubType
                    getDrawResults()
                }
            }

            override fun onTabReselected(p0: TabLayout.Tab?) {}
            override fun onTabUnselected(p0: TabLayout.Tab?) {}
        })
    }

    private fun getDrawResults() {
        lottoViewModel.currentTicket?.apply {
            attachObserver()
            when {
                DateUtils.isFromDateMore(firstDrawDate, DateUtils.getTodaysDate(DATE_NO_FORMAT_PATTERN), DATE_NO_FORMAT_PATTERN) -> setUpDrawResultsBoard()
                DateUtils.isFromDateMore(lastDrawDate, DateUtils.getTodaysDate(DATE_NO_FORMAT_PATTERN), DATE_NO_FORMAT_PATTERN) -> lottoViewModel.fetchLottoDrawResults(gameSubType, firstDrawDate, DateUtils.getTodaysDate(DATE_NO_FORMAT_PATTERN))
                else -> lottoViewModel.fetchLastLottoDrawResultsRange(gameSubType)
            }
        }
    }

    private fun attachObserver() {
        lottoViewModel.lottoDrawResultsLiveData = MutableLiveData()
        lottoViewModel.lottoDrawResultsLiveData.observe(this, { results ->
            lottoViewModel.lottoDrawResultsLiveData.removeObservers(this)
            originalResults.clear()
            originalResults.addAll(results.lottoDrawResults)
            originalResults.forEach { it.purchasedTickets = lottoViewModel.currentDrawResult.purchasedTickets }
            updateDrawResults()
            dismissProgressDialog()
        })
    }

    private fun updateDrawResults() {
        filteredDrawResults.clear()

        val filteredDrawResults = originalResults.filter { it.drawType == gameSubType && LottoUtils.changeDateFormat(it.drawDate, DateUtils.DASHED_DATE_PATTERN, DATE_DISPLAY_SHORT_MONTH_PATTERN) == currentDrawDate }.toMutableList()
        if (filteredDrawResults.isNotEmpty()) {
            val currentDrawResults = filteredDrawResults[0]
            lottoViewModel.currentDrawResult = currentDrawResults
            val drawResultLinearLayout = drawResultsView as LinearLayout
            drawResultLinearLayout.removeAllViews()

            val drawList = mutableListOf<Int>()
            currentDrawResults.lottoResultNumbers.sorted().forEach {
                drawResultLinearLayout.addView(createLottoBall(it, false))
                drawList.add(it)
            }

            drawList.add(currentDrawResults.bonusOrPowerball.toInt())
            drawResultLinearLayout.addView(createLottoBall(currentDrawResults.bonusOrPowerball.toInt(), true))

            lottoTicketAdapter.setDrawResults(drawList)
            drawResultTextView.text = getText(R.string.lotto_draw_results)
        } else {
            setUpDrawResultsBoard()
            mutableListOf<Int>().apply {
                add(0)
                lottoTicketAdapter.setDrawResults(this)
            }
            drawResultTextView.text = getText(R.string.lotto_upcoming_draw_results)
        }

        lottoTicketAdapter.notifyDataSetChanged()
    }

    private fun setupBoards() {
        lottoViewModel.currentTicket?.apply {
            boardList.clear()
            boardsPlayed.forEach {
                val board = LottoBoard()
                board.ballList = it.selectedNumbers
                if (isPowerBall()) {
                    board.powerBall = it.powerBall
                }
                board.boardLetter = it.boardLetter
                boardList.add(board)
                board.isColorEnabled = false
            }
        }

        lottoTicketAdapter = LottoTicketAdapter(boardList)
        lottoTicketAdapter.setPowerBall(isPowerBall())
        boardRecyclerView.adapter = lottoTicketAdapter
    }

    private fun setUpDrawResultsBoard() {
        val drawResultLinearLayout = drawResultsView as LinearLayout
        drawResultLinearLayout.removeAllViews()
        val numberOfBalls = lottoViewModel.currentGameRules.maximumPrimaryBallNumber
        for (i in 1..numberOfBalls) {
            drawResultLinearLayout.addView(createUnknownBall(false))
        }
        drawResultLinearLayout.addView(createUnknownBall(true))
    }

    private fun setupJackpotView(currentGameType: String?) {
        val jackpotItem = lottoViewModel.currentGameRules.lottoJackpots.find { it.drawType.equals(currentGameType.removeSpaces(), ignoreCase = true) }
        currentGameType?.let {
            jackpotView.setLottoBadgeImageView(LottoUtils.getGameTypeBadgeDrawable(it))
        }
        jackpotItem?.jackpot?.let {
            jackpotView.setJackpotAmount(it.toString().separateThousands())
        }
        lottoViewModel.currentTicket?.let {
            jackpotView.setLottoBadgeImageView(LottoUtils.getGameTypeBadgeDrawable(it))
        }
    }

    private fun setupDrawList() {
        drawList = SelectorList()
        var drawDates = mutableListOf<String>()
        val currentTicket = lottoViewModel.currentTicket

        lottoViewModel.currentGameRules.gameType.let {
            currentTicket?.apply {
                drawDates = LottoUtils.getDrawDates(it, firstDrawDate, numberOfDraws) as MutableList<String>
            }
        }

        drawDates.forEachIndexed { index, drawDate ->
            if (drawDate == DateUtils.formatDate(lottoViewModel.currentDrawResult.drawDate, DateUtils.DASHED_DATE_PATTERN, DATE_DISPLAY_SHORT_MONTH_PATTERN)) {
                selectedIndex = index
                return@forEachIndexed
            }
        }

        currentTicket?.apply {
            currentDrawDate = drawDates[selectedIndex]
            drawInformationContentAndLabelView.setContentText(getString(R.string.lotto_draw_no, firstDrawNumber + selectedIndex))
            drawDateRoundedSelectorView.setText(getString(R.string.lotto_purchase_draw_number, selectedIndex + 1, currentDrawDate))
            for (i in 0 until numberOfDraws) {
                if (drawDates.isNotEmpty()) {
                    drawList.add(StringItem("${(i + 1).toString().addDayOfMonthSuffix(BMBApplication.getApplicationLocale())} ${getString(R.string.lotto_draw)}", drawDates[i]))
                }
            }
        }

        drawDateRoundedSelectorView.setList(drawList, getString(R.string.lotto_select_draw))
        drawDateRoundedSelectorView.setItemSelectionInterface {
            updateSelectedDrawInformation(it)
            updateDrawResults()
        }

        drawDateRoundedSelectorView
    }

    private fun updateSelectedDrawInformation(selectedIndex: Int) {
        this.selectedIndex = selectedIndex
        var drawDates = mutableListOf<String>()
        lottoViewModel.currentGameRules.gameType.let {
            lottoViewModel.currentTicket?.apply {
                drawDates = LottoUtils.getDrawDates(it, firstDrawDate, numberOfDraws) as MutableList<String>
            }
        }

        currentDrawDate = drawDates[selectedIndex]
        drawDateRoundedSelectorView.setText(getString(R.string.lotto_purchase_draw_number, selectedIndex + 1, currentDrawDate))
        lottoViewModel.currentTicket?.let {
            drawInformationContentAndLabelView.setContentText(getString(R.string.lotto_draw_no, "${it.firstDrawNumber + selectedIndex}"))
        }
    }

    private fun createLottoBall(ballValue: Int, isBonusBall: Boolean): LottoBallView {
        return createLottoBall(ballValue, isBonusBall, false)
    }

    private fun createUnknownBall(isBonusBall: Boolean): LottoBallView {
        return createLottoBall(-1, isBonusBall, true)
    }

    private fun createLottoBall(ballValue: Int, isBonusBall: Boolean, isUnknownBall: Boolean): LottoBallView {
        val lottoBallView = LottoBallView(requireContext())
        lottoBallView.apply {
            val layoutParam = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply {
                if (isBonusBall) {
                    marginStart = resources.getDimensionPixelOffset(R.dimen.tiny_space)
                    marginEnd = resources.getDimensionPixelOffset(R.dimen.tiny_space)
                }
                weight = 1f
            }
            layoutParams = layoutParam
            colorEnabled = !isUnknownBall
            isPowerBall = isBonusBall
            if (isUnknownBall) {
                setUnknownBall()
            } else {
                setValue(ballValue)
            }
            enlargeBall()
        }

        return lottoBallView
    }
}