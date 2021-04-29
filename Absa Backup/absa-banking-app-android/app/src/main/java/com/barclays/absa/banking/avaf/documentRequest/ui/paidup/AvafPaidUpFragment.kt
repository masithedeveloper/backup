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
 */

package com.barclays.absa.banking.avaf.documentRequest.ui.paidup

import com.barclays.absa.banking.R
import com.barclays.absa.banking.avaf.documentRequest.ui.AvafDocumentRequestBaseFragment
import com.barclays.absa.banking.avaf.documentRequest.ui.DocumentRequestDetails
import com.barclays.absa.banking.avaf.documentRequest.ui.DocumentRequestType
import com.barclays.absa.banking.databinding.AvafDocumentRequestBaseFragmentBinding
import com.barclays.absa.utils.AnalyticsUtil
import com.barclays.absa.utils.extensions.viewBinding

class AvafPaidUpFragment : AvafDocumentRequestBaseFragment() {
    private val binding by viewBinding(AvafDocumentRequestBaseFragmentBinding::bind)
    override var toolbarTitleResId: Int = R.string.avaf_document_request_paidup_title
    override val analyticsScreen: String = "AVAFPaidUpLetterRequest_PaidUpLetterRequestScreen_ScreenDisplayed"

    override fun initViews() {
        binding.documentRequestTitleTextView.text = getString(R.string.avaf_document_request_please_confirm_email)
    }

    override fun setupListeners() {
        binding.continueButton.setOnClickListener {
            AnalyticsUtil.trackAction("AVAFPaidUpLetterRequest_PaidUpLetterRequestScreen_ContinueButtonClicked")
            if (hasValidationErrors()) {
                return@setOnClickListener
            }

            val details = DocumentRequestDetails(DocumentRequestType.PAID_UP, getEmail(), getDate())
            val action = AvafPaidUpFragmentDirections.actionPaidUpFragmentToPaidUpConfirmationFragment(details)
            navigate(action)
        }
    }
}