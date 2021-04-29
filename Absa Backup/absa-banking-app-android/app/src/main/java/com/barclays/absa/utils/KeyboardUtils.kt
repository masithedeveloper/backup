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
package com.barclays.absa.utils

import android.app.Activity
import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.barclays.absa.banking.framework.app.BMBApplication

object KeyboardUtils {

    private val topMostActivity
        get() = BMBApplication.getInstance().topMostActivity

    @JvmStatic
    fun hideKeyboard(activity: Activity) {
        var view = activity.currentFocus
        if (view == null) {
            view = View(activity)
        } else {
            view.clearFocus()
        }
        hideSoftKeyboard(view)
    }

    fun hideKeyboard(fragment: Fragment) {
        hideSoftKeyboard(fragment.view)
    }

    @JvmStatic
    fun hideSoftKeyboard(view: View?) {
        view?.apply {
            val inputMethodManager = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.hideSoftInputFromWindow(windowToken, 0)
        }
    }

    fun showSoftKeyboard() {
        (topMostActivity.getSystemService(AppCompatActivity.INPUT_METHOD_SERVICE) as InputMethodManager).toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY)
    }
}