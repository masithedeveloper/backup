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
package com.barclays.absa.banking.express.cashSend.changeCashSendBeneficiaryDetails

import com.barclays.absa.banking.express.Repository
import com.barclays.absa.banking.express.cashSend.changeCashSendBeneficiaryDetails.dto.ChangeCashSendBeneficiaryDetailsRequest
import com.barclays.absa.banking.express.cashSend.changeCashSendBeneficiaryDetails.dto.ChangeCashSendBeneficiaryDetailsResponse
import za.co.absa.networking.hmac.service.BaseRequest

class ChangeCashSendBeneficiaryDetailsRepository : Repository() {
    private lateinit var changeCashSendBeneficiaryDetailsRequest: ChangeCashSendBeneficiaryDetailsRequest

    override val apiService = createXTMSService()

    override val service = "CashSendBeneficiaryManagementFacade"
    override val operation = "ChangeCashSendBeneficiary"

    override var mockResponseFile = "express/cashSend/change_cash_send_beneficiary.json"

    suspend fun changeCashSendBeneficiaryDetails(changeCashSendBeneficiaryDetailsRequest: ChangeCashSendBeneficiaryDetailsRequest): ChangeCashSendBeneficiaryDetailsResponse? {
        this.changeCashSendBeneficiaryDetailsRequest = changeCashSendBeneficiaryDetailsRequest
        return submitRequest()
    }

    override fun buildRequest(baseRequest: BaseRequest.Builder) = baseRequest
            .addParameter("cifKey", changeCashSendBeneficiaryDetailsRequest.cifKey)
            .addParameter("tieBreaker", changeCashSendBeneficiaryDetailsRequest.tieBreaker)
            .addParameter("instructionType", changeCashSendBeneficiaryDetailsRequest.instructionType.value)
            .addParameter("beneficiaryNumber", changeCashSendBeneficiaryDetailsRequest.beneficiaryNumber.toString())
            .addParameter("beneficiaryStatus", changeCashSendBeneficiaryDetailsRequest.beneficiaryStatus)
            .addParameter("uniqueEFTNumber", changeCashSendBeneficiaryDetailsRequest.uniqueEFTNumber)
            .addParameter("beneficiaryName", changeCashSendBeneficiaryDetailsRequest.beneficiaryName)
            .addParameter("beneficiaryShortName", changeCashSendBeneficiaryDetailsRequest.beneficiaryShortName)
            .addParameter("beneficiarySurname", changeCashSendBeneficiaryDetailsRequest.beneficiarySurname)
            .addParameter("recipientCellNo", changeCashSendBeneficiaryDetailsRequest.recipientCellphoneNumber)
            .addParameter("statementReference", changeCashSendBeneficiaryDetailsRequest.statementReference)
            .build()
}