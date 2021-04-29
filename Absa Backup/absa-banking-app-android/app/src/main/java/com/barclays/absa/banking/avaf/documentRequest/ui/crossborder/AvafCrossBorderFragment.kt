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

package com.barclays.absa.banking.avaf.documentRequest.ui.crossborder

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.View
import com.barclays.absa.banking.R
import com.barclays.absa.banking.avaf.documentRequest.ui.DocumentRequestConstants
import com.barclays.absa.banking.card.ui.transactions.DatePickerDialogFragment
import com.barclays.absa.banking.databinding.AvafCrossBorderFragmentBinding
import com.barclays.absa.banking.framework.BaseFragment
import com.barclays.absa.utils.AnalyticsUtil
import com.barclays.absa.utils.DateUtils
import com.barclays.absa.utils.extensions.viewBinding
import styleguide.forms.validation.EmailValidationRule
import styleguide.forms.validation.FieldRequiredValidationRule
import styleguide.forms.validation.addValidationRule
import styleguide.forms.validation.addValidationRules
import java.util.*

class AvafCrossBorderFragment : BaseFragment(R.layout.avaf_cross_border_fragment) {
    private val binding by viewBinding(AvafCrossBorderFragmentBinding::bind)
    private lateinit var fromDatePickerDialogFragment: DatePickerDialogFragment
    private lateinit var toDatePickerDialogFragment: DatePickerDialogFragment
    private var fromDatePickerListener = DatePickerDialog.OnDateSetListener { _, selectedYear, selectedMonth, selectedDay ->
        val calender = Calendar.getInstance().apply {
            set(selectedYear, selectedMonth, selectedDay)
        }

        with(binding.documentFromDateNormalInputView) {
            text = DateUtils.format(calender.time, DateUtils.DATE_DISPLAY_PATTERN)
            tag = calender.time
            clearError()
        }
    }

    private var toDatePickerListener = DatePickerDialog.OnDateSetListener { _, selectedYear, selectedMonth, selectedDay ->
        val calender = Calendar.getInstance().apply {
            set(selectedYear, selectedMonth, selectedDay)
        }

        with(binding.documentToDateNormalInputView) {
            text = DateUtils.format(calender.time, DateUtils.DATE_DISPLAY_PATTERN)
            tag = calender.time
            clearError()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        AnalyticsUtil.trackAction("AVAFCrossBorderLetterRequest_CrossBorderLetterRequestScreen_ScreenDisplayed")
        setToolBar(getString(R.string.avaf_document_request_crossborder_request))
        initViews()
        setupListeners()
        setupValidationRules()
    }

    private fun setupListeners() {
        with(binding) {
            documentFromDateNormalInputView.setOnClickListener { showDatePickerDialog(fromDatePickerListener) }
            documentToDateNormalInputView.setOnClickListener { showDatePickerDialog(toDatePickerListener) }

            continueButton.setOnClickListener {
                AnalyticsUtil.trackAction("AVAFCrossBorderLetterRequest_CrossBorderLetterRequestScreen_ContinueButtonClicked")
                if (hasValidationErrors()) {
                    return@setOnClickListener
                }

                if (isMoreThanThreeMonths()) {
                    documentToDateNormalInputView.setError(getString(R.string.avaf_document_request_cross_border_date_period_exceed))
                    return@setOnClickListener
                }

                val name = driverNameNormalInputView.selectedValue.trim()
                val identification = driverIdNumberNormalInputView.selectedValue.trim()
                val email = emailNormalInputView.selectedValue.trim()
                val dateTo = documentToDateNormalInputView.tag as Date
                val dateFrom = documentFromDateNormalInputView.tag as Date
                val action = AvafCrossBorderFragmentDirections.actionCrossBorderFragmentToCrossBorderConfirmationFragment(name, identification, dateFrom, dateTo, email)
                navigate(action)
            }
        }
    }

    private fun setupValidationRules() {
        with(binding) {
            emailNormalInputView.addValidationRules(
                    FieldRequiredValidationRule(R.string.invalid_email_address),
                    EmailValidationRule(R.string.invalid_email_address)
            )

            documentToDateNormalInputView.addValidationRule(FieldRequiredValidationRule(R.string.avaf_document_request_select_date))
            documentFromDateNormalInputView.addValidationRule(FieldRequiredValidationRule(R.string.avaf_document_request_select_date))
            driverNameNormalInputView.addValidationRule(FieldRequiredValidationRule(R.string.name_required))
            driverIdNumberNormalInputView.addValidationRule(FieldRequiredValidationRule(R.string.avaf_document_request_cross_border_id_required))
        }
    }

    private fun initViews() {
        binding.documentToDateNormalInputView.setOnClickListener {
            toDatePickerDialogFragment = DatePickerDialogFragment.newInstance(DatePickerDialogFragment.DateType.TO_DATE).apply {
                setOnDateSetListener(toDatePickerListener)
            }
        }

        fromDatePickerDialogFragment = DatePickerDialogFragment.newInstance(DatePickerDialogFragment.DateType.FROM_DATE).apply {
            setOnDateSetListener(fromDatePickerListener)
        }
    }

    private fun showDatePickerDialog(listener: DatePickerDialog.OnDateSetListener) {
        val calendar = Calendar.getInstance()
        val datePickerDialog = DatePickerDialog(requireContext(),
                R.style.DatePickerDialogTheme,
                listener,
                calendar[Calendar.YEAR],
                calendar[Calendar.MONTH],
                calendar[Calendar.DAY_OF_MONTH])

        with(datePickerDialog) {
            datePicker.minDate = calendar.timeInMillis
            show()
        }
    }

    private fun hasValidationErrors(): Boolean {
        return !binding.driverNameNormalInputView.validate() || !binding.driverIdNumberNormalInputView.validate() || !binding.documentFromDateNormalInputView.validate() || !binding.documentToDateNormalInputView.validate() || !binding.emailNormalInputView.validate()
    }

    private fun isMoreThanThreeMonths(): Boolean {
        val dateTo = binding.documentToDateNormalInputView.tag as? Date
        val dateFrom = binding.documentFromDateNormalInputView.tag as? Date

        if (dateFrom == null || dateTo == null) {
            return true
        }

        val dateThreeMonthSkip = GregorianCalendar().apply {
            time = dateFrom
            add(Calendar.MONTH, DocumentRequestConstants.CROSSBORDER_DATE_GAP_MONTHS)
        }

        return dateTo.after(dateThreeMonthSkip.time)
    }
}