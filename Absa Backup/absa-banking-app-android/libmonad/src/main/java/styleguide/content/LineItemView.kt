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
import android.graphics.Typeface
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.TextView
import androidx.annotation.ColorInt
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.line_item_view.view.*
import za.co.absa.presentation.uilib.R

open class LineItemView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : ConstraintLayout(context, attrs, defStyleAttr) {

    init {
        LayoutInflater.from(context).inflate(R.layout.line_item_view, this)

        context.obtainStyledAttributes(attrs, R.styleable.LineItemView).apply {
            val label = getString(R.styleable.LineItemView_attribute_label_text)
            val content = getString(R.styleable.LineItemView_attribute_content_text)
            val textColor = getColor(R.styleable.LineItemView_android_textColor, ContextCompat.getColor(context, R.color.graphite))
            val maxLines = getInteger(R.styleable.LineItemView_attribute_max_lines, 1)

            setLineItemViewLabel(label)
            setTextColor(textColor)
            setLineItemViewContent(content)
            setMaxLines(maxLines)

            recycle()
        }
    }

    fun setTextColor(@ColorInt textColor: Int) {
        labelTextView.setTextColor(textColor)
        contentTextView.setTextColor(textColor)
    }

    fun setLineItemViewLabel(label: String?) {
        label?.let { labelTextView.text = it }
    }

    fun setLineItemViewContent(content: String?) {
        content?.let { contentTextView.text = it }
    }

    fun setViewMargins(margin: Int) {
        val labelLayoutParams = labelTextView.layoutParams as LayoutParams
        labelLayoutParams.marginStart = margin

        val contentTextView = contentTextView.layoutParams as LayoutParams
        contentTextView.marginEnd = margin
    }

    fun setLineItemContentToBold() {
        contentTextView.typeface = Typeface.DEFAULT_BOLD
    }

    fun getLabelTextView(): TextView = labelTextView

    fun getContentTextView(): TextView = contentTextView

    private fun setMaxLines(maxLines: Int) {
        contentTextView.maxLines = maxLines
    }
}