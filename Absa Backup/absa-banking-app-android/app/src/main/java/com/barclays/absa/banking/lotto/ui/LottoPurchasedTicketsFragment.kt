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
import androidx.lifecycle.MutableLiveData
import com.barclays.absa.banking.R
import com.barclays.absa.banking.card.ui.creditCard.hub.FilteringOptions
import com.barclays.absa.banking.card.ui.creditCard.hub.TransactionHistoryFilterFragment
import com.barclays.absa.banking.framework.BaseFragment
import com.barclays.absa.banking.lotto.services.LottoTicketHistory
import com.barclays.absa.utils.DateUtils
import com.barclays.absa.utils.viewModel
import com.google.android.material.tabs.TabLayout
import kotlinx.android.synthetic.main.lotto_purchased_tickets_fragment.*
import java.util.*

class LottoPurchasedTicketsFragment : BaseFragment(R.layout.lotto_purchased_tickets_fragment), TransactionHistoryFilterFragment.UpdateFilteringOptions {

    private lateinit var lottoViewModel: LottoViewModel
    private var lottoTicketHistoryList = mutableListOf<LottoTicketHistory>()
    private var filterOptions = FilteringOptions()

    override fun onAttach(context: Context) {
        super.onAttach(context)
        lottoViewModel = (context as LottoActivity).viewModel()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setTitle()
        setupTabLayout()
        setUpFilterView()

        filterOptions.shouldShowTransactionFilter = false
        lottoViewModel.lottoTicketHistoryLiveData.observe(viewLifecycleOwner, {
            dismissProgressDialog()
            lottoTicketHistoryList = it
            setUpTickets()
        })

        (activity as LottoActivity).tagLottoAndPowerBallEvent("PurchasedTicketsScreen_ScreenDisplayed")

        lottoViewModel.lottoDrawResultsLiveData = MutableLiveData()
        lottoViewModel.lottoDrawResultsLiveData.observe(viewLifecycleOwner, {
            dismissProgressDialog()
            navigate(LottoPurchasedTicketsFragmentDirections.actionLottoPurchasedTicketsFragmentToLottoTicketFragment())
            lottoViewModel.lottoDrawResultsLiveData.removeObservers(this)
        })
    }

    private fun setTitle() {
        if (lottoViewModel.currentGameRules.gameType == LottoViewModel.GAME_TYPE_LOTTO) {
            setToolBar(R.string.lotto_lotto_tickets)
        } else {
            setToolBar(R.string.lotto_powerball_ticket, true)
        }
    }

    private fun setUpFilterView() {
        val fromCal = Calendar.getInstance()
        fromCal.add(Calendar.DAY_OF_YEAR, -7)

        val selectedFromDate = DateUtils.format(fromCal, DateUtils.DASHED_DATE_PATTERN)
        val selectedToDate = DateUtils.getTodaysDate()

        val formatDate = DateUtils.formatDate(selectedFromDate, DateUtils.DASHED_DATE_PATTERN, DateUtils.DATE_DISPLAY_PATTERN) + " - " + DateUtils.formatDate(selectedToDate, DateUtils.DASHED_DATE_PATTERN, DateUtils.DATE_DISPLAY_PATTERN)

        ticketFilterAndSearchView.setSearchText(formatDate)
        ticketFilterAndSearchView.hideSearchView()
        ticketFilterAndSearchView.setOnCalendarLayoutClickListener { filterTransactionsUsingDateRange(selectedFromDate, selectedToDate) }
    }

    private fun filterTransactionsUsingDateRange(fromDate: String, toDate: String) {
        filterOptions.filterType = ""
        filterOptions.fromDate = fromDate
        filterOptions.toDate = toDate
        showBottomBarMenu();
    }

