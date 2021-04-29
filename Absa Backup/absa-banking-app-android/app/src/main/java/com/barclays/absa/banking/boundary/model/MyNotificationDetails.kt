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

class MyNotificationDetails : TransactionResponse() {

    @JsonProperty("cellNolist")
    var cellphoneNumber: List<String>? = null
    @JsonProperty("faxinfolist")
    var faxDetails: List<FaxDetails>? = null
    @JsonProperty("emaillist")
    var email: List<String>? = null
    @JsonProperty("actualCellNo")
    var actualCellphoneNumber: String? = null

}
