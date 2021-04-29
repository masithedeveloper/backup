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
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.View
import kotlinx.android.synthetic.main.image_text_check_box.view.*
import liblotto.lotto.R
import styleguide.forms.CheckBoxView

class ImageTextCheckBoxView : CheckBoxView {

    constructor(context: Context) : this(context, null)

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs, R.layout.image_text_check_box) {
        init(context, attrs)
    }

    private fun init(context: Context, attrs: AttributeSet?) {
        if (attrs != null) {
            val styledAttributes = context.obtainStyledAttributes(attrs, za.co.absa.presentation.uilib.R.styleable.ImageTextCheckBoxView)
            val imageResource = styledAttributes.getResourceId(za.co.absa.presentation.uilib.R.styleable.ImageTextCheckBoxView_attribute_image, -1)

            imageView.setImageResource(imageResource)
            styledAttributes.recycle()
        }

        setOnClickListener {
            if (checkBox.visibility == View.VISIBLE && alpha == 1f) {
                checkBox.isChecked = !checkBox.isChecked
            }
        }
    }

    fun setSubText(text: String) {
        checkBoxSubTextView.text = text
        checkBoxSubTextView.visibility = if (text.isEmpty()) GONE else View.VISIBLE
    }

    fun setImage(drawable: Drawable?) {
        imageView.setImageDrawable(drawable)
    }

    override fun setEnabled(enabled: Boolean) {
        checkBox.isClickable = enabled

        alpha = if (enabled) {
            1f
        } else {
            0.4f
        }
    }
}