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
 */

package styleguide.forms

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.CompoundButton
import androidx.constraintlayout.widget.ConstraintLayout
import kotlinx.android.synthetic.main.check_box_with_description_view.view.*
import za.co.absa.presentation.uilib.R

class CheckBoxWithDescriptionView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : ConstraintLayout(context, attrs, defStyleAttr) {
    private var isChecked: Boolean = false
    private var description: String? = ""
    private var secondaryDescription: String? = ""
    private var errorMessage: String? = ""
    private var isErrorMessageVisible: Boolean = false
    private var isRequired: Boolean = false
    private val onCheckedListener: OnCheckedListener? = null

    init {
        LayoutInflater.from(context).inflate(R.layout.check_box_with_description_view, this)

        val styledAttributes = context.obtainStyledAttributes(attrs, R.styleable.CheckBoxWithDescriptionView)
        isChecked = styledAttributes.getBoolean(R.styleable.CheckBoxWithDescriptionView_attribute_is_checked, isChecked)
        description = styledAttributes.getString(R.styleable.CheckBoxWithDescriptionView_attribute_description)
        secondaryDescription = styledAttributes.getString(R.styleable.CheckBoxWithDescriptionView_attribute_secondary_description)
        errorMessage = styledAttributes.getString(R.styleable.CheckBoxWithDescriptionView_attribute_view_error_message)
        isErrorMessageVisible = styledAttributes.getBoolean(R.styleable.CheckBoxWithDescriptionView_attribute_is_error_visible, isErrorMessageVisible)
        isRequired = styledAttributes.getBoolean(R.styleable.CheckBoxWithDescriptionView_attribute_is_required, isRequired)
        styledAttributes.recycle()

        checkBox?.setOnCheckedChangeListener { _: CompoundButton?, isChecked: Boolean ->
            checkBox.isChecked = isChecked
            onCheckedListener?.onChecked(isChecked)
        }

        checkBoxTextView.setOnClickListener {
            if (checkBox.visibility == VISIBLE) {
                checkBox.isChecked = !checkBox.isChecked
            }
        }

        setCheckBoxChecked(isChecked)
        setCheckBoxText(description)
        setErrorTextViewVisibility(isErrorMessageVisible)
        setErrorTextViewText(errorMessage)
        setSecondaryDescriptionText(secondaryDescription)

        checkBox.setButtonDrawable(R.drawable.check_box_view_button)
    }

    fun setErrorTextViewText(errorMessage: String?) {
        errorMessageTextView.text = errorMessage
    }

    fun setErrorTextViewVisibility(errorMessageVisible: Boolean) {
        errorMessageTextView.visibility = if (errorMessageVisible) VISIBLE else GONE
    }

    fun setCheckBoxText(description: String?) {
        checkBoxTextView.text = description
    }

    fun setCheckBoxChecked(isChecked: Boolean) {
        checkBox.isChecked = isChecked
    }

    fun setSecondaryDescriptionText(text: String?) {
        checkBoxSecondaryDescriptionTextView.text = text
    }

    fun setIsClickable(isClickable: Boolean) {
        checkBox.isClickable = isClickable
        checkBoxTextView.isClickable = isClickable
    }
}