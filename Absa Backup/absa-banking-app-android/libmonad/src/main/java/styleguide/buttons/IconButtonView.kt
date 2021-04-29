/*
 * Copyright (c) 2021 Absa Bank Limited, All Rights Reserved.
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

package styleguide.buttons

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.icon_button_view.view.*
import za.co.absa.presentation.uilib.R
import za.co.absa.presentation.uilib.R.styleable

class IconButtonView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : ConstraintLayout(context, attrs, defStyleAttr) {

    init {
        View.inflate(context, R.layout.icon_button_view, this)
        val typedArray = context.obtainStyledAttributes(attrs, styleable.IconButtonView)
        textView.text = (typedArray.getString(styleable.IconButtonView_attribute_title))
        imageView.setImageResource(typedArray.getResourceId(styleable.IconButtonView_attribute_icon, -1))
        imageView.contentDescription = context.getString(R.string.icon_button_view_content_description, textView.text.toString())

        val backgroundResourceId = typedArray.getResourceId(styleable.IconButtonView_android_background, -1)
        background = if (backgroundResourceId != -1) {
            ContextCompat.getDrawable(context, backgroundResourceId)
        } else {
            ContextCompat.getDrawable(context, R.drawable.fwab_button_light)
        }
        typedArray.recycle()
    }

    fun setIcon(@DrawableRes icon: Int, @StringRes iconContentDescription: Int = -1) {
        imageView.setImageResource(icon)
        if (iconContentDescription != -1) {
            imageView.contentDescription = context.getString(R.string.icon_button_view_content_description, context.getString(iconContentDescription))
        }
    }

    fun setText(text: String) {
        textView.text = text
    }

    fun setText(@StringRes text: Int) {
        textView.text = context.getString(text)
    }
}