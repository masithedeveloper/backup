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
package styleguide.cards

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import kotlinx.android.synthetic.main.bank_card_view.view.*
import styleguide.utils.extensions.toFormattedAccountNumber
import za.co.absa.presentation.uilib.R

class BankCardView : ConstraintLayout {

    constructor(context: Context) : super(context, null) {
        init(context)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init(context)
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init(context)
    }

    private fun init(context: Context) {
        LayoutInflater.from(context).inflate(R.layout.bank_card_view, this)
        setBackgroundResource(R.drawable.ic_wealth_horizontal)
    }

    fun setAccount(account: LargeCard) {
        cardNameTextView.text = account.cardName
        cardNumberTextView.text = account.cardNumber.toFormattedAccountNumber()
    }
}