/*
 * Copyright (c) 2019 Absa Bank Limited, All Rights Reserved.
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
package com.barclays.absa.banking.personalLoan.services

import android.os.Parcel
import android.os.Parcelable
import com.barclays.absa.banking.boundary.shared.dto.TransactionResponse
import com.barclays.absa.banking.framework.ExtendedRequest
import com.barclays.absa.banking.framework.ExtendedResponseListener
import com.barclays.absa.banking.framework.KParcelable
import com.barclays.absa.banking.framework.api.request.params.RequestParams
import com.barclays.absa.banking.personalLoan.services.PersonalLoansService.Companion.OP2118_GET_CREDIT_LIMITS_SERVICE

class CreditLimitsRequest<T>(responseListener: ExtendedResponseListener<T>) : ExtendedRequest<T>(responseListener) {

    init {
        params = RequestParams.Builder(OP2118_GET_CREDIT_LIMITS_SERVICE).build()
        mockResponseFile = "personal_loan/op2118_personal_loans_vcl.json"
        printRequest()
    }

    @Suppress("UNCHECKED_CAST")
    override fun getResponseClass() = CreditLimitsResponse::class.java as Class<T>

    override fun isEncrypted() = true

}

class CreditLimitsResponse() : TransactionResponse(), KParcelable {
    var creditLimits = mutableListOf<CreditLimit>()
    var requiresSettlement: Boolean? = null

    constructor(parcel: Parcel) : this() {
        parcel.readList(creditLimits as List<CreditLimit>, CreditLimit::class.java.classLoader)
        requiresSettlement = parcel.readValue(Boolean::class.java.classLoader) as? Boolean
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeList(creditLimits as List<CreditLimit>)
        parcel.writeValue(requiresSettlement)
    }

    companion object CREATOR : Parcelable.Creator<CreditLimitsResponse> {
        override fun createFromParcel(parcel: Parcel): CreditLimitsResponse {
            return CreditLimitsResponse(parcel)
        }

        override fun newArray(size: Int): Array<CreditLimitsResponse?> {
            return arrayOfNulls(size)
        }
    }
}

class CreditLimit() : KParcelable {
    var amount: String = ""
    var period: String = ""

    constructor(parcel: Parcel) : this() {
        parcel.readString()?.let { amount = it }
        parcel.readString()?.let { period = it }
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(amount)
        parcel.writeString(period)
    }

    companion object CREATOR : Parcelable.Creator<CreditLimit> {
        override fun createFromParcel(parcel: Parcel): CreditLimit {
            return CreditLimit(parcel)
        }

        override fun newArray(size: Int): Array<CreditLimit?> {
            return arrayOfNulls(size)
        }
    }
}