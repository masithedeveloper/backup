/*
 * Copyright (c) 2021 Absa Bank Limited, All Rights Reserved.
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
package com.barclays.absa.banking.express.onceOffPaymentHistoryDetails.dto

import com.barclays.absa.banking.express.getAllBalances.dto.AccountTypesExpress
import com.barclays.absa.banking.express.onceOffPaymentHistoryPaymentNotification.dto.PaymentHistoryLine
import com.barclays.absa.banking.shared.BaseModel

class OnceOffPaymentHistoryDetails() : BaseModel {
    var fromAccount: String = ""
    var fromAccountName: String = ""
    var fromAccountType: AccountTypesExpress = AccountTypesExpress.Savings
    var fromDate: String = ""
    var toDate: String = ""
    var powerOfAttorney: Boolean = true
    var payType: String = "ONCEOFF"
    var cifKey: String = ""
    var paymentHistoryLines: List<PaymentHistoryLine> = arrayListOf()
}