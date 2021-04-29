/*
 *  Copyright (c) 2018 Absa Bank Limited, All Rights Reserved.
 *
 *  This code is confidential to Absa Bank Limited and shall not be disclosed
 *  outside the Bank without the prior written permission of the Absa Legal
 *
 *  In the event that such disclosure is permitted the code shall not be copied
 *  or distributed other than on a need-to-know basis and any recipients may be
 *  required to sign a confidentiality undertaking in favor of Absa Bank
 *  Limited
 *
 */
package com.barclays.absa.banking.card.services.card.dto.creditCard

import android.os.Parcel
import android.os.Parcelable
import com.barclays.absa.banking.boundary.model.Amount
import com.barclays.absa.banking.framework.KParcelable

class CreditCardAccount() : KParcelable {
    var id: String? = ""
    var allowPayment: Boolean? = false
    var accountName: String? = ""
    var accountNo: String? = ""
    var hostProductType: String? = ""
    var available: Amount? = Amount()
    var balance: Amount? = Amount()
    var unclearedAmt: String? = ""
    var limit: String? = ""
    var openedDate: String? = ""
    var options: String? = ""
    var accessBits: String? = ""
    var accessTypeBits: String? = ""
    var powerOfAttorney: String? = ""
    var status: String? = ""
    var product: String? = ""
    var productType: String? = ""
    var branch: String? = ""
    var corp: String? = ""
    var dateClosed: String? = ""
    var existingOverdraftLimit: String? = ""
    var accountBalance: Amount? = Amount()
    var availableBalance: Amount? = Amount()
    var productCode: String? = ""
    var withdrawalPerc: String? = ""
    var noticePeriod: String? = ""
    var liqEffDateOut: String? = ""
    var perEffDateOut: String? = ""
    var effDateFalg: Boolean? = false
    var noticePeriodDays: String? = ""
    var liquidityPreference: String? = ""
    var investTerm: String? = ""
    var investAmount: String? = ""
    var rateOption: String? = ""
    var nettposition: Double? = 0.0
    var accountTypeId: String? = ""
    var accountTypeName: String? = ""
    var accountTypedescription: String? = ""
    var drInterestRate: String? = ""
    var crInterestRate: String? = ""
    var creditLimit: String? = ""
    var expiryDate: String? = ""
    var arrearAmount: Amount? = Amount()
    var fullAmountPayable: Amount? = Amount()
    var fullAmountPayableLastStatementDate: String? = ""
    var fullAmountPayableExcludingPayments: String? = ""
    var minimumAmountPayable: Amount? = Amount()
    var paymentDueDate: String? = ""
    var straightBalance: Amount? = Amount()
    var budgetedBalance: Amount? = Amount()
    var authorisedAmount: Amount? = Amount()
    var authorisedAmountUnclearedDeposits: String? = ""

