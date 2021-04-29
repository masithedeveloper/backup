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

package com.barclays.absa.banking.manage.profile.ui.widgets

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isEmpty
import androidx.core.view.isVisible
import androidx.navigation.findNavController
import com.barclays.absa.banking.R
import com.barclays.absa.banking.framework.utils.ContactDialogOptionListener
import com.barclays.absa.banking.manage.profile.ui.docHandler.SELECT_FAX_NO_REQUEST_CODE
import com.barclays.absa.banking.manage.profile.ui.docHandler.SELECT_TELEPHONE_NO_REQUEST_CODE
import kotlinx.android.synthetic.main.manage_profile_address_entry_layout.view.*
import styleguide.forms.NormalInputView
import styleguide.utils.extensions.removeSpaces
import styleguide.utils.extensions.toTitleCase
import kotlin.math.abs

class ManageProfileAddressWidget(context: Context, addressFlowTypeType: AddressFlowType, val addressDetails: AddressDetails, private val manageProfileHostFragment: ManageProfileAddressInterface, private val residentialAddress: AddressDetails? = null) : ConstraintLayout(context, null, 0) {
    constructor(context: Context, addressFlowTypeType: AddressFlowType, addressDetails: AddressDetails, manageProfileHostFragment: ManageProfileAddressInterface) : this(context, addressFlowTypeType, addressDetails, manageProfileHostFragment, null)

    private var originalAddressValues: AddressDetails? = null
    private var isUserEditingField: Boolean = false
    private var isCheckBoxClicked: Boolean = false
    private var hasOptionalFieldsChanged: Boolean = false
    private var isRequiredFieldEmpty: Boolean = false
    private var hasMandatoryFieldChanged: Boolean = false
    private var hasSuburbChanged: Boolean = false
    private var isPostalCodeSameAsResidential: Boolean = false

    init {
        View.inflate(context, R.layout.manage_profile_address_entry_layout, this)

        if (addressFlowTypeType == AddressFlowType.EMPLOYER) {
            employerNameNormalInputView.visibility = View.VISIBLE
            telephoneNumberNormalInputView.visibility = View.VISIBLE
            addressLineTwoNormalInputView.visibility = View.GONE
            addressDetails.apply {
                employerNameNormalInputView.selectedValue = employerName
                telephoneNumberNormalInputView.selectedValue = employerTelephoneNumber
            }

            addressLineOneNormalInputView.setTitleText(context.getString(R.string.manage_profile_employer_address))
            employerNameNormalInputView.addValueViewTextWatcher(genericValidationOfFields(employerNameNormalInputView))
            addressLineOneNormalInputView.addValueViewTextWatcher(genericValidationOfFields(addressLineOneNormalInputView))
            telephoneNumberNormalInputView.addValueViewTextWatcher(genericValidationOfFields(telephoneNumberNormalInputView))
            faxNumberNormalInputView.addValueViewTextWatcher(genericValidationOfFields(faxNumberNormalInputView))
        } else {
            sameAsResidentialAddressCheckboxView.visibility = View.VISIBLE
        }

        if (addressFlowTypeType == AddressFlowType.PERSONAL) {
            addressDetails.apply {
                addressLineOneNormalInputView.selectedValue = addressLineOne.toTitleCase()
                addressLineTwoNormalInputView.selectedValue = addressLineTwo.toTitleCase()
                suburbNormalInputView.selectedValue = suburb.toTitleCase()
                cityOrSuburbLabelView.setContentText(town.toTitleCase())
                postalCodeLabelView.setContentText(postalCode.toTitleCase())
            }
        } else {
            // Because this will end up causing a lot of confusion to anyone who looks at Manage profile, here is a quick summary:
            // Due to the way Mainframe works, Address line one is not Address line One. Its actually the employer name. It gets returned as Address line one however.
            // Address line two is always the physical address. So address line two from the service is displayed as Employer address on our side.
            addressDetails.apply {
                employerNameNormalInputView.selectedValue = employerName.toTitleCase()
                addressLineOneNormalInputView.selectedValue = addressLineTwo.toTitleCase()
                suburbNormalInputView.selectedValue = suburb.toTitleCase()
                cityOrSuburbLabelView.setContentText(town.toTitleCase())
                postalCodeLabelView.setContentText(postalCode.toTitleCase())
            }
        }

        addressLineOneNormalInputView.addValueViewTextWatcher(genericValidationOfFields(addressLineOneNormalInputView))
        addressLineTwoNormalInputView.addValueViewTextWatcher(genericValidationOfFields(addressLineTwoNormalInputView))
        suburbNormalInputView.addValueViewTextWatcher(genericValidationOfFields(suburbNormalInputView))
        telephoneNumberNormalInputView.setImageViewOnTouchListener(ContactDialogOptionListener(telephoneNumberNormalInputView.editText, R.string.selFrmPhoneBookMsg, context, SELECT_TELEPHONE_NO_REQUEST_CODE, null))
        faxNumberNormalInputView.setImageViewOnTouchListener(ContactDialogOptionListener(faxNumberNormalInputView.editText, R.string.selFrmPhoneBookMsg, context, SELECT_FAX_NO_REQUEST_CODE, null))

        suburbNormalInputView.setOnClickListener {
            manageProfileHostFragment.saveFormData()
            val isPoBox = addressLineOneNormalInputView.selectedValue.removeSpaces().contains(context.getString(R.string.manage_profile_pobox_string), ignoreCase = true)
            val bundle = ManageProfilePostalCodeLookupFragmentArgs.Builder(isPoBox).build().toBundle()
            findNavController().navigate(R.id.manageProfilePostalCodeLookupFragment, bundle)
        }

        if (sameAsResidentialAddressCheckboxView.isVisible) {
            sameAsResidentialAddressCheckboxView.setOnCheckedListener { isSameAsResidentialAddress ->
                isCheckBoxClicked = true
                isPostalCodeSameAsResidential = isSameAsResidentialAddress
                if (!isUserEditingField) {
                    if (isSameAsResidentialAddress) {
                        changeFieldsToResidentialAddressValues()
                    } else {
                        undoValueChanges()
                    }
                }
            }
        }
    }

