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
 */

@file:JvmName("StringExtensions")

package styleguide.utils.extensions

import android.annotation.SuppressLint
import za.co.absa.presentation.uilib.BuildConfig
import java.math.BigDecimal
import java.math.RoundingMode
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import java.util.regex.Pattern

/**
 * Converts string to sentence case
 *
 * @return String in sentence case, or empty if null, empty or white spaces
 */
@SuppressLint("DefaultLocale")
fun String?.toSentenceCase(): String {
    if (this.isNullOrBlank()) return ""

    val stringArray = this.trim().toLowerCase(Locale.getDefault()).split(" ")
    val stringBuilder = StringBuilder()
    if (stringArray.isNotEmpty()) {
        stringArray.forEachIndexed { index, s ->
            if (index == 0) {
                stringBuilder.append(stringArray[0].capitalize()).append(" ")
            } else {
                stringBuilder.append(s).append(" ")
            }
        }
    }
    return stringBuilder.toString().trimEnd()
}

/**
 * Converts a string to title case
 *
 * @return The title case converted string, or empty if null, empty or white spaces
 */
@SuppressLint("DefaultLocale")
fun String?.toTitleCase(): String {
    if (this.isNullOrBlank()) return ""

    val stringArray = this.trim().toLowerCase(Locale.getDefault()).split(" ".toRegex())
    val stringBuilder = StringBuilder()
    for (stringElement in stringArray) {
        if (stringElement.trim().isNotEmpty()) {
            stringBuilder.append(stringElement.capitalize()).append(" ")
        }
    }
    return stringBuilder.trimEnd().toString()
}

/**
 * Return string in TitleCase with commas replaced with empty space
 *
 * @return String in TitleCase with comma replaced with empty space
 * or empty if, null, empty, blank spaces, single character or no comma separation
 *
 */
fun String?.toTitleCaseRemovingCommas(): String {
    if (this.isNullOrBlank()) return ""

    val trimmedAddress = this.trim()
    if (trimmedAddress.isEmpty() || trimmedAddress.length == 1) return ""

    val addressSplit = trimmedAddress.split(",", " ")
    if (addressSplit.isNullOrEmpty()) return ""

    return if (addressSplit.size == 1) {
        addressSplit.first().toLowerCase(Locale.getDefault()).toTitleCase()
    } else {
        trimmedAddress.replace(",", " ").toTitleCase()
    }
}

/**
 * This function converts to title case based on delimiter specified characters
 *
 * @return TitleCase converted string based on delimiter specified characters
 * or empty, if null, empty or white space
 */
fun String?.toTitleCaseSplit(): String {
    if (this.isNullOrBlank()) return ""

    val stringBuilder = StringBuilder()
    val delimiter = " '-/"
    var capNextString = true

    for (character in this) {
        val replacementCharacter = if (capNextString) Character.toUpperCase(character) else Character.toLowerCase(character)
        stringBuilder.append(replacementCharacter)
        capNextString = delimiter.indexOf(replacementCharacter) >= 0
    }
    return stringBuilder.toString()
}

/**
 * Converts to formatted card number
 *
 * @return Formatted card number, or empty if null, empty or white spaces
 */
fun String?.toFormattedCardNumber(): String {
    if (this.isNullOrBlank()) return ""

    val cardSplitPositions = intArrayOf(4, 9, 14)
    return this.toSplitString(*cardSplitPositions)
}

/**
 * Convert to formatted cellphone number
 *
 * @return Formatted cellphone number, or empty if null, empty or white spaces
 */
fun String?.toFormattedCellphoneNumber(): String {
    if (this.isNullOrBlank()) return ""
    var formattedCellphoneNumber = this.replace("+27", "")
    formattedCellphoneNumber = formattedCellphoneNumber.removeSpaces()
    if (!formattedCellphoneNumber.startsWith("0")) {
        formattedCellphoneNumber = "0$formattedCellphoneNumber"
    }
    return formattedCellphoneNumber.toSplitString(3, 7)
}

fun String?.toTenDigitPhoneNumber(): String {
    if (this.isNullOrBlank()) return ""
    var formattedCellphoneNumber = this.replace("+27", "")
    formattedCellphoneNumber = formattedCellphoneNumber.removeSpaces()
    if (formattedCellphoneNumber.startsWith("27") && formattedCellphoneNumber.length == 11) {
        formattedCellphoneNumber = formattedCellphoneNumber.replaceFirst("27", "0")
    }
    if (!formattedCellphoneNumber.startsWith("0")) {
        formattedCellphoneNumber = "0$formattedCellphoneNumber"
    }
    return formattedCellphoneNumber
}

