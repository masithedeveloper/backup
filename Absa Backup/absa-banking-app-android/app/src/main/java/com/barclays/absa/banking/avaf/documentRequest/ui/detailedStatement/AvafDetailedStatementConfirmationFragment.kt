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
 */

package com.barclays.absa.banking.avaf.documentRequest.ui.detailedStatement

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
import com.barclays.absa.banking.express.documentRequest.statements.AvafStatementsViewModel
import com.barclays.absa.banking.framework.BaseFragment
import com.barclays.absa.banking.newToBank.services.dto.ResultAnimations
import com.barclays.absa.utils.AnalyticsUtil
import com.barclays.absa.utils.DateTimeHelper
import com.barclays.absa.utils.extensions.viewBinding
import styleguide.screens.GenericResultScreenFragment
import styleguide.screens.GenericResultScreenProperties

class AvafDetailedStatementConfirmationFragment : BaseFragment(R.layout.avaf_document_request_confirmation_fragment) {
    private val statementsViewModel by viewModels<AvafStatementsViewModel>()
    private val binding by viewBinding(AvafDocumentRequestConfirmationFragmentBinding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setToolBar(R.string.avaf_document_request_confirmation_title)
        arguments?.let {
            initViews(it)
            initListeners(it)
        } ?: run { showGenericErrorMessageThenFinish() }
    }

    private fun initViews(bundle: Bundle) {
        val navArgs = AvafDetailedStatementConfirmationFragmentArgs.fromBundle(bundle)
        val date = DateTimeHelper.formatDate(navArgs.documentRequestDetails.date)
        binding.emailPrimaryContentAndLabelView.setContentText(navArgs.documentRequestDetails.email)
        binding.documentDateSecondaryContentAndLabelView.setContentText(date)
        binding.documentDateSecondaryContentAndLabelView.visibility = VISIBLE
        binding.requestTypeSecondaryContentAndLabelView.setContentText(getString(R.string.avaf_detailed_statement))
    }

    private fun initListeners(bundle: Bundle) {
        binding.confirmButton.setOnClickListener {
            val navArgs = AvafDetailedStatementConfirmationFragmentArgs.fromBundle(bundle)
            val accountNumber = activity?.intent?.getStringExtra(AvafDocumentRequestActivity.ACCOUNT_NUMBER) ?: ""
            statementsViewModel.requestDocument(navArgs.documentRequestDetails, accountNumber)
            statementsViewModel.documentRequestRequestLiveData.observe(viewLifecycleOwner, {
                statementsViewModel.documentRequestRequestLiveData.removeObservers(viewLifecycleOwner)
                dismissProgressDialog()
                hideToolBar()
                navigate(AvafDetailedStatementConfirmationFragmentDirections.actionAvafDetailedStatementConfirmationFragmentToDocumentReqResultFragment(requestSuccessfulProperties()))
            })

            statementsViewModel.failureLiveData.observe(viewLifecycleOwner, {
                statementsViewModel.failureLiveData.removeObservers(viewLifecycleOwner)
                dismissProgressDialog()
                hideToolBar()
                navigate(AvafDetailedStatementConfirmationFragmentDirections.actionAvafDetailedStatementConfirmationFragmentToDocumentReqResultFragment(requestFailureProperties()))
            })
        }
    }

    private fun requestSuccessfulProperties(): GenericResultScreenProperties {
        val navArgs = AvafDetailedStatementConfirmationFragmentArgs.fromBundle(requireArguments())
        GenericResultScreenFragment.setPrimaryButtonOnClick {
            with(Intent()) {
                putExtra(AvafConstants.IS_DOCUMENT_REQUEST_SUCCESSFUL, true)
                activity?.setResult(Activity.RESULT_OK, this)
                activity?.finish()
            }
        }

        return GenericResultScreenProperties.PropertiesBuilder()
                .setResultScreenAnimation(ResultAnimations.generalSuccess)
                .setTitle(getString(R.string.avaf_document_request_result_success_title))
                .setDescription(getString(R.string.avaf_document_request_statement_result_success_message, navArgs.documentRequestDetails.email, DateTimeHelper.formatDate(navArgs.documentRequestDetails.date)))
                .setPrimaryButtonLabel(getString(R.string.done))
                .setShouldAnimateOnlyOnce(true)
                .build(true)
    }

    private fun requestFailureProperties(): GenericResultScreenProperties {
        GenericResultScreenFragment.setPrimaryButtonOnClick {
            with(Intent()) {
                AnalyticsUtil.trackAction("DetailedStatement_SubmittedSuccessfullyScreen_DoneButtonClicked")
                putExtra(AvafConstants.IS_DOCUMENT_REQUEST_SUCCESSFUL, false)
                activity?.setResult(Activity.RESULT_CANCELED, this)
                activity?.finish()
            }
        }

        return GenericResultScreenProperties.PropertiesBuilder()
                .setResultScreenAnimation(ResultAnimations.generalError)
                .setTitle(getString(R.string.something_went_wrong))
                .setDescription(getString(R.string.avaf_failure_message))
                .setContactViewContactName(getString(R.string.avaf_contact_centre))
                .setContactViewContactNumber(getString(R.string.avaf_contact_number))
                .setPrimaryButtonLabel(getString(R.string.avaf_try_again_later))
                .setShouldAnimateOnlyOnce(true)
                .build(true)
    }
}