    private fun changeFieldsToResidentialAddressValues() {
        originalAddressValues = AddressDetails().apply {
            addressLineOne = addressLineOneNormalInputView.selectedValue
            addressLineTwo = addressLineTwoNormalInputView.selectedValue
            suburb = suburbNormalInputView.selectedValue
            town = cityOrSuburbLabelView.contentTextViewValue
            postalCode = postalCodeLabelView.contentTextViewValue

            addressLineOneNormalInputView.selectedValue = residentialAddress?.addressLineOne.toTitleCase()
            addressLineTwoNormalInputView.selectedValue = residentialAddress?.addressLineTwo.toTitleCase()
            suburbNormalInputView.selectedValue = residentialAddress?.suburb.toTitleCase()
            cityOrSuburbLabelView.setContentText(residentialAddress?.town.toTitleCase())
            postalCodeLabelView.setContentText(residentialAddress?.postalCode.toTitleCase())
        }
    }

    private fun undoValueChanges() {
        addressLineOneNormalInputView.selectedValue = originalAddressValues?.addressLineOne.toTitleCase()
        addressLineTwoNormalInputView.selectedValue = originalAddressValues?.addressLineTwo.toTitleCase()
        suburbNormalInputView.selectedValue = originalAddressValues?.suburb.toTitleCase()
        cityOrSuburbLabelView.setContentText(originalAddressValues?.town.toTitleCase())
        postalCodeLabelView.setContentText(originalAddressValues?.postalCode.toTitleCase())
    }

