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
import androidx.core.view.isVisible
import com.barclays.absa.banking.R
import com.barclays.absa.banking.manage.profile.ui.ManageProfileBaseFragment
import com.barclays.absa.banking.manage.profile.ui.ManageProfileConstants.ANALYTICS_TAG
import com.barclays.absa.banking.manage.profile.ui.OccupationStatus
import com.barclays.absa.banking.manage.profile.ui.addressDetails.ManageProfileAddressDetailsViewModel
import com.barclays.absa.banking.manage.profile.ui.models.EducationDetailsDisplayInformation
import com.barclays.absa.banking.manage.profile.ui.widgets.*
import com.barclays.absa.banking.shared.services.dto.LookupItem
import com.barclays.absa.utils.AnalyticsUtil
import com.barclays.absa.utils.viewModel
import kotlinx.android.synthetic.main.manage_profile_address_entry_layout.*
import kotlinx.android.synthetic.main.manage_profile_edit_employment_details_fragment.*
import styleguide.forms.NormalInputView
import styleguide.forms.SelectorList
import styleguide.utils.extensions.removeSpaces
import styleguide.utils.extensions.toFormattedCellphoneNumber

class ManageProfileEditEmploymentDetailsFragment : ManageProfileBaseFragment(R.layout.manage_profile_edit_employment_details_fragment), ManageProfileAddressInterface {
    private lateinit var manageProfileAddressWidget: ManageProfileAddressWidget
    private lateinit var manageProfileAddressDetailsViewModel: ManageProfileAddressDetailsViewModel

    private var originalAddressDetails = AddressDetails()
    private var educationDetailsDisplayInformation = EducationDetailsDisplayInformation()

    private var occupationListLookupTable = SelectorList<LookupItem>()
    private var occupationSectorListLookupTable = SelectorList<LookupItem>()
    private var occupationLevelListLookupTable = SelectorList<LookupItem>()
    private var occupationStatusListLookupTable = SelectorList<LookupItem>()

    private var selectedOccupation = -1
    private var selectedOccupationLevel = -1
    private var selectedOccupationStatus = -1
    private var selectedOccupationSector = -1

    private var hasOccupationSectorValueBeenChangedToNoneAutomatically: Boolean = false
    private var hasOccupationSectorValueBeenChangedToNoneManually: Boolean = false
    private var hasOccupationLevelValueBeenChangedToNoneAutomatically: Boolean = false
    private var hasOccupationLevelValueBeenChangedToNoneManually: Boolean = false

    private var isSelEmployed: Boolean = false
    private var hasOptionalFieldChanged: Boolean = false
    private var hasMandatoryFieldChanged: Boolean = false
    private var isEmploymentStatusChanged: Boolean = false
    private var hasFieldOnParentFragmentChanged = false

    override fun onAttach(context: Context) {
        super.onAttach(context)

        manageProfileAddressDetailsViewModel = manageProfileActivity.viewModel()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initData()
        initSelectorInterfaces()
        initOnClickListeners()
    }

