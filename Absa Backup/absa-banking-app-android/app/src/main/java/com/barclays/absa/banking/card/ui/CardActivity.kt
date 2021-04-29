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
package com.barclays.absa.banking.card.ui

import android.os.Bundle
import com.barclays.absa.banking.R
import com.barclays.absa.banking.card.services.card.dto.ManageCardResponseObject
import com.barclays.absa.banking.card.ui.creditCard.hub.ManageCardFragment
import com.barclays.absa.banking.card.ui.creditCard.hub.ManageCardFragment.Companion.CREDIT_CARD
import com.barclays.absa.banking.express.secondaryCard.getSecondaryCardMandate.dto.GetSecondaryCardMandateResponse
import com.barclays.absa.banking.framework.BaseActivity
import com.barclays.absa.banking.shared.BaseAlertDialog
import styleguide.utils.extensions.toTitleCase

class CardActivity : BaseActivity(), CardManagementView {

    companion object {
        const val CARD_NUMBER = "CARD_NUMBER"
        const val CARD_MANAGE_ITEM = "CARD_MANAGE_ITEM"
        const val SECONDARY_CARDS = "SECONDARY_CARDS"
    }

    override val isFromCardHub: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_card)
        val cardItem = intent.extras?.getParcelable<ManageCardResponseObject.ManageCardItem>(CARD_MANAGE_ITEM)
        val secondaryCard = intent.extras?.getParcelable<GetSecondaryCardMandateResponse>(SECONDARY_CARDS) as GetSecondaryCardMandateResponse

        if (cardItem != null) {
            cardItem.cardLimit?.cardType?.let { cardType ->
                val cardTypeTitle = if (CREDIT_CARD.equals(cardType, ignoreCase = true) || getString(R.string.credit_card_type).equals(cardType, ignoreCase = true))
                    getString(R.string.credit_card) else getString(R.string.debit_card)
                setToolBarBack(cardTypeTitle.toTitleCase()) { this.onBackPressed() }
            }

            supportFragmentManager.beginTransaction().apply {
                add(R.id.cardInformationConstraintLayout, ManageCardFragment.newInstance(cardItem, secondaryCard, getString(R.string.card)))
                commit()
            }
        } else {
            BaseAlertDialog.showErrorAlertDialog(getString(R.string.technical_difficulties_try_shortly)) { _, _ -> finish() }
        }
    }

    override fun onBackPressed() {
        if (supportFragmentManager.backStackEntryCount > 1) {
            removeFragments(1)
        } else {
            super.onBackPressed()
        }
    }
}