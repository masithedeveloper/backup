/*
 *  Copyright (c) 2019 Absa Bank Limited, All Rights Reserved.
 *
 *  This code is confidential to Absa Bank Limited and shall not be disclosed
 *  outside the Bank without the prior written permission of the Absa Legal
 *
 *  In the event that such disclosure is permitted the code shall not be copied
 *  or distributed other than on a need-to-know basis and any recipients may be
 *  required to sign a confidentiality undertaking in favor of Absa Bank
 *  Limited
 *
 */
package lotto.betSelector

import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.bet_selector_number_item.view.*
import liblotto.lotto.R
import kotlin.random.Random

class BoardSelectorAdapter(private var selectableLottoBalls: ArrayList<SelectionNumbers>, private var numberOfSelectableBalls: Int, private var numberOfBallsOnBoard: Int, private var boardSelectorInterface: BoardSelectorInterface) : RecyclerView.Adapter<BoardSelectorAdapter.LottoNumberViewHolder>() {
    private var justClicked = false
    var isEnabled = true
    var numberOfSelectedNumbers = 0
    var isRandomising = false

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LottoNumberViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.bet_selector_number_item, parent, false)
        return LottoNumberViewHolder(view.rootView)
    }

    override fun onBindViewHolder(holder: LottoNumberViewHolder, position: Int) {
        val context = holder.itemView.context
        holder.lottoNumberButton.text = selectableLottoBalls[holder.adapterPosition].number.toString()
        if (selectableLottoBalls[holder.adapterPosition].isItemSelected) {
            pulseAnimation(holder.lottoNumberButton)
            holder.lottoNumberButton.background = ContextCompat.getDrawable(context, R.drawable.lotto_number_selected_button)
            holder.lottoNumberButton.setTextColor(ContextCompat.getColor(context, R.color.white))
        } else {
            holder.lottoNumberButton.background = ContextCompat.getDrawable(context, R.drawable.lotto_number_button)
            holder.lottoNumberButton.setTextColor(ContextCompat.getColor(context, R.color.graphite))
        }

        holder.lottoNumberButton.isEnabled = selectableLottoBalls[holder.adapterPosition].isBallEnabled

        holder.lottoNumberButton.setOnClickListener {
            if (!isRandomising && !boardSelectorInterface.isSorting()) {
                changeLottoNumberStyle(holder, it as Button)
                preventDoubleClick(it, 500)
            }
        }
    }

    fun preventDoubleClick(view: View, duration: Int) {
        if (!justClicked) {
            view.postDelayed({ justClicked = false }, duration.toLong())
        }
        justClicked = true
    }

    fun removeNumber(lottoBall: Button) {
        lottoBall.performClick()
    }

    fun changeLottoNumberStyle(viewHolder: LottoNumberViewHolder, lottoBall: Button) {
        if (justClicked) {
            return
        }

        val position = viewHolder.adapterPosition

        if (selectableLottoBalls[position].isItemSelected) {
            if (!isEnabled) {
                boardSelectorInterface.enableBoard()
            }

            lottoBall.isEnabled = true
            lottoBall.background = ContextCompat.getDrawable(lottoBall.context, R.drawable.lotto_number_button)
            lottoBall.setTextColor(ContextCompat.getColor(lottoBall.context, R.color.graphite))
            numberOfSelectedNumbers -= 1
            boardSelectorInterface.onNumberRemoved(selectableLottoBalls[position].number)
            selectableLottoBalls[position].isItemSelected = false
        } else if (numberOfSelectedNumbers < numberOfSelectableBalls && isEnabled) {
            lottoBall.background = ContextCompat.getDrawable(lottoBall.context, R.drawable.lotto_number_selected_button)
            lottoBall.setTextColor(ContextCompat.getColor(lottoBall.context, R.color.white))
            numberOfSelectedNumbers += 1
            selectableLottoBalls[position].isItemSelected = true
            boardSelectorInterface.onNumberSelected(selectableLottoBalls[position].number)
        }

        notifyItemChanged(position)
    }

    override fun getItemCount(): Int = selectableLottoBalls.size

    fun clearSelectedNumbers() {

        if (numberOfSelectedNumbers > 0) {
            selectableLottoBalls.forEach { selectionNumbers ->
                if (selectionNumbers.isItemSelected) {
                    selectionNumbers.isItemSelected = false
                }
            }
        }
        numberOfSelectedNumbers = 0
        notifyDataSetChanged()
    }

    fun randomiseSelection() {
        var numberOfRandomItemsToSelect = numberOfSelectableBalls - numberOfSelectedNumbers
        if (numberOfRandomItemsToSelect == 0) {
            clearSelectedNumbers()
            numberOfRandomItemsToSelect = numberOfSelectableBalls

            Handler(Looper.getMainLooper()).postDelayed({ randomiseBalls(numberOfRandomItemsToSelect) }, 1000)
        } else {
            randomiseBalls(numberOfRandomItemsToSelect)
        }
    }

    private fun randomiseBalls(numberOfRandomItemsToSelect: Int) {
        val randomLottoBallPosition = arrayListOf<Int>()
        for (i in 1..numberOfRandomItemsToSelect) {
            var randomNumbers = Random.nextInt(1, numberOfBallsOnBoard)
            while (selectableLottoBalls[randomNumbers].isItemSelected || randomLottoBallPosition.contains(randomNumbers) || !selectableLottoBalls[randomNumbers].isBallEnabled) {
                randomNumbers = Random.nextInt(1, numberOfBallsOnBoard)
            }
            randomLottoBallPosition.add(randomNumbers)
        }

        randomLottoBallPosition.forEachIndexed { index, item ->
            val handler = Handler(Looper.getMainLooper())
            handler.postDelayed({
                selectableLottoBalls[item].isItemSelected = true
                boardSelectorInterface.onNumberSelected(selectableLottoBalls[item].number)
                notifyItemChanged(item)

                if (index == randomLottoBallPosition.size - 1) {
                    boardSelectorInterface.randomiseComplete()
                    isRandomising = false
                }

            }, ((400 * index).toLong()))
        }

        numberOfSelectedNumbers = numberOfSelectableBalls
    }

    inner class LottoNumberViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var lottoNumberButton: Button = itemView.lottoNumberButton
    }

    private fun pulseAnimation(view: View) {
        val scaleDown = ObjectAnimator.ofPropertyValuesHolder(
                view,
                PropertyValuesHolder.ofFloat("scaleX", 1.2f),
                PropertyValuesHolder.ofFloat("scaleY", 1.2f))
        scaleDown.duration = 210

        scaleDown.repeatCount = 1
        scaleDown.repeatMode = ObjectAnimator.REVERSE

        scaleDown.start()
    }

    fun notifyDataUpdated() {
        numberOfSelectedNumbers = selectableLottoBalls.count { it.isItemSelected }
        notifyDataSetChanged()
    }

    fun changeBallState(ball: Int) {
        val ballPosition = ball - 1
        if (ballPosition < numberOfBallsOnBoard) {
            selectableLottoBalls[ballPosition].isBallEnabled = !selectableLottoBalls[ballPosition].isBallEnabled
            notifyItemChanged(ballPosition)
        }
    }

    fun disableBoard() {
        isEnabled = false
    }

    fun enableBoard() {
        isEnabled = true
    }
}