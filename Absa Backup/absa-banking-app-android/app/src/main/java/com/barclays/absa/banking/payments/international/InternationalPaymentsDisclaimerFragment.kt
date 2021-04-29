/*
 * Copyright (c) 2018 Absa Bank Limited, All Rights Reserved.
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

package com.barclays.absa.banking.payments.international

import android.os.Bundle
import android.view.View
import com.barclays.absa.banking.R
import com.barclays.absa.banking.payments.international.InternationalPaymentsConstants.Companion.INTERNATIONAL_PAYMENTS
import com.barclays.absa.utils.AnalyticsUtil
import kotlinx.android.synthetic.main.international_payments_disclaimer_fragment.*

class InternationalPaymentsDisclaimerFragment : InternationalPaymentsAbstractBaseFragment(R.layout.international_payments_disclaimer_fragment) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        AnalyticsUtil.trackAction(INTERNATIONAL_PAYMENTS, "InternationalPayments_DisclaimerScreen_ScreenDisplayed")
        setToolBar(getString(R.string.disclaimer))

        internationalPaymentsActivity.hideProgressIndicator()

        continueButton.setOnClickListener {
            AnalyticsUtil.trackAction(INTERNATIONAL_PAYMENTS, "InternationalPayments_DisclaimerScreen_ContinueButtonClicked")
            if (iAgreeCheckBox.isValid) {
                AnalyticsUtil.trackAction(INTERNATIONAL_PAYMENTS, "InternationalPayments_DisclaimerScreen_DisclaimerCheckboxChecked")
                if (internationalPaymentsActivity.isExistingBeneficiary) {
                    navigate(InternationalPaymentsDisclaimerFragmentDirections.actionInternationalPaymentsDisclaimerFragmentToInternationalPaymentsExistingBeneficiaryDetailsFragment())
                } else {
                    navigate(InternationalPaymentsDisclaimerFragmentDirections.actionInternationalPaymentsDisclaimerFragmentToInternationalPaymentBeneficiaryDetailsFragment())
                }
            } else {
                iAgreeCheckBox.setErrorMessage(getString(R.string.acknowledge_that_this_is_a_gift_error))
            }
        }
    }
}