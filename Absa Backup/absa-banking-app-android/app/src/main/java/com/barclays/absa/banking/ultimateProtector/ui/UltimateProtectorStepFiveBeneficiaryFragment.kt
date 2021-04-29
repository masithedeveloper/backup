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

package com.barclays.absa.banking.ultimateProtector.ui

import android.app.DatePickerDialog
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.DatePicker
import com.barclays.absa.banking.R
import com.barclays.absa.banking.databinding.UltimateProtectorStepFiveBeneficiaryFragmentBinding
import com.barclays.absa.banking.framework.AbsaBaseFragment
import com.barclays.absa.banking.framework.BaseActivity
import com.barclays.absa.banking.funeralCover.ui.FuneralCoverActivity
import com.barclays.absa.banking.funeralCover.ui.FuneralCoverPolicyDetailsOverviewFragment
import com.barclays.absa.banking.policy_beneficiaries.ui.InsuranceBeneficiaryHelper
import com.barclays.absa.banking.ultimateProtector.services.dto.BeneficiaryInfo
import com.barclays.absa.banking.ultimateProtector.services.dto.Relationship
import com.barclays.absa.banking.ultimateProtector.services.dto.Title
import com.barclays.absa.utils.AnalyticsUtil
import com.barclays.absa.utils.CompatibilityUtils
import com.barclays.absa.utils.DateUtils
import com.barclays.absa.utils.viewModel
import styleguide.forms.validation.addRequiredValidationHidingTextWatcher
import java.time.LocalDate
import java.util.*

class UltimateProtectorStepFiveBeneficiaryFragment : AbsaBaseFragment<UltimateProtectorStepFiveBeneficiaryFragmentBinding>(), DatePickerDialog.OnDateSetListener {

    private lateinit var ultimateProtectorViewModel: UltimateProtectorViewModel
    private lateinit var datePickerDialog: DatePickerDialog

    companion object {
        const val MAX_AGE_YEARS = 18
        const val MIN_AGE_YEARS = 75

        @JvmStatic
        fun newInstance() = UltimateProtectorStepFiveBeneficiaryFragment()
    }

    override fun getLayoutResourceId(): Int = R.layout.ultimate_protector_step_five_beneficiary_fragment

    override fun onAttach(context: Context) {
        super.onAttach(context)
        ultimateProtectorViewModel = (activity as BaseActivity).viewModel()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ultimateProtectorViewModel.ultimateProtectorInfo.beneficiaryInfo = BeneficiaryInfo()
        ultimateProtectorViewModel.buildTitles(resources.getStringArray(R.array.titles))
        ultimateProtectorViewModel.buildRelationships(resources.getStringArray(R.array.relationships))
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setToolBar(getString(R.string.ultimate_protector_beneficiary_details))
        activity?.let {
            bindDataToViews(it)
            attachEventHandlers()
            AnalyticsUtil.trackAction(UltimateProtectorHostActivity.ULTIMATE_PROTECTOR_CHANNEL, "WIMI_Life_UP_Step5_AddBeneficiary")
        }
    }

    private fun isAllFieldsCompleted(): Boolean {
        when {
            binding.titleNormalInputView.selectedValue.trim().isEmpty() -> binding.titleNormalInputView.setError(getString(R.string.ultimate_protector_title_required))
            binding.nameNormalInputView.selectedValue.trim().isEmpty() -> binding.nameNormalInputView.setError(getString(R.string.name_required))
            binding.surnameNormalInputView.selectedValue.trim().isEmpty() -> binding.surnameNormalInputView.setError(getString(R.string.surname_required))
            binding.relationshipNormalInputView.selectedValue.trim().isEmpty() -> if (binding.categoryNormalInputView.selectedValue.trim().isEmpty()) {
                binding.categoryNormalInputView.setError(getString(R.string.category_required))
            } else {
                binding.relationshipNormalInputView.setError(getString(R.string.relationship_required))
            }
            binding.dateOfBirthNormalInputView.selectedValue.trim().isEmpty() -> binding.dateOfBirthNormalInputView.setError(getString(R.string.date_of_birth_required))
            else -> {
                return true
            }
        }
        return false
    }

