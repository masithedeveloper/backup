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

package com.barclays.absa.banking.saveAndInvest

import android.content.Context
import androidx.annotation.LayoutRes
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import com.barclays.absa.banking.R
import com.barclays.absa.banking.express.invest.saveEmploymentDetails.EmploymentDetailsViewModel
import com.barclays.absa.banking.express.invest.saveEmploymentDetails.dto.EmploymentDetailsRequest
import com.barclays.absa.banking.express.invest.saveExistingCustomerDetails.CustomerDetailsSaveViewModel
import com.barclays.absa.banking.express.invest.saveExistingCustomerDetails.dto.CustomerDetailsSaveRequest
import com.barclays.absa.banking.framework.BaseFragment
import com.barclays.absa.banking.newToBank.services.dto.ResultAnimations
import com.barclays.absa.utils.viewModel
import styleguide.screens.GenericResultScreenFragment
import styleguide.screens.GenericResultScreenProperties

abstract class SaveAndInvestBaseFragment(@LayoutRes layout: Int) : BaseFragment(layout) {
    protected lateinit var saveAndInvestActivity: SaveAndInvestActivity
    protected val saveAndInvestViewModel by activityViewModels<SaveAndInvestViewModel>()
    protected val customerDetailsSaveViewModel by viewModels<CustomerDetailsSaveViewModel>()
    protected val employmentDetailsViewModel by viewModels<EmploymentDetailsViewModel>()

    override fun onAttach(context: Context) {
        super.onAttach(context)
        saveAndInvestActivity = context as SaveAndInvestActivity
    }

    protected fun saveApplicationDetails() {
        customerDetailsSaveViewModel.save(CustomerDetailsSaveRequest().apply {
            personalDetails = saveAndInvestViewModel.personalDetails
            contactDetails = saveAndInvestViewModel.contactDetails
            addressDetails = saveAndInvestViewModel.addressDetails
            accountCreationDetails = saveAndInvestViewModel.accountCreationDetails
        })
    }

    protected fun saveEmploymentDetailsInfo() {
        employmentDetailsViewModel.saveEmploymentDetails(EmploymentDetailsRequest().apply {
            employmentDetails = saveAndInvestViewModel.employmentDetails
            dataSharingAndMarketingConsent = saveAndInvestViewModel.dataSharingAndMarketingConsent
            rbaServiceStatus = saveAndInvestViewModel.riskRatingResponse.riskRatingServiceStatus
            riskRating = saveAndInvestViewModel.riskRatingResponse.riskRating
        })
    }

    protected fun trackAnalyticsAction(action: String) {
        saveAndInvestActivity.trackAnalyticsAction(action)
    }

    protected fun getFailureScreenProperties() : GenericResultScreenProperties {
        GenericResultScreenFragment.setPrimaryButtonOnClick {
            trackAnalyticsAction("RBAFailureScreen_DoneButtonClicked")
            navigateToHomeScreenSelectingHomeIcon()
        }
        return GenericResultScreenProperties.PropertiesBuilder()
                .setResultScreenAnimation(ResultAnimations.generalError)
                .setTitle(getString(R.string.depositor_plus_something_went_wrong))
                .setDescription(getString(R.string.depositor_plus_technical_error_processing))
                .setContactViewContactName(getString(R.string.depositor_plus_call_centre))
                .setContactViewContactNumber(getString(R.string.depositor_plus_open_account_call_centre_number))
                .setPrimaryButtonLabel(getString(R.string.done))
                .build(false)
    }
}