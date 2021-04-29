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
package com.barclays.absa.banking.account.ui

import android.content.Intent
import android.os.Bundle
import com.barclays.absa.banking.presentation.shared.IntentFactory
import kotlinx.android.synthetic.main.choose_dates_activity.*
import java.util.*

class AccountDateRangeActivity : ChooseDatesActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        intent.extras?.apply {
            val fromDate = getSerializable(IntentFactory.FROM_DATE) as Date
            val toDate = getSerializable(IntentFactory.TO_DATE) as Date

            statementDialogUtils.fromDate = fromDate
            statementDialogUtils.toDate = toDate
            updateFromAndToDate()
        }

        updateSelectedRange()

        downloadButton.setOnClickListener {
            setResult(RESULT_OK, Intent().apply {
                putExtra(IntentFactory.FROM_DATE, statementDialogUtils.fromDate)
                putExtra(IntentFactory.TO_DATE, statementDialogUtils.toDate)
            })
            finish()
        }
    }
}