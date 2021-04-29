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
import android.content.res.Resources
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.lifecycle.MutableLiveData
import com.barclays.absa.banking.BuildConfig
import com.barclays.absa.banking.R
import com.barclays.absa.banking.framework.BaseFragment
import com.barclays.absa.banking.lotto.services.LottoTicketHistory
import com.barclays.absa.banking.lotto.services.dto.LottoGameRules
import com.barclays.absa.utils.AnalyticsUtil
import com.barclays.absa.utils.DateUtils
import com.barclays.absa.utils.viewModel
import com.google.android.material.tabs.TabLayout
import kotlinx.android.synthetic.main.lotto_lotto_and_powerball_fragment.*
import lotto.jackpot.JackpotInfo
import lotto.jackpot.LottoJackpotFragmentPagerAdapter
import styleguide.utils.extensions.separateThousands
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.math.roundToLong

class LottoAndPowerBallFragment : BaseFragment(R.layout.lotto_lotto_and_powerball_fragment) {

    private lateinit var lottoViewModel: LottoViewModel
    private lateinit var jackpotFragmentPagerAdapter: LottoJackpotFragmentPagerAdapter

    private var gameType: String = ""
    private var isCutOff = false
    private val cutOffTime = "20:25"
    private var cutOffTimer: CountDownTimer? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        lottoViewModel = (activity as LottoActivity).viewModel()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AnalyticsUtil.trackAction("Lotto", "Lotto_LottoAndPowerBallHubScreen_LottoTabClicked")
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setToolBar(R.string.lotto_lotto_and_powerball, true)

        jackpotViewPager.isSaveEnabled = false

        gameType = if (lottoViewModel.currentGameRules.gameType.isEmpty()) LottoViewModel.GAME_TYPE_LOTTO else lottoViewModel.currentGameRules.gameType

        jackpotFragmentPagerAdapter = if (!::jackpotFragmentPagerAdapter.isInitialized) {
            LottoJackpotFragmentPagerAdapter(childFragmentManager, listOf())
        } else {
            LottoJackpotFragmentPagerAdapter(childFragmentManager, jackpotFragmentPagerAdapter.jackpots)
        }

        jackpotViewPager?.apply {
            clipToPadding = false
            pageMargin = -(15 * Resources.getSystem().displayMetrics.density).toInt()
            offscreenPageLimit = 3
            isHorizontalFadingEdgeEnabled = true
            setFadingEdgeLength(30)
            adapter = jackpotFragmentPagerAdapter
        }

        val gameRules = lottoViewModel.lottoGameRulesListLiveData.value
        gameRules?.apply {
            if (isEmpty()) {
                showGenericErrorMessageThenFinish()
            } else {
                if (gameType == LottoViewModel.GAME_TYPE_LOTTO) {
                    lottoTabLayout.getTabAt(0)?.select()
                    lottoViewModel.currentGameRules = first()
                    lottoViewModel.currentGameSubType = LottoViewModel.GAME_TYPE_LOTTO
                    playLottoButton.text = getString(R.string.lotto_play_lotto)
                } else {
                    lottoTabLayout.getTabAt(1)?.select()
                    lottoViewModel.currentGameRules = last()
                    lottoViewModel.currentGameSubType = LottoViewModel.GAME_TYPE_POWERBALL
                    playLottoButton.text = getString(R.string.lotto_play_powerball)
                }
                setupTabSelectedLayout(gameRules)
                updateViewPager()
            }
        }

        setUpObservers()
        setUpClickListeners()
        checkLottoTime()

