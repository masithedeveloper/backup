/*
 * Copyright (c) 2021 Absa Bank Limited, All Rights Reserved.
 *
 * This code is confidential to Absa Bank Limited and shall not be disclosed
 * outside the Bank without the prior written permission of the Absa Legal
 *
 * In the event that such disclosure is permitted the code shall not be copied
 * or distributed other than on a need-to-know basis and any recipients may be
 * required to sign a confidentiality undertaking in favor of Absa Bank
 * Limited
 */

package com.barclays.absa.banking.express.cashSend.cashSendListBeneficiaries

import com.barclays.absa.banking.express.Repository
import com.barclays.absa.banking.express.cashSend.cashSendListBeneficiaries.dto.CashSendBeneficiariesResponse
import com.barclays.absa.banking.express.shared.dto.InstructionType
import za.co.absa.networking.hmac.service.ApiService
import za.co.absa.networking.hmac.service.BaseRequest

class CashSendBeneficiariesRepository : Repository() {
    private var detailsRequired: Boolean = false

    override val apiService: ApiService = createXTMSService()

    override val service: String = "CashSendBeneficiaryManagementFacade"
    override val operation: String = "ListCashSendBeneficiaries"

    override var mockResponseFile: String = "express/cashSend/list_cash_send_beneficiaries.json"

    suspend fun fetchBeneficiaries(detailsRequired: Boolean): CashSendBeneficiariesResponse? {
        this.detailsRequired = detailsRequired
        return submitRequest()
    }

    override fun buildRequest(baseRequest: BaseRequest.Builder) = baseRequest
            .enablePagination()
            .addParameter("instructionType", InstructionType.CASHSEND_BENEFICIARY.value)
            .addParameter("beneficiaryStatusGroup", "ALL")
            .addParameter("detailsRequired", detailsRequired)
            .build()
}