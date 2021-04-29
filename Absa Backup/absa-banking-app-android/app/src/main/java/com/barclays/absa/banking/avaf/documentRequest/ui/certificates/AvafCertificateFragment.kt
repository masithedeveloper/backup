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

package com.barclays.absa.banking.avaf.documentRequest.ui.certificates

import android.app.DatePickerDialog
import android.view.View.VISIBLE
import android.widget.DatePicker
import com.barclays.absa.banking.R
import com.barclays.absa.banking.avaf.documentRequest.ui.*
import com.barclays.absa.banking.databinding.AvafDocumentRequestBaseFragmentBinding
import com.barclays.absa.banking.express.avaf.accountInformation.dto.AbsaVehicleAndAssetFinanceDetail
import com.barclays.absa.utils.AnalyticsUtil
import com.barclays.absa.utils.DateUtils
import com.barclays.absa.utils.extensions.viewBinding
import java.util.*

class AvafCertificateFragment : AvafDocumentRequestBaseFragment() {
    private val binding by viewBinding(AvafDocumentRequestBaseFragmentBinding::bind)
    override var toolbarTitleResId: Int = R.string.avaf_document_request_tax_certificate_title
    override val analyticsScreen: String = "AVAFAuditTaxCertificate_AuditTaxCertificatesScreen_ScreenDisplayed"

    override fun initViews() {
        binding.documentRequestTitleTextView.text = getString(R.string.avaf_document_request_tax_certificate_subtitle)
        binding.documentDateNormalInputView.apply {
            setTitleText(R.string.avaf_document_request_tax_certificate_select_year)
            visibility = VISIBLE
        }
    }

    override fun setupListeners() {
        binding.continueButton.setOnClickListener {
            AnalyticsUtil.trackAction("AVAFAuditTaxCertificate_AuditTaxCertificatesScreen_ContinueButtonClicked")
            if (hasValidationErrors()) {
                return@setOnClickListener
            }

            val email = getEmail()
            val date: Date = getDate()
            val details = DocumentRequestDetails(DocumentRequestType.TAX_CERTIFICATE, email, date)
            val action = AvafCertificateFragmentDirections.actionCertificateFragmentToCertificateConfirmationFragment(details)
            navigate(action)
        }

        binding.documentDateNormalInputView.setOnClickListener { showDatePickerDialog() }
    }

    private fun showDatePickerDialog() {
        val calendar = Calendar.getInstance()
        val datePickerDialog = DatePickerDialog(requireContext(), R.style.DatePickerDialogTheme, { _: DatePicker?, year: Int, month: Int, day: Int ->
            calendar.set(year, month, day)
            binding.documentDateNormalInputView.apply {
                text = DateUtils.format(calendar.time, DateUtils.DATE_DISPLAY_PATTERN_FULL)
                tag = calendar.time
                clearError()
            }
        }, calendar[Calendar.YEAR], calendar[Calendar.MONTH], calendar[Calendar.DAY_OF_MONTH])

        datePickerDialog.datePicker.apply {
            activity?.intent?.getParcelableExtra<AbsaVehicleAndAssetFinanceDetail>(AvafDocumentRequestActivity.ACCOUNT_DETAIL)?.let { accountDetail ->
                val contractStartDate = DateUtils.getDate(accountDetail.contractStartDate, DateUtils.DASHED_DATE_PATTERN)
                val contractEndDate = DateUtils.getDate(accountDetail.contractEndDate, DateUtils.DASHED_DATE_PATTERN)
                val earliestAllowedDate = Calendar.getInstance().apply { add(Calendar.YEAR, DocumentRequestConstants.CERTIFICATE_EARLIEST_YEAR_BACKTRACK) }.time
                val latestAllowedDate = Calendar.getInstance().apply {
                    add(Calendar.MONTH, DocumentRequestConstants.CERTIFICATE_LATEST_MONTH_BACKTRACK)
                    set(Calendar.DAY_OF_MONTH, getActualMaximum(Calendar.DAY_OF_MONTH))
                }.time

                val contractEndBufferDate = Calendar.getInstance().apply {
                    time = contractEndDate
                    add(Calendar.MONTH, DocumentRequestConstants.CERTIFICATE_CONTRACT_END_BUFFER_IN_MONTHS)
                }.time

                maxDate = if (contractEndBufferDate.before(latestAllowedDate)) {
                    contractEndBufferDate.time
                } else {
                    latestAllowedDate.time
                }

                minDate = if (contractStartDate.before(earliestAllowedDate)) {
                    earliestAllowedDate.time
                } else {
                    contractStartDate.time
                }
            }
        }

        datePickerDialog.show()
    }
}