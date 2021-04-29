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

import lotto.LottoBoard

class LottoTicketAdapter(lottoBoards: MutableList<LottoBoard>) : LottoNewTicketAdapter(null, lottoBoards) {

    private var filteredDrawResults: MutableList<Int>? = null
    private var isPowerBall = false

    override fun onBindViewHolder(holder: LottoNewTicketViewHolder, position: Int) {
        holder.lottoBoard.isPowerBall = isPowerBall
        filteredDrawResults?.let { holder.lottoBoard.setDrawResultBalls(it) }

        super.onBindViewHolder(holder, position)
        holder.lottoBoard.hideBorder()

        holder.lottoBoard.selectedLottoBalls.forEach { lottoBallView ->
            if (lottoBallView.colorEnabled) {
                lottoBallView.scaleX = 0f
                lottoBallView.scaleY = 0f
                lottoBallView.animate().scaleX(1f).scaleY(1f).setStartDelay(100).setDuration(300).start()
            }
        }
    }

    fun setDrawResults(filteredDrawResults: MutableList<Int>) {
        this.filteredDrawResults = filteredDrawResults
    }

    fun setPowerBall(isPowerBall: Boolean) {
        this.isPowerBall = isPowerBall
    }
}