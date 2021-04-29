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
package com.barclays.absa.banking.relationshipBanking.ui

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import android.view.View.VISIBLE
import com.barclays.absa.banking.R
import com.barclays.absa.banking.databinding.BusinessAddressFragmentBinding
import com.barclays.absa.banking.newToBank.NewToBankActivity
import com.barclays.absa.banking.newToBank.NewToBankConfirmAddressPresenter
import com.barclays.absa.banking.newToBank.NewToBankConfirmAddressView
import com.barclays.absa.banking.newToBank.NewToBankView
import com.barclays.absa.banking.newToBank.services.dto.AddressDetails
import com.barclays.absa.banking.newToBank.services.dto.PostalCode
import com.barclays.absa.banking.newToBank.services.dto.ResultAnimations
import com.barclays.absa.banking.presentation.shared.ExtendedFragment
import com.barclays.absa.utils.CommonUtils
import styleguide.forms.ItemSelectionInterface
import styleguide.forms.NormalInputView
import styleguide.forms.SelectorList
import styleguide.forms.StringItem
import styleguide.forms.validation.addRequiredValidationHidingTextWatcher
import java.util.*

class BusinessAddressFragment : ExtendedFragment<BusinessAddressFragmentBinding>(), NewToBankConfirmAddressView, ItemSelectionInterface {

    private lateinit var newToBankBusinessView: NewToBankView
    private val presenter = NewToBankConfirmAddressPresenter(this)

    override fun getLayoutResourceId(): Int = R.layout.business_address_fragment

