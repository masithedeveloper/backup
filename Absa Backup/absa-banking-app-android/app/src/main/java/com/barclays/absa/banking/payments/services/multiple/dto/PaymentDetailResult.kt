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
package com.barclays.absa.banking.payments.services.multiple.dto

import com.barclays.absa.banking.boundary.model.Amount
import com.barclays.absa.banking.framework.data.ResponseObject
import com.fasterxml.jackson.annotation.JsonProperty

class PaymentDetailResult : ResponseObject() {

    @JsonProperty("result")
    var result: String? = null
    @JsonProperty("iipReferenceNo")
    var iipReferenceNumber: String? = null
    @JsonProperty("paymentDateTime")
    var paymentDateTime: String? = null
    @JsonProperty("immediatePayment")
    var immediatePayment: String? = null
    @JsonProperty("benAccNo")
    var accountNumber: String? = null
    @JsonProperty("benId")
    var beneficiaryId: String? = null
    @JsonProperty("benName")
    var beneficiaryName: String? = null
    @JsonProperty("nowFlg")
    var nowFlag: String? = null
    @JsonProperty("txnAmt")
    var transactionAmount: Amount? = null
    @JsonProperty("warningMsg")
    var warningMessage: String? = null
    @JsonProperty("errorMsg")
    var transactionErrorMessage: String? = null

}