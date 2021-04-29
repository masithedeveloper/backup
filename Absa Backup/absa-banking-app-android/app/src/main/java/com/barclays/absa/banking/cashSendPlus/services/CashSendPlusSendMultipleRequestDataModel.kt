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

package com.barclays.absa.banking.cashSendPlus.services

import com.barclays.absa.banking.shared.BaseModel
import com.google.gson.annotations.SerializedName

class CashSendPlusSendMultipleRequestDataModel : BaseModel {
    @SerializedName("cashSendDTOs")
    var cashSendData = mutableListOf<CashSendPlusSendMultipleData>()

    class CashSendPlusSendMultipleData : BaseModel {
        var beneficiaryName = ""
        var beneficiarySurname = ""
        var beneficiaryShortName = ""
        var beneficiaryNumber = ""
        var fromAccountNo = ""
        var cellNo = ""
        var statementDescription = ""
        var amount = ""
        var accessCode = ""
        var virtualServerID = ""
        var sessionKeyID = ""
        var paymentNo = "0"
        var uniqueEFT = ""
        var cashSendPlus = "true"
    }
}