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

import com.fasterxml.jackson.annotation.JsonProperty
import java.io.Serializable

class ValidatePaymentDetails : Serializable {
    var residingCountry: String? = ""
    var fromAccountNumber: String? = ""
    var beneficiaryFirstName: String? = ""
    var beneficiarySurName: String? = ""
    var beneficiaryAccountNumber: String? = ""
    var gender: String? = ""
    var nonResidentAccountIdentifier: String? = ""
    var amountToSend: String? = ""
    var receivedAmount: String? = ""
    var beneficiaryId: String? = ""
    var destinationCountryCode: String? = ""
    var destinationCountryDescription: String? = ""
    var state: String? = ""
    var destinationState: String? = ""
    var destinationCity: String? = ""
    var sendCurrency: String? = ""
    var payoutCurrency: String? = ""
    var streetAddress: String? = ""
    var city: String? = ""
    var availableBalance: String? = ""
    var foreignCurrencyWithZar: String? = ""
    var accountType: String? = ""
    var testQuestion: String? = ""
    var testAnswer: String? = ""
    @JsonProperty("displayQuestionAndAnswer")
    var isDisplayQuestionAndAnswer: Boolean = false
}