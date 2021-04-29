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
package com.barclays.absa.banking.boundary.model.cashSend

import com.barclays.absa.banking.boundary.model.Amount
import com.barclays.absa.banking.framework.data.ResponseObject
import com.fasterxml.jackson.annotation.JsonProperty

class CashSendOnceOffConfirmation : ResponseObject() {

    @JsonProperty("txnStatus")
    var txnStatus: String? = null
    @JsonProperty("msg")
    var message: String? = null
    @JsonProperty("txnRef")
    var txnReference: String? = null
    @JsonProperty("actNo")
    var fromAccountNumber: String? = null
    @JsonProperty("firstName")
    var firstName: String? = null
    @JsonProperty("surname")
    var surname: String? = null
    @JsonProperty("nickName")
    var nickName: String? = null
    @JsonProperty("cellNo")
    var cellNumber: String? = null
    @JsonProperty("amt")
    var amount: Amount? = null
        get() {
            if (field == null) {
                this.amount = Amount()
            }
            return field
        }
    @JsonProperty("accessPin")
    var atmPin: String? = null
    @JsonProperty("myRef")
    var myReference: String? = null
    @JsonProperty("showBene")
    var showBeneficiary: String? = null
    @JsonProperty("benId")
    var beneficiaryId: String? = null
    @JsonProperty("actType")
    var accountType: String? = null
    @JsonProperty("benName")
    var beneficiaryName: String? = null
    @JsonProperty("acceptTerms")
    var acceptTerms: String? = null
    @JsonProperty("tvnFlag")
    var tvnFlag: String? = null
    @JsonProperty("corelationId")
    var correlationId: String? = null
    @JsonProperty("transactionDate")
    var transactionDate: String? = null
    @JsonProperty("cellnumber")
    var cellphoneNumber: String? = null
    @JsonProperty("email")
    var email: String? = null
}
