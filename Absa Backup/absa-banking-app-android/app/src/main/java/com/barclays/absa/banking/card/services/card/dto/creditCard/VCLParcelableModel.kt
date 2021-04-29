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
import com.barclays.absa.banking.framework.KParcelable
import styleguide.utils.extensions.removeCurrencyDefaultZero

class VCLParcelableModel() : KParcelable {

    var incomeAndExpense: IncomeAndExpensesResponse? = IncomeAndExpensesResponse()
    var creditCardVCLGadget: CreditCardGadget? = null
    var currentCreditLimit: String? = ""
    var newCreditLimitAmount: String? = ""
    var creditCardNumber: String? = ""
    var bank: String? = ""
    var accountNumber: String? = ""
    var accountType: String? = ""
    var branchCode: String? = ""
    var emailAddress: String? = ""
    var creditLimitIncreaseAmount: String? = "0.00"
    var isTermsAccepted: Boolean = false
    var isFreshBureauDataAvailable: String? = "false"
    var maintenanceExpense: String? = "0.00"
    var groceriesExpense: String? = "0.00"
    var municipalLevies: String? = "0.00"
    var domesticWorkerExpense: String? = "0.00"
    var educationExpense: String? = "0.00"
    var transportExpense: String? = "0.00"
    var entertainmentExpense: String? = "0.00"
    var otherLivingExpense: String? = "0.00"
    var totalFixedAmount: String? = "0.00"
    var loansFixedAmount: String? = "0.00"
    var creditCardFixedAmount: String? = "0.00"
    var assetFinanceFixedAmount: String? = "0.00"
    var retailAccountsFixedAmount: String? = "0.00"
    var bondFixedAmount: String? = "0.00"
    var otherFixedAmount: String? = "0.00"
    var deaTermsAccepted: Boolean = false

    constructor(parcel: Parcel) : this() {
        incomeAndExpense = parcel.readParcelable(IncomeAndExpensesResponse::class.java.classLoader)
        creditCardVCLGadget = parcel.readParcelable(CreditCardGadget::class.java.classLoader)
        currentCreditLimit = parcel.readString()
        newCreditLimitAmount = parcel.readString()
        creditCardNumber = parcel.readString()
        bank = parcel.readString()
        accountNumber = parcel.readString()
        accountType = parcel.readString()
        branchCode = parcel.readString()
        emailAddress = parcel.readString()
        creditLimitIncreaseAmount = parcel.readString()
        isTermsAccepted = parcel.readByte() != 0.toByte()
        isFreshBureauDataAvailable = parcel.readString()
        maintenanceExpense = parcel.readString()
        groceriesExpense = parcel.readString()
        municipalLevies = parcel.readString()
        domesticWorkerExpense = parcel.readString()
        educationExpense = parcel.readString()
        transportExpense = parcel.readString()
        entertainmentExpense = parcel.readString()
        otherLivingExpense = parcel.readString()
        totalFixedAmount = parcel.readString()
        loansFixedAmount = parcel.readString()
        creditCardFixedAmount = parcel.readString()
        assetFinanceFixedAmount = parcel.readString()
        retailAccountsFixedAmount = parcel.readString()
        bondFixedAmount = parcel.readString()
        otherFixedAmount = parcel.readString()
        deaTermsAccepted = parcel.readByte() != 0.toByte()
    }

    fun populateInstalmentAmounts() {
        val listOfTransactions: List<BureauDebtInstalments>? = incomeAndExpense?.bureauDebtInstalmentList
        if (listOfTransactions != null) {
            for (item: BureauDebtInstalments in listOfTransactions) {
                val amount: String = item.bureauCategoryAmount.toString()
                when (item.bureauCategory.toString()) {
                    in LOANS -> loansFixedAmount = amount
                    in CREDIT_CARDS -> creditCardFixedAmount = amount
                    in ASSET_FINANCE -> assetFinanceFixedAmount = amount
                    in RETAIL_ACCOUNTS -> retailAccountsFixedAmount = amount
                    in BOND_AND_RENTALS -> bondFixedAmount = amount
                    in OTHER_PAYMENTS -> otherFixedAmount = amount
                }
            }
        }
        totalFixedAmount = calculateTotalFixedExpense().toString()
        calculateTotalMonthlyLivingExpense()
    }

