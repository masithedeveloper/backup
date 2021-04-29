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
import java.util.*

class RegisterProfileDetail : ResponseObject() {
    var showPasswordScreen: Boolean = false
    var selectedBillingAccountNo: String? = null
    @JsonProperty("accounts")
    var accounts: ArrayList<RegisterAccountListObject>? = null

    @JsonProperty("surname")
    var surname: String? = null

    @JsonProperty("enterpriseSessionID")
    var enterpriseSessionID: String? = null

    @JsonProperty("selectedAccessAccountNo")
    var selectedAccessAccountNo: String? = null

    @JsonProperty("email")
    var email: String? = null

    @JsonProperty("firstName")
    var firstname: String? = null

    @JsonProperty("cellPhoneNumber")
    var cellPhoneNumber: String? = null

    @JsonProperty("atmCardNo")
    var atmCardNo: String? = null

    @JsonProperty("title")
    var title: String? = null

    @JsonProperty("userNumber")
    var userNumber: String? = null

    @JsonProperty("rsaIdNumber")
    var rsaIdNumber: String? = null

    @JsonProperty("onlinePin")
    var onlinePin: String? = null

    @JsonProperty("atmPin")
    var atmPin: String? = null

    @JsonProperty("success")
    var status: String? = null

    @JsonProperty("failureCode")
    var failureCode: String? = null

    @JsonProperty("failureMessage")
    var failureMessage: String? = null

    @JsonProperty("clientType")
    var clientType: String? = null

    @JsonProperty("cellPhoneNumberActual")
    var cellPhoneNumberActual: String? = null

    @JsonProperty("clntAgrmntAccepted")
    var clientAgreementAccepted: String? = null

    val isMobileRecordNotFound: Boolean
        get() = MOBILE_NUMBER_RECORD_NOT_FOUND == this.cellPhoneNumberActual

    fun shouldShowPasswordScreen(): Boolean {
        return showPasswordScreen
    }

    companion object {
        private const val MOBILE_NUMBER_RECORD_NOT_FOUND = "0000000000"
    }
}
