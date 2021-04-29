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

package com.barclays.absa.banking.bankConfirmationLetter.ui

import android.os.Bundle
import com.barclays.absa.banking.R
import com.barclays.absa.banking.framework.BaseActivity

class BankConfirmationLetterActivity : BaseActivity(R.layout.activity_bank_confirmation_letter) {

    companion object {
        const val BANK_CONFIRMATION_LETTER_ANALYTIC_TAG = "BankConfirmationLetter"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setToolBar(R.string.bank_confirmation_letter_select_account, null)
    }
}