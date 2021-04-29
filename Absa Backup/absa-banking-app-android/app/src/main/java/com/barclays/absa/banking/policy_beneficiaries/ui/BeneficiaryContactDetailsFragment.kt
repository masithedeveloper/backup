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

package com.barclays.absa.banking.policy_beneficiaries.ui

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.lifecycle.Observer
import com.barclays.absa.banking.R
import com.barclays.absa.banking.framework.app.BMBApplication
import com.barclays.absa.banking.framework.app.BMBConstants
import com.barclays.absa.banking.framework.utils.ContactDialogOptionListener
import com.barclays.absa.banking.funeralCover.ui.InsurancePolicyClaimsBaseActivity
import com.barclays.absa.banking.presentation.shared.observeWithReset
import com.barclays.absa.banking.shared.services.SharedViewModel
import com.barclays.absa.banking.shared.services.dto.SuburbResult
import com.barclays.absa.utils.AnalyticsUtil
import com.barclays.absa.utils.CommonUtils
import com.barclays.absa.utils.PermissionHelper.requestContactsReadPermission
import com.barclays.absa.utils.ValidationUtils
import com.barclays.absa.utils.viewModel
import kotlinx.android.synthetic.main.generic_insurance_beneficiary_contact_details_fragment.*
import styleguide.forms.ItemSelectionInterface
import styleguide.forms.validation.addRequiredValidationHidingTextWatcher
import styleguide.forms.validation.*
import styleguide.utils.extensions.toTitleCase

class BeneficiaryContactDetailsFragment : ManageBeneficiaryBaseFragment(R.layout.generic_insurance_beneficiary_contact_details_fragment) {