    //Need to fix the progress indicator
    private fun bindDataToViews(context: Context) {
        if (context is UltimateProtectorHostActivity) {
            context.setStep(5)
        }

        if (context is FuneralCoverActivity) {
            context.setStep(2)
            binding.noteSecondaryContentAndLabelView.visibility = View.GONE
        }
        populateTitleOptions()
        populateCategoryOptions()
        initializeDatePicker(context)
        prePopulateFields()
    }

    private fun prePopulateFields() {
        val beneficiaryInfo = ultimateProtectorViewModel.ultimateProtectorInfo.beneficiaryInfo
        binding.nameNormalInputView.selectedValue = beneficiaryInfo?.firstName.toString()
        binding.surnameNormalInputView.selectedValue = beneficiaryInfo?.surname.toString()
        binding.dateOfBirthNormalInputView.selectedValue = DateUtils.formatDate(beneficiaryInfo?.dateOfBirth, "yyyy-MM-dd", "dd MMM yyyy") ?: ""
        binding.titleNormalInputView.selectedIndex = binding.titleNormalInputView.findItemIndex(beneficiaryInfo?.title?.description.toString())
        binding.categoryNormalInputView.selectedIndex = binding.categoryNormalInputView.findItemIndex(beneficiaryInfo?.category.toString())

        beneficiaryInfo?.category?.let {
            if (it.isNotEmpty()) {
                initializeRelationship(it)
            }
        }

        binding.relationshipNormalInputView.selectedIndex = binding.relationshipNormalInputView.findItemIndex(beneficiaryInfo?.relationship?.description.toString())
        beneficiaryInfo?.dateOfBirth?.let {
            if (it.isNotEmpty()) {
                val date: Array<String> = beneficiaryInfo.dateOfBirth.split("-").toTypedArray()
                datePickerDialog = DatePickerDialog(requireActivity(), R.style.CalenderDialogTheme, this, date[0].toInt(), date[1].toInt() - 1, date[2].toInt())
            }
        }
    }

    private fun populateTitleOptions() {
        val titleOptions = resources.getStringArray(R.array.ultimate_protector_title)
        binding.titleNormalInputView.setList(ultimateProtectorViewModel.buildOptionsSelectorListFromArray(titleOptions), getString(R.string.ultimate_protector_beneficiary_title))
    }

    private fun populateCategoryOptions() {
        val categoryOptions = resources.getStringArray(R.array.beneficiaryCategory)
        binding.categoryNormalInputView.setList(ultimateProtectorViewModel.buildOptionsSelectorListFromArray(categoryOptions), getString(R.string.member_category))
    }

    private fun initializeRelationship(category: String) {
        binding.relationshipNormalInputView.setList(InsuranceBeneficiaryHelper.buildSelectorOptionsFromCategory(activity as Context, category), getString(R.string.relationship))
        if (category == getString(R.string.spouse)) {
            binding.relationshipNormalInputView.selectedIndex = 0
            setRelationship(getString(R.string.spouse))
        }
    }

