/*
 *  Copyright (c) 2019 Absa Bank Limited, All Rights Reserved.
 *
 *  This code is confidential to Absa Bank Limited and shall not be disclosed
 *  outside the Bank without the prior written permission of the Absa Legal
 *
 *  In the event that such disclosure is permitted the code shall not be copied
 *  or distributed other than on a need-to-know basis and any recipients may be
 *  required to sign a confidentiality undertaking in favor of Absa Bank
 *  Limited
 *
 */
@file:JvmName("AccountUtils")

package com.barclays.absa.utils.extensions

import com.barclays.absa.banking.framework.dagger.getServiceInterface
import com.barclays.absa.banking.framework.services.contracts.IAppCacheService
import com.barclays.absa.utils.AbsaCacheManager
import styleguide.utils.extensions.toFormattedAccountNumber

fun String?.getAccountDisplayDescriptionFromAccountNumber(): String {
    val appCacheService: IAppCacheService = getServiceInterface()
    if (this.isNullOrEmpty() || this.isNullOrBlank()) return ""
    var cachedAccountsList = appCacheService.getSecureHomePageObject()?.accounts
    if (cachedAccountsList != null) {
        cachedAccountsList = AbsaCacheManager.getInstance().accountsList.accountsList
    }
    cachedAccountsList?.let { accounts ->
        accounts.forEach {
            if (this == it.accountNumber) {
                return if (!it.description.isNullOrEmpty()) it.description else it.accountType
            }
        }
    }
    return ""
}

fun String?.getAccountDisplayDescriptionWithFormattedAccountNumber(): String {
    val description = getAccountDisplayDescriptionFromAccountNumber()
    return "$description (${this.toFormattedAccountNumber()})"
}