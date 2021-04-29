/*
 * Copyright (c) 2019 Absa Bank Limited, All Rights Reserved.
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

import com.fasterxml.jackson.annotation.JsonProperty
import java.io.Serializable

class PropertyAddress : Serializable {
    var addressLine1: String? = ""
    var addressLine2: String? = ""
    var postalCode: String? = ""
    @JsonProperty("suburbRsa")
    var suburb: String? = ""
    var town: String? = ""

    fun getFormattedAddress(): String {
        val separator = " ,"
        return StringBuilder()
                .append(addressLine1).append(separator)
                .append(addressLine2).append(separator)
                .append(suburb).append(separator)
                .append(town).append(separator)
                .append(postalCode)
                .toString()
    }

    fun getFormattedDisplayAddress(): String {
        val separator = ", "
        return StringBuilder()
                .append(addressLine1).append(separator)
                .append(addressLine2).append(separator)
                .append(suburb).append(separator)
                .append(town).append(separator)
                .append(postalCode)
                .toString()
    }
}
