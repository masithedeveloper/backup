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
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.account_view.view.*
import styleguide.utils.extensions.toSpecialFormattedAccountNumber
import za.co.absa.presentation.uilib.R

open class AccountView : ConstraintLayout {

    companion object {
        private const val CIA_ACCOUNT_DISPLAY_LIMIT = 5
    }

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, -1, true)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int, shouldInflateBase: Boolean) : super(context, attrs, defStyleAttr) {
        initialise(shouldInflateBase)
    }

    fun initialise(shouldInflateBase: Boolean) {
        if (shouldInflateBase) {
            LayoutInflater.from(context).inflate(R.layout.account_view, this)
        }
        setBackgroundResource(R.drawable.rounded_account_card)
        minimumHeight = resources.getDimensionPixelSize(R.dimen.card_min_height)
    }

    fun setAccount(account: Account?) {
        if (account != null) {
            amountTextView?.text = account.amount
            balanceLabelTextView?.text = account.balanceLabel
            cardNumberTextView?.text = account.cardNumber.toSpecialFormattedAccountNumber()
            accountNameTextView?.text = account.accountName
        } else {
            amountTextView?.text = ""
            balanceLabelTextView?.text = ""
            cardNumberTextView?.text = ""
            accountNameTextView?.text = ""
        }
    }

    fun setSingleCiaAccountAppearance() {
        balanceLabelTextView?.visibility = VISIBLE
        ciaBackgroundAccent?.visibility = VISIBLE
        balanceLabelTextView?.setTextColor(ContextCompat.getColor(context, R.color.graphite))
        accountNameTextView?.setTextColor(ContextCompat.getColor(context, R.color.graphite))
        amountTextView?.setTextColor(ContextCompat.getColor(context, R.color.graphite))
        cardNumberTextView?.setTextColor(ContextCompat.getColor(context, R.color.graphite))
        setBackground(R.drawable.rounded_account_card_light_grey)
    }

    fun setMultipleCiaAccountAppearance(currencyLabel: String, ciaAccountCount: Int) {
        currencyTextView?.visibility = VISIBLE
        currencyTextView?.text = currencyLabel
        currencyTextView?.setTextColor(ContextCompat.getColor(context, R.color.graphite))
        currencyTextView?.setPadding(0, 0, 0, 10)
        balanceLabelTextView?.visibility = GONE
        amountTextView?.visibility = INVISIBLE
        accountNameTextView?.setTextColor(ContextCompat.getColor(context, R.color.graphite))
        setBackground(R.drawable.rounded_account_card_light_grey)
        if (ciaAccountCount > CIA_ACCOUNT_DISPLAY_LIMIT) {
            balanceLabelTextView?.setTextColor(ContextCompat.getColor(context, R.color.graphite))
            balanceLabelTextView?.visibility = VISIBLE
        }
    }

    fun setBackground(backgroundResource: Int) {
        setBackgroundResource(backgroundResource)
    }

    fun enableNewColorScheme() {
        amountTextView?.setTextColor(ContextCompat.getColor(context, R.color.graphite))
        balanceLabelTextView?.setTextColor(ContextCompat.getColor(context, R.color.brownish_grey))
        cardNumberTextView?.setTextColor(ContextCompat.getColor(context, R.color.brownish_grey))
        accountNameTextView?.setTextColor(ContextCompat.getColor(context, R.color.graphite))
    }

    fun adjustPadding() {
        setPadding(resources.getDimensionPixelSize(R.dimen.small_space), paddingTop, paddingRight, paddingBottom)
    }

    fun showAlertImageView() {
        alertImageView?.visibility = VISIBLE
    }

    fun hideAlertImageView() {
        alertImageView?.visibility = GONE
    }
}