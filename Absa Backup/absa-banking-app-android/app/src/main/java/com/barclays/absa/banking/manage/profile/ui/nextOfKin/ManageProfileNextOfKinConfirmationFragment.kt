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

package com.barclays.absa.banking.manage.profile.ui.nextOfKin

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import com.barclays.absa.banking.R
import com.barclays.absa.banking.boundary.shared.dto.TransactionVerificationType
import com.barclays.absa.banking.framework.app.BMBConstants.FAILURE
import com.barclays.absa.banking.framework.app.BMBConstants.SUCCESS
import com.barclays.absa.banking.manage.profile.services.dto.NextOfKinDetails
import com.barclays.absa.banking.manage.profile.ui.ManageProfileBaseFragment
import com.barclays.absa.banking.manage.profile.ui.ManageProfileConstants.ANALYTICS_TAG
import com.barclays.absa.banking.manage.profile.ui.ManageProfileResultFactory
import com.barclays.absa.banking.presentation.sureCheck.SureCheckDelegate
import com.barclays.absa.utils.AnalyticsUtil
import kotlinx.android.synthetic.main.manage_profile_next_of_kin_confirmation_fragment.*
import styleguide.content.PrimaryContentAndLabelView
import styleguide.utils.extensions.removeSpaces
import styleguide.utils.extensions.toFormattedCellphoneNumber

class ManageProfileNextOfKinConfirmationFragment : ManageProfileBaseFragment(R.layout.manage_profile_next_of_kin_confirmation_fragment) {
    private var nextOfKinDetails = NextOfKinDetails()
    private lateinit var sureCheckDelegate: SureCheckDelegate

    override fun onAttach(context: Context) {
        super.onAttach(context)

        sureCheckDelegate = object : SureCheckDelegate(context) {
            override fun onSureCheckProcessed() {
                Handler(Looper.getMainLooper()).postDelayed({ manageProfileViewModel.updateNextOfKinDetails(nextOfKinDetails) }, 250)
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setToolBar(getString(R.string.manage_profile_confirm_detail_changes_toolbar_title))
        initData()
        setUpOnClickListener()
        initObservers()
    }

    private fun initData() {
        val nextOfKinDetailsToUpdate = manageProfileViewModel.nextOfKinDetailsToUpdate
        setContentAndLabelViewVisibility(firstNamePrimaryContentAndLabelView, nextOfKinDetailsToUpdate.firstName)
        setContentAndLabelViewVisibility(surnamePrimaryContentAndLabelView, nextOfKinDetailsToUpdate.surname)
        setContentAndLabelViewVisibility(relationshipPrimaryContentAndLabelView, manageProfileViewModel.getLookupValue(manageProfileViewModel.relationshipLookUpResult.value!!, nextOfKinDetailsToUpdate.relationship))
        setContentAndLabelViewVisibility(cellphonePrimaryContentAndLabelView, nextOfKinDetailsToUpdate.cellphoneNumber)
        setContentAndLabelViewVisibility(emailPrimaryContentAndLabelView, nextOfKinDetailsToUpdate.email)
        setContentAndLabelViewVisibility(homePhonePrimaryContentAndLabelView, (nextOfKinDetailsToUpdate.homeTelephoneCode + nextOfKinDetailsToUpdate.homeTelephoneNumber).toFormattedCellphoneNumber())
        setContentAndLabelViewVisibility(workPhonePrimaryContentAndLabelView, (nextOfKinDetailsToUpdate.workTelephoneCode + nextOfKinDetailsToUpdate.workTelephoneNumber).toFormattedCellphoneNumber())
    }

    private fun setUpOnClickListener() {
        saveButton.setOnClickListener {
            AnalyticsUtil.trackAction(ANALYTICS_TAG, "ManageProfile_ConfirmOtherFinancialDetailsChangesScreen_SaveButtonClicked")
            nextOfKinDetails.apply {
                clientType = manageProfileViewModel.customerInformation.value?.customerInformation?.personalInformation?.clientType.toString()
                firstName = manageProfileViewModel.nextOfKinDetailsToUpdate.firstName
                surname = manageProfileViewModel.nextOfKinDetailsToUpdate.surname
                relationship = manageProfileViewModel.nextOfKinDetailsToUpdate.relationship
                cellphoneNumber = manageProfileViewModel.nextOfKinDetailsToUpdate.cellphoneNumber.removeSpaces()
                email = manageProfileViewModel.nextOfKinDetailsToUpdate.email
                homeTelephoneCode = manageProfileViewModel.nextOfKinDetailsToUpdate.homeTelephoneCode
                homeTelephoneNumber = if (manageProfileViewModel.nextOfKinDetailsToUpdate.homeTelephoneNumber.length > 1) manageProfileViewModel.nextOfKinDetailsToUpdate.homeTelephoneNumber.removeSpaces() else " "
                workTelephoneCode = manageProfileViewModel.nextOfKinDetailsToUpdate.workTelephoneCode
                workTelephoneNumber = if (manageProfileViewModel.nextOfKinDetailsToUpdate.workTelephoneNumber.length > 1) manageProfileViewModel.nextOfKinDetailsToUpdate.workTelephoneNumber.removeSpaces() else " "
            }
            manageProfileViewModel.updateNextOfKinDetails(nextOfKinDetails)
        }
    }

    private fun initObservers() {
        manageProfileViewModel.updateCustomerInformation.observe(viewLifecycleOwner, { customerInformation ->
            when {
                customerInformation.sureCheckFlag == null && SUCCESS.equals(customerInformation.transactionStatus, true) -> navigate(ManageProfileNextOfKinConfirmationFragmentDirections.actionGlobalManageProfileResultFragment(ManageProfileResultFactory(this).updatedOtherPersonalInformationSuccess("", "MangeProfile_NextOfKinDetailsUpdateSuccessScreen_DoneButtonClicked")))
                FAILURE.equals(customerInformation.transactionStatus, true) -> navigate(ManageProfileNextOfKinConfirmationFragmentDirections.actionGlobalManageProfileResultFragment(ManageProfileResultFactory(this).updatedOtherPersonalInformationFailure(customerInformation.transactionMessage, "ManageProfile_NextOfKinDetailsUpdateFailureScreen_OKButtonClicked")))
                TransactionVerificationType.SURECHECKV2Required.toString().equals(customerInformation.sureCheckFlag, true) -> sureCheckDelegate.processSureCheck(baseActivity, manageProfileViewModel.updateCustomerInformation.value) { manageProfileViewModel.updateNextOfKinDetails(nextOfKinDetails) }
            }
        })
    }

    private fun setContentAndLabelViewVisibility(contentAndLabelView: PrimaryContentAndLabelView, contentAndLabelViewText: String) {
        if (contentAndLabelViewText.isEmpty() || contentAndLabelViewText == " ") {
            contentAndLabelView.visibility = View.GONE
        } else {
            contentAndLabelView.setContentText(contentAndLabelViewText)
        }
    }
}