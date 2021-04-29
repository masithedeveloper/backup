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

package com.barclays.absa.banking.boundary.model.debitOrder

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper

class DebitOrderDisputeLimit {
    @JsonProperty("Limit")
    var limit: String? = ""
    @JsonProperty("LimitExceededMessage_E")
    var limitExceededMessageEnglish: String? = ""
    @JsonProperty("LimitExceededMessage_A")
    var limitExceededMessageAfrikaans: String? = ""

    fun populateDebitOrderLimitObject(jsonResponse: String?): DebitOrderDisputeLimit? {
        val objectMapper = ObjectMapper()
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)

        return if (!jsonResponse.isNullOrEmpty()) {
            try {
                objectMapper.readValue(jsonResponse, DebitOrderDisputeLimit::class.java) as DebitOrderDisputeLimit
            } catch (e: Exception) {
                DebitOrderDisputeLimit()
            }
        } else {
            DebitOrderDisputeLimit()
        }
    }
}