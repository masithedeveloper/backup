/*
 * Copyright (c) 2020 Absa Bank Limited, All Rights Reserved.
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
package com.barclays.absa.banking.virtualpayments.scan2Pay.services.response.dto

import com.barclays.absa.banking.R
import com.barclays.absa.banking.boundary.shared.dto.TransactionResponse
import com.barclays.absa.banking.framework.app.BMBApplication
import com.barclays.absa.banking.shared.BaseModel
import com.fasterxml.jackson.annotation.JsonProperty

class ScanToPayCardListResponse : TransactionResponse() {
    @JsonProperty("cards")
    val cardList: List<Card> = emptyList()

    class Card : BaseModel {
        var cardNumber: String = ""
        var type: String = ""
        var scheme: String = ""
        var brandNumber: String = ""
        var trackAndTraceDate: String = ""
        var holdDate: String = ""
        var holdCode: String = ""
        var closeDate: String = ""
        var expiryDate: String = ""
        var friendlyName: String = ""
        var cardHolderName: String = ""
        var closeReason: String = ""
        var closeSubReason: String = ""

        @JsonProperty("indA")
        var indicatorA: String = ""

        @JsonProperty("indB")
        var indicatorB: String = ""
        var active: String = ""

        @JsonProperty("linkedAccount")
        var linkedAccounts: List<LinkedAccount> = emptyList()

        @JsonProperty("featureDetails")
        var featureList: List<FeatureDetails> = emptyList()

        fun getCardTypeDescription():String = when {
            type.equals("DR", true) -> BMBApplication.getInstance().topMostActivity.getString(R.string.debit_card)
            type.equals("CR", true) -> BMBApplication.getInstance().topMostActivity.getString(R.string.credit_card)
            else -> ""
        }

        fun isScanToPayEnabled(): Boolean {
            val scanToPayFeature = featureList.firstOrNull { featureDetails -> featureDetails.name.equals("ScanToPay", true) } ?: return false
            return scanToPayFeature.status.equals("enabled", true)
        }

        class FeatureDetails : BaseModel {
            var name: String = ""
            var type: String = ""
            var status: String = ""

            @JsonProperty("_default")
            var default: Boolean? = null
        }

        class LinkedAccount : BaseModel {
            val accountNumber: String = ""
            val accountType: String = ""
            val accountName: String = ""
        }
    }
}