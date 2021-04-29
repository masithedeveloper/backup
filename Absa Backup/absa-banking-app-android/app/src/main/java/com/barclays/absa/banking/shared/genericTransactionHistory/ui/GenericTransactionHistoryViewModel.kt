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
package com.barclays.absa.banking.shared.genericTransactionHistory.ui

import com.barclays.absa.banking.boundary.model.AccountObject
import com.barclays.absa.banking.presentation.shared.BaseViewModel
import com.barclays.absa.utils.AccountHelper

class GenericTransactionHistoryViewModel : BaseViewModel() {

    var accountDetail: AccountObject? = AccountObject()

    fun getAccountObject(accountNumber: String) {
        accountDetail = AccountHelper.getAccountObjectForAccountNumber(accountNumber)
    }
}