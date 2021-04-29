/*
 * Copyright (c) 2019 Absa Bank Limited, All Rights Reserved.
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
package com.barclays.absa.banking.lotto.services

import com.barclays.absa.banking.boundary.shared.dto.SureCheckResponse
import com.barclays.absa.banking.framework.ExtendedRequest
import com.barclays.absa.banking.framework.ExtendedResponseListener
import com.barclays.absa.banking.framework.api.request.params.RequestParams
import com.barclays.absa.banking.shared.BaseModel
import com.fasterxml.jackson.databind.ObjectMapper
import java.math.BigDecimal

class LottoTicketPurchaseRequest(ticketPurchaseData: LottoTicketPurchaseDataModel, responseListener: ExtendedResponseListener<LottoTicketPurchaseResponse>) : ExtendedRequest<LottoTicketPurchaseResponse>(responseListener) {

    init {
        val lottoBoardNumberJson = "{\"lottoBoardList\":" + ObjectMapper().writeValueAsString(ticketPurchaseData.lottoBoardNumbers) + "}"

        params = RequestParams.Builder("OP2130")
                .put("cellPhoneNumber", ticketPurchaseData.cellphoneNumber)
                .put("lottoGameType", ticketPurchaseData.lottoGameType)
                .put("purchaseAmount", ticketPurchaseData.purchaseAmount.toString())
                .put("sourceAccount", ticketPurchaseData.sourceAccount)
                .put("quickPickInd", ticketPurchaseData.quickPickInd.toString())
                .put("playPlus1", ticketPurchaseData.playPlus1.toString())
                .put("playPlus2", ticketPurchaseData.playPlus2.toString())
                .put("numberOfBoards", ticketPurchaseData.numberOfBoards.toString())
                .put("numberOfDraws", ticketPurchaseData.numberOfDraws.toString())
                .put("lottoBoardNumbers", lottoBoardNumberJson)
                .build()

        mockResponseFile = "lotto/op2125_purchase_ticket_response.json"
        printRequest()
    }

    @Suppress("UNCHECKED_CAST")
    override fun getResponseClass() = LottoTicketPurchaseResponse::class.java

    override fun isEncrypted() = true
}

class LottoTicketPurchaseDataModel : BaseModel {
    var cellphoneNumber = ""
    var lottoGameType = ""
    var purchaseAmount = BigDecimal(0)
    var sourceAccount = ""
    var quickPickInd = false
    var playPlus1 = false
    var playPlus2 = false
    var numberOfBoards = 2
    var numberOfDraws = 1
    var lottoBoardNumbers = mutableListOf<LottoBoardNumbers>()
    var date = ""
}

class LottoTicketPurchaseResponse : SureCheckResponse() {
    var lottoBoards = mutableListOf<LottoBoardNumbers>()
    var lottoLimitErrorType = ""
    var lottoGameAvailable = "Y"
}

class LottoBoardNumbers : BaseModel {
    var lottoBoardNumbers = mutableListOf<Int>()
    var powerBall = -1
}