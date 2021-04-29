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

package com.barclays.absa.banking.express.invest.createAndLinkAccountForExistingCustomer.dto

import com.barclays.absa.banking.shared.BaseModel
import com.fasterxml.jackson.annotation.JsonProperty

class AccountCreateAndLinkRequest : BaseModel {
    @JsonProperty("createAndLinkNoticeDepositAccountVO")
    var investmentAccount: InvestmentAccount = InvestmentAccount()
    var startDate: String = ""
    var endDate: String = ""
    var onceOffAmount: String = ""
    var changedAccountName: String = ""
    var beneficiaryName: String = ""
    var nextPaymentDate: String = ""
    var savingsFrequencyType: String = ""
    var monthlyInstallmentAmount: String = ""
    var interestPayoutAccount: String = ""
    var interestPayoutReference: String = ""
    var initiatePaymentAccount: Long = 0L
    var initiatePaymentReference: String = ""
    var monthlyDebitAccount: Long = 0L
    var monthlyDebitReference: String = ""
    var interestPayoutBranchCode: String = ""
    var interestPayoutAccountType: String = ""
}