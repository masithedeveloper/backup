/*
 * Copyright (c) 2020 Absa Bank Limited, All Rights Reserved.
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
package styleguide.forms

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.TextView
import androidx.core.widget.TextViewCompat
import za.co.absa.presentation.uilib.R

class LargeInputView<T : SelectorInterface> : BaseInputView<T> {
    constructor(context: Context) : super(context) {
        init(context, null)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init(context, attrs)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init(context, attrs)
    }

    override fun init(context: Context, attributes: AttributeSet?) {
        LayoutInflater.from(context).inflate(R.layout.base_input_view, this)
        val inputViewAttributes = InputViewAttributes().apply {
            titleResourceId = R.styleable.LargeInputView_attribute_title_text
            descriptionId = R.styleable.LargeInputView_attribute_description_text
            errorTextId = R.styleable.LargeInputView_attribute_error_text
            isEditableId = R.styleable.LargeInputView_attribute_editable
            hintResourceId = R.styleable.LargeInputView_attribute_hint_text
            imageResourceId = R.styleable.LargeInputView_attribute_image
            selectorViewType = R.styleable.LargeInputView_attribute_selector_type
            inputType = R.styleable.LargeInputView_android_inputType
            maxLength = R.styleable.LargeInputView_android_maxLength
            allowedCharacters = R.styleable.LargeInputView_android_digits
            maskId = R.styleable.LargeInputView_attribute_mask
        }
        super.init(context, attributes, R.styleable.LargeInputView, inputViewAttributes)
        valueEditText?.let { TextViewCompat.setTextAppearance(it, R.style.LargeInputTextTheme) }
    }

    override var errorTextView: TextView?
        get() = super.errorTextView
        set(errorTextView) {
            super.errorTextView = errorTextView
        }
}