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
import com.barclays.absa.banking.card.ui.creditCard.hub.TransactionHistoryFilterFragment.Companion.newInstance
import com.barclays.absa.banking.card.ui.creditCard.hub.TransactionHistoryFilterFragment.UpdateFilteringOptions
import com.barclays.absa.banking.framework.BaseFragment
import com.barclays.absa.banking.lotto.services.LottoDrawResult
import com.barclays.absa.utils.DateUtils
import com.barclays.absa.utils.viewModel
import com.google.android.material.tabs.TabLayout
import kotlinx.android.synthetic.main.lotto_game_results_fragment.*
import java.util.*

class LottoGameResultsFragment : BaseFragment(R.layout.lotto_game_results_fragment), UpdateFilteringOptions {

    private lateinit var lottoViewModel: LottoViewModel
    private lateinit var drawResults: MutableList<LottoDrawResult>
    private var filteredDrawResults: MutableList<LottoDrawResult> = mutableListOf()
    private var originalResults = mutableListOf<LottoDrawResult>()
    private var filterOptions = FilteringOptions()

    override fun onAttach(context: Context) {
        super.onAttach(context)
        lottoViewModel = (context as LottoActivity).viewModel()
        lottoViewModel.lottoDrawResultsLiveData.value?.let {
            originalResults.addAll(it.lottoDrawResults)
            saveOriginalResults(it.lottoDrawResults)
        }
    }

    private fun saveOriginalResults(results: MutableList<LottoDrawResult>) {
        lottoViewModel.originalDrawResults.clear()
        lottoViewModel.originalDrawResults.addAll(results)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setToolBar(getString(R.string.lotto_game_results))

        setUpFilterView()

        filterOptions.shouldShowTransactionFilter = false
        lottoViewModel.lottoDrawResultsLiveData.value?.lottoDrawResults = originalResults
        lottoViewModel.currentGameSubType = lottoViewModel.currentGameRules.gameType
        drawResults = originalResults
        updateDrawResults()

        setupTabLayout()
    }

    private fun attachObserver() {
        lottoViewModel.lottoDrawResultsLiveData = MutableLiveData()
        lottoViewModel.lottoDrawResultsLiveData.observe(this, { results ->
            lottoViewModel.lottoDrawResultsLiveData.removeObservers(this)
            saveOriginalResults(results.lottoDrawResults)
            originalResults.clear()
            originalResults.addAll(results.lottoDrawResults)
            updateDrawResults()
        })

        lottoViewModel.lottoTicketHistoryLiveData = MutableLiveData()
        lottoViewModel.lottoTicketHistoryLiveData.observe(this, {
            lottoViewModel.lottoTicketHistoryLiveData.removeObservers(this)
            lottoViewModel.lottoTicketHistory = it
            lottoViewModel.fetchLottoDrawResults(lottoViewModel.currentGameRules.gameType, filterOptions.fromDate, filterOptions.toDate)
        })
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

                    attachObserver()
                    lottoViewModel.currentGameSubType = lottoViewModel.currentGameRules.gameType
                    lottoViewModel.fetchLottoDrawResults(lottoViewModel.currentGameRules.gameType, filterOptions.fromDate, filterOptions.toDate)
                }
            }

            override fun onTabReselected(p0: TabLayout.Tab?) {}
            override fun onTabUnselected(p0: TabLayout.Tab?) {}
        })
    }

    private fun updateDrawResults() {
        filteredDrawResults.clear()

        val filteredDrawResults = drawResults.filter { it.drawType == lottoViewModel.currentGameRules.gameType }.toMutableList()

        filteredDrawResults.forEach { lottoDrawResult ->
            val normalizedDrawDate = DateUtils.formatDate(lottoDrawResult.drawDate, DateUtils.DASHED_DATE_PATTERN, LottoUtils.DATE_NO_FORMAT_PATTERN)

            lottoDrawResult.purchasedTickets.clear()
            lottoViewModel.lottoTicketHistory.filter { it.lottoGameType == lottoViewModel.currentGameRules.gameType }.forEach { ticket ->
                if (normalizedDrawDate == ticket.firstDrawDate || normalizedDrawDate == ticket.lastDrawDate) {
                    lottoDrawResult.hasPurchasedTicket = true
                    lottoDrawResult.purchasedTickets.add(LottoUtils.createLottoTicket(ticket, lottoViewModel.currentGameRules.gameType, baseActivity))
                }
            }

            if (!drawResults.contains(lottoDrawResult)) {
                drawResults.add(lottoDrawResult)
            }
        }

        val adapter = LottoGameResultsAdapter(filteredDrawResults) { index ->
            lottoViewModel.currentDrawResult = drawResults[index]
            navigate(LottoGameResultsFragmentDirections.actionLottoGameResultsFragmentToLottoDrawResultsFragment())
            (activity as LottoActivity).tagLottoAndPowerBallEvent("ViewGameResultsScreen_PastResultClicked")
        }

        gameResultsRecyclerView.adapter = adapter
        dismissProgressDialog()
    }

    private fun setUpFilterView() {
        val fromCalendar = Calendar.getInstance()
        fromCalendar.add(Calendar.DAY_OF_YEAR, -7)

        val selectedFromDate = DateUtils.format(fromCalendar, DateUtils.DASHED_DATE_PATTERN)
        val selectedToDate = DateUtils.getTodaysDate()

        val formatDate = DateUtils.formatDate(selectedFromDate, DateUtils.DASHED_DATE_PATTERN, DateUtils.DATE_DISPLAY_PATTERN) + " - " + DateUtils.formatDate(selectedToDate, DateUtils.DASHED_DATE_PATTERN, DateUtils.DATE_DISPLAY_PATTERN)

        filterAndSearchView.setSearchText(formatDate)
        filterAndSearchView.hideSearchView()
        filterAndSearchView.setOnCalendarLayoutClickListener { filterTransactionsUsingDateRange(selectedFromDate, selectedToDate) }
    }

    private fun filterTransactionsUsingDateRange(fromDate: String, toDate: String) {
        filterOptions.filterType = ""
        filterOptions.fromDate = fromDate
        filterOptions.toDate = toDate
        showBottomBarMenu()
    }

    private fun showBottomBarMenu() {
        val filterBottomDialogFragment = newInstance(this, filterOptions, "Lotto ticket list")
        filterBottomDialogFragment.apply {
            if (!isAdded && !isVisible) {
                show(baseActivity.supportFragmentManager, "filter_dialog_fragment")
            }
        }
    }

    override fun updateFilteringOptions(filteringOptions: FilteringOptions) {
        filterOptions = filteringOptions
        val date = DateUtils.formatDate(filteringOptions.fromDate, DateUtils.DASHED_DATE_PATTERN, DateUtils.DATE_DISPLAY_PATTERN) + " - " + DateUtils.formatDate(filterOptions.toDate, DateUtils.DASHED_DATE_PATTERN, DateUtils.DATE_DISPLAY_PATTERN)
        filterAndSearchView.setSearchText(date)

        attachObserver()
        lottoViewModel.fetchTicketHistory(filterOptions.fromDate, filterOptions.toDate)
    }
}