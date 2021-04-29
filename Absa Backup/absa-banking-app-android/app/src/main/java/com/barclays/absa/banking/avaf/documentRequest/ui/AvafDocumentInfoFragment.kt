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

package com.barclays.absa.banking.avaf.documentRequest.ui

import android.os.Bundle
import android.view.View
import com.barclays.absa.banking.R
import com.barclays.absa.banking.databinding.AvafDocumentInfoFragmentBinding
import com.barclays.absa.banking.framework.BaseFragment
import com.barclays.absa.utils.AnalyticsUtil
import com.barclays.absa.utils.extensions.viewBinding

class AvafDocumentInfoFragment : BaseFragment(R.layout.avaf_document_info_fragment) {
    private val binding by viewBinding(AvafDocumentInfoFragmentBinding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        arguments?.let {
            val offerInfo = AvafDocumentInfoFragmentArgs.fromBundle(it).offerInfo
            initViews(offerInfo)
            setUpListener(offerInfo)
            setUpToolbar(offerInfo)

            if (offerInfo.type == DocumentRequestType.LOAN_AMORTIZATION) {
                AnalyticsUtil.trackAction("AmortisationSchedule_AmortisationScheduleRequestScreen_ScreenDisplay")
            } else if (offerInfo.type == DocumentRequestType.DETAILED_STATEMENT) {
                AnalyticsUtil.trackAction("DetailedStatement_RequestDetailedStatementScreen_ScreenDisplayed")
            }
        }
    }

    private fun initViews(info: AvafOfferInfo) {
        with(binding) {
            navToolbar.title = info.headerTitle
            headerTextView.text = info.offerHeader
            offerTitleAndDescriptionView.title = info.offerTitle
            offerTitleAndDescriptionView.description = info.offerSubTitle
            descriptionBodyTextView.text = info.offerDescription
            closingNoteTextView.text = info.offerClosingText
            requestButton.text = info.acceptText
        }
    }

    private fun setUpToolbar(info: AvafOfferInfo) {
        with(baseActivity) {
            setToolBar("")
            hideToolBar()
            binding.navToolbar.setNavigationOnClickListener {
                if (info.type == DocumentRequestType.LOAN_AMORTIZATION) {
                    AnalyticsUtil.trackAction("AmortisationSchedule_RequestAmortisationScreen_BackButtonClicked")
                }

                onBackPressed()
            }
            binding.navToolbar.setNavigationIcon(R.drawable.ic_arrow_back_white)
        }
    }

    private fun setUpListener(offerInfo: AvafOfferInfo) {
        binding.requestButton.setOnClickListener {
            offerInfo.acceptFunction.invoke()
        }
    }
}