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

package com.barclays.absa.banking.express.payments.validateMultiplePayment.dto

import za.co.absa.networking.dto.BaseResponse
import za.co.absa.networking.dto.ResultMessage

class ValidateMultiplePaymentResponse : BaseResponse() {
    val errorsDetected: Boolean = false
    val warningsDetected: Boolean = false
    val validateMultiplePaymentResponseList: List<ValidateMultiplePayment> = arrayListOf()
}

class ValidateMultiplePayment {
    var paymentSequenceNumber: Int = 0
    var targetAccountNumber: String = ""
    var paymentToken: String = ""
    var realTimePaymentReferenceNumber: String = ""
    var warningMessageList: List<ResultMessage> = arrayListOf()
    var validationMessageList: List<ResultMessage> = arrayListOf()
}