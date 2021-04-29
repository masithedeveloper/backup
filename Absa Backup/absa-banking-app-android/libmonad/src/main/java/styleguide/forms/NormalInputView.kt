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
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import styleguide.forms.validation.ValueRequiredValidationHidingTextWatcher
import za.co.absa.presentation.uilib.R

class NormalInputView<T : SelectorInterface> : BaseInputView<T> {
    private var customOnClickListener: OnClickListener? = null

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
            titleResourceId = R.styleable.NormalInputView_attribute_title_text
            descriptionId = R.styleable.NormalInputView_attribute_description_text
            errorTextId = R.styleable.NormalInputView_attribute_error_text
            isEditableId = R.styleable.NormalInputView_attribute_editable
            hintResourceId = R.styleable.NormalInputView_attribute_hint_text
            imageResourceId = R.styleable.NormalInputView_attribute_image
            selectorViewType = R.styleable.NormalInputView_attribute_selector_type
            inputType = R.styleable.NormalInputView_android_inputType
            maxLength = R.styleable.NormalInputView_android_maxLength
            contentDescriptionId = R.styleable.NormalInputView_android_contentDescription
            allowedCharacters = R.styleable.NormalInputView_android_digits
            maskId = R.styleable.NormalInputView_attribute_mask
        }
        super.init(context, attributes, R.styleable.NormalInputView, inputViewAttributes)
    }

    fun setIconImageViewDescription(description: String?) {
        val iconImageView = findViewById<ImageView>(R.id.icon_view)
        iconImageView.contentDescription = description
    }

    override fun onClick(v: View) {
        if (customOnClickListener == null) {
            super.onClick(v)
        } else {
            preventDoubleClick(v)
            customOnClickListener!!.onClick(v)
        }
    }

    fun setCustomOnClickListener(onClickListener: OnClickListener?) {
        customOnClickListener = onClickListener
    }

    fun clearCustomOnClickListener() {
        customOnClickListener = null
    }

    override val editText: EditText?
        get() = valueEditText

    override var errorTextView: TextView?
        get() = super.errorTextView
        set(errorTextView) {
            super.errorTextView = errorTextView
        }

    fun setEditTextFocusChangeListener(focusChangeListener: OnFocusChangeListener) {
        editText?.onFocusChangeListener = focusChangeListener
    }
}