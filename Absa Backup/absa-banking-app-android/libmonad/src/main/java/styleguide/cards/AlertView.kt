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
package styleguide.cards

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import androidx.annotation.DrawableRes
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.alert_view.view.*
import za.co.absa.presentation.uilib.R

class AlertView : ConstraintLayout {

    constructor(context: Context) : super(context, null) {
        init(context, null)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs, -1) {
        init(context, attrs)
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init(context, attrs)
    }

    private fun init(context: Context, attributes: AttributeSet?) {
        LayoutInflater.from(context).inflate(R.layout.alert_view, this)

        val typedArray = context.obtainStyledAttributes(attributes, R.styleable.AlertView)
        val isDark = typedArray.getBoolean(R.styleable.AlertView_attribute_is_dark, false)
        if (isDark) {
            val colorDark = ContextCompat.getColor(context, R.color.graphite_light_theme_item_color)
            labelTextView.setTextColor(colorDark)
            textTextView.setTextColor(colorDark)
            arrowRightImageView.setImageResource(R.drawable.ic_arrow_right_dark)
        }

        val offerImage = typedArray.getResourceId(R.styleable.AlertView_attribute_image, -1)
        if (offerImage != -1) {
            offerConstraintLayout.background = ContextCompat.getDrawable(context, offerImage)
        } else {
            throw RuntimeException("Set AlertView image please")
        }

        typedArray.recycle()
    }

    fun hideTextView() {
        textTextView.visibility = View.GONE
    }

    fun setAlert(alert: Alert) {
        labelTextView.text = alert.label

        if (alert.text.isEmpty()) {
            hideTextView()
        } else {
            textTextView.text = alert.text
        }
    }

    fun setImageViewWidget(@DrawableRes resourceId: Int) {
        arrowRightImageView.setImageResource(resourceId)
    }

    fun hideRightImage() {
        arrowRightImageView.visibility = View.GONE
    }

    fun showRightImage() {
        arrowRightImageView.visibility = View.VISIBLE
    }

    fun setImage(resourceId: Int) {
        offerConstraintLayout.background = ContextCompat.getDrawable(context, resourceId)
    }
}
