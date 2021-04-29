/*
 * Copyright (c) 2018 Absa Bank Limited, All Rights Reserved.
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
package com.barclays.absa.banking.boundary.shared.dto

open class SureCheckResponse : TransactionResponse() {

    open var sureCheckFlag: String? = null
    open var tvnFlag: String? = null
    open var correlationId: String? = null
    open var referenceNumber: String = ""
    open var transactionDate: String? = null
    open var cellnumber: String = ""
    open var email: String = ""
    open var notificationMethod: String = ""
}