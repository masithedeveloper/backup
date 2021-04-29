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

package com.barclays.absa.banking.express.beneficiaries.dto

import com.barclays.absa.banking.boundary.model.CustomerProfileObject
import com.barclays.absa.banking.shared.BaseModel

class BeneficiaryNotification : BaseModel {
    var notificationMethod: BeneficiaryNotificationMethod = BeneficiaryNotificationMethod.NONE
    var recipientName: String = ""
    var cellphoneNumber: String = ""
    var emailAddress: String = ""
    var faxCode: String = ""
    var faxNumber: String = ""
    var paymentMadeBy: String = CustomerProfileObject.instance.customerName ?: ""
    var contactMeOn: String = ""
    var additionalComments: String = ""
}