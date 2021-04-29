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

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import com.barclays.absa.banking.R
import com.barclays.absa.banking.framework.app.BMBApplication
import com.barclays.absa.banking.manage.profile.services.dto.NextOfKinDetails
import com.barclays.absa.banking.manage.profile.ui.ManageProfileBaseFragment
import com.barclays.absa.banking.manage.profile.ui.ManageProfileConstants.ANALYTICS_TAG
import com.barclays.absa.banking.shared.services.dto.LookupItem
import com.barclays.absa.banking.shared.services.dto.LookupResult
import com.barclays.absa.utils.AnalyticsUtil
import com.barclays.absa.utils.ValidationUtils
import kotlinx.android.synthetic.main.manage_profile_edit_next_of_kin_fragment.*
import styleguide.forms.NormalInputView
import styleguide.forms.SelectorList
import styleguide.utils.extensions.toFormattedCellphoneNumber
import styleguide.utils.extensions.toTitleCase

class ManageProfileEditNextOfKinFragment : ManageProfileBaseFragment(R.layout.manage_profile_edit_next_of_kin_fragment) {
    private var relationshipList: List<LookupItem> = listOf()
    private lateinit var nextOfKinDetails: NextOfKinDetails

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setToolBar(R.string.manage_profile_hub_next_of_kin_title)
        initData()
        setUpTextWatchers()
        setUpItemSelectionInterface()
        setUpOnClickListeners()
        continueButton.isEnabled = hasProfileDetailsChanged()
    }

    private fun initData() {
        manageProfileViewModel.customerInformation.value?.customerInformation?.nextOfKinDetails?.let {
            nextOfKinDetails = it
        }

        relationshipList = manageProfileViewModel.retrieveRelationshipList()
        relationshipNormalInputView.setList(relationshipList as SelectorList<LookupItem>, getString(R.string.manage_profile_next_of_kin_select_type_of_relationship))
        firstNameNormalInputView.selectedValue = manageProfileViewModel.nextOfKinDetailsToUpdate.firstName.toTitleCase()
        surnameNormalInputView.selectedValue = manageProfileViewModel.nextOfKinDetailsToUpdate.surname.toTitleCase()
        manageProfileViewModel.relationshipLookUpResult.value?.let {
            relationshipNormalInputView.selectedValue = manageProfileViewModel.getLookupValue(it, manageProfileViewModel.nextOfKinDetailsToUpdate.relationship)
        }
        cellphoneNormalInputView.selectedValue = manageProfileViewModel.nextOfKinDetailsToUpdate.cellphoneNumber.toFormattedCellphoneNumber()
        emailAddressNormalInputView.selectedValue = manageProfileViewModel.nextOfKinDetailsToUpdate.email.toLowerCase(BMBApplication.getApplicationLocale())
        homePhoneNormalInputView.selectedValue = (manageProfileViewModel.nextOfKinDetailsToUpdate.homeTelephoneCode + manageProfileViewModel.nextOfKinDetailsToUpdate.homeTelephoneNumber).toFormattedCellphoneNumber()
        workPhoneNormalInputView.selectedValue = (manageProfileViewModel.nextOfKinDetailsToUpdate.workTelephoneCode + manageProfileViewModel.nextOfKinDetailsToUpdate.workTelephoneNumber).toFormattedCellphoneNumber()
    }

    private fun validateNextOfKinFields(): Boolean {
        when {
            firstNameNormalInputView.selectedValue.isEmpty() -> firstNameNormalInputView.setError(getString(R.string.manage_profile_next_of_kin_first_name_error))
            surnameNormalInputView.selectedValue.isEmpty() || surnameNormalInputView.selectedValue.length < 2 -> surnameNormalInputView.setError(getString(R.string.manage_profile_next_of_kin_surname_error))
            relationshipNormalInputView.selectedValue.isEmpty() -> relationshipNormalInputView.setError(getString(R.string.manage_profile_next_of_kin_relationship_error))
            cellphoneNormalInputView.selectedValue.isEmpty() && homePhoneNormalInputView.selectedValue.isEmpty() && workPhoneNormalInputView.selectedValue.isEmpty() -> cellphoneNormalInputView.setError(getString(R.string.manage_profile_next_of_kin_cellphone_number_error))
            emailAddressNormalInputView.selectedValue.isNotEmpty() && !ValidationUtils.isValidEmailAddress(emailAddressNormalInputView.selectedValue) -> emailAddressNormalInputView.setError(getString(R.string.manage_profile_next_of_kin_valid_email_address_error))
            cellphoneNormalInputView.selectedValue.isEmpty() && homePhoneNormalInputView.selectedValue.isEmpty() && workPhoneNormalInputView.selectedValue.isEmpty() -> homePhoneNormalInputView.setError(getString(R.string.manage_profile_next_of_kin_homephone_number_error))
            cellphoneNormalInputView.selectedValue.isEmpty() && homePhoneNormalInputView.selectedValue.isEmpty() && workPhoneNormalInputView.selectedValue.isEmpty() -> workPhoneNormalInputView.setError(getString(R.string.manage_profile_next_of_kin_workphone_number_error))
            cellphoneNormalInputView.selectedValue.isNotEmpty() && !ValidationUtils.validatePhoneNumberInput(cellphoneNormalInputView.selectedValue) -> cellphoneNormalInputView.setError(getString(R.string.manage_profile_next_of_kin_valid_cellphone_number_error))
            homePhoneNormalInputView.selectedValue.isNotEmpty() && !ValidationUtils.validatePhoneNumberInput(homePhoneNormalInputView.selectedValue) -> homePhoneNormalInputView.setError(getString(R.string.manage_profile_next_of_kin_valid_homephone_number_error))
            workPhoneNormalInputView.selectedValue.isNotEmpty() && !ValidationUtils.validatePhoneNumberInput(workPhoneNormalInputView.selectedValue) -> workPhoneNormalInputView.setError(getString(R.string.manage_profile_next_of_kin_valid_workphone_number_error))
            else -> return true
        }
        return false
    }

    private fun isMandatoryFieldsPopulated(): Boolean {
        return firstNameNormalInputView.selectedValue.isNotEmpty() && surnameNormalInputView.selectedValue.isNotEmpty() && relationshipNormalInputView.selectedValue.isNotEmpty() && (cellphoneNormalInputView.selectedValue.isNotEmpty() || homePhoneNormalInputView.selectedValue.isNotEmpty() || workPhoneNormalInputView.selectedValue.isNotEmpty())
    }

    private fun setUpTextWatchers() {
        firstNameNormalInputView.addValueViewTextWatcher(genericTextWatcher(firstNameNormalInputView))
        surnameNormalInputView.addValueViewTextWatcher(genericTextWatcher(surnameNormalInputView))
        relationshipNormalInputView.addValueViewTextWatcher(genericTextWatcher(relationshipNormalInputView))
        cellphoneNormalInputView.addValueViewTextWatcher(genericTextWatcher(cellphoneNormalInputView))
        emailAddressNormalInputView.addValueViewTextWatcher(genericTextWatcher(emailAddressNormalInputView))
        homePhoneNormalInputView.addValueViewTextWatcher(genericTextWatcher(homePhoneNormalInputView))
        workPhoneNormalInputView.addValueViewTextWatcher(genericTextWatcher(workPhoneNormalInputView))
    }

    private fun nextOfKinDetailsToUpdate() {
        manageProfileViewModel.nextOfKinDetailsToUpdate.apply {
            firstName = if (firstNameNormalInputView.selectedValue.isNotEmpty()) firstNameNormalInputView.selectedValue else " "
            surname = if (surnameNormalInputView.selectedValue.isNotEmpty()) surnameNormalInputView.selectedValue else " "
            cellphoneNumber = if (cellphoneNormalInputView.selectedValue.isNotEmpty()) cellphoneNormalInputView.selectedValue else " "
            email = if (emailAddressNormalInputView.selectedValue.isNotEmpty()) emailAddressNormalInputView.selectedValue else " "
            if (homePhoneNormalInputView.selectedValue.isNotEmpty()) {
                homeTelephoneCode = homePhoneNormalInputView.selectedValue.substring(0, 3)
                homeTelephoneNumber = homePhoneNormalInputView.selectedValue.substring(3)
            } else {
                homeTelephoneCode = " "
                homeTelephoneNumber = " "
            }
            if (workPhoneNormalInputView.selectedValue.isNotEmpty()) {
                workTelephoneCode = workPhoneNormalInputView.selectedValue.substring(0, 3)
                workTelephoneNumber = workPhoneNormalInputView.selectedValue.substring(3)
            } else {
                workTelephoneCode = " "
                workTelephoneNumber = " "
            }
        }
    }

    private fun setUpItemSelectionInterface() {
        relationshipNormalInputView.setItemSelectionInterface {
            manageProfileViewModel.nextOfKinDetailsToUpdate.relationship = relationshipList[it].itemCode!!
        }
    }

    private fun setUpOnClickListeners() {
        continueButton.setOnClickListener {
            AnalyticsUtil.trackAction(ANALYTICS_TAG, "ManageProfile_EditNextOfKinScreen_ContinueButtonClicked")
            if (isMandatoryFieldsPopulated() && validateNextOfKinFields()) {
                nextOfKinDetailsToUpdate()
                navigate(ManageProfileEditNextOfKinFragmentDirections.actionManageProfileEditNextOfKinFragmentToManageProfileNextOfKinConfirmationFragment())
            } else {
                validateNextOfKinFields()
            }
        }
    }

    private fun genericTextWatcher(normalInputView: NormalInputView<*>): TextWatcher {
        return object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (normalInputView == cellphoneNormalInputView || normalInputView == homePhoneNormalInputView || normalInputView == workPhoneNormalInputView) {
                    setLabelsAsOptional()
                }
                continueButton.isEnabled = hasProfileDetailsChanged()
                normalInputView.clearError()
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        }
    }

    private fun hasProfileDetailsChanged(): Boolean {
        var relationshipLookUpResult = LookupResult()
        manageProfileViewModel.relationshipLookUpResult.value?.let {
            relationshipLookUpResult = it
        }
        return nextOfKinDetails.firstName != firstNameNormalInputView.selectedValue ||
                nextOfKinDetails.surname != surnameNormalInputView.selectedValue ||
                manageProfileViewModel.getLookupValue(relationshipLookUpResult, nextOfKinDetails.relationship) != relationshipNormalInputView.selectedValue ||
                nextOfKinDetails.cellphoneNumber.toFormattedCellphoneNumber() != cellphoneNormalInputView.selectedValue.toFormattedCellphoneNumber() ||
                nextOfKinDetails.email != emailAddressNormalInputView.selectedValue ||
                (nextOfKinDetails.homeTelephoneCode + nextOfKinDetails.homeTelephoneNumber).toFormattedCellphoneNumber() != homePhoneNormalInputView.selectedValue.toFormattedCellphoneNumber() ||
                (nextOfKinDetails.workTelephoneCode + nextOfKinDetails.workTelephoneNumber).toFormattedCellphoneNumber() != workPhoneNormalInputView.selectedValue.toFormattedCellphoneNumber()

    }

    private fun setLabelsAsOptional() {
        when {
            cellphoneNormalInputView.selectedValue.isNotEmpty() -> {
                cellphoneNormalInputView.setTitleText(getString(R.string.manage_profile_overview_cellphone))
                homePhoneNormalInputView.setTitleText(getString(R.string.manage_profile_homephone_optional))
                workPhoneNormalInputView.setTitleText(getString(R.string.manage_profile_workphone_optional))
            }
            homePhoneNormalInputView.selectedValue.isNotEmpty() -> {
                cellphoneNormalInputView.setTitleText(getString(R.string.manage_profile_cellphone_optional))
                homePhoneNormalInputView.setTitleText(getString(R.string.manage_profile_overview_home_phone))
                workPhoneNormalInputView.setTitleText(getString(R.string.manage_profile_workphone_optional))
            }
            workPhoneNormalInputView.selectedValue.isNotEmpty() -> {
                cellphoneNormalInputView.setTitleText(getString(R.string.manage_profile_cellphone_optional))
                homePhoneNormalInputView.setTitleText(getString(R.string.manage_profile_homephone_optional))
                workPhoneNormalInputView.setTitleText(getString(R.string.manage_profile_overview_work_phone))
            }
        }
    }
}