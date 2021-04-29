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

package com.barclays.absa.banking.fixedDeposit.services.dto

import android.os.Parcelable
import com.barclays.absa.banking.shared.BaseModel
import com.fasterxml.jackson.annotation.JsonProperty
import kotlinx.android.parcel.Parcelize

@Parcelize
class InterestPaymentInstruction(
        @JsonProperty("cifKey")
        var cifKey: String = "",
        @JsonProperty("trgAccount")
        var targetAccount: String = "",
        @JsonProperty("trgAccountType")
        var targetAccountType: String = "",
        @JsonProperty("trgClrCode")
        var targetBranchCode: String = "",
        @JsonProperty("trgInstCode")
        var targetInstitutionCode: String = "",
        @JsonProperty("trgStmtRef")
        var targetAccountReference: String = "",
        @JsonProperty("intExtBenInd")
        var intExtBenInd: String = "",
        @JsonProperty("tieb")
        var tieb: String = "",
        @JsonProperty("freq")
        var freq: String = "",
        @JsonProperty("amount")
        var amount: String = "",
        @JsonProperty("srcAcc")
        var sourceAccount: String = "",
        @JsonProperty("payday")
        var payday: String = "") : BaseModel, Parcelable