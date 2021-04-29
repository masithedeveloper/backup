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
package com.barclays.absa.banking.boundary.model

import com.barclays.absa.banking.boundary.shared.dto.TransactionResponse
import com.fasterxml.jackson.annotation.JsonProperty
import java.io.Serializable
import java.util.*

class BeneficiaryListObject : TransactionResponse(), Serializable {

    @JsonProperty("beneNames")
    var beneficiaryNames: Any? = null
    @JsonProperty("totalRecords")
    var totalRecords: Int? = null
        get() {
            if (field == null) {
                this.totalRecords = 0
            }
            return field
        }
    @JsonProperty("payLst")
    var paymentBeneficiaryList: List<BeneficiaryObject> = emptyList()
    @JsonProperty("cashSend")
    var cashsendBeneficiaryList: List<BeneficiaryObject> = emptyList()
    @JsonProperty("airTime")
    var airtimeBeneficiaryList: List<BeneficiaryObject> = emptyList()
    @JsonProperty("pmtType")
    var paymentType: String? = null
    @JsonProperty("latestTransactionBenLst")
    var latestTransactionBeneficiaryList: List<BeneficiaryObject> = emptyList()
    @JsonProperty("electricity")
    var electricityBeneficiaryList: List<BeneficiaryObject> = emptyList()

    var latestPaymentBeneficiaryList: List<BeneficiaryObject>? = ArrayList()
    var latestCashsendBeneficiaryList: List<BeneficiaryObject> = ArrayList()
    var latestAirtimeBeneficiaryList: List<BeneficiaryObject> = ArrayList()
}
