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
package com.barclays.absa.banking.paymentsRewrite

import com.barclays.absa.banking.boundary.model.AccountObject
import com.barclays.absa.banking.boundary.model.Amount
import java.math.BigDecimal

fun Amount.toBigDecimal(): BigDecimal {
    return BigDecimal.valueOf(amountDouble)
}

fun CharSequence.toAmount(currency: String? = null): Amount {
    val currencyToUse = currency ?: "R"
    return Amount(currencyToUse, this.toString().replace(currencyToUse,"", ignoreCase = true))
}

fun AccountObject.hasSufficientFunds(requiredAmount: Amount): Boolean {
    val availableAmount = availableBalance.amountValue
    return availableAmount >= requiredAmount.toBigDecimal()
}