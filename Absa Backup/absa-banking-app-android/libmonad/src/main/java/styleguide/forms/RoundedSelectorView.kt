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
import androidx.core.content.ContextCompat
import za.co.absa.presentation.uilib.R

class RoundedSelectorView<T : SelectorInterface> : SelectorView<T> {
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
        LayoutInflater.from(context).inflate(R.layout.rounded_selector_view, this)
        val inputViewAttributes = InputViewAttributes().apply {
            descriptionId = R.styleable.RoundedSelectorView_attribute_description_text
            errorTextId = R.styleable.RoundedSelectorView_attribute_error_text
            hintResourceId = R.styleable.RoundedSelectorView_attribute_hint_text
            imageResourceId = R.styleable.RoundedSelectorView_attribute_image
            selectorViewType = R.styleable.RoundedSelectorView_attribute_selector_type
            inputType = R.styleable.RoundedSelectorView_android_inputType
            maxLength = R.styleable.RoundedSelectorView_android_maxLength
            maskId = R.styleable.RoundedSelectorView_attribute_mask
        }
        super.init(context, attributes, R.styleable.RoundedSelectorView, inputViewAttributes)
    }

    fun setHint(hint: Int) {
        selectedValueTextView?.setTextColor(ContextCompat.getColor(context, R.color.grey))
        selectedValueTextView?.setText(hint)
    }

    override var selectedIndex: Int
        get() = super.selectedIndex
        set(index) {
            super.selectedIndex = index
            selectedValueTextView?.setTextColor(ContextCompat.getColor(context, R.color.graphite_light_theme_item_color))
        }

    override var selectedValue: String
        get() = super.selectedValue
        set(value) {
            super.selectedValue = value
            selectedValueTextView?.setTextColor(ContextCompat.getColor(context, R.color.graphite_light_theme_item_color))
        }

    fun setText(text: String) {
        selectedValue = text
    }
}