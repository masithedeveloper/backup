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
import android.graphics.PorterDuff
import android.util.AttributeSet
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import kotlinx.android.synthetic.main.lotto_ball.view.*
import liblotto.lotto.R

class LottoBallView : ConstraintLayout {

    var ballNumber = 0
    var isPowerBall = false
    var colorEnabled = true
        set(value) {
            field = value
            reColorBall(ballNumber)
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

    private fun init(context: Context) {
        View.inflate(context, R.layout.lotto_ball, this)
        clearBall()
    }

    fun setValue(value: Int) {
        ballNumber = value

        if (value < 10) {
            ballNumberTextView.text = String.format("0%s", value.toString())
        } else {
            ballNumberTextView.text = value.toString()
        }

        reColorBall(value)
    }

    fun setUnknownBall() {
        ballNumberTextView.text = "?"
        if (isPowerBall) {
            setUnpickedPowerBall()
        }
    }

    private fun setUnpickedPowerBall() {
        ballImageView.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_powerball_ball_no_color))
        val layoutParams = ballImageView.layoutParams
        layoutParams.height = resources.getDimensionPixelOffset(R.dimen.lotto_ball_unpicked_border_size)
        layoutParams.width = resources.getDimensionPixelOffset(R.dimen.lotto_ball_unpicked_border_size)
        ballImageView.layoutParams = layoutParams
    }

    private fun reColorBall(value: Int) {
        if (colorEnabled) {
            setBallColor(value)
        } else {
            clearColor()
        }
    }

    private fun setBallColor(value: Int) {
        var color = when {
            value <= 13 -> ContextCompat.getColor(context, R.color.lottoRed)
            value <= 26 -> ContextCompat.getColor(context, R.color.lottoYellow)
            value <= 39 -> ContextCompat.getColor(context, R.color.lottoGreen)
            else -> ContextCompat.getColor(context, R.color.lottoBlue)
        }

        if (isPowerBall) {
            color = ContextCompat.getColor(context, R.color.lottoBlue)
            ballImageView.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_power_ball_border))
            val layoutParams = ballImageView.layoutParams
            layoutParams.height = resources.getDimensionPixelOffset(R.dimen.lotto_bonus_ball_size)
            layoutParams.width = resources.getDimensionPixelOffset(R.dimen.lotto_bonus_ball_size)
            ballImageView.layoutParams = layoutParams
        } else {
            ballImageView.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_ball_border))
        }

        val wrappedDrawable = DrawableCompat.wrap(ballImageView.drawable.mutate())
        DrawableCompat.setTint(wrappedDrawable, color)
        DrawableCompat.setTintMode(wrappedDrawable, PorterDuff.Mode.SRC_IN)
        ballNumberTextView.setTextColor(ContextCompat.getColor(context, R.color.graphite_light_theme_item_color))
    }

    fun clearBall() {
        ballNumber = 0
        ballImageView.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_ball_unpicked))
        ballNumberTextView.text = ""
        ballNumberTextView.setTextColor(ContextCompat.getColor(context, R.color.graphite))
    }

    private fun clearColor() {
        if (isPowerBall) {
            setUnpickedPowerBall()
        } else {
            ballImageView.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_ball_unpicked))
            ballNumberTextView.setTextColor(ContextCompat.getColor(context, R.color.graphite))
        }
    }

    fun enlargeBall() {
        this.layoutParams.height = resources.getDimensionPixelSize(R.dimen.lotto_board_height)
        this.scaleX = 1.02f
        this.scaleY = 1.02f
    }
}