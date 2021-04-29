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

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import com.barclays.absa.banking.R
import com.barclays.absa.banking.boundary.shared.dto.TransactionVerificationType
import com.barclays.absa.banking.framework.app.BMBConstants.FAILURE
import com.barclays.absa.banking.framework.app.BMBConstants.SUCCESS
import com.barclays.absa.banking.manage.profile.ManageProfileFinancialDetailsFlow
import com.barclays.absa.banking.manage.profile.services.dto.FinancialInformation
import com.barclays.absa.banking.manage.profile.ui.ManageProfileBaseFragment
import com.barclays.absa.banking.manage.profile.ui.ManageProfileConstants.ANALYTICS_TAG
import com.barclays.absa.banking.manage.profile.ui.ManageProfileConstants.YES
import com.barclays.absa.banking.manage.profile.ui.ManageProfileResultFactory
import com.barclays.absa.banking.presentation.sureCheck.SureCheckDelegate
import com.barclays.absa.utils.AnalyticsUtil
import kotlinx.android.synthetic.main.manage_profile_other_financial_details_confirmation_fragment.*

class ManageProfileOtherFinancialDetailsConfirmationFragment : ManageProfileBaseFragment(R.layout.manage_profile_other_financial_details_confirmation_fragment) {
    private lateinit var sureCheckDelegate: SureCheckDelegate
    private lateinit var financialInformation: FinancialInformation

    override fun onAttach(context: Context) {
        super.onAttach(context)

        sureCheckDelegate = object : SureCheckDelegate(context) {
            override fun onSureCheckProcessed() {
                Handler(Looper.getMainLooper()).postDelayed({ manageProfileViewModel.updateFinancialDetails() }, 250)
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setToolBar(getString(R.string.manage_profile_confirm_detail_changes_toolbar_title))

        initData()
        initObservers()
        setUpOnClickListener()
    }

    private fun initData() {
        financialInformation = manageProfileViewModel.customerInformation.value?.customerInformation?.financialInformation ?: FinancialInformation()
        if (YES.equals(manageProfileViewModel.manageProfileFinancialDetailsMetaData.socialGrantFlag, true)) {
            socialGrantContentAndLabelView.setContentText(getString(R.string.yes))
        } else {
            socialGrantContentAndLabelView.setContentText(getString(R.string.no))
        }

        if (YES.equals(manageProfileViewModel.manageProfileFinancialDetailsMetaData.creditWorthinessFlag, true)) {
            creditWorthinessContentAndLabelView.setContentText(getString(R.string.yes))
        } else {
            creditWorthinessContentAndLabelView.setContentText(getString(R.string.no))
        }
    }

    private fun setUpOnClickListener() {
        saveButton.setOnClickListener {
            AnalyticsUtil.trackAction(ANALYTICS_TAG, "ManageProfile_ConfirmOtherFinancialDetailsChangesScreen_SaveButtonClicked")
            manageProfileViewModel.manageProfileFinancialDetailsFlow = ManageProfileFinancialDetailsFlow.UPDATE_OTHER_FINANCIAL_DETAILS
            manageProfileViewModel.updateFinancialDetails()
        }
    }

    private fun initObservers() {
        manageProfileViewModel.updateCustomerInformation.observe(viewLifecycleOwner, { customerInformation ->
            when {
                customerInformation.sureCheckFlag == null && SUCCESS.equals(customerInformation.transactionStatus, true) -> navigate(ManageProfileOtherFinancialDetailsConfirmationFragmentDirections.actionGlobalManageProfileResultFragment(ManageProfileResultFactory(this).updatedOtherPersonalInformationSuccess("", "MangeProfile_OtherFinancialDetailsUpdateSuccessScreen_DoneButtonClicked")))
                FAILURE.equals(customerInformation.transactionStatus, true) -> navigate(ManageProfileOtherFinancialDetailsConfirmationFragmentDirections.actionGlobalManageProfileResultFragment(ManageProfileResultFactory(this).updatedOtherPersonalInformationFailure(customerInformation.transactionMessage, "ManageProfile_OtherFinancialDetailsUpdateFailureScreen_OKButtonClicked")))
                TransactionVerificationType.SURECHECKV2Required.toString().equals(customerInformation.sureCheckFlag, true) -> sureCheckDelegate.processSureCheck(baseActivity, manageProfileViewModel.updateCustomerInformation.value) { manageProfileViewModel.updateFinancialDetails() }
            }
        })
    }
}