/*
 * Copyright (c) 2019 Absa Bank Limited, All Rights Reserved.
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
package styleguide.utils

import android.widget.EditText

class PaymentsCurrencyTextWatcher(editText: EditText?, currency: String?, private val valueChangedObserver: ValueChangedObserver<String>) : CurrencyTextWatcher(editText, currency) {

    private lateinit var previousValue: String

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        super.beforeTextChanged(s, start, count, after)
        previousValue = s.toString()
    }

    override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
        valueChangedObserver.onValueChanged(previousValue, s.toString())
    }
}

interface ValueChangedObserver<T> {
    fun onValueChanged(oldValue: T, newValue: T): Boolean
}