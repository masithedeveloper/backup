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
package com.barclays.absa.banking.boundary.model.funeralCover

import android.os.Parcel
import android.os.Parcelable
import com.barclays.absa.banking.boundary.shared.dto.TransactionResponse
import com.barclays.absa.banking.framework.KParcelable
import com.fasterxml.jackson.annotation.JsonProperty

class RolePlayerDetails() : TransactionResponse(), KParcelable {

    @JsonProperty("rolePlayerDetails")
    var listFamilyMemberCoverDetails: List<FamilyMemberCoverDetails>? = emptyList()
    var sumAssured: String? = ""
    var initials: String? = ""
    var surname: String? = ""
    var gender: String? = ""
    var dateOfBirth: String? = ""
    var relationship: String? = ""
    var category: String? = ""
    var premiumAmount: String? = ""
    var coverAmount: String? = ""
    var relationshipCode: String? = ""
    var benefitCode: String? = ""

    constructor(parcel: Parcel) : this() {
        sumAssured = parcel.readString()
        initials = parcel.readString()
        surname = parcel.readString()
        gender = parcel.readString()
        dateOfBirth = parcel.readString()
        relationship = parcel.readString()
        category = parcel.readString()
        premiumAmount = parcel.readString()
        coverAmount = parcel.readString()
        relationshipCode = parcel.readString()
        benefitCode = parcel.readString()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(sumAssured)
        parcel.writeString(initials)
        parcel.writeString(surname)
        parcel.writeString(gender)
        parcel.writeString(dateOfBirth)
        parcel.writeString(relationship)
        parcel.writeString(category)
        parcel.writeString(premiumAmount)
        parcel.writeString(coverAmount)
        parcel.writeString(relationshipCode)
        parcel.writeString(benefitCode)
    }

    companion object CREATOR : Parcelable.Creator<RolePlayerDetails> {
        override fun createFromParcel(parcel: Parcel): RolePlayerDetails {
            return RolePlayerDetails(parcel)
        }

        override fun newArray(size: Int): Array<RolePlayerDetails?> {
            return arrayOfNulls(size)
        }
    }
}