    private fun setRelationship(description: String) {
        val code = ultimateProtectorViewModel.relationshipMap[description]
        code?.let {
            ultimateProtectorViewModel.ultimateProtectorInfo.beneficiaryInfo?.relationship = Relationship(description, code)
        }
    }

    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        val selectedDate = Calendar.getInstance()
        selectedDate.set(year, month, dayOfMonth)
        binding.dateOfBirthNormalInputView.selectedValue = DateUtils.format(selectedDate.time, DateUtils.DATE_DISPLAY_PATTERN)
        ultimateProtectorViewModel.ultimateProtectorInfo.beneficiaryInfo?.dateOfBirth = DateUtils.format(selectedDate.time, DateUtils.DASHED_DATE_PATTERN)
    }

    private fun initializeDatePicker(context: Context) {
        if (CompatibilityUtils.isVersionGreaterThanOrEqualTo(Build.VERSION_CODES.N)) {
            datePickerDialog = DatePickerDialog(context, R.style.CalenderDialogTheme)
            datePickerDialog.setOnDateSetListener(this)
        } else {
            datePickerDialog = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val currentDate = LocalDate.now()
                DatePickerDialog(context, R.style.CalenderDialogTheme, this, currentDate.year, currentDate.month.value, currentDate.dayOfMonth)
            } else {
                val todayCalendar = GregorianCalendar()
                DatePickerDialog(context, R.style.CalenderDialogTheme, this, todayCalendar.get(Calendar.YEAR), todayCalendar.get(Calendar.MONTH) + 1, todayCalendar.get(Calendar.DAY_OF_MONTH))
            }
        }
    }

    private fun attachEventHandlers() {
        binding.titleNormalInputView.setItemSelectionInterface {
            val description = binding.titleNormalInputView.selectedValue
            val code = ultimateProtectorViewModel.titleMap[description]
            code?.let {
                ultimateProtectorViewModel.ultimateProtectorInfo.beneficiaryInfo?.title = Title(description, code)
            }
        }

        binding.nameNormalInputView.addRequiredValidationHidingTextWatcher {
            ultimateProtectorViewModel.ultimateProtectorInfo.beneficiaryInfo?.firstName = it
            if (it.isNotEmpty()) {
                val initialBuilder = StringBuilder()
                val names = it.split(" ")
                if (names.isEmpty()) {
                    initialBuilder.append(it[0])
                } else {
                    names.forEach { name ->
                        if (name.isNotEmpty()) {
                            initialBuilder.append(name[0])
                        }
                    }
                }
                ultimateProtectorViewModel.ultimateProtectorInfo.beneficiaryInfo?.initials = initialBuilder.toString()
            }
        }

        binding.surnameNormalInputView.addRequiredValidationHidingTextWatcher {
            ultimateProtectorViewModel.ultimateProtectorInfo.beneficiaryInfo?.surname = it
        }

        binding.categoryNormalInputView.setItemSelectionInterface {
            if (binding.categoryNormalInputView.selectedValue.isNotEmpty()) {
                binding.relationshipNormalInputView.visibility = View.VISIBLE
                binding.relationshipNormalInputView.clear()
                binding.relationshipNormalInputView.selectedIndex = -1
                val selectedCategory = binding.categoryNormalInputView.selectedValue
                ultimateProtectorViewModel.ultimateProtectorInfo.beneficiaryInfo?.category = binding.categoryNormalInputView.selectedValue
                initializeRelationship(selectedCategory)
            }
        }

        binding.relationshipNormalInputView.setItemSelectionInterface {
            setRelationship(binding.relationshipNormalInputView.selectedValue)
        }

        binding.dateOfBirthNormalInputView.setOnClickListener {
            val maxDate = Calendar.getInstance()
            maxDate.add(Calendar.YEAR, -MAX_AGE_YEARS)

            val minDate = Calendar.getInstance()
            minDate.add(Calendar.YEAR, -MIN_AGE_YEARS)
            minDate.add(Calendar.DAY_OF_YEAR, 1)

            datePickerDialog.setButton(DatePickerDialog.BUTTON_POSITIVE, getString(R.string.ok_call_me), datePickerDialog)
            datePickerDialog.setButton(DatePickerDialog.BUTTON_NEGATIVE, getString(R.string.cancel), datePickerDialog)

            val datePicker = datePickerDialog.datePicker
            datePicker.maxDate = maxDate.timeInMillis
            datePicker.minDate = minDate.timeInMillis
            datePicker.touchables[0].performClick()

            binding.dateOfBirthNormalInputView.setDescription(getString(R.string.date_of_birth_description))
            datePickerDialog.show()
        }

        binding.addBeneficiaryButton.setOnClickListener {
            if (isAllFieldsCompleted()) {
                val hostActivity = activity
                if (hostActivity is FuneralCoverActivity) {
                    hostActivity.startFragment(FuneralCoverPolicyDetailsOverviewFragment.newInstance(), true, BaseActivity.AnimationType.SLIDE)
                } else {
                    navigate(UltimateProtectorStepFiveBeneficiaryFragmentDirections.actionUltimateProtectorStepFiveBeneficiaryFragmentToUltimateProtectorStepSixFragment())
                }
            }
        }
    }
}
