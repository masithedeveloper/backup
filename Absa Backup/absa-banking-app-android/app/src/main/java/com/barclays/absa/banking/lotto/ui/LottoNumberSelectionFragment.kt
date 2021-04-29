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
import android.content.DialogInterface
import android.os.*
import android.view.*
import com.barclays.absa.banking.R
import com.barclays.absa.banking.framework.BaseFragment
import com.barclays.absa.banking.framework.utils.AlertInfo
import com.barclays.absa.utils.viewModel
import kotlinx.android.synthetic.main.lotto_number_selection_fragment.*
import lotto.LottoBallView
import lotto.LottoBoard
import lotto.LottoBoardView
import lotto.betSelector.BoardSelectorAdapter
import lotto.betSelector.BoardSelectorInterface
import styleguide.utils.ViewAnimation

open class LottoNumberSelectionFragment : BaseFragment(R.layout.lotto_number_selection_fragment) {

    private lateinit var lottoActivity: LottoActivity
    private lateinit var powerBallViewAnimation: ViewAnimation
    private lateinit var lottoViewModel: LottoViewModel

    private var isBonusBoardAvailable = false
    private var isPowerBall = false
    private var isPowerBallRandom = false
    private var totalBallCount: Int = 6
    private var currentBoard: LottoBoard? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        lottoActivity = (context as LottoActivity)
        lottoViewModel = lottoActivity.viewModel()
        totalBallCount = lottoViewModel.currentGameRules.maximumPrimaryBallNumber + lottoViewModel.currentGameRules.maximumSecondaryBallNumber