    private lateinit var sharedViewModel: SharedViewModel
    private val observer by lazy {
        Observer<List<SuburbResult>> { suburbResult ->
            dismissProgressDialog()
            suburbResult?.let {
                cityNormalInputView.setList(InsuranceBeneficiaryHelper.buildSuburbOptionsFromList(it), getString(R.string.manage_policy_beneficiaries_city_title))
                cityNormalInputView.selectedIndex = -1
                cityNormalInputView.triggerListActivity()
            }
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        sharedViewModel = manageBeneficiaryActivity.viewModel()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setToolBar(getString(R.string.manage_policy_beneficiaries_contact_details))
        manageBeneficiaryActivity.setStep(2)
        AnalyticsUtil.trackAction("Insurance_Hub", "${InsurancePolicyClaimsBaseActivity.policyType}_BeneficiaryContactDetailsScreen_ScreenDisplayed")

        val customerInformation = manageBeneficiaryViewModel.customerInformation
        when (manageBeneficiaryViewModel.beneficiaryAction) {
            BeneficiaryAction.ADD -> {
                val residentialAddress = customerInformation.residentialAddress

                residentialAddress?.addressLine1?.toTitleCase()?.let {
                    addressLineOneNormalInputView.selectedValue = it
                    manageBeneficiaryViewModel.policyBeneficiaryInfo.addressLine1 = it
                }

                residentialAddress?.addressLine2?.toTitleCase()?.let {
                    addressLineTwoNormalInputView.selectedValue = it
                    manageBeneficiaryViewModel.policyBeneficiaryInfo.addressLine2 = it
                }

                residentialAddress?.suburbRsa?.toTitleCase()?.let {
                    suburbNormalInputView.selectedValue = it
                    manageBeneficiaryViewModel.policyBeneficiaryInfo.suburb = it
                }

                val town = residentialAddress?.town.toTitleCase()
                val postalCode = residentialAddress?.postalCode
                cityNormalInputView.selectedValue = "$town,$postalCode"
                manageBeneficiaryViewModel.policyBeneficiaryInfo.town = town
                postalCode?.let {
                    manageBeneficiaryViewModel.policyBeneficiaryInfo.postalCode = it
                }
            }
            BeneficiaryAction.EDIT -> {
                val beneficiary = manageBeneficiaryViewModel.policyBeneficiaryInfo
                addressLineOneNormalInputView.selectedValue = beneficiary.addressLine1.toTitleCase()
                addressLineTwoNormalInputView.selectedValue = beneficiary.addressLine2.toTitleCase()
                suburbNormalInputView.selectedValue = beneficiary.suburb.toTitleCase()
                cityNormalInputView.selectedValue = "${beneficiary.town.toTitleCase()}, ${beneficiary.postalCode}"
                emailAddressNumberNormalInputView.selectedValue = beneficiary.emailAddress.toLowerCase(BMBApplication.getApplicationLocale())
                contactNumberNormalInputView.selectedValue = beneficiary.cellphoneNumber
            }
            else -> {
            }
        }
        attachEventHandlers()
        setValidationRules()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.clear_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.clear_menu_item -> {
                clearFields()
                true
            }
            else -> {
                super.onOptionsItemSelected(item)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == BMBConstants.REQUEST_CODE_MY_REFERENCE_DETAILS && resultCode == Activity.RESULT_OK) {
            requestContactsReadPermission(manageBeneficiaryActivity) {
                val contact = CommonUtils.getContact(manageBeneficiaryActivity, data?.data)
                CommonUtils.updateMobileNumberOnSelection(manageBeneficiaryActivity, contactNumberNormalInputView.editText, contact)
            }
        }
    }

    private fun clearFields() {
        addressLineOneNormalInputView.clear()
        addressLineTwoNormalInputView.clear()
        suburbNormalInputView.clear()
        cityNormalInputView.clear()
        emailAddressNumberNormalInputView.clear()
        contactNumberNormalInputView.clear()
    }

    private fun attachEventHandlers() {
        addressLineOneNormalInputView.addRequiredValidationHidingTextWatcher {
            manageBeneficiaryViewModel.policyBeneficiaryInfo.addressLine1 = it
        }

        addressLineTwoNormalInputView.addRequiredValidationHidingTextWatcher {
            manageBeneficiaryViewModel.policyBeneficiaryInfo.addressLine2 = it
        }

        suburbNormalInputView.addRequiredValidationHidingTextWatcher {
            manageBeneficiaryViewModel.policyBeneficiaryInfo.suburb = it
            cityNormalInputView.clear()
        }

        cityNormalInputView.setCustomOnClickListener {
            val suburb = suburbNormalInputView.selectedValue
            if (suburb.isNotEmpty()) {
                sharedViewModel.suburbsMutableLiveData.observeWithReset(this@BeneficiaryContactDetailsFragment, observer)
                sharedViewModel.getSuburbs(suburb)
            }
        }

        cityNormalInputView.setItemSelectionInterface {
            val item = cityNormalInputView.selectedItem as InsuranceBeneficiaryHelper.SuburbItem
            suburbNormalInputView.selectedValue = item.suburbResult.suburb.toTitleCase()
            cityNormalInputView.selectedValue = item.suburbResult.townOrCity.toTitleCase() + ", " + item.suburbResult.postalCode

            item.suburbResult.suburb.let {
                manageBeneficiaryViewModel.policyBeneficiaryInfo.suburb = it.toTitleCase()
            }
            item.suburbResult.townOrCity.let {
                manageBeneficiaryViewModel.policyBeneficiaryInfo.town = it.toTitleCase()
            }

            item.suburbResult.postalCode.let {
                manageBeneficiaryViewModel.policyBeneficiaryInfo.postalCode = it
            }
        }

        contactNumberNormalInputView.setImageViewOnTouchListener(ContactDialogOptionListener(contactNumberNormalInputView.editText, R.string.selFrmPhoneBookMsg, manageBeneficiaryActivity, BMBConstants.REQUEST_CODE_MY_REFERENCE_DETAILS, this@BeneficiaryContactDetailsFragment))

        continueButton.setOnClickListener {
            if (isAllFieldsValid()) {
                with(manageBeneficiaryViewModel.policyBeneficiaryInfo) {
                    cellphoneNumber = contactNumberNormalInputView.selectedValue
                    emailAddress = emailAddressNumberNormalInputView.selectedValue
                    addressLine2 = addressLineTwoNormalInputView.selectedValue
                }
                navigate(BeneficiaryContactDetailsFragmentDirections.beneficiaryContactDetailsFragmentToBeneficiaryAllocationFragment())
            }
        }
    }

    private fun setValidationRules(){
        addressLineOneNormalInputView.addValidationRule(FieldRequiredValidationRule(R.string.manage_policy_beneficiaries_address_error))
        suburbNormalInputView.addValidationRule(FieldRequiredValidationRule(R.string.manage_policy_beneficiaries_suburb_error))
        cityNormalInputView.addValidationRule(FieldRequiredValidationRule(R.string.manage_policy_beneficiaries_postal_code_error))
        contactNumberNormalInputView.addValidationRule(CellphoneAndLandlineNumberValidationRule(R.string.manage_policy_beneficiaries_contact_number_error))
        emailAddressNumberNormalInputView.addValidationRule(EmailValidationRule(R.string.manage_policy_beneficiaries_email_address_error))
    }

    private fun isAllFieldsValid(): Boolean {
        return addressLineOneNormalInputView.validate() && suburbNormalInputView.validate() && cityNormalInputView.validate() &&
                contactNumberNormalInputView.optionalValidate() && emailAddressNumberNormalInputView.optionalValidate()
    }
}