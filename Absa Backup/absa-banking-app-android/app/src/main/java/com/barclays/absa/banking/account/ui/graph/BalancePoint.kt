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
package com.barclays.absa.banking.account.ui.graph

import com.barclays.absa.utils.TextFormatUtils
import java.util.*

class BalancePoint(val date: Date, val amount: Double) {

    val stringAmount: String = "R ${TextFormatUtils.formatBasicAmount(amount)}"
    val displayDate: String = BalanceGraphWidget.displayDateFormat.format(date)
    val transactionDate: String = BalanceGraphWidget.transactionDateFormat.format(date)
}
