/*
 * Copyright (c) 2020 Absa Bank Limited, All Rights Reserved.
 *
 * This code is confidential to Absa Bank Limited and shall not be disclosed
 * outside the Bank without the prior written permission of the Absa Legal
 *
 * In the event that such disclosure is permitted the code shall not be copied
 * or distributed other than on a need-to-know basis and any recipients may be
 * required to sign a confidentiality undertaking in favor of Absa Bank Limited
 *
 */
package com.barclays.absa.banking.payments.swift.services

import com.barclays.absa.banking.framework.ExtendedResponseListener
import com.barclays.absa.banking.payments.international.services.dto.ValidateForHolidaysAndTimeResponse
import com.barclays.absa.banking.payments.swift.services.request.SwiftBopDataValidationRequestDataModel
import com.barclays.absa.banking.payments.swift.services.request.SwiftBopFieldsRequestDataModel
import com.barclays.absa.banking.payments.swift.services.request.SwiftProcessTransactionRequestDataModel
import com.barclays.absa.banking.payments.swift.services.request.SwiftQuoteRequestDataModel
import com.barclays.absa.banking.payments.swift.services.response.dto.*

interface SwiftService {

    companion object {
        const val OP2137_GET_TRANSACTIONS_LIST = "OP2137"
        const val OP0879_GET_TRANSACTION_HISTORY = "OP0879"
        const val OP0896_GET_LEVEL_1_CATEGORIES = "OP0896"
        const val OP2138_GET_LEVEL_2_CATEGORIES = "OP2138"
        const val OP0888_GET_BOP_FIELDS = "OP0888"
        const val OP0887_BOP_DATA_VALIDATION = "OP0887"
        const val OP2140_GET_QUOTE = "OP2140"
        const val OP2141_PROCESS_TRANSACTION = "OP2141"
    }

    fun requestTransactionsList(responseListener: ExtendedResponseListener<SwiftTransactionsListResponse>, caseId: String = "")

    fun requestTransactionHistory(responseListener: ExtendedResponseListener<SwiftTransactionHistoryResponse>, accessAccountNumber: String)

    fun validateForHolidaysAndTime(validateForHolidaysAndTimeRequestExtendedResponseListener: ExtendedResponseListener<ValidateForHolidaysAndTimeResponse>)

    fun requestLevel1Categories(categoryFlow: String, toAccountNo: String, senderType: String,
                                responseListener: ExtendedResponseListener<SwiftLevelOneCategoriesResponse>)

    fun requestLevel2Categories(categoryFlow: String, toAccountNo: String, senderType: String, categoryDescription: String,
                                responseListener: ExtendedResponseListener<SwiftLevelTwoCategoriesResponse>)

    fun requestBopFields(swiftBopFieldsRequestDataModel: SwiftBopFieldsRequestDataModel, responseListener: ExtendedResponseListener<SwiftBopFieldsResponse>)

    fun requestBopDataValidation(swiftBopDataValidationRequestDataModel: SwiftBopDataValidationRequestDataModel, responseListener: ExtendedResponseListener<SwiftBopDataValidationResponse>)

    fun requestSwiftQuote(swiftQuoteRequestDataModel: SwiftQuoteRequestDataModel, responseListener: ExtendedResponseListener<SwiftQuoteResponse>)

    fun requestProcessTransaction(swiftProcessTransactionRequestDataModel: SwiftProcessTransactionRequestDataModel, responseListener: ExtendedResponseListener<SwiftProcessTransactionResponse>)
}