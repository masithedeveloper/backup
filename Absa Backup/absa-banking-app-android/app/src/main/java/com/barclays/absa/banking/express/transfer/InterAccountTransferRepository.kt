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

package com.barclays.absa.banking.express.transfer

import com.barclays.absa.banking.express.Repository
import com.barclays.absa.banking.transfer.TransferConfirmationData
import com.barclays.absa.utils.DateUtils
import styleguide.utils.extensions.removeStringCurrencyDefaultZero
import za.co.absa.networking.dto.BaseResponse
import za.co.absa.networking.hmac.service.ApiService
import za.co.absa.networking.hmac.service.BaseRequest

class InterAccountTransferRepository : Repository() {
    private lateinit var details: TransferConfirmationData

    override var mockResponseFile: String = "avaf/avaf_interaccount_transfer_success_response.json"
    override val service: String = "InterAccountTransferFacade"
    override val operation: String = "ValidateAndPayAnInterAccountTransfer"
    override val apiService: ApiService = createXTMSService()

    override fun buildRequest(baseRequest: BaseRequest.Builder): BaseRequest {
        return baseRequest
                .addParameter("sourceAccount", details.fromAccountNumber)
                .addParameter("sourceAccountStatementDescription", details.fromAccountReference)
                .addParameter("targetAccount", details.toAccountNumber)
                .addParameter("targetAccountStatementDescription", details.toAccountReference)
                .addParameter("amount", details.amountToTransfer.removeStringCurrencyDefaultZero())
                .addParameter("useTime", details.useTime.toString())
                .addParameter("transactionDate", DateUtils.format(details.transactionDate, DateUtils.DASHED_DATETIME_PATTERN))
                .build()
    }

    suspend fun validateAndPay(transferDetails: TransferConfirmationData): BaseResponse? {
        details = transferDetails
        return submitRequest()
    }
}