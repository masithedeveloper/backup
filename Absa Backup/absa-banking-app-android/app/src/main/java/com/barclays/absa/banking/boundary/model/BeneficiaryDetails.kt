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

class BeneficiaryDetails : ResponseObject() {

    @JsonProperty("benTyp")
    val beneficiaryType: String? = null
    @JsonProperty("benImg")
    val beneficiaryImage: String? = null
    @JsonProperty("benId")
    val beneficiaryId: String? = null
    @JsonProperty("benNo")
    val beneficiaryNo: String? = null
    @JsonProperty("favStarInd")
    val favoriteIndicator: String? = null
    @JsonProperty("benNam")
    val beneficiaryName: String? = null
    @JsonProperty("actNo")
    val accountNumber: String? = null
    @JsonProperty("bankNam")
    val bankName: String? = null
    @JsonProperty("branch")
    val branch: String? = null
    @JsonProperty("myRef")
    var myReference: String? = null
    @JsonProperty("myNoticeTyp")
    val myNoticeType: String? = null
    @JsonProperty("myNoticeDtl")
    val myNoticeDetail: String = ""
    @JsonProperty("benRef")
    var beneficiaryReference: String = ""
    @JsonProperty("benNoticeTyp")
    val beneficiaryNoticeType: String? = null
    @JsonProperty("benNoticeDtl")
    val beneficiaryNoticeDetail: String = ""
    @JsonProperty("branchCd")
    val branchCode: String? = null
    @JsonProperty("actTyp")
    val accountType: String? = null
    @JsonProperty("benRecNam")
    val beneficiaryRecipientName: String? = null
    @JsonProperty("myRecNam")
    val myRecipientName: String? = null
    @JsonProperty("benAcctNumAtInst")
    val beneficiaryInstitutionAccountNumber: String? = null
    @JsonProperty("benNotice")
    val beneficiaryNotice: String? = null
    @JsonProperty("myNotice")
    val myNotice: String? = null
    @JsonProperty("networkProvNam")
    val networkProviderName: String? = null
    @JsonProperty("benShortNam")
    val beneficiaryShortName: String? = null
    @JsonProperty("benSurNam")
    val beneficiarySurName: String? = null
    @JsonProperty("status")
    val status: String? = null
    @JsonProperty("uniqueEFT")
    val uniqueEFT: String? = null
    @JsonProperty("benStatusType")
    val beneficiaryStatusType: String? = null
    @JsonProperty("immediatePaymentAllowed")
    val immediatePaymentAllowed: String? = null
    @JsonProperty("hasImage")
    val hasImage: String? = null
    @JsonProperty("tiebNumber")
    val tiebNumber: String? = null
    @JsonProperty("imageName")
    val imageName: String? = null

}
