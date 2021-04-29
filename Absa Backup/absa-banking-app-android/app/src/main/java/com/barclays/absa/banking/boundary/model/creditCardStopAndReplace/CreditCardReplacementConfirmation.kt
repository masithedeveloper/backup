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
package com.barclays.absa.banking.boundary.model.creditCardStopAndReplace

import com.barclays.absa.banking.boundary.shared.dto.SureCheckResponse
import com.fasterxml.jackson.annotation.JsonProperty

class CreditCardReplacementConfirmation : SureCheckResponse() {
    @JsonProperty("msg")
    var message: String? = null
    @JsonProperty("txnRef")
    var txnReference: String? = null
    var creditCardNumber: String? = null
    var dateLoss: String? = null
    var dateLastUse: String? = null
    var contactNumber: String? = null
    var cardDeliveryMethod: String? = null
    var deliveryBranchCode: String? = null
    var replacementReason: String? = null
}
