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
package com.barclays.absa.banking.payments.international.services.dto

import com.fasterxml.jackson.annotation.JsonProperty
import java.io.Serializable

class ValidateForHolidaysAndTimeObject : Serializable {

    @JsonProperty("allowSWIFT")
    var allowSwift: Boolean = false
    var allowWU: Boolean = false

    override fun toString(): String {
        return "(allowSwift=$allowSwift, allowWU=$allowWU)"
    }

    override fun equals(other: Any?): Boolean {
        if (other === this) return true

        return if (other is ValidateForHolidaysAndTimeObject) {
            true and (allowSwift == other.allowSwift)
                    .and(allowWU == other.allowWU)
        } else false
    }

    override fun hashCode(): Int {
        val PRIME = 59
        var result = 1
        result = result * PRIME + if (allowSwift) 79 else 97
        result = result * PRIME + if (allowWU) 79 else 97
        return result
    }
}