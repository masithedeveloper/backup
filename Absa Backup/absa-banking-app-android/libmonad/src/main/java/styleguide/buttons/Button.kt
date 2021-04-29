/*
 * Copyright (c) 2021 Absa Bank Limited, All Rights Reserved.
 *
 * This code is confidential to Absa Bank Limited and shall not be disclosed
 * outside the Bank without the prior written permission of the Absa Legal
 *
 * In the event that such disclosure is permitted the code shall not be copied
 * or distributed other than on a need-to-know basis and any recipients may be
 * required to sign a confidentiality undertaking in favor of Absa Bank
 * Limited
 */

package styleguide.buttons

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.View.OnClickListener
import androidx.appcompat.widget.AppCompatButton

class Button(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : AppCompatButton(context, attrs, defStyleAttr) {

    constructor(context: Context) : this(context, null, 0)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

    companion object {
        private const val TWO_SECOND_DELAY = 2000L
    }

    override fun setOnClickListener(onClickListener: OnClickListener?) {
        val onClick = OnClickListener {
            preventDoubleClick(it)
            onClickListener?.onClick(it)
        }
        super.setOnClickListener(onClick)
    }

    fun preventDoubleClick(view: View) {
        view.isClickable = false
        view.postDelayed({ view.isClickable = true }, TWO_SECOND_DELAY)
    }
}