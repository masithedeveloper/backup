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

package com.barclays.absa.banking.depositorPlus.ui

import android.os.Bundle
import android.view.View
import com.barclays.absa.banking.R
import com.barclays.absa.banking.saveAndInvest.SaveAndInvestMarketingConsentFragment
import com.barclays.absa.banking.shared.TermsAndConditionsInfo

class DepositorPlusMarketingConsentFragment : SaveAndInvestMarketingConsentFragment() {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setToolBar(R.string.marketing_consent)

        with(saveAndInvestActivity) {
            showProgressIndicatorAndToolbar()
            setProgressStep(4)
        }
    }

    override fun navigateOnMarketingConsentUpdate() {
        saveAndInvestActivity.hideProgressIndicator()
        val termsAndConditionsInfo = TermsAndConditionsInfo(
                destination = R.id.action_termsAndConditionsFragment_to_depositorPlusConfirmationFragment,
                shouldDisplayCheckBox = true,
                productCode = saveAndInvestActivity.productType.productCode)
        navigate(DepositorPlusMarketingConsentFragmentDirections.actionDepositorPlusMarketingConsentFragmentToTermsAndConditionsFragment(termsAndConditionsInfo))
    }

    override fun navigateToFailure() {
        navigate(DepositorPlusMarketingConsentFragmentDirections.actionDepositorPlusMarketingConsentFragmentToDepositorPlusGenericResultScreenFragment(buildFailureResultScreenProperties()))
    }
}