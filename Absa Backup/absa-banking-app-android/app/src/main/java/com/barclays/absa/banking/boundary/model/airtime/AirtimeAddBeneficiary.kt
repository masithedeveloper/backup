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
package com.barclays.absa.banking.boundary.model.airtime

import com.barclays.absa.banking.boundary.model.AddBeneficiaryObject
import com.barclays.absa.banking.boundary.model.NetworkProvider
import com.fasterxml.jackson.annotation.JsonProperty

open class AirtimeAddBeneficiary : AddBeneficiaryObject() {

    @JsonProperty("networkProviderJsonBean")
    var networkProvider: List<NetworkProvider>? = null

    @JsonProperty("benNam")
    open var beneficiaryName: String? = null

    @JsonProperty("cellNo")
    open var cellNumber: String? = null

    open var networkProviderName: String? = null

    var institutionCode: String? = null
    open var addFavourites: String? = null
    var image: String? = null

    @JsonProperty("txnRefNo")
    var transactionReferenceNumber: String? = null
}
