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
import android.os.Handler
import android.os.Looper
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.barclays.absa.banking.R
import com.barclays.absa.banking.lotto.ui.LottoUtils.getCharForNumber
import lotto.LottoBoard
import lotto.LottoBoardView
import styleguide.utils.AnimationHelper

open class LottoNewTicketAdapter(var lottoTicketInterface: LottoTicketInterface?, var lottoBoards: MutableList<LottoBoard>) : RecyclerView.Adapter<LottoNewTicketAdapter.LottoNewTicketViewHolder>() {

    private lateinit var context: Context
    private var isDeleteVisible = false
    private var isDeleteDisabled = false

    companion object {
        const val ANIMATION_DURATION: Long = 300
        const val TOTAL_ANIMATION_DURATION: Long = 900
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LottoNewTicketViewHolder {
        val lottoBoardView = LottoBoardView(parent.context)
        val layoutParams = RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT, RecyclerView.LayoutParams.WRAP_CONTENT)
        layoutParams.topMargin = parent.context.resources.getDimensionPixelSize(liblotto.lotto.R.dimen.small_space)
        lottoBoardView.layoutParams = layoutParams
        context = parent.context
        return LottoNewTicketViewHolder(lottoBoardView)
    }

    override fun getItemCount(): Int {
        return lottoBoards.size
    }

    override fun onBindViewHolder(holder: LottoNewTicketViewHolder, position: Int) {
        holder.lottoBoard.setBalls(lottoBoards[position].ballList)
        holder.lottoBoard.setBoardLetter(lottoBoards[position].boardLetter)
        holder.lottoBoard.setPowerBall(lottoBoards[position].powerBall)

        if (lottoBoards[position].ballList.isEmpty()) {
            lottoBoards[position].isComplete = false
            holder.lottoBoard.showChooseNumberButton()
        } else {
            lottoBoards[position].isComplete = true
            holder.lottoBoard.hideChooseNumberButton()
        }

        if (isDeleteVisible) {
            holder.innerConstraintLayout.animate().translationX(-context.resources.getDimension(R.dimen.icon_size)).setDuration(ANIMATION_DURATION).start()
            holder.lottoBoard.showDeleteIcon()
        } else {
            holder.innerConstraintLayout.animate().translationX(0f).setDuration(ANIMATION_DURATION).start()
            holder.lottoBoard.hideDeleteIcon()
        }

        lottoTicketInterface?.apply {
            holder.lottoBoard.setChooseNumbersOnClickListener {
                if (isDeleteVisible) {
                    hideDeleteOptions()
                } else {
                    chooseNumberClicked(lottoBoards[holder.adapterPosition])
                }
            }

            holder.itemView.setOnTouchListener { _, event ->
                if (isDeleteVisible) {
                    hideDeleteOptions()
                    return@setOnTouchListener false
                }

                when {
                    event.action == MotionEvent.ACTION_CANCEL -> AnimationHelper.scaleOutTouchAnimation(holder.itemView)
                    event.action == MotionEvent.ACTION_DOWN -> AnimationHelper.scaleInTouchAnimation(holder.itemView)
                    event.action == MotionEvent.ACTION_UP -> {
                        chooseNumberClicked(lottoBoards[holder.adapterPosition])
                        AnimationHelper.scaleOutTouchAnimation(holder.itemView)
                    }
                }
                true
            }

            holder.deleteImageView.setOnClickListener {
                if (isDeleteDisabled) {
                    return@setOnClickListener
                }

                isDeleteDisabled = true
                it.postDelayed({ isDeleteDisabled = false }, TOTAL_ANIMATION_DURATION)

                if (lottoBoards.size > 2) {
                    val slideOutAnimation = AnimationUtils.loadAnimation(context, R.anim.slide_out)
                    slideOutAnimation.setAnimationListener(object : Animation.AnimationListener {
                        override fun onAnimationRepeat(animation: Animation?) {}
                        override fun onAnimationStart(animation: Animation?) {}

                        override fun onAnimationEnd(animation: Animation?) {
                            holder.lottoBoard.clearBoardLetter()
                            itemDeleted()
                            lottoBoards.removeAt(holder.adapterPosition)
                            notifyItemRemoved(holder.adapterPosition)

                            Handler(Looper.getMainLooper()).postDelayed({ updateBoardNames() }, ANIMATION_DURATION)
                        }
                    })

                    holder.itemView.startAnimation(slideOutAnimation)
                } else {
                    Toast.makeText(context, context.getString(R.string.lotto_minimum_boards_played), Toast.LENGTH_LONG).show()
                    AnimationHelper.shakeShakeAnimate(holder.itemView)
                }
            }
        }
    }

    private fun updateBoardNames() {
        var count = 0
        lottoBoards.forEachIndexed { index, lottoBoard ->
            val charForNumber = getCharForNumber(index)
            if (charForNumber != lottoBoard.boardLetter) {
                count++
                lottoBoard.boardLetter = charForNumber
                Handler(Looper.getMainLooper()).postDelayed({ notifyItemChanged(index) }, (count * 100).toLong())
            }
        }
    }

    fun showDeleteOptions() {
        isDeleteVisible = true
        notifyDataSetChanged()
    }

    fun hideDeleteOptions() {
        isDeleteVisible = false
        notifyDataSetChanged()
    }

    fun addNewBoard() {
        val lottoBoard = LottoBoard()
        lottoBoard.boardLetter = getCharForNumber(itemCount)
        lottoBoards.add(lottoBoard)
        notifyItemInserted(itemCount - 1)
    }

    class LottoNewTicketViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val lottoBoard: LottoBoardView = itemView as LottoBoardView
        val innerConstraintLayout: ConstraintLayout = itemView.findViewById(R.id.innerConstraintLayout)
        val deleteImageView: ImageView = itemView.findViewById(R.id.deleteImageView)
    }

    interface LottoTicketInterface {
        fun chooseNumberClicked(lottoBoard: LottoBoard)
        fun itemDeleted()
        fun hideDeleteOptions()
    }
}