    private fun genericValidationOfFields(inputView: NormalInputView<*>): TextWatcher {
        val listOfOptionalFields = arrayListOf<NormalInputView<*>>(addressLineTwoNormalInputView, telephoneNumberNormalInputView, faxNumberNormalInputView)
        return object : TextWatcher {
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                isUserEditingField = abs(count - before) == 1
                if (inputView.hasError()) {
                    inputView.hideError()
                }
                validateInputs()
            }

            override fun afterTextChanged(s: Editable?) {
                if (sameAsResidentialAddressCheckboxView.visibility == View.VISIBLE && sameAsResidentialAddressCheckboxView.isChecked && isUserEditingField) {
                    sameAsResidentialAddressCheckboxView.isChecked = false
                    isCheckBoxClicked = false
                }
                isUserEditingField = false

                when {
                    inputView in listOfOptionalFields && inputView.hasValueChanged() -> {
                        isRequiredFieldEmpty = !validateThatNoMandatoryFieldsAreNotEmpty()
                        hasOptionalFieldsChanged = validateOptionalFields()
                        hasMandatoryFieldChanged = !validateThatNoValuesHasChanged()
                        manageProfileHostFragment.validateData(validateInputs(), hasMandatoryFieldChanged || hasOptionalFieldsChanged)
                        if (inputView.hasError()) {
                            inputView.hideError()
                        }
                    }
                    inputView in listOfOptionalFields && inputView.isEmpty() && !inputView.hasValueChanged() -> {
                        isRequiredFieldEmpty = !validateThatNoMandatoryFieldsAreNotEmpty()
                        hasOptionalFieldsChanged = validateOptionalFields()
                        manageProfileHostFragment.validateData(validateInputs(), hasMandatoryFieldChanged || hasOptionalFieldsChanged)
                        if (inputView.hasError()) {
                            inputView.hideError()
                        }
                    }
                    inputView.selectedValue.isEmpty() && inputView !in listOfOptionalFields -> {
                        isRequiredFieldEmpty = validateThatNoMandatoryFieldsAreNotEmpty()
                        inputView.setError(context.getString(R.string.manage_profile_address_widget_field_is_mandatory))
                        manageProfileHostFragment.validateData(false, hasMandatoryFieldChanged || hasOptionalFieldsChanged)
                    }
                    inputView.hasValueChanged() && inputView !in listOfOptionalFields && inputView.selectedValue.isNotEmpty() && validateThatNoMandatoryFieldsAreNotEmpty() -> {
                        isRequiredFieldEmpty = !validateThatNoMandatoryFieldsAreNotEmpty()
                        hasOptionalFieldsChanged = validateOptionalFields()
                        hasMandatoryFieldChanged = validateMandatoryFields()
                        hasSuburbChanged = suburbNormalInputView.hasValueChanged()
                        manageProfileHostFragment.validateData(validateInputs(), hasMandatoryFieldChanged || hasOptionalFieldsChanged)
                    }
                    else -> {
                        isRequiredFieldEmpty = !validateThatNoMandatoryFieldsAreNotEmpty()
                        hasMandatoryFieldChanged = validateMandatoryFields()
                        hasOptionalFieldsChanged = validateOptionalFields()
                        hasSuburbChanged = suburbNormalInputView.hasValueChanged()
                        manageProfileHostFragment.validateData(validateInputs(), hasMandatoryFieldChanged || hasOptionalFieldsChanged)
                    }
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        }
    }

    private fun validateOptionalFields(): Boolean = addressLineTwoNormalInputView.hasValueChanged() ||
            telephoneNumberNormalInputView.hasValueChanged() && telephoneNumberNormalInputView.selectedValue.length == 12 ||
            faxNumberNormalInputView.hasValueChanged() && faxNumberNormalInputView.selectedValue.length == 12

    private fun validateMandatoryFields(): Boolean = (addressLineOneNormalInputView.hasValueChanged() ||
            suburbNormalInputView.hasValueChanged() ||
            !cityOrSuburbLabelView.contentTextViewValue.equals(addressDetails.town, ignoreCase = true) ||
            !postalCodeLabelView.contentTextViewValue.equals(addressDetails.postalCode, ignoreCase = true)) ||
            employerNameNormalInputView.hasValueChanged()


    private fun validateThatNoValuesHasChanged(): Boolean = !addressLineOneNormalInputView.hasValueChanged() &&
            !suburbNormalInputView.hasValueChanged() &&
            cityOrSuburbLabelView.contentTextViewValue.equals(addressDetails.town, ignoreCase = true) &&
            postalCodeLabelView.contentTextViewValue.equals(addressDetails.postalCode, ignoreCase = true) &&
            !employerNameNormalInputView.hasValueChanged()

    private fun validateThatNoMandatoryFieldsAreNotEmpty(): Boolean {
        val listOfRequiredFields = arrayListOf<NormalInputView<*>>(addressLineOneNormalInputView, employerNameNormalInputView, suburbNormalInputView)
        listOfRequiredFields.forEach {
            if (it.isVisible && it.selectedValue.isEmpty()) {
                isRequiredFieldEmpty = true
                return false
            }
        }
        return true
    }

    private fun validateThatNoMandatoryFieldsMinusSuburbAreNotEmpty(): Boolean {
        val listOfRequiredFields = arrayListOf<NormalInputView<*>>(addressLineOneNormalInputView, employerNameNormalInputView)
        listOfRequiredFields.forEach {
            if (it.isVisible && it.selectedValue.isEmpty()) {
                isRequiredFieldEmpty = true
                return false
            }
        }
        if (suburbNormalInputView.hasError()) {
            suburbNormalInputView.hideError()
        }
        return true
    }

    fun validateInputs(): Boolean = when {
        isPostalCodeSameAsResidential -> validateThatNoMandatoryFieldsMinusSuburbAreNotEmpty()
        hasOptionalFieldsChanged && hasSuburbChanged -> validateThatNoMandatoryFieldsAreNotEmpty() && validateOptionalFields()
        hasMandatoryFieldChanged && hasSuburbChanged -> validateThatNoMandatoryFieldsAreNotEmpty()
        hasOptionalFieldsChanged && isRequiredFieldEmpty -> !isRequiredFieldEmpty
        hasOptionalFieldsChanged && !hasMandatoryFieldChanged -> validateOptionalFields()
        hasOptionalFieldsChanged && hasMandatoryFieldChanged && !isRequiredFieldEmpty -> validateOptionalFields() && validateMandatoryFields() && validateThatNoMandatoryFieldsAreNotEmpty()
        hasOptionalFieldsChanged && hasMandatoryFieldChanged -> validateOptionalFields() && !validateMandatoryFields() && validateThatNoMandatoryFieldsAreNotEmpty()
        hasMandatoryFieldChanged && validateThatNoMandatoryFieldsAreNotEmpty() -> validateMandatoryFields()
        isRequiredFieldEmpty -> validateThatNoMandatoryFieldsAreNotEmpty() && validateOptionalFields()
        validateThatNoValuesHasChanged() -> !validateThatNoValuesHasChanged()
        else -> validateMandatoryFields()
    }
}