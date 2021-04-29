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
package com.barclays.absa.banking.payments.international.services.dto

import com.barclays.absa.banking.framework.data.ResponseObject
import com.fasterxml.jackson.annotation.JsonProperty

class NatureOfPayment : ResponseObject() {
    @JsonProperty("categoryCode")
    val categoryCode: String? = ""
    @JsonProperty("categoryDescription")
    val categoryDescription: String? = ""
    @JsonProperty("subCategoryCode")
    val subCategoryCode: String? = ""
    @JsonProperty("subCategoryDescription")
    val subCategoryDescription: String? = ""
    @JsonProperty("categoryFlow")
    val categoryFlow: String? = ""
    @JsonProperty("rulingDescription")
    val rulingDescription: String? = ""
    @JsonProperty("rulingCode")
    val rulingCode: String? = ""
    @JsonProperty("transferType")
    val transferType: String? = ""
    @JsonProperty("typeOfClient")
    val typeOfClient: String? = ""
    @JsonProperty("foreignAmount")
    val foreignAmount: String? = ""
    @JsonProperty("localCurrencyCode")
    val localCurrencyCode: String? = ""
    @JsonProperty("localAmount")
    val localAmount: String? = ""
    @JsonProperty("documentsDTO")
    val documents: Array<NatureOfPaymentDocument>? = null

}