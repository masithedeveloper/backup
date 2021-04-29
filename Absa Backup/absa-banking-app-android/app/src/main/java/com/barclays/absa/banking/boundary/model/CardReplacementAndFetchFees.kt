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

class CardReplacementAndFetchFees : ResponseObject() {

    @JsonProperty("cardDeliveryFeeApplicable")
    var cardDeliveryFeeApplicable: String? = null
    @JsonProperty("cardDeliveryFeeType")
    var cardDeliveryFeeType: String? = null
    @JsonProperty("replacementFeeApplicable")
    var replacementFeeApplicable: String? = null
    @JsonProperty("replacementFeeType")
    var replacementFeeType: String? = null
    @JsonProperty("cardDeliveryFee")
    var cardDeliveryFee: Amount? = null
    @JsonProperty("replacementFee")
    var replacementFee: Amount? = null
    @JsonProperty("status")
    var status: String? = null
    @JsonProperty("statusMessage")
    var statusMessage: String? = null

}
