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
package com.barclays.absa.banking.lotto.ui

import com.barclays.absa.banking.shared.BaseModel
import lotto.ticket.Board

class LottoTicket : BaseModel {
    var referenceNumber : String = ""
    var firstDrawNumber: Int = 0
    var boardTitle: String = ""
    var ticketDescription: String = ""
    var firstDrawDate: String = ""
    var lastDrawDate: String = ""
    var dateRange: String = ""
    var isWinningTicket: Boolean = false
    var quickPickInd: Boolean = false
    var lottoPlus1: Boolean = false
    var lottoPlus2: Boolean = false
    var isPowerball: Boolean = false
    var numberOfDraws: Int = 0
    var boardsPlayed: ArrayList<Board> = arrayListOf()
}
