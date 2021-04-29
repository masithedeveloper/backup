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
package com.barclays.absa.banking.boundary.model.authorisations

import com.barclays.absa.banking.boundary.model.Amount
import com.barclays.absa.banking.framework.data.ResponseObject
import com.fasterxml.jackson.annotation.JsonProperty
import java.io.Serializable

class AuthorisationTransactionDetails : ResponseObject() {

    @JsonProperty("transactionType")
    var transactionType: String? = null

    @JsonProperty("requestedBy")
    var requestedBy: String? = null

    @JsonProperty("fromAccount")
    var fromAccount: String? = null

    @JsonProperty("accountType")
    var accountType: String? = null

    @JsonProperty("transactionAmount")
    private val transactionAmount: Amount? = null

    @JsonProperty("toAccount")
    var toAccount: String? = null

    @JsonProperty("cellNumber")
    var cellNumber: String? = null

    @JsonProperty("networkProvider")
    var networkProvider: String? = null

    @JsonProperty("benName")
    var beneficiaryName: String? = null

    @JsonProperty("benAccount")
    var beneficiaryAccount: String? = null

    @JsonProperty("bankName")
    var bankName: String? = null

    @JsonProperty("bankCode")
    var bankCode: String? = null

    @JsonProperty("benRef")
    var beneficiaryReference: String? = null

    @JsonProperty("myRef")
    var myReference: String? = null

    @JsonProperty("debitDateTime")
    var debitDateTime: String? = null

    @JsonProperty("transactionDateTime")
    var transactionDateTime: String? = null

    @JsonProperty("myNotice")
    var myNotice: String? = null

    @JsonProperty("benNotice")
    var beneficiaryNotice: String? = null

    @JsonProperty("benNoticeTyp")
    var beneficiaryNoticeType: String? = null

    @JsonProperty("benNoticeDtl")
    var beneficiaryNoticeDetail: String? = null

    @JsonProperty("myNoticeTyp")
    var myNoticeType: String? = null

    @JsonProperty("myNoticeDtl")
    var myNoticeDetail: String? = null

    @JsonProperty("transactionTypeCode")
    var transactionTypeCode: String? = null

    @JsonProperty("operatorName")
    var operatorName: String? = null

    @JsonProperty("operatorId")
    var operatorId: String? = null

    @JsonProperty("iipRef")
    var iipReference: String? = null

    @JsonProperty("cspLimits")
    var cashSendPlusLimitsData: CashSendPlusLimitsData? = null

    fun getTransactionAmount(): Amount {
        return transactionAmount ?: Amount()
    }

    class CashSendPlusLimitsData: Serializable {
        val cashSendPlusLimitAmt: String = ""
        val cashSendPlusLimitAmtPrev: String = ""
    }
}