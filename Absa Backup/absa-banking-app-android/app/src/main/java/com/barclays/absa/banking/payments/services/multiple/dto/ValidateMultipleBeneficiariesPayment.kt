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
package com.barclays.absa.banking.payments.services.multiple.dto

import com.barclays.absa.banking.boundary.model.Amount
import com.barclays.absa.banking.boundary.shared.dto.TransactionResponse
import com.fasterxml.jackson.annotation.JsonProperty
import java.io.Serializable
import java.util.*

class ValidateMultipleBeneficiariesPayment : TransactionResponse(), Serializable {

    @JsonProperty("transactionReferenceId")
    var transactionReferenceId: String? = null
    @JsonProperty("errors")
    var errors: ArrayList<String>? = null
    @JsonProperty("totalNormalAndIIPAmt")
    var totalNormalAndIIPAmt: Amount? = null
    @JsonProperty("totalFutureDatedAmt")
    var totalFutureDatedAmt: Amount? = null
    @JsonProperty("fundsTransferBeneficiaryValidateRespDTO")
    var beneficiaryList: List<BeneficiaryDetails>? = null
}
