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

import com.barclays.absa.banking.framework.AbstractInteractor
import com.barclays.absa.banking.framework.ExtendedResponseListener
import com.barclays.absa.banking.payments.international.services.dto.ValidateForHolidaysAndTimeRequest
import com.barclays.absa.banking.payments.international.services.dto.ValidateForHolidaysAndTimeResponse
import com.barclays.absa.banking.payments.swift.services.request.*
import com.barclays.absa.banking.payments.swift.services.response.dto.*

class SwiftInteractor : AbstractInteractor(), SwiftService {

    override fun requestTransactionsList(responseListener: ExtendedResponseListener<SwiftTransactionsListResponse>, caseId: String) =
            submitRequest(SwiftTransactionsListRequest(caseId, responseListener))

    override fun requestTransactionHistory(responseListener: ExtendedResponseListener<SwiftTransactionHistoryResponse>, accessAccountNumber: String) =
            submitRequest(SwiftTransactionHistoryRequest(accessAccountNumber, responseListener))

    override fun validateForHolidaysAndTime(validateForHolidaysAndTimeRequestExtendedResponseListener: ExtendedResponseListener<ValidateForHolidaysAndTimeResponse>) =
            submitRequest(ValidateForHolidaysAndTimeRequest(validateForHolidaysAndTimeRequestExtendedResponseListener))

    override fun requestLevel1Categories(categoryFlow: String, toAccountNo: String, senderType: String,
                                         responseListener: ExtendedResponseListener<SwiftLevelOneCategoriesResponse>) =
            submitRequest(SwiftLevelOneCategoriesRequest(categoryFlow, toAccountNo, senderType, responseListener))

    override fun requestLevel2Categories(categoryFlow: String, toAccountNo: String, senderType: String, categoryDescription: String,
                                         responseListener: ExtendedResponseListener<SwiftLevelTwoCategoriesResponse>) =
            submitRequest(SwiftLevelTwoCategoriesRequest(categoryFlow, toAccountNo, senderType, categoryDescription, responseListener))

    override fun requestBopDataValidation(swiftBopDataValidationRequestDataModel: SwiftBopDataValidationRequestDataModel, responseListener: ExtendedResponseListener<SwiftBopDataValidationResponse>) =
            submitRequest(SwiftBopDataValidationRequest(swiftBopDataValidationRequestDataModel, responseListener))

    override fun requestBopFields(swiftBopFieldsRequestDataModel: SwiftBopFieldsRequestDataModel, responseListener: ExtendedResponseListener<SwiftBopFieldsResponse>) =
            submitRequest(SwiftBopFieldsRequest(swiftBopFieldsRequestDataModel, responseListener))

    override fun requestSwiftQuote(swiftQuoteRequestDataModel: SwiftQuoteRequestDataModel,
                                   responseListener: ExtendedResponseListener<SwiftQuoteResponse>) =
            submitRequest(SwiftQuoteRequest(swiftQuoteRequestDataModel, responseListener))

    override fun requestProcessTransaction(swiftProcessTransactionRequestDataModel: SwiftProcessTransactionRequestDataModel,
                                           responseListener: ExtendedResponseListener<SwiftProcessTransactionResponse>) =
            submitRequest(SwiftProcessTransactionRequest(swiftProcessTransactionRequestDataModel, responseListener))
}