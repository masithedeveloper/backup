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
package com.barclays.absa.banking.boundary.model

import android.os.Parcel
import android.os.Parcelable
import com.barclays.absa.banking.framework.KParcelable
import com.barclays.absa.banking.framework.app.BMBApplication
import com.barclays.absa.banking.framework.app.BMBConstants
import com.barclays.absa.banking.framework.utils.BMBLogger
import com.fasterxml.jackson.annotation.JsonProperty
import java.io.Serializable
import java.math.BigDecimal
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols

class Amount : Serializable, BMBConstants, KParcelable {

    @JsonProperty("amt")
    private var amount: String? = ""

    @JsonProperty("curr")
    var currency: String? = ""

    val amountValue: BigDecimal
        get() {
            try {
                return if (amount!!.contains(",")) {
                    BigDecimal(getAmount().replace(" ", "").replace(BMBConstants.NO_BREAK_SPACE, "").replace(",", ""))
                } else {
                    BigDecimal(amount)
                }

            } catch (e: NumberFormatException) {
                BMBLogger.d(e)
            }

            return BigDecimal("0.00")
        }

    val amountDouble: Double
        get() {
            try {
                return java.lang.Double.parseDouble(getAmount().replace(" ", "").replace(BMBConstants.NO_BREAK_SPACE, "").replace(",", ""))
            } catch (e: NumberFormatException) {
                BMBLogger.d(e)
            }

            return 0.00
        }

    val amountInt: Int
        get() = amountDouble.toInt()

    constructor(parcel: Parcel) : this() {
        amount = parcel.readString()
        currency = parcel.readString()
    }

    constructor(currency: String?, amount: String) {
        this.currency = currency ?: ""
        this.amount = amount
    }

    constructor(amount: String) {
        this.currency = "R"
        this.amount = amount
    }

    constructor(amount: BigDecimal) : this(amount.toString())

    constructor() {
        this.currency = ""
        this.amount = ""
    }

    fun getAmount(): String = if (amount.isNullOrEmpty()) "0.00" else amount.toString()

    fun setAmount(amount: String) {
        this.amount = amount
    }

    override fun toString(): String {
        if (currency!!.equals(BMBConstants.HYPHEN, ignoreCase = true)) {
            return BMBConstants.HYPHEN
        }

        if (amount!!.contains(BMBConstants.NA_STR)) {
            return BMBConstants.NA_STR
        }

        val maskCharacter = "*"
        if (amount!!.contains(maskCharacter)) {
            return amount.toString()
        }

        val decimalFormatSymbols = DecimalFormatSymbols(BMBApplication.getApplicationLocale())
        decimalFormatSymbols.decimalSeparator = '.'
        decimalFormatSymbols.groupingSeparator = ' '

        val decimalFormat = DecimalFormat()
        decimalFormat.groupingSize = 3
        decimalFormat.minimumFractionDigits = 2
        decimalFormat.maximumFractionDigits = 2
        decimalFormat.decimalFormatSymbols = decimalFormatSymbols
        return currency + BMBConstants.SPACE_STRING + decimalFormat.format(amountValue).trim { it <= ' ' }
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(amount)
        parcel.writeString(currency)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object {
        @JvmField
        val CREATOR = object : Parcelable.Creator<Amount> {
            override fun createFromParcel(parcel: Parcel): Amount {
                return Amount(parcel)
            }

            override fun newArray(size: Int): Array<Amount?> {
                return arrayOfNulls(size)
            }
        }
    }
}
