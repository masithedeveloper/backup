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

object SuspendReason {

    lateinit var cancellingMandate: Pair<String, String>
    lateinit var mandateEnded: Pair<String, String>
    lateinit var mandateChanged: Pair<String, String>
    lateinit var mandateNotAuthorised: Pair<String, String>

    fun initializeReasons(context: Context) {
        cancellingMandate = Pair("CTCA", context.getString(R.string.debicheck_reason_contract_cancel))
        mandateEnded = Pair("CTEX", context.getString(R.string.debicheck_reason_contract_expired))
        mandateChanged = Pair("CTAM", context.getString(R.string.debicheck_reason_contract_ammended))
        mandateNotAuthorised = Pair("CTCA", context.getString(R.string.debicheck_reason_contract_authorise))
    }

    fun codeFromReason(reason: String): String {
        return when (reason) {
            cancellingMandate.second -> cancellingMandate.first
            mandateEnded.second -> mandateEnded.first
            mandateChanged.second -> mandateChanged.first
            else -> mandateNotAuthorised.first
        }
    }
}