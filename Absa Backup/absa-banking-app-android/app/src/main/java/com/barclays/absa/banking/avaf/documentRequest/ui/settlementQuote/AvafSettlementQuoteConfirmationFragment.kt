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

package com.barclays.absa.banking.avaf.documentRequest.ui.settlementQuote

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.View.VISIBLE
import androidx.fragment.app.viewModels
import com.barclays.absa.banking.R
import com.barclays.absa.banking.avaf.documentRequest.ui.AvafDocumentRequestActivity
import com.barclays.absa.banking.avaf.ui.AvafConstants
import com.barclays.absa.banking.databinding.AvafDocumentRequestConfirmationFragmentBinding
import com.barclays.absa.banking.express.documentRequest.settlementQuote.AvafSettlementQuoteViewModel
import com.barclays.absa.banking.framework.BaseFragment
import com.barclays.absa.banking.newToBank.services.dto.ResultAnimations
import com.barclays.absa.utils.AnalyticsUtil
import com.barclays.absa.utils.DateTimeHelper
import com.barclays.absa.utils.DateUtils
import com.barclays.absa.utils.DateUtils.DATE_DISPLAY_PATTERN_FULL
import com.barclays.absa.utils.extensions.viewBinding
import styleguide.screens.GenericResultScreenFragment
import styleguide.screens.GenericResultScreenProperties

class AvafSettlementQuoteConfirmationFragment : BaseFragment(R.layout.avaf_document_request_confirmation_fragment) {
    private val binding by viewBinding(AvafDocumentRequestConfirmationFragmentBinding::bind)
    private val viewModel by viewModels<AvafSettlementQuoteViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        AnalyticsUtil.trackAction("AVAFSettlementQuoteRequest_ConfirmRequestScreen_ScreenDisplayed")
        setToolBar(R.string.avaf_document_request_confirmation_title)
        arguments?.let {
            initViews(it)
            initListeners(it)
        } ?: run { showGenericErrorMessageThenFinish() }
    }

    private fun initViews(bundle: Bundle) {
        val navArgs = AvafSettlementQuoteConfirmationFragmentArgs.fromBundle(bundle)
        val date = DateTimeHelper.formatDate(navArgs.documentRequestDetails.date)
        with(binding) {
            emailPrimaryContentAndLabelView.setContentText(navArgs.documentRequestDetails.email)
            documentDateSecondaryContentAndLabelView.setContentText(date)
            documentDateSecondaryContentAndLabelView.visibility = VISIBLE
            requestTypeSecondaryContentAndLabelView.setContentText(getString(R.string.avaf_document_request_settlement_quotation))
        }
    }

    private fun initListeners(bundle: Bundle) {
        binding.confirmButton.setOnClickListener {
            AnalyticsUtil.trackAction("AVAFSettlementQuoteRequest_ConfirmRequestScreen_ConfirmButtonClicked")
            val accountNumber = activity?.intent?.getStringExtra(AvafDocumentRequestActivity.ACCOUNT_NUMBER) ?: ""
            viewModel.requestDocument(AvafSettlementQuoteConfirmationFragmentArgs.fromBundle(bundle).documentRequestDetails.email, accountNumber)
            viewModel.documentRequestRequestLiveData.observe(viewLifecycleOwner, {
                viewModel.documentRequestRequestLiveData.removeObservers(viewLifecycleOwner)
                navigate(AvafSettlementQuoteConfirmationFragmentDirections.actionSettlementQuoteConfirmationFragmentToDocumentReqResultFragment(requestSuccessfulProperties()))
            })
        }
    }

    private fun requestSuccessfulProperties(): GenericResultScreenProperties {
        dismissProgressDialog()
        hideToolBar()
        val navArgs = AvafSettlementQuoteConfirmationFragmentArgs.fromBundle(requireArguments())
        GenericResultScreenFragment.setPrimaryButtonOnClick {
            AnalyticsUtil.trackAction("AVAFSettlementQuoteRequest_RequestSubmittedSuccessfullyScreen_DoneButtonClicked")
            with(Intent()) {
                putExtra(AvafConstants.IS_DOCUMENT_REQUEST_SUCCESSFUL, true)
                activity?.setResult(Activity.RESULT_OK, this)
                activity?.finish()
            }
        }

        AnalyticsUtil.trackAction("AVAFSettlementQuoteRequest_RequestSubmittedSuccessfullyScreen_ScreenDisplayed")
        return GenericResultScreenProperties.PropertiesBuilder()
                .setResultScreenAnimation(ResultAnimations.generalSuccess)
                .setTitle(getString(R.string.avaf_document_request_result_success_title))
                .setDescription(getString(R.string.avaf_document_request_result_success_message, getString(R.string.avaf_settlement_quote), navArgs.documentRequestDetails.email, DateTimeHelper.formatDate(navArgs.documentRequestDetails.date)))
                .setPrimaryButtonLabel(getString(R.string.done))
                .setShouldAnimateOnlyOnce(true)
                .build(true)
    }
}