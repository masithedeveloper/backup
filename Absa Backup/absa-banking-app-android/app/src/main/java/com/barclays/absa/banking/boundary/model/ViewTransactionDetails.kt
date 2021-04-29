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

class ViewTransactionDetails : ResponseObject() {

    @JsonProperty("frmkdActNo")
    var maskedFromAccountNumber: String? = null
    @JsonProperty("txnPrt")
    var transactionPart: String? = null
    @JsonProperty("txnStatus")
    var transactionStatus: String? = null
    @JsonProperty("refNo")
    var referenceNumber: String? = null
    @JsonProperty("dt")
    var date: String? = null
    @JsonProperty("bal")
    var transactionAmount: Amount? = null
    @JsonProperty("frmActNo")
    var fromAccountNumber: String? = null
    @JsonProperty("frmActNam")
    var fromAccountName: String? = null
    @JsonProperty("status")
    var status: String? = null
    @JsonProperty("benShortNam")
    var beneficiaryShortName: String? = null
    @JsonProperty("benSurNam")
    var beneficiarySurName: String? = null
    @JsonProperty("networkProvNam")
    var networkProviderName: String? = null
    @JsonProperty("benNotice")
    var beneficiaryNotice: String? = null
    @JsonProperty("myNotice")
    var myNotice: String? = null
    @JsonProperty("benRecNam")
    var beneficiaryRecipientName: String? = null
    @JsonProperty("myRecNam")
    var myRecipientName: String? = null
    @JsonProperty("benAcctNumAtInst")
    var beneficiaryAccountNumberAtInstitution: String? = null
    @JsonProperty("benTyp")
    var beneficiaryType: String? = null
    @JsonProperty("benImg")
    var beneficiaryImage: String? = null
    @JsonProperty("benId")
    var beneficiaryId: String? = null
    @JsonProperty("benNo")
    var beneficiaryNumber: String? = null
    @JsonProperty("favStarInd")
    var favStarInd: String? = null
    @JsonProperty("benNam")
    var beneficiaryName: String? = null
    @JsonProperty("actNo")
    var accountNumber: String? = null
    @JsonProperty("bankNam")
    var bankName: String? = null
    @JsonProperty("branch")
    var branch: String? = null
    @JsonProperty("myRef")
    var myReference: String? = null
    @JsonProperty("myNoticeTyp")
    var myNoticeType: String? = null
    @JsonProperty("myNoticeDtl")
    var myNoticeDetails: String? = null
    @JsonProperty("benRef")
    var beneficiaryReference: String? = null
    @JsonProperty("benNoticeTyp")
    var benNoticeType: String? = null
    @JsonProperty("benNoticeDtl")
    var beneficiaryNoticeDetails: String? = null
    @JsonProperty("branchCd")
    var branchCode: String? = null
    @JsonProperty("actTyp")
    var accountType: String? = null
    @JsonProperty("uniqueEFT")
    var uniqueEFT: String? = null
    @JsonProperty("benStatusType")
    var beneficiaryStatusType: String? = null
    @JsonProperty("immediatePaymentAllowed")
    var immediatePaymentAllowed: String? = null
    @JsonProperty("hasImage")
    var hasImage: Boolean = false
    @JsonProperty("tiebNumber")
    var tiebNumber: String? = null
    @JsonProperty("ownNotificationDTO")
    var myNotificationDetails: MyNotificationDetails? = null

}
