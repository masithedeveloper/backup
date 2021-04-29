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

import com.barclays.absa.banking.boundary.shared.dto.SureCheckResponse
import com.fasterxml.jackson.annotation.JsonProperty

class AddNewWesternUnionBeneficiaryResponse : SureCheckResponse() {
    @JsonProperty("id")
    val benId: String? = ""
    val sequenceNumber: String? = ""
    val tiebNumber: String? = ""
    val status: String? = ""
    val beneficiaryShortName: String? = ""
    @JsonProperty("nonResidentAccountIdentifier")
    val residentialStatus: String? = ""
    @JsonProperty("accountNo")
    val accountNumber: String? = ""
    @JsonProperty("refName")
    val referenceName: String? = ""
    val eftNumber: String? = ""
    @JsonProperty("cifkey")
    val cifKey: String? = ""
    val transferType: String? = ""
    @JsonProperty("beneficiaryIFTType")
    val beneficiaryType: String? = ""
    val beneficiaryFirstName: String? = ""
    val ownNotificationType: String? = ""
    val foreignCurrencyWithZAR: String? = ""
    val beneficiarySurname: String? = ""
    val paymentType: String? = ""
    val subType: String? = ""
    @JsonProperty("lastPayDate")
    val lastPaymentDate: String? = ""
    val corelationId: String? = ""

}