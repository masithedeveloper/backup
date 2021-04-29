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

import android.app.DatePickerDialog
import android.content.Context
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import com.barclays.absa.banking.R
import com.barclays.absa.banking.funeralCover.ui.InsurancePolicyClaimsBaseActivity
import com.barclays.absa.banking.shared.AlertDialogProperties
import com.barclays.absa.banking.shared.BaseAlertDialog.showAlertDialog
import com.barclays.absa.banking.shared.services.SharedViewModel
import com.barclays.absa.banking.shared.services.dto.LookupItem
import com.barclays.absa.utils.AnalyticsUtil.trackAction
import com.barclays.absa.utils.DateUtils
import com.barclays.absa.utils.ValidationUtils
import com.barclays.absa.utils.viewModel
import kotlinx.android.synthetic.main.manage_beneficiaries_details_fragment.*
import styleguide.forms.SelectorList
import styleguide.forms.StringItem
import styleguide.forms.validation.addRequiredValidationHidingTextWatcher
import styleguide.utils.extensions.toTitleCase
import java.util.*

class BeneficiaryDetailsFragment : ManageBeneficiaryBaseFragment(R.layout.manage_beneficiaries_details_fragment) {

    private lateinit var sharedViewModel: SharedViewModel
    lateinit var titleSelectorItems: SelectorList<StringItem>

