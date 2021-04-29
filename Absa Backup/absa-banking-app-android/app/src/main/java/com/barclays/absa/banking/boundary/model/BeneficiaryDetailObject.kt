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
import java.util.*

class BeneficiaryDetailObject : AddBeneficiaryObject(), Serializable {

    @JsonProperty("ownNotificationDTO")
    var ownNotificationDetails: BeneficiaryDetailObject? = null
        set(ownNotificationDetails) {
            field = ownNotificationDetails
            actualCellNo = field?.actualCellNo.toString()
            emailList = field?.emailList
        }

    @JsonProperty("favStarInd")
    var isFavourite: Boolean = false
        set(isFavourite) {
            field = isFavourite
            isSelected = field
        }

    var isSelected: Boolean = false

    @JsonProperty("benImg")
    var beneficiaryImageURL: String = ""

    @JsonProperty("benNam")
    var beneficiaryName: String = ""

    @JsonProperty("benSurNam")
    var beneficiarySurName: String = ""

    @JsonProperty("benShortNam")
    var beneficiaryShortName: String = ""

    var beneficiaryAcctNo: String = ""

    // TODO: 2021/01/15 double check naming
    var actNo: String = ""
        set(actNo) {
            field = actNo
            beneficiaryAcctNo = field
        }

    @JsonProperty("bankNam")
    var bankName: String = ""

    var branch: String = ""

    @JsonProperty("branchCd")
    var branchCode: String = ""

    @JsonProperty("myRef")
    var myReference: String = ""
        set(myReference) {
            field = myReference
            networkProviderName = field
        }

    var myNotice: String = ""

    @JsonProperty("myNoticeTyp")
    var myNoticeType: String = ""

    @JsonProperty("myNoticeDtl")
    var myNoticeDetail: String = ""

    @JsonProperty("benRef")
    var benReference: String = ""
        set(benReference) {
            field = benReference
            actHolder = field
            networkProviderCode = field
        }

    var benNotice: String = ""

    @JsonProperty("benNoticeTyp")
    var benNoticeType: String = ""

    @JsonProperty("benNoticeDtl")
    var benNoticeDetail: String = ""
        set(benNoticeDetail) {
            field = localizeContact(benNoticeDetail)
        }

    var networkProviderName: String = ""
    var networkProviderCode: String = ""

    var cellNo: String = ""
        set(cellNo) {
            field = localizeContact(cellNo)
        }
    var actualCellNo: String = ""
        set(actualCellNo) {
            field = localizeContact(actualCellNo)
        }

    var email: String = ""
    var faxCode: String = ""

    @JsonProperty("fax_Number")
    var faxNumber: String = ""

    var benCellNo: String = ""
    var benEmail: String = ""
    var benFaxCode: String = ""

    @JsonProperty("benFax_Number")
    var benFaxNumber: String = ""

    @JsonProperty("actTyp")
    var accountType: String = ""

    @JsonProperty("benStatusType")
    var beneficiaryStatusType: String = ""

    var instCode: String = ""
    var bankActNo: String = ""
    var actHolder: String = ""

    private var branchIIPStatus: Boolean = false

    var benAcctNumAtInst: String = ""
        set(benAcctNumAtInst) {
            field = benAcctNumAtInst
            bankActNo = field
        }

    var tiebNumber: String = ""

    var hasImage: Boolean = false

    @JsonProperty("emaillist")
    var emailList: ArrayList<String>? = null

    @JsonProperty("txnActvLst")
    var transactions: ArrayList<TransactionObject>? = null
    var immediatePaymentAllowed: Boolean = false

    fun setBranchIIPStatus(branchIIPStatus: String) {
        this.branchIIPStatus = true.toString().equals(branchIIPStatus, ignoreCase = true)
    }

    fun getBranchIIPStatus(): Boolean {
        return branchIIPStatus
    }

    private fun localizeContact(contac: String): String {
        var contact = contac

        if (contact.isNotEmpty()) {
            val code = "27"
            if (contact.indexOf("+") == 0) {
                contact = contact.substring(1)
            }
            if (contact.length > 10 && contact.substring(0, 2).equals(code, ignoreCase = true)) {
                return contact.replaceFirst("27".toRegex(), "0")
            }
        }
        return contact
    }
}