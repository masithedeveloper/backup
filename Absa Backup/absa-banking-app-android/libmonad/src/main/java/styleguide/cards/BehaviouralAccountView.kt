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
package styleguide.cards

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import kotlinx.android.synthetic.main.behavioural_account_view.view.*
import za.co.absa.presentation.uilib.R

class BehaviouralAccountView : AccountView {

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, -1)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr, false) {
        LayoutInflater.from(context).inflate(R.layout.behavioural_account_view, this)
    }

    fun setBehaviouralText(text: String) {
        if (text.isNotBlank()) {
            behaviouralTextView.text = text
            showBehaviouralChallenge()
        } else {
            hideBehaviouralChallenge()
        }
    }

    private fun showBehaviouralChallenge() {
        behaviouralTextView.visibility = VISIBLE
        behaviouralIconImageView.visibility = VISIBLE
    }

    private fun hideBehaviouralChallenge() {
        behaviouralTextView.visibility = View.GONE
        behaviouralIconImageView.visibility = View.GONE
    }
}