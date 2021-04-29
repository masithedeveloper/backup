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
package com.barclays.absa.banking.card.ui.cardViewDetails

import android.os.Bundle
import android.view.View
import com.barclays.absa.banking.R
import com.barclays.absa.banking.card.services.card.dto.CardDetails
import com.barclays.absa.banking.card.ui.creditCard.hub.ManageCardFragment.Companion.CARD_DETAILS
import com.barclays.absa.banking.card.ui.creditCard.hub.ManageCardFragment.Companion.CREDIT_CARD
import com.barclays.absa.banking.card.ui.creditCard.hub.ManageCardFragment.Companion.PAUSE_CARD
import com.barclays.absa.banking.framework.BaseActivity
import com.barclays.absa.utils.AnalyticsUtil
import kotlinx.android.synthetic.main.view_card_details_activity.*
import styleguide.cards.LargeCard
import styleguide.content.SlideToRevealView
import styleguide.utils.extensions.toFormattedAccountNumber

class ViewCardDetailsActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.view_card_details_activity)
        setToolBarBack(R.string.title_activity_card_details)
        populateFields()
    }

    private fun populateFields() {
        val cardDetails = intent.getParcelableExtra(CARD_DETAILS) ?: CardDetails()
        val isCardPaused = intent.getBooleanExtra(PAUSE_CARD, false)
        val isCreditCard = listOf(CREDIT_CARD, getString(R.string.credit_card_type), getString(R.string.credit_card)).any { cardType -> cardType.equals(cardDetails.cardType, true) }
        val cardType = if (isCreditCard) getString(R.string.credit_card) else getString(R.string.debit_card)

        expirySlideToRevealView.setUpSlider(cardDetails.expiryDate, "ViewCard_ViewCardDetailsScreen_SlideToRevealExpiryDate")
        cvvSlideToRevealView.setUpSlider(cardDetails.cvv, "ViewCard_ViewCardDetailsScreen_SlideToRevealCVV")
        bankCardView.setAccount(LargeCard(cardType, cardDetails.cardNumber.toFormattedAccountNumber()))
        doneButton.setOnClickListener { onBackPressed() }

        lockImageView.visibility = if (isCardPaused) View.VISIBLE else View.GONE
        lockTextView.visibility = if (isCardPaused) View.VISIBLE else View.GONE
    }

    private fun SlideToRevealView.setUpSlider(hiddenText: String, analyticsTag: String) {
        this.apply {
            setHiddenText(hiddenText)
            onActionDownExecuted = { actionExecuted ->
                if (actionExecuted) {
                    AnalyticsUtil.trackAction("View Card", analyticsTag)
                }
            }
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        AnalyticsUtil.trackAction("View Card", "ViewCard_ViewCardDetailsScreen_BackButtonClicked")
    }
}