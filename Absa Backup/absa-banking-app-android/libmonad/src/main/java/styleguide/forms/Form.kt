/*
 *  Copyright (c) 2020 Absa Bank Limited, All Rights Reserved.
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
package styleguide.forms

import android.view.View
import android.view.ViewGroup

class Form(private val parentView: ViewGroup? = null) {

    var inputViews: MutableList<SelectorView<*>> = mutableListOf()

    init {
        parentView?.let {
            val childCount = it.childCount
            if (childCount > 0) {
                for (i in 0..childCount) {
                    val currentChild = it.getChildAt(i)
                    if (currentChild is SelectorView<*>) {
                        inputViews.add(currentChild)
                    }
                }
            }
        }
    }

    fun addField(inputView: SelectorView<*>) {
        inputViews.add(inputView)
    }

    fun isValid(): Boolean {
        var result = true
        parentView?.let {
            if (it.visibility == View.VISIBLE) {
                inputViews.forEach { inputView ->
                    result = result && inputView.validateIfVisible()
                }
            } else {
                return true
            }
        }
        return result
    }
}