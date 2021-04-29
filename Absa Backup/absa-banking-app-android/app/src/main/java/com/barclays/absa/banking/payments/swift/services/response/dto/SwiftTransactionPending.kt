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

import android.os.Parcelable
import com.fasterxml.jackson.annotation.JsonProperty
import kotlinx.android.parcel.Parcelize
import styleguide.utils.extensions.toSwiftAmount
import java.util.*

@Parcelize
data class SwiftTransactionPending(@JsonProperty("expectedPayoutAmount") var expectedPayoutAmount: String = "",
                                   @JsonProperty("senderFirstName") var senderFirstName: String = "",
                                   @JsonProperty("originatingCountry") var originatingCountry: String = "",
                                   @JsonProperty("toAccount") var toAccount: String = "",
                                   @JsonProperty("triNumber") var triNumber: String = "",
                                   @JsonProperty("foreignCurrencyCode") var foreignCurrencyCode: String = "",
                                   @JsonProperty("endDate") var endDate: String = "",
                                   @JsonProperty("beneficiaryType") var beneficiaryType: String = "",
                                   @JsonProperty("caseIDNumber") var caseIDNumber: String = "",
                                   @JsonProperty("originatingCountryCode") var originatingCountryCode: String = "",
                                   @JsonProperty("senderLastName") var senderLastName: String = "",
                                   @JsonProperty("transactionDate") var transactionDate: String = "",
                                   @JsonProperty("balance") var balance: String = "",
                                   @JsonProperty("whoWillPay") var whoWillPay: String = "",
                                   @JsonProperty("foreignCurrencyAmount") var foreignCurrencyAmount: String = "") : SwiftTransaction(), Parcelable {

    fun getFormattedSwiftForeignCurrencyAmount(): String = "${Currency.getInstance(foreignCurrencyCode).symbol} ${foreignCurrencyAmount.toSwiftAmount()}"
}