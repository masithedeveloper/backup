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

object RejectReason {
    lateinit var noneOfTheseReasons: Pair<String, String>
    lateinit var unknownCreditor: Pair<String, String>
    lateinit var incorrectDay: Pair<String, String>

    fun initializeReasons(context: Context) {
        noneOfTheseReasons = Pair("MS02", context.getString(R.string.debicheck_unspecified_reason))
        unknownCreditor = Pair("BE05", context.getString(R.string.debicheck_unknown_creditor))
        incorrectDay = Pair("DT01", context.getString(R.string.debicheck_wrong_collection_day))
    }

    fun codeFromReason(reason: String): String {
        return when (reason) {
            noneOfTheseReasons.second -> noneOfTheseReasons.first
            unknownCreditor.second -> unknownCreditor.first
            else -> incorrectDay.first
        }
    }
}