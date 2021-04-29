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

package com.barclays.absa.banking.manage.profile.ui.financialDetails

import android.os.Bundle
import android.view.View
import com.barclays.absa.banking.R
import com.barclays.absa.banking.manage.profile.services.dto.FinancialInformation
import com.barclays.absa.banking.manage.profile.ui.ManageProfileBaseFragment
import com.barclays.absa.banking.manage.profile.ui.ManageProfileConstants.ANALYTICS_TAG
import com.barclays.absa.banking.manage.profile.ui.ManageProfileConstants.NO
import com.barclays.absa.banking.manage.profile.ui.ManageProfileConstants.YES
import com.barclays.absa.utils.AnalyticsUtil
import kotlinx.android.synthetic.main.manage_profile_other_financial_details_fragment.*

class ManageProfileEditOtherFinancialDetailsFragment : ManageProfileBaseFragment(R.layout.manage_profile_other_financial_details_fragment) {
    private var isSocialGrantStateChanged: Boolean = false
    private var isCreditWorthinessStateChanged: Boolean = false
    private lateinit var financialInformation: FinancialInformation

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setToolBar(getString(R.string.manage_profile_financial_details_hub_other_financial_details_action))
        initData()
        setUpOnCheckChangedListeners()
        setUpOnClickListener()
    }

    private fun initData() {
        financialInformation = manageProfileViewModel.customerInformation.value?.customerInformation?.financialInformation ?: FinancialInformation()
        continueButton.isEnabled = enableContinueButton()
        manageProfileViewModel.manageProfileFinancialDetailsMetaData.socialGrantFlag.let {
            if (it.isNotEmpty()) {
                socialGrantCheckBoxView.isChecked = it.equals(YES, true)
            }
        }

        manageProfileViewModel.manageProfileFinancialDetailsMetaData.creditWorthinessFlag.let {
            if (it.isNotEmpty()) {
                creditWorthinessCheckBoxView.isChecked = it.equals(YES, true)
            }
        }
    }

    private fun setUpOnCheckChangedListeners() {
        socialGrantCheckBoxView.setOnCheckedListener { isChecked ->
            manageProfileViewModel.manageProfileFinancialDetailsMetaData.socialGrantFlag = if (isChecked) YES else NO

            isSocialGrantStateChanged = financialInformation.socialGrantFlag != manageProfileViewModel.manageProfileFinancialDetailsMetaData.socialGrantFlag
            continueButton.isEnabled = isSocialGrantStateChanged || isCreditWorthinessStateChanged
            if (isChecked) {
                AnalyticsUtil.trackAction(ANALYTICS_TAG, "ManageProfile_OtherFinancialDetailsScreen_RecieveSocialGrantCheckboxChecked")
            }
        }

        creditWorthinessCheckBoxView.setOnCheckedListener { isChecked ->
            manageProfileViewModel.manageProfileFinancialDetailsMetaData.creditWorthinessFlag = if (isChecked) YES else NO
            isCreditWorthinessStateChanged = financialInformation.creditWorthinessFlag != manageProfileViewModel.manageProfileFinancialDetailsMetaData.creditWorthinessFlag
            continueButton.isEnabled = isSocialGrantStateChanged || isCreditWorthinessStateChanged
            if (isChecked) {
                AnalyticsUtil.trackAction(ANALYTICS_TAG, "ManageProfile_OtherFinancialDetailsScreen_ProvideInformationToCreditorsCheckBoxChecked")
            }
        }
    }

    private fun setUpOnClickListener() {
        continueButton.setOnClickListener {
            AnalyticsUtil.trackAction(ANALYTICS_TAG, "ManageProfile_OtherFinancialDetailsScreen_ContinueButtonClicked")
            manageProfileViewModel.manageProfileFinancialDetailsMetaData.socialGrantFlag = if (socialGrantCheckBoxView.isChecked) YES else NO
            manageProfileViewModel.manageProfileFinancialDetailsMetaData.creditWorthinessFlag = if (creditWorthinessCheckBoxView.isChecked) YES else NO
            navigate(ManageProfileEditOtherFinancialDetailsFragmentDirections.actionManageProfileOtherFinancialDetailsFragmentToManageProfileOtherFinancialDetailsConfirmationFragment())
        }
    }

    private fun enableContinueButton(): Boolean {
        isSocialGrantStateChanged = financialInformation.socialGrantFlag != manageProfileViewModel.manageProfileFinancialDetailsMetaData.socialGrantFlag
        isCreditWorthinessStateChanged = financialInformation.creditWorthinessFlag != manageProfileViewModel.manageProfileFinancialDetailsMetaData.creditWorthinessFlag
        return isSocialGrantStateChanged || isCreditWorthinessStateChanged
    }
}