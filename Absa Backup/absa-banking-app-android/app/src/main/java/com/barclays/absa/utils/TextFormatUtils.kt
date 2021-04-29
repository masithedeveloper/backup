/*
 * Copyright (c) 2020 Absa Bank Limited, All Rights Reserved.
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
package com.barclays.absa.utils

import com.barclays.absa.banking.boundary.model.Amount
import com.barclays.absa.banking.framework.app.BMBApplication
import styleguide.utils.extensions.toFormattedAccountNumber
import styleguide.utils.extensions.toFormattedAmount
import styleguide.utils.extensions.toFormattedAmountZeroDefault
import java.math.BigDecimal

object TextFormatUtils {

    @JvmStatic
    fun formatAccountNumberAndDescription(accountDescription: String?, accountNumber: String?) = "$accountDescription (${accountNumber?.toFormattedAccountNumber()})"

    @JvmStatic
    fun formatCashSendAmount(amount: Amount) = "${amount.currency} ${amount.getAmount().toFormattedAmountZeroDefault()}"

    @JvmStatic
    fun formatBasicAmountAsRand(amount: String?) = String.format(BMBApplication.getApplicationLocale(), "R\u00A0%s", amount.toFormattedAmountZeroDefault())

    @JvmStatic
    fun formatBasicAmountAsRand(amount: Int) = String.format(BMBApplication.getApplicationLocale(), "R\u00A0%s", amount.toString().toFormattedAmountZeroDefault())

    @JvmStatic
    fun formatBasicAmountAsRandNoDecimal(amount: Int) = String.format(BMBApplication.getApplicationLocale(), "R\u00A0%s", amount.toString().toFormattedAmount())

    @JvmStatic
    fun formatBasicAmountAsRandNoDecimal(amount: String?) = String.format(BMBApplication.getApplicationLocale(), "R\u00A0%s", amount.toFormattedAmount())

    @JvmStatic
    fun spaceFormattedAmount(currency: String, amount: String) = "$currency ${amount.toFormattedAmount()}"

    @JvmStatic
    fun formatBasicAmount(amount: String?) = amount.toFormattedAmountZeroDefault()

    @JvmStatic
    fun formatBasicAmount(amount: Double) = amount.toString().toFormattedAmountZeroDefault()

    @JvmStatic
    fun formatBasicAmount(amount: BigDecimal) = amount.toString().toFormattedAmountZeroDefault()

    @JvmStatic
    fun formatBasicAmount(amount: Amount?) = if (amount == null) "R 0.00" else "${amount.currency}\u00A0${amount.getAmount().toFormattedAmountZeroDefault()}"
}