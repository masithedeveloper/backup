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
package com.barclays.absa.banking.paymentsRewrite.ui

import android.os.Bundle
import android.text.style.ClickableSpan
import android.view.View
import com.barclays.absa.banking.R
import com.barclays.absa.banking.boundary.model.CustomerProfileObject
import com.barclays.absa.banking.databinding.ImportantNoticeFragmentBinding
import com.barclays.absa.utils.PdfUtil.showCutOffTimesPdf
import com.barclays.absa.utils.PdfUtil.showTermsAndConditionsClientAgreement
import com.barclays.absa.utils.extensions.viewBinding
import styleguide.utils.TextFormattingUtils

class ImportantNoticeFragment : PaymentsBaseFragment(R.layout.important_notice_fragment) {
    private val binding by viewBinding(ImportantNoticeFragmentBinding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        showToolBar()
        setToolBar(getString(R.string.important_notice_heading))

        TextFormattingUtils.makeTextClickable(binding.termsConditionsTextView, R.color.color_FF666666, getString(R.string.payment_accept_personal_client_agreement), getString(R.string.personal_client_agreement), object : ClickableSpan() {
            override fun onClick(widget: View) {
                showTermsAndConditionsClientAgreement(baseActivity, CustomerProfileObject.instance.clientTypeGroup)
            }
        })

        with(binding) {
            termsConditionsTextView.visibility = if (paymentsViewModel.hasIIPBeneficiary) View.GONE else View.VISIBLE
            cutOffTimesButton.setOnClickListener { showCutOffTimesPdf(baseActivity) }
            doneButton.setOnClickListener { activity?.onBackPressed() }
        }
    }
}