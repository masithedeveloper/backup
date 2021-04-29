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

import com.barclays.absa.banking.boundary.shared.dto.TransactionResponse
import com.barclays.absa.banking.framework.ExtendedRequest
import com.barclays.absa.banking.framework.ExtendedResponseListener
import com.barclays.absa.banking.framework.api.request.params.RequestParams

class LottoAcceptTermsAndPurchaseTicketsRequest(ticketPurchaseData: LottoTicketPurchaseDataModel, responseListener: ExtendedResponseListener<LottoAcceptTermsAndPurchaseTicketsResponse>) : ExtendedRequest<LottoAcceptTermsAndPurchaseTicketsResponse>(responseListener) {

    init {
        params = RequestParams.Builder("OP2130")
                .put("acceptRejectLottoTerms", "Y")
                .put("cellphoneNumber", ticketPurchaseData.cellphoneNumber)
                .put("lottoGameType", ticketPurchaseData.lottoGameType)
                .put("purchaseAmount", ticketPurchaseData.purchaseAmount.toString())
                .put("sourceAccount", ticketPurchaseData.sourceAccount)
                .put("quickPickInd", ticketPurchaseData.quickPickInd.toString())
                .put("playPlus1", ticketPurchaseData.playPlus1.toString())
                .put("playPlus2", ticketPurchaseData.playPlus2.toString())
                .put("numberOfBoards", ticketPurchaseData.numberOfBoards.toString())
                .put("numberOfDraws", ticketPurchaseData.numberOfDraws.toString())
                .put("lottoBoardNumbers", ticketPurchaseData.lottoBoardNumbers.toString())
                .build()
        printRequest()
    }

    @Suppress("UNCHECKED_CAST")
    override fun getResponseClass() = LottoAcceptTermsAndPurchaseTicketsResponse::class.java

    override fun isEncrypted() = true
}

class LottoAcceptTermsAndPurchaseTicketsResponse : TransactionResponse()