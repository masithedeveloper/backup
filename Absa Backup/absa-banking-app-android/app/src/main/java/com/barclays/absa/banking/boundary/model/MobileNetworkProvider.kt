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

class MobileNetworkProvider : ResponseObject() {

    @JsonProperty("image")
    val image: String? = null
    @JsonProperty("netProviderName")
    val networkProviderName: String? = null
    @JsonProperty("minRcAmt")
    val minimumRechargeAmount: Voucher? = null
    @JsonProperty("maxRcAmt")
    val maximumRechargeAmount: Voucher? = null
    @JsonProperty("vouchers")
    val vouchers: List<Voucher>? = null
    @JsonProperty("institutionCode")
    val institutionCode: String? = null
    @JsonProperty("institutionDescription")
    val institutionDescription: String? = null
    @JsonProperty("SMSvouchers")
    val smsVouchers: List<Voucher>? = null
    @JsonProperty("DataBundlevouchers")
    val dataBundleVouchers: List<Voucher>? = null
    @JsonProperty("BlackBrryvouchers")
    val blackBerryVouchers: List<Voucher>? = null
    @JsonProperty("DataBundleMonthlyVouchers")
    val dataBundleMonthlyVouchers: List<Voucher>? = null
}