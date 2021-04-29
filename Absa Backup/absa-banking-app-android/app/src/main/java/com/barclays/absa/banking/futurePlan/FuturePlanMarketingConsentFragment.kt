/*
 * Copyright (c) 2021 Absa Bank Limited, All Rights Reserved.
 *
 * This code is confidential to Absa Bank Limited and shall not be disclosed
 * outside the Bank without the prior written permission of the Absa Legal
 *
 * In the event that such disclosure is permitted the code shall not be copied
 * or distributed other than on a need-to-know basis and any recipients may be
 * required to sign a confidentiality undertaking in favor of Absa Bank Limited
 *
 */

package com.barclays.absa.banking.futurePlan

import android.os.Bundle
import android.view.View
import com.barclays.absa.banking.R
import com.barclays.absa.banking.futurePlan.FuturePlanActivity.Companion.FUTURE_PLAN
import com.barclays.absa.banking.saveAndInvest.SaveAndInvestMarketingConsentFragment
import com.barclays.absa.banking.shared.TermsAndConditionsInfo

class FuturePlanMarketingConsentFragment : SaveAndInvestMarketingConsentFragment() {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setToolBar(R.string.marketing_consent)
        saveAndInvestActivity.showProgressIndicatorAndToolbar()
        saveAndInvestActivity.setProgressStep(3)
    }

    override fun navigateOnMarketingConsentUpdate() {
        saveAndInvestActivity.hideProgressIndicator()
        val termsAndConditionsInfo = TermsAndConditionsInfo(
                destination = R.id.action_futurePlanTermsAndConditionsFragment_to_futurePlanConfirmationFragment,
                shouldDisplayCheckBox = true,
                productCode = saveAndInvestActivity.productType.productCode,
                productName = FUTURE_PLAN)
        navigate(FuturePlanMarketingConsentFragmentDirections.actionFuturePlanMarketingConsentFragmentToFuturePlanTermsAndConditionsFragment(termsAndConditionsInfo))
    }

    override fun navigateToFailure() {
        navigate(FuturePlanMarketingConsentFragmentDirections.actionFuturePlanMarketingConsentFragmentToGenericResultScreenFragment(buildFailureResultScreenProperties()))
    }
}