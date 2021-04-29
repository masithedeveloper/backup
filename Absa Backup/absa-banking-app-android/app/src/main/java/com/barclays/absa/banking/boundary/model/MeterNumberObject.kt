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
import java.io.Serializable

class MeterNumberObject : ResponseObject(), Serializable {

    @JsonProperty("meterNumber")
    var meterNumber: String? = null
    @JsonProperty("utility")
    var utility: String? = null
    @JsonProperty("arrearsAmnt")
    var arrearsAmount: String? = null
    @JsonProperty("customerName")
    var customerName: String? = null
    @JsonProperty("customerAddress")
    var customerAddress: String? = null
    @JsonProperty("customerStandNo")
    var customerStandNumber: String? = null
    @JsonProperty("customerArea")
    var customerArea: String? = null
    @JsonProperty("txnStatus")
    var transactionStatus: String? = null
    @JsonProperty("txnMessage")
    var transactionMessage: String? = null

}
