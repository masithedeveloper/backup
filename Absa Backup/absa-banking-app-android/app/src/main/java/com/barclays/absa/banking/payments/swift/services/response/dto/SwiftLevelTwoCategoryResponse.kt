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
package com.barclays.absa.banking.payments.swift.services.response.dto

import com.barclays.absa.banking.shared.BaseModel
import com.fasterxml.jackson.annotation.JsonIgnore

class SwiftLevelTwoCategoryResponse : BaseModel {
    val categoryCode: String = ""
    val categoryDescription: String = ""
    val subCategoryCode: String = ""
    val subCategoryDescription: String = ""
    val categoryFlow: String = ""
    val rulingDescription: String = ""
    val rulingCode: String = ""
    val transferType: String = ""
    val typeOfClient: String = ""
    val foreignAmount: String = ""
    val localCurrencyCode: String = ""
    val localAmount: String = ""
    @JsonIgnore
    val documentsDTO: Any? = null
}