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

package lotto

import android.animation.LayoutTransition
import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.AttributeSet
import android.view.View
import android.view.ViewTreeObserver
import android.view.animation.Animation
import android.view.animation.Animation.AnimationListener
import android.view.animation.AnimationUtils
import android.widget.LinearLayout
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.view.get
import androidx.core.view.size
import androidx.transition.ChangeBounds
import androidx.transition.TransitionManager
import kotlinx.android.synthetic.main.lotto_board.view.*
import liblotto.lotto.R
import kotlin.Comparator

open class LottoBoardView : ConstraintLayout {

    private var viewClickInterface: ViewClickInterface? = null
    private var isDisplayBoard: Boolean = false

    var isRandomising: Boolean = false
    var lottoBoard = LottoBoard()
    var selectedLottoBalls: MutableList<LottoBallView> = mutableListOf()
    var drawResults = mutableListOf<Int>()
    var isPowerBall = false
    var isSorting = false

    protected lateinit var ballLinearLayout: LinearLayout

    constructor(context: Context) : this(context, null)

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init(context)
    }

    private fun init(context: Context) {
        View.inflate(context, R.layout.lotto_board, this)
        ballLinearLayout = boardLinearLayout as LinearLayout
    }

    fun createBoard(numberOfBalls: Int) {
        setUpEmptyLayout(numberOfBalls)

        positionImageView.visibility = View.VISIBLE
        positionImageView.viewTreeObserver?.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                positionImageView.animate().translationX(ballLinearLayout[0].x + (ballLinearLayout[0].width / 2) - positionImageView.width / 2).setDuration(0).start()
                positionImageView.viewTreeObserver?.removeOnGlobalLayoutListener(this)

                if (selectedLottoBalls.size == 6) {
                    positionImageView.alpha = 0f
                }
            }
        })
    }

    fun hideBorder() {
        borderImageView.visibility = GONE
    }

    private fun setUpEmptyLayout(numberOfBalls: Int) {
        ballLinearLayout.removeAllViews()
        for (i in 1..numberOfBalls) {
            val newBall = createNewBall()
            newBall.setValue(0)
            newBall.clearBall()

            ballLinearLayout.addView(newBall)
        }
    }

    fun setBoardLetter(boardLetter: Char) {
        if (boardLetterTextView.text.isNotEmpty() && boardLetter != boardLetterTextView.text.first()) {
            val scaleAnimation = AnimationUtils.loadAnimation(context, R.anim.scale_out_in)
            boardLetterTextView.startAnimation(scaleAnimation)
        }

        boardLetterTextView.text = boardLetter.toString()
    }

    fun clearBoardLetter() {
        boardLetterTextView.text = ""
    }

    fun showChooseNumberButton() {
        chooseNumberButton.visibility = View.VISIBLE
        boardLinearLayout.visibility = View.GONE
    }

    fun hideChooseNumberButton() {
        chooseNumberButton.visibility = View.GONE
        boardLinearLayout.visibility = View.VISIBLE
    }

    fun addBall(ballNumber: Int) {
        if ((isPowerBall && lottoBoard.ballList.size >= 5) || lottoBoard.ballList.size >= 6) {
            return
        }

        val lottoBallView: LottoBallView = ballLinearLayout[lottoBoard.ballList.size] as LottoBallView
        lottoBoard.ballList.add(ballNumber)

        val slideUpOutAnimation = AnimationUtils.loadAnimation(context, R.anim.slide_up_out)
        slideUpOutAnimation.setAnimationListener(object : AnimationListener {
            override fun onAnimationRepeat(animation: Animation?) {}
            override fun onAnimationStart(animation: Animation?) {}

            override fun onAnimationEnd(animation: Animation?) {
                lottoBallView.setValue(ballNumber)
                lottoBallView.colorEnabled = true
                if (isPowerBallSet()) {
                    selectedLottoBalls.add(selectedLottoBalls.size - 1, lottoBallView)
                } else {
                    selectedLottoBalls.add(lottoBallView)
                }
                lottoBallView.startAnimation(AnimationUtils.loadAnimation(context, R.anim.slide_up))

                if (!isRandomising) {
                    sortBoard(false)
                }
            }
        })

        lottoBallView.startAnimation(slideUpOutAnimation)
        if (isPowerBallSet() && lottoBoard.ballList.size == ballLinearLayout.size - 1) {
            positionImageView.animate().alpha(0f).setDuration(300).start()
        } else {
            focusCurrentBall()
        }

        if (lottoBoard.ballList.size == ballLinearLayout.size) {
            positionImageView.animate().alpha(0f).setDuration(300).start()
        }
    }

    private fun isListSorted(ballList: MutableList<Int>): Boolean {
        val tmpBallList = ArrayList(ballList)
        val tmpBallList2 = ArrayList(ballList)

        tmpBallList.sort()
        return tmpBallList == tmpBallList2
    }

    fun sortBoard(isRandomBoard: Boolean) {
        var ballSortSize = lottoBoard.ballList.size
        if (isListSorted(lottoBoard.ballList) || ballSortSize <= 1) {
            return
        }
        isSorting = true

        var smallestIndex = lottoBoard.ballList.indexOfFirst { it > lottoBoard.ballList[ballSortSize - 1] }
        var biggestIndex = ballSortSize

        if (isRandomBoard) {
            biggestIndex = 0
            while (ballSortSize > 0) {
                for (index in 0 until ballSortSize) {
                    if (lottoBoard.ballList[index] > lottoBoard.ballList[ballSortSize - 1]) {
                        smallestIndex = index
                        if (biggestIndex == 0) {
                            biggestIndex = ballSortSize - 1
                        }
                        break
                    }
                }
                ballSortSize--
            }
        }

        val viewSortComparator = Comparator<LottoBallView> { o1, o2 ->
            return@Comparator when {
                o1.isPowerBall -> 1
                o2.isPowerBall -> -1
                else -> compareValues(o1.ballNumber, o2.ballNumber)
            }
        }

        selectedLottoBalls.sortWith(viewSortComparator)
        lottoBoard.ballList.sort()

        var scaleUp = 1.1f

        if (isPowerBall && !isPowerBallSet()) {
            scaleUp = 1.2f
            biggestIndex--
        }

        if (biggestIndex >= selectedLottoBalls.size) {
            biggestIndex = selectedLottoBalls.size - 1
        }

        for (i in smallestIndex..biggestIndex) {
            val currentLottoBallView = selectedLottoBalls[i]
            if (!currentLottoBallView.isPowerBall) {
                currentLottoBallView.animate().scaleY(scaleUp).scaleX(scaleUp).setStartDelay(200).setDuration(200).start()
            }
        }

        handler?.postDelayed({
            val changeBounds = ChangeBounds()
            TransitionManager.beginDelayedTransition(ballLinearLayout, changeBounds)

            if (selectedLottoBalls.isNotEmpty()) {
                for (i in smallestIndex..biggestIndex) {
                    val currentLottoBallView = selectedLottoBalls[i]
                    if (!currentLottoBallView.isPowerBall) {
                        ballLinearLayout.removeView(currentLottoBallView)
                    }
                }

                for (i in smallestIndex..biggestIndex) {
                    val currentLottoBallView = selectedLottoBalls[i]
                    if (!currentLottoBallView.isPowerBall) {
                        ballLinearLayout.addView(currentLottoBallView, i)
                        currentLottoBallView.animate().scaleY(1f).scaleX(1f).setStartDelay(400).setDuration(200).start()
                    }
                }
            }

            handler?.postDelayed({ isSorting = false }, 400)
        }, 600)
    }

    private fun focusCurrentBall(delay: Long) {
        val nextBallIndex = lottoBoard.ballList.size
        if (nextBallIndex < ballLinearLayout.size) {
            val nextBall = ballLinearLayout[nextBallIndex]
            positionImageView.animate().translationX(nextBall.x + (ballLinearLayout[0].width / 2) - positionImageView.width / 2).setStartDelay(delay).setDuration(350).start()
        }
    }

    private fun focusCurrentBall() {
        focusCurrentBall(0L)
    }

    fun addBalls(lottoBalls: MutableList<Int>) {
        lottoBalls.forEach { ballNumber ->
            val item: LottoBallView = ballLinearLayout[selectedLottoBalls.size] as LottoBallView
            item.setValue(ballNumber)
            selectedLottoBalls.add(item)
            lottoBoard.ballList.add(ballNumber)
        }
    }

    fun addPowerBall(ballNumber: Int) {
        val lottoBallView: LottoBallView = ballLinearLayout[5] as LottoBallView
        lottoBoard.powerBall = ballNumber
        positionImageView.animate().alpha(0f).setDuration(300).start()

        val slideUpOutAnimation = AnimationUtils.loadAnimation(context, R.anim.slide_up_out)
        slideUpOutAnimation.setAnimationListener(object : AnimationListener {
            override fun onAnimationRepeat(animation: Animation?) {}
            override fun onAnimationStart(animation: Animation?) {}

            override fun onAnimationEnd(animation: Animation?) {

                ChangeBounds().apply {
                    duration = 200
                    TransitionManager.beginDelayedTransition(this@LottoBoardView, this)
                }

                lottoBallView.apply {
                    isPowerBall = true
                    setValue(ballNumber)
                    colorEnabled = true
                    setPadding(resources.getDimensionPixelSize(R.dimen.small_space), 0, 0, 0)
                    selectedLottoBalls.add(lottoBallView)
                    startAnimation(AnimationUtils.loadAnimation(context, R.anim.slide_up))
                }
            }
        })
        lottoBallView.startAnimation(slideUpOutAnimation)
    }

    private fun createPowerBall(ballNumber: Int): LottoBallView {
        val powerBall = createNewBall(lottoBoard.isColorEnabled)
        powerBall.isPowerBall = true
        powerBall.setValue(ballNumber)
        powerBall.setPadding(resources.getDimensionPixelSize(R.dimen.small_space), 0, 0, 0)
        if (drawResults.isNotEmpty()) {
            powerBall.colorEnabled = drawResults[drawResults.size - 1] == ballNumber
        }
        return powerBall
    }

    private fun createNewBall(): LottoBallView {
        return createNewBall(lottoBoard.isColorEnabled)
    }

    private fun createNewBall(isColor: Boolean): LottoBallView {
        val newBall = LottoBallView(context)
        val layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT)
        layoutParams.weight = 1f
        newBall.layoutParams = layoutParams
        newBall.colorEnabled = isColor

        if (!isDisplayBoard) {
            newBall.setOnClickListener {
                if (newBall.ballNumber > 0) {
                    viewClickInterface?.onViewClicked(newBall)
                }
            }
        }

        return newBall
    }

    private fun createNewBall(ballNumber: Int): LottoBallView {
        val newBall = createNewBall()
        newBall.setValue(ballNumber)
        if (drawResults.isNotEmpty()) {
            newBall.colorEnabled = false

            val ballListSize = if (isPowerBall) drawResults.size - 1 else drawResults.size

            for (i in 0 until ballListSize) {
                if (drawResults[i] == ballNumber) {
                    newBall.colorEnabled = true
                    break
                }
            }
        }
        return newBall
    }

    fun setDrawResultBalls(drawResultBalls: MutableList<Int>) {
        this.drawResults = drawResultBalls
    }

    fun removeBallWithNumber(ballNumber: Int) {
        if (selectedLottoBalls.size > 0) {
            val ball = selectedLottoBalls.find { it.ballNumber == ballNumber }
            val index = selectedLottoBalls.indexOf(ball)
            if (index >= 0) {
                selectedLottoBalls.removeAt(index)
                lottoBoard.ballList.removeAt(index)
            }
            ball?.let { animateBallRemoval(it) }

            positionImageView.animate().alpha(1f).setDuration(300).setStartDelay(300).start()
            focusCurrentBall(300)
        }
    }

    private fun animateBallRemoval(ball: LottoBallView) {
        val slideUpAndOutAnimation = AnimationUtils.loadAnimation(context, R.anim.slide_down_out)
        slideUpAndOutAnimation.setAnimationListener(object : AnimationListener {
            override fun onAnimationRepeat(animation: Animation?) {}
            override fun onAnimationStart(animation: Animation?) {}

            override fun onAnimationEnd(animation: Animation?) {
                ballLinearLayout.removeView(ball)
                if (isPowerBallSet()) {
                    ballLinearLayout.addView(createNewBall(false), 4)
                } else {
                    ballLinearLayout.addView(createNewBall(false))
                }
            }
        })
        ball.startAnimation(slideUpAndOutAnimation)
    }

    fun isPowerBallSet() = lottoBoard.powerBall != -1

    fun removePowerBall() {
        if (lottoBoard.powerBall == -1) {
            return
        }

        val powerBallIndex = selectedLottoBalls.size - 1
        val ball = selectedLottoBalls[powerBallIndex]
        selectedLottoBalls.removeAt(powerBallIndex)
        lottoBoard.powerBall = -1
        animateBallRemoval(ball)
        positionImageView.animate().alpha(0f).setDuration(300).start()
        positionImageView.alpha = 0f

        handler.postDelayed({
            positionImageView.animate().alpha(1f).setDuration(300).start()
            focusCurrentBall()
        }, 500)
    }

    private fun sortAndDrawBoard() {
        lottoBoard.ballList.sortBy { it }
        selectedLottoBalls.sortBy { ballSorter(it) }
        redrawBoard()
    }

    private fun redrawBoard() {
        ballLinearLayout.removeAllViews()
        selectedLottoBalls.forEach {
            ballLinearLayout.addView(it)
        }
    }

    fun setPowerBall(ball: Int) {
        if (ball != -1) {
            val powerBall = createPowerBall(ball)
            selectedLottoBalls.add(powerBall)
            if (ballLinearLayout.size == 6) {
                ballLinearLayout.removeViewAt(5)
            }
            ballLinearLayout.addView(powerBall)
            lottoBoard.powerBall = ball
        }
    }

    fun setBalls(ballList: List<Int>) {
        isDisplayBoard = true
        selectedLottoBalls.clear()

        if (ballList.isEmpty()) {
            showChooseNumberButton()
        } else {
            ballList.forEach {
                selectedLottoBalls.add(createNewBall(it))
            }
        }
        positionImageView.visibility = View.GONE
        sortAndDrawBoard()
    }

    fun showDeleteIcon() {
        if (deleteImageView.visibility == View.GONE) {
            deleteImageView.visibility = View.VISIBLE
            deleteImageView.alpha = 0f
            deleteImageView.animate().alpha(1f).setStartDelay(200).setDuration(400).start()
        }
    }

    fun hideDeleteIcon() {
        if (deleteImageView.visibility == View.VISIBLE) {
            deleteImageView.animate().alpha(0f).setStartDelay(0).setDuration(250).start()
            Handler(Looper.getMainLooper()).postDelayed({ deleteImageView.visibility = View.GONE }, 400)
        }
    }

    fun clearBoardIfFull() {
        if (selectedLottoBalls.size == ballLinearLayout.size) {
            clearBoard()
        }
    }

    fun clearBoard() {
        positionImageView.animate().translationX(ballLinearLayout[0].x + (ballLinearLayout[0].width / 2) - positionImageView.width / 2).setDuration(0).start()

        setUpEmptyLayout(ballLinearLayout.size)
        selectedLottoBalls.clear()
        lottoBoard.ballList.clear()
        lottoBoard.powerBall = -1

        positionImageView.animate().alpha(1f).setStartDelay(200).setDuration(400).start()
    }

    fun setChooseNumbersOnClickListener(onClickListener: OnClickListener) {
        chooseNumberButton.setOnClickListener(onClickListener)
    }

    private fun ballSorter(lottoBall: LottoBallView): Int = lottoBall.ballNumber

    fun animateLayoutChanges() {
        ballLinearLayout.layoutTransition = LayoutTransition()
    }

    fun showErrorMessage(error: String) {
        val shakeAnimation = AnimationUtils.loadAnimation(context, R.anim.shake)
        innerConstraintLayout.startAnimation(shakeAnimation)
        errorTextView.visibility = View.VISIBLE
        errorTextView.text = error
        borderImageView.background = ContextCompat.getDrawable(context, R.drawable.lotto_error_board_border)
    }

    fun clearError() {
        errorTextView.visibility = View.GONE
        borderImageView.background = ContextCompat.getDrawable(context, R.drawable.lotto_board_border)
    }

    fun lottoBallListSize(): Int {
        return lottoBoard.ballList.size
    }

    fun setViewClickInterface(viewClickInterface: ViewClickInterface) {
        this.viewClickInterface = viewClickInterface
    }

    interface ViewClickInterface {
        fun onViewClicked(lottoBallView: LottoBallView)
    }
}