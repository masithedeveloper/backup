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

import com.barclays.absa.banking.shared.BaseModel
import com.fasterxml.jackson.annotation.JsonFormat
import java.util.*

class BeneficiaryPaymentInstruction : BaseModel {
    var futureDatedPayment: Boolean = false
    var paymentSourceAccountNumber: String = ""
    var paymentAmount: String = ""
    var paymentSourceAccountReference: String = ""
    var paymentTargetAccountReference: String = ""
    var processedPaymentStatus: String = ""
    var processedPaymentNumber: String = ""
    var realTimeProcessedPayment = false
    var realTimeProcessedPaymentReferenceNumber: String = ""
    var futureDatedPaymentMultiNumber: String = ""

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm")
    var paymentTransactionDateAndTime: Date = Date()
}