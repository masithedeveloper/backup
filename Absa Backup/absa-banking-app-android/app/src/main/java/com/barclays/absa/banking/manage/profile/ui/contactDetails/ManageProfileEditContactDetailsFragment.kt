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

package com.barclays.absa.banking.manage.profile.ui.contactDetails

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.TextView
import androidx.navigation.fragment.findNavController
import com.barclays.absa.banking.R
import com.barclays.absa.banking.framework.BaseFragment
import com.barclays.absa.banking.framework.app.BMBApplication
import com.barclays.absa.banking.manage.profile.services.dto.ContactInformation
import com.barclays.absa.banking.manage.profile.services.dto.PostalAddress
import com.barclays.absa.banking.manage.profile.ui.ManageProfileBaseFragment
import com.barclays.absa.banking.manage.profile.ui.ManageProfileConstants
import com.barclays.absa.banking.manage.profile.ui.ManageProfileConstants.ADDRESS_DETAILS
import com.barclays.absa.banking.manage.profile.ui.ManageProfileConstants.ON_BUTTON_CLICK_EVENT
import com.barclays.absa.banking.manage.profile.ui.ManageProfileConstants.TOOLBAR_TITLE
import com.barclays.absa.banking.manage.profile.ui.addressDetails.GenericAddressDetails
import com.barclays.absa.banking.manage.profile.ui.addressDetails.ManageProfileGenericAddressFragmentDirections
import com.barclays.absa.banking.manage.profile.ui.addressDetails.ManageProfileUpdatedAddressDetails
import com.barclays.absa.banking.manage.profile.ui.models.AddressLineFields
import com.barclays.absa.banking.manage.profile.ui.widgets.AddressDetails
import com.barclays.absa.banking.manage.profile.ui.widgets.AddressFlowType
import com.barclays.absa.banking.shared.services.dto.LookupItem
import com.barclays.absa.utils.AnalyticsUtil
import com.barclays.absa.utils.ValidationUtils
import kotlinx.android.synthetic.main.manage_profile_edit_contact_details_fragment.*
import styleguide.forms.NormalInputView
import styleguide.forms.SelectorList
import styleguide.forms.validation.CellphoneNumberValidationRule
import styleguide.forms.validation.LandLineValidationRule
import styleguide.forms.validation.addValidationRule
import styleguide.utils.extensions.toFormattedCellphoneNumber
import styleguide.utils.extensions.toTitleCase
import java.io.Serializable

class ManageProfileEditContactDetailsFragment : ManageProfileBaseFragment(R.layout.manage_profile_edit_contact_details_fragment) {
    private lateinit var genericAddressDetails: GenericAddressDetails
    private var preferredCommunicationList = SelectorList<LookupItem>()
    private lateinit var contactInformation: ContactInformation
    private lateinit var postalAddress: PostalAddress

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setToolBar(R.string.manage_profile_contact_details_toolbar_title)

        if (arguments?.getParcelable<ManageProfileUpdatedAddressDetails>(ADDRESS_DETAILS) != null) {
            arguments?.getParcelable<ManageProfileUpdatedAddressDetails>(ADDRESS_DETAILS)?.let { initRestoredData(it) }
        }

