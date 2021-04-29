/*
 *  Copyright (c) 2021 Absa Bank Limited, All Rights Reserved.
 *
 *  This code is confidential to Absa Bank Limited and shall not be disclosed
 *  outside the Bank without the prior written permission of the Absa Legal
 *
 *  In the event that such disclosure is permitted the code shall not be copied
 *  or distributed other than on a need-to-know basis and any recipients may be
 *  required to sign a confidentiality undertaking in favor of Absa Bank
 *  Limited
 *
 */
package styleguide.content

import android.content.Context
import android.text.InputType
import android.util.AttributeSet
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.filter_and_search_view.view.*
import za.co.absa.presentation.uilib.R

class FilterAndSearchView : ConstraintLayout {

    constructor(context: Context) : super(context) {
        init(context, null)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init(context, attrs)
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init(context, attrs)
    }

    private fun init(context: Context, attributeSet: AttributeSet?) {
        inflate(context, R.layout.filter_and_search_view, this)
        val paddingTopBottom = resources.getDimensionPixelSize(R.dimen.medium_space)
        val paddingLeftRight = resources.getDimensionPixelSize(R.dimen.small_space)

        val typedArray = context.obtainStyledAttributes(attributeSet, R.styleable.FilterAndSearchView)
        setCalenderImageViewDescription(typedArray.getString(R.styleable.FilterAndSearchView_attribute_calender_content_description))
        setPadding(paddingLeftRight, paddingTopBottom, paddingLeftRight, paddingTopBottom)
        setSearchImageViewDescription(typedArray.getString(R.styleable.FilterAndSearchView_attribute_search_content_description))
        setSearchText(typedArray.getString(R.styleable.FilterAndSearchView_attribute_search_text))
        if (typedArray.getBoolean(R.styleable.FilterAndSearchView_attribute_enable_dark_background, false)) {
            setUpDarkSearchView()
        }
        typedArray.recycle()
    }

    private fun setSearchImageViewDescription(searchImageViewDescription: String?) {
        if (searchImageViewDescription != null) {
            searchImageView.contentDescription = searchImageViewDescription
        } else {
            throw IllegalArgumentException("The searchImageView content description is not set")
        }
    }

    private fun setCalenderImageViewDescription(calenderImageViewDescription: String?) {
        if (calenderImageViewDescription != null) {
            calenderImageView.contentDescription = calenderImageViewDescription
        } else {
            throw IllegalArgumentException("The calendarImageView content description is not set")
        }
    }

    fun setSearchText(searchText: String?) {
        searchTextView.text = searchText ?: ""
    }

    fun setEditable(editable: Boolean) {
        searchTextView.inputType = if (editable) {
            InputType.TYPE_CLASS_TEXT
        } else {
            InputType.TYPE_NULL
        }
    }

    private fun setUpDarkSearchView() {
        calendarLinearLayout.background = ContextCompat.getDrawable(context, R.drawable.dark_search_and_filter_background)
        searchImageView.background = ContextCompat.getDrawable(context, R.drawable.dark_search_and_filter_background)
    }

    fun setOnCalendarLayoutClickListener(onClickListener: OnClickListener?) {
        calendarLinearLayout.setOnClickListener(onClickListener)
    }

    fun setOnSearchClickListener(onClickListener: OnClickListener?) {
        searchImageView.setOnClickListener(onClickListener)
    }

    fun hideSearchView() {
        searchImageView.visibility = GONE
    }

    fun showSearchView() {
        searchImageView.visibility = VISIBLE
    }
}