/*
 * Copyright (c) 2021 Absa Bank Limited, All Rights Reserved.
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

package com.barclays.absa.banking.avaf.documentRequest.ui.loanAmortization

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import com.barclays.absa.banking.R
import com.barclays.absa.banking.avaf.documentRequest.ui.AvafDocumentRequestActivity
import com.barclays.absa.banking.avaf.documentRequest.ui.AvafDocumentRequestBaseFragment
import com.barclays.absa.banking.avaf.documentRequest.ui.DocumentRequestDetails
import com.barclays.absa.banking.avaf.documentRequest.ui.DocumentRequestType
import com.barclays.absa.banking.databinding.AvafDocumentRequestBaseFragmentBinding
import com.barclays.absa.banking.express.notificationDetails.NotificationDetailsViewModel
import com.barclays.absa.utils.ProfileManager
import com.barclays.absa.utils.extensions.viewBinding
import styleguide.utils.ValidationUtils

class AvafLoanAmortizationFragment : AvafDocumentRequestBaseFragment() {
    private val binding by viewBinding(AvafDocumentRequestBaseFragmentBinding::bind)
    private val notificationDetailsViewModel by viewModels<NotificationDetailsViewModel>()

    override var toolbarTitleResId: Int = R.string.avaf_amortisation_request
    override val analyticsScreen: String = ""

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        showToolBar()
    }

    override fun initViews() {
        binding.documentRequestTitleTextView.text = getString(R.string.avaf_document_request_please_confirm_email)
        binding.emailNormalInputView.setDescription(getString(R.string.avaf_loan_amortization_email_description))
        val accountNumber = activity?.intent?.getStringExtra(AvafDocumentRequestActivity.ACCOUNT_NUMBER) ?: ""
        notificationDetailsViewModel.fetchNotificationDetails(accountNumber, ProfileManager.getInstance().activeUserProfile.userNumber.toString())
        notificationDetailsViewModel.notificationDetailsLiveData.observe(viewLifecycleOwner, { notificationDetailsResponse ->
            dismissProgressDialog()
            if (notificationDetailsResponse.alertInfo.email.isNotBlank() && ValidationUtils.isValidEmailAddress(notificationDetailsResponse.alertInfo.email)) {
                binding.emailNormalInputView.text = notificationDetailsResponse.alertInfo.email
            }
        })
    }

    override fun setupListeners() {
        binding.continueButton.setOnClickListener {
            if (hasValidationErrors()) {
                return@setOnClickListener
            }

            val details = DocumentRequestDetails(DocumentRequestType.LOAN_AMORTIZATION, getEmail(), getDate())
            navigate(AvafLoanAmortizationFragmentDirections.actionAvafLoanAmortizationFragmentToAvafLoanAmortizationConfirmationFragment(details))
        }
    }
}