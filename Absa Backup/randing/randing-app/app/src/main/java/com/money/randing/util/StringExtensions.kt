package com.money.randing.util

import java.math.RoundingMode
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols

fun String?.toRandAmount(): String {
    if (this.isNullOrBlank()) return ""

    return "R\u00A0${this.toFormattedAmountZeroDefault()}"
}

fun String?.toFormattedAmountZeroDefault(): String {
    if (this.isNullOrBlank()) return "0.00"

    return try {
        val amount = this.replace(",", "")
        if (amount.isBlank()) return "0.00"
        val formatter = "#,##0.00".createFormatter()
        formatter.format(amount.toBigDecimal())
    } catch (e: NumberFormatException) {
        this
    }
}

private fun String.createFormatter(): DecimalFormat {
    val decimalFormatSymbols = DecimalFormatSymbols()
    decimalFormatSymbols.groupingSeparator = '\u00A0'
    decimalFormatSymbols.decimalSeparator = '.'
    val formatter = DecimalFormat(this, decimalFormatSymbols)
    formatter.roundingMode = RoundingMode.DOWN
    return formatter
}

