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

class SecureHomePageObject : ResponseObject() {

    @JsonProperty("deviceList")
    var devices: List<Device>? = null

    @JsonProperty("passwordLength")
    var passwordLength: String? = ""

    @JsonProperty("passwordDigitList")
    var passwordDigits: List<PasswordDigit>? = null

    @JsonProperty("ival")
    var iVal: String? = ""

    @JsonProperty("balNotYetClrdDt")
    var balanceNotYetClearedDate: String? = ""
    var accessPrivileges: AccessPrivileges? = null

    @JsonProperty("custProf")
    var customerProfile: CustomerProfileObject = CustomerProfileObject()
        @Deprecated("Use CustomerProfile instance instead")
        get

    @JsonProperty("payLst")
    var paymentBeneficiaries: List<BeneficiaryObject>? = null

    @JsonProperty("cashSend")
    var cashsendBeneficiaries: List<BeneficiaryObject>? = null

    @JsonProperty("airTime")
    var airtimeBeneficiaries: List<BeneficiaryObject>? = null
    var accountImage: String? = null
    @JsonProperty("imageName")
    var imageName: String? = ""
    var isPrimarySecondFactorDevice: Boolean = false
    var accounts: List<AccountObject> = arrayListOf()
    var fromAccounts: List<AccountObject>? = null
    var toAccounts: List<AccountObject>? = null
    var serializableDeviceList: DeviceList? = null
    var langCode: String? = ""
}
