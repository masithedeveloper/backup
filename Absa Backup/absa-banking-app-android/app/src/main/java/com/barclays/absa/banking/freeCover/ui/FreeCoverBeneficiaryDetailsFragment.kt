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

import android.app.DatePickerDialog
import android.os.Bundle
import android.text.InputFilter
import android.text.InputFilter.LengthFilter
import android.view.View
import com.barclays.absa.banking.R
import com.barclays.absa.banking.freeCover.ui.FreeCoverActivity.Companion.FREE_COVER_ANALYTICS_TAG
import com.barclays.absa.banking.policy_beneficiaries.ui.InsuranceBeneficiaryHelper
import com.barclays.absa.utils.AnalyticsUtil
import com.barclays.absa.utils.DateUtils.*
import kotlinx.android.synthetic.main.generic_insurance_beneficiary_details_fragment.*
import styleguide.forms.ItemSelectionInterface
import styleguide.forms.SelectorType
import styleguide.forms.validation.FieldRequiredValidationRule
import styleguide.forms.validation.ValueRequiredValidationHidingTextWatcher
import styleguide.forms.validation.addValidationRule
import styleguide.utils.extensions.toSentenceCase
import java.util.*

class FreeCoverBeneficiaryDetailsFragment : FreeCoverBaseFragment(R.layout.generic_insurance_beneficiary_details_fragment) {
    companion object {
        const val MIN_AGE_YEARS = 18
        const val MAX_AGE_YEARS = 70
        const val ESTATE_LATE_CODE = "07"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        InsuranceBeneficiaryHelper.buildTitles(resources.getStringArray(R.array.titles))
        InsuranceBeneficiaryHelper.buildRelationships(resources.getStringArray(R.array.relationships))
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setToolBar(getString(R.string.insurance_beneficiary_details).toSentenceCase())
        freeCoverInterface.setStep(3)
        AnalyticsUtil.trackAction(FREE_COVER_ANALYTICS_TAG, "FreeCover_DetailsScreen_ScreenDisplayed")

        initViews()
        addValidationRules()
        setUpNormalInputViews()
        setUpOnClickListener()
        setUpTextWatchers()
    }

    private fun initViews() {
        titleNormalInputView.setList(InsuranceBeneficiaryHelper.buildSelectorOptionsFromArray(resources.getStringArray(R.array.titles)), getString(R.string.free_cover_beneficiary_title))
        categoryNormalInputView.setList(InsuranceBeneficiaryHelper.buildSelectorOptionsFromArray(resources.getStringArray(R.array.beneficiaryCategory)), getString(R.string.free_cover_relationship_list_title))
        categoryNormalInputView.setItemSelectionInterface(ItemSelectionInterface {
            setRelationshipList(categoryNormalInputView.selectedValue)
        })
    }

    private fun setRelationshipList(categoryType: String) {
        when (categoryType) {
            getString(R.string.free_cover_spouse) -> {
                relationshipNormalInputView.selectedValue = getString(R.string.free_cover_spouse)
                relationshipNormalInputView.setSelectorViewType(SelectorType.NONE)
            }

            getString(R.string.free_cover_children), getString(R.string.free_cover_parents), getString(R.string.free_cover_extended_family) -> {
                relationshipNormalInputView.apply {
                    text = ""
                    setSelectorViewType(SelectorType.LONG_LIST)

                    val relationshipArray = when (categoryType) {
                        getString(R.string.free_cover_children) -> {
                            R.array.beneficiaryRelationshipChildren
                        }
                        getString(R.string.free_cover_parents) -> {
                            R.array.beneficiaryRelationshipParentsOrInLaws
                        }
                        else -> {
                            R.array.freeCoverRelationshipExtendedFamily
                        }
                    }
                    setList(InsuranceBeneficiaryHelper.buildSelectorOptionsFromArray(resources.getStringArray(relationshipArray)), categoryType)
                }
            }
        }
    }

    private fun setUpTextWatchers() {
        titleNormalInputView.addValueViewTextWatcher(ValueRequiredValidationHidingTextWatcher(titleNormalInputView))
        firstNameNormalInputView.addValueViewTextWatcher(ValueRequiredValidationHidingTextWatcher(firstNameNormalInputView))
        surnameNormalInputView.addValueViewTextWatcher(ValueRequiredValidationHidingTextWatcher(surnameNormalInputView))
        categoryNormalInputView.addValueViewTextWatcher(ValueRequiredValidationHidingTextWatcher(categoryNormalInputView))
        relationshipNormalInputView.addValueViewTextWatcher(ValueRequiredValidationHidingTextWatcher(relationshipNormalInputView))
        dateOfBirthNormalInputView.addValueViewTextWatcher(ValueRequiredValidationHidingTextWatcher(dateOfBirthNormalInputView))
    }

