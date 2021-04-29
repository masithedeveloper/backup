/*
 * Copyright (c) 2020 Absa Bank Limited, All Rights Reserved.
 *
 * This code is confidential to Absa Bank Limited and shall not be disclosed
 * outside the Bank without the prior written permission of the Absa Legal
 *
 * In the event that such disclosure is permitted the code shall not be copied
 * or distributed other than on a need-to-know basis and any recipients may be
 * required to sign a confidentiality undertaking in favor of Absa Bank Limited
 *
 */
package com.barclays.absa.banking.payments.swift.ui

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import com.barclays.absa.banking.R
import kotlinx.android.synthetic.main.swift_transaction_view.view.*
import styleguide.utils.extensions.toTitleCase

class SwiftTransactionView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : CardView(context, attrs, defStyleAttr) {

    init {
        inflate(context, R.layout.swift_transaction_view, this)
        setCardBackgroundColor(ContextCompat.getColor(context, R.color.transactions_card_background_colour))
        radius = resources.getDimension(R.dimen.medium_radius_sdp)
        cardElevation = resources.getDimension(R.dimen.card_elevation_normal)
    }

    fun setAmountText(amountValue: String) {
        if (amountValue.isNotEmpty()) {
            swiftLabelAmountTextView.text = amountValue
        } else {
            swiftLabelAmountTextView.visibility = View.GONE
        }
    }

    fun setSenderNameText(nameValue: String) {
        if (nameValue.isNotEmpty()) {
            swiftSenderNameTextView.text = nameValue.toTitleCase()
        } else {
            swiftSenderNameTextView.visibility = View.GONE
        }
    }

    fun setSwiftTransactionReference(referenceValue: String) {
        if (referenceValue.isNotEmpty()) {
            swiftReferenceLabelTextView.text = context.getString(R.string.swift_reference, referenceValue)
        } else {
            swiftReferenceLabelTextView.visibility = View.GONE
        }
    }

    fun setClaimedDateText(dateValue: String) {
        if (dateValue.isNotEmpty()) {
            swiftPaymentClaimedLabelTextView.text = dateValue
        } else {
            swiftPaymentClaimedLabelTextView.visibility = View.GONE
        }
    }

    fun setClaimedDateTextColour(colour: Int) {
        swiftPaymentClaimedLabelTextView.setTextColor(colour)
    }

    fun setArrowVisibility(visible: Boolean) {
        swiftArrowImageView.visibility = if (visible) {
            View.VISIBLE
        } else {
            View.INVISIBLE
        }
    }
}