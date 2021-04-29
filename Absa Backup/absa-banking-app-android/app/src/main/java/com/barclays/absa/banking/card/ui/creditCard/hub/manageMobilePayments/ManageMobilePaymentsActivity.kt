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
package com.barclays.absa.banking.card.ui.creditCard.hub.manageMobilePayments

import android.os.Bundle
import com.barclays.absa.banking.R
import com.barclays.absa.banking.framework.BaseActivity
import com.barclays.absa.utils.viewModel

class ManageMobilePaymentsActivity : BaseActivity(R.layout.manage_mobile_payments_activity), ManageMobilePaymentsInterface {

    companion object {
        const val CARD_NUMBER = "CARD_NUMBER"
    }

    private lateinit var manageMobilePaymentsViewModel: ManageMobilePaymentsViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setToolBarBack(R.string.mobile_payments_title_mobile_payments)
        manageMobilePaymentsViewModel = viewModel()
        manageMobilePaymentsViewModel.cardNumber = intent.getStringExtra(CARD_NUMBER) ?: ""
    }

    override fun manageMobilePaymentsViewModel(): ManageMobilePaymentsViewModel = viewModel()
}