    constructor(parcel: Parcel) : this() {
        id = parcel.readString()
        allowPayment = parcel.readValue(Boolean::class.java.classLoader) as? Boolean
        accountName = parcel.readString()
        accountNo = parcel.readString()
        hostProductType = parcel.readString()
        available = parcel.readParcelable(Amount::class.java.classLoader)
        balance = parcel.readParcelable(Amount::class.java.classLoader)
        unclearedAmt = parcel.readString()
        limit = parcel.readString()
        openedDate = parcel.readString()
        options = parcel.readString()
        accessBits = parcel.readString()
        accessTypeBits = parcel.readString()
        powerOfAttorney = parcel.readString()
        status = parcel.readString()
        product = parcel.readString()
        productType = parcel.readString()
        branch = parcel.readString()
        corp = parcel.readString()
        dateClosed = parcel.readString()
        existingOverdraftLimit = parcel.readString()
        accountBalance = parcel.readParcelable(Amount::class.java.classLoader)
        availableBalance = parcel.readParcelable(Amount::class.java.classLoader)
        productCode = parcel.readString()
        withdrawalPerc = parcel.readString()
        noticePeriod = parcel.readString()
        liqEffDateOut = parcel.readString()
        perEffDateOut = parcel.readString()
        effDateFalg = parcel.readValue(Boolean::class.java.classLoader) as? Boolean
        noticePeriodDays = parcel.readString()
        liquidityPreference = parcel.readString()
        investTerm = parcel.readString()
        investAmount = parcel.readString()
        rateOption = parcel.readString()
        nettposition = parcel.readValue(Double::class.java.classLoader) as? Double
        accountTypeId = parcel.readString()
        accountTypeName = parcel.readString()
        accountTypedescription = parcel.readString()
        drInterestRate = parcel.readString()
        crInterestRate = parcel.readString()
        creditLimit = parcel.readString()
        expiryDate = parcel.readString()
        arrearAmount = parcel.readParcelable(Amount::class.java.classLoader)
        fullAmountPayable = parcel.readParcelable(Amount::class.java.classLoader)
        fullAmountPayableLastStatementDate = parcel.readString()
        fullAmountPayableExcludingPayments = parcel.readString()
        minimumAmountPayable = parcel.readParcelable(Amount::class.java.classLoader)
        paymentDueDate = parcel.readString()
        straightBalance = parcel.readParcelable(Amount::class.java.classLoader)
        budgetedBalance = parcel.readParcelable(Amount::class.java.classLoader)
        authorisedAmount = parcel.readParcelable(Amount::class.java.classLoader)
        authorisedAmountUnclearedDeposits = parcel.readString()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
        parcel.writeValue(allowPayment)
        parcel.writeString(accountName)
        parcel.writeString(accountNo)
        parcel.writeString(hostProductType)
        parcel.writeParcelable(available, flags)
        parcel.writeParcelable(balance, flags)
        parcel.writeString(unclearedAmt)
        parcel.writeString(limit)
        parcel.writeString(openedDate)
        parcel.writeString(options)
        parcel.writeString(accessBits)
        parcel.writeString(accessTypeBits)
        parcel.writeString(powerOfAttorney)
        parcel.writeString(status)
        parcel.writeString(product)
        parcel.writeString(productType)
        parcel.writeString(branch)
        parcel.writeString(corp)
        parcel.writeString(dateClosed)
        parcel.writeString(existingOverdraftLimit)
        parcel.writeParcelable(accountBalance, flags)
        parcel.writeParcelable(availableBalance, flags)
        parcel.writeString(productCode)
        parcel.writeString(withdrawalPerc)
        parcel.writeString(noticePeriod)
        parcel.writeString(liqEffDateOut)
        parcel.writeString(perEffDateOut)
        parcel.writeValue(effDateFalg)
        parcel.writeString(noticePeriodDays)
        parcel.writeString(liquidityPreference)
        parcel.writeString(investTerm)
        parcel.writeString(investAmount)
        parcel.writeString(rateOption)
        parcel.writeValue(nettposition)
        parcel.writeString(accountTypeId)
        parcel.writeString(accountTypeName)
        parcel.writeString(accountTypedescription)
        parcel.writeString(drInterestRate)
        parcel.writeString(crInterestRate)
        parcel.writeString(creditLimit)
        parcel.writeString(expiryDate)
        parcel.writeParcelable(arrearAmount, flags)
        parcel.writeParcelable(fullAmountPayable, flags)
        parcel.writeString(fullAmountPayableLastStatementDate)
        parcel.writeString(fullAmountPayableExcludingPayments)
        parcel.writeParcelable(minimumAmountPayable, flags)
        parcel.writeString(paymentDueDate)
        parcel.writeParcelable(straightBalance, flags)
        parcel.writeParcelable(budgetedBalance, flags)
        parcel.writeParcelable(authorisedAmount, flags)
        parcel.writeString(authorisedAmountUnclearedDeposits)
    }

    companion object {
        @JvmField
        val CREATOR = object : Parcelable.Creator<CreditCardAccount> {
            override fun createFromParcel(parcel: Parcel): CreditCardAccount {
                return CreditCardAccount(parcel)
            }

            override fun newArray(size: Int): Array<CreditCardAccount?> {
                return arrayOfNulls(size)
            }
        }
    }
}
