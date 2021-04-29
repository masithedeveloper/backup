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

package com.barclays.absa.banking.express.documentRequest.crossborder

import com.barclays.absa.banking.express.Repository
import com.barclays.absa.banking.express.documentRequest.crossborder.dto.CrossborderDetails
import com.barclays.absa.utils.DateUtils
import za.co.absa.networking.dto.BaseResponse
import za.co.absa.networking.hmac.service.BaseRequest

class AvafCrossborderRepository : Repository() {
    private lateinit var details: CrossborderDetails

    override val apiService = createXTMSService()
    override val service = "AVAFDocumentsFacade"
    override val operation = "GetAVAFCrossBorderLetter"
    override var mockResponseFile = "express/avaf/crossborder_request_response_success.json"

    suspend fun submitDocumentRequest(crossborderDetails: CrossborderDetails): BaseResponse? {
        details = crossborderDetails

        return submitRequest()
    }

    override fun buildRequest(baseRequest: BaseRequest.Builder): BaseRequest {
        return baseRequest
                .addParameter("userEmail", details.email)
                .addParameter("accountNumber", details.accountNumber)
                .addParameter("driverName", details.name)
                .addParameter("driverIdNumber", details.identification)
                .addParameter("periodStart", DateUtils.format(details.dateFrom, DateUtils.DASHED_DATE_PATTERN))
                .addParameter("periodEnd", DateUtils.format(details.dateTo, DateUtils.DASHED_DATE_PATTERN))
                .build()
    }
}