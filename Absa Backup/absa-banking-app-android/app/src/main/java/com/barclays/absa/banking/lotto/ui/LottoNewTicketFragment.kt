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
import android.os.Handler
import android.os.Looper
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import androidx.transition.ChangeBounds
import androidx.transition.TransitionManager
import com.barclays.absa.banking.R
import com.barclays.absa.banking.boundary.model.Amount
import com.barclays.absa.banking.framework.BaseFragment
import com.barclays.absa.banking.lotto.services.LottoBoardNumbers
import com.barclays.absa.banking.lotto.ui.LottoNewTicketAdapter.Companion.ANIMATION_DURATION
import com.barclays.absa.banking.lotto.ui.LottoNewTicketAdapter.Companion.TOTAL_ANIMATION_DURATION
import com.barclays.absa.utils.viewModel
import kotlinx.android.synthetic.main.lotto_new_ticket_fragment.*
import kotlinx.android.synthetic.main.step_indicator_button.*
import kotlinx.android.synthetic.main.step_indicator_button.view.*
import lotto.LottoBoard
import styleguide.utils.AnimationHelper.shakeShakeAnimate

class LottoNewTicketFragment : BaseFragment(R.layout.lotto_new_ticket_fragment), LottoNewTicketAdapter.LottoTicketInterface {

    private lateinit var lottoNewTicketAdapter: LottoNewTicketAdapter
    private lateinit var lottoViewModel: LottoViewModel
    private lateinit var menu: Menu

    private var lottoBoardList: ArrayList<LottoBoard> = ArrayList()

    override fun onAttach(context: Context) {
        super.onAttach(context)
        lottoViewModel = (context as LottoActivity).viewModel()
        setHasOptionsMenu(true)
        setUpInitialBoard()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setToolBar(getString(R.string.lotto_new_ticket))
        setUpClickListeners()

        stepIndicatorView.stepIndicatorTextView.text = getString(R.string.lotto_step_indicator_text, "1", "3", getString(R.string.lotto_complete_game_boards_step_indicator))
        updatePurchaseTotal()

        lottoNewTicketAdapter = LottoNewTicketAdapter(this, lottoBoardList)
        lottoNewTicketAdapter.setHasStableIds(true)

        boardRecyclerView.itemAnimator = null
        boardRecyclerView.adapter = lottoNewTicketAdapter

        if (lottoBoardList.size >= lottoViewModel.currentGameRules.maximumBoards) {
            addBoardOptionActionButtonView.visibility = View.GONE
        }
    }

    private fun setUpClickListeners() {
        addBoardOptionActionButtonView.setOnClickListener {
            lottoNewTicketAdapter.addNewBoard()
            lottoViewModel.purchaseData.numberOfBoards += 1
            updatePurchaseTotal()

            animateLayoutChanges()
            if (lottoBoardList.size >= lottoViewModel.currentGameRules.maximumBoards) {
                addBoardOptionActionButtonView.visibility = View.GONE
            }
        }

        nextButton.setOnClickListener { view ->
            preventDoubleClick(view)
            if (isValidInput()) {
                val purchaseLottoBoardList = mutableListOf<LottoBoardNumbers>()

                lottoBoardList.forEach {
                    val boardList = LottoBoardNumbers()
                    boardList.lottoBoardNumbers = it.ballList
                    boardList.powerBall = it.powerBall
                    purchaseLottoBoardList.add(boardList)
                }

                lottoViewModel.purchaseData.lottoBoardNumbers = purchaseLottoBoardList
                navigate(LottoNewTicketFragmentDirections.actionLottoNewTicketFragmentToLottoPlayTheLottoFragment())
            }
        }
    }

    private fun isValidInput(): Boolean {
        val indexOfIncompleteBoard = lottoBoardList.indexOfFirst { !it.isComplete }
        val incomplete = indexOfIncompleteBoard > -1

        if (incomplete) {
            shakeShakeAnimate(boardRecyclerView.findViewHolderForAdapterPosition(indexOfIncompleteBoard)?.itemView)
            Toast.makeText(context, getString(R.string.lotto_please_complete_board, lottoBoardList[indexOfIncompleteBoard].boardLetter), Toast.LENGTH_LONG).show()
        }

        return !incomplete
    }

    private fun setUpInitialBoard() {
        val lottoBoardA = LottoBoard()
        val lottoBoardB = LottoBoard()

        lottoBoardA.boardLetter = LottoUtils.getCharForNumber(0)
        lottoBoardB.boardLetter = LottoUtils.getCharForNumber(1)

        lottoBoardList.add(lottoBoardA)
        lottoBoardList.add(lottoBoardB)
        lottoViewModel.purchaseData.numberOfBoards = 2
        lottoViewModel.lottoBoardList.value = lottoBoardList
    }

    override fun chooseNumberClicked(lottoBoard: LottoBoard) {
        lottoViewModel.selectedLottoBoard.value = lottoBoard
        if (findNavController().currentDestination?.id != R.id.lottoNumberSelectionFragment) {
            navigate(LottoNewTicketFragmentDirections.actionLottoNewTicketFragmentToLottoNumberSelectionFragment())
        }
    }

    override fun itemDeleted() {
        animateLayoutChanges()
        addBoardOptionActionButtonView.visibility = View.VISIBLE
        lottoViewModel.purchaseData.numberOfBoards -= 1
        updatePurchaseTotal()
    }

    private fun updatePurchaseTotal() {
        val costForBoards = lottoViewModel.currentGameRules.basePrice * lottoViewModel.purchaseData.numberOfBoards.toBigDecimal()
        totalTextView.text = Amount(costForBoards.toString()).toString()
    }

    private fun animateLayoutChanges() {
        val transition = ChangeBounds()
        transition.duration = ANIMATION_DURATION
        TransitionManager.beginDelayedTransition(parentConstraintLayout, transition)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        this.menu = menu
        inflater.inflate(R.menu.delete_cancel_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.deleteMenuItem) {
            showDeleteOptions()
        } else if (item.itemId == R.id.cancelMenuItem) {
            hideDeleteOptions()
        }

        return super.onOptionsItemSelected(item)
    }

    override fun hideDeleteOptions() {
        lottoNewTicketAdapter.hideDeleteOptions()
        val cancelMenuItem = menu.getItem(1)
        cancelMenuItem.isVisible = false

        val deleteMenuItem = menu.getItem(0)
        deleteMenuItem.isVisible = true
        deleteMenuItem.isEnabled = false
        Handler(Looper.getMainLooper()).postDelayed({ deleteMenuItem.isEnabled = true }, TOTAL_ANIMATION_DURATION)
    }

    private fun showDeleteOptions() {
        lottoNewTicketAdapter.showDeleteOptions()
        val deleteMenuItem = menu.getItem(0)
        deleteMenuItem.isVisible = false

        val cancelMenuItem = menu.getItem(1)
        cancelMenuItem.isVisible = true
        cancelMenuItem.isEnabled = false
        Handler(Looper.getMainLooper()).postDelayed({ cancelMenuItem.isEnabled = true }, TOTAL_ANIMATION_DURATION)
    }
}