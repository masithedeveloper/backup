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

class CashSendUpdateAtmPinRequest {
    var cashSendType: String = ""
    var instructionType: String = ""
    var beneficiaryName: String = ""
    var beneficiarySurname: String = ""
    var beneficiaryShortName: String = ""
    var recipientCellphoneNumber: String = ""
    var statementReference: String = ""
    var transactionDateAndTime: String = ""
    var sourceAccount: String = ""
    var paymentAmount: String = ""
    var cifKey: String = ""
    var tieBreaker: String = ""
    var beneficiaryNumber: String = ""
    var applicationId: String = "AXOBMOBAPP"
    var pin: String = ""
    var uniqueEFT: String = ""
    var paymentNumber = 0
}