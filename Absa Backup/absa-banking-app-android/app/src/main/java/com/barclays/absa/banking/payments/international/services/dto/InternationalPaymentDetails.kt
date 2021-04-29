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
package com.barclays.absa.banking.payments.international.services.dto

import com.barclays.absa.banking.framework.data.ResponseObject
import com.fasterxml.jackson.annotation.JsonProperty

class InternationalPaymentDetails : ResponseObject() {
    @JsonProperty("prrCaseId")
    var paymentReceiptCaseId: String? = ""
    @JsonProperty("prIipInd")
    var paymentReceiptIIPIndicator: String? = ""
    var count: String? = ""
    @JsonProperty("prStatus")
    var paymentReceiptStatus: String? = ""
    @JsonProperty("prSrcRef")
    var paymentReceiptSourceReference: String? = ""
    @JsonProperty("prIipRef")
    var paymentReceiptIIPReference: String? = ""
    @JsonProperty("prAmount")
    var paymentReceiptAmount: String? = ""
    @JsonProperty("prrSwiftTranNum")
    var paymentReceiptSwiftTransactionNumber: String? = ""
    @JsonProperty("prPaymNo")
    var paymentReceiptPaymentNumber: String? = ""
    @JsonProperty("transferType")
    var transferType: String? = ""
    @JsonProperty("prrEndorsedDoc")
    var paymentReceiptEndorsedDocument: String? = ""
    @JsonProperty("prTrgRef")
    var paymentReceiptTransferReference: String? = ""
    @JsonProperty("prActionDate")
    var paymentReceiptActionDate: String? = ""
    @JsonProperty("prrMtcNumber")
    var paymentReceiptMoneyTransferControlNumber: String? = ""
    @JsonProperty("prSrcAcc")
    var paymentReceiptSourceAccount: String? = ""

}