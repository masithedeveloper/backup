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

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import com.barclays.absa.banking.R
import com.barclays.absa.banking.avaf.documentRequest.ui.AvafDocumentRequestActivity
import com.barclays.absa.banking.avaf.ui.AvafConstants
import com.barclays.absa.banking.databinding.AvafDocumentRequestConfirmationFragmentBinding
import com.barclays.absa.banking.express.documentRequest.statements.AvafStatementsViewModel
import com.barclays.absa.banking.framework.BaseFragment
import com.barclays.absa.banking.newToBank.services.dto.ResultAnimations
import com.barclays.absa.utils.AnalyticsUtil
import com.barclays.absa.utils.DateTimeHelper
import com.barclays.absa.utils.extensions.viewBinding
import styleguide.screens.GenericResultScreenFragment
import styleguide.screens.GenericResultScreenProperties

class AvafCertificateConfirmationFragment : BaseFragment(R.layout.avaf_document_request_confirmation_fragment) {
    private val binding by viewBinding(AvafDocumentRequestConfirmationFragmentBinding::bind)
    private val statementsViewModel by viewModels<AvafStatementsViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        AnalyticsUtil.trackAction("AVAFAuditTaxCertificate_ConfirmRequestScreen_ScreenDisplayed")
        setToolBar(R.string.avaf_document_request_confirmation_title)
        arguments?.let {
            initViews(it)
            initListeners(it)
        } ?: run { showGenericErrorMessageThenFinish() }
    }

    private fun initViews(bundle: Bundle) {
        val args = AvafCertificateConfirmationFragmentArgs.fromBundle(bundle)
        val date = DateTimeHelper.formatDate(args.documentDetails.date)
        binding.emailPrimaryContentAndLabelView.setContentText(args.documentDetails.email)
        binding.requestTypeSecondaryContentAndLabelView.setContentText(getString(R.string.avaf_document_request_tax_certificate_title))
        binding.documentDateSecondaryContentAndLabelView.apply {
            setContentText(date)
            visibility = View.VISIBLE
            setLabelText(getString(R.string.avaf_document_request_tax_certificate_period))
        }
    }

    private fun initListeners(bundle: Bundle) {
        binding.confirmButton.setOnClickListener {
            AnalyticsUtil.trackAction("AVAFAuditTaxCertificate_ConfirmRequestScreen_ConfirmButtonClicked")
            val accountNumber = activity?.intent?.getStringExtra(AvafDocumentRequestActivity.ACCOUNT_NUMBER) ?: ""
            with(statementsViewModel) {
                val args = AvafCertificateConfirmationFragmentArgs.fromBundle(bundle)
                requestDocument(args.documentDetails, accountNumber)
                documentRequestRequestLiveData.observe(viewLifecycleOwner, {
                    documentRequestRequestLiveData.removeObservers(viewLifecycleOwner)
                    navigate(AvafCertificateConfirmationFragmentDirections.actionCertificateConfirmationFragmentToDocumentReqResultFragment(requestSuccessfulProperties()))
                })
            }
        }
    }

    private fun requestSuccessfulProperties(): GenericResultScreenProperties {
        dismissProgressDialog()
        hideToolBar()
        GenericResultScreenFragment.setPrimaryButtonOnClick {
            AnalyticsUtil.trackAction("AVAFAuditTaxCertificate_RequestSubmittedSuccessfullyScreen_DoneButtonClicked")
            with(Intent()) {
                putExtra(AvafConstants.IS_DOCUMENT_REQUEST_SUCCESSFUL, true)
                activity?.setResult(Activity.RESULT_OK, this)
                activity?.finish()
            }
        }

        val args = AvafCertificateConfirmationFragmentArgs.fromBundle(requireArguments())
        val successMessage = getString(R.string.avaf_document_request_result_success_message, getString(R.string.avaf_document_request_tax_certificate_title), args.documentDetails.email, DateTimeHelper.formatDate(args.documentDetails.date))
        AnalyticsUtil.trackAction("AVAFAuditTaxCertificate_RequestSubmittedSuccessfullyScreen_ScreenDisplayed")
        return GenericResultScreenProperties.PropertiesBuilder()
                .setResultScreenAnimation(ResultAnimations.generalSuccess)
                .setTitle(getString(R.string.avaf_document_request_result_success_title))
                .setDescription(successMessage)
                .setPrimaryButtonLabel(getString(R.string.done))
                .setShouldAnimateOnlyOnce(true)
                .build(true)
    }
}