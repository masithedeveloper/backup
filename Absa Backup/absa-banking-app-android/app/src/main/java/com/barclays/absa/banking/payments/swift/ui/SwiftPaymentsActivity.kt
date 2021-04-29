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
package com.barclays.absa.banking.payments.swift.ui

import android.os.Bundle
import com.barclays.absa.banking.R
import com.barclays.absa.banking.framework.BaseActivity
import com.barclays.absa.banking.payments.swift.services.response.dto.SwiftTransactionPending
import com.barclays.absa.utils.viewModel
import kotlinx.android.synthetic.main.swift_payments_activity.*

class SwiftPaymentsActivity : BaseActivity(R.layout.swift_payments_activity) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val swiftPaymentViewModel = viewModel<SwiftPaymentsViewModel>()
        intent.getParcelableExtra<SwiftTransactionPending>(SwiftTransactionsActivity.TRANSACTION_DETAIL_KEY)?.let {
            swiftPaymentViewModel.swiftTransaction.value = it
        }
    }

    fun setProgressIndicatorVisibility(visibility: Int) {
        progressIndicatorView.visibility = visibility
    }

    fun setProgressStep(step: Int) {
        progressIndicatorView.setNextStep(step)
        progressIndicatorView.animateNextStep()
    }
}