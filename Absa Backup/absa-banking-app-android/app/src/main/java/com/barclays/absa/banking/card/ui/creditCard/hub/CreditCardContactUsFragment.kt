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

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.barclays.absa.banking.R
import com.barclays.absa.banking.framework.utils.TelephoneUtil
import com.barclays.absa.banking.shared.ItemPagerFragment
import com.barclays.absa.utils.AnalyticsUtil
import kotlinx.android.synthetic.main.credit_card_contact_us_fragment.*

class CreditCardContactUsFragment : ItemPagerFragment() {
    private lateinit var creditCardHubActivity: CreditCardHubActivity

    override fun onAttach(context: Context) {
        super.onAttach(context)
        creditCardHubActivity = context as CreditCardHubActivity
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? = inflater.inflate(R.layout.credit_card_contact_us_fragment, container, false).rootView

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        southAfricaContactView.setOnClickListener {
            TelephoneUtil.call(creditCardHubActivity, getString(R.string.local_fraud_num))
            AnalyticsUtil.trackAction("Contact us tab", "CreditCardHub_ContactUsTab_LocalTelephoneClicked")
        }
        internationalContactView.setOnClickListener {
            TelephoneUtil.call(creditCardHubActivity, getString(R.string.international_fraud_num))
            AnalyticsUtil.trackAction("Contact us tab", "CreditCardHub_ContactUsTab_InternationalTelephoneClicked")
        }
        techLineContactView.setOnClickListener {
            TelephoneUtil.call(creditCardHubActivity, getString(R.string.credit_card_hub_technical_number))
            AnalyticsUtil.trackAction("Contact us tab", "CreditCardHub_ContactUsTab_TechnicalTelephoneClicked")
        }
        AnalyticsUtil.trackAction("Contact us tab", "CreditCardHub_ManageCardsTab_ScreenDisplayed")
    }

    override fun getTabDescription(): String = arguments?.getString(TAB_DESCRIPTION_KEY) ?: ""

    override fun onResume() {
        super.onResume()
        CreditCardHubActivity.hideKeyboard(creditCardHubActivity)
    }

    companion object {
        @JvmStatic
        fun newInstance(description: String?): CreditCardContactUsFragment {
            return CreditCardContactUsFragment().apply {
                arguments = Bundle().apply { putString(TAB_DESCRIPTION_KEY, description) }
            }
        }
    }
}