/**
 * Format string to id number format
 *
 * @return String in id number format,
 * or unFormatted if string not exactly 13 characters
 * or empty if null, empty or white space
 */
fun String?.toFormattedIdNumber(): String {
    if (this.isNullOrBlank()) return ""
    if (this.length != 13) return this
    return this.toSplitString(6, 11, 14)
}

/**
 * Split the string at the positions specified
 *
 * @return String spaced at positions specified, or empty if null, empty or white spaces
 */
fun String?.toSplitString(vararg positions: Int): String {
    if (this.isNullOrBlank()) return ""

    val stringBuilder = StringBuilder(this)
    // Split at specified positions
    positions.forEach { position ->
        if (position < stringBuilder.length) {
            stringBuilder.insert(position, " ")
        }
    }
    return stringBuilder.toString()
}

/**
 * Removes all spaces from string
 *
 * @return String without spaces, or empty if null, empty or white spaces
 */
fun String?.removeSpaces(): String {
    if (this.isNullOrBlank()) return ""

    return this.replace("\\s".toRegex(), "")
}

/**
 * Removes all the commas from the string
 *
 * @return String without commas, or empty if null, empty or white spaces
 */
fun String?.removeCommas(): String {
    if (this.isNullOrBlank()) return ""

    return this.replace(",", "")
}

/**
 * Removes all commas and dots from the amount String
 *
 * @return Amount without cents and no comma separation
 */
fun String?.removeCommasAndDots(): String {
    if (this.isNullOrBlank()) return ""

    var formattedString = this
    if (formattedString.contains(".")) {
        formattedString = formattedString.substring(0, formattedString.indexOf("."))
    }
    if (formattedString.contains(",")) {
        formattedString = formattedString.replace(",", "")
    }
    return formattedString
}

/**
 * Masks a cellphone number
 *
 * @return The masked cellphone number, or empty if null, empty, white spaces or less than 4
 */
fun String?.toMaskedCellphoneNumber(): String {
    if (this.isNullOrBlank()) return ""
    val formattedCellphoneNumber = this.toFormattedCellphoneNumber()
    if (formattedCellphoneNumber.length < 4) return ""
    return "*** *** ${formattedCellphoneNumber.takeLast(4)}"
}

/**
 * Converts string to masked string with last four characters visible
 *
 * @return Masked String, or empty if null, empty, white spaces or less than 4
 */
fun String?.toMaskedAccountNumber(): String {
    if (this.isNullOrBlank() || this.length < 4) return ""

    return "****" + this.replace(" ", "").takeLast(4)
}

/**
 * Converts string to masked string
 *
 * @return Masked String, or empty if null, empty or white spaces
 */
fun String?.toMaskedString(): String {
    if (this.isNullOrBlank()) return ""

    return String(CharArray(this.length) { '*' })
}

/**
 * Converts to unformatted fax number
 *
 * @return Unformatted fax number, or empty if null, empty or white spaces
 */
fun String?.toUnFormattedFaxNumber(): String {
    if (this.isNullOrBlank()) return ""

    return this.replace("[^0-9]+".toRegex(), "")
}

/**
 * Return string with currency removed
 *
 * @return String without currency, or 0 if null, empty or whitespace
 */
fun String?.removeCurrencyDefaultZero(currency: String = "R"): Double {
    if (this.isNullOrBlank() || this.replace("[\u00A0 ,$currency]".toRegex(), "").isEmpty()) return "0".toDouble()

    return this.replace("[\u00A0 ,$currency]".toRegex(), "").toDouble()
}

/**
 * Return string with currency removed
 *
 * @return String without currency, or 0 if null, empty or whitespace
 */
fun String?.removeStringCurrencyDefaultZero(currency: String = "R"): String {
    if (this.isNullOrBlank() || this.replace(currency, "").replace(" ", "").isEmpty()) return "0"

    return this.replace(currency, "").replace(" ", "")
}

/**
 * Return string with currency removed
 *
 * @return String with currency removed, or empty if null, empty or white spaces
 */
fun String?.removeCurrency(): String {
    if (this.isNullOrBlank()) return ""

    return this.replace("[\u00A0 ,R]".toRegex(), "")
}

/**
 * Converts email address to masked string with '@' and '.' revealed
 *
 * @return Masked String, or empty if null, empty or white spaces
 */
