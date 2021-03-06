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

import com.barclays.absa.banking.framework.data.ResponseObject
import com.fasterxml.jackson.annotation.JsonProperty
import java.util.*

class AccountDetail : ResponseObject() {
    @JsonProperty("acntDetails")
    var accountObject: AccountObject? = null

    @JsonProperty("fromDt")
    var fromDate: String? = null
        get() {
            if (field == null) {
                this.fromDate = ""
            }
            return field
        }
    @JsonProperty("toDt")
    var toDate: String? = null
        get() {
            if (field == null) {
                this.toDate = ""
            }
            return field
        }
    @JsonProperty("actActvLst")
    var transactions: List<Transaction> = ArrayList()
}
