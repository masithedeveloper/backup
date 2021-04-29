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

class NetworkProvider : ResponseObject() {

    @JsonProperty("image")
    var imageUrl: String? = null

    @JsonProperty("netProviderName")
    var name: String? = null

    var institutionCode: String? = null

    var rechargeCurrency: String? = null
    var rechargeCurrencyList: List<String>? = null
    var rechargeAmount: List<String>? = null
    var rechargeSMS: List<String>? = null
    var rechargeDataBundleVoucher: List<String>? = null
    var rechargeDataMonthlyVoucher: List<String>? = null
    var rechargeTelkomVoucher: List<String>? = null
    var rechargeBlackberry: List<String>? = null
    var rechargeWhatsappDataBundleVouchers: List<String>? = null
    var rechargeYoutubeDataBundleVouchers: List<String>? = null
    var rechargeTwitterDataBundleVouchers: List<String>? = null
    var rechargeFacebookDataBundleVouchers: List<String>? = null
    var minRechargeAmount: String? = null
    var maxRechargeAmount: String? = null
    var smsAmount: Amount? = null
}
