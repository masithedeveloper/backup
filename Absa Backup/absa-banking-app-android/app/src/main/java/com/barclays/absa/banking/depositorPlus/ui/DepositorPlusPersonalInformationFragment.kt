/*
 * Copyright (c) 2021 Absa Bank Limited, All Rights Reserved.
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
import com.barclays.absa.banking.saveAndInvest.SaveAndInvestPersonalInformationFragment

class DepositorPlusPersonalInformationFragment : SaveAndInvestPersonalInformationFragment() {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        saveAndInvestActivity.showProgressIndicatorAndToolbar()
        saveAndInvestActivity.setProgressStep(1)
    }

    override fun navigateOnSuccess() {
        navigate(DepositorPlusPersonalInformationFragmentDirections.actionDepositorPlusPersonalInformationFragmentToDepositorPlusFundYourAccountFragment())
    }

    override fun navigateOnRiskBasedFailure() {
        saveAndInvestActivity.hideProgressIndicatorAndToolbar()
        navigate(DepositorPlusPersonalInformationFragmentDirections.actionDepositorPlusPersonalInformationFragmentToGenericResultScreenFragment(getRiskedBasedFailureScreenProperties()))
    }

    override fun navigateOnFailure() {
        saveAndInvestActivity.hideProgressIndicatorAndToolbar()
        navigate(DepositorPlusPersonalInformationFragmentDirections.actionDepositorPlusPersonalInformationFragmentToGenericResultScreenFragment(getFailureScreenProperties()))
    }

    override fun navigateToSourceOfFunds() {
        saveAndInvestActivity.hideProgressIndicator()
        navigate(DepositorPlusPersonalInformationFragmentDirections.actionDepositorPlusPersonalInformationFragmentToSaveAndInvestSourceOfFundsFragment())
    }
}