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
 *
 */
package com.barclays.absa.banking.express.cashSend.addCashSendBeneficiaryDetails

import com.barclays.absa.banking.express.cashSend.addCashSendBeneficiaryDetails.dto.AddCashSendBeneficiaryDetailsRequest
import com.barclays.absa.banking.express.cashSend.addCashSendBeneficiaryDetails.dto.AddCashSendBeneficiaryDetailsResponse
import com.barclays.absa.banking.express.sureCheck.SureCheckRepository
import za.co.absa.networking.hmac.service.BaseRequest

class AddCashSendBeneficiaryDetailsRepository : SureCheckRepository() {
    private lateinit var addCashSendBeneficiaryDetailsRequest: AddCashSendBeneficiaryDetailsRequest

    override val apiService = createXTMSService()

    override val service = "CashSendBeneficiaryManagementFacade"
    override val operation = "AddCashSendBeneficiary"

    override var mockResponseFile = "express/cashSend/add_cash_send_beneficiary.json"

    override var securityFunctionType: String = "addBeneficiary"

    suspend fun addCashSendBeneficiaryDetails(addCashSendBeneficiaryDetailsRequest: AddCashSendBeneficiaryDetailsRequest): AddCashSendBeneficiaryDetailsResponse? {
        this.addCashSendBeneficiaryDetailsRequest = addCashSendBeneficiaryDetailsRequest
        return submitRequest()
    }

    override fun buildRequest(baseRequest: BaseRequest.Builder): BaseRequest = baseRequest
            .addParameter("instructionType", addCashSendBeneficiaryDetailsRequest.instructionType.value)
            .addParameter("beneficiaryName", addCashSendBeneficiaryDetailsRequest.beneficiaryName)
            .addParameter("beneficiarySurname", addCashSendBeneficiaryDetailsRequest.beneficiarySurname)
            .addParameter("beneficiaryShortName", addCashSendBeneficiaryDetailsRequest.beneficiaryShortName)
            .addParameter("recipientCellNo", addCashSendBeneficiaryDetailsRequest.recipientCellphoneNumber)
            .addParameter("statementReference", addCashSendBeneficiaryDetailsRequest.statementReference)
            .build()
}