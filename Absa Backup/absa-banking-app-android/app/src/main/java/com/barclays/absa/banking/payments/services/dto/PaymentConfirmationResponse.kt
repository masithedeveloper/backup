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
package com.barclays.absa.banking.payments.services.dto

import com.barclays.absa.banking.boundary.shared.dto.SureCheckResponse
import com.barclays.absa.banking.payments.services.TxnAmt
import com.fasterxml.jackson.annotation.JsonProperty

class PaymentConfirmationResponse : SureCheckResponse() {

    @JsonProperty("thirDetl")
    var bankAccountHolder: String? = null
    @JsonProperty("branchCd")
    var branchCode: String? = null
    @JsonProperty("thirRef")
    var beneficiaryReference: String? = null
    var popUpFlag: String? = null
    @JsonProperty("frmActNo")
    var fromAccountNumber: String? = null
    @JsonProperty("branchNam")
    var branchName: String? = null
    @JsonProperty("maskedFrmActNo")
    var maskedFromAccountNumber: String? = null
    @JsonProperty("myDetl")
    var myDetl: String? = null
    var txnRef: String? = null
    @JsonProperty("actTyp")
    var accountType: String? = null
    @JsonProperty("thirMethod")
    var beneficiaryMethodDetails: String? = null
    @JsonProperty("frmActDesc")
    var fromAccountType: String? = null
    @JsonProperty("instNam")
    var institutionName: String? = null

    @JsonProperty("txnAmt")
    var transactionAmount: TxnAmt? = null

    @JsonProperty("acctAtInst")
    var acctAtInst: String? = null
    @JsonProperty("myRef")
    var myReference: String? = null
    @JsonProperty("benStatusType")
    var beneficiaryType: String? = null
    var iipReferenceNumber: String? = null
    @JsonProperty("imidPay")
    var immediatePay: String? = null
    @JsonProperty("futureTxDate")
    var futureDate: String? = null
    @JsonProperty("actNo")
    var accountNumber: String? = null
    var myNotice: String? = null
    @JsonProperty("nowFlg")
    var paymentDate: String? = null
    var txnErrorCode: String? = null
    var msg: String? = null
    @JsonProperty("benNam")
    var beneficiaryName: String? = null
    @JsonProperty("bankNam")
    var bankName: String? = null
    @JsonProperty("thirNotice")
    var beneficiaryNotice: String? = null
    @JsonProperty("myMethod")
    var myMethod: String? = null
    var myFaxCode: String? = null
    var instCode: String? = null
    var benFaxCode: String? = null
    var beneficiaryId: String? = null

    fun getMyMethodDetails(): String? {
        return this.myDetl
    }
}