    private fun setUpTickets() {
        val listOfTickets = arrayListOf<LottoTicket>()

        lottoTicketHistoryList.forEach {
            if (lottoViewModel.currentGameRules.gameType == it.lottoGameType) {
                listOfTickets.add(LottoUtils.createLottoTicket(it, lottoViewModel.currentGameRules.gameType, baseActivity))
            }
        }

        lottoResultsRecyclerView.adapter = LottoTicketResultAdapter(listOfTickets) { position ->
            val currentTicket = listOfTickets[position]
            lottoViewModel.currentTicket = currentTicket
            currentTicket.apply {
                lottoViewModel.currentGameSubType = ""
                if (isPowerball) {
                    lottoViewModel.currentGameSubType = if (lottoPlus1) {
                        LottoViewModel.GAME_SUBTYPE_POWERBALL_PLUS
                    } else {
                        LottoViewModel.GAME_TYPE_POWERBALL
                    }
                } else {
                    lottoViewModel.currentGameSubType = when {
                        lottoPlus2 -> LottoViewModel.GAME_SUBTYPE_LOTTO_PLUS2
                        lottoPlus1 -> LottoViewModel.GAME_SUBTYPE_LOTTO_PLUS1
                        else -> LottoViewModel.GAME_TYPE_LOTTO
                    }
                }

                lottoViewModel.currentGameSubType = lottoViewModel.currentGameRules.gameType
                if (DateUtils.isFromDateMore(DateUtils.getTodaysDate(LottoUtils.DATE_NO_FORMAT_PATTERN), firstDrawDate, LottoUtils.DATE_NO_FORMAT_PATTERN)) {
                    if (currentTicket.firstDrawDate == currentTicket.lastDrawDate) {
                        lottoViewModel.fetchLottoDrawResults(lottoViewModel.currentGameSubType!!, currentTicket.firstDrawDate, currentTicket.firstDrawDate)
                    } else {
                        lottoViewModel.fetchLottoDrawResults(lottoViewModel.currentGameSubType!!, currentTicket.firstDrawDate, DateUtils.getTodaysDate(LottoUtils.DATE_NO_FORMAT_PATTERN))
                    }
                } else {
                    navigate(LottoPurchasedTicketsFragmentDirections.actionLottoPurchasedTicketsFragmentToLottoTicketFragment())
                }
            }
            (activity as LottoActivity).tagLottoAndPowerBallEvent("PurchasedTicketsScreen_TicketHistoryIndividualTicketClicked")
        }
    }

    private fun setupTabLayout() {
        val selectedTabPosition = if (lottoViewModel.currentGameRules.gameType == LottoViewModel.GAME_TYPE_POWERBALL) 1 else 0
        lottoTabLayout.getTabAt(selectedTabPosition)?.select()

        lottoTabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                tab?.apply {

                    val gameRules = lottoViewModel.lottoGameRulesListLiveData.value

                    gameRules?.let {
                        lottoViewModel.currentGameRules = if (position == 0) {
                            it.first()
                        } else {
                            it.last()
                        }
                    }

                    setTitle()
                    setUpTickets()
                }
            }

            override fun onTabReselected(p0: TabLayout.Tab?) {}
            override fun onTabUnselected(p0: TabLayout.Tab?) {}
        })
    }

    private fun showBottomBarMenu() {
        val filterBottomDialogFragment = TransactionHistoryFilterFragment.newInstance(this, filterOptions, "Lotto ticket list")
        filterBottomDialogFragment.apply {
            if (!isAdded && !isVisible) {
                show(baseActivity.supportFragmentManager, "filter_dialog_fragment")
            }
        }
    }

    override fun updateFilteringOptions(filteringOptions: FilteringOptions) {
        filterOptions = filteringOptions
        val date = DateUtils.formatDate(filteringOptions.fromDate, DateUtils.DASHED_DATE_PATTERN, DateUtils.DATE_DISPLAY_PATTERN) + " - " + DateUtils.formatDate(filteringOptions.toDate, DateUtils.DASHED_DATE_PATTERN, DateUtils.DATE_DISPLAY_PATTERN)
        ticketFilterAndSearchView.setSearchText(date)
        lottoViewModel.fetchTicketHistory(filteringOptions.fromDate, filteringOptions.toDate)
    }
}