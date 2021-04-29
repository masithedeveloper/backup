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
package lotto.jackpot

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.annotation.DrawableRes
import androidx.constraintlayout.widget.ConstraintLayout
import kotlinx.android.synthetic.main.jackpot_view.view.*
import liblotto.lotto.R

const val DRAW_TYPE_LOTTO = "Lotto"
const val DRAW_TYPE_LOTTO_PLUS1 = "LottoPlus1"
const val DRAW_TYPE_LOTTO_PLUS2 = "LottoPlus2"
const val DRAW_TYPE_POWERBALL = "Powerball"
const val DRAW_TYPE_POWERBALL_PLUS = "PowerballPlus"

class LottoJackpotView : ConstraintLayout {
    constructor(context: Context) : super(context) {
        init(context)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init(context)
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init(context)
    }

    private fun init(context: Context) {
        View.inflate(context, R.layout.jackpot_view, this)
    }

    fun setJackpotAmount(amount: String) {
        jackpotAmountTextView.text = amount
    }

    fun setLottoBadgeImageView(@DrawableRes badgeImageView: Int) {
        lotteryBadgeImageView.setImageResource(badgeImageView)
    }

}