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

class ClientContactInformation : ResponseObject() {

    @JsonProperty("addressLine1")
    var addressLine1: String? = null
    @JsonProperty("addressLine2")
    var addressLine2: String? = null
    @JsonProperty("postalCode")
    var postalCode: String? = null
    @JsonProperty("cellphoneNbr")
    var cellphoneNumber: String? = null
    @JsonProperty("suburbRsa")
    var suburbRsa: String? = null
    @JsonProperty("town")
    var town: String? = null
    @JsonProperty("workTelephoneNbr")
    var workTelephoneNumber: String? = null
    @JsonProperty("homeTelephoneNbr")
    var homeTelephoneNumber: String? = null
    @JsonProperty("cellphoneNbrActual")
    var cellphoneNumberActual: String? = null
    @JsonProperty("workTelephoneNbrActual")
    var workTelephoneNumberActual: String? = null
    @JsonProperty("homeTelephoneNbrActual")
    var homeTelephoneNumberActual: String? = null

}
