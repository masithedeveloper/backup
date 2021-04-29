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
package com.barclays.absa.banking.card.ui.creditCard.hub

import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.lifecycle.MutableLiveData
import com.barclays.absa.banking.R
import com.barclays.absa.banking.card.services.card.dto.ManageCardResponseObject
import com.barclays.absa.banking.card.ui.CardIntentFactory
import com.barclays.absa.banking.express.secondaryCard.getSecondaryCardMandate.SecondaryCardFetchMandateViewModel
import com.barclays.absa.banking.express.secondaryCard.getSecondaryCardMandate.dto.GetSecondaryCardMandateResponse
import com.barclays.absa.banking.framework.BaseActivity
import com.barclays.absa.banking.presentation.adapters.CardClickListener
import com.barclays.absa.banking.presentation.adapters.CardsAdapter
import com.barclays.absa.banking.presentation.shared.IntentFactory
import com.barclays.absa.banking.riskBasedApproach.services.dto.RiskBasedApproachViewModel
import com.barclays.absa.utils.AnalyticsUtil
import com.barclays.absa.utils.viewModel
import kotlinx.android.synthetic.main.activity_card_list.*
import styleguide.utils.extensions.removeSpaces

class CardListActivity : BaseActivity(R.layout.activity_card_list), CardClickListener {
    private val viewModel by viewModels<RiskBasedApproachViewModel>()
    private val manageCardsViewModel by viewModels<ManageCardViewModel>()
    private lateinit var fetchSecondaryCardViewModel: SecondaryCardFetchMandateViewModel

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setToolBarBack(R.string.manage_card_title)
        fetchSecondaryCardViewModel = viewModel()

        viewModel.personalInformationResponse.observe(this, { personalInfo ->
            personalInfo.customerInformation?.clientType?.let {
                if (isBusinessAccount && !isSoleProprietor(it)) {
                    startActivity(IntentFactory.capabilityUnavailable(this, R.string.feature_unavailable, getString(R.string.business_banking_feature_unavailable)))
                } else {
                    manageCardsViewModel.getListOfCreditAndDebitCards()
                }
            }
        })
        viewModel.fetchPersonalInformation()

        manageCardsViewModel.manageCardResponse = MutableLiveData()
        manageCardsViewModel.manageCardResponse.observe(this, {
            manageCardsViewModel.manageCardResponse.removeObservers(this)
            listOfCardsRecyclerView.adapter = CardsAdapter(it, this)
            dismissProgressDialog()
        })
    }

    override fun onCardClickListener(mangeCardItem: ManageCardResponseObject.ManageCardItem, cardTypeForAnalytics: String) {
        AnalyticsUtil.trackAction("MANAGE_CARDS", "ManageCards_ManageCardsScreen_${cardTypeForAnalytics.removeSpaces()}Clicked")
        val cardType = mangeCardItem.cardLimit?.cardType ?: ""
        var secondaryCardData: GetSecondaryCardMandateResponse
        if (ManageCardFragment.CREDIT_CARD.equals(cardType, ignoreCase = true) || getString(R.string.credit_card_type).equals(cardType, ignoreCase = true)) {
            with(fetchSecondaryCardViewModel) {
                secondaryCardMandateLiveData = MutableLiveData()
                fetchSecondaryCardMandates(mangeCardItem.cardLimit?.cardNumber ?: "")
                secondaryCardMandateLiveData.observe(this@CardListActivity, {
                    dismissProgressDialog()
                    secondaryCardData = it
                    startActivity(CardIntentFactory.cardInformationIntent(this@CardListActivity, mangeCardItem, secondaryCardData))
                })
                failureLiveData.observe(this@CardListActivity, {
                    dismissProgressDialog()
                    startActivity(CardIntentFactory.cardInformationIntent(this@CardListActivity, mangeCardItem, GetSecondaryCardMandateResponse()))
                })
            }
        } else {
            startActivity(CardIntentFactory.cardInformationIntent(this, mangeCardItem, GetSecondaryCardMandateResponse()))
        }
    }

    override fun showNoCardsAvailable() {
        noCardsAvailableTextView.visibility = View.VISIBLE
        listOfCardsRecyclerView.visibility = View.GONE
    }
}