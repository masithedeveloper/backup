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

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import com.barclays.absa.banking.R
import com.barclays.absa.banking.avaf.documentRequest.ui.AvafDocumentRequestActivity
import com.barclays.absa.banking.avaf.ui.AvafConstants
import com.barclays.absa.banking.databinding.AvafCrossBorderConfirmationFragmentBinding
import com.barclays.absa.banking.express.documentRequest.crossborder.AvafCrossborderViewModel
import com.barclays.absa.banking.express.documentRequest.crossborder.dto.CrossborderDetails
import com.barclays.absa.banking.framework.BaseFragment
import com.barclays.absa.banking.newToBank.services.dto.ResultAnimations
import com.barclays.absa.utils.AnalyticsUtil
import com.barclays.absa.utils.DateTimeHelper
import com.barclays.absa.utils.DateTimeHelper.SPACED_PATTERN_DD_MMMM_YYYY
import com.barclays.absa.utils.DateUtils
import com.barclays.absa.utils.extensions.viewBinding
import styleguide.screens.GenericResultScreenFragment
import styleguide.screens.GenericResultScreenProperties
import java.util.*

class AvafCrossBorderConfirmationFragment : BaseFragment(R.layout.avaf_cross_border_confirmation_fragment) {
    private val binding by viewBinding(AvafCrossBorderConfirmationFragmentBinding::bind)
    private val crossborderViewModel by viewModels<AvafCrossborderViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        AnalyticsUtil.trackAction("AVAFCrossBorderLetterRequest_ConfirmRequestScreen_ScreenDisplayed")
        setToolBar(R.string.avaf_document_request_confirmation_title)
        initViews()
        initListeners()
    }

    private fun initViews() {
        arguments?.let {
            val args = AvafCrossBorderConfirmationFragmentArgs.fromBundle(it)
            val dateFrom = DateTimeHelper.formatDate(args.dateFrom, SPACED_PATTERN_DD_MMMM_YYYY)
            val dateTo = DateTimeHelper.formatDate(args.dateTo, SPACED_PATTERN_DD_MMMM_YYYY)
            with(binding) {
                emailPrimaryContentAndLabelView.setContentText(args.email)
                documentDateFromSecondaryContentAndLabelView.setContentText(dateFrom)
                documentDateToSecondaryContentAndLabelView.setContentText(dateTo)
                driverNameSecondaryContentAndLabelView.setContentText(args.name)
                driverIdSecondaryContentAndLabelView.setContentText(args.identification)
            }
        }
    }

    private fun initListeners() {
        binding.confirmButton.setOnClickListener {
            AnalyticsUtil.trackAction("AVAFCrossBorderLetterRequest_ConfirmRequestScreen_ConfirmButtonClicked")
            arguments?.let {
                val accountNumber = activity?.intent?.getStringExtra(AvafDocumentRequestActivity.ACCOUNT_NUMBER) ?: ""
                val args = AvafCrossBorderConfirmationFragmentArgs.fromBundle(it)
                val details = CrossborderDetails(args.name, args.identification, args.dateFrom, args.dateTo, args.email, accountNumber)
                with(crossborderViewModel) {
                    requestDocument(details)

                    documentRequestLiveData.observe(viewLifecycleOwner, {
                        documentRequestLiveData.removeObservers(viewLifecycleOwner)
                        navigate(AvafCrossBorderConfirmationFragmentDirections.actionCrossBorderConfirmationFragmentToDocumentReqResultFragment(requestSuccessfulProperties()))
                    })
                }
            }
        }
    }

    private fun requestSuccessfulProperties(): GenericResultScreenProperties {
        dismissProgressDialog()
        hideToolBar()
        val args = AvafCrossBorderConfirmationFragmentArgs.fromBundle(requireArguments())
        val successMessage = getString(R.string.avaf_document_request_cross_border_letter_result_success_message, args.email, DateUtils.format(Date(), DateUtils.DASHED_DATE_PATTERN))
        GenericResultScreenFragment.setPrimaryButtonOnClick {
            AnalyticsUtil.trackAction("AVAFCrossBorderLetterRequest_RequestSubmittedSuccessfullyScreen_DoneButtonClicked")
            with(Intent()) {
                putExtra(AvafConstants.IS_DOCUMENT_REQUEST_SUCCESSFUL, true)
                activity?.setResult(Activity.RESULT_OK, this)
                activity?.finish()
            }
        }

        AnalyticsUtil.trackAction("AVAFCrossBorderLetterRequest_RequestSubmittedSuccessfullyScreen_ScreenDisplayed")
        return GenericResultScreenProperties.PropertiesBuilder()
                .setResultScreenAnimation(ResultAnimations.generalSuccess)
                .setTitle(getString(R.string.avaf_document_request_result_success_title))
                .setDescription(successMessage)
                .setPrimaryButtonLabel(getString(R.string.done))
                .setShouldAnimateOnlyOnce(true)
                .build(true)
    }
}