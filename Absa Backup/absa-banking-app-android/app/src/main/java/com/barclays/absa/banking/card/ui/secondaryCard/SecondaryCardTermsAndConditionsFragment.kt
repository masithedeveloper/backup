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
import com.barclays.absa.banking.boundary.model.CustomerProfileObject
import com.barclays.absa.utils.AnalyticsUtil
import kotlinx.android.synthetic.main.secondary_card_terms_and_conditions_fragment.*

class SecondaryCardTermsAndConditionsFragment : SecondaryCardBaseFragment(R.layout.secondary_card_terms_and_conditions_fragment) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val language = if (CustomerProfileObject.instance.languageCode == "A") "AFR" else "ENG"
        secondaryCardTermsAndConditionsPdfView.showPDF(secondaryViewModel.secondaryCardMandates.secondaryCardsMandatesTermsAndConditions.replace("#LANG#", language))

        continueButton.setOnClickListener {
            if (acceptTermsCheckBox.isChecked) {
                AnalyticsUtil.trackAction("Secondary card", "SecondaryCard_TermsAndConditionsScreen_CheckBoxChecked")
                navigate(SecondaryCardTermsAndConditionsFragmentDirections.actionSecondaryCardTermsAndConditionsFragmentToSecondaryCardSummaryFragment())
            } else {
                acceptTermsCheckBox.setErrorMessage(getString(R.string.secondary_card_terms_and_conditions_error_message))
            }
        }

        acceptTermsCheckBox.setOnCheckedListener { acceptTermsCheckBox.clearError() }
    }
}