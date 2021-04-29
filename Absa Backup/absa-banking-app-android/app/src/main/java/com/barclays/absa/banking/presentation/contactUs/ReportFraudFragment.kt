/*
 * Copyright (c) 2019 Absa Bank Limited, All Rights Reserved.
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
package com.barclays.absa.banking.presentation.contactUs

import android.os.Bundle
import android.view.View
import com.barclays.absa.banking.R
import com.barclays.absa.banking.databinding.ReportFraudFragmentBinding
import com.barclays.absa.banking.framework.AbsaBaseFragment

class ReportFraudFragment : AbsaBaseFragment<ReportFraudFragmentBinding>() {

    override fun getLayoutResourceId(): Int = R.layout.report_fraud_fragment

    private lateinit var reportFraudView: ContactUsContracts.ReportFraudView

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        reportFraudView = activity as ContactUsContracts.ReportFraudView
        initViews()
    }

    private fun initViews() {
        binding.apply {
            contactUsOptionActionButtonView.setOnClickListener { reportFraudView.navigateToContactUsFragment() }
            callMeBackButton.setOnClickListener { reportFraudView.showConfirmCallDialog() }
        }
    }
}