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

package com.barclays.absa.banking.express.payments.resendNoticeOfPayment.dto

import com.barclays.absa.banking.express.beneficiaries.dto.BeneficiaryNotification
import com.barclays.absa.banking.shared.BaseModel
import com.fasterxml.jackson.annotation.JsonFormat
import com.fasterxml.jackson.annotation.JsonProperty
import java.util.*

class ResendNoticeOfPaymentRequest : BaseModel {
    var beneficiaryName: String = ""
    var targetAccountNumber: String = ""
    var clearingCodeOrInstitutionCode: String = ""
    var bankName: String = ""
    var targetAccountReference: String = ""
    var realTimeProcessedPayment: Boolean = false
    var paymentAmount: String = ""
    var processedPaymentNumber: String = ""
    var uniqueEFTNumber: String = ""
    var beneficiaryNumber: Int = 0
    var tieBreaker: String = ""
    var cifKey: String = ""
    var beneficiaryStatus: String = ""
    var instructionType: String = ""
    var beneficiaryNotification: BeneficiaryNotification = BeneficiaryNotification()

    @JsonProperty("ownNotificationVO")
    var ownNotification: BeneficiaryNotification = BeneficiaryNotification()

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm")
    var paymentTransactionDateAndTime: Date = Date()
}