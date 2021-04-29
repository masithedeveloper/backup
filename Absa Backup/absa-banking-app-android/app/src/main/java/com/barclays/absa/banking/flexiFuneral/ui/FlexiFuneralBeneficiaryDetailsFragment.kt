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
import android.content.res.Configuration
import android.os.Bundle
import android.view.View
import androidx.lifecycle.MutableLiveData
import com.barclays.absa.banking.R
import com.barclays.absa.banking.policy_beneficiaries.ui.InsuranceBeneficiaryHelper
import com.barclays.absa.utils.AnalyticsUtil
import com.barclays.absa.utils.DateUtils
import kotlinx.android.synthetic.main.generic_insurance_beneficiary_details_fragment.*
import styleguide.forms.ItemSelectionInterface
import styleguide.forms.SelectorType
import styleguide.forms.validation.addRequiredValidationHidingTextWatcher
import java.util.*

class FlexiFuneralBeneficiaryDetailsFragment : FlexiFuneralBaseFragment(R.layout.generic_insurance_beneficiary_details_fragment) {
    var mininumAge: Int = -1
    var maximumAge: Int = -1

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()
        attachObservers()
        setUpOnClickListeners()
        setUpNormalInputViews()
        setUpTextWatchers()
        hostActivity.setToolBar(getString(R.string.insurance_beneficiary_details))
        AnalyticsUtil.trackAction("WIMI_FlexiFuneral", "FlexiFuneral_AddBeneficiary_ScreenDisplayed")
    }

    private fun initViews() {
        titleNormalInputView.setList(InsuranceBeneficiaryHelper.buildSelectorOptionsFromArray(resources.getStringArray(R.array.titles)), getString(R.string.flexi_funeral_select_title))
        categoryNormalInputView.setList(InsuranceBeneficiaryHelper.buildSelectorOptionsFromArray(resources.getStringArray(R.array.flexiFuneralFamilyMemberCategory)), getString(R.string.flexi_funeral_relationship_list_title))
        categoryNormalInputView.setItemSelectionInterface {
            setRelationshipList(categoryNormalInputView.selectedValue)
        }
        getAgeLimitMessage()
    }

    private fun attachObservers() {
        flexiFuneralViewModel.addBeneficiaryStatus = MutableLiveData()
        flexiFuneralViewModel.addBeneficiaryStatus.observe(viewLifecycleOwner, {

            if (it.addBeneficiaryStatus && "Success".equals(it.transactionStatus, true)) {
                navigate(FlexiFuneralBeneficiaryDetailsFragmentDirections.actionFlexiFuneralBeneficiaryDetailsFragmentToFlexiFuneralConfirmationFragment())
            }
            dismissProgressDialog()
        })

        flexiFuneralViewModel.failureResponse = MutableLiveData()
        flexiFuneralViewModel.failureResponse.observe(viewLifecycleOwner, {
            showGenericErrorMessage()
        })
    }

    private fun setUpOnClickListeners() {
        relationshipNormalInputView.setCustomOnClickListener {
            if (categoryNormalInputView.selectedValue.isEmpty()) {
                relationshipNormalInputView.setError(getString(R.string.flexi_funeral_select_category_first))
            } else {
                relationshipNormalInputView.triggerListActivity()
            }
        }

        continueButton.setOnClickListener {
            if (isValidData()) {
                flexiFuneralViewModel.flexiFuneralBeneficiaryDetails.apply {
                    titleInEnglish = englishTitleList[titleNormalInputView.selectedIndex]
                    title = titleNormalInputView.selectedValue
                    firstName = firstNameNormalInputView.selectedValue
                    surname = surnameNormalInputView.selectedValue
                    initials = firstNameNormalInputView.selectedValue.first().toString()
                    dateOfBirth = DateUtils.formatDate(dateOfBirthNormalInputView.selectedValue, DateUtils.DATE_DISPLAY_PATTERN, DateUtils.DASHED_DATE_PATTERN)
                    relationship = InsuranceBeneficiaryHelper.buildFamilyMembersRelationshipMappings(hostActivity).getValue(relationshipNormalInputView.selectedValue)
                }
                flexiFuneralViewModel.addBeneficiary(flexiFuneralViewModel.flexiFuneralBeneficiaryDetails)
            }
        }
    }

    private fun setUpNormalInputViews() {
        dateOfBirthNormalInputView.setValueEditable(false)
        dateOfBirthNormalInputView.setCustomOnClickListener {
            initializeDatePicker(mininumAge, maximumAge)
        }
    }

    private fun setUpTextWatchers() {
        firstNameNormalInputView.addRequiredValidationHidingTextWatcher()
        surnameNormalInputView.addRequiredValidationHidingTextWatcher()
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

    private fun getAgeLimitMessage() {
        flexiFuneralViewModel.flexiFuneralValidationRulesDetails.forEach {
            if (it.targetName == "Beneficiary") {
                dateOfBirthNormalInputView.setDescription(getString(R.string.flexi_funeral_age_limit_message, it.minAge, it.maxAge))
                mininumAge = it.minAge.toInt()
                maximumAge = it.maxAge.toInt()
            }
        }
    }

    private val englishTitleList: Array<String>
        get() {
            val configuration = Configuration(hostActivity.resources.configuration).apply { setLocale(Locale.ENGLISH) }
            return hostActivity.createConfigurationContext(configuration).resources.getStringArray(R.array.titles)
        }

    private fun isValidData(): Boolean {
        when {
            titleNormalInputView.selectedValue.isEmpty() -> titleNormalInputView.setError(getString(R.string.flexi_funeral_beneficiary_title_error))
            firstNameNormalInputView.selectedValue.isEmpty() -> firstNameNormalInputView.setError(getString(R.string.flexi_funeral_beneficiary_first_name_error))
            surnameNormalInputView.selectedValue.isEmpty() -> surnameNormalInputView.setError(getString(R.string.flexi_funeral_beneficiary_surname_error))
            categoryNormalInputView.selectedValue.isEmpty() -> categoryNormalInputView.setError(getString(R.string.flexi_funeral_category_error))
            relationshipNormalInputView.selectedValue.isEmpty() -> relationshipNormalInputView.setError(getString(R.string.flexi_funeral_relationship_error))
            dateOfBirthNormalInputView.selectedValue.isEmpty() -> dateOfBirthNormalInputView.setError(getString(R.string.flexi_funeral_date_of_birth_error))
            else -> return true
        }
        return false
    }

    private fun initializeDatePicker(minimumAge: Int, maximumAge: Int) {
        val calendar = Calendar.getInstance()
        val datePickerDialog = FlexiFuneralCustomerDatePickerDialog(hostActivity, { _, year, month, dayOfMonth ->
            val dateOfBirth = Calendar.getInstance()
            dateOfBirth.set(year, month, dayOfMonth)
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
            datePicker.touchables[0].performClick()
            show()
        }

    }

    override fun onDestroyView() {
        flexiFuneralViewModel.addBeneficiaryStatus.removeObservers(this)
        super.onDestroyView()
    }
}