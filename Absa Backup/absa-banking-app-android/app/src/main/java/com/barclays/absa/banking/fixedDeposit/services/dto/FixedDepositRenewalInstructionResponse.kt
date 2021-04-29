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

package com.barclays.absa.banking.fixedDeposit.services.dto

import com.barclays.absa.banking.boundary.shared.dto.TransactionResponse
import com.barclays.absa.banking.shared.BaseModel
import com.fasterxml.jackson.annotation.JsonProperty

class FixedDepositRenewalInstructionResponse(@JsonProperty("tdRenewalInstructionDetails")
                                             var renewalInstructionDetails: RenewalInstructionDetails = RenewalInstructionDetails()) : TransactionResponse()

class RenewalInstructionDetails(
        var accountNumber: String = "",
        var amount: String = "",
        var automaticRenewal: String = "",
        var capDay: String = "",
        var capFreq: String = "",
        var depositBalance: String = "",
        var endDate: String = "",
        var eventNo: String = "",
        var isMatured: Boolean = false,
        var islamicIndicator: String = "",
        var maturityDate: String = "",
        var maxMaturityDate: String = "",
        var maxTerm: String = "",
        var minMaturityDate: String = "",
        var minTerm: String = "",
        var minimumDeposit: String = "",
        var nextCapDate: String = "",
        var productCategory: String = "",
        var productCode: String = "",
        var startDate: String = "",
        var status: String = "",
        var submittedOn: String = "",
        var term: String = "") : BaseModel