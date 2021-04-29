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

package com.barclays.absa.banking.express.shared.updateMarketingConsentDetails.dto

import com.fasterxml.jackson.annotation.JsonProperty
import za.co.absa.networking.dto.BaseResponse

open class MarketingConsentRequestOrResponse : BaseResponse() {
    open var option: String = ""

    @JsonProperty("ncrInd")
    var nonCreditIndicator: String = ""

    @JsonProperty("ncrSms")
    var nonCreditSMS: String = ""

    @JsonProperty("ncrEmail")
    var nonCreditEmail: String = ""

    @JsonProperty("ncrAvoice")
    var nonCreditAutoVoice: String = ""

    @JsonProperty("ncrTel")
    var nonCreditTelephone: String = ""

    @JsonProperty("ncrPost")
    var nonCreditPost: String = ""

    @JsonProperty("ncrDtChng")
    var nonCreditDateChange: String = ""

    @JsonProperty("crInd")
    var creditIndicator: String = ""

    @JsonProperty("crSms")
    var creditSMS: String = ""

    @JsonProperty("crEmail")
    var creditEmail: String = ""

    @JsonProperty("crAvoice")
    var creditAutoVoice: String = ""

    @JsonProperty("crTel")
    var creditTelephone: String = ""

    @JsonProperty("crPost")
    var creditPost: String = ""

    @JsonProperty("crDtChng")
    var creditDateChange: String = ""

    var clientCode: String = ""

    var account: String = ""
}