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
package com.barclays.absa.banking.boundary.model.creditCardInsurance

import android.os.Parcel
import android.os.Parcelable
import com.barclays.absa.banking.framework.KParcelable

class RolePlayers() : KParcelable {
    var title: String? = ""
    var initials: String? = ""
    var firstName: String? = ""
    var surname: String? = ""
    var gender: String? = ""
    var dateOfBirth: String? = ""
    var coverAmount: String? = ""
    var premiumAmount: String? = ""
    var relationship: String? = ""
    var benefitCode: String? = ""
    var benefitCounter: String? = ""
    var descriptionEnglish: String? = ""
    var descriptionAfrican: String? = ""

    constructor(parcel: Parcel) : this() {
        title = parcel.readString()
        initials = parcel.readString()
        firstName = parcel.readString()
        surname = parcel.readString()
        gender = parcel.readString()
        dateOfBirth = parcel.readString()
        coverAmount = parcel.readString()
        premiumAmount = parcel.readString()
        relationship = parcel.readString()
        benefitCode = parcel.readString()
        benefitCounter = parcel.readString()
        descriptionEnglish = parcel.readString()
        descriptionAfrican = parcel.readString()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(title)
        parcel.writeString(initials)
        parcel.writeString(firstName)
        parcel.writeString(surname)
        parcel.writeString(gender)
        parcel.writeString(dateOfBirth)
        parcel.writeString(coverAmount)
        parcel.writeString(premiumAmount)
        parcel.writeString(relationship)
        parcel.writeString(benefitCode)
        parcel.writeString(benefitCounter)
        parcel.writeString(descriptionEnglish)
        parcel.writeString(descriptionAfrican)
    }

    companion object {
        @JvmField
        val CREATOR = object : Parcelable.Creator<RolePlayers> {
            override fun createFromParcel(parcel: Parcel): RolePlayers {
                return RolePlayers(parcel)
            }

            override fun newArray(size: Int): Array<RolePlayers?> {
                return arrayOfNulls(size)
            }
        }
    }
}