        val lottoBoard = lottoViewModel.lottoBoardList.value?.find { lottoViewModel.selectedLottoBoard.value == it }
        lottoBoard?.let {
            currentBoard = it
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setToolBar(getString(R.string.lotto_choose_numbers_title, 6))
        setHasOptionsMenu(true)

        isPowerBall = lottoViewModel.currentGameRules.gameType == LottoViewModel.GAME_TYPE_POWERBALL
        if (isPowerBall) {
            setupPowerBallBoard(lottoViewModel.currentGameRules.secondaryHighNumber, 1)
            powerBallLinearLayout.viewTreeObserver?.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
                override fun onGlobalLayout() {
                    powerBallViewAnimation = ViewAnimation(powerBallLinearLayout)
                    powerBallViewAnimation.collapseView(0)

                    if (selectedNumbersLottoBoard.lottoBallListSize() == lottoViewModel.currentGameRules.maximumPrimaryBallNumber) {
                        showPowerBallView()
                    }

                    powerBallLinearLayout.viewTreeObserver?.removeOnGlobalLayoutListener(this)
                }
            })
        } else {
            powerBallLinearLayout.visibility = View.GONE
        }
        setupBetSelectorBoard()

        saveBoardButton.setOnClickListener {
            if ((isPowerBall && selectedNumbersLottoBoard.lottoBoard.ballList.size == 5 && selectedNumbersLottoBoard.lottoBoard.powerBall != -1) || selectedNumbersLottoBoard.lottoBoard.ballList.size == 6) {
                currentBoard?.ballList = selectedNumbersLottoBoard.lottoBoard.ballList
                currentBoard?.powerBall = selectedNumbersLottoBoard.lottoBoard.powerBall
                lottoActivity.superOnBackPressed()
            } else {
                selectedNumbersLottoBoard.showErrorMessage(getString(R.string.lotto_please_select_all_balls))
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_lotto, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_clear -> {
                if (selectedNumbersLottoBoard.isRandomising) {
                    return true
                }

                item.isEnabled = false
                Handler(Looper.getMainLooper()).postDelayed({ item.isEnabled = true }, 500)

                selectedNumbersLottoBoard.clearBoard()
                lottoBetSelectorView.clearSelectedBalls()
                if (isPowerBall) {
                    hidePowerBallView()
                    powerBallBetSelectorView.clearSelectedBalls()
                }
                true
            }
            else -> {
                super.onOptionsItemSelected(item)
            }
        }
    }

    private fun setupBetSelectorBoard() {
        selectedNumbersLottoBoard.isPowerBall = isPowerBall

        val numberOfSelectableBalls = lottoViewModel.currentGameRules.maximumPrimaryBallNumber

        selectedNumbersLottoBoard.setViewClickInterface(object : LottoBoardView.ViewClickInterface {
            override fun onViewClicked(lottoBallView: LottoBallView) {
                if (lottoBallView.isPowerBall) {
                    powerBallBetSelectorView.clearSelectedBalls()
                    selectedNumbersLottoBoard.removePowerBall()
                } else {
                    val viewHolderForPosition = lottoBetSelectorView.findViewHolderForAdapterPosition(lottoBallView.ballNumber - 1) as BoardSelectorAdapter.LottoNumberViewHolder
                    lottoBetSelectorView.adapter.removeNumber(viewHolderForPosition.lottoNumberButton)

                    if (isPowerBall && selectedNumbersLottoBoard.lottoBallListSize() == numberOfSelectableBalls - 1) {
                        hidePowerBallView()
                    }
                }
            }
        })

        lottoBetSelectorView.setupBoard(lottoViewModel.currentGameRules.primaryHighNumber, numberOfSelectableBalls, object : BoardSelectorInterface {
            override fun isSorting(): Boolean {
                return selectedNumbersLottoBoard.isSorting
            }

            override fun randomiseComplete() {
                if (!isPowerBallRandom) {
                    Handler(Looper.getMainLooper()).postDelayed({ selectedNumbersLottoBoard?.sortBoard(true) }, 500)
                }
            }

            override fun enableBoard() {
                hidePowerBallView()
            }

            override fun onNumberSelected(number: Int) {
                selectedNumbersLottoBoard.clearError()
                selectedNumbersLottoBoard.addBall(number)

                hapticVibrate()

                if (isPowerBall && selectedNumbersLottoBoard.lottoBallListSize() == numberOfSelectableBalls) {
                    lottoBetSelectorView.animate().alpha(0.5f).setDuration(300).start()
                    showPowerBallView()
                }
            }

            override fun onNumberRemoved(number: Int) {
                selectedNumbersLottoBoard.removeBallWithNumber(number)

                hapticVibrate()

                if (isPowerBall && selectedNumbersLottoBoard.lottoBallListSize() == numberOfSelectableBalls - 1) {
                    hidePowerBallView()
                }
            }
        })

        randomiseFloatingActionButton.setOnClickListener {
            (activity as LottoActivity).tagLottoAndPowerBallEvent("BetSelectorScreen_DiceRandomiserFloatingActionButtonClicked")
            if (selectedNumbersLottoBoard.selectedLottoBalls.size == totalBallCount) {
                showBallCancelDialog { _, _ ->
                    randomiseBoard()
                }
            } else {
                randomiseBoard()
            }
        }

        selectedNumbersLottoBoard.animateLayoutChanges()
        selectedNumbersLottoBoard.hideChooseNumberButton()
        randomiseFloatingActionButton.bringToFront()
        selectedNumbersLottoBoard.createBoard(6)

        lottoViewModel.selectedLottoBoard.value?.let { lottoBoard ->
            selectedNumbersLottoBoard.setBoardLetter(lottoBoard.boardLetter)
            selectedNumbersLottoBoard.addBalls(lottoBoard.ballList)
            lottoBetSelectorView.setSelectedBalls(lottoBoard.ballList)
            if (lottoBoard.powerBall != -1) {
                selectedNumbersLottoBoard.setPowerBall(lottoBoard.powerBall)
                powerBallBetSelectorView.setSelectedBall(lottoBoard.powerBall)
            }
        }
    }

    private fun randomiseBoard() {
        randomiseFloatingActionButton.isEnabled = false
        selectedNumbersLottoBoard.isRandomising = true
        saveBoardButton.isEnabled = false

        var selectedBallListSize = selectedNumbersLottoBoard.selectedLottoBalls.size

        if (selectedNumbersLottoBoard.selectedLottoBalls.size == totalBallCount) {
            resetAndRandomWholeBoard(totalBallCount)
            randomiseFloatingActionButton.animate().rotationBy(-1080f).setDuration(3500).start()
            randomiseFloatingActionButton.postDelayed({
                randomiseComplete()
            }, 3500)
            return
        }

        randomiseFloatingActionButton.animate().rotationBy(-1080f).setDuration(2500).start()
        randomiseFloatingActionButton.postDelayed({
            randomiseComplete()
        }, 2500)

        isPowerBallRandom = false
        if (isPowerBall) {
            if (selectedNumbersLottoBoard.isPowerBallSet()) {
                selectedBallListSize--
            } else {
                isPowerBallRandom = true
                animatePowerBallRandom(totalBallCount)
            }

            if (selectedBallListSize != lottoViewModel.currentGameRules.maximumPrimaryBallNumber) {
                lottoBetSelectorView.randomiseLottoBalls()
                selectedNumbersLottoBoard.clearBoardIfFull()
                hidePowerBallView()
            }
        } else {
            lottoBetSelectorView.randomiseLottoBalls()
            selectedNumbersLottoBoard.clearBoardIfFull()
        }
    }

    private fun randomiseComplete() {
        randomiseFloatingActionButton.isEnabled = true
        selectedNumbersLottoBoard.isRandomising = false
        saveBoardButton.isEnabled = true
    }

    private fun resetAndRandomWholeBoard(totalBallCount: Int) {
        lottoBetSelectorView.randomiseLottoBalls()
        if (isPowerBall) {
            isPowerBallRandom = true
            hidePowerBallView()
            animatePowerBallRandom(totalBallCount)
        }
        selectedNumbersLottoBoard.clearBoardIfFull()
    }

    private fun animatePowerBallRandom(totalBallCount: Int) {
        powerBallBetSelectorView.clearSelectedBalls()
        lottoBetSelectorView.animate().alpha(1f).setDuration(300).start()
        val waitDuration = if (selectedNumbersLottoBoard.selectedLottoBalls.size == totalBallCount) totalBallCount + 2 else (totalBallCount - selectedNumbersLottoBoard.selectedLottoBalls.size)
        Handler(Looper.getMainLooper()).postDelayed({
            powerBallBetSelectorView.randomiseLottoBalls()
        }, (waitDuration * 400).toLong())
    }

    fun hapticVibrate() {
        val vibrator = context?.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator?
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator?.vibrate(VibrationEffect.createOneShot(10, 60))
        } else {
            @Suppress("DEPRECATION")
            vibrator?.vibrate(10)
        }
    }

    private fun setupPowerBallBoard(numberOfPowerBalls: Int, selectablePowerBalls: Int) {
        isBonusBoardAvailable = true

        powerBallBetSelectorView.setupBoard(numberOfPowerBalls, selectablePowerBalls, object : BoardSelectorInterface {
            override fun isSorting(): Boolean {
                return selectedNumbersLottoBoard.isSorting
            }

            override fun randomiseComplete() {
                Handler(Looper.getMainLooper()).postDelayed({ selectedNumbersLottoBoard?.sortBoard(true) }, 500)
            }

            override fun enableBoard() {}

            override fun onNumberSelected(number: Int) {
                selectedNumbersLottoBoard.clearError()
                selectedNumbersLottoBoard.addPowerBall(number)
                hapticVibrate()
            }

            override fun onNumberRemoved(number: Int) {
                selectedNumbersLottoBoard.removePowerBall()
                hapticVibrate()
            }
        })
    }

    private fun showPowerBallView() {
        if (lottoBetSelectorView.isBoardEnabled()) {
            lottoBetSelectorView.isEnabled = false
            powerBallViewAnimation.expandView(300)

            Handler(Looper.getMainLooper()).postDelayed({ scrollToTopOfView(powerBallLinearLayout) }, 300)
        }
    }

    protected fun scrollToTopOfView(view: View) {
        scrollView.post { scrollView.smoothScrollTo(0, view.y.toInt()) }
    }

    private fun hidePowerBallView() {
        if (!lottoBetSelectorView.isBoardEnabled()) {
            lottoBetSelectorView.isEnabled = true
            powerBallViewAnimation.collapseView(300)
            lottoBetSelectorView.animate().alpha(1f).setDuration(300).start()
            Handler(Looper.getMainLooper()).postDelayed({ scrollToTopOfView(powerBallLinearLayout) }, 300)
        }
    }

    private fun showBallCancelDialog(onPositiveClick: DialogInterface.OnClickListener) {
        AlertInfo().apply {

            val isComplete = currentBoard?.isComplete ?: false
            if (hasBallsChanged() && isComplete) {
                message = getString(R.string.lotto_discard_changes_board_message)
                title = getString(R.string.lotto_discard_changes_board_title)
            } else {
                message = getString(R.string.lotto_discard_board_message)
                title = getString(R.string.lotto_discard_board_title)
            }

            negativeButtonText = getString(R.string.cancel)
            positiveButtonText = getString(R.string.ok)

            negativeDismissListener = DialogInterface.OnClickListener { dialog, _ ->
                dialog.dismiss()
            }

            positiveDismissListener = onPositiveClick

            showCustomAlertDialog(this)
        }
    }

    private fun hasBallsChanged(): Boolean {
        return selectedNumbersLottoBoard.lottoBoard.ballList != currentBoard?.ballList || selectedNumbersLottoBoard.lottoBoard.powerBall != currentBoard?.powerBall
    }

    fun onBackPressed() {
        if (selectedNumbersLottoBoard.isRandomising) {
            return
        }

        val isComplete = currentBoard?.isComplete ?: false
        if (isComplete && !hasBallsChanged()) {
            lottoActivity.superOnBackPressed()
        } else if (selectedNumbersLottoBoard.selectedLottoBalls.size > 0) {
            showBallCancelDialog { _, _ ->
                lottoActivity.superOnBackPressed()
            }
        } else {
            lottoActivity.superOnBackPressed()
        }
    }
}