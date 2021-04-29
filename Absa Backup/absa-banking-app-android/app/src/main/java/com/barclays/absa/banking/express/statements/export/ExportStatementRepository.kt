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
package com.barclays.absa.banking.express.statements.export

import com.barclays.absa.banking.express.Repository
import za.co.absa.networking.hmac.service.BaseRequest

class ExportStatementRepository : Repository() {

    private lateinit var exportStatementRequest: ExportStatementRequest

    override val apiService = createXTMSService()

    override val service = "TransactionHistoryFacade"
    override val operation = "DownloadTransactionHistoryJsonFormat"

    override var mockResponseFile = "express/export_statement.json"

    override fun buildRequest(baseRequest: BaseRequest.Builder): BaseRequest {
        return baseRequest.addParameter("fileType", exportStatementRequest.fileType)
                .addParameter("fromAccountNr", exportStatementRequest.fromAccountNr)
                .addParameter("fromDate", exportStatementRequest.fromDate)
                .addParameter("toDate", exportStatementRequest.toDate)
                .build()
    }

    suspend fun exportStatement(request: ExportStatementRequest): ExportStatementResponse? {
        exportStatementRequest = request
        return submitRequest()
    }
}