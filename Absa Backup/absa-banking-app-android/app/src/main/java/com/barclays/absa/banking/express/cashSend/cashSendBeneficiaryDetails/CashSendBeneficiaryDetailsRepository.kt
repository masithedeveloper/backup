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

package com.barclays.absa.banking.express.cashSend.cashSendBeneficiaryDetails

import com.barclays.absa.banking.express.Repository
import com.barclays.absa.banking.express.cashSend.cashSendBeneficiaryDetails.dto.CashSendBeneficiaryDetailsResponse
import com.barclays.absa.banking.express.shared.dto.BeneficiaryDetails
import za.co.absa.networking.hmac.service.ApiService
import za.co.absa.networking.hmac.service.BaseRequest

class CashSendBeneficiaryDetailsRepository : Repository() {
    private lateinit var beneficiaryDetailsRequest: BeneficiaryDetails

    override val apiService: ApiService = createXTMSService()

    override val service: String = "CashSendBeneficiaryManagementFacade"
    override val operation: String = "EnquireCashSendBeneficiaryDetails"

    override var mockResponseFile: String = "express/cashSend/cash_send_beneficiary_details.json"

    suspend fun fetchBeneficiaryDetails(beneficiaryDetailsRequest: BeneficiaryDetails): CashSendBeneficiaryDetailsResponse? {
        this.beneficiaryDetailsRequest = beneficiaryDetailsRequest
        return submitRequest()
    }

    override fun buildRequest(baseRequest: BaseRequest.Builder) = baseRequest
            .addObjectParameter("beneficiaryEnquiry", beneficiaryDetailsRequest)
            .build()
}