    private fun calculateTotalFixedExpense(): Double {
        val loans = loansFixedAmount.removeCurrencyDefaultZero()
        val creditCard = creditCardFixedAmount.removeCurrencyDefaultZero()
        val asset = assetFinanceFixedAmount.removeCurrencyDefaultZero()
        val retail = retailAccountsFixedAmount.removeCurrencyDefaultZero()
        val bond = bondFixedAmount.removeCurrencyDefaultZero()
        val other = otherFixedAmount.removeCurrencyDefaultZero()

        return loans + creditCard + asset + retail + bond + other
    }

    private fun calculateTotalMonthlyLivingExpense() {
        val total: Double
        val cellphone = incomeAndExpense?.telephoneExpense.removeCurrencyDefaultZero()
        val security = incomeAndExpense?.securityExpense.removeCurrencyDefaultZero()
        val insurance = incomeAndExpense?.insuranceExpense.removeCurrencyDefaultZero()
        val other = creditCardVCLGadget?.incomeAndExpenses?.totalMonhtlyLivingExpenses.removeCurrencyDefaultZero() - (cellphone + security + insurance)
        total = cellphone + security + insurance + other
        creditCardVCLGadget?.incomeAndExpenses?.totalMonhtlyLivingExpenses = total.toString()
        otherLivingExpense = other.toString()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeParcelable(incomeAndExpense, flags)
        parcel.writeParcelable(creditCardVCLGadget, flags)
        parcel.writeString(currentCreditLimit)
        parcel.writeString(newCreditLimitAmount)
        parcel.writeString(creditCardNumber)
        parcel.writeString(bank)
        parcel.writeString(accountNumber)
        parcel.writeString(accountType)
        parcel.writeString(branchCode)
        parcel.writeString(emailAddress)
        parcel.writeString(creditLimitIncreaseAmount)
        parcel.writeByte(if (isTermsAccepted) 1 else 0)
        parcel.writeString(isFreshBureauDataAvailable)
        parcel.writeString(maintenanceExpense)
        parcel.writeString(groceriesExpense)
        parcel.writeString(municipalLevies)
        parcel.writeString(domesticWorkerExpense)
        parcel.writeString(educationExpense)
        parcel.writeString(transportExpense)
        parcel.writeString(entertainmentExpense)
        parcel.writeString(otherLivingExpense)
        parcel.writeString(totalFixedAmount)
        parcel.writeString(loansFixedAmount)
        parcel.writeString(creditCardFixedAmount)
        parcel.writeString(assetFinanceFixedAmount)
        parcel.writeString(retailAccountsFixedAmount)
        parcel.writeString(bondFixedAmount)
        parcel.writeString(otherFixedAmount)
        parcel.writeByte(if (deaTermsAccepted) 1 else 0)
    }

    companion object {
        private const val OTHER_PAYMENTS = "AA"
        private const val CREDIT_CARDS = "CC"
        private const val LOANS = "LB"
        private const val BOND_AND_RENTALS = "MB"
        private const val RETAIL_ACCOUNTS = "RI"
        private const val ASSET_FINANCE = "VF"

        @JvmField
        val CREATOR = object : Parcelable.Creator<VCLParcelableModel> {
            override fun createFromParcel(parcel: Parcel): VCLParcelableModel {
                return VCLParcelableModel(parcel)
            }

            override fun newArray(size: Int): Array<VCLParcelableModel?> {
                return arrayOfNulls(size)
            }
        }
    }
}