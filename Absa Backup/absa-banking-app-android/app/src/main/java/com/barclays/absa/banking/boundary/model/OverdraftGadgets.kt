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

import android.os.Parcel
import android.os.Parcelable
import com.barclays.absa.banking.framework.KParcelable

data class OverdraftGadgets(var offersOverdraftEnum: String? = "",
                            var existingOverdraftLimit: String? = "",
                            var newOverdraftLimit: String? = "") : KParcelable {

    constructor(parcel: Parcel) : this(
            parcel.readString(),
            parcel.readString(),
            parcel.readString())

    fun getNewOverdraftLimitValue(): Double {
        return toDouble(newOverdraftLimit)
    }

    fun getExistingOverdraftLimitValue(): Double {
        return toDouble(existingOverdraftLimit)
    }

    fun getOverdraftAmount(): Double {
        return if (getNewOverdraftLimitValue() != 0.0) {
            getNewOverdraftLimitValue()
        } else if (getExistingOverdraftLimitValue() != 0.0) {
            getExistingOverdraftLimitValue()
        } else {
            0.0
        }
    }

    fun toDouble(stringValue: String?): Double {
        var value = stringValue?.toDoubleOrNull()
        return if (value == null) 0.0 else value
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(offersOverdraftEnum)
        parcel.writeString(existingOverdraftLimit)
        parcel.writeString(newOverdraftLimit)
    }

    companion object CREATOR : Parcelable.Creator<OverdraftGadgets> {
        override fun createFromParcel(parcel: Parcel): OverdraftGadgets {
            return OverdraftGadgets(parcel)
        }

        override fun newArray(size: Int): Array<OverdraftGadgets?> {
            return arrayOfNulls(size)
        }
    }
}
