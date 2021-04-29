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
package styleguide.forms.maskedEditText

import android.content.Context
import android.text.InputFilter
import android.text.InputFilter.LengthFilter
import android.util.AttributeSet
import android.view.KeyEvent
import androidx.appcompat.widget.AppCompatEditText
import za.co.absa.presentation.uilib.R

class MaskedEditText(context: Context, attrs: AttributeSet?) : AppCompatEditText(context, attrs) {

    private var maskedFormatter: MaskedFormatter? = null
    private var maskedWatcher: MaskedWatcher? = null
    private var onImeBack: EditTextImeBackListener? = null

    init {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.MaskedEditText)
        if (typedArray.hasValue(R.styleable.MaskedEditText_mask)) {
            val mask = typedArray.getString(R.styleable.MaskedEditText_mask)
            if (mask != null && mask.isNotEmpty()) {
                setMask(mask)
            }
        }
        typedArray.recycle()
    }

    override fun onKeyPreIme(keyCode: Int, event: KeyEvent): Boolean {
        if (event.keyCode == KeyEvent.KEYCODE_BACK && event.action == KeyEvent.ACTION_UP) {
            onImeBack?.onBackEvent()
        }
        return super.dispatchKeyEvent(event)
    }

    fun setOnEditTextImeBackListener(listener: EditTextImeBackListener?) {
        onImeBack = listener
    }

    val unMaskedText: String
        get() {
            val text = text
            if (text == null) {
                return ""
            } else if (maskedFormatter == null) {
                return text.toString()
            }
            val formattedString = maskedFormatter?.formatString(text.toString())
            return formattedString?.unMaskedString.toString()
        }

    fun setMask(mask: String) {
        maskedFormatter = MaskedFormatter(mask)
        filters = arrayOf<InputFilter>(LengthFilter(mask.length))
        maskedWatcher?.let { removeTextChangedListener(it) }
        maskedWatcher = MaskedWatcher(maskedFormatter, this)
        addTextChangedListener(maskedWatcher)
    }
}