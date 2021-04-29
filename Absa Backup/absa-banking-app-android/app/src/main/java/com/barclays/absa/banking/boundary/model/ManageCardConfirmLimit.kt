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

import com.barclays.absa.banking.boundary.shared.dto.SureCheckResponse
import com.fasterxml.jackson.annotation.JsonProperty

class ManageCardConfirmLimit : SureCheckResponse() {

    @JsonProperty("newATMLimit")
    val newATMLimit: Amount? = null
    @JsonProperty("newPOSLimit")
    val newPOSLimit: Amount? = null
    @JsonProperty("oldATMLimit")
    val oldATMLimit: Amount? = null
    @JsonProperty("oldPOSLimit")
    val oldPOSLimit: Amount? = null
    @JsonProperty("cardNumber")
    val cardNumber: String? = null
    @JsonProperty("cardType")
    val cardType: String? = null
    @JsonProperty("txnRefID")
    val txnRefID: String? = null
}
