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
import com.barclays.absa.banking.boundary.shared.dto.SureCheckResponse
import com.fasterxml.jackson.annotation.JsonProperty

class CashSendBeneficiaryConfirmation : SureCheckResponse() {
    @JsonProperty("benId")
    var beneficiaryID: String? = ""
    @JsonProperty("actType")
    var accountType: String? = ""
    @JsonProperty("actNo")
    var fromAccountNumber: String? = ""
    @JsonProperty("amt")
    var amount: Amount = Amount()
    @JsonProperty("cellNo")
    var cellNumber: String? = ""
    @JsonProperty("benName")
    var beneficiaryName: String? = ""
    @JsonProperty("myRef")
    var myReference: String? = ""
    @JsonProperty("msg")
    var message: String? = ""
    @JsonProperty("txnRef")
    var txnReference: String? = ""
    var firstName: String? = ""
    var surname: String? = ""
    var nickName: String? = ""
    var acceptTerms: String? = ""
    var accessPin: String? = ""
}