fun String?.toMaskedEmailAddress(): String {
    if (this.isNullOrBlank()) return ""

    val charArray = CharArray(this.length) { '*' }
    val charactersToReveal = 4
    val charsToReveal = listOf('@', '.')

    this.forEachIndexed { index, c ->
        if (this.length >= index && (index < charactersToReveal || charsToReveal.contains(c))) {
            charArray[index] = this[index]
        }
    }
    return String(charArray)
}


// This method is intermediate as Java doesn't recognize default params
// This method can be deleted once no more Java classes are using this method
fun String?.toFormattedAccountNumber(): String = this.toFormattedAccountNumber(5, 0, 4)

fun String?.toSpecialFormattedAccountNumber(): String {
    if (this.isNullOrEmpty()) return ""
    return if (this.isNumbersOnly()) this.toFormattedAccountNumber() else this
}

/**
 * Format to Account Number
 *
 * @return Formatted Account Number, or empty if null, empty, white spaces or less than accountNumberLimit
 */
fun String?.toFormattedAccountNumber(accountNumberLimit: Int = 5, formattingStartPoint: Int = 0, formattingEndPoint: Int = 4): String {

    if (this.isNullOrEmpty()) return ""

    if (this.length <= accountNumberLimit) return this

    val intermediateAccountNumber = StringBuilder(this)
    val formattedAccountNumber = StringBuilder()

    while (intermediateAccountNumber.length > accountNumberLimit) {
        val numberSection = intermediateAccountNumber.take(formattingEndPoint - formattingStartPoint)
        intermediateAccountNumber.delete(formattingStartPoint, formattingEndPoint)
        formattedAccountNumber.append(numberSection).append(" ")
    }
    formattedAccountNumber.append(intermediateAccountNumber)
    return formattedAccountNumber.toString()
}

/**
 * Converts amount to masked string
 *
 * @return Masked String, or empty if null, empty or white spaces
 */
fun String?.toMaskedAmount(): String {
    if (this.isNullOrBlank()) return ""

    val length = this.replace(",", "").split("[.]".toRegex())[0].length
    return String(CharArray(length) { '*' })
}

/**
 * Converts string to abbreviated string
 *
 * @return Abbreviated to 2 letter string unless single word in which case it will be single letter string
 * , or empty if null, empty or white spaces
 */
fun String?.extractTwoLetterAbbreviation(): String {
    if (this.isNullOrBlank()) return ""

    val textToAbbreviate = this.trim()
    return if (textToAbbreviate.contains(" ")) {
        val splitString: MutableList<String> = textToAbbreviate.split(" ") as MutableList<String>
        splitString.removeAll { s: String -> s.isBlank() }

        if (splitString.size == 1) {
            splitString[0].first().toString()
        } else {
            "${splitString[0].first()}${splitString[1].first()}"
        }
    } else {
        textToAbbreviate[0].toString()
    }
}

/**
 * Remove all parentheses from string
 *
 * @return String with parentheses removed, or empty if null, empty or white spaces
 */
fun String?.removeParentheses(): String {
    if (this.isNullOrBlank()) return ""

    return this.replace("[()]".toRegex(), "")
}

/**
 * Remove space after forward slash character
 *
 * @return String with space after forward slash character removed
 * , or empty if null, empty or white spaces
 */
fun String?.removeSpaceAfterForwardSlash(): String {
    if (this.isNullOrBlank()) return ""

    return this.replace("/ ", "/")
}


/**
 * Gets the unFormatted phone number
 *
 * @return The unFormatted phone number, or empty, if null, empty or white spaces
 */
fun String?.getUnFormattedPhoneNumber(): String {
    if (this.isNullOrBlank()) return ""

    var formattedNumber = this.replace("[^0-9]+".toRegex(), "")

    if (formattedNumber.length >= 9) {
        formattedNumber = "0${formattedNumber.takeLast(9)}"
    }
    return formattedNumber
}

/**
 * Get string spaced at the same increments
 *
 * @param numberIncrements Number after which space is required,
 * default = 4
 *
 * @return String spaced at the same increments.
 * Or empty if null, empty or white space
 * Or the unchanged string if the increment number is <= 0
 */
fun String?.insertSpaceAtIncrements(numberIncrements: Int = 4): String {
    if (this.isNullOrBlank()) return ""
    if (numberIncrements <= 0) return this

    val oldString = StringBuilder(this)
    val newString = StringBuilder()

    while (oldString.length > numberIncrements) {
        newString.append(oldString.take(numberIncrements)).append(" ")
        oldString.delete(0, numberIncrements)
    }
    return newString.append(oldString).toString()
}

