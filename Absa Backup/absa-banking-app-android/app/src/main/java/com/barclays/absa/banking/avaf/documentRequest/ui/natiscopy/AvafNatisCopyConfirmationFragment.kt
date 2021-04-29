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

package com.barclays.absa.banking.avaf.documentRequest.ui.natiscopy

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import com.barclays.absa.banking.R
import com.barclays.absa.banking.avaf.documentRequest.ui.AvafDocumentRequestActivity
import com.barclays.absa.banking.avaf.ui.AvafConstants
import com.barclays.absa.banking.databinding.AvafNatisCopyRequestFragmentBinding
import com.barclays.absa.banking.express.documentRequest.natiscopy.AvafNatisCopyViewModel
import com.barclays.absa.banking.framework.BaseFragment
import com.barclays.absa.banking.newToBank.services.dto.ResultAnimations
import com.barclays.absa.utils.AnalyticsUtil
import com.barclays.absa.utils.DateTimeHelper
import com.barclays.absa.utils.extensions.viewBinding
import styleguide.screens.GenericResultScreenFragment
import styleguide.screens.GenericResultScreenProperties

class AvafNatisCopyConfirmationFragment : BaseFragment(R.layout.avaf_natis_copy_request_fragment) {
    private val binding by viewBinding(AvafNatisCopyRequestFragmentBinding::bind)
    private val natisCopyViewModel by viewModels<AvafNatisCopyViewModel>()
    lateinit var accountNumber: String

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        AnalyticsUtil.trackAction("AVAFCopyOfNaTIS_ConfirmRequestScreen_ScreenDisplayed")
        setToolBar(R.string.avaf_document_request_confirmation_title)
        accountNumber = activity?.intent?.getStringExtra(AvafDocumentRequestActivity.ACCOUNT_NUMBER) ?: ""
        arguments?.let {
            initViews(it)
            initListeners(it)
        } ?: run { showGenericErrorMessageThenFinish() }
    }

    private fun initViews(bundle: Bundle) {
        val navArgs = AvafNatisCopyConfirmationFragmentArgs.fromBundle(bundle)
        val date = DateTimeHelper.formatDate(navArgs.documentDetails.date)
        binding.emailPrimaryContentAndLabelView.setContentText(navArgs.documentDetails.email)
        binding.accountNumberSecondaryContentAndLabelView.setContentText(accountNumber)
        with(binding.documentDateSecondaryContentAndLabelView) {
            setContentText(date)
            visibility = View.VISIBLE
        }
    }

    private fun initListeners(bundle: Bundle) {
        binding.confirmButton.setOnClickListener {
            AnalyticsUtil.trackAction("AVAFCopyOfNaTIS_ConfirmRequestScreen_ConfirmButtonClicked")
            with(natisCopyViewModel) {
                val navArgs = AvafNatisCopyConfirmationFragmentArgs.fromBundle(bundle)
                requestDocument(navArgs.documentDetails, accountNumber)

                natisCopyRequestRequestLiveData.observe(viewLifecycleOwner, {
                    natisCopyRequestRequestLiveData.removeObservers(viewLifecycleOwner)
                    navigate(AvafNatisCopyConfirmationFragmentDirections.actionNatisCopyConfirmationFragmentToDocumentReqResultFragment(requestSuccessfulProperties()))
                })
            }
        }
    }

    private fun requestSuccessfulProperties(): GenericResultScreenProperties {
        with(baseActivity) {
            dismissProgressDialog()
            hideToolBar()
        }
        val navArgs = AvafNatisCopyConfirmationFragmentArgs.fromBundle(requireArguments())
        val successMessage = getString(R.string.avaf_document_request_result_success_message, getString(R.string.avaf_document_request_electronic_copy_of_natis_document), navArgs.documentDetails.email, DateTimeHelper.formatDate(navArgs.documentDetails.date))
        GenericResultScreenFragment.setPrimaryButtonOnClick {
            AnalyticsUtil.trackAction("AVAFCopyOfNaTIS_RequestSubmittedSuccessfullyScreen_DoneButtonClicked")
            with(Intent()) {
                putExtra(AvafConstants.IS_DOCUMENT_REQUEST_SUCCESSFUL, true)
                activity?.setResult(Activity.RESULT_OK, this)
                activity?.finish()
            }
        }

        AnalyticsUtil.trackAction("AVAFCopyOfNaTIS_RequestSubmittedSuccessfullyScreen_ScreenDisplayed")
        return GenericResultScreenProperties.PropertiesBuilder()
                .setResultScreenAnimation(ResultAnimations.generalSuccess)
                .setTitle(getString(R.string.avaf_document_request_result_success_title))
                .setDescription(successMessage)
                .setPrimaryButtonLabel(getString(R.string.done))
                .setShouldAnimateOnlyOnce(true)
                .build(true)
    }
}