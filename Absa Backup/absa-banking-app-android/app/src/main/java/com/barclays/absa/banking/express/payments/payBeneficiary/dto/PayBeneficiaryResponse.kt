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

package com.barclays.absa.banking.express.payments.payBeneficiary.dto

import za.co.absa.networking.dto.BaseResponse
import java.util.*

class PayBeneficiaryResponse : BaseResponse() {
    val result: String = ""
    val paymentNumber: String = ""
    val uniqueEFTNumber: String = ""
    val paymentTransactionDateAndTime: Date = Date()
    val retryIIP: Int = 0
    val paymentReferenceNumber: String = ""
}