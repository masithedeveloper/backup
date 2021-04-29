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
package com.barclays.absa.banking.virtualpayments.scan2Pay.ui.views

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.barclays.absa.banking.R
import kotlinx.android.synthetic.main.fragment_absa_vertical_card.*

class AbsaVerticalCardFragment : Fragment(R.layout.fragment_absa_vertical_card) {

    companion object {
        private const val CARD_TYPE = "CARD_TYPE"
        private const val CARD_NUMBER = "CARD_NUMBER"

        fun newInstance(cardType: String, cardNumber: String): AbsaVerticalCardFragment = AbsaVerticalCardFragment().apply {
            arguments = Bundle().apply {
                putString(CARD_TYPE, cardType)
                putString(CARD_NUMBER, cardNumber)
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        arguments?.let {
            cardTypeTextView.text = it.getString(CARD_TYPE) ?: ""
            cardNumberTextView.text = it.getString(CARD_NUMBER) ?: ""
        }
    }
}