    @Suppress("UNCHECKED_CAST")
    private fun initData() {
        educationDetailsDisplayInformation = manageProfileViewModel.educationDetails.value ?: EducationDetailsDisplayInformation()

        originalAddressDetails.apply {
            manageProfileViewModel.customerInformation.value?.customerInformation?.employmentInformation?.let { employmentInformation ->
                employerName = employmentInformation.employerAddressDTO.addressLine1
                employerTelephoneNumber = employmentInformation.workTelephoneCode + employmentInformation.workNumber
                employerFaxNumber = employmentInformation.faxWorkCode + employmentInformation.workFaxNumber
                addressLineOne = employmentInformation.employerAddressDTO.addressLine2
                suburb = employmentInformation.employerAddressDTO.suburbRsa
                town = employmentInformation.employerAddressDTO.town
                postalCode = employmentInformation.employerAddressDTO.postalCode
            }
        }

        occupationListLookupTable = manageProfileViewModel.retrieveOccupationList()
        occupationSectorListLookupTable = manageProfileViewModel.retrieveOccupationSectorList()
        occupationSectorListLookupTable.add(0, LookupItem("00", "00", getString(R.string.none)))
        occupationLevelListLookupTable = manageProfileViewModel.retrieveOccupationLevelList()
        occupationLevelListLookupTable.add(0, LookupItem("00", "00", getString(R.string.none)))
        occupationStatusListLookupTable = manageProfileViewModel.retrieveOccupationStatusList()

        occupationStatusNormalInputView.setList(occupationStatusListLookupTable, getString(R.string.manage_profile_edit_employment_details_occupation_status_toolbar_title))
        occupationNormalInputView.setList(occupationListLookupTable, getString(R.string.manage_profile_edit_employment_details_occupation_toolbar_title))
        occupationSectorNormalInputView.setList(occupationSectorListLookupTable, getString(R.string.manage_profile_edit_employment_details_occupation_sector_toolbar_title))
        occupationLevelNormalInputView.setList(occupationLevelListLookupTable, getString(R.string.manage_profile_edit_employment_details_occupation_level_toolbar_title))

        if (manageProfileViewModel.employmentInformationStateInformation.selectedOccupationStatusIndex == -1) {
            selectedOccupation = occupationListLookupTable.indexOfFirst { lookupItem -> lookupItem.itemCode.equals(educationDetailsDisplayInformation.occupationCode, ignoreCase = true) }
            selectedOccupationLevel = occupationLevelListLookupTable.indexOfFirst { lookupItem -> lookupItem.itemCode.equals(educationDetailsDisplayInformation.occupationLevelCode, ignoreCase = true) }
            selectedOccupationStatus = occupationStatusListLookupTable.indexOfFirst { lookupItem -> lookupItem.itemCode.equals(educationDetailsDisplayInformation.occupationStatusCode, ignoreCase = true) }
            selectedOccupationSector = occupationSectorListLookupTable.indexOfFirst { lookupItem -> lookupItem.itemCode.equals(educationDetailsDisplayInformation.occupationSectorCode, ignoreCase = true) }

            occupationStatusNormalInputView.selectedIndex = selectedOccupationStatus
            toggleInputFields(occupationStatusListLookupTable[selectedOccupationStatus].itemCode.toString())
            occupationNormalInputView.selectedIndex = selectedOccupation
            occupationSectorNormalInputView.selectedIndex = selectedOccupationSector
            occupationLevelNormalInputView.selectedIndex = selectedOccupationLevel

            manageProfileViewModel.manageProfileEducationDetailsModel.apply {
                if (occupationStatus.isEmpty()) {
                    occupationStatus = occupationStatusListLookupTable[selectedOccupationStatus].defaultLabel.toString()
                }
                occupation = if (occupationNormalInputView.visibility == View.VISIBLE && occupation.isEmpty() && selectedOccupation > -1) occupationListLookupTable[selectedOccupation].defaultLabel.toString() else "00"
                occupationSector = if (occupationSectorNormalInputView.visibility == View.VISIBLE && occupationSector.isEmpty() && selectedOccupationSector > -1) occupationSectorListLookupTable[selectedOccupationSector].defaultLabel.toString() else "00"
                occupationLevel = if (occupationLevelNormalInputView.visibility == View.VISIBLE && occupationLevel.isEmpty() && selectedOccupationLevel > -1) occupationLevelListLookupTable[selectedOccupationLevel].defaultLabel.toString() else "00"
            }

            toggleVisibility()
        } else {
            manageProfileViewModel.employmentInformationStateInformation.apply {
                occupationStatusNormalInputView.selectedIndex = selectedOccupationStatusIndex
                occupationNormalInputView.selectedIndex = selectedOccupationIndex
                occupationSectorNormalInputView.selectedIndex = selectedOccupationSectorIndex
                occupationLevelNormalInputView.selectedIndex = selectedOccupationLevelIndex

                if (shouldShowAddressFields) {
                    manageProfileAddressWidget = ManageProfileAddressWidget(manageProfileActivity, AddressFlowType.EMPLOYER, originalAddressDetails, this@ManageProfileEditEmploymentDetailsFragment)
                    employerAddressDetailsLinearLayout.addView(manageProfileAddressWidget)
                    employerAddressDetailsLinearLayout.visibility = View.VISIBLE
                    if (manageProfileAddressDetailsViewModel.selectedPostSuburb.suburbPostalCode.isNotEmpty()) {
                        manageProfileAddressDetailsViewModel.selectedPostSuburb.apply {
                            Handler(Looper.getMainLooper()).postDelayed({
                                suburbNormalInputView?.selectedValue = suburbName
                                postalCodeLabelView?.setContentText(suburbPostalCode)
                                cityOrSuburbLabelView?.setContentText(townName)
                            }, 50)
                        }
                    }
                }
            }
        }
        toggleButton(validateData())
    }

