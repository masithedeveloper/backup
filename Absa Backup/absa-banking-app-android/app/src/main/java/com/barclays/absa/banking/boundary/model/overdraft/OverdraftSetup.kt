/*
 *  Copyright (c) 2018 Absa Bank Limited, All Rights Reserved.
 *
 *  This code is confidential to Absa Bank Limited and shall not be disclosed
 *  outside the Bank without the prior written permission of the Absa Legal
 *
 *  In the event that such disclosure is permitted the code shall not be copied
 *  or distributed other than on a need-to-know basis and any recipients may be
 *  required to sign a confidentiality undertaking in favor of Absa Bank
 *  Limited
 *
 */
package com.barclays.absa.banking.boundary.model.overdraft

import java.io.Serializable

class OverdraftSetup(var accountNumber: String?, var overdraftAmount: String?) : Serializable {
    var creditProtection: Boolean = false
    var creditAgreementNoticeMethod: String = ""
    var marketingConsent: String = "N"
    var emailChannel: String = "N"
    var directMailChannel: String = "N"
    var telephoneChannel: String = "N"
    var smsChannel: String = "N"
    var accountNumberAndDescription: String? = ""
}
