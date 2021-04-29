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

import com.barclays.absa.banking.shared.BaseModel
import com.google.gson.annotations.SerializedName

class ScanToPayAuthResponse : BaseModel {

    var meta: Meta = Meta()
    var display: List<Display> = emptyList()

    class Card : BaseModel {
        val index: Int = 0

        @SerializedName("card_number")
        var cardNumber: String = ""

        @SerializedName("card_type")
        var cardType: String = ""

        @SerializedName("product_name")
        var productName: String = ""

        @SerializedName("accepted_by_merchant")
        var acceptedByMerchant: Boolean = false
    }

    class Meta : BaseModel {
        @SerializedName("flow_id")
        var flowId: String = ""
        var amount: Double = 0.00

        @SerializedName("currency_code")
        var currencyCode: String = "ZAR"

        @SerializedName("cards")
        var cards: List<Card> = emptyList()
    }

    class Display : BaseModel {
        val name: String = ""
        val value: String = ""
    }
}