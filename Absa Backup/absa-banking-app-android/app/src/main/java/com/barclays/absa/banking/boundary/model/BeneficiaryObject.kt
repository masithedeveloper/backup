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

open class BeneficiaryObject : Serializable, Entry {

    @JsonProperty("benId")
    var beneficiaryID: String = ""

    var bankName: String = ""
    var accountType: String = ""
    @JsonProperty("lastTxnAmt")
    var lastTransactionAmount: Amount? = null
    @JsonProperty("lastTxnDate")
    var lastTransactionDate: String = ""
    @JsonProperty("benNam")
    var beneficiaryName: String = ""
    var imageName: String? = ""
    @JsonProperty("hasImage")
    var imageAvailable: String = ""
    @JsonProperty("tiebNumber")
    var tiebNumber: String = ""

    var uniqueEFT: String = ""
    @JsonProperty("benRef")
    var beneficiaryReference: String = ""
    @JsonProperty("myRef")
    var myReference: String = ""
    @JsonProperty("isFav")
    var favourite: String = ""
    @JsonProperty("txnTyp")
    var beneficiaryType: String = ""
    @JsonProperty("image")
    var beneficiaryImageURL: String = ""

    var isImmediatePayment: Boolean = false
    // Sometimes "benNo" and other times "actNo" is used to set the variable, so which is it?
    // Based on what i can see it should be "actNo". Sunil please check this.
    @JsonProperty("actNo")
    var beneficiaryAccountNumber: String = ""
    @JsonProperty("benSurNam")
    var beneficiarySurname: String = ""

    val isImageAvailable: Boolean
        get() = this.imageAvailable.equals("Y", ignoreCase = true)

    val isFavourite: Boolean
        get() = this.favourite.equals("Y", ignoreCase = true)

    override fun toString(): String {
        return (this.beneficiaryID + ":"
                + "beneficiaryName: " + this.beneficiaryName
                + " isfavourite: " + this.favourite
                + " lasTtransactionDate:" + this.lastTransactionDate
                + " lastTransactionAmount:" + lastTransactionAmount
                + " reference:" + this.myReference)
    }

    override fun hashCode(): Int {
        return super.hashCode()
    }

    override fun equals(other: Any?): Boolean {
        if (other == null) {
            return false
        } else if (other is BeneficiaryObject) {
            val beneficiaryObject = other as BeneficiaryObject?
            if (beneficiaryObject?.beneficiaryID != null) {
                return beneficiaryObject.beneficiaryID.equals(this.beneficiaryID, ignoreCase = true)
            }
        }
        return super.equals(other)
    }

    override fun getEntryType(): Int {
        return Entry.BENEFICIARY
    }
}
