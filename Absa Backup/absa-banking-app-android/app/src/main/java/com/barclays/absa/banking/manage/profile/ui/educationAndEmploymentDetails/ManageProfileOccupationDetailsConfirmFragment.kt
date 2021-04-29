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
 */

package com.barclays.absa.banking.manage.profile.ui.educationAndEmploymentDetails

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import com.barclays.absa.banking.R
import com.barclays.absa.banking.boundary.shared.dto.TransactionVerificationType
import com.barclays.absa.banking.framework.app.BMBConstants.FAILURE
import com.barclays.absa.banking.framework.app.BMBConstants.SUCCESS
import com.barclays.absa.banking.manage.profile.ui.ManageProfileBaseFragment
import com.barclays.absa.banking.manage.profile.ui.ManageProfileConstants
import com.barclays.absa.banking.manage.profile.ui.ManageProfileResultFactory
import com.barclays.absa.banking.manage.profile.ui.widgets.ConfirmScreenItemWidget
import com.barclays.absa.banking.presentation.sureCheck.SureCheckDelegate
import com.barclays.absa.utils.AnalyticsUtil
import kotlinx.android.synthetic.main.manage_profile_occupation_details_confirm_fragment.*

class ManageProfileOccupationDetailsConfirmFragment : ManageProfileBaseFragment(R.layout.manage_profile_occupation_details_confirm_fragment) {
    private lateinit var sureCheckDelegate: SureCheckDelegate

    override fun onAttach(context: Context) {
        super.onAttach(context)

        sureCheckDelegate = object : SureCheckDelegate(context) {
            override fun onSureCheckProcessed() {
                Handler(Looper.getMainLooper()).postDelayed({ manageProfileViewModel.updateEmploymentInformation() }, 250)
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initData()
        initOnClickListeners()
        initObservers()
    }

    private fun initData() {
        manageProfileViewModel.confirmScreenItemList.forEach { confirmScreenItem ->
            confirmationItemsLinearLayout.addView(ConfirmScreenItemWidget(manageProfileActivity, confirmScreenItem))
        }
    }

    private fun initOnClickListeners() {
        continueButton.setOnClickListener {
            AnalyticsUtil.trackAction(ManageProfileConstants.ANALYTICS_TAG, "ManageProfile_ConfirmEmploymentDetailsScreen_SaveButtonClicked")
            manageProfileViewModel.updateEmploymentInformation()
        }
    }

    private fun initObservers() {
        manageProfileViewModel.updateCustomerInformation.observe(viewLifecycleOwner, { customerInformation ->
            when {
                customerInformation.sureCheckFlag == null && customerInformation.transactionStatus.equals(SUCCESS, ignoreCase = true) -> navigate(ManageProfileOccupationDetailsConfirmFragmentDirections.actionGlobalManageProfileResultFragment(ManageProfileResultFactory(this).updatedOtherPersonalInformationSuccess("", "MangeProfile_ EmploymentDetailsUpdateSuccessScreen_DoneButtonClicked")))
                FAILURE.equals(customerInformation.transactionStatus, ignoreCase = true) -> navigate(ManageProfileOccupationDetailsConfirmFragmentDirections.actionGlobalManageProfileResultFragment(ManageProfileResultFactory(this).updatedOtherPersonalInformationFailure(customerInformation.transactionMessage, "ManageProfile_EmploymentDetailsUpdateErrorScreen_OKButtonClicked")))
                TransactionVerificationType.SURECHECKV2Required.toString().equals(customerInformation.sureCheckFlag, true) -> sureCheckDelegate.processSureCheck(manageProfileActivity, manageProfileViewModel.updateCustomerInformation.value) { manageProfileViewModel.updateEmploymentInformation() }
            }
        })
    }
}