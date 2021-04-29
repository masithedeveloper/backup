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

package com.barclays.absa.banking.shared

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import com.barclays.absa.banking.R
import com.barclays.absa.banking.express.shared.getDocumentDetails.DocumentDetailsViewModel
import com.barclays.absa.banking.express.shared.getDocumentDetails.dto.DocumentDetailsRequest
import com.barclays.absa.banking.framework.BaseFragment
import com.barclays.absa.utils.AnalyticsUtil.trackAction
import kotlinx.android.synthetic.main.pdf_viewer.view.*
import kotlinx.android.synthetic.main.terms_and_conditions_fragment.*
import styleguide.utils.extensions.removeSpaces

class TermsAndConditionsFragment : BaseFragment(R.layout.terms_and_conditions_fragment) {

    private val documentDetailsViewModel by viewModels<DocumentDetailsViewModel>()
    private lateinit var termsAndConditionsInfo: TermsAndConditionsInfo

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        termsAndConditionsInfo = arguments?.getParcelable(TermsAndConditionsInfo::class.java.simpleName) ?: TermsAndConditionsInfo()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setToolBar(R.string.depositor_plus_terms_and_conditions)
        viewLifecycleOwner.lifecycle.addObserver(termsAndConditionsPDFViewer)

        termsAndConditionsCheckBoxView.visibility = if (termsAndConditionsInfo.shouldDisplayCheckBox) View.VISIBLE else View.GONE

        if (!termsAndConditionsInfo.shouldDisplayCheckBox) {
            nextButton.text = getString(R.string.done)
        }

        if (termsAndConditionsInfo.isDocFusionDocument) {
            with(termsAndConditionsInfo) {
                termsAndConditionsPDFViewer.downloadAndViewPdf(url, cacheKey)
            }
        } else {
            documentDetailsViewModel.fetchDocumentDetails(DocumentDetailsRequest(applicationType = termsAndConditionsInfo.applicationType, itemCode = termsAndConditionsInfo.productCode))
            documentDetailsViewModel.documentDetailsLiveData.observe(viewLifecycleOwner, {
                dismissProgressDialog()
                if (it.pdfDocument.isNotEmpty()) {
                    termsAndConditionsPDFViewer.viewPdf(it.pdfDocument, it.fileName)
                }
            })

            documentDetailsViewModel.failureLiveData.observe(viewLifecycleOwner, {
                dismissProgressDialog()
                BaseAlertDialog.showErrorAlertDialog(it.resultMessages.first().responseMessage)
            })
        }

        termsAndConditionsPDFViewer.shareImageView.setOnClickListener {
            trackAction(termsAndConditionsInfo.productName, "${termsAndConditionsInfo.productName.removeSpaces()}_TermsAndConditionsScreen_ShareOptionClicked")
            termsAndConditionsPDFViewer.sharePdf()
        }

        nextButton.setOnClickListener {
            with(it.findNavController()) {
                if (termsAndConditionsInfo.shouldDisplayCheckBox) {
                    when {
                        !termsAndConditionsCheckBoxView.isChecked -> termsAndConditionsCheckBoxView.setErrorMessage(R.string.please_accept_terms_and_conditions)
                        else -> navigate(termsAndConditionsInfo.destination)
                    }
                } else {
                    navigateUp()
                }
            }
        }
    }
}