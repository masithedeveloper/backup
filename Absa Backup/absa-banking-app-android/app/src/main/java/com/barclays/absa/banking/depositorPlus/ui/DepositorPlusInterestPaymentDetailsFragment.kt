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

import android.content.Context
import android.os.Bundle
import android.view.View
import com.barclays.absa.banking.R
import com.barclays.absa.banking.boundary.model.AccountObject
import com.barclays.absa.banking.saveAndInvest.SaveAndInvestInterestPaymentDetailsFragment

class DepositorPlusInterestPaymentDetailsFragment : SaveAndInvestInterestPaymentDetailsFragment() {

    override fun onAttach(context: Context) {
        super.onAttach(context)
        investmentAccountName = getString(R.string.depositor_plus_account_title)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setToolBar(R.string.depositor_plus_interest_payout_details)
        saveAndInvestActivity.setProgressStep(3)
    }

    override fun navigateOnValidFields() {
        navigate(DepositorPlusInterestPaymentDetailsFragmentDirections.actionDepositorPlusInterestPaymentDetailsFragmentToDepositorPlusMarketingConsentFragment())
    }

    override fun onInvestmentAccountSelected() {
        super.onInvestmentAccountSelected()
        trackAnalyticsAction("InterestPayoutDetailsScreen_DepositorPlusAccountSelected")
    }

    override fun onAnotherBankAccountSelected() {
        super.onAnotherBankAccountSelected()
        trackAnalyticsAction("InterestPayoutDetailsScreen_AccountAtAnotherBankSelected")
    }

    override fun onAbsaAccountSelected(selectedAccount: AccountObject) {
        super.onAbsaAccountSelected(selectedAccount)
        trackAnalyticsAction("InterestPayoutDetailsScreen_AbsaAccountSelected")
    }
}