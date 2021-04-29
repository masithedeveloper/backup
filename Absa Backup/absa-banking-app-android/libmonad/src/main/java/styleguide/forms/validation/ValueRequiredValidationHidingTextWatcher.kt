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
package styleguide.forms.validation

import android.text.Editable
import android.text.TextWatcher
import styleguide.forms.BaseInputView

class ValueRequiredValidationHidingTextWatcher(private val baseInputView: BaseInputView<*>?, private val onValueChangeCompletionListener: ((String) -> Unit)?) : TextWatcher {
    constructor(inputView: BaseInputView<*>) : this(inputView, null) {
        inputView.addValueViewTextWatcher(this)
    }

    override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

    override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
        if (s.isNotEmpty()) {
            baseInputView?.showError(false)
        }
    }

    override fun afterTextChanged(s: Editable) {
        if (s.isNotEmpty()) {
            onValueChangeCompletionListener?.invoke(s.toString())
        }
    }
}