    private fun toggleVisibility() {
        if (selectedOccupation == -1) {
            occupationNormalInputView.selectedValue = getString(R.string.none)
        }
        if (selectedOccupationLevel == -1) {
            hasOccupationLevelValueBeenChangedToNoneAutomatically = true
            occupationLevelNormalInputView.selectedValue = getString(R.string.none)
        }
        if (selectedOccupationSector == -1) {
            hasOccupationSectorValueBeenChangedToNoneAutomatically = true
            occupationSectorNormalInputView.selectedValue = getString(R.string.none)
        }
    }

    private fun initSelectorInterfaces() {
        occupationStatusNormalInputView.setItemSelectionInterface { index ->
            selectedOccupationStatus = index
            manageProfileViewModel.manageProfileEducationDetailsModel.occupationStatus = occupationStatusListLookupTable[selectedOccupationStatus].defaultLabel.toString()

            toggleInputFields(occupationStatusListLookupTable[index].itemCode.toString())

            if (isEmployerDetailsRequired(occupationStatusListLookupTable[selectedOccupationStatus].itemCode.toString()) && occupationStatusListLookupTable[selectedOccupationStatus].itemCode.toString() != manageProfileViewModel.originalEducationDetailsDisplayInformation.occupationStatusCode) {
                isEmploymentStatusChanged = true
                hasMandatoryFieldChanged = true
                hasFieldOnParentFragmentChanged = true
                if (selectedOccupation == -1) {
                    occupationNormalInputView.setError(getString(R.string.manage_profile_select_occupation_error))
                }
                if (!::manageProfileAddressWidget.isInitialized) {
                    manageProfileAddressWidget = ManageProfileAddressWidget(manageProfileActivity, AddressFlowType.EMPLOYER, originalAddressDetails, this)
                    employerAddressDetailsLinearLayout.addView(manageProfileAddressWidget)
                    employerAddressDetailsLinearLayout.visibility = View.VISIBLE
                }
            } else {
                isEmploymentStatusChanged = false
                employerAddressDetailsLinearLayout.visibility = View.GONE
            }
            toggleButton(validateData())
            AnalyticsUtil.trackAction(ANALYTICS_TAG, "ManageProfile_EmploymentDetailsScreen_OccupationStatusButtonClicked")
        }

        occupationNormalInputView.setItemSelectionInterface {
            selectedOccupation = it
            manageProfileViewModel.manageProfileEducationDetailsModel.occupation = if (occupationNormalInputView.visibility == View.VISIBLE) occupationListLookupTable[selectedOccupation].defaultLabel.toString() else ""
            hasFieldOnParentFragmentChanged = true
            toggleButton(validateData())
            occupationNormalInputView.hideError()
            AnalyticsUtil.trackAction(ANALYTICS_TAG, "ManageProfile_EmploymentDetailsScreen_OccupationButtonClicked")
        }

        occupationSectorNormalInputView.setItemSelectionInterface {
            selectedOccupationSector = it
            manageProfileViewModel.manageProfileEducationDetailsModel.occupationSector = if (occupationSectorNormalInputView.visibility == View.VISIBLE) occupationSectorListLookupTable[selectedOccupationSector].defaultLabel.toString() else ""
            hasOccupationSectorValueBeenChangedToNoneManually = true
            hasOccupationSectorValueBeenChangedToNoneAutomatically = false
            hasFieldOnParentFragmentChanged = true
            hasOptionalFieldChanged = true
            toggleButton(validateData())
            AnalyticsUtil.trackAction(ANALYTICS_TAG, "ManageProfile_EmploymentDetailsScreen_OccupationSectorButtonClicked")
        }
        occupationLevelNormalInputView.setItemSelectionInterface {
            selectedOccupationLevel = it
            manageProfileViewModel.manageProfileEducationDetailsModel.occupationLevel = if (occupationLevelNormalInputView.visibility == View.VISIBLE) occupationLevelListLookupTable[selectedOccupationLevel].defaultLabel.toString() else ""
            hasOccupationLevelValueBeenChangedToNoneManually = true
            hasOccupationLevelValueBeenChangedToNoneAutomatically = false
            hasFieldOnParentFragmentChanged = true
            hasOptionalFieldChanged = true
            toggleButton(validateData())
            AnalyticsUtil.trackAction(ANALYTICS_TAG, "ManageProfile_EmploymentDetailsScreen_OccupationLevelButtonClicked")
        }
    }

