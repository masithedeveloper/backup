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

package com.barclays.absa.banking.lotto.services.dto

import android.os.Parcelable
import com.barclays.absa.banking.boundary.shared.dto.TransactionResponse
import com.fasterxml.jackson.annotation.JsonProperty
import kotlinx.android.parcel.Parcelize
import java.math.BigDecimal

class LottoGameRulesResponse : TransactionResponse() {
    @JsonProperty("lottoGameRules")
    var lottoGameRules: MutableList<LottoGameRules> = mutableListOf()
}

@Parcelize
class LottoGameRules(
        @JsonProperty("gameType")
        var gameType: String = "",
        @JsonProperty("basePrice")
        var basePrice: BigDecimal = BigDecimal(5.0),
        @JsonProperty("maximumTicketPrice")
        var maximumTicketPrice: BigDecimal = BigDecimal(750.0),
        @JsonProperty("minBoards")
        var minimumBoards: Int = 1,
        @JsonProperty("maxBoards")
        var maximumBoards: Int = 20,
        @JsonProperty("minimumPrimaryBalNumber")
        var minimumPrimaryBallNumber: Int = 5,
        @JsonProperty("maximumPrimaryBalNumber")
        var maximumPrimaryBallNumber: Int = 5,
        @JsonProperty("minimumSecondaryBalNumber")
        var minimumSecondaryBallNumber: Int = 1,
        @JsonProperty("maximumSecondaryBalNumber")
        var maximumSecondaryBallNumber: Int = 1,
        @JsonProperty("quickPickInd")
        var quickPickInd: Boolean = true,
        @JsonProperty("multiplierInd")
        var multiplierInd: Boolean = true,
        @JsonProperty("multiplierPrice")
        var multiplierPrice: BigDecimal = BigDecimal(5.0),
        @JsonProperty("maximumNoOfDraws")
        var maximumNoOfDraws: Int = 10,
        @JsonProperty("primaryLowNumber")
        var primaryLowNumber: Int = 1,
        @JsonProperty("primaryHighNumber")
        var primaryHighNumber: Int = 52,
        @JsonProperty("secondaryLowNumber")
        var secondaryLowNumber: Int = 1,
        @JsonProperty("secondaryHighNumber")
        var secondaryHighNumber: Int = 20,
        @JsonProperty("nextDrawDate")
        var nextDrawDate: String = "",
        @JsonProperty("lottoJackpots")
        var lottoJackpots: MutableList<LottoJackpot> = mutableListOf()) : Parcelable

@Parcelize
class LottoJackpot(@JsonProperty("drawType")
                   var drawType: String = "",
                   @JsonProperty("jackpot")
                   var jackpot: Double = 0.00,
                   @JsonProperty("jackpotType")
                   var jackpotType: String = "") : Parcelable