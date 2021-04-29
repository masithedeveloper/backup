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
package com.barclays.absa.banking.express.onceOffPaymentHistoryPaymentNotification.dto

import com.barclays.absa.banking.express.beneficiaries.dto.BeneficiaryAccountType
import com.barclays.absa.banking.express.beneficiaries.dto.BeneficiaryNotification
import com.barclays.absa.banking.shared.BaseModel
import com.fasterxml.jackson.annotation.JsonProperty

class OnceOffPaymentHistoryPaymentNotificationRequest : BaseModel {
    var fromAccount: String = ""
    var fromDate: String = ""
    var toDate: String = ""
    var powerOfAttorney: Boolean = true
    var cifKey: String = ""
    var beneficiaryNotification: BeneficiaryNotification = BeneficiaryNotification()
    var ownNotification: BeneficiaryNotification = BeneficiaryNotification()
    var paymentHistoryLine: PaymentHistoryLine = PaymentHistoryLine()
}

class PaymentHistoryLine {
    var amount: String = ""
    @JsonProperty("trgBusinessCode")
    var targetBusinessCode: String = ""
    var paymentNumber: String = ""
    var transactionDate: String = ""
    var note: String = ""
    @JsonProperty("trgAccountNumber")
    var targetAccountNumber: String = ""
    @JsonProperty("srcStatementRef")
    var sourceStatementReference: String = ""
    @JsonProperty("trgStatementRef")
    var targetStatementReference: String = ""
    var paymentStatus: String = ""
    var eftNumber: String = ""
    @JsonProperty("tiebNumber")
    var tieBreakerNumber: String = ""
    @JsonProperty("benfName")
    var beneficiaryName: String = ""
    var payType: String = ""
    var beneficiaryAccountType: BeneficiaryAccountType? = null
    var typeOfBeneficiary: String = ""
    var clearingCodeOrInstitutionCode: String = ""
    var targetAccountType: String = ""
    @JsonProperty("paymentReferenceNo")
    var paymentReferenceNumber: String = ""
    @JsonProperty("instrType")
    var instructionType: String = ""
    var transactionTime: String = ""
    @JsonProperty("benefBank")
    var beneficiaryBank: String = ""
    var realTimePayment: Boolean = false
    var longBankOrInstitutionName: String = ""
}