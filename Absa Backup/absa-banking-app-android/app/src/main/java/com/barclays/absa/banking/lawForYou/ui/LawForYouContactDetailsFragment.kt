/*
 * Copyright (c) 2020 Absa Bank Limited, All Rights Reserved.
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
package com.barclays.absa.banking.lawForYou.ui

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.MutableLiveData
import com.barclays.absa.banking.R
import com.barclays.absa.banking.riskBasedApproach.services.dto.PersonalInformationResponse
import com.barclays.absa.banking.shared.services.dto.SuburbResponse
import com.barclays.absa.utils.AnalyticsUtil
import com.barclays.absa.utils.extensions.toSelectorList
import kotlinx.android.synthetic.main.law_for_you_contact_details_fragment.*
import styleguide.forms.Form
import styleguide.forms.NormalInputView
import styleguide.forms.StringItem
import styleguide.forms.validation.*
import styleguide.utils.extensions.toFormattedCellphoneNumber
import styleguide.utils.extensions.toTenDigitPhoneNumber
import styleguide.utils.extensions.toTitleCase

class LawForYouContactDetailsFragment : LawForYouBaseFragment(R.layout.law_for_you_contact_details_fragment) {

    private var blockSuburbTextChangeListener = false

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)
        setToolBar(R.string.contact_details)
        lawForYouActivity.setProgressStep(2)

        setupObservers()
        setListeners()
        setValidationRules()

        lawForYouViewModel.fetchPersonalInformation()
        lawForYouViewModel.lawForYouDetails.dayOfDebit = "1"
        AnalyticsUtil.trackAction("Law For You", "LawForYou_ContactDetails_ScreenDisplayed")
    }

    override fun onDestroyView() {
        lawForYouViewModel.personalInformationResponseMutableLiveData.removeObservers(viewLifecycleOwner)
        lawForYouViewModel.cityAndPostalCodeMutableLiveData = MutableLiveData()
        lawForYouViewModel.cityAndPostalCodeMutableLiveData.removeObservers(viewLifecycleOwner)
        super.onDestroyView()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.clear_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.clear_menu_item) {
            clearAllInputs()
            AnalyticsUtil.trackAction("Law For You", "LawForYou_ContactDetails_ClearTapped")
        }
        return super.onOptionsItemSelected(item)
    }

    private fun setupObservers() {
        lawForYouViewModel.cityAndPostalCodeMutableLiveData.observe(viewLifecycleOwner, { updateCityAndPostalCodeList(it) })
        lawForYouViewModel.personalInformationResponseMutableLiveData.observe(viewLifecycleOwner, { handlePersonInformationResponse(it) })
    }

    private fun setListeners() {
        addressLine1NormalInputView.addRequiredValidationHidingTextWatcher()
        suburbNormalInputView.addRequiredValidationHidingTextWatcher()
        suburbNormalInputView.editText?.apply {
            setOnFocusChangeListener { _, hasFocus ->
                if (hasFocus) {
                    blockSuburbTextChangeListener = false
                }
            }
            addTextChangedListener {
                if (!blockSuburbTextChangeListener) {
                    cityAndPostalCodeNormalInputView.clear()
                }
            }
        }
        cityAndPostalCodeNormalInputView.apply {
            addRequiredValidationHidingTextWatcher()
            setCustomOnClickListener {
                if (suburbNormalInputView.selectedValue.isNotEmpty()) {
                    lawForYouViewModel.fetchCityAndPostalCodes(suburbNormalInputView.selectedValue)
                    cityAndPostalCodeNormalInputView.clear()
                } else {
                    suburbNormalInputView.setError(R.string.law_for_you_enter_suburb_error)
                }
            }
        }
        contactNumberNormalInputView.addRequiredValidationHidingTextWatcher()
        emailAddressNumberNormalInputView.addRequiredValidationHidingTextWatcher()
        continueButton.setOnClickListener { validateInputsAndNavigate() }
    }

    private fun setValidationRules() {
        addressLine1NormalInputView.addValidationRule(FieldRequiredValidationRule(R.string.law_for_you_enter_address_error))
        suburbNormalInputView.addValidationRule(FieldRequiredValidationRule(R.string.law_for_you_enter_suburb_error))
        cityAndPostalCodeNormalInputView.addValidationRule(FieldRequiredValidationRule(R.string.law_for_you_select_city_error))
        contactNumberNormalInputView.addValidationRule(CellphoneNumberValidationRule(R.string.law_for_you_enter_contact_number_error))
        emailAddressNumberNormalInputView.addValidationRule(EmailValidationRule(R.string.law_for_you_enter_email_address_error))
    }

    private fun clearAllInputs() = listOf<NormalInputView<*>>(addressLine1NormalInputView,
            addressLine2NormalInputView, suburbNormalInputView, cityAndPostalCodeNormalInputView,
            contactNumberNormalInputView, emailAddressNumberNormalInputView).forEach { it.clear() }

    private fun handlePersonInformationResponse(personalInformationResponse: PersonalInformationResponse) {
        val customerInformation = personalInformationResponse.customerInformation ?: PersonalInformationResponse.CustomerInformation()
        val residentialAddress = customerInformation.residentialAddress ?: PersonalInformationResponse.PostalAddress()
        val town = (residentialAddress.town).toTitleCase()
        val postalCode = residentialAddress.postalCode
        val cityAndPostalCode = "$town, $postalCode"

        if (lawForYouViewModel.lawForYouDetails.addressLine1.isEmpty()) {
            addressLine1NormalInputView.selectedValue = residentialAddress.addressLine1.toTitleCase()
        }
        if (lawForYouViewModel.lawForYouDetails.addressLine2.isEmpty()) {
            addressLine2NormalInputView.selectedValue = residentialAddress.addressLine2.toTitleCase()
        }
        if (lawForYouViewModel.lawForYouDetails.suburb.isEmpty()) {
            suburbNormalInputView.selectedValue = residentialAddress.suburbRsa.toTitleCase()
        }
        if (lawForYouViewModel.lawForYouDetails.city.isEmpty()) {
            cityAndPostalCodeNormalInputView.selectedValue = cityAndPostalCode
        }
        if (lawForYouViewModel.lawForYouDetails.cellNumber.isEmpty()) {
            contactNumberNormalInputView.selectedValue = customerInformation.cellNumber.toFormattedCellphoneNumber()
        }
        if (lawForYouViewModel.lawForYouDetails.emailAddress.isEmpty()) {
            emailAddressNumberNormalInputView.selectedValue = customerInformation.email.toString()
        }

        dismissProgressDialog()
    }

    private fun updateCityAndPostalCodeList(suburbResponse: SuburbResponse) {
        dismissProgressDialog()
        val selectorList = suburbResponse.suburbs.toSelectorList { suburb -> StringItem("${suburb.suburb}, ${suburb.townOrCity}, ${suburb.postalCode}") }
        cityAndPostalCodeNormalInputView.apply {
            setList(selectorList, getString(R.string.select_city_and_postal_code))
            setItemSelectionInterface {
                cityAndPostalCodeNormalInputView.selectedValue = selectorList[it].item?.substringAfter(", ") ?: ""
                blockSuburbTextChangeListener = true
                suburbNormalInputView.selectedValue = selectorList[it].item?.substringBefore(", ") ?: ""
                suburbNormalInputView.clearFocus()
            }
            selectedIndex = -1
            triggerListActivity()
        }
    }

    private fun validateInputsAndNavigate() {
        if (!Form(formContainer).isValid()) {
            return
        }
        lawForYouViewModel.lawForYouDetails.apply {
            cellNumber = contactNumberNormalInputView.selectedValue.toTenDigitPhoneNumber()
            emailAddress = emailAddressNumberNormalInputView.selectedValue.trim()
            addressLine1 = addressLine1NormalInputView.selectedValue.trim()
            addressLine2 = addressLine2NormalInputView.selectedValue.trim()
            suburb = suburbNormalInputView.selectedValue.trim()
            city = cityAndPostalCodeNormalInputView.selectedValue.substringBefore(",").trim()
            postalCode = cityAndPostalCodeNormalInputView.selectedValue.substringAfter(",").trim()
            country = lawForYouViewModel.personalInformationResponseMutableLiveData.value?.customerInformation?.postalAddress?.country.toString()
        }

        navigate(LawForYouContactDetailsFragmentDirections.actionLawForYouContactDetailsFragmentToLawForYouPolicyDetailsFragment())
    }
}