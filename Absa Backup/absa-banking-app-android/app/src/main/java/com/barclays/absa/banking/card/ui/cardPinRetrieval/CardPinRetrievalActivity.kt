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
package com.barclays.absa.banking.card.ui.cardPinRetrieval

import android.os.Bundle
import com.barclays.absa.banking.R
import com.barclays.absa.banking.boundary.model.CardPin
import com.barclays.absa.banking.card.ui.creditCard.hub.ManageCardFragment.Companion.CARD_ITEM
import com.barclays.absa.banking.framework.BaseActivity
import com.barclays.absa.crypto.PinEncryptionUtils
import kotlinx.android.synthetic.main.card_pin_retrieval_activity.*
import com.barclays.absa.utils.AnalyticsUtil

class CardPinRetrievalActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.card_pin_retrieval_activity)
        setToolBarBack(getString(R.string.title_activity_retrieve_pin))
        populatePin()
        doneButton.setOnClickListener { onBackPressed() }
        AnalyticsUtil.trackAction("Pin Retrieval", "Pin Retrieval Success")
    }

    private fun populatePin() {
        val cardPin = intent.getSerializableExtra(CARD_ITEM) as CardPin
        if (!cardPin.cardPinBlock.isNullOrEmpty()) {
            pinSlideToRevealView.setHiddenText(PinEncryptionUtils().decodedPin(cardPin.cardPinBlock!!))
        }
    }
}
