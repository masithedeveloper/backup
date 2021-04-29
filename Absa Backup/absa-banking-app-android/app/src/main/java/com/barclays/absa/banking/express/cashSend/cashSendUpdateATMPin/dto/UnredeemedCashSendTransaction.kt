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
 */
package com.barclays.absa.banking.express.cashSend.cashSendUpdateATMPin.dto

import com.barclays.absa.banking.shared.BaseModel
import com.fasterxml.jackson.annotation.JsonProperty

class UnredeemedCashSendTransaction : BaseModel {
    var beneficiaryNumber: Long = 0
    var beneficiaryShortName: String = ""

    @JsonProperty("recipientCellNo")
    var recipientCellNumber: String = ""
        
    var statementReference: String = ""
    var transactionDateTime: String = ""
    var uniqueEFT: String = ""
    var paymentNumber: Long = 0
    var sourceAccount: String = ""
    var paymentAmount: String = ""
    var transactionResult: Boolean = false
    var paymentProcessed: Boolean = false
}