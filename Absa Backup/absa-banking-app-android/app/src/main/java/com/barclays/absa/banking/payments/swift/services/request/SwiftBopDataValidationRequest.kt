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
import com.barclays.absa.banking.payments.swift.services.response.dto.SwiftBopDataValidationResponse
import com.barclays.absa.banking.shared.BaseModel

class SwiftBopDataValidationRequest<T>(swiftBopDataValidationRequestDataModel: SwiftBopDataValidationRequestDataModel, responseListener: ExtendedResponseListener<T>) : ExtendedRequest<T>(responseListener) {
    init {
        swiftBopDataValidationRequestDataModel.apply {
            params = RequestParams.Builder(SwiftService.OP0887_BOP_DATA_VALIDATION)
                    .put("bopCategory", bopCategory)
                    .put("bopSubCategory", bopSubCategory)
                    .put("rulingCode", rulingCode)
                    .put("foreignCurrencyCode", foreignCurrencyCode)
                    .put("categoryFlow", categoryFlow)
                    .put("foreignCurrencyAmount", foreignCurrencyAmount)
                    .put("typeOfPayment", typeOfPayment)
                    .build()
        }

        mockResponseFile = "swift/op0887_swift_bop_data_validation.json"
        printRequest()
    }

    @Suppress("UNCHECKED_CAST")
    override fun getResponseClass(): Class<T> = SwiftBopDataValidationResponse::class.java as Class<T>
    override fun isEncrypted(): Boolean = true
}

data class SwiftBopDataValidationRequestDataModel(val bopCategory: String, val bopSubCategory: String,
                                                  val rulingCode: String, val foreignCurrencyCode: String,
                                                  val categoryFlow: String, val foreignCurrencyAmount: String,
                                                  val typeOfPayment: String) : BaseModel