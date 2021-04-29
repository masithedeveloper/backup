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

import com.barclays.absa.banking.framework.data.ResponseObject
import com.fasterxml.jackson.annotation.JsonProperty
import styleguide.utils.extensions.removeCurrency

class TransactionUnredeem : ResponseObject() {

    var beneficiaryId: String? = null
    var beneficiaryName: String? = ""
    var transactionDateTime: String = ""

    @JsonProperty("beneficiarySurName")
    var beneficiarySurname: String? = ""

    @JsonProperty("beneficiaryNickName")
    var beneficiaryNickname: String = ""

    @JsonProperty("cellPhoneNumber")
    var cellphoneNumber: String = ""
    var beneficiaryAccountNumber: String? = null

    @JsonProperty("statementTxnDesc1")
    var statementTransactionDescription1: String? = ""

    @JsonProperty("transactionRefNo")
    var transactionReferenceNumber: String? = ""

    var beneficiaryPayment: Boolean? = null
    var cashSendPlus: Boolean? = null
    var uniqueEFT: String? = null

    @JsonProperty("transactionCurrencyAmount")
    var amount: Amount? = null

    fun getAmountString(): String? {
        if (amount == null) return null

        return amount!!.getAmount().removeCurrency()
    }
}