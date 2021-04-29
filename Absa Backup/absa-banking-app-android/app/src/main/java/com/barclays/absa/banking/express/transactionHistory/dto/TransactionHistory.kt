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

package com.barclays.absa.banking.express.transactionHistory.dto

import com.barclays.absa.banking.shared.BaseModel

class TransactionHistory : BaseModel {
    var availableBalance: String = ""
    var currentBalance: String = ""
    var firstLineTxAmount: String = ""
    var firstLineTxBalanceAmount: String = ""
    var fromAccount: String = ""
    var sortFields: List<SortFields> = emptyList()
    var fromAccountName: String = ""
    var fromAccountType: String = ""
    var fromDate: String = ""
    var toDate: String = ""
    var unclearedEffectsAmount: String = ""
    var unclearedEffectsEnabled: Boolean = false
    var unclearedEffectsExist: Boolean = false
    var powerOfAttorney: Boolean = false
    var stampedTransactionHistory: Boolean = false
    var accountHistoryLines: ArrayList<AccountHistoryLines> = arrayListOf()
}