fun String?.separateThousands(separator: String = " "): String {
    return when {
        this.isNullOrEmpty() -> ""
        this.equals("null", ignoreCase = true) -> ""
        separator.isEmpty() || length <= 3 -> this
        else -> {
            val formatter = "#,##0".createFormatter()
            formatter.format(toDouble())
        }
    }
}

/**
 * The function convert string to #,###.## formatted amount
 *
 * @return String converted to #,###.## formatted amount,
 * or empty, if null, empty or white spaces
 * or unChanged if NumberFormatException
 */
fun String?.toFormattedAmount(): String {
    if (this.isNullOrBlank()) return ""

    return try {
        val amount = this.replace(",", "")
        if (amount.isBlank()) return ""
        val formatter = "#,###.##".createFormatter()
        formatter.format(amount.toDouble())
    } catch (e: NumberFormatException) {
        this
    }
}

fun BigDecimal.toRandAmount(): String = this.toString().toRandAmount()

fun String?.toRandAmount(): String {
    if (this.isNullOrBlank()) return ""

    return "R\u00A0${this.toFormattedAmountZeroDefault()}"
}

fun String?.toDoubleString(): String {
    if (this.isNullOrBlank()) return ""

    val pattern = Pattern.compile("\\d*\\.0")
    val patternTwo = Pattern.compile("\\w*")
    return when {
        pattern.matcher(this).matches() -> this + ("0")
        patternTwo.matcher(this.replace(" ", "")).matches() -> "$this.00"
        else -> this
    }
}

fun String?.toDoubleStringPercentage(): String {
    return when {
        this.isNullOrBlank() -> ""
        this.matches(Regex("\\d*\\.\\d")) -> "${this}0%"
        this.matches(Regex("\\w*")) -> "$this.00%"
        else -> "$this%"
    }
}

/**
 * The function convert string to #,##0.00 formatted amount
 *
 * @return String converted to #,##0.00 formatted amount,
 * or 0.00, if null, empty or white spaces
 * or unChanged if NumberFormatException
 */
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

fun StringBuilder.stripSuffix() {
    if (this.isNotEmpty()) {
        this.removeSuffix("|")
    }
}

fun String.toOneWordString(): String {
    return this.replace(" ", "\u00A0")
}

fun String.addDayOfMonthSuffix(locale: Locale): String {
    val dayOfMonth = this.toIntOrNull() ?: return ""

    return when (locale) {
        Locale.ENGLISH -> dayOfMonth.toString() + dayOfMonth.toPositionSuffixEnglish()
        else -> dayOfMonth.toString() + dayOfMonth.toPositionSuffixAfrikaans()
    }
}

fun Int.toPositionSuffixEnglish(): String {
    if (this == 11 || this == 12 || this == 13) {
        return "th"
    }
    return when (this % 10) {
        1 -> "st"
        2 -> "nd"
        3 -> "rd"
        else -> "th"
    }
}

fun Int.toPositionSuffixAfrikaans(): String {
    return if (this == 1 || this == 8 || this > 19) {
        "ste"
    } else {
        "de"
    }
}

fun String.toIso8601DateFormat(): String {
    val fromDateFormat = SimpleDateFormat("yyyyMMdd", Locale.getDefault())
    val toDateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    try {
        val sourceDate = fromDateFormat.parse(this)
        var formattedDate = ""
        if (sourceDate != null) {
            formattedDate = toDateFormat.format(sourceDate)
        }
        return formattedDate
    } catch (e: ParseException) {
        if (BuildConfig.DEBUG) {
            e.printStackTrace()
        }
        return ""
    }
}

fun String.toTwoDigitDay(): String {
    //this will never be empty, as it should be called when selected from a radio button view or similar
    val formatter = "00".createFormatter()
    return formatter.format(this.toInt())
}

fun String?.formatAmountAsRand(): String = String.format("R\u00A0%s", this.toFormattedAmountZeroDefault())

fun String.toSwiftAmount(): String = if (this.isNotBlank()) {
    DecimalFormat("###,###,###,###,##0.00######", DecimalFormatSymbols().apply {
        groupingSeparator = ' '
        decimalSeparator = '.'
    }).format(this.toDouble())
} else {
    this
}

fun String.isNumbersOnly(): Boolean = this.matches(Regex("^[0-9]+$"))

fun String.toLowerCaseWithLocale() = this.toLowerCase(Locale.getDefault())

fun String.toUpperCaseWithLocale() = this.toUpperCase(Locale.getDefault())

fun String.takeNumbersOrEmpty(): String = replace("[^0-9]".toRegex(), "")

fun String.takeLeadingNumbersOrZero(): String = takeWhile { it.isDigit() }.ifEmpty { "0" }