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
package styleguide.widgets

import android.content.Context
import android.content.res.TypedArray
import android.util.AttributeSet
import android.view.View
import androidx.appcompat.widget.AppCompatRadioButton
import androidx.constraintlayout.widget.ConstraintLayout
import kotlinx.android.synthetic.main.content_radio_button_view.view.*
import za.co.absa.presentation.uilib.R

class ContentRadioButtonView : ConstraintLayout {

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
        inflate(context, R.layout.content_radio_button_view, this)

        val typedArray: TypedArray = context.obtainStyledAttributes(attrs, R.styleable.ContentRadioButtonView)
        val text = typedArray.getString(R.styleable.ContentRadioButtonView_android_text)

        radioButton.text = text
        radioButton.setButtonDrawable(R.drawable.radio_button_view_button)

        typedArray.recycle()
    }

    fun setChecked(isChecked: Boolean) {
        radioButton.isChecked = isChecked
        secondaryLinearLayout.visibility = if (isChecked) VISIBLE else GONE
    }

    fun setSecondaryLinearLayout(view: View) {
        secondaryLinearLayout.addView(view)
    }

    fun isChecked() = radioButton.isChecked

    fun getRadioButton(): AppCompatRadioButton = radioButton
}