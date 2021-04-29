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

package com.barclays.absa.banking.flexiFuneral.ui

import FlexiFuneralCustomerDatePickerDialog
import android.app.DatePickerDialog
import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import android.view.View
import androidx.lifecycle.MutableLiveData
import com.barclays.absa.banking.R
import com.barclays.absa.banking.flexiFuneral.services.dto.CoverDetails
import com.barclays.absa.banking.flexiFuneral.services.dto.MultipleDependentsDetails
import com.barclays.absa.banking.policy_beneficiaries.ui.InsuranceBeneficiaryHelper
import com.barclays.absa.utils.AnalyticsUtil
import com.barclays.absa.utils.DateUtils
import kotlinx.android.synthetic.main.flexi_funeral_add_family_member_fragment.*
import styleguide.forms.SelectorList
import styleguide.forms.SelectorType
import styleguide.forms.StringItem
import styleguide.forms.validation.addRequiredValidationHidingTextWatcher
import styleguide.utils.extensions.toRandAmount
import java.util.*

class FlexiFuneralAddFamilyMemberFragment : FlexiFuneralBaseFragment(R.layout.flexi_funeral_add_family_member_fragment) {

    private var isFamilyMemberQuotesListEmpty = true
    private var mininumAge: Int = -1
    private var maximumAge: Int = -1
    private var currentIndex: Int = -1

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpToolBar()
        attachOnClickListeners()
        attachObservers()
        setUpNormalInputViews()
        AnalyticsUtil.trackAction("WIMI_FlexiFuneral", "FlexiFuneral_AddMember_ScreenDisplayed")
    }

    private fun setUpToolBar() {
        hostActivity.apply {
            setToolBar(getString(R.string.flexi_funeral_family_member_details))
            showToolbar()
        }
    }

    private fun attachObservers() {
        flexiFuneralViewModel.familyMemberCoverAmounts = MutableLiveData()
        flexiFuneralViewModel.familyMemberCoverAmounts.observe(viewLifecycleOwner, { familyMemberCoverAmountsResponse ->
            if (familyMemberCoverAmountsResponse.quotes.isNotEmpty()) {
                coverAmountNormalInputView.apply {
                    clearCustomOnClickListener()
                    setList(buildSelectorList(familyMemberCoverAmountsResponse.quotes.first().coverAmountQuotes), getString(R.string.flexi_funeral_cover_and_premium))
                    triggerListActivity()
                }

                flexiFuneralViewModel.multipleDependentsDetails.familyMemberQuotes = familyMemberCoverAmountsResponse.quotes.first().coverAmountQuotes
                isFamilyMemberQuotesListEmpty = false
            }
            dismissProgressDialog()
        })
    }

    private fun setUpNormalInputViews() {
        categoryNormalInputView.setList(InsuranceBeneficiaryHelper.buildSelectorOptionsFromArray(resources.getStringArray(R.array.flexiFuneralFamilyMemberCategory)), getString(R.string.flexi_funeral_relationship_list_title))
        genderNormalInputView.setList(InsuranceBeneficiaryHelper.buildSelectorOptionsFromArray(resources.getStringArray(R.array.flexiFuneralGender)), getString(R.string.insurance_gender_title))
        dateOfBirthNormalInputView.setValueEditable(false)
        initialsNormalInputView.addRequiredValidationHidingTextWatcher()
        surnameNormalInputView.addRequiredValidationHidingTextWatcher()

        categoryNormalInputView.setItemSelectionInterface { index ->
            setCategoryNormalInputViewDescription()

            val isMaximumNumberReached = when (categoryNormalInputView.selectedValue) {
                getString(R.string.flexi_funeral_spouse) -> validateMaximumNumberAllowedInEachCategory(1, getString(R.string.flexi_funeral_spouse))
                getString(R.string.flexi_funeral_children) -> validateMaximumNumberAllowedInEachCategory(10, getString(R.string.flexi_funeral_children))
                getString(R.string.flexi_funeral_extended_family) -> validateMaximumNumberAllowedInEachCategory(8, getString(R.string.flexi_funeral_extended_family))
                else -> false
            }

            if (isMaximumNumberReached) {
                categoryNormalInputView.setError(getString(R.string.flexi_funeral_category_maximum_limit_reached_error))
            } else {
                if (currentIndex != index && currentIndex != -1) {

                    listOf(relationshipNormalInputView, genderNormalInputView, coverAmountNormalInputView).forEach { normalInputView ->
                        normalInputView.clearSelectedIndexAndValue()
                    }

                    dateOfBirthNormalInputView.clear()
                    resetCoverAmount()
                }
                currentIndex = index
                categoryNormalInputView.clearError()
                relationshipNormalInputView.clearError()
                dateOfBirthNormalInputView.clearError()
                setRelationshipList(categoryNormalInputView.selectedValue)
                getCategoryAgeLimitMessage()

                when (categoryNormalInputView.selectedValue) {
                    getString(R.string.flexi_funeral_spouse) -> genderNormalInputView.visibility = View.VISIBLE
                    else -> genderNormalInputView.visibility = View.GONE
                }
            }
        }

        relationshipNormalInputView.setCustomOnClickListener {
            if (categoryNormalInputView.selectedValue.isEmpty()) {
                relationshipNormalInputView.setError(getString(R.string.flexi_funeral_select_category_first))
            } else {
                relationshipNormalInputView.triggerListActivity()
            }
        }

        relationshipNormalInputView.setItemSelectionInterface {
            if (relationshipNormalInputView.previousIndex != it) {
                genderNormalInputView.clearSelectedIndexAndValue()
                dateOfBirthNormalInputView.clearSelectedIndexAndValue()
                coverAmountNormalInputView.clearSelectedIndexAndValue()
                resetCoverAmount()
            }

            genderNormalInputView.visibility = when (relationshipNormalInputView?.selectedValue) {
                getString(R.string.flexi_funeral_spouse),
                getString(R.string.flexi_funeral_cousin),
                getString(R.string.flexi_funeral_gardener_or_caretaker),
                -> View.VISIBLE
                else -> {
                    genderNormalInputView.selectedValue = (InsuranceBeneficiaryHelper.buildFamilyMembersGenderMappings(hostActivity).getValue(relationshipNormalInputView.selectedValue))
                    View.GONE
                }
            }
        }

        genderNormalInputView.setItemSelectionInterface {
            if (genderNormalInputView.previousIndex != it) {
                dateOfBirthNormalInputView.clearSelectedIndexAndValue()
                coverAmountNormalInputView.clearSelectedIndexAndValue()
                resetCoverAmount()
            }
        }

        dateOfBirthNormalInputView.setCustomOnClickListener {
            if (categoryNormalInputView.selectedValue.isNotEmpty() && !categoryNormalInputView.hasError()) {
                initializeDatePicker(mininumAge, maximumAge)
            } else {
                dateOfBirthNormalInputView.setError(getString(R.string.flexi_funeral_select_category_first))
            }
        }

        coverAmountNormalInputView.setCustomOnClickListener(setCoverCustomClickListener())

        coverAmountNormalInputView.setItemSelectionInterface { index ->
            flexiFuneralViewModel.multipleDependentsDetails.apply {
                dependentsCoverAmount = familyMemberQuotes[index].coverAmount.substringBefore(".")
                dependentsPremium = familyMemberQuotes[index].monthlyPremium
                selectedCoverIndex = index
            }
        }
    }

    private fun resetCoverAmount() {
        isFamilyMemberQuotesListEmpty = true
        coverAmountNormalInputView.setCustomOnClickListener(setCoverCustomClickListener())
    }

    private fun setCoverCustomClickListener(): View.OnClickListener {
        return View.OnClickListener {
            coverAmountNormalInputView.clearError()
            if (isValidData() && isFamilyMemberQuotesListEmpty) {
                saveFamilyMemberDetails()
                flexiFuneralViewModel.fetchFamilyMemberCoverAmounts(flexiFuneralViewModel.multipleDependentsDetails)
            }
        }
    }

    private fun setCategoryNormalInputViewDescription() {
        when (categoryNormalInputView.selectedValue) {
            getString(R.string.flexi_funeral_spouse) -> categoryNormalInputView.setDescription(getString(R.string.flexi_funeral_maximum_spouse_allowed_error))
            getString(R.string.flexi_funeral_children) -> categoryNormalInputView.setDescription(getString(R.string.flexi_funeral_maximum_children_allowed_error))
            getString(R.string.flexi_funeral_extended_family) -> categoryNormalInputView.setDescription(getString(R.string.flexi_funeral_maximum_extended_family_allowed_error))
        }
    }

    private fun attachOnClickListeners() {
        addFamilyMemberButton.setOnClickListener {
            if (isValidData()) {

                if (coverAmountNormalInputView.selectedValue.isNotEmpty()) {
                    flexiFuneralViewModel.isFromFamilyMemberDetailsFragment = true
                    flexiFuneralViewModel.familyMemberList.add(flexiFuneralViewModel.multipleDependentsDetails)
                    flexiFuneralViewModel.multipleDependentsDetails = MultipleDependentsDetails()
                    hostActivity.superOnBackPressed()
                } else {
                    coverAmountNormalInputView.setError(getString(R.string.flexi_funeral_select_a_cover_error_message))
                }
            }
        }
    }

    private fun isValidData(): Boolean {
        when {
            initialsNormalInputView.selectedValue.isEmpty() -> initialsNormalInputView.setError(getString(R.string.flexi_funeral_initials_error))
            surnameNormalInputView.selectedValue.isEmpty() -> surnameNormalInputView.setError(getString(R.string.flexi_funeral_surname_error))
            categoryNormalInputView.selectedValue.isEmpty() -> categoryNormalInputView.setError(getString(R.string.flexi_funeral_category_error))
            relationshipNormalInputView.selectedValue.isEmpty() -> relationshipNormalInputView.setError(getString(R.string.flexi_funeral_relationship_error))
            genderNormalInputView.selectedValue.isEmpty() -> genderNormalInputView.setError(getString(R.string.flexi_funeral_gender_error))
            dateOfBirthNormalInputView.selectedValue.isEmpty() -> dateOfBirthNormalInputView.setError(getString(R.string.flexi_funeral_date_of_birth_error))
            else -> return true
        }
        return false
    }

    private fun buildSelectorList(coverOptions: List<CoverDetails>): SelectorList<StringItem> = SelectorList<StringItem>().apply {
        addAll(coverOptions.map {
            StringItem(getString(R.string.flexi_funeral_main_member_cover_option, it.coverAmount.toRandAmount(), it.monthlyPremium.toRandAmount()))
        })
    }

    private fun setRelationshipList(categoryType: String) {
        when (categoryType) {
            getString(R.string.flexi_funeral_spouse) -> {
                relationshipNormalInputView.text = getString(R.string.flexi_funeral_spouse)
                relationshipNormalInputView.setSelectorViewType(SelectorType.NONE)
            }
            getString(R.string.flexi_funeral_children), getString(R.string.flexi_funeral_extended_family) -> {
                relationshipNormalInputView.apply {
                    text = ""
                    setSelectorViewType(SelectorType.LONG_LIST)
                    val relationshipArray = if (categoryType == getString(R.string.flexi_funeral_children)) R.array.flexiFuneralRelationshipChildren else R.array.flexiFuneralRelationshipExtendedFamily
                    setList(InsuranceBeneficiaryHelper.buildSelectorOptionsFromArray(resources.getStringArray(relationshipArray)), categoryType)
                }
            }
        }
    }

    private val englishMemberCategoryList: Array<String>
        get() {
            val configuration = Configuration(hostActivity.resources.configuration).apply { setLocale(Locale.ENGLISH) }
            return hostActivity.createConfigurationContext(configuration).resources.getStringArray(R.array.flexiFuneralFamilyMemberCategory)
        }

    private fun getCategoryAgeLimitMessage() {
        for (item in flexiFuneralViewModel.flexiFuneralValidationRulesDetails) {
            if (englishMemberCategoryList[categoryNormalInputView.selectedIndex].startsWith(item.targetName.substringBefore(" "))) {
                when (categoryNormalInputView.selectedValue) {
                    getString(R.string.flexi_funeral_spouse) -> dateOfBirthNormalInputView.setDescription(getString(R.string.flexi_funeral_spouse_age_limit_message, item.maxAge))
                    getString(R.string.flexi_funeral_children) -> dateOfBirthNormalInputView.setDescription(getString(R.string.flexi_funeral_child_age_limit_message, item.maxAge))
                    getString(R.string.flexi_funeral_extended_family) -> dateOfBirthNormalInputView.setDescription(getString(R.string.flexi_funeral_extended_family_age_limit_message, item.maxAge))
                }
                mininumAge = item.minAge.toInt()
                maximumAge = item.maxAge.toInt()
            }
        }
    }

    private fun saveFamilyMemberDetails() {
        flexiFuneralViewModel.multipleDependentsDetails.apply {
            dependentsInitials = initialsNormalInputView.selectedValue
            dependentsSurname = surnameNormalInputView.selectedValue
            dependentsRelationship = InsuranceBeneficiaryHelper.buildFamilyMembersRelationshipMappings(activity as Context).getValue(relationshipNormalInputView.selectedValue)
            dependentsCategory = categoryNormalInputView.selectedValue
            dependentsDateOfBirth = DateUtils.formatDate(dateOfBirthNormalInputView.selectedValue, DateUtils.DATE_DISPLAY_PATTERN, DateUtils.DASHED_DATE_PATTERN)
            dependentsGender = if (genderNormalInputView.visibility == View.VISIBLE) {
                (InsuranceBeneficiaryHelper.buildFamilyMembersGenderMappings(hostActivity).getValue(genderNormalInputView.selectedValue))
            } else {
                genderNormalInputView.selectedValue
            }
        }
    }

    private fun validateMaximumNumberAllowedInEachCategory(maximumMembersAllowed: Int, category: String): Boolean {
        return flexiFuneralViewModel.familyMemberList.filter { details -> details.dependentsCategory.equals(category, true) }.size >= maximumMembersAllowed
    }

    override fun onDestroyView() {
        super.onDestroyView()
        flexiFuneralViewModel.familyMemberCoverAmounts.removeObservers(this)
    }

    private fun initializeDatePicker(minimumAge: Int, maximumAge: Int) {
        val calendar = Calendar.getInstance()
        val datePickerDialog = FlexiFuneralCustomerDatePickerDialog(hostActivity, { _, year, month, dayOfMonth ->
            val dateOfBirth = Calendar.getInstance()
            dateOfBirth.set(year, month, dayOfMonth)

            if (dateOfBirthNormalInputView.selectedValue.isNotEmpty()) {
                if (dateOfBirth != DateUtils.getCalendar(dateOfBirthNormalInputView.selectedValue, DateUtils.DATE_DISPLAY_PATTERN)) {
                    resetCoverAmount()
                    coverAmountNormalInputView.clearSelectedIndexAndValue()
                }
            }

            dateOfBirthNormalInputView.selectedValue = DateUtils.format(dateOfBirth.time, DateUtils.DATE_DISPLAY_PATTERN)
            dateOfBirthNormalInputView.hideError()
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH))

        val currentDate = Calendar.getInstance()
        val ageLimitCutOffDay = currentDate.get(Calendar.DAY_OF_MONTH)
        val ageLimitCutOffMonth = currentDate.get(Calendar.MONTH)

        val maxDate = Calendar.getInstance().apply {
            add(Calendar.YEAR, -minimumAge)
            set(Calendar.DAY_OF_MONTH, ageLimitCutOffDay)
            set(Calendar.MONTH, ageLimitCutOffMonth)
        }

        val minDate = Calendar.getInstance().apply {
            add(Calendar.YEAR, -maximumAge)
            set(Calendar.DAY_OF_MONTH, ageLimitCutOffDay)
            set(Calendar.MONTH, ageLimitCutOffMonth)
        }

        val datePicker = datePickerDialog.datePicker
        datePicker.maxDate = maxDate.timeInMillis
        datePicker.minDate = minDate.timeInMillis

        datePickerDialog.apply {
            setButton(DatePickerDialog.BUTTON_POSITIVE, getString(R.string.ok_call_me), datePickerDialog)
            setButton(DatePickerDialog.BUTTON_NEGATIVE, getString(R.string.cancel), datePickerDialog)
            datePicker.touchables.first().performClick()
            show()
        }
    }
}