    private fun areAllFieldsValid() = titleNormalInputView.validate()
            && firstNameNormalInputView.validate()
            && surnameNormalInputView.validate()
            && categoryNormalInputView.validate()
            && relationshipNormalInputView.validate()
            && dateOfBirthNormalInputView.validate()

    private fun addValidationRules() {
        firstNameNormalInputView.editText?.filters = arrayOf<InputFilter>(LengthFilter(30))
        surnameNormalInputView.editText?.filters = arrayOf<InputFilter>(LengthFilter(30))

        titleNormalInputView.addValidationRule(FieldRequiredValidationRule(R.string.flexi_funeral_beneficiary_title_error))
        firstNameNormalInputView.addValidationRule(FieldRequiredValidationRule(R.string.flexi_funeral_beneficiary_first_name_error))
        surnameNormalInputView.addValidationRule(FieldRequiredValidationRule(R.string.flexi_funeral_beneficiary_surname_error))
        categoryNormalInputView.addValidationRule(FieldRequiredValidationRule(R.string.flexi_funeral_category_error))
        relationshipNormalInputView.addValidationRule(FieldRequiredValidationRule(R.string.flexi_funeral_relationship_error))
        dateOfBirthNormalInputView.addValidationRule(FieldRequiredValidationRule(R.string.flexi_funeral_date_of_birth_error))
    }

    private fun setUpNormalInputViews() {
        with(dateOfBirthNormalInputView) {
            setValueEditable(false)
            setDescription(getString(R.string.free_cover_date_of_birth_description))
            setCustomOnClickListener(View.OnClickListener {
                showDayPicker()
            })
        }
    }

    private fun showDayPicker() {
        val minDateTimeInMillis = Calendar.getInstance().apply {
            add(Calendar.YEAR, -MAX_AGE_YEARS)
            add(Calendar.DAY_OF_YEAR, 1)
        }.timeInMillis

        val maxDateTimeInMillis = Calendar.getInstance().apply {
            add(Calendar.YEAR, -MIN_AGE_YEARS)
        }.timeInMillis

        val currentDate = Calendar.getInstance()

        DatePickerDialog(hostActivity, R.style.DatePickerDialogTheme, { _, year, month, day ->
            val calendarTime = Calendar.getInstance().apply { set(year, month, day) }.time
            dateOfBirthNormalInputView.selectedValue = format(calendarTime, DATE_DISPLAY_PATTERN)
            freeCoverViewModel.applyFreeCoverData.dateOfBirth = format(calendarTime, DASHED_DATE_PATTERN)
        }, currentDate.get(Calendar.YEAR), currentDate.get(Calendar.MONTH), currentDate.get(Calendar.DAY_OF_MONTH)).apply {
            datePicker.minDate = minDateTimeInMillis
            datePicker.maxDate = maxDateTimeInMillis
            setButton(DatePickerDialog.BUTTON_POSITIVE, getString(R.string.ok_call_me), this)
            setButton(DatePickerDialog.BUTTON_NEGATIVE, getString(R.string.cancel), this)
        }.show()
    }

    private fun getInitials(firstName: String): String {
        val allNames = firstName.split(" ")
        var initials = ""

        allNames.forEach {
            if (it.isNotBlank()) initials += it.first()
        }
        return initials.take(3)
    }

    private fun setUpOnClickListener() {
        relationshipNormalInputView.setCustomOnClickListener(View.OnClickListener {
            if (categoryNormalInputView.selectedValue.isBlank()) {
                relationshipNormalInputView.setError(getString(R.string.flexi_funeral_select_category_first))
            } else {
                relationshipNormalInputView.triggerListActivity()
            }
        })

        continueButton.setOnClickListener {
            AnalyticsUtil.trackAction(FREE_COVER_ANALYTICS_TAG, "FreeCover_DetailsScreen_BeneficiaryButtonClicked")
            if (areAllFieldsValid()) {
                freeCoverViewModel.applyFreeCoverData.apply {
                    firstName = firstNameNormalInputView.selectedValue.trim()
                    surname = surnameNormalInputView.selectedValue.trim()
                    initials = getInitials(firstName)
                    dateOfBirth = formatDate(dateOfBirthNormalInputView.selectedValue, DATE_DISPLAY_PATTERN, DASHED_DATE_PATTERN)
                    relationshipCode = InsuranceBeneficiaryHelper.relationshipMap[relationshipNormalInputView.selectedValue] ?: ""
                    titleCode = InsuranceBeneficiaryHelper.titleMap[titleNormalInputView.selectedValue] ?: ""
                }

                navigate(FreeCoverBeneficiaryDetailsFragmentDirections.actionFreeCoverBeneficiaryDetailsFragmentToFreeCoverBeneficiaryContactDetailsFragment())
            }
        }
    }
}