    override fun getToolbarTitle(): String = getString(R.string.relationship_banking_about_your_business)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        newToBankBusinessView = activity as NewToBankActivity
        newToBankBusinessView.trackSoleProprietorCurrentFragment("SoleProprietor_DisplayAboutYourBusinessAddressInputScreen_ScreenDisplayed")
        newToBankBusinessView.setToolbarBackTitle(toolbarTitle)
        newToBankBusinessView.showProgressIndicator()
        initViews()
        setUpComponentListeners()
    }

    private fun initViews() {
        val addressMaxLength = 30

        CommonUtils.setInputFilterForRestrictingSpecialCharacter(binding.businessComplexNormalInputView.editText, addressMaxLength)
        CommonUtils.setInputFilterForRestrictingSpecialCharacter(binding.streetAddressNormalInputView.editText, addressMaxLength)
        CommonUtils.setInputFilterForRestrictingSpecialCharacter(binding.cityNormalInputView.editText, addressMaxLength)
        CommonUtils.setInputFilterForRestrictingSpecialCharacter(binding.suburbNormalInputView.editText, addressMaxLength)
        binding.postalCodeNormalInputView.setItemSelectionInterface(this)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.cancel_menu, menu)
    }

    override fun showPostalCodeList(postalCodes: ArrayList<PostalCode>?) {
        if (postalCodes != null) run {
            val postalCodeItems = SelectorList<StringItem>()
            postalCodes.indices.forEach { postalCodeIndex ->
                var duplicate = false

                for (postalCode in postalCodeItems.indices) {
                    if (postalCodeItems[postalCode].displayValue!!.trim { it <= ' ' } == postalCodes[postalCodeIndex].suburb.trim { it <= ' ' }) {
                        duplicate = true
                        break
                    }
                }
                if (!duplicate) {
                    postalCodeItems.add(StringItem(postalCodes[postalCodeIndex].suburb, postalCodes[postalCodeIndex].streetPostCode))
                }
            }
            binding.postalCodeNormalInputView.setList(postalCodeItems, getString(R.string.new_to_bank_postal_code_toolbar_title))
            binding.postalCodeNormalInputView.triggerListActivity()
        } else {
            binding.suburbNormalInputView.setError(getString(R.string.new_to_bank_address_suburb_incorrect_error))
        }
    }

    private fun getPostalCodeValues() {
        if (validateSuburb() && validateCityTown()) {
            val suburb = binding.suburbNormalInputView.selectedValue
            presenter.performPostalCodeLookup("", suburb)
        }
    }

    private fun validateSuburb(): Boolean {
        return if (binding.suburbNormalInputView.selectedValue.length > 1) {
            binding.suburbNormalInputView.hideError()
            true
        } else {
            binding.suburbNormalInputView.setError(getString(R.string.new_to_bank_please_enter_valid_suburb))
            false
        }
    }

    private fun validateCityTown(): Boolean {
        return if (binding.cityNormalInputView.selectedValue.length > 1) {
            binding.cityNormalInputView.hideError()
            true
        } else {
            binding.cityNormalInputView.setError(getString(R.string.relationship_banking_enter_valid_city))
            false
        }
    }

    override fun validateCustomerSuccess() {
        presenter.performCasaScreening(newToBankBusinessView.newToBankTempData.customerDetails.nationalityCode)
    }

    override fun casaScreeningSuccess() = newToBankBusinessView.fetchProofOfResidenceInfo()

    override fun savePropertyData() {
        val newToBankTempData = newToBankBusinessView.newToBankTempData

        val addressDetails = AddressDetails().apply {
            addressLine1 = binding.businessComplexNormalInputView.selectedValue
            addressLine2 = binding.streetAddressNormalInputView.selectedValue
            postalCode = binding.postalCodeNormalInputView.selectedValue
            suburb = binding.suburbNormalInputView.selectedValue
            town = binding.cityNormalInputView.selectedValue
            addressType = "PHYSICAL_ADDRESS"
        }

        newToBankTempData.addressDetails = addressDetails
        presenter.performValidateAddress(addressDetails)
    }

    override fun navigateToFailureScreen(errorMessage: String?, retainState: Boolean) {
        newToBankBusinessView.navigateToGenericResultFragment(retainState, false, errorMessage, ResultAnimations.generalFailure)
    }

    override fun trackCurrentFragment(fragmentInfo: String?) = newToBankBusinessView.trackCurrentFragment(fragmentInfo)

    private fun isInvalidField(normalInputView: NormalInputView<*>, errorToDisplay: String): Boolean {
        return if (normalInputView.visibility == VISIBLE && normalInputView.selectedValueUnmasked.isEmpty()) {
            normalInputView.setError(errorToDisplay)
            scrollToTopOfView(normalInputView)
            true
        } else {
            normalInputView.clearError()
            false
        }
    }

    private fun scrollToTopOfView(view: View) = binding.scrollView.post { binding.scrollView.smoothScrollTo(0, view.y.toInt()) }

    override fun onItemClicked(index: Int) {
        val suburb = binding.postalCodeNormalInputView.selectedItem?.displayValue
        val postalCode = binding.postalCodeNormalInputView.selectedItem?.displayValueLine2
        binding.suburbNormalInputView.selectedValue = suburb.toString()
        binding.postalCodeNormalInputView.selectedValue = postalCode.toString()
    }

    private fun isValidInput(): Boolean {
        return when {
            isInvalidField(binding.businessComplexNormalInputView, getString(R.string.relationship_banking_enter_valid_complex)) -> false
            isInvalidField(binding.streetAddressNormalInputView, getString(R.string.relationship_banking_enter_valid_address)) -> false
            isInvalidField(binding.suburbNormalInputView, getString(R.string.new_to_bank_please_enter_valid_suburb)) -> false
            isInvalidField(binding.cityNormalInputView, getString(R.string.relationship_banking_enter_valid_city)) -> false
            isInvalidField(binding.postalCodeNormalInputView, getString(R.string.new_to_bank_select_postal_code)) -> false
            else -> true
        }
    }

    private fun setUpComponentListeners() {
        binding.uploadDocumentsButton.setOnClickListener {
            newToBankBusinessView.trackSoleProprietorCurrentFragment("SoleProprietor_AboutYourBusinessAddressInputScreen_UploadDocumentsButtonClicked")
            if (isValidInput()) {
                savePropertyData()
            }
        }

        binding.apply {
            businessComplexNormalInputView.addRequiredValidationHidingTextWatcher()
            streetAddressNormalInputView.addRequiredValidationHidingTextWatcher()
            cityNormalInputView.addRequiredValidationHidingTextWatcher()
            postalCodeNormalInputView.addRequiredValidationHidingTextWatcher()
            suburbNormalInputView.addValueViewTextWatcher(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {

                }

                override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                    if (s.isNotEmpty()) {
                        suburbNormalInputView.showError(false)
                    }
                }

                override fun afterTextChanged(s: Editable) {
                    postalCodeNormalInputView.selectedIndex = -1
                    postalCodeNormalInputView.selectedValue = ""
                }
            })
            postalCodeNormalInputView.setOnClickListener { getPostalCodeValues() }
        }
    }
}
