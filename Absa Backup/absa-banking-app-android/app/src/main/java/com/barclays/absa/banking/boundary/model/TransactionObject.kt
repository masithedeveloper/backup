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
package com.barclays.absa.banking.boundary.model

import com.fasterxml.jackson.annotation.JsonProperty
import java.io.Serializable

class TransactionObject : Serializable {
    @JsonProperty("dt")
    var date: String? = null

    var description: String? = null
    var balance: String? = null

    @JsonProperty("refNo")
    var referenceNumber: String? = null

    @JsonProperty("txnStatus")
    var transactionStatus: String? = null

    @JsonProperty("bal")
    var amount: Amount? = null

    @JsonProperty("txnPrt")
    var transactionPart: String? = null

    @JsonProperty("suppGrpCode")
    var supplierGroupCode: String? = null

    var tariffIndex: String? = null
    var receiptNumber: String? = null

    @JsonProperty("electricTokens")
    var purchaseHistoryElectricityTokens: List<PurchaseHistoryElectricityTokens>? = null

    @JsonProperty("electricDebts")
    var purchaseHistoryElectricityDebts: List<PurchaseHistoryElectricityDebts>? = null

    @JsonProperty("electricFixedCosts")
    var purchaseHistoryElectricityFixedCosts: List<PurchaseHistoryElectricityFixedCost>? = null

    override fun toString(): String {
        return ("date: " + date
                + " description: " + description
                + " balance:" + balance
                + " amount:" + amount)
    }
}