    private fun toggleInputFields(occupationStatusCode: String) {
        when {
            isNotEmployed(occupationStatusCode) -> {
                occupationNormalInputView.visibility = View.GONE
                occupationSectorNormalInputView.visibility = View.GONE
                occupationLevelNormalInputView.visibility = View.GONE
            }
            isSelfEmployed(occupationStatusCode) -> {
                isSelEmployed = true
                occupationNormalInputView.visibility = View.VISIBLE
                occupationSectorNormalInputView.visibility = View.VISIBLE
                occupationLevelNormalInputView.visibility = View.GONE
            }
            else -> {
                occupationNormalInputView.visibility = View.VISIBLE
                occupationSectorNormalInputView.visibility = View.VISIBLE
                occupationLevelNormalInputView.visibility = View.VISIBLE
            }
        }
    }

    private fun initOnClickListeners() {
        continueButton.setOnClickListener {
            manageProfileViewModel.confirmScreenItemList.clear()
            manageProfileViewModel.manageProfileEducationDetailsModel.occupationCode = if (occupationNormalInputView.isVisible) occupationListLookupTable[selectedOccupation].itemCode.toString() else "00"
            manageProfileViewModel.manageProfileEducationDetailsModel.occupationLevelCode = if (occupationLevelNormalInputView.isVisible && selectedOccupationLevel != -1) occupationLevelListLookupTable[selectedOccupationLevel].itemCode.toString() else "00"
            manageProfileViewModel.manageProfileEducationDetailsModel.occupationSectorCode = if (occupationSectorNormalInputView.isVisible && selectedOccupationSector != -1) occupationSectorListLookupTable[selectedOccupationSector].itemCode.toString() else "00"
            manageProfileViewModel.manageProfileEducationDetailsModel.occupationStatusCode = occupationStatusListLookupTable[selectedOccupationStatus].itemCode.toString()

            addItemToConfirmationScreen(occupationStatusNormalInputView.selectedValue, getString(R.string.manage_profile_edit_employment_details_occupation_status_label))

            if (occupationNormalInputView.isVisible) {
                addItemToConfirmationScreen(occupationNormalInputView.selectedValue, getString(R.string.manage_profile_edit_employment_details_occupation_label))
            }

            if (occupationLevelNormalInputView.isVisible) {
                addItemToConfirmationScreen(occupationLevelNormalInputView.selectedValue, getString(R.string.manage_profile_edit_employment_details_extract_string))
            }

            if (occupationSectorNormalInputView.isVisible) {
                addItemToConfirmationScreen(occupationSectorNormalInputView.selectedValue, getString(R.string.manage_profile_edit_employment_details_occupation_sector_label))
            }

            val employmentStatus = occupationStatusListLookupTable.find { occupationStatusNormalInputView.selectedValue.equals(it.defaultLabel, ignoreCase = true) }?.itemCode.toString()
            if (!isEmployerDetailsRequired(employmentStatus)) {
                manageProfileViewModel.isEmploymentFlowAddressRemoved = true
                manageProfileViewModel.wasAddressFieldsChangedInEmploymentFlow = false

                manageProfileViewModel.manageProfileEducationDetailsModel.apply {
                    employerAddressLineOne = " "
                    employerAddressLineTwo = " "
                    employerSuburb = " "
                    employerTown = " "
                    employerCountry = " "
                    employerFaxCode = ""
                    employerFaxNumber = ""
                    employerTelephoneCode = ""
                    employerTelephoneNumber = ""

                    occupationCode = "00"
                    occupationLevelCode = "00"
                    occupationSectorCode = "00"
                }
            } else {
                manageProfileViewModel.isEmploymentFlowAddressRemoved = false
                manageProfileViewModel.wasAddressFieldsChangedInEmploymentFlow = false
            }

            if (employerAddressDetailsLinearLayout.visibility == View.VISIBLE) {
                manageProfileViewModel.wasAddressFieldsChangedInEmploymentFlow = true
                manageProfileViewModel.manageProfileEducationDetailsModel.apply {
                    employerAddressLineOne = employerNameNormalInputView.selectedValue
                    employerAddressLineTwo = addressLineOneNormalInputView.selectedValue
                    employerSuburb = suburbNormalInputView.selectedValue
                    employerTown = cityOrSuburbLabelView.contentTextViewValue
                    employerPostalCode = postalCodeLabelView.contentTextViewValue
                    employerTelephoneCode = if (telephoneNumberNormalInputView.selectedValue.isNotEmpty()) telephoneNumberNormalInputView.selectedValue.removeSpaces().substring(0, 3) else " "
                    employerTelephoneNumber = if (telephoneNumberNormalInputView.selectedValue.isNotEmpty()) telephoneNumberNormalInputView.selectedValue.removeSpaces().substring(3, 10) else " "
                    employerFaxCode = if (faxNumberNormalInputView.selectedValue.isNotEmpty()) faxNumberNormalInputView.selectedValue.removeSpaces().substring(0, 3) else ""
                    employerFaxNumber = if (faxNumberNormalInputView.selectedValue.isNotEmpty()) faxNumberNormalInputView.selectedValue.removeSpaces().substring(3, 10) else ""

                    addItemToConfirmationScreen(employerAddressLineOne, getString(R.string.manage_profile_address_widget_employer_name_label))
                    addItemToConfirmationScreen((employerTelephoneCode + employerTelephoneNumber).toFormattedCellphoneNumber(), getString(R.string.manage_profile_address_widget_telephone_number))
                    addItemToConfirmationScreen((employerFaxCode + employerFaxNumber).toFormattedCellphoneNumber(), getString(R.string.manage_profile_address_widget_fax_number_label))
                    addItemToConfirmationScreen(employerAddressLineTwo, getString(R.string.manage_profile_address_widget_address_line_one_label))
                    addItemToConfirmationScreen(employerSuburb, getString(R.string.manage_profile_address_details_overview_suburb_label))
                    addItemToConfirmationScreen(employerTown, getString(R.string.manage_profile_address_details_overview_city_label))
                    addItemToConfirmationScreen(employerPostalCode, getString(R.string.manage_profile_address_details_overview_postal_code_label))
                }
            }

            navigate(ManageProfileEditEmploymentDetailsFragmentDirections.actionManageProfileEditEmploymentDetailsFragmentToManageProfileOccupationDetailsConfirmFragment())
        }
    }

