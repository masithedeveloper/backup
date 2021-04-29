/*
 * Copyright (c) 2018 Absa Bank Limited, All Rights Reserved.
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
package com.barclays.absa.utils

import com.barclays.absa.banking.boundary.model.AccessPrivileges.Companion.instance
import com.barclays.absa.banking.boundary.model.AccountObject

object OperatorPermissionUtils {
    @JvmStatic
    fun canViewBalances(accountObject: AccountObject?): Boolean {
        return isMainUser() || "N".equals(accountObject?.isBalanceMasked, ignoreCase = true)
    }

    @JvmStatic
    fun canViewTransactions(accountObject: AccountObject?): Boolean {
        return isMainUser() || accountObject?.isAccountDetailAllowed == true
    }

    @JvmStatic
    fun isMainUser(): Boolean = !instance.isOperator

    fun isOperator(): Boolean = instance.isOperator
}