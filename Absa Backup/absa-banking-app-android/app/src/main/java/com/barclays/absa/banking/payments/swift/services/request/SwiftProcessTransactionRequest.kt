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
import com.barclays.absa.banking.payments.swift.services.response.dto.SwiftProcessTransactionResponse
import com.barclays.absa.banking.shared.BaseModel

class SwiftProcessTransactionRequest<T>(swiftProcessTransactionRequestDataModel: SwiftProcessTransactionRequestDataModel,
                                        responseListener: ExtendedResponseListener<T>) : ExtendedRequest<T>(responseListener) {
    init {
        swiftProcessTransactionRequestDataModel.apply {
            params = RequestParams.Builder(SwiftService.OP2141_PROCESS_TRANSACTION)
                    .put("toAccount", toAccount)
                    .put("transferType", "SWIFT")
                    .put("expectedPayoutAmount", expectedPayoutAmount)
                    .put("caseIDNumber", caseIDNumber)
                    .put("foreignCurrencyCode", foreignCurrencyCode)
                    .put("categoryFlow", categoryFlow)
                    .put("foreignCurrencyAmount", foreignCurrencyAmount)
                    .put("senderName", senderName)
                    .put("originatingCountry", originatingCountryCode)
                    .put("categoryCode", categoryCode)
                    .put("subCategoryCode", subCategoryCode)
                    .put("foreignCurrency", foreignCurrency)
                    .put("destinationCurrencyRate", destinationCurrencyRate)
                    .put("localCurrency", localCurrency)
                    .put("commissionAmountFee", commissionAmountFee)
                    .put("vatAmount", vatAmount)
                    .put("totalDue", totalDue)
                    .put("beneficiaryName", beneficiaryName)
                    .put("beneficiarySurname", beneficiarySurname)
                    .put("valueDate", valueDate)
                    .put("localAmount", localAmount)
                    .build()
        }

        mockResponseFile = "swift/op2141_swift_process_transaction.json"
        printRequest()
    }

    @Suppress("UNCHECKED_CAST")
    override fun getResponseClass(): Class<T> = SwiftProcessTransactionResponse::class.java as Class<T>
    override fun isEncrypted(): Boolean = true
}

data class SwiftProcessTransactionRequestDataModel(val toAccount: String, val expectedPayoutAmount: String,
                                                   val caseIDNumber: String, val foreignCurrencyCode: String,
                                                   val categoryFlow: String, val foreignCurrencyAmount: String,
                                                   val senderName: String, val originatingCountryCode: String,
                                                   val categoryCode: String, val subCategoryCode: String,
                                                   val foreignCurrency: String, val destinationCurrencyRate: String,
                                                   val localCurrency: String, val commissionAmountFee: String,
                                                   val vatAmount: String, val totalDue: String, val beneficiaryName: String,
                                                   val beneficiarySurname: String, val valueDate: String, val localAmount: String) : BaseModel