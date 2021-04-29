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

package com.barclays.absa.banking.express.payments.payMultipleBeneficiary.dto

import za.co.absa.networking.dto.BaseResponse
import za.co.absa.networking.dto.ResultMessage
import java.util.*

class PayMultipleBeneficiaryResponse : BaseResponse() {
    val iipCode475Returned: Boolean = false
    val delayedIIPReturned: Boolean = false
    val errorsDetected: Boolean = false
    val alreadyProcessed: Boolean = false
    val pendingDetected: Boolean = false
    val successResponseList: List<PaymentResponse> = arrayListOf()
    val failureResponseList: List<PaymentResponse> = arrayListOf()
    val iipDelayedResponseList: List<PaymentResponse> = arrayListOf()
    val iipRetryResponseList: List<PaymentResponse> = arrayListOf()
    val pendingResponseList: List<PaymentResponse> = arrayListOf()
    val alreadyProcessedResponseList: List<PaymentResponse> = arrayListOf()
}

class PaymentResponse {
    var paymentSequenceNumber: Int = 0
    var targetAccountNumber: String = ""
    var paymentToken: String = ""
    var realTimePaymentReferenceNumber: String = ""
    var result: String = ""
    var paymentNumber: String = ""
    var uniqueEFTNumber: String = ""
    var paymentTransactionDateAndTime: Date = Date()
    var retryIIP: Int = 0
    var paymentReferenceNumber: String = ""
    var statusCode: Int = 0
    var warningMessageList: List<ResultMessage> = arrayListOf()
    var errorMessageList: List<ResultMessage> = arrayListOf()
}