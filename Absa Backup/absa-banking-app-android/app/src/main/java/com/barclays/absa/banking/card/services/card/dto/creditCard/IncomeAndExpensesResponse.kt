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
import com.barclays.absa.banking.framework.KParcelable
import java.io.Serializable

class IncomeAndExpensesResponse() : Serializable, KParcelable {
    var insuranceExpense: String = "0.0"
    var securityExpense: String = "0.0"
    var telephoneExpense: String = "0.0"
    var totalGrossMonthlyIncome: String = "0.0"
    var totalMonhtlyLivingExpenses: String = "0.0"
    var totalNetMonthlyIncome: String = "0.0"
    var bureauDebtInstalmentList: MutableList<BureauDebtInstalments> = mutableListOf()
    var totalMonthlyFixedDebtInstallment: String = "0.0"

    constructor(parcel: Parcel) : this() {
        parcel.readString()?.let { insuranceExpense = it }
        parcel.readString()?.let { securityExpense = it }
        parcel.readString()?.let { telephoneExpense = it }
        parcel.readString()?.let { totalGrossMonthlyIncome = it }
        parcel.readString()?.let { totalMonhtlyLivingExpenses = it }
        parcel.readString()?.let { totalNetMonthlyIncome = it }
        parcel.readString()?.let { totalMonthlyFixedDebtInstallment = it }
        parcel.readList(bureauDebtInstalmentList as List<BureauDebtInstalments>, BureauDebtInstalments::class.java.classLoader)
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(insuranceExpense)
        parcel.writeString(securityExpense)
        parcel.writeString(telephoneExpense)
        parcel.writeString(totalGrossMonthlyIncome)
        parcel.writeString(totalMonhtlyLivingExpenses)
        parcel.writeString(totalNetMonthlyIncome)
        parcel.writeString(totalMonthlyFixedDebtInstallment)
        parcel.writeList(bureauDebtInstalmentList as List<BureauDebtInstalments>)
    }

    companion object {
        @JvmField
        val CREATOR = object : Parcelable.Creator<IncomeAndExpensesResponse> {
            override fun createFromParcel(parcel: Parcel): IncomeAndExpensesResponse {
                return IncomeAndExpensesResponse(parcel)
            }

            override fun newArray(size: Int): Array<IncomeAndExpensesResponse?> {
                return arrayOfNulls(size)
            }
        }
    }
}