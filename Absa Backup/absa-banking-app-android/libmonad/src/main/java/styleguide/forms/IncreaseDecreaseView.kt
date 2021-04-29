/*
 *  Copyright (c) 2020 Absa Bank Limited, All Rights Reserved.
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
package styleguide.forms

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.widget.FrameLayout
import kotlinx.android.synthetic.main.fragment_increase_decrease_view.view.*
import za.co.absa.presentation.uilib.R

class IncreaseDecreaseView : FrameLayout {

    private var title: String = ""
    private var description: String = ""
    private var minimumValue: Int = 0
    private var maximumValue: Int = 0
    private var initialValue: Int = 1
    var currentValue: Int = 1

    var onValueChangeListener: OnValueChangeListener? = null

    interface OnValueChangeListener {
        fun onValueChange(value: Int)
    }

    constructor(context: Context) : super(context) {
        setup(null)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        setup(attrs)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        setup(attrs)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) : super(context, attrs, defStyleAttr, defStyleRes) {
        setup(attrs)
    }

    private fun setup(attrs: AttributeSet?) {
        LayoutInflater.from(context).inflate(R.layout.fragment_increase_decrease_view, this)
        if (attrs != null) {
            val typedArray = context.obtainStyledAttributes(attrs, R.styleable.IncreaseDecreaseView)
            typedArray.apply {
                title = getString(R.styleable.IncreaseDecreaseView_attribute_increase_decrease_view_title) ?: ""
                description = getString(R.styleable.IncreaseDecreaseView_attribute_increase_decrease_view_description) ?: ""
                currentValue = getInteger(R.styleable.IncreaseDecreaseView_attribute_increase_decrease_view_initial_value, initialValue)
                minimumValue = getInteger(R.styleable.IncreaseDecreaseView_attribute_increase_decrease_view_min_value, 0)
                maximumValue = getInteger(R.styleable.IncreaseDecreaseView_attribute_increase_decrease_view_max_value, 1_000_000)
            }.recycle()
        }

        setTitle(title)
        setDescription(description)
        setupCurrentValue(currentValue)

        decreaseImageView.setOnClickListener { decreaseValue() }
        increaseImageView.setOnClickListener { increaseValue() }
    }

    fun setupCurrentValue(newCurrentValue: Int) {
        if (newCurrentValue in minimumValue..maximumValue) {
            currentValue = newCurrentValue
            setupViewForCurrentValue()
        } else {
            Log.e(IncreaseDecreaseView::class.simpleName, "Set current value in range of min..max")
        }
    }

    fun setTitle(title: String) {
        this.title = title
        titleTextView.text = title
    }

    fun setDescription(description: String) {
        this.description = description
        descriptionTextView.text = description
    }

    fun increaseValue() {
        if (currentValue == maximumValue) {
            return
        }
        currentValue++
        setupViewForCurrentValue()
    }

    fun decreaseValue() {
        if (currentValue == minimumValue) {
            return
        }
        currentValue--
        setupViewForCurrentValue()
    }

    fun setMinimumValue(minimumValue: Int) {
        this.minimumValue = minimumValue
        setupViewForCurrentValue()
    }

    fun setMaximumValue(maximumValue: Int) {
        this.maximumValue = maximumValue
        setupViewForCurrentValue()
    }

    private fun setupViewForCurrentValue() {
        currentValueTextView.text = currentValue.toString()
        onValueChangeListener?.onValueChange(currentValue)
        val increaseDrawable = when {
            currentValue == maximumValue -> R.drawable.ic_increase_disabled
            currentValue > initialValue -> R.drawable.ic_increase_new_value
            else -> R.drawable.ic_increase_enabled
        }

        val decreaseDrawable = when {
            currentValue == minimumValue -> R.drawable.ic_decrease_disabled
            currentValue > minimumValue -> R.drawable.ic_decrease_enabled
            else -> R.drawable.ic_decrease_disabled
        }
        decreaseImageView.setImageResource(decreaseDrawable)
        increaseImageView.setImageResource(increaseDrawable)
    }
}