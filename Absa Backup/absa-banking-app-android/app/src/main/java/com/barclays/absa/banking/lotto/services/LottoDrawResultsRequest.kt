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
import com.barclays.absa.banking.lotto.ui.LottoTicket
import com.barclays.absa.banking.lotto.ui.LottoUtils
import com.barclays.absa.banking.shared.BaseModel
import com.barclays.absa.utils.DateUtils
import java.util.*

class LottoDrawResultsRequest(drawType: String, var fromDate: String, var toDate: String, responseListener: ExtendedResponseListener<LottoDrawResultsResponse>) : ExtendedRequest<LottoDrawResultsResponse>(responseListener) {

    init {
        if (fromDate.isEmpty() && toDate.isEmpty()) {
            toDate = DateUtils.getTodaysDate()
            val currentDateCal = DateUtils.getCalendarObj(toDate)
            toDate = DateUtils.format(currentDateCal, LottoUtils.DATE_NO_FORMAT_PATTERN)
            currentDateCal.add(Calendar.MONTH, -1)
            fromDate = DateUtils.format(currentDateCal.time, LottoUtils.DATE_NO_FORMAT_PATTERN)
        }

        params = RequestParams.Builder("OP2127")
                .put("lottoDrawType", drawType)
                .put("fromDate", fromDate.replace("-", ""))
                .put("toDate", toDate.replace("-", ""))
                .build()
        mockResponseFile = "lotto/op2127_lotto_draw_results.json"
        printRequest()
    }

    @Suppress("UNCHECKED_CAST")
    override fun getResponseClass() = LottoDrawResultsResponse::class.java

    override fun isEncrypted() = true
}

class LottoDrawResultsResponse : TransactionResponse() {
    var lottoDrawResults = mutableListOf<LottoDrawResult>()
}

class LottoDrawResult : BaseModel {
    var purchasedTickets = mutableListOf<LottoTicket>()
    var hasPurchasedTicket: Boolean = false
    var drawDate: String = ""
    var drawType: String = ""
    var drawId: String = ""
    var lottoResultNumbers = mutableListOf<Int>()
    var bonusOrPowerball: String = ""
    var totalPrizePool: String = ""
    var rolloverAmount: String = ""
    var totalSales: String = ""
    var div1Payout: String = ""
    var div2Payout: String = ""
    var div3Payout: String = ""
    var div4Payout: String = ""
    var div5Payout: String = ""
    var div6Payout: String = ""
    var div7Payout: String = ""
    var div8Payout: String = ""
    var div9Payout: String = ""
    var div1NoOfWinners: String = ""
    var div2NoOfWinners: String = ""
    var div3NoOfWinners: String = ""
    var div4NoOfWinners: String = ""
    var div5NoOfWinners: String = ""
    var div6NoOfWinners: String = ""
    var div7NoOfWinners: String = ""
    var div8NoOfWinners: String = ""
    var div9NoOfWinners: String = ""
}