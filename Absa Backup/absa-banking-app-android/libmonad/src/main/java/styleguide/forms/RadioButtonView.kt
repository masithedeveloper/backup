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
import android.os.Bundle
import android.os.Parcelable
import android.util.AttributeSet
import android.view.View
import android.widget.LinearLayout
import android.widget.RadioGroup
import androidx.appcompat.widget.AppCompatRadioButton
import androidx.constraintlayout.widget.ConstraintLayout
import kotlinx.android.synthetic.main.radio_button_view.view.*
import styleguide.utils.AnimationHelper
import styleguide.utils.ViewUtils
import za.co.absa.presentation.uilib.R

class RadioButtonView<T : SelectorInterface?> : ConstraintLayout {

    private var items: SelectorList<*> = SelectorList<T>()
    private var errorMessage: String? = ""
    private var isErrorMessageVisible = false
    private var isRequired = false
    private var margin = 0f
    private var buttonResourceId = 0
    private var groupOrientation = LinearLayout.VERTICAL
    private var itemCheckedInterface: ItemCheckedInterface? = null
    private var isItemCheckListenerEnabled = true

    companion object {
        private const val SUPER_INSTANCE_STATE = "saved_instance_state_parcelable"
        private const val SELECTED_INDEX = "selected_index"
    }

    var selectedIndex = -1
        set(value) {
            field = value
            if (value < radioGroup.childCount && value >= 0) {
                (radioGroup.getChildAt(value) as AppCompatRadioButton).isChecked = true
            }
        }

    private fun init(context: Context, attrs: AttributeSet?) {
        View.inflate(context, R.layout.radio_button_view, this)
        wireUpComponents()
        setAttributes(context, attrs)
        radioGroup.orientation = groupOrientation
        radioGroup.setOnCheckedChangeListener { _: RadioGroup?, _: Int -> setErrorTextViewVisibility(!isValid) }
        errorMessageTextView.text = errorMessage
    }

