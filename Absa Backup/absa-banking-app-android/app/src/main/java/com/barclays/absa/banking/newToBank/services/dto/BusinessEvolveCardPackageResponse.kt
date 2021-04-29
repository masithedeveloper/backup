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

package com.barclays.absa.banking.newToBank.services.dto

import com.barclays.absa.banking.boundary.shared.dto.TransactionResponse
import java.io.Serializable

class BusinessEvolveCardPackageResponse : TransactionResponse() {
    var description = ""
    var minimumIncome = "0.0"
    var packageName = ""
    var products = mutableListOf<Products>()

    data class Products(
            var tabTitle: String = "",
            var identifier: String = "",
            var analyticTag: String = "",
            var bottomButtonTitle: String = "",
            var headerTitle: String = "",
            var headerSubTitle: String = "",
            var monthlyFee: String = "0.0",
            var note: String = "",
            var noteUnderlinedWords: String = "",
            var noteLinks: String = "",
            var features: MutableList<Features> = mutableListOf()
    ): Serializable

    data class Features(
            var featureName: String = "",
            var featurePoints: MutableList<String> = mutableListOf()
    ): Serializable
}