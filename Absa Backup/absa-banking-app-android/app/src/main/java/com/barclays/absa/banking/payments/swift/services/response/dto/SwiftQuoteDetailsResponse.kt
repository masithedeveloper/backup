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

import com.barclays.absa.banking.shared.BaseModel
import styleguide.utils.extensions.toSwiftAmount
import java.util.*

class SwiftQuoteDetailsResponse : BaseModel {
    var caseIDNumber: String = ""
    val commissionAmountFee: String = ""
    val beneficiaryName: String = ""
    val destinationCurrencyRate: String = ""
    val valueDate: String = ""
    val localCurrency: String = ""
    val localAmount: String = ""
    val vatAmount: String = ""
    val totalDue: String = ""
    val beneficiarySurname: String = ""
    val cmaIndicator: Boolean = false
    val quoteTimer: String = ""

    fun getFormattedDestinationCurrencyRate(): String = destinationCurrencyRate.toSwiftAmount()

    fun formattedLocalCurrencyAmount(): String {
        val symbol = if (localCurrency.equals("ZAR", true)) "R" else Currency.getInstance(localCurrency).symbol
        return "$symbol ${localAmount.toSwiftAmount()}"
    }
}