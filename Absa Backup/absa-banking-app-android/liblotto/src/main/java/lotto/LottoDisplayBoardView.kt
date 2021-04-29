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

import android.content.Context
import android.os.Build
import android.util.AttributeSet
import androidx.core.widget.TextViewCompat
import kotlinx.android.synthetic.main.lotto_board.view.*
import liblotto.lotto.R

class LottoDisplayBoardView : LottoBoardView {

    constructor(context: Context, lottoBoardList: LottoBoard) : super(context) {
        this.lottoBoard = lottoBoardList
        init()
    }

    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init()
    }

    private fun init() {
        ballLinearLayout.removeAllViews()
        setBalls(lottoBoard.ballList)
        setBoardLetter(lottoBoard.boardLetter)
        setPowerBall(lottoBoard.powerBall)
        setTextAppearance()
        resetMargins()
        hideBorder()
        hideChooseNumberButton()
    }

    private fun setTextAppearance() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            boardTextView.setTextAppearance(R.style.NormalTextRegularDark)
            boardLetterTextView.setTextAppearance(R.style.NormalTextRegularDark)
        } else {
            TextViewCompat.setTextAppearance(boardTextView, R.style.NormalTextRegularDark)
            TextViewCompat.setTextAppearance(boardLetterTextView, R.style.NormalTextRegularDark)
        }
    }

    private fun resetMargins() {
        val boardTextViewLayoutParams = boardTextView.layoutParams as LayoutParams
        boardTextViewLayoutParams.marginStart = resources.getDimensionPixelSize(R.dimen.small_space)
        val boardLinearLayoutLayoutParams = boardLinearLayout.layoutParams as LayoutParams
        boardLinearLayoutLayoutParams.marginEnd = resources.getDimensionPixelSize(R.dimen.small_space)
    }
}