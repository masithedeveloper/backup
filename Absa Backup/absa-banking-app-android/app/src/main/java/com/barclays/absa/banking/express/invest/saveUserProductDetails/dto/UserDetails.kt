/*
 * Copyright (c) 2021 Absa Bank Limited, All Rights Reserved.
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
package com.barclays.absa.banking.express.invest.saveUserProductDetails.dto

import com.barclays.absa.banking.shared.BaseModel
import com.fasterxml.jackson.annotation.JsonProperty

class UserDetails : BaseModel {
    var userName: String = ""
    var accountNumber: String = ""
    var userNumber: String = ""

    @JsonProperty("ssoToken")
    var singleSignOnToken: String = ""
    var primarySecondFactorDevice: Boolean = false
    var statusCode: String = ""
    var operatorTypeHoldStatus: Boolean = false
    var cifHoldStatus: Boolean = false
    var ficaHoldStatus: Int = 0
    var casaHoldStatus: Boolean = false
    var casaReferenceNumber: Int = 0
    var clientType: String = ""
    var mappedUser: String = ""
    var secondFactorState: String = ""
    var cifDetails: CifDetails = CifDetails()
    var notificationDetails: String = ""
    var clientTypeHoldStatus: Boolean = false
    var secondFactorEnrollStatus: String = ""
    var blockChequeJourney: Boolean = false
    var privateBankCustomer: Boolean = false
}