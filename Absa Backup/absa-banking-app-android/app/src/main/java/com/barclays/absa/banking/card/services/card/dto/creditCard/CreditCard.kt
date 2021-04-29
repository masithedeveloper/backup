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
package com.barclays.absa.banking.card.services.card.dto.creditCard

import android.os.Parcel
import android.os.Parcelable
import com.barclays.absa.banking.boundary.model.Amount
import com.barclays.absa.banking.boundary.model.Entry
import com.barclays.absa.banking.framework.KParcelable
import com.barclays.absa.banking.framework.data.ResponseObject
import com.fasterxml.jackson.annotation.JsonProperty

open class CreditCard() : ResponseObject(), Entry, KParcelable {

    @JsonProperty("id")
    val id: String? = null

    @JsonProperty("allowPayment")
    var allowPayment: Boolean = false

    @JsonProperty("accountName")
    val accountName: String? = null

    @JsonProperty("accountNo")
    var accountNo: String? = null

    @JsonProperty("hostProductType")
    val hostProductType: String? = null

    @JsonProperty("available")
    var available: Amount? = null

    @JsonProperty("balance")
    val balance: Amount? = null

    @JsonProperty("unclearedAmt")
    val unclearedAmt: String? = null

    @JsonProperty("limit")
    val limit: String? = null

    @JsonProperty("openedDate")
    val openedDate: String? = null

    @JsonProperty("options")
    val options: String? = null

    @JsonProperty("accessBits")
    val accessBits: String? = null

    @JsonProperty("accessTypeBits")
    val accessTypeBits: String? = null

    @JsonProperty("powerOfAttorney")
    val powerOfAttorney: Boolean = false

    @JsonProperty("status")
    val status: String? = null

    @JsonProperty("product")
    val product: String? = null

    @JsonProperty("productType")
    val productType: String? = null

    @JsonProperty("branch")
    val branch: String? = null

    @JsonProperty("corp")
    val corp: String? = null

    @JsonProperty("dateClosed")
    val dateClosed: String? = null

    @JsonProperty("accountBalance")
    val accountBalance: Amount? = null

    @JsonProperty("availableBalance")
    var availableBalance: Amount? = null

    @JsonProperty("existingOverdraftLimit")
    val existingOverdraftLimit: String? = null

    @JsonProperty("productCode")
    var productCode: String? = null

    @JsonProperty("withdrawalPerc")
    val withdrawalPerc: String? = null

    @JsonProperty("noticePeriod")
    val noticePeriod: String? = null

    @JsonProperty("liqEffDateOut")
    val liqEffDateOut: String? = null

    @JsonProperty("perEffDateOut")
    val perEffDateOut: String? = null

    @JsonProperty("effDateFalg")
    val effDateFalg: Boolean = false

    @JsonProperty("noticePeriodDays")
    val noticePeriodDays: String? = null

    @JsonProperty("liquidityPreference")
    val liquidityPreference: String? = null

    @JsonProperty("investTerm")
    val investTerm: String? = null

    @JsonProperty("investAmount")
    val investAmount: String? = null

    @JsonProperty("rateOption")
    val rateOption: String? = null

    @JsonProperty("nettposition")
    val nettposition: Double = 0.toDouble()

    @JsonProperty("accountTypeId")
    val accountTypeId: String? = null

    @JsonProperty("accountTypeName")
    var accountTypeName: String? = null

    @JsonProperty("accountTypedescription")
    var accountTypeDescription: String? = null

    @JsonProperty("drInterestRate")
    val drInterestRate: String? = null

    @JsonProperty("crInterestRate")
    val crInterestRate: String? = null

    @JsonProperty("creditLimit")
    var creditLimit: String? = null

    @JsonProperty("expiryDate")
    val expiryDate: String? = null

    @JsonProperty("arrearAmount")
    val arrearAmount: Amount? = null

    @JsonProperty("fullAmountPayable")
    val fullAmountPayable: Amount? = null

    @JsonProperty("fullAmountPayableLastStatementDate")
    val fullAmountPayableLastStatementDate: String? = null

    @JsonProperty("fullAmountPayableExcludingPayments")
    val fullAmountPayableExcludingPayments: String? = null

    @JsonProperty("minimumAmountPayable")
    var minimumAmountPayable: Amount? = null

    @JsonProperty("paymentDueDate")
    var paymentDueDate: String? = null

    @JsonProperty("straightBalance")
    val straightBalance: Amount? = null

    @JsonProperty("budgetedBalance")
    val budgetedBalance: Amount? = null

    @JsonProperty("authorisedAmount")
    val authorisedAmount: Amount? = null

    @JsonProperty("authorisedAmountUnclearedDeposits")
    val authorisedAmountUnclearedDeposits: Amount? = null

    var accessPin: String? = ""

    constructor(parcel: Parcel) : this() {
        allowPayment = parcel.readByte() != 0.toByte()
        accountNo = parcel.readString()
        available = parcel.readParcelable(Amount::class.java.classLoader)
        availableBalance = parcel.readParcelable(Amount::class.java.classLoader)
        productCode = parcel.readString()
        accountTypeName = parcel.readString()
        accountTypeDescription = parcel.readString()
        creditLimit = parcel.readString()
        minimumAmountPayable = parcel.readParcelable(Amount::class.java.classLoader)
        paymentDueDate = parcel.readString()
        accessPin = parcel.readString()
    }

    override fun getEntryType(): Int {
        return Entry.CREDIT_CARD
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeByte(if (allowPayment) 1 else 0)
        parcel.writeString(accountNo)
        parcel.writeParcelable(available, flags)
        parcel.writeParcelable(availableBalance, flags)
        parcel.writeString(productCode)
        parcel.writeString(accountTypeName)
        parcel.writeString(accountTypeDescription)
        parcel.writeString(creditLimit)
        parcel.writeParcelable(minimumAmountPayable, flags)
        parcel.writeString(paymentDueDate)
        parcel.writeString(accessPin)
    }

    companion object CREATOR : Parcelable.Creator<CreditCard> {
        override fun createFromParcel(parcel: Parcel): CreditCard {
            return CreditCard(parcel)
        }

        override fun newArray(size: Int): Array<CreditCard?> {
            return arrayOfNulls(size)
        }
    }
}
