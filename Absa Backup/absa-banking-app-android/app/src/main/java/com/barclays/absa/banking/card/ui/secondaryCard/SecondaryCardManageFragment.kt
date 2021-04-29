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
package com.barclays.absa.banking.card.ui.secondaryCard

import android.os.Bundle
import android.view.View
import com.barclays.absa.banking.R
import com.barclays.absa.banking.card.ui.secondaryCard.SecondaryCardActivity.Companion.SECONDARY_CARD_OBJECT
import com.barclays.absa.banking.express.secondaryCard.getSecondaryCardMandate.dto.GetSecondaryCardMandateResponse
import com.barclays.absa.banking.express.secondaryCard.getSecondaryCardMandate.dto.SecondaryCards
import com.barclays.absa.utils.AnalyticsUtil
import kotlinx.android.synthetic.main.secondary_card_manage_fragment.*
import styleguide.utils.extensions.toFormattedAccountNumber

class SecondaryCardManageFragment : SecondaryCardBaseFragment(R.layout.secondary_card_manage_fragment) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val secondaryCardMandates = arguments?.getParcelable<GetSecondaryCardMandateResponse>(SECONDARY_CARD_OBJECT) as GetSecondaryCardMandateResponse
        primaryAccountSecondaryContentAndLabelView.setContentText(secondaryCardMandates.primaryPlastic.toFormattedAccountNumber())

        if (secondaryViewModel.originalSecondaryCardList.isEmpty()) {
            secondaryViewModel.originalSecondaryCardList = secondaryCardMandates.secondaryCardMandateDetailsList.map { it.copy() }
        } else {
            nextButton.isEnabled = !isEqual(secondaryViewModel.originalSecondaryCardList, secondaryViewModel.secondaryCardMandates.secondaryCardMandateDetailsList)
        }

        secondaryViewModel.secondaryCardMandates = secondaryCardMandates
        secondaryCardsRecyclerView.adapter = SecondaryCardAdapter(secondaryCardMandates.secondaryCardMandateDetailsList, object : SecondaryCardAdapter.SecondaryCardClickListener {
            override fun onSecondaryCardCheckChangeListener(updatedSecondaryCards: ArrayList<SecondaryCards>) {
                secondaryViewModel.secondaryCardMandates.secondaryCardMandateDetailsList = updatedSecondaryCards
                nextButton.isEnabled = !isEqual(secondaryViewModel.originalSecondaryCardList, secondaryViewModel.secondaryCardMandates.secondaryCardMandateDetailsList)
            }
        })

        nextButton.setOnClickListener {
            AnalyticsUtil.trackAction("Secondary card", "SecondaryCard_EnableAccessScreen_NextButtonClicked")
            navigate(SecondaryCardManageFragmentDirections.actionSecondaryCardManageFragmentToSecondaryCardTermsAndConditionsFragment())
        }
    }

    fun <T> isEqual(first: List<T>, second: List<T>): Boolean {
        if (first.size != second.size) {
            return false
        }
        return first.zip(second).all { (x, y) -> x == y }
    }
}