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

import com.barclays.absa.banking.boundary.model.AccountObject
import com.barclays.absa.banking.boundary.model.BeneficiaryObject

class CashSendPlusSendMultiplePaymentDetails {
    var id: Long = 0
    var amount: String = "0"
    var reference: String = ""
    var accessPin: String = ""
    var encryptedAccessPin: String = ""
    var isAccessPinShared = false
    var mapId: String = ""
    var virtualSessId: String = ""
    var accountDetail: AccountObject = AccountObject()
    var beneficiaryInfo: BeneficiaryObject = BeneficiaryObject()
}