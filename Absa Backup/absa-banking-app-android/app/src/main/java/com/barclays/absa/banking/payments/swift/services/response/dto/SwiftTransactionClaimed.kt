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
package com.barclays.absa.banking.payments.swift.services.response.dto

import styleguide.utils.extensions.toSwiftAmount
import java.util.*

class SwiftTransactionClaimed : SwiftTransaction() {

    var date: String = ""
    var transactionNumber: String = ""
    var mtcnNumber: String? = ""
    var swiftTransactionRefNumber: String = ""
    var originatingCountry: String = ""
    var senderName: String = ""
    var senderSurname: String = ""
    var foreignCurrencyAmount: String = ""
    var foreignCurrency: String = ""
    var totalDue: String = ""
    var endorsedDocIndicator: String = ""
    var transferType: String = ""

    fun getSwiftSenderFullName() = "$senderName $senderSurname"
    fun getFormattedSwiftForeignCurrencyAmount() = "${Currency.getInstance(foreignCurrency).symbol} ${foreignCurrencyAmount.toSwiftAmount()}"
    fun getFormattedTotalDueAmount(): String = totalDue.toSwiftAmount()
}