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
import com.fasterxml.jackson.annotation.JsonProperty

import java.io.Serializable

class BeneficiaryDetails : Serializable {

    @JsonProperty("iipReferenceNumber")
    var iipReferenceNumber: String? = null
    //  @JsonProperty("benAcctNum")
    //var beneficiaryAccountNumber: Long? = null
    @JsonProperty("myDetl")
    var myDetails: String? = null
    @JsonProperty("imidPay")
    var immidiatePay: String? = null
    @JsonProperty("benId")
    var beneficiaryId: Int? = null
    @JsonProperty("thirDetl")
    var theirDetails: String? = null
    @JsonProperty("frmActNo")
    var fromAccountNumber: Long? = null
    @JsonProperty("branchNam")
    var branchName: String? = null
    @JsonProperty("bankNam")
    var bankName: String? = null
    @JsonProperty("instNam")
    var institutionName: Any? = null
    @JsonProperty("branchCd")
    var branchCode: Int? = null
    @JsonProperty("nowFlg")
    var nowFlg: String? = null
    @JsonProperty("frmActDesc")
    var fromAccounttDescription: Any? = null
    @JsonProperty("benStatusType")
    var beneficiaryStatusType: String? = null
    @JsonProperty("errorMsg")
    var errorMessage: Any? = null
    @JsonProperty("thirRef")
    var theirReference: String? = null
    @JsonProperty("myMethod")
    var myMethod: String? = null
    @JsonProperty("benNam")
    var beneficiaryName: String? = null
    @JsonProperty("validateMsg")
    var validateMessage: Any? = null
    @JsonProperty("warningMsg")
    var warningMessage: Any? = null
    @JsonProperty("maskedFrmActNo")
    var maskedFromAccountNumber: String? = null
    @JsonProperty("thirNotice")
    var theirNotice: String? = null
    @JsonProperty("myNotice")
    var myNotice: String? = null
    @JsonProperty("futureTxDate")
    var futureTransactionDate: String? = null
    @JsonProperty("popUpFlag")
    var popUpFlag: Any? = null
    @JsonProperty("thirMethod")
    var theirMethod: String? = null
    @JsonProperty("txnAmt")
    var transactionAmount: Amount? = null
    @JsonProperty("myRef")
    var myReference: String? = null
    @JsonProperty("hasImage")
    var hasBeneficiaryImage: String? = null
    @JsonProperty("imageName")
    var beneficiaryImageName: String? = null
}