    private fun setAttributes(context: Context, attrs: AttributeSet?) {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.RadioButtonView)
        errorMessage = typedArray.getString(R.styleable.RadioButtonView_attribute_error_text)
        isErrorMessageVisible = typedArray.getBoolean(R.styleable.RadioButtonView_attribute_is_error_visible, isErrorMessageVisible)
        isRequired = typedArray.getBoolean(R.styleable.RadioButtonView_attribute_is_required, isRequired)
        margin = typedArray.getDimension(R.styleable.RadioButtonView_attribute_radio_margin, resources.getDimension(R.dimen.medium_space))
        buttonResourceId = typedArray.getResourceId(R.styleable.RadioButtonView_android_button, R.drawable.radio_button_view_button)
        groupOrientation = typedArray.getInt(R.styleable.RadioButtonView_android_orientation, LinearLayout.VERTICAL)
        typedArray.recycle()
    }

    override fun onSaveInstanceState(): Parcelable? {
        Bundle().apply {
            putParcelable(SUPER_INSTANCE_STATE, super.onSaveInstanceState())
            putInt(SELECTED_INDEX, selectedIndex)
            return this
        }
    }

    override fun onRestoreInstanceState(state: Parcelable) {
        val bundle = state as Bundle
        super.onRestoreInstanceState(bundle.getParcelable(SUPER_INSTANCE_STATE))
        this.selectedIndex = bundle.getInt(SELECTED_INDEX, -1)
    }

    constructor(context: Context) : super(context) {
        init(context, null)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init(context, attrs)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init(context, attrs)
    }

    val isValid: Boolean
        get() = if (isRequired) selectedValue != null else true

    fun setErrorMessage(value: String) {
        setErrorMessage(value, true)
    }

    fun setErrorMessage(value: String, shouldShake: Boolean) {
        if (shouldShake) {
            AnimationHelper.shakeShakeAnimate(this)
        }
        errorMessageTextView.text = value
        errorMessageTextView.visibility = View.VISIBLE
    }

    fun showError() {
        errorMessageTextView.visibility = View.VISIBLE
    }

    fun hideError() {
        errorMessageTextView.visibility = View.GONE
    }

    fun setIsRequired(value: Boolean) {
        isRequired = value
    }

    fun validate() {
        if (isValid) {
            hideError()
        } else {
            showError()
        }
        setErrorTextViewVisibility(!isValid)
    }

    fun getDataSourceCount() = items.size

    fun setDataSource(items: SelectorList<*>?) {
        setDataSource(items, -1)
    }

    fun setDataSource(items: SelectorList<*>?, selectedItemIndex: Int) {
        if (items == null) {
            return
        }

        this.items = items

        this.items.forEach { item ->
            val radioButton = AppCompatRadioButton(context, null).apply {
                ViewUtils.setTextAppearance(this, context, R.style.RadioButtonTextAppearance)
                text = item?.displayValue
                setPadding(margin.toInt(), margin.toInt(), margin.toInt(), margin.toInt())
                tag = item
                id = ViewUtils.generateViewId()
                setButtonDrawable(buttonResourceId)
            }
            radioGroup.addView(radioButton)
        }
        radioGroup.setOnCheckedChangeListener { group: RadioGroup, checkedId: Int ->
            if (checkedId > -1) {
                val appCompatRadioButton: AppCompatRadioButton = group.findViewById(checkedId)
                if (appCompatRadioButton.isChecked) {
                    selectedIndex = group.indexOfChild(appCompatRadioButton)
                    if (isItemCheckListenerEnabled) {
                        itemCheckedInterface?.onChecked(selectedIndex)
                    }
                }
            }
        }
        if (selectedItemIndex < radioGroup.childCount && selectedItemIndex > -1) {
            (radioGroup.getChildAt(selectedItemIndex) as AppCompatRadioButton).isChecked = true
        }
        if (isErrorMessageVisible) {
            showError()
        } else {
            hideError()
        }
        setErrorTextViewVisibility(isErrorMessageVisible)
    }

    @Suppress("UNCHECKED_CAST")
    val selectedValue: T?
        get() = if (radioGroup.checkedRadioButtonId == -1) null else radioGroup.findViewById<View>(radioGroup.checkedRadioButtonId).tag as T

    fun clearGroupChecks() {
        radioGroup.clearCheck()
    }

    fun getRadioGroup(): RadioGroup? = radioGroup

    fun clearSelection() {
        selectedIndex = -1
        clearGroupChecks()
        if (radioGroup.checkedRadioButtonId >= 0) {
            val appCompatRadioButton: AppCompatRadioButton = radioGroup.findViewById(radioGroup.checkedRadioButtonId)
            appCompatRadioButton.isChecked = false
        }
    }

    private fun setErrorTextViewVisibility(isVisible: Boolean) {
        errorMessageTextView.visibility = if (isVisible) View.VISIBLE else View.GONE
    }

    private fun wireUpComponents() {
        radioGroup.isSaveEnabled = false
    }

    fun setItemCheckedInterface(itemCheckedInterface: ItemCheckedInterface) {
        this.itemCheckedInterface = itemCheckedInterface
    }

    fun disableItemCheckInterface() {
        isItemCheckListenerEnabled = false
    }

    fun enableItemCheckInterface() {
        isItemCheckListenerEnabled = true
    }

    fun disableRadioItem(index: Int) {
        radioGroup.getChildAt(index).isEnabled = false
    }

    fun disableRadioGroup() {
        for (item in 0 until radioGroup.childCount) {
            radioGroup.getChildAt(item).isEnabled = false
        }
    }

    fun enableRadioGroup() {
        for (item in 0 until radioGroup.childCount) {
            radioGroup.getChildAt(item).isEnabled = true
        }
    }

    @Suppress("UNCHECKED_CAST")
    fun getSelectedValue(selectedIndex: Int): T {
        return items[selectedIndex] as T
    }
}