    override fun onAttach(context: Context) {
        super.onAttach(context)
        sharedViewModel = manageBeneficiaryActivity.viewModel()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (manageBeneficiaryViewModel.beneficiaryAction == BeneficiaryAction.EDIT && manageBeneficiaryViewModel.policyBeneficiaries.size != 1) {
            setHasOptionsMenu(true)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.remove_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_remove -> {
                showDeleteConfirmation()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun showDeleteConfirmation() {
        val policyBeneficiaryInfo = manageBeneficiaryViewModel.policyBeneficiaryInfo
        showAlertDialog(AlertDialogProperties.Builder()
                .title(getString(R.string.manage_policy_beneficiaries_remove_beneficiary))
                .message(getString(R.string.delete_warning, "${policyBeneficiaryInfo.title.first.toTitleCase()} ${policyBeneficiaryInfo.firstName} ${policyBeneficiaryInfo.surname}"))
                .positiveButton(getString(R.string.yes))
                .positiveDismissListener { _, _ ->
                    manageBeneficiaryViewModel.beneficiaryAction = BeneficiaryAction.REMOVE
                    if (manageBeneficiaryViewModel.policyBeneficiaries.size == 1) {
                        navigate(BeneficiaryDetailsFragmentDirections.actionBeneficiaryDetailsFragmentToBeneficiaryConfirmationFragment())
                    } else {
                        navigate(BeneficiaryDetailsFragmentDirections.beneficiaryDetailsFragmentToBeneficiaryAllocationFragment())
                    }
                }
                .negativeButton(getString(R.string.cancel))
                .build())
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setToolBar(getString(R.string.manage_policy_beneficiaries_beneficiary_details))
        manageBeneficiaryActivity.setStep(1)
        manageBeneficiaryActivity.showProgressIndicator()
        initViews()
        attachEventHandlers()
        showSourceOfFundsFieldIfEmpty()
        trackAction("Insurance_Hub", "${InsurancePolicyClaimsBaseActivity.policyType}_BeneficiaryDetailsScreen_ScreenDisplayed")
    }

    private fun initViews() {
        val policyBeneficiary = manageBeneficiaryViewModel.policyBeneficiaryInfo
        initialiseTitleSelector()
        val title = policyBeneficiary.title.first
        if (title.isNotEmpty()) {
            titleNormalInputView.selectedValue = title.toTitleCase()
            titleNormalInputView.selectedIndex = titleSelectorItems.indexOf(titleSelectorItems.find { it.item.equals(title, true) })
        }

        firstNameNormalInputView.selectedValue = policyBeneficiary.firstName
        surnameNormalInputView.selectedValue = policyBeneficiary.surname

        initialiseCategorySelector()
        categoryNormalInputView.selectedValue = policyBeneficiary.category

        val relationship = policyBeneficiary.relationship?.first ?: ""
        if (relationship.isNotEmpty()) {
            relationshipNormalInputView.visibility = View.VISIBLE
            relationshipNormalInputView.selectedValue = relationship
        }

        val dateOfBirth = policyBeneficiary.dateOfBirth
        if (dateOfBirth.isNotEmpty()) {
            dateOfBirthNormalInputView.selectedValue = DateUtils.formatDate(dateOfBirth, DateUtils.DASHED_DATE_PATTERN, DateUtils.DATE_DISPLAY_PATTERN)
        }
        dateOfBirthNormalInputView.setDescription(getString(R.string.date_of_birth_description))

        identificationRadioButtonView.setDataSource(InsuranceBeneficiaryHelper.buildSelectorOptionsFromArray(resources.getStringArray(R.array.idTypes)))
        val idTypeDescription = policyBeneficiary.idType?.second ?: ""
        if (idTypeDescription.isNotEmpty()) {
            val selectedIndex = InsuranceBeneficiaryHelper.findIdTypeIndex(idTypeDescription)
            identificationRadioButtonView.selectedIndex = selectedIndex
            changeIdNumberFieldTitle(selectedIndex)
        }

        val idNumber = policyBeneficiary.idNumber
        if (idNumber.isNotEmpty() || identificationRadioButtonView.selectedIndex != -1) {
            identificationNormalInputView.visibility = View.VISIBLE
            identificationNormalInputView.selectedValue = idNumber
        }

        if (manageBeneficiaryViewModel.beneficiaryAction == BeneficiaryAction.EDIT) {
            if (relationship.isNotEmpty()) {
                policyBeneficiary.category = InsuranceBeneficiaryHelper.findCategory(manageBeneficiaryActivity, relationship)
                val categoryArray = resources.getStringArray(R.array.beneficiaryCategory)
                val relationshipSelectorList = InsuranceBeneficiaryHelper.buildSelectorOptionsFromCategory(requireContext(), policyBeneficiary.category)
                categoryNormalInputView.selectedValue = policyBeneficiary.category
                categoryNormalInputView.selectedIndex = categoryArray.indexOf(categoryArray.find { it.equals(policyBeneficiary.category, true) })
                relationshipNormalInputView.selectedIndex = relationshipSelectorList.indexOf(relationshipSelectorList.find { it.item == policyBeneficiary.relationship?.first })
            }
        }
        bindDataToRelationshipField(policyBeneficiary.category)
    }


    private fun showSourceOfFundsFieldIfEmpty() {
        if (manageBeneficiaryViewModel.sourceOfFunds.isNullOrEmpty()) {
            val items = manageBeneficiaryViewModel.sourceOfFundList
            sourceOfFundsNormalInputView.visibility = View.VISIBLE
            sourceOfFundsNormalInputView.setList(InsuranceBeneficiaryHelper.buildSelectorOptionsFromList(items), getString(R.string.source_of_funds_title))
            sourceOfFundsNormalInputView.selectedIndex = InsuranceBeneficiaryHelper.getMatchingLookupIndex("20", items)
        }
    }

    private fun initialiseCategorySelector() {
        val categorySelectorItems = if (manageBeneficiaryViewModel.isExergyPolicy) {
            InsuranceBeneficiaryHelper.buildSelectorOptionsFromArray(resources.getStringArray(R.array.flexiFuneralFamilyMemberCategory))
        } else {
            InsuranceBeneficiaryHelper.buildSelectorOptionsFromArray(resources.getStringArray(R.array.beneficiaryCategory))
        }
        categoryNormalInputView.setList(categorySelectorItems, getString(R.string.member_category))
    }

    private fun initialiseTitleSelector() {
        val titleMutableList = mutableListOf<String>()

        if (manageBeneficiaryViewModel.isExergyPolicy) {
            manageBeneficiaryViewModel.exergyTitles.forEach { exergyCodeDetails ->
                titleMutableList.add(exergyCodeDetails.description.toTitleCase())
            }
        } else {
            manageBeneficiaryViewModel.titles.forEach { lookupItem ->
                titleMutableList.add(lookupItem.defaultLabel.toTitleCase())
            }
        }
        titleSelectorItems = InsuranceBeneficiaryHelper.buildSelectorOptionsFromArray(titleMutableList.toTypedArray())
        titleNormalInputView.setList(titleSelectorItems, getString(R.string.ultimate_protector_beneficiary_title))
    }

    private fun attachEventHandlers() {
        titleNormalInputView.setItemSelectionInterface {
            val selectedTitle = titleNormalInputView.selectedValue
            val titleCode = if (manageBeneficiaryViewModel.isExergyPolicy) {
                InsuranceBeneficiaryHelper.getMatchingExergyCodesDetails(selectedTitle, manageBeneficiaryViewModel.exergyTitles).code
            } else {
                InsuranceBeneficiaryHelper.getMatchingLookupItem(selectedTitle, manageBeneficiaryViewModel.titles)?.itemCode.toString()
            }

            manageBeneficiaryViewModel.policyBeneficiaryInfo.title = Pair(selectedTitle, titleCode)
        }

        firstNameNormalInputView.addRequiredValidationHidingTextWatcher {
            manageBeneficiaryViewModel.policyBeneficiaryInfo.firstName = it.trimStart(' ')
            manageBeneficiaryViewModel.policyBeneficiaryInfo.initials = manageBeneficiaryViewModel.policyBeneficiaryInfo.firstName.first().toString()
        }

        surnameNormalInputView.addRequiredValidationHidingTextWatcher {
            manageBeneficiaryViewModel.policyBeneficiaryInfo.surname = it
        }

        categoryNormalInputView.setItemSelectionInterface {
            val selectedCategory = categoryNormalInputView.selectedValue
            manageBeneficiaryViewModel.policyBeneficiaryInfo.category = selectedCategory
            showRelationshipField()
            relationshipNormalInputView.clear()
            relationshipNormalInputView.selectedIndex = -1
            bindDataToRelationshipField(selectedCategory)
        }

        relationshipNormalInputView.setItemSelectionInterface {
            val selectedRelationship = relationshipNormalInputView.selectedValue
            setRelationship(selectedRelationship)
        }

        dateOfBirthNormalInputView.setOnClickListener {
            showDayPicker()
        }

        dateOfBirthNormalInputView.addRequiredValidationHidingTextWatcher {
            manageBeneficiaryViewModel.policyBeneficiaryInfo.dateOfBirth = DateUtils.formatDate(it, DateUtils.DATE_DISPLAY_PATTERN, DateUtils.DASHED_DATE_PATTERN)
        }

        identificationRadioButtonView.setItemCheckedInterface { index ->
            identificationNormalInputView.requestFocus()
            showIdNumberField(index)
            changeIdNumberFieldTitle(index)
            val selectedIdType = identificationRadioButtonView.selectedValue?.displayValue ?: ""
            InsuranceBeneficiaryHelper.idTypesMap[selectedIdType]?.let { code ->
                manageBeneficiaryViewModel.policyBeneficiaryInfo.idType = Pair(selectedIdType, code)
            }
        }

        identificationNormalInputView.addRequiredValidationHidingTextWatcher()

        sourceOfFundsNormalInputView.setItemSelectionInterface {
            val selectedFund = sourceOfFundsNormalInputView.selectedItem as LookupItem
            manageBeneficiaryViewModel.policyInfo.sourceOfFunds = selectedFund
        }

        continueButton.setOnClickListener {
            if (isAllFieldsValid()) {
                manageBeneficiaryViewModel.policyBeneficiaryInfo.initials = manageBeneficiaryViewModel.policyBeneficiaryInfo.firstName.first().toString()
                if (manageBeneficiaryViewModel.beneficiaryAction == BeneficiaryAction.REMOVE) {
                    manageBeneficiaryViewModel.beneficiaryAction = BeneficiaryAction.EDIT
                }
                navigate(BeneficiaryDetailsFragmentDirections.beneficiaryDetailsFragmentToBeneficiaryContactDetailsFragment())
            }
        }
    }

    private fun isAllFieldsValid(): Boolean {
        if (titleNormalInputView.selectedValue.isEmpty()) {
            titleNormalInputView.setError(getString(R.string.manage_policy_beneficiaries_title_error))
            return false
        }

        if (firstNameNormalInputView.selectedValue.isEmpty()) {
            firstNameNormalInputView.setError(getString(R.string.manage_policy_beneficiaries_first_name_error))
            return false
        }

        if (surnameNormalInputView.selectedValue.isEmpty()) {
            surnameNormalInputView.setError(getString(R.string.manage_policy_beneficiaries_surname_error))
            return false
        }

        if (categoryNormalInputView.selectedValue.isEmpty()) {
            categoryNormalInputView.setError(getString(R.string.manage_policy_beneficiaries_category_error))
            return false
        }

        if (relationshipNormalInputView.selectedValue.isEmpty()) {
            relationshipNormalInputView.setError(getString(R.string.manage_policy_beneficiaries_relationship_error))
            return false
        }

        if (dateOfBirthNormalInputView.selectedValue.isEmpty()) {
            dateOfBirthNormalInputView.setError(getString(R.string.manage_policy_beneficiaries_date_of_birth_error))
            return false
        }

        val selectedIndex = identificationRadioButtonView.selectedIndex

        when {
            selectedIndex == 0 && identificationNormalInputView.selectedValue.isNotEmpty() && !ValidationUtils.isValidSouthAfricanIdNumber(identificationNormalInputView.selectedValue) -> {
                identificationNormalInputView.setError(getString(R.string.manage_policy_beneficiaries_id_number_error))
                return false
            }
            (selectedIndex == 0 || selectedIndex == 1) && identificationNormalInputView.selectedValue.isEmpty() -> {
                identificationNormalInputView.setError(getString(R.string.manage_policy_beneficiaries_id_number_error))
                return false
            }
            else -> manageBeneficiaryViewModel.policyBeneficiaryInfo.idNumber = identificationNormalInputView.selectedValue
        }
        return true
    }

    private fun showDayPicker() {
        val minDateTimeInMillis = Calendar.getInstance().apply {
            add(Calendar.YEAR, -75)
            add(Calendar.DAY_OF_YEAR, 1)
        }.timeInMillis

        val maxDateTimeInMillis = Calendar.getInstance().apply {
            add(Calendar.YEAR, -18)
        }.timeInMillis

        val currentDate = Calendar.getInstance()

        DatePickerDialog(manageBeneficiaryActivity, R.style.DatePickerDialogTheme, { _, year, month, day ->
            val calendarTime = Calendar.getInstance().apply { set(year, month, day) }.time
            dateOfBirthNormalInputView.selectedValue = DateUtils.format(calendarTime, DateUtils.DATE_DISPLAY_PATTERN)
            manageBeneficiaryViewModel.policyBeneficiaryInfo.dateOfBirth = DateUtils.format(calendarTime, DateUtils.DASHED_DATE_PATTERN)
        }, currentDate.get(Calendar.YEAR), currentDate.get(Calendar.MONTH), currentDate.get(Calendar.DAY_OF_MONTH)).apply {
            datePicker.minDate = minDateTimeInMillis
            datePicker.maxDate = maxDateTimeInMillis
            setButton(DatePickerDialog.BUTTON_POSITIVE, getString(R.string.ok_call_me), this)
            setButton(DatePickerDialog.BUTTON_NEGATIVE, getString(R.string.cancel), this)
        }.show()
    }

    private fun showRelationshipField() {
        if (relationshipNormalInputView.visibility == View.GONE) {
            relationshipNormalInputView.visibility = View.VISIBLE
        }
    }

    private fun bindDataToRelationshipField(selectedCategory: String) {
        when {
            selectedCategory == getString(R.string.spouse) -> {
                with(relationshipNormalInputView) {
                    selectedIndex = 0
                    selectedValue = getString(R.string.spouse)
                    setList(InsuranceBeneficiaryHelper.buildSelectorOptionsFromCategory(manageBeneficiaryActivity, selectedCategory), getString(R.string.relationship))
                }
                setRelationship(getString(R.string.spouse))
            }
            manageBeneficiaryViewModel.isExergyPolicy -> {
                val relationshipArray = if (selectedCategory == getString(R.string.flexi_funeral_children)) R.array.flexiFuneralRelationshipChildren else R.array.flexiFuneralRelationshipExtendedFamily
                relationshipNormalInputView.setList(InsuranceBeneficiaryHelper.buildSelectorOptionsFromArray(resources.getStringArray(relationshipArray)), selectedCategory)
            }
            else -> relationshipNormalInputView.setList(InsuranceBeneficiaryHelper.buildSelectorOptionsFromCategory(manageBeneficiaryActivity, selectedCategory), getString(R.string.relationship))
        }
    }

    private fun setRelationship(selectedRelationship: String) {
        if (manageBeneficiaryViewModel.isExergyPolicy) {
            manageBeneficiaryViewModel.policyBeneficiaryInfo.relationship = Pair(selectedRelationship, InsuranceBeneficiaryHelper.getMatchingExergyCodesDetails(selectedRelationship, manageBeneficiaryViewModel.exergyRelationships).code)
        } else {
            InsuranceBeneficiaryHelper.relationshipMap[selectedRelationship]?.let { code ->
                manageBeneficiaryViewModel.policyBeneficiaryInfo.relationship = Pair(selectedRelationship, code)
            }
        }
    }

    private fun showIdNumberField(position: Int) {
        val isChecked = position > -1
        if (isChecked && identificationNormalInputView.visibility == View.GONE) {
            identificationNormalInputView.visibility = View.VISIBLE
        }
    }

    private fun changeIdNumberFieldTitle(position: Int) {
        when (position) {
            0 -> identificationNormalInputView.setTitleText(getString(R.string.manage_policy_beneficiaries_identification_number))
            1 -> identificationNormalInputView.setTitleText(getString(R.string.manage_policy_beneficiaries_passport_number))
        }
    }
}