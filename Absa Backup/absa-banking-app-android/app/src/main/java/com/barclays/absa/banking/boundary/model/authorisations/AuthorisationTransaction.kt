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
package com.barclays.absa.banking.boundary.model.authorisations

import com.barclays.absa.banking.boundary.model.Amount
import com.barclays.absa.banking.framework.data.ResponseObject
import com.fasterxml.jackson.annotation.JsonProperty


class AuthorisationTransaction : ResponseObject() {

    @JsonProperty("transactionTypeCode")
    val transactionTypeCode: String = ""

    @JsonProperty("transactionType")
    val transactionType: String = ""

    @JsonProperty("transactionDate")
    val transactionDate: String = ""

    @JsonProperty("amount")
    var amount: Amount? = Amount()

    @JsonProperty("accountNumber")
    val accountNumber: String = ""

    @JsonProperty("selectedOperator")
    val selectedOperator: String = ""

    @JsonProperty("operatorName")
    var operatorName: String = ""

    @JsonProperty("authUser")
    val authorisationUser: String = ""

    @JsonProperty("authUserName")
    val authorisedUserName: String = ""

    @JsonProperty("outstandingAuth")
    val outstandingAuthorisation: String = ""

    @JsonProperty("actionCode")
    val actionCode: String = ""

    @JsonProperty("productCode")
    val productCode: String = ""

    @JsonProperty("authFlag")
    val authorisationFlag: Boolean = false

    @JsonProperty("rejectFlag")
    val rejectFlag: Boolean = false

    @JsonProperty("txnDate")
    val txnDate: String = ""

    val transactionCategoryType: TransactionTypeCategory?
        get() {
            return when (transactionType) {
                "Pay Beneficiary" -> TransactionTypeCategory.PAY_BENEFICIARY
                "Inter Account Transfer" -> TransactionTypeCategory.INTER_ACCOUNT_TRANSFER
                "Prepaid" -> TransactionTypeCategory.PREPAID
                "Immediate Interbank Payment" -> TransactionTypeCategory.IMMEDIATE_INTERBANK_PAYMENT
                "Cash Send" -> TransactionTypeCategory.CASH_SEND
                "CashSend Plus" -> TransactionTypeCategory.CASH_SEND_PLUS
                "CashSend Plus Registration" -> TransactionTypeCategory.CASH_SEND_PLUS_REGISTRATION
                "CashSend Plus De-registration" -> TransactionTypeCategory.CASH_SEND_PLUS_DE_REGISTRATION
                "CashSend Plus Limits" -> TransactionTypeCategory.CASH_SEND_PLUS_LIMITS
                "Pay Once-off" -> TransactionTypeCategory.PAY_ONCE_OFF
                "Future Dated Payment" -> TransactionTypeCategory.FUTURE_DATED_PAYMENT
                "Delete CashSend Plus" -> TransactionTypeCategory.DELETE_CASH_SEND_PLUS
                else -> null
            }
        }

    enum class TransactionTypeCategory {
        ALL_TRANSACTIONS,
        PAY_BENEFICIARY,
        INTER_ACCOUNT_TRANSFER,
        PREPAID,
        IMMEDIATE_INTERBANK_PAYMENT,
        CASH_SEND,
        CASH_SEND_PLUS,
        CASH_SEND_PLUS_REGISTRATION,
        CASH_SEND_PLUS_DE_REGISTRATION,
        CASH_SEND_PLUS_LIMITS,
        PAY_ONCE_OFF,
        FUTURE_DATED_PAYMENT,
        DELETE_CASH_SEND_PLUS
    }
}
