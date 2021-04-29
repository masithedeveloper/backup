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

package com.barclays.absa.banking.shared

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.barclays.absa.banking.R
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

import kotlinx.android.synthetic.main.bottom_sheet_options_fragment.*

class BottomSheetOptionsFragment : BottomSheetDialogFragment() {

    private var optionOneOnClick: () -> Unit = { }
    private var optionTwoOnClick: () -> Unit = { }

    companion object {
        private const val TITLE = "title"
        private const val OPTION_ONE_TEXT = "optionOneText"
        private const val OPTION_TWO_TEXT = "optionTwoText"

        @JvmStatic
        fun newInstance(title: String, optionOneText: String, optionTwoText: String): BottomSheetOptionsFragment {
            return BottomSheetOptionsFragment().apply {
                arguments = Bundle().apply {
                    putString(TITLE, title)
                    putString(OPTION_ONE_TEXT, optionOneText)
                    putString(OPTION_TWO_TEXT, optionTwoText)
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(DialogFragment.STYLE_NORMAL, R.style.BottomSheetOptionsDialogTheme)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.bottom_sheet_options_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        arguments?.let {
            titleTextView.text = it.getString(TITLE, "")
            optionOneTextView.text = it.getString(OPTION_ONE_TEXT, "")
            optionTwoTextView.text = it.getString(OPTION_TWO_TEXT, "")
        }
        setupClickListeners()
    }

    private fun setupClickListeners() {
        optionOneTextView.setOnClickListener {
            optionOneOnClick.invoke()
            dismissAllowingStateLoss()
        }

        optionTwoTextView.setOnClickListener {
            optionTwoOnClick.invoke()
            dismissAllowingStateLoss()
        }

        cancelTextView.setOnClickListener {
            dismissAllowingStateLoss()
        }
    }

    fun setOptionOneClickListener(clickAction: () -> Unit) {
        optionOneOnClick = clickAction
    }

    fun setOptionTwoClickListener(clickAction: () -> Unit) {
        optionTwoOnClick = clickAction
    }
}