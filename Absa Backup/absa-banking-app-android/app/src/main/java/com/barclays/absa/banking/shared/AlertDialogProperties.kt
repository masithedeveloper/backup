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

import android.content.DialogInterface
import android.widget.EditText
import android.widget.LinearLayout
import androidx.annotation.StringRes


class AlertDialogProperties private constructor(
        var title: String = "",
        var message: String = "",
        var positiveButtonText: String = "",
        var negativeButtonText: String = "",
        var positiveDismissListener: DialogInterface.OnClickListener? = null,
        var negativeDismissListener: DialogInterface.OnClickListener? = null,
        var editText: EditText? = null) {

    data class Builder(
            var title: String = "",
            var message: String = "",
            var positiveButton: String = "",
            var negativeButton: String = "",
            var positiveDismissListener: DialogInterface.OnClickListener? = null,
            var negativeDismissListener: DialogInterface.OnClickListener? = null,
            var editText: EditText? = null) {

        fun title(title: String?) = apply { this.title = title ?: "" }
        fun message(message: String?) = apply { this.message = message ?: "" }
        fun positiveButton(positiveButton: String) = apply { this.positiveButton = positiveButton }
        fun negativeButton(negativeButton: String) = apply { this.negativeButton = negativeButton }
        fun positiveDismissListener(positiveDismissListener: DialogInterface.OnClickListener?) = apply { this.positiveDismissListener = positiveDismissListener }
        fun negativeDismissListener(negativeDismissListener: DialogInterface.OnClickListener?) = apply { this.negativeDismissListener = negativeDismissListener }
        fun editText(editText: EditText) = apply { this.editText = editText }

        fun build(): AlertDialogProperties {
            return AlertDialogProperties(title, message, positiveButton, negativeButton, positiveDismissListener, negativeDismissListener, editText)
        }
    }

    fun setupEditText(@StringRes hint: Int) {
        editText?.layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT)
        editText?.hint = editText?.context?.getString(hint)
    }
}