        initData()
        setUpItemSelectionInterface()
        setUpTextWatchers()
        setUpOnClickListeners()
        continueButton.isEnabled = hasContactDetailsChanged() || hasPostalAddressDetailsChanged()
    }

    private fun initRestoredData(manageProfileUpdatedAddressDetails: ManageProfileUpdatedAddressDetails) {
        manageProfileViewModel.editedAddressFields = AddressLineFields()

        manageProfileViewModel.postalAddressDetailsToUpdate.apply {
            addressLineOne = manageProfileUpdatedAddressDetails.addressLineOne
            addressLineTwo = manageProfileUpdatedAddressDetails.addressLineTwo
            addressSuburb = manageProfileUpdatedAddressDetails.suburb
            addressCity = manageProfileUpdatedAddressDetails.town
            addressPostalCode = manageProfileUpdatedAddressDetails.postalCode
        }

        manageProfileViewModel.editedAddressFields.addressLine1 = manageProfileUpdatedAddressDetails.addressLineOne
        manageProfileViewModel.editedAddressFields.addressLine2 = manageProfileUpdatedAddressDetails.addressLineTwo
    }

    private fun initData() {
        contactInformation = manageProfileViewModel.customerInformation.value?.customerInformation?.contactInformation ?: ContactInformation()
        postalAddress = manageProfileViewModel.customerInformation.value?.customerInformation?.postalAddress ?: PostalAddress()

        genericAddressDetails = GenericAddressDetails().apply {
            genericAddress = AddressDetails().apply {
                addressLineOne = postalAddress.addressLine1
                addressLineTwo = postalAddress.addressLine2
                suburb = postalAddress.suburbRsa
                town = postalAddress.town
                postalCode = postalAddress.postalCode
            }

            residentialAddress = AddressDetails().apply {
                manageProfileViewModel.customerInformation.value?.customerInformation?.residentialAddress?.let {
                    addressLineOne = it.addressLine1
                    addressLineTwo = it.addressLine2
                    suburb = it.suburbRsa
                    town = it.town
                    postalCode = it.postalCode
                }
            }

            flowType = AddressFlowType.PERSONAL
        }

        preferredCommunicationList = manageProfileViewModel.retrieveCommunicationMethodList()
        cellphoneNormalInputView.selectedValue = manageProfileViewModel.contactInformationToUpdate.cellNumber
        homePhoneNormalInputView.selectedValue = (manageProfileViewModel.contactInformationToUpdate.homeTelephoneCode + manageProfileViewModel.contactInformationToUpdate.homeNumber).toFormattedCellphoneNumber()
        homeFaxNormalInputView.selectedValue = (manageProfileViewModel.contactInformationToUpdate.faxHomeCode + manageProfileViewModel.contactInformationToUpdate.homeFaxNumber).toFormattedCellphoneNumber()
        emailAddressNormalInputView.selectedValue = manageProfileViewModel.contactInformationToUpdate.email.toLowerCase(BMBApplication.getApplicationLocale())

        manageProfileViewModel.communicationMethodLookUpResult.value?.let {
            preferredCommunicationNormalInputView.selectedValue = manageProfileViewModel.getLookupValue(it, manageProfileViewModel.communicationMethodToUpdate)
            val lookupItem = manageProfileViewModel.getLookupItem(it, manageProfileViewModel.communicationMethodToUpdate)
            preferredCommunicationNormalInputView.selectedIndex = when (lookupItem.itemCode) {
                "01" -> 0
                "02" -> 1
                "03" -> 2
                "04" -> 3
                else -> -1
            }
        }

        preferredCommunicationNormalInputView.setList(preferredCommunicationList, getString(R.string.manage_profile_contact_details_select_method))
        setVisibilityOfPostalAddressDetails()
    }

    private fun saveContactAndPostalAddressDetails() {
        manageProfileViewModel.contactInformationToUpdate.apply {
            cellNumber = cellphoneNormalInputView.selectedValue

            if (homePhoneNormalInputView.selectedValue.length >= 3) {
                homeTelephoneCode = homePhoneNormalInputView.selectedValue.substring(0, 3)
                homeNumber = homePhoneNormalInputView.selectedValue.substring(3)
            } else {
                homeTelephoneCode = " "
                homeNumber = " "
            }
            if (homeFaxNormalInputView.visibility == View.VISIBLE && homeFaxNormalInputView.selectedValue.length >= 3) {
                faxHomeCode = homeFaxNormalInputView.selectedValue.substring(0, 3)
                homeFaxNumber = homeFaxNormalInputView.selectedValue.substring(3)
            }
            email = emailAddressNormalInputView.selectedValue
        }

        manageProfileViewModel.postalAddressDetailsToUpdate.apply {
            addressLineOne = if (addressLine1TextView.text.isEmpty()) manageProfileViewModel.postalAddressDetailsToUpdate.addressLineOne else addressLine1TextView.text.toString()
            addressLineTwo = if (addressLine2TextView.text.isEmpty()) manageProfileViewModel.postalAddressDetailsToUpdate.addressLineTwo else addressLine2TextView.text.toString()
            addressSuburb = if (suburbTextView.text.isEmpty()) manageProfileViewModel.postalAddressDetailsToUpdate.addressSuburb else suburbTextView.text.toString()
            addressCity = if (cityTextView.text.isEmpty()) manageProfileViewModel.postalAddressDetailsToUpdate.addressCity else cityTextView.text.toString()
            addressPostalCode = if (postalCodeTextView.text.isEmpty()) manageProfileViewModel.postalAddressDetailsToUpdate.addressPostalCode else postalCodeTextView.text.toString()
        }
    }

    private fun setUpOnClickListeners() {
        postalAddressHeadingAndActionView.setCustomActionOnclickListener {
            AnalyticsUtil.trackAction(ManageProfileConstants.ANALYTICS_TAG, "ManageProfile_ContactDetailsSummaryScreen_EditButtonClicked")
            Bundle().apply {
                putString(TOOLBAR_TITLE, getString(R.string.manage_profile_address_details_edit_postal_address_toolbar_title))
                putParcelable(ADDRESS_DETAILS, genericAddressDetails)
                putSerializable(ON_BUTTON_CLICK_EVENT, nextStep() as Serializable)
                putString(ManageProfileConstants.ON_BUTTON_CLICK_EVENT_TAG, "ManageProfile_ContactDetailsAddressPostalAddressScreen_ContinueButtonClicked")
                findNavController().navigate(R.id.action_manageProfileEditContactDetailsFragment_to_manageProfileGenericAddressFragment, this)
            }
        }

        continueButton.setOnClickListener {
            AnalyticsUtil.trackAction(ManageProfileConstants.ANALYTICS_TAG, "ManageProfile_ContactDetailsScreen_ContinueButtonClicked")
            if (isContactDetailsFieldsValid()) {
                manageProfileViewModel.hasPostalAddressDetailsChanged = hasPostalAddressDetailsChanged()
                saveContactAndPostalAddressDetails()
                navigate(ManageProfileEditContactDetailsFragmentDirections.actionManageProfileEditContactDetailsFragmentToManageProfileContactDetailsConfirmationFragment())
            }
        }
    }

    private fun isContactDetailsFieldsValid(): Boolean {
        when {
            cellphoneNormalInputView.selectedValue.isEmpty() -> cellphoneNormalInputView.setError(getString(R.string.manage_profile_next_of_kin_cellphone_number_error))
            cellphoneNormalInputView.selectedValue.isNotEmpty() && !ValidationUtils.validatePhoneNumberInput(cellphoneNormalInputView.selectedValue) -> cellphoneNormalInputView.setError(getString(R.string.manage_profile_next_of_kin_valid_cellphone_number_error))
            homePhoneNormalInputView.selectedValue.isNotEmpty() && !ValidationUtils.validatePhoneNumberInput(homePhoneNormalInputView.selectedValue) -> homePhoneNormalInputView.setError(getString(R.string.manage_profile_next_of_kin_valid_homephone_number_error))
            homeFaxNormalInputView.visibility == View.VISIBLE && homeFaxNormalInputView.selectedValue.isNotEmpty() && !ValidationUtils.validatePhoneNumberInput(homeFaxNormalInputView.selectedValue) -> homeFaxNormalInputView.setError(getString(R.string.manage_profile_contact_details_valid_home_fax_number_error))
            emailAddressNormalInputView.selectedValue.isEmpty() -> emailAddressNormalInputView.setError(getString(R.string.manage_profile_contact_details_email_address_error))
            emailAddressNormalInputView.selectedValue.isNotEmpty() && !ValidationUtils.isValidEmailAddress(emailAddressNormalInputView.selectedValue) -> emailAddressNormalInputView.setError(getString(R.string.manage_profile_next_of_kin_valid_email_address_error))
            else -> return true
        }
        return false
    }

    private fun setUpItemSelectionInterface() {
        preferredCommunicationNormalInputView.setItemSelectionInterface { index ->
            var itemCode = ""
            preferredCommunicationList[index].itemCode?.let {
                itemCode = it
            }
            manageProfileViewModel.communicationMethodToUpdate = itemCode
            continueButton.isEnabled = hasContactDetailsChanged() || hasPostalAddressDetailsChanged()
            setVisibilityOfPostalAddressDetails()
        }
    }

    private fun setVisibilityOfPostalAddressDetails() {
        if ("01".equals(manageProfileViewModel.communicationMethodToUpdate, true)) {
            setTextViewTextAndVisibility(addressLine1TextView, manageProfileViewModel.postalAddressDetailsToUpdate.addressLineOne)
            setTextViewTextAndVisibility(addressLine2TextView, manageProfileViewModel.postalAddressDetailsToUpdate.addressLineTwo)
            setTextViewTextAndVisibility(suburbTextView, manageProfileViewModel.postalAddressDetailsToUpdate.addressSuburb)
            setTextViewTextAndVisibility(cityTextView, manageProfileViewModel.postalAddressDetailsToUpdate.addressCity)
            setTextViewTextAndVisibility(postalCodeTextView, manageProfileViewModel.postalAddressDetailsToUpdate.addressPostalCode)
            postalAddressConstraintLayout.visibility = View.VISIBLE
        } else {
            postalAddressConstraintLayout.visibility = View.GONE
        }
    }

    private fun setTextViewTextAndVisibility(textView: TextView, text: String) {
        if (text.isEmpty()) {
            textView.visibility = View.GONE
        } else {
            textView.text = text.toTitleCase()
            textView.visibility = View.VISIBLE
        }
    }

    private fun setUpTextWatchers() {
        cellphoneNormalInputView.addValueViewTextWatcher(cellphoneNumberValidation(cellphoneNormalInputView))
        homePhoneNormalInputView.addValueViewTextWatcher(homeTelephoneNumberValidation(homePhoneNormalInputView))
        homeFaxNormalInputView.addValueViewTextWatcher(genericTextWatcher(homeFaxNormalInputView))
        emailAddressNormalInputView.addValueViewTextWatcher(genericTextWatcher(emailAddressNormalInputView))
        preferredCommunicationNormalInputView.addValueViewTextWatcher(genericTextWatcher(preferredCommunicationNormalInputView))
    }

    private fun genericTextWatcher(normalInputView: NormalInputView<*>): TextWatcher {
        return object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                continueButton.isEnabled = hasContactDetailsChanged() || hasPostalAddressDetailsChanged()
                normalInputView.clearError()
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        }
    }

    private fun homeTelephoneNumberValidation(normalInputView: NormalInputView<*>): TextWatcher {
        val homePhoneInputField = arrayListOf(homePhoneNormalInputView)
        return object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (normalInputView in homePhoneInputField && s?.length == 12) {
                    normalInputView.addValidationRule(LandLineValidationRule(R.string.manage_profile_next_of_kin_valid_homephone_number_error))
                    continueButton.isEnabled = normalInputView.validate()
                } else if (normalInputView in homePhoneInputField && !s.isNullOrEmpty() && s.length != 12) {
                    continueButton.isEnabled = false
                } else {
                    continueButton.isEnabled = hasContactDetailsChanged() || hasPostalAddressDetailsChanged()
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        }
    }

    private fun cellphoneNumberValidation(normalInputView: NormalInputView<*>): TextWatcher {
        val cellInputField = arrayListOf(cellphoneNormalInputView)
        return object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (normalInputView in cellInputField && s?.length == 12) {
                    normalInputView.addValidationRule(CellphoneNumberValidationRule(R.string.manage_profile_next_of_kin_valid_cellphone_number_error))
                    continueButton.isEnabled = normalInputView.validate()
                } else if (normalInputView in cellInputField && !s.isNullOrEmpty() && s.length != 12) {
                    continueButton.isEnabled = false
                } else {
                    continueButton.isEnabled = hasContactDetailsChanged() || hasPostalAddressDetailsChanged()
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        }
    }

    private fun hasContactDetailsChanged(): Boolean {
        return contactInformation.cellNumber.toFormattedCellphoneNumber() != cellphoneNormalInputView.selectedValue.toFormattedCellphoneNumber() ||
                contactInformation.email.toLowerCase() != emailAddressNormalInputView.selectedValue.toLowerCase() ||
                (contactInformation.homeTelephoneCode + contactInformation.homeNumber).toFormattedCellphoneNumber() != homePhoneNormalInputView.selectedValue.toFormattedCellphoneNumber() ||
                contactInformation.preferredContactMethod != manageProfileViewModel.communicationMethodToUpdate
    }

    private fun String.toLowerCase(): String {
        return this.toLowerCase(BMBApplication.getApplicationLocale())
    }

    private fun hasPostalAddressDetailsChanged(): Boolean {
        return !postalAddress.addressLine1.equals(manageProfileViewModel.postalAddressDetailsToUpdate.addressLineOne, true) ||
                !postalAddress.addressLine2.equals(manageProfileViewModel.postalAddressDetailsToUpdate.addressLineTwo, true) ||
                !postalAddress.suburbRsa.equals(manageProfileViewModel.postalAddressDetailsToUpdate.addressSuburb, true) ||
                !postalAddress.town.equals(manageProfileViewModel.postalAddressDetailsToUpdate.addressCity, true) ||
                postalAddress.postalCode != manageProfileViewModel.postalAddressDetailsToUpdate.addressPostalCode
    }
}

private fun nextStep(): (baseFragment: BaseFragment, data: ManageProfileUpdatedAddressDetails) -> Unit {
    return { baseFragment: BaseFragment, manageProfileUpdatedAddressDetails: ManageProfileUpdatedAddressDetails ->
        baseFragment.navigate(ManageProfileGenericAddressFragmentDirections.actionManageProfileGenericAddressFragmentToManageProfileEditContactDetailsFragment(manageProfileUpdatedAddressDetails))
    }
}