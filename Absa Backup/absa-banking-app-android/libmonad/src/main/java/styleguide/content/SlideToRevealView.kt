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

package styleguide.content

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.animation.AnimationUtils
import androidx.constraintlayout.widget.ConstraintLayout
import kotlinx.android.synthetic.main.slide_to_reveal_view.view.*
import za.co.absa.presentation.uilib.R
import kotlin.properties.Delegates

class SlideToRevealView : ConstraintLayout {
    private var initialXPosition: Float = 0F
    private var saveStateOfChevron: Boolean = false
    private var actionDownExecuted: Boolean by Delegates.observable(false) { _, _, newValue -> onActionDownExecuted?.invoke(newValue) }
    var onActionDownExecuted: ((Boolean) -> Unit)? = null

    companion object {
        private const val START_COORDINATE = 0F
        private const val DEGREE_OF_ANGLE = 180
        private const val SHORT_DURATION: Long = 250
    }

    constructor(context: Context) : super(context) {
        init(context, null)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init(context, attrs)
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init(context, attrs)
    }

    private fun init(context: Context, attrs: AttributeSet?) {
        LayoutInflater.from(context).inflate(R.layout.slide_to_reveal_view, this)
        val anim = AnimationUtils.loadAnimation(context, R.anim.gradual_shake)
        doubleRightArrowImageView.startAnimation(anim)

        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.SlideToRevealView)
        slideToRevealTextView.text = typedArray.getString(R.styleable.SlideToRevealView_attribute_action_description_text)
        dividerView.visibility = if (typedArray.getBoolean(R.styleable.SlideToRevealView_attribute_show_divider, false)) View.VISIBLE else View.GONE
        typedArray.recycle()

        sliderConstraintLayout.setOnTouchListener(::touchListener)
    }

    private fun touchListener(view: View, event: MotionEvent): Boolean {
        val widthOfScreen = view.width.toFloat() * 0.85F

        when (event.action) {
            MotionEvent.ACTION_MOVE -> {
                val xCoordinate = event.rawX
                if (xCoordinate - initialXPosition > 0) {
                    view.x = xCoordinate - initialXPosition
                    doubleRightArrowImageView.rotation = (view.x / widthOfScreen) * DEGREE_OF_ANGLE
                }
            }
            MotionEvent.ACTION_UP -> if (saveStateOfChevron) {
                doubleRightArrowImageView.animate().rotation(START_COORDINATE).setDuration(SHORT_DURATION).start()
                view.animate().x(START_COORDINATE).setDuration(SHORT_DURATION).start()
                saveStateOfChevron = false
            } else {
                doubleRightArrowImageView.animate().rotation(START_COORDINATE).setDuration(SHORT_DURATION).start()
                view.animate().x(START_COORDINATE).setDuration(SHORT_DURATION).start()
            }
            MotionEvent.ACTION_DOWN -> {
                initialXPosition = event.rawX
                actionDownExecuted = true
            }
        }
        return true
    }

    fun setHiddenText(hiddenText: String) {
        hiddenTextView.text = hiddenText
    }
}