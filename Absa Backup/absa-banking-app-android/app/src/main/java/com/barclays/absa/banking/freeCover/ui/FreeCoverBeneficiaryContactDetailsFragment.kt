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

package com.barclays.absa.banking.freeCover.ui

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.InputFilter
import android.text.TextWatcher
import android.view.View
import androidx.lifecycle.Observer
import com.barclays.absa.banking.R
import com.barclays.absa.banking.freeCover.ui.FreeCoverActivity.Companion.FREE_COVER_ANALYTICS_TAG
import com.barclays.absa.banking.policy_beneficiaries.ui.InsuranceBeneficiaryHelper
import com.barclays.absa.banking.presentation.shared.observeWithReset
import com.barclays.absa.banking.shared.services.SharedViewModel
import com.barclays.absa.banking.shared.services.dto.SuburbResult
import com.barclays.absa.utils.AnalyticsUtil
import com.barclays.absa.utils.ValidationUtils
import kotlinx.android.synthetic.main.generic_insurance_beneficiary_contact_details_fragment.*
import styleguide.forms.validation.FieldRequiredValidationRule
import styleguide.forms.validation.ValueRequiredValidationHidingTextWatcher
import styleguide.forms.validation.addValidationRule
import styleguide.utils.extensions.toSentenceCase

class FreeCoverBeneficiaryContactDetailsFragment : FreeCoverBaseFragment(R.layout.generic_insurance_beneficiary_contact_details_fragment) {

    private lateinit var sharedViewModel: SharedViewModel

    override fun onAttach(context: Context) {
        super.onAttach(context)
        sharedViewModel = freeCoverInterface.sharedViewModel()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)
        setToolBar(getString(R.string.free_cover_beneficiary_contact_details).toSentenceCase())
        freeCoverInterface.setStep(3)

        setUpOnClickListener()
        addValidationRules()
        setUpTextWatchers()
        setItemSelectionInterface()
        AnalyticsUtil.trackAction(FREE_COVER_ANALYTICS_TAG, "FreeCover_DetailsScreen_ScreenDisplayed")
    }

    private fun setUpTextWatchers() {
        addressLineOneNormalInputView.addValueViewTextWatcher(ValueRequiredValidationHidingTextWatcher(addressLineOneNormalInputView))
        suburbNormalInputView.addValueViewTextWatcher(ValueRequiredValidationHidingTextWatcher(suburbNormalInputView))
        cityNormalInputView.addValueViewTextWatcher(ValueRequiredValidationHidingTextWatcher(cityNormalInputView))
    }

    private fun setItemSelectionInterface() {
        cityNormalInputView.setItemSelectionInterface {
            if (cityNormalInputView.selectedValue.split(",").size != 3) {
                cityNormalInputView.setError(R.string.free_cover_invalid_city_and_postal_code_error)
            } else {
                cityNormalInputView.clearError()
            }
        }
    }

    private fun areAllFieldsValid(): Boolean {
        if (cityNormalInputView.hasError()) {
            cityNormalInputView.requestFocus()
            cityNormalInputView.focusAndShakeError()
        }
        return addressLineOneNormalInputView.validate() && suburbNormalInputView.validate() && !cityNormalInputView.hasError() && cityNormalInputView.validate()
    }

    private fun addValidationRules() {
        addressLineOneNormalInputView.editText?.filters = arrayOf<InputFilter>(InputFilter.LengthFilter(30))
        addressLineTwoNormalInputView.editText?.filters = arrayOf<InputFilter>(InputFilter.LengthFilter(30))
        emailAddressNumberNormalInputView.editText?.filters = arrayOf<InputFilter>(InputFilter.LengthFilter(30))

        addressLineOneNormalInputView.addValidationRule(FieldRequiredValidationRule(R.string.law_for_you_enter_address_error))
        suburbNormalInputView.addValidationRule(FieldRequiredValidationRule(R.string.suburb_city_error_message))
        cityNormalInputView.addValidationRule(FieldRequiredValidationRule(R.string.suburb_city_error_message))
    }

    private fun optionalValidations(): Boolean {
        when {
            contactNumberNormalInputView.selectedValueUnmasked.isNotBlank() && !ValidationUtils.validatePhoneNumberInput(contactNumberNormalInputView.text) -> contactNumberNormalInputView.setError(R.string.law_for_you_enter_contact_number_error)
            emailAddressNumberNormalInputView.selectedValue.isNotBlank() && !ValidationUtils.isValidEmailAddress(emailAddressNumberNormalInputView.text) -> emailAddressNumberNormalInputView.setError(R.string.law_for_you_enter_email_address_error)
            else -> return true
        }
        return false
    }

    private val observer by lazy {
        Observer<List<SuburbResult>> { suburbResult ->
            dismissProgressDialog()
            suburbResult?.let {
                with(cityNormalInputView) {
                    setList(InsuranceBeneficiaryHelper.buildSuburbOptionsFromList(it), getString(R.string.manage_policy_beneficiaries_city_title))
                    selectedIndex = -1
                    triggerListActivity()
                }
            }
        }
    }

    private fun setUpOnClickListener() {
        with(cityNormalInputView) {
            setCustomOnClickListener {
                val suburb = suburbNormalInputView.selectedValue
                if (suburb.isNotBlank()) {
                    sharedViewModel.suburbsMutableLiveData.observeWithReset(viewLifecycleOwner, observer)
                    sharedViewModel.getSuburbs(suburb)
                } else {
                    cityNormalInputView.setError(R.string.free_cover_please_enter_suburb_first)
                }
            }
            addValueViewTextWatcher(object : TextWatcher {
                override fun afterTextChanged(s: Editable) {
                    suburbNormalInputView.selectedValue = s.split(",")[0].trim()
                }

                override fun beforeTextChanged(s: CharSequence?, p1: Int, p2: Int, p3: Int) {}
                override fun onTextChanged(s: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            })
        }

        continueButton.setOnClickListener {
            AnalyticsUtil.trackAction(FREE_COVER_ANALYTICS_TAG, "FreeCover_DetailsScreen_BeneficiaryButtonClicked")
            if (optionalValidations() && areAllFieldsValid()) {
                with(freeCoverViewModel.applyFreeCoverData) {
                    addressLineOne = addressLineOneNormalInputView.selectedValue.trim()
                    addressLineTwo = addressLineTwoNormalInputView.selectedValue.trim()
                    suburbRsa = suburbNormalInputView.selectedValue.trim()

                    val city: List<String> = cityNormalInputView.selectedValue.split(",")
                    town = city[1].trim()
                    postalCode = city[2].trim()

                    cellphoneNumber = contactNumberNormalInputView.selectedValue.trim()
                    emailAddress = emailAddressNumberNormalInputView.selectedValue.trim()
                }
                navigate(FreeCoverBeneficiaryContactDetailsFragmentDirections.actionFreeCoverBeneficiaryContactDetailsFragmentToFreeCoverConfirmationFragment())
            }
        }
    }
}