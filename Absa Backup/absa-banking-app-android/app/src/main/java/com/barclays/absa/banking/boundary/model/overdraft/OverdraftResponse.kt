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
package com.barclays.absa.banking.boundary.model.overdraft

import com.barclays.absa.banking.boundary.shared.dto.TransactionResponse
import com.fasterxml.jackson.annotation.JsonProperty

open class OverdraftResponse : TransactionResponse() {
    @JsonProperty("status")
    var ficaAndCifStatus: String? = ""
    @JsonProperty("quoteRefNumber")
    var quoteReferenceNumber: String? = ""
    val productType: String? = ""
    @JsonProperty("createDate")
    val creationDate: String? = ""
    val income: String? = ""
    var preApprovedInd: String? = ""
    var approvedAmount: String? = ""
    val expenses: String? = ""
    var cppAmount: String? = ""
    var isCppChecked: String = "false"
    val totalMonthlyGrossIncome: String? = ""
    val totalMonthlyNetIncome: String? = ""
    val totalMonthlyLivingExpenses: String? = ""
    val totalMonthlyDisableIncome: String? = ""
    val customerBureauCommitments: String? = ""
    val customerMaintenanceExpenses: String? = ""
    var systemDecision: String? = ""
    var systemResult: String? = ""
    val quoteStatus: String? = ""
}