        lottoViewModel.checkServiceAvailability(gameType)
    }

    private fun setUpClickListeners() {
        val hostActivity = activity as LottoActivity
        playLottoButton.setOnClickListener {
            hostActivity.tagLottoAndPowerBallEvent("LottoAndPowerBallHubScreen_OwnNumbersButtonClicked")
            navigate(LottoAndPowerBallFragmentDirections.actionLottoAndPowerBallFragmentToLottoNewTicketFragment())
        }

        quickPickButton.setOnClickListener {
            hostActivity.tagLottoAndPowerBallEvent("LottoAndPowerBallHubScreen_QuickPickButtonClicked")
            navigate(LottoAndPowerBallFragmentDirections.actionLottoAndPowerBallFragmentToLottoQuickPickFragment())
        }

        viewLottoResultsOptionActionButtonView.setOnClickListener {
            val fromCal = Calendar.getInstance()
            fromCal.add(Calendar.DAY_OF_YEAR, -30)

            val selectedFromDate = DateUtils.format(fromCal, DateUtils.DASHED_DATE_PATTERN)
            val selectedToDate = DateUtils.getTodaysDate()

            lottoViewModel.fetchLottoDrawResults(gameType, selectedFromDate, selectedToDate)

            lottoViewModel.lottoDrawResultsLiveData = MutableLiveData()
            lottoViewModel.lottoDrawResultsLiveData.observe(this, {
                hostActivity.tagLottoAndPowerBallEvent("LottoAndPowerBallHubScreen_ViewPastResultsButtonClicked")
                dismissProgressDialog()
                navigate(LottoAndPowerBallFragmentDirections.actionLottoAndPowerBallFragmentToLottoGameResultsFragment())
                lottoViewModel.lottoDrawResultsLiveData.removeObservers(this)
            })
        }

        purchasedTicketsOptionActionButtonView.setOnClickListener {
            hostActivity.tagLottoAndPowerBallEvent("LottoAndPowerBallHubScreen_PurchaseHistoryButtonClicked")
            navigate(LottoAndPowerBallFragmentDirections.actionLottoAndPowerBallFragmentToLottoPurchasedTicketsFragment())
        }
    }

    private fun setUpObservers() {
        lottoViewModel.serviceAvailabilityLiveData = MutableLiveData()
        lottoViewModel.serviceAvailabilityLiveData.observe(this, {
            if (it.lottoGameAvailable.equals("N", ignoreCase = true)) {
                lottoCloseTimeTextView.text = baseActivity.getString(R.string.lotto_closed, lottoViewModel.currentGameRules.gameType, it.nextStartDate)
                (activity as LottoActivity).tagLottoAndPowerBallEvent("LottoAndPowerBallHubScreen_InformationalDisplayed")
                lottoCloseTimeTextView.visibility = View.VISIBLE
                quickPickButton.visibility = View.GONE
                playLottoButton.visibility = View.GONE
                nextDrawLineItem.visibility = View.GONE
            } else {
                lottoCloseTimeTextView.visibility = View.GONE
                quickPickButton.visibility = View.VISIBLE
                playLottoButton.visibility = View.VISIBLE
                nextDrawLineItem.visibility = View.VISIBLE
            }

            if (lottoViewModel.lottoTicketHistoryLiveData.value == null) {
                lottoViewModel.fetchTicketHistory("", "")
            } else {
                dismissProgressDialog()
            }

            lottoViewModel.serviceAvailabilityLiveData.removeObservers(this)
        })

        lottoViewModel.lottoTicketHistoryLiveData = MutableLiveData()
        lottoViewModel.lottoTicketHistoryLiveData.observe(this, { ticketHistory ->
            lottoViewModel.lottoTicketHistory = ticketHistory
            lottoViewModel.lottoTicketHistoryLiveData.removeObservers(this)
            dismissProgressDialog()
            refreshTicketList()
        })

        lottoViewModel.failureResponse = MutableLiveData()
        lottoViewModel.failureResponse.observe(this, {
            dismissProgressDialog()
        })
    }

    private fun refreshTicketList() {
        if (lottoViewModel.lottoTicketHistory.size > 0) {
            val filteredTicketHistory = lottoViewModel.lottoTicketHistory.filter { it.lottoGameType == gameType }
            setUpTickets(filteredTicketHistory)
        } else {
            ticketRecyclerView.visibility = View.GONE
        }
    }

    private fun checkLottoTime() {
        val nextDrawDate = "${lottoViewModel.currentGameRules.nextDrawDate} $cutOffTime"
        val numberOfMillisTillDateTime = DateUtils.numberOfMillisTillDateTimeSA(nextDrawDate)
        val daysLeft = TimeUnit.MILLISECONDS.toDays(numberOfMillisTillDateTime)
        when {
            daysLeft >= 2 -> nextDrawLineItem.setLineItemViewContent("$daysLeft ${getString(R.string.lotto_days)}")
            daysLeft == 1L -> nextDrawLineItem.setLineItemViewContent("$daysLeft ${getString(R.string.lotto_day)}")
            else -> {
                val hoursLeft = (numberOfMillisTillDateTime / (1000f * 60f * 60f) % 24f).roundToLong()

                when {
                    hoursLeft >= 2 -> nextDrawLineItem.setLineItemViewContent("$hoursLeft ${getString(R.string.lotto_hours)}")
                    hoursLeft == 1L -> nextDrawLineItem.setLineItemViewContent("$hoursLeft ${getString(R.string.lotto_hour)}")
                    else -> {
                        startCutOffTimer(numberOfMillisTillDateTime)
                    }
                }
            }
        }
    }

    private fun showCutOffTime() {
        if (gameType == LottoViewModel.GAME_TYPE_LOTTO) {
            lottoCloseTimeTextView.text = getString(R.string.lotto_closed, getString(R.string.lotto), lottoViewModel.serviceAvailabilityLiveData.value?.nextStartDate)
        } else {
            lottoCloseTimeTextView.text = getString(R.string.lotto_closed, getString(R.string.lotto_powerball), lottoViewModel.serviceAvailabilityLiveData.value?.nextStartDate)
        }

        lottoCloseTimeTextView.visibility = View.VISIBLE
    }

    private fun cutOffTime() {
        isCutOff = true
        showCutOffTime()
        playLottoButton.visibility = View.GONE
        quickPickButton.visibility = View.GONE
        lottoCloseSoonTextView.visibility = View.GONE
    }

    private fun startCutOffTimer(millisLeft: Long) {
        if (cutOffTimer != null) {
            cutOffTimer!!.cancel()
        }

        lottoCloseSoonTextView.text = getString(R.string.lotto_close_soon, gameType, cutOffTime)
        lottoCloseSoonTextView.visibility = View.VISIBLE

        cutOffTimer = object : CountDownTimer(millisLeft, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                if (!isVisible) {
                    return
                }

                val minutesLeft = TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished)
                val secondsLeft = TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) - (TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished) * 60)

                val secondsText = if (secondsLeft > 1 || secondsLeft.toInt() == 0) "$secondsLeft ${getString(R.string.lotto_seconds)}" else "$secondsLeft ${getString(R.string.lotto_second)}"

                when {
                    minutesLeft >= 2 -> nextDrawLineItem.setLineItemViewContent("$minutesLeft ${getString(R.string.lotto_minutes)} " + secondsText)
                    minutesLeft == 1L -> nextDrawLineItem.setLineItemViewContent("$minutesLeft ${getString(R.string.lotto_minute)} " + secondsText)
                    else -> nextDrawLineItem.setLineItemViewContent(secondsText)
                }
            }

            override fun onFinish() {
                if (isVisible) {
                    nextDrawLineItem.setLineItemViewContent(getString(R.string.lotto_draw_in_progress))
                    cutOffTime()
                }
            }
        }
        cutOffTimer?.start()
    }

    private fun setupTabSelectedLayout(gameRules: MutableList<LottoGameRules>) {
        lottoTabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                tab?.apply {
                    if (position == 0) {
                        gameType = LottoViewModel.GAME_TYPE_LOTTO
                        lottoViewModel.currentGameRules = gameRules.first()
                        playLottoButton.text = getString(R.string.lotto_play_lotto)
                    } else {
                        gameType = LottoViewModel.GAME_TYPE_POWERBALL
                        lottoViewModel.currentGameRules = gameRules.last()
                        playLottoButton.text = getString(R.string.lotto_play_powerball)
                    }
                    cutOffTimer?.cancel()
                    refreshTicketList()
                    updateViewPager()

                    setUpObservers()
                    lottoViewModel.checkServiceAvailability(gameType)

                    lottoCloseSoonTextView.visibility = View.GONE
                    checkLottoTime()
                }
            }

            override fun onTabReselected(p0: TabLayout.Tab?) {}
            override fun onTabUnselected(p0: TabLayout.Tab?) {}
        })
    }

    private fun updateViewPager() {
        val lottoJackpots = mutableListOf<JackpotInfo>()
        lottoViewModel.currentGameRules.lottoJackpots.sortBy { it.drawType }
        lottoViewModel.currentGameRules.lottoJackpots.forEach {
            val jackpotInfo = JackpotInfo()
            it.apply {
                jackpotInfo.drawType = drawType
                val jackpotAmount = jackpot.toString()
                jackpotInfo.jackpot = "R ${jackpotAmount.separateThousands()}"
                jackpotInfo.jackpotType = drawType
            }
            lottoJackpots.add(jackpotInfo)
        }
        jackpotFragmentPagerAdapter.jackpots = lottoJackpots
        jackpotFragmentPagerAdapter.notifyDataSetChanged()
        pageIndicatorTabLayout.setupWithViewPager(jackpotViewPager)
        jackpotViewPager?.let { viewPager ->
            if (viewPager.adapter != null && viewPager.adapter!!.count > 0) {
                viewPager.setCurrentItem(0, false)
                viewPager.animate().scaleY(0f).scaleX(0f).setDuration(0).start()
                invalidatePageTransformer()
            }
        }
    }

    private fun invalidatePageTransformer() {
        Handler(Looper.getMainLooper()).postDelayed({
            jackpotViewPager?.let { viewPager ->
                viewPager.animate().scaleY(1f).scaleX(1f).setDuration(200).start()
                try {
                    if (viewPager.adapter != null && viewPager.adapter!!.count > 0) {
                        if (viewPager.beginFakeDrag()) {
                            viewPager.fakeDragBy(0f)
                            viewPager.endFakeDrag()
                        }
                    }
                } catch (e: Exception) {
                    if (BuildConfig.DEBUG) {
                        e.printStackTrace()
                    }
                }
            }
        }, 100)
    }

    private fun setUpTickets(lottoTicketHistory: List<LottoTicketHistory>) {
        val listOfTickets = arrayListOf<LottoTicket>()
        if (lottoTicketHistory.isNotEmpty()) {
            val todaysDate = DateUtils.getTodaysDate(LottoUtils.DATE_NO_FORMAT_PATTERN)
            val filteredLottoTicketHistory = lottoTicketHistory.filter { it.lastDrawDate >= todaysDate }.sortedBy { it.lastDrawDate }
            filteredLottoTicketHistory.forEach {
                val ticketLottoTicket = LottoTicket()
                ticketLottoTicket.apply {
                    if (it.lottoGameType == gameType) {
                        listOfTickets.add(LottoUtils.createLottoTicket(it, gameType, requireContext()))
                    }
                }
            }

            ticketRecyclerView.adapter = LottoTicketResultAdapter(listOfTickets) { position ->
                lottoViewModel.currentTicket = listOfTickets[position]
                lottoViewModel.lastFromDate = DateUtils.getTodaysDate(LottoUtils.DATE_NO_FORMAT_PATTERN)
                lottoViewModel.lastToDate = listOfTickets[position].lastDrawDate

                lottoViewModel.currentGameSubType = lottoViewModel.currentGameRules.gameType
                if (DateUtils.isFromDateMore(lottoViewModel.lastFromDate, listOfTickets[position].firstDrawDate, LottoUtils.DATE_NO_FORMAT_PATTERN)) {
                    lottoViewModel.fetchLottoDrawResults(lottoViewModel.currentGameRules.gameType, listOfTickets[position].firstDrawDate, lottoViewModel.lastFromDate)

                    lottoViewModel.lottoDrawResultsLiveData = MutableLiveData()
                    lottoViewModel.lottoDrawResultsLiveData.observe(this, {
                        lottoViewModel.lottoDrawResultsLiveData.removeObservers(this)
                        dismissProgressDialog()
                        navigate(LottoAndPowerBallFragmentDirections.actionLottoAndPowerBallFragmentToLottoTicketFragment())
                    })
                } else {
                    navigate(LottoAndPowerBallFragmentDirections.actionLottoAndPowerBallFragmentToLottoTicketFragment())
                }
            }
            ticketRecyclerView.visibility = View.VISIBLE
        } else {
            ticketRecyclerView.visibility = View.GONE
        }
    }
}