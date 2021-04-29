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
 */

package com.barclays.absa.banking.presentation.homeLoan.services.dto

import android.os.Parcelable
import com.barclays.absa.banking.framework.data.ResponseObject
import com.fasterxml.jackson.annotation.JsonProperty
import kotlinx.android.parcel.Parcelize

@Parcelize
class HomeLoanDetails(@JsonProperty("homeLoanDTO")
                      var homeLoanDTO: HomeLoanDTO = HomeLoanDTO()) : ResponseObject(), Parcelable

@Parcelize
class HomeLoanDTO(@JsonProperty("accountNumber")
                  val accountNumber: String = "",
                  @JsonProperty("accountType")
                  val accountType: String = "",
                  @JsonProperty("description")
                  val description: String = "",
                  @JsonProperty("term")
                  val term: String = "",
                  @JsonProperty("contractDate")
                  val contractDate: String = "",
                  @JsonProperty("remainingTerm")
                  val remainingTerm: String = "",
                  @JsonProperty("arrearAmount")
                  val arrearsAmount: String = "",
                  @JsonProperty("advanceAmount")
                  val advanceAmount: String = "",
                  @JsonProperty("paymentDue")
                  val paymentDue: String = "",
                  @JsonProperty("paymentDate")
                  val paymentDate: String = "",
                  @JsonProperty("interestRate")
                  val interestRate: String = "",
                  @JsonProperty("instalment")
                  val instalment: String = "") : Parcelable