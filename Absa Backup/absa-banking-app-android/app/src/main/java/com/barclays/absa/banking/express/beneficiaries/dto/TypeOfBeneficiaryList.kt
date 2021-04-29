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

package com.barclays.absa.banking.express.beneficiaries.dto

import com.fasterxml.jackson.annotation.JsonProperty

enum class TypeOfBeneficiaryList(val value: String) {
    @JsonProperty("I")
    InstitutionalBeneficiary("I"),

    @JsonProperty("V")
    OwnDefinedBeneficiary("V"),

    @JsonProperty("O")
    OnceOffPayment("O"),

    @JsonProperty("D")
    InstitutionalAndOwnDefinedBeneficiary("D"),

    @JsonProperty("P")
    InstitutionalAndOnceOffPaymentBeneficiary("P"),

    @JsonProperty("N")
    None("N")
}