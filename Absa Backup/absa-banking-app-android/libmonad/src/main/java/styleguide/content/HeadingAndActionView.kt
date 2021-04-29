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
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.line_item_with_action_view.view.*
import za.co.absa.presentation.uilib.R

class HeadingAndActionView : ConstraintLayout {
    constructor(context: Context) : super(context) {
        init(context, null)
    }

    constructor(context: Context, attributes: AttributeSet) : super(context, attributes) {
        init(context, attributes)
    }

    constructor(context: Context, attributes: AttributeSet, defStyleAttr: Int) : super(context, attributes, defStyleAttr) {
        init(context, attributes)
    }

    private fun init(context: Context, attributes: AttributeSet? = null) {
        LayoutInflater.from(context).inflate(R.layout.line_item_with_action_view, this)

        val typedArray = context.obtainStyledAttributes(attributes, R.styleable.HeadingAndActionView)
        val labelText = typedArray.getString(R.styleable.HeadingAndActionView_attribute_primary_label_text)
        val buttonText = typedArray.getString(R.styleable.HeadingAndActionView_attribute_primary_action_text)
        val buttonColor = typedArray.getColor(R.styleable.HeadingAndActionView_attribute_primary_action_color, ContextCompat.getColor(context, R.color.pink))

        typedArray.recycle()

        actionTextView.setTextColor(buttonColor)
        actionTextView.text = buttonText
        labelTextView.text = labelText

        actionTextView.visibility = if (buttonText.isNullOrEmpty()) View.GONE else View.VISIBLE
    }

    fun setCustomActionOnclickListener(customOnClickListener: OnClickListener) {
        actionTextView.setOnClickListener(customOnClickListener)
    }

    fun setActionTextVisible() {
        actionTextView.visibility = View.VISIBLE
    }

    fun setActionTextGone() {
        actionTextView.visibility = View.GONE
    }
}