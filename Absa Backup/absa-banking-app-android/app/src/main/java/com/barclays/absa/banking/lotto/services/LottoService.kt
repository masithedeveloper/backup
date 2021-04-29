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
import com.barclays.absa.banking.framework.AbstractInteractor
import com.barclays.absa.banking.framework.ExtendedResponseListener
import com.barclays.absa.banking.lotto.services.dto.LottoGameRulesResponse

interface LottoService {
    fun checkLottoTermsAcceptance(responseListener: ExtendedResponseListener<TermsAcceptanceResponse>)
    fun checkLottoServiceAvailability(gameType: String, responseListener: ExtendedResponseListener<LottoServiceAvailabilityResponse>)
    fun acceptLottoTermsAndConditions(responseListener: ExtendedResponseListener<TransactionResponse>)
    fun fetchTicketHistory(fromDate: String, toDate: String, responseListener: ExtendedResponseListener<LottoTicketHistoryResponse>)
    fun purchaseLottoTickets(ticketPurchaseData: LottoTicketPurchaseDataModel, responseListener: ExtendedResponseListener<LottoTicketPurchaseResponse>)
    fun fetchLottoGameRules(responseListener: ExtendedResponseListener<LottoGameRulesResponse>)
    fun fetchDrawResults(drawType: String, fromDate: String, toDate: String, responseListener: ExtendedResponseListener<LottoDrawResultsResponse>)
    fun acceptTermsAndPurchaseLottoTickets(ticketPurchaseData: LottoTicketPurchaseDataModel, responseListener: ExtendedResponseListener<LottoAcceptTermsAndPurchaseTicketsResponse>)
}

class LottoInteractor : AbstractInteractor(), LottoService {

    override fun checkLottoTermsAcceptance(responseListener: ExtendedResponseListener<TermsAcceptanceResponse>) {
        submitRequest(LottoCheckTermsAcceptanceRequest(responseListener))
    }

    override fun checkLottoServiceAvailability(gameType: String, responseListener: ExtendedResponseListener<LottoServiceAvailabilityResponse>) {
        submitRequest(LottoCheckAvailabilityRequest(gameType, responseListener))
    }

    override fun acceptLottoTermsAndConditions(responseListener: ExtendedResponseListener<TransactionResponse>) {
        submitRequest(AcceptRejectLottoTermsRequest("Y", responseListener))
    }

    override fun fetchTicketHistory(fromDate: String, toDate: String, responseListener: ExtendedResponseListener<LottoTicketHistoryResponse>) {
        submitRequest(LottoTicketHistoryRequest(fromDate, toDate, responseListener))
    }

    override fun purchaseLottoTickets(ticketPurchaseData: LottoTicketPurchaseDataModel, responseListener: ExtendedResponseListener<LottoTicketPurchaseResponse>) {
        submitRequest(LottoTicketPurchaseRequest(ticketPurchaseData, responseListener))
    }

    override fun fetchLottoGameRules(responseListener: ExtendedResponseListener<LottoGameRulesResponse>) {
        submitRequest(LottoGameRulesRequest(responseListener))
    }

    override fun fetchDrawResults(drawType: String, fromDate: String, toDate: String, responseListener: ExtendedResponseListener<LottoDrawResultsResponse>) {
        submitRequest(LottoDrawResultsRequest(drawType, fromDate, toDate, responseListener))
    }

    override fun acceptTermsAndPurchaseLottoTickets(ticketPurchaseData: LottoTicketPurchaseDataModel, responseListener: ExtendedResponseListener<LottoAcceptTermsAndPurchaseTicketsResponse>) {
        submitRequest(LottoAcceptTermsAndPurchaseTicketsRequest(ticketPurchaseData, responseListener))
    }
}