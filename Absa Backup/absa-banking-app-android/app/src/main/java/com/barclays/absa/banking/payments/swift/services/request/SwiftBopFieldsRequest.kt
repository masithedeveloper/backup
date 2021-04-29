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
package com.barclays.absa.banking.payments.swift.services.request

import com.barclays.absa.banking.framework.ExtendedRequest
import com.barclays.absa.banking.framework.ExtendedResponseListener
import com.barclays.absa.banking.framework.api.request.params.RequestParams
import com.barclays.absa.banking.payments.swift.services.SwiftService
import com.barclays.absa.banking.payments.swift.services.response.dto.SwiftBopFieldsResponse
import com.barclays.absa.banking.shared.BaseModel

class SwiftBopFieldsRequest<T>(swiftBopFieldsRequestDataModel: SwiftBopFieldsRequestDataModel, responseListener: ExtendedResponseListener<T>) : ExtendedRequest<T>(responseListener) {
    init {
        swiftBopFieldsRequestDataModel.apply {
            params = RequestParams.Builder(SwiftService.OP0888_GET_BOP_FIELDS)
                    .put("bopCategory", bopCategory)
                    .put("bopSubCategory", bopSubCategory)
                    .put("flow", flow)
                    .put("typeOfPayment", typeOfPayment)
                    .put("destinationCountry", destinationCountry)
                    .put("transactionType", "SWIFT")
                    .put("originatingCountryCode", originatingCountryCode)
                    .put("foreignCurrencyCode", foreignCurrencyCode)
                    .put("valueDate", valueDate)
                    .put("beneficiaryName", beneficiaryName)

                    // This is a ugly hack as requested by AOL as they are not passing the beneficiary surname in
                    // the case of a company for sender selection, but still requires a value for beneficiary surname.
                    .put("beneficiarySurName", if (beneficiarySurName.isEmpty())
                        beneficiaryName else beneficiarySurName)

                    .put("fromAccountNumber", fromAccountNumber)
                    .put("caseIDNumber", caseIDNumber)
                    .build()
        }

        mockResponseFile = "swift/op0888_swift_get_bop_fields.json"
        printRequest()
    }

    @Suppress("UNCHECKED_CAST")
    override fun getResponseClass(): Class<T> = SwiftBopFieldsResponse::class.java as Class<T>
    override fun isEncrypted(): Boolean = true
}

data class SwiftBopFieldsRequestDataModel(
        val bopCategory: String, val bopSubCategory: String, val flow: String, val typeOfPayment: String,
        val destinationCountry: String, val originatingCountryCode: String, val foreignCurrencyCode: String,
        val valueDate: String, val beneficiaryName: String, val beneficiarySurName: String, val fromAccountNumber: String,
        val caseIDNumber: String) : BaseModel