    private fun addItemToConfirmationScreen(selectedValue: String, selectedLabel: String) {
        if (selectedValue.isNotEmpty()) {
            ConfirmScreenItem().apply {
                value = selectedValue
                label = selectedLabel
                manageProfileViewModel.confirmScreenItemList.add(this)
            }
        }
    }

    override fun saveFormData() {
        manageProfileViewModel.employmentInformationStateInformation.apply {
            selectedOccupationStatusIndex = selectedOccupationStatus
            selectedOccupationIndex = selectedOccupation
            selectedOccupationSectorIndex = selectedOccupationSector
            selectedOccupationLevelIndex = selectedOccupationLevel
            shouldShowAddressFields = employerAddressDetailsLinearLayout.visibility == View.VISIBLE
        }

        manageProfileViewModel.manageProfileAddressDetails.apply {
            telephoneNumber = telephoneNumberNormalInputView.selectedValue
            faxNumber = faxNumberNormalInputView.selectedValue
            addressLineOne = employerNameNormalInputView.selectedValue
            addressLineTwo = addressLineOneNormalInputView.selectedValue
        }
    }

    private fun toggleButton(validationResults: Boolean) {
        continueButton.isEnabled = validationResults
    }

    private fun validateInputs(): Boolean {
        return when {
            hasMandatoryFieldChanged && hasOptionalFieldChanged && employerAddressDetailsLinearLayout.isVisible -> {
                validateOptionalFields() && validateMandatoryFields() && manageProfileAddressWidget.validateInputs()
            }
            hasMandatoryFieldChanged && !hasOptionalFieldChanged && employerAddressDetailsLinearLayout.isVisible -> {
                validateMandatoryFields() && manageProfileAddressWidget.validateInputs()
            }
            hasOptionalFieldChanged -> {
                validateOptionalFields()
            }
            hasMandatoryFieldChanged && !hasOptionalFieldChanged && employerAddressDetailsLinearLayout.isVisible -> {
                validateMandatoryFields() && manageProfileAddressWidget.validateInputs()
            }
            else -> validateMandatoryFields()
        }
    }

    private fun validateField(inputView: NormalInputView<*>, originalValue: String): Boolean {
        return when {
            inputView.isVisible && !isOptionalField(inputView) && inputView.selectedValue.isNotEmpty() && inputView.selectedValue != originalValue -> {
                true
            }
            inputView.isVisible && isOptionalField(inputView) && inputView.selectedValue != originalValue -> {
                true
            }
            inputView.isVisible && isOptionalField(inputView) && inputView.selectedValue != originalValue && !hasOccupationSectorValueBeenChangedToNoneAutomatically && hasOccupationSectorValueBeenChangedToNoneManually -> {
                true
            }
            else -> false
        }
    }

    private fun isOptionalField(inputView: NormalInputView<*>): Boolean {
        val listOfOptionalFields = listOf<NormalInputView<*>>(occupationLevelNormalInputView, occupationSectorNormalInputView)
        return inputView in listOfOptionalFields
    }

    override fun validateData(validationResult: Boolean?, wasAnyFieldsChanged: Boolean?): Boolean {
        val isValid = (!::manageProfileAddressWidget.isInitialized && validateInputs()) || (::manageProfileAddressWidget.isInitialized && manageProfileAddressWidget.validateInputs() && validateInputs())
        when {
            isEmploymentStatusChanged && employerAddressDetailsLinearLayout.isVisible && wasAnyFieldsChanged == null -> {
                toggleButton(!manageProfileAddressWidget.validateInputs() && validateInputs())
            }
            isEmploymentStatusChanged && !hasOptionalFieldChanged && employerAddressDetailsLinearLayout.isVisible && wasAnyFieldsChanged == null && !manageProfileAddressWidget.validateInputs() -> {
                toggleButton(true)
            }
            hasFieldOnParentFragmentChanged && employerAddressDetailsLinearLayout.isVisible && wasAnyFieldsChanged == null -> {
                toggleButton(!manageProfileAddressWidget.validateInputs() && validateInputs())
            }
            hasFieldOnParentFragmentChanged && employerAddressDetailsLinearLayout.isVisible && wasAnyFieldsChanged == false -> {
                toggleButton(manageProfileAddressWidget.validateInputs() && validateInputs())
            }
            hasFieldOnParentFragmentChanged && employerAddressDetailsLinearLayout.isVisible && wasAnyFieldsChanged == true -> {
                toggleButton(manageProfileAddressWidget.validateInputs() && validateInputs())
            }
            hasFieldOnParentFragmentChanged && !employerAddressDetailsLinearLayout.isVisible -> {
                toggleButton(validateInputs())
            }
            hasOptionalFieldChanged && hasFieldOnParentFragmentChanged && !employerAddressDetailsLinearLayout.isVisible -> {
                toggleButton(validateInputs() && validateOptionalFields())
            }
            isEmploymentStatusChanged && employerAddressDetailsLinearLayout.isVisible && wasAnyFieldsChanged == true -> {
                toggleButton(!manageProfileAddressWidget.validateInputs() && validateInputs())
            }
            else -> {
                toggleButton(validateInputs())
            }
        }
        return isValid
    }

    private fun validateOptionalFields(): Boolean {
        return validateField(occupationLevelNormalInputView, manageProfileViewModel.originalEducationDetailsDisplayInformation.occupationLevel) ||
                validateField(occupationSectorNormalInputView, manageProfileViewModel.originalEducationDetailsDisplayInformation.occupationSector)
    }

    private fun validateMandatoryFields(): Boolean {
        return if (selectedOccupation == -1 && isEmploymentStatusChanged) {
            false
        } else {
            validateField(occupationStatusNormalInputView, manageProfileViewModel.originalEducationDetailsDisplayInformation.occupationStatus) ||
                    validateField(occupationNormalInputView, manageProfileViewModel.originalEducationDetailsDisplayInformation.occupation)
        }
    }

    private fun isEmployerDetailsRequired(occupationCode: String): Boolean = listOf(OccupationStatus.FULL_TIME_EMPLOYED, OccupationStatus.PART_TIME_CONTRACT_WORKER, OccupationStatus.SELF_EMPLOYED_PROFESSIONAL, OccupationStatus.TEMPORARY_EMPLOYED, OccupationStatus.SELF_EMPLOYED_NON_PROFESSIONAL)
            .any { it.occupationCode == occupationCode }

    private fun isSelfEmployed(occupationCode: String): Boolean = listOf(OccupationStatus.SELF_EMPLOYED_PROFESSIONAL, OccupationStatus.SELF_EMPLOYED_NON_PROFESSIONAL)
            .any { it.occupationCode == occupationCode }
}