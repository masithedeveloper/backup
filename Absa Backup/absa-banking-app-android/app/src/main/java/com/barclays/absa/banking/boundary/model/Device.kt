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

import java.io.Serializable

class Device private constructor() : ResponseObject(), Serializable {

    @JsonProperty("appVersion")
    var appVersion: String? = null
    @JsonProperty("imie")
    var imei: String? = null
    @JsonProperty("model")
    var model: String? = null
    @JsonProperty("manufacturer")
    var manufacturer: String? = null
    @JsonProperty("msisdn")
    var msisdn: String? = null
    @JsonProperty("nickname")
    var nickname: String? = null
    @JsonProperty("status")
    private var authPending: String? = null
    @JsonProperty("channelID")
    var channelID: String? = null
    @JsonProperty("actionType")
    var actionType: String? = null

    val isAuthPending: Boolean
        get() = !"A".equals(authPending!!, ignoreCase = true)

    init {
        this.appVersion = ""
        this.imei = ""
        this.model = ""
        this.manufacturer = ""
        this.msisdn = ""
        this.nickname = ""
        this.authPending = ""
        this.channelID = ""
        this.actionType = ""
    }

    fun setAuthPending(authPending: String) {
        this.authPending = authPending
    }
}
