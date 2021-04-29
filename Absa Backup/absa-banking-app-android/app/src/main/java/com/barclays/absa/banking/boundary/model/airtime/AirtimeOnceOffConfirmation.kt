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
package com.barclays.absa.banking.boundary.model.airtime

import com.barclays.absa.banking.boundary.model.Amount
import com.barclays.absa.banking.boundary.shared.dto.SureCheckResponse
import com.fasterxml.jackson.annotation.JsonProperty

class AirtimeOnceOffConfirmation : SureCheckResponse() {

    @JsonProperty("benNam")
    var beneficiaryName: String? = null
    @JsonProperty("txnAmt")
    var amount: Amount? = Amount()
    @JsonProperty("txnRefNo")
    var txnReferenceNumber: String? = null
    @JsonProperty("frmAcctNo")
    var fromAccount: String? = null
    @JsonProperty("frmAcctTyp")
    var description: String? = null
    @JsonProperty("cellNo")
    var cellNumber: String? = null
    @JsonProperty("networkProvider")
    var institutionCode: String? = null
    @JsonProperty("airtimeTyp")
    var rechargeType: String? = null
    @JsonProperty("networkProviderName")
    var networkProviderName: String? = null

    var voucherDescription: String = ""
}