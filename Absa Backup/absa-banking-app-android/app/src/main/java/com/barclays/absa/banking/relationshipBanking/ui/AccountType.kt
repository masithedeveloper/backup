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

package com.barclays.absa.banking.relationshipBanking.ui

enum class AccountType(val key: String) {
    PERSONAL_ACCOUNT("Personal account"),
    BUSINESS_ACCOUNT("Business account"),
    STUDENT_ACCOUNT("Student account");

    companion object {
        val accountTypes: List<String>
            get() = values().map { it.key }
    }
}