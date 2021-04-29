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

package com.barclays.absa.banking.debiCheck.services.dto

import android.content.Context
import com.barclays.absa.banking.R

object DisputeReason {

    lateinit var incorrectDebitDay: Pair<String, String>
    lateinit var incorrectAmount: Pair<String, String>
    lateinit var didNotAgree: Pair<String, String>

    fun initializeReasons(context: Context) {
        incorrectDebitDay = Pair("DT01", context.getString(R.string.debicheck_dispute_incorrect_debit_day))
        incorrectAmount = Pair("AM02", context.getString(R.string.debicheck_dispute_incorrect_amount))
        didNotAgree = Pair("NWIA", context.getString(R.string.debicheck_dispute_not_what_i_agreed_to))
    }

    fun codeFromReason(reason: String): String {
        return when (reason) {
            incorrectDebitDay.second -> incorrectDebitDay.first
            incorrectAmount.second -> incorrectAmount.first
            else -> didNotAgree.first
        }
    }

    fun reasonFromCode(code: String): String {
        return when (code) {
            incorrectDebitDay.first -> incorrectDebitDay.second
            incorrectAmount.first -> incorrectAmount.second
            else -> didNotAgree.second
        }
    }
}