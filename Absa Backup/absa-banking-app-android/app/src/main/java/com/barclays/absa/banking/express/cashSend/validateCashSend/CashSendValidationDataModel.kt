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
package com.barclays.absa.banking.express.cashSend.validateCashSend

import com.barclays.absa.banking.shared.BaseModel

class CashSendValidationDataModel : BaseModel {
    var serviceOperation: String = ""
    var pin: String = ""
    var applicationId: String = "AXOBMOBAPP"
    var beneficiaryNumber: String = ""
    var tieBreaker: String = ""
    var cifKey: String = ""
    var paymentAmount: String = ""
    var sourceAccount: String = ""
    var transactionDateTime: String = ""
    var statementReference: String = ""
    var recipientCellphoneNumber: String = ""
    var beneficiaryShortName: String = ""
    var beneficiarySurname: String = ""
    var beneficiaryName: String = ""
    var instructionType: String = ""
    var cashSendType: String = ""
}