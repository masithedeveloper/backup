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
package com.barclays.absa.banking.payments.international.services.dto

import com.barclays.absa.banking.boundary.shared.dto.TransactionResponse
import com.fasterxml.jackson.annotation.JsonProperty

class QuoteDetails : TransactionResponse() {
    var transactionReferenceNumber: String? = ""
    var foreignCurrency: String? = ""
    var foreignAmount: String? = ""
    @JsonProperty("usdconversionRate")
    var usdConversionRate: String? = ""
    var expectedPayoutCurrency: String? = ""
    var expectedPayoutAmount: String? = ""
    var destinationCurrencyRate: String? = ""
    var localCurrency: String? = ""
    var localAmount: String? = ""
    var commissionAmountFee: String? = ""
    var vatAmount: String? = ""
    var totalDue: String? = ""
    var fromAccount: String? = ""
    var senderFirstName: String? = ""
    var senderSurname: String? = ""
    var receiverFirstName: String? = ""
    var receiverSurname: String? = ""
    var valueDate: String? = ""
    var natureOfPaymentDTO: Array<NatureOfPayment>? = null
    @JsonProperty("mtcnCode")
    var moneyTransferControlNumberCode: String? = ""
    var destinationCountry: String? = ""
    var destinationCity: String? = ""
    var originatingCountry: String? = ""
    var testQuestion: String? = ""
    var testAnswer: String? = ""
    var quoteTimer: String? = ""

}