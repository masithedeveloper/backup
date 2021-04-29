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
 */

package com.barclays.absa.banking.express.beneficiaries.dto

import com.barclays.absa.banking.shared.BaseModel
import com.fasterxml.jackson.annotation.JsonProperty
import java.util.*

class OnceOffBeneficiary : BaseModel {
    @JsonProperty("beneficiaryDetailsVO")
    var beneficiaryDetails: OnceOffBeneficiaryDetails = OnceOffBeneficiaryDetails()
    var processedTransactions: List<BeneficiaryPaymentInstruction> = arrayListOf()
    var futureDatedTransactionsList: List<BeneficiaryPaymentInstruction> = arrayListOf()
    var transactionDate: Date = Date()
    var beneficiaryNumber: Int = 0
    var beneficiaryName: String = ""
    var targetAccountNumber: String = ""
    var beneficiaryNumberNameAndAccount: String = ""
}