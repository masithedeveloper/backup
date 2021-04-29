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

import com.barclays.absa.banking.boundary.shared.dto.SureCheckResponse

class OnceOffPaymentConfirmationResponse : SureCheckResponse() {
    var txnRef: String? = null
    var msg: String? = null
    var fromAccountNumber: String? = null
    var fromAccountType: String? = null
    var description: String? = null
    var beneficiaryName: String? = null
    var acctAtInst: String? = null
    var institutionName: String? = null
    var instCode: String? = null
    var beneficiaryType: String? = null
    var bankName: String? = null
    var branchName: String? = null
    var branchCode: String? = null
    var accountNumber: String? = null
    var accountType: String? = null
    var paymentDate: String? = null
    var immediatePay: String? = null
    var myReference: String? = null
    var myNotice: String? = null
    var myMethod: String? = null
    var myMethodDetails: String? = null
    var myEmail: String? = null
    var myMobile: String? = null
    var myFaxNum: String? = null
    var beneficiaryReference: String? = null
    var beneficiaryNotice: String? = null
    var beneficiaryMethod: String? = null
    var beneficiaryMethodDetails: String? = null
    var benMobile: String? = null
    var benEmail: String? = null
    var benFaxNum: String? = null
    var futureDate: String? = null
    var myFaxCode: String? = null
    var benFaxCode: String? = null
    var maskedFromAccountNumber: String? = null
    var accept_terms: String? = null
    var bankAccountNumber: String? = null
    var bankAccountHolder: String? = null
    var institutionCode: String? = null
    var iipTrackingNo: String? = null
    var transactionAmount: Amount? = null
    var popUpFlag: String? = null
}