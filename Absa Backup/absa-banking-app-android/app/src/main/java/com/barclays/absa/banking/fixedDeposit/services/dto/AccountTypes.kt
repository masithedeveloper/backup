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

package com.barclays.absa.banking.fixedDeposit.services.dto

import com.barclays.absa.banking.shared.BaseModel

class AccountTypes : BaseModel {
    val accountType: String? = ""
    val description: String? = ""
    val code: String? = ""

    override fun equals(other: Any?): Boolean {
        return accountType != null && this.accountType == other.toString()
    }

    override fun hashCode(): Int {
        var result = accountType?.hashCode() ?: 0
        result = 31 * result + (description?.hashCode() ?: 0)
        result = 31 * result + (code?.hashCode() ?: 0)
        return result
    }
}
