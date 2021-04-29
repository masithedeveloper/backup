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

import android.content.Context
import android.graphics.Rect
import android.util.AttributeSet
import android.view.Gravity
import android.view.View
import android.widget.LinearLayout
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import liblotto.lotto.R

class BetSelectorView : RecyclerView {

    private lateinit var boardListener: BoardSelectorInterface
    lateinit var adapter: BoardSelectorAdapter
    private var numberOfBallsOnBoard: Int = 0
    private var numberOfSelectableBalls: Int = 0
    private lateinit var selectableLottoBalls: ArrayList<SelectionNumbers>

    companion object {
        const val WIDTH_OF_LOTTO_BOARD = 6
    }

    constructor(context: Context) : super(context) {
        init(context)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init(context)
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init(context)
    }

    fun init(context: Context) {
        val params = LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)
        params.gravity = Gravity.CENTER_HORIZONTAL
        layoutParams = params
        layoutManager = GridLayoutManager(context, WIDTH_OF_LOTTO_BOARD)
        clipToPadding = false
        overScrollMode = View.OVER_SCROLL_NEVER
        isVerticalScrollBarEnabled = false
        isNestedScrollingEnabled = false
    }

    fun setupBoard(numberOfBallsOnBoard: Int, numberOfSelectableBalls: Int, boardListener: BoardSelectorInterface) {
        this.numberOfBallsOnBoard = numberOfBallsOnBoard
        this.numberOfSelectableBalls = numberOfSelectableBalls
        this.boardListener = boardListener
        buildAdapter()
    }

    private fun buildAdapter() {
        selectableLottoBalls = buildLottoNumbers()
        adapter = BoardSelectorAdapter(selectableLottoBalls, numberOfSelectableBalls, numberOfBallsOnBoard, boardListener)
        addItemDecoration(ItemOffsetDecoration())
        setAdapter(adapter)
    }

    private fun buildLottoNumbers(): ArrayList<SelectionNumbers> {
        val boardNumbers = arrayListOf<SelectionNumbers>()
        for (currentNumber in 1..numberOfBallsOnBoard) {
            val inputItem = SelectionNumbers()
            inputItem.number = currentNumber
            inputItem.isItemSelected = false
            boardNumbers.add(inputItem)
        }
        return boardNumbers
    }

    fun clearSelectedBalls() {
        adapter.clearSelectedNumbers()
    }

    fun randomiseLottoBalls() {
        if (::adapter.isInitialized) {
            adapter.randomiseSelection()
            adapter.isRandomising = true
        }
    }

    fun setSelectedBall(ball: Int) {
        selectableLottoBalls.find { it.number == ball }?.isItemSelected = true
        adapter.notifyDataUpdated()
    }

    fun setSelectedBalls(ballList: MutableList<Int>) {
        ballList.forEach { ballNumber ->
            selectableLottoBalls.find { it.number == ballNumber }?.isItemSelected = true
        }
        adapter.notifyDataUpdated()
    }

    inner class ItemOffsetDecoration : RecyclerView.ItemDecoration() {
        private val spacing: Float = context.resources.getDimension(R.dimen._15sdp)

        override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: State) {
            super.getItemOffsets(outRect, view, parent, state)
            val position = parent.getChildAdapterPosition(view)
            val column = position % WIDTH_OF_LOTTO_BOARD
            outRect.left = (column * spacing / WIDTH_OF_LOTTO_BOARD).toInt()
            outRect.right = (spacing - (column + 1) * spacing / WIDTH_OF_LOTTO_BOARD).toInt()
        }
    }

    override fun setEnabled(enabled: Boolean) {
        if (enabled) {
            adapter.enableBoard()
        } else {
            adapter.disableBoard()
        }
    }

    fun isBoardEnabled(): Boolean = adapter.isEnabled
}