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
import android.content.Intent
import android.os.Bundle
import android.os.Parcelable
import android.view.View
import com.barclays.absa.banking.R
import com.barclays.absa.banking.framework.BaseFragment
import com.barclays.absa.banking.lotto.services.LottoTicketPurchaseDataModel
import com.barclays.absa.banking.lotto.ui.LottoActivity.Companion.LOTTO_GAME_RULES_EXTRA
import com.barclays.absa.banking.lotto.ui.LottoViewModel.Companion.GAME_TYPE_LOTTO
import com.barclays.absa.banking.lotto.ui.LottoViewModel.Companion.GAME_TYPE_POWERBALL
import com.barclays.absa.banking.presentation.transactions.AccountRefreshInterface
import com.barclays.absa.utils.AccountBalanceUpdateHelper
import com.barclays.absa.utils.viewModel
import kotlinx.android.synthetic.main.lotto_success_fragment.*
import lotto.LottoBoard

class LottoPurchaseSuccessFragment : BaseFragment(R.layout.lotto_success_fragment) {
    private lateinit var purchaseData: LottoTicketPurchaseDataModel
    private lateinit var lottoViewModel: LottoViewModel

    override fun onAttach(context: Context) {
        super.onAttach(context)
        lottoViewModel = (context as LottoActivity).viewModel()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        hideToolBar()
        scrollView.setLayerType(View.LAYER_TYPE_SOFTWARE, null)

        purchaseData = lottoViewModel.purchaseData

        if (purchaseData.lottoGameType == GAME_TYPE_LOTTO) {
            titleCenteredTitleView.setTitle(getString(R.string.lotto_purchase_successful))
            ticketView.setTicketTitle(if (purchaseData.playPlus2) getString(R.string.lotto_plus_two) else if (purchaseData.playPlus1) getString(R.string.lotto_plus_one) else getString(R.string.lotto))
        } else {
            titleCenteredTitleView.setTitle(getString(R.string.lotto_powerball_purchase_successful))
            ticketView.setTicketTitle(if (purchaseData.playPlus1) getString(R.string.lotto_powerball_plus) else getString(R.string.lotto_powerball))
        }

        ticketView.setTicketBoardsDescription(LottoUtils.getNoAmountTicketDescription(requireContext(), purchaseData.numberOfBoards, purchaseData.numberOfDraws, purchaseData.date))

        val referenceNumber = lottoViewModel.lottoPurchaseResponseLiveData.value?.referenceNumber
        contentDescriptionView.setDescription(getString(R.string.lotto_success_description, purchaseData.lottoGameType, purchaseData.date, referenceNumber?.takeLast(20)))

        val boardList = arrayListOf<LottoBoard>()
        lottoViewModel.lottoPurchaseResponseLiveData.value?.lottoBoards?.forEachIndexed { index, it ->
            val board = LottoBoard()
            it.lottoBoardNumbers.forEach {
                if (it != 0) {
                    board.ballList.add(it)
                }
            }
            if (lottoViewModel.currentGameRules.gameType == GAME_TYPE_POWERBALL) {
                board.powerBall = it.powerBall
            }
            board.boardLetter = LottoUtils.getCharForNumber(index)
            boardList.add(board)
        }

        ticketView.setSelectedLottoBoards(boardList)

        doneButton.setOnClickListener {
            AccountBalanceUpdateHelper(baseActivity).refreshHomeScreenAccountsAndBalances(object : AccountRefreshInterface {
                override fun onSuccess() {
                    navigateToLottoActivity()
                }

                override fun onFailure() {
                    navigateToLottoActivity()
                }

                private fun navigateToLottoActivity() {
                    activity?.let { activity ->
                        val lottoBoardType = if (lottoViewModel.purchaseData.quickPickInd) "QuickPickSuccessScreen" else "SuccessScreen"
                        (activity as LottoActivity).tagLottoAndPowerBallEvent("${lottoBoardType}_SuccessDoneClicked")
                        val intent = Intent(activity, LottoActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                        intent.putParcelableArrayListExtra(LOTTO_GAME_RULES_EXTRA, lottoViewModel.lottoGameRulesListLiveData.value as ArrayList<out Parcelable>)
                        startActivity(intent)
                    }
                }
            })
        }
    }
}