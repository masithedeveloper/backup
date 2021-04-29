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

class ManageCardLimitDetails : ResponseObject() {

    @JsonProperty("cardNumber")
    var cardNumber: String? = ""
    @JsonProperty("cardType")
    var cardType: String? = ""
    @JsonProperty("atmCurrentLimit")
    var atmCurrentLimit: Amount? = Amount()
    @JsonProperty("posCurrentLimit")
    var posCurrentLimit: Amount? = Amount()
    @JsonProperty("posMaxLimit")
    var posMaxLimit: Amount? = Amount()
    @JsonProperty("atmMaxLimit")
    var atmMaxLimit: Amount? = Amount()
}