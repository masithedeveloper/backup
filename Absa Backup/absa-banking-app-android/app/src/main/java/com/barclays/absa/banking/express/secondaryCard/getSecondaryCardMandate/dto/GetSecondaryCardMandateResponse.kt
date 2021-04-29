/*
 * Copyright (c) 2020 Absa Bank Limited, All Rights Reserved.
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
package com.barclays.absa.banking.express.secondaryCard.getSecondaryCardMandate.dto

import android.os.Parcel
import android.os.Parcelable
import com.barclays.absa.banking.framework.KParcelable
import com.barclays.absa.banking.shared.BaseModel
import za.co.absa.networking.dto.BaseResponse

class GetSecondaryCardMandateResponse() : BaseResponse(), KParcelable {
    var primaryPlastic: String = ""
    var secondaryPlastic: String = ""
    var secondaryEmbossName: String = ""
    var secondaryTenantInd: String = ""
    var secondaryTenantMandate: String = ""
    var secondaryCardsMandatesTermsAndConditions: String = ""
    var secondaryCardMandateDetailsList: ArrayList<SecondaryCards> = arrayListOf()

    constructor(parcel: Parcel) : this() {
        primaryPlastic = parcel.readString() ?: ""
        secondaryPlastic = parcel.readString() ?: ""
        secondaryEmbossName = parcel.readString() ?: ""
        secondaryTenantInd = parcel.readString() ?: ""
        secondaryTenantMandate = parcel.readString() ?: ""
        secondaryCardsMandatesTermsAndConditions = parcel.readString() ?: ""
        secondaryCardMandateDetailsList = parcel.createTypedArrayList(SecondaryCards) ?: arrayListOf()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(primaryPlastic)
        parcel.writeString(secondaryPlastic)
        parcel.writeString(secondaryEmbossName)
        parcel.writeString(secondaryTenantInd)
        parcel.writeString(secondaryTenantMandate)
        parcel.writeString(secondaryCardsMandatesTermsAndConditions)
        parcel.writeTypedList(secondaryCardMandateDetailsList)
    }

    companion object CREATOR : Parcelable.Creator<GetSecondaryCardMandateResponse> {
        override fun createFromParcel(parcel: Parcel): GetSecondaryCardMandateResponse {
            return GetSecondaryCardMandateResponse(parcel)
        }

        override fun newArray(size: Int): Array<GetSecondaryCardMandateResponse?> {
            return arrayOfNulls(size)
        }
    }

    fun addAdditionalSecondaryCardToList() {
        val additionalSecondaryCard = SecondaryCards().apply {
            additionalPlasticNumber = secondaryPlastic
            additionalEmbossName = secondaryEmbossName
            additionalTenantInd = secondaryTenantInd
            additionalTenantMandate = secondaryTenantMandate
        }
        if (additionalSecondaryCard.additionalPlasticNumber.isNotBlank() && additionalSecondaryCard.additionalEmbossName.isNotBlank()) {
            secondaryCardMandateDetailsList.add(0, additionalSecondaryCard)
        }
    }
}

data class SecondaryCards(var additionalPlasticNumber: String = "",
                          var additionalEmbossName: String = "",
                          var additionalTenantInd: String = "",
                          var additionalTenantMandate: String = "") : BaseModel, KParcelable {

    constructor(parcel: Parcel) : this(
            parcel.readString() ?: "",
            parcel.readString() ?: "",
            parcel.readString() ?: "",
            parcel.readString() ?: "")

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(additionalPlasticNumber)
        parcel.writeString(additionalEmbossName)
        parcel.writeString(additionalTenantInd)
        parcel.writeString(additionalTenantMandate)
    }

    companion object CREATOR : Parcelable.Creator<SecondaryCards> {
        override fun createFromParcel(parcel: Parcel): SecondaryCards {
            return SecondaryCards(parcel)
        }

        override fun newArray(size: Int): Array<SecondaryCards?> {
            return arrayOfNulls(size)
        }
    }
}