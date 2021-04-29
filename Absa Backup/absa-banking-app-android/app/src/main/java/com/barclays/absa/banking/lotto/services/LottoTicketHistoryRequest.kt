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
import com.barclays.absa.banking.lotto.ui.LottoUtils.DATE_NO_FORMAT_PATTERN
import com.barclays.absa.banking.shared.BaseModel
import com.barclays.absa.utils.DateUtils
import java.util.*

class LottoTicketHistoryRequest(var fromDate: String, var toDate: String, responseListener: ExtendedResponseListener<LottoTicketHistoryResponse>) : ExtendedRequest<LottoTicketHistoryResponse>(responseListener) {

    init {
        if (fromDate.isEmpty() && toDate.isEmpty()) {
            toDate = DateUtils.getTodaysDate()
            val currentDateCal = DateUtils.getCalendarObj(toDate)
            toDate = DateUtils.format(currentDateCal, DATE_NO_FORMAT_PATTERN)
            currentDateCal.add(Calendar.MONTH, -1)
            fromDate = DateUtils.format(currentDateCal.time, DATE_NO_FORMAT_PATTERN)
        }

        params = RequestParams.Builder("OP2124")
                .put("fromDate", fromDate.replace("-", ""))
                .put("toDate", toDate.replace("-", ""))
                .build()

        mockResponseFile = "lotto/op2124_lotto_ticket_history.json"
        printRequest()
    }

    @Suppress("UNCHECKED_CAST")
    override fun getResponseClass() = LottoTicketHistoryResponse::class.java

    override fun isEncrypted() = true
}

class LottoTicketHistoryResponse : TransactionResponse() {
    var lottoTicketHistory = mutableListOf<LottoTicketHistory>()
}

class LottoTicketHistory : BaseModel {
    val retrievalReferenceNumber = ""
    val purchaseAmount = ""
    val sourceAccount = ""
    val sourceAccountType = ""
    val lottoGameType = ""
    val quickPickInd = true
    val lottoPlus1 = false
    val lottoPlus2 = false
    val numberOfDraws = 1
    val transactionDate = ""
    val firstDrawDate = ""
    val lastDrawDate = ""
    val firstDrawId = ""
    val lastDrawId = ""
    val lottoReferenceNumber = ""
    val applicationId = ""
    val lottoCustomerDetails = LottoCustomerDetails()
    val lottoBoardList = mutableListOf<LottoBoardNumbers>()
}

class LottoCustomerDetails {
    val cifKey = ""
    val firstName = ""
    val surname = ""
    val idNumber = ""
    val cellPhoneNumber = ""
}
