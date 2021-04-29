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

package com.barclays.absa.utils.extensions

import junit.framework.Assert.assertEquals
import org.junit.jupiter.api.Test
import styleguide.utils.extensions.*

internal class StringExtensionsKtTest {

    companion object {
        private const val DEFAULT_MESSAGE = "Not expected"
    }

    @Test
    fun toTitleCase() {
        val testCaseOne = "this is the lower case string".toTitleCase()
        assertEquals(DEFAULT_MESSAGE, "This Is The Lower Case String", testCaseOne)

        val testCaseTwo = "this is the lower case      string".toTitleCase()
        assertEquals(DEFAULT_MESSAGE, "This Is The Lower Case String", testCaseTwo)

        val testCaseThree = "   this is the lower case string   ".toTitleCase()
        assertEquals(DEFAULT_MESSAGE, "This Is The Lower Case String", testCaseThree)

        val testCaseFour = "  this  Is  tHe loWEr  casE  StriNG  ".toTitleCase()
        assertEquals(DEFAULT_MESSAGE, "This Is The Lower Case String", testCaseFour)

        val testCaseFive = "".toTitleCase()
        assertEquals(DEFAULT_MESSAGE, "", testCaseFive)

        val testCaseSix = null.toTitleCase()
        assertEquals(DEFAULT_MESSAGE, "", testCaseSix)

        val testCaseSeven = "  ".toTitleCase()
        assertEquals(DEFAULT_MESSAGE, "", testCaseSeven)
    }

    @Test
    fun toSplitString() {
        val testCaseOne = "0784820256".toSplitString(3, 7)
        assertEquals(DEFAULT_MESSAGE, "078 482 0256", testCaseOne)

        val testCaseTwo = "4483850000053625".toSplitString(4, 9, 14)
        assertEquals(DEFAULT_MESSAGE, "4483 8500 0005 3625", testCaseTwo)

        val testCaseThree = "123".toSplitString(1, 5)
        assertEquals(DEFAULT_MESSAGE, "1 23", testCaseThree)

        val testCaseFour = "".toSplitString(3, 7)
        assertEquals(DEFAULT_MESSAGE, "", testCaseFour)

        val testCaseFive = null.toSplitString(3, 7)
        assertEquals(DEFAULT_MESSAGE, "", testCaseFive)

        val testCaseSix = "1".toSplitString(1)
        assertEquals(DEFAULT_MESSAGE, "1", testCaseSix)

        val testCaseSeven = "  ".toSplitString()
        assertEquals(DEFAULT_MESSAGE, "", testCaseSeven)
    }

    @Test
    fun toFormattedCardNumber() {
        val testCaseOne = "44838".toFormattedCardNumber()
        assertEquals(DEFAULT_MESSAGE, "4483 8", testCaseOne)

        val testCaseTwo = "448385000".toFormattedCardNumber()
        assertEquals(DEFAULT_MESSAGE, "4483 8500 0", testCaseTwo)

        val testCaseThree = "4483850000053".toFormattedCardNumber()
        assertEquals(DEFAULT_MESSAGE, "4483 8500 0005 3", testCaseThree)

        val testCaseFour = "4483850000053625".toFormattedCardNumber()
        assertEquals(DEFAULT_MESSAGE, "4483 8500 0005 3625", testCaseFour)

        val testCaseFive = "".toFormattedCardNumber()
        assertEquals(DEFAULT_MESSAGE, "", testCaseFive)

        val testCaseSix = null.toFormattedCardNumber()
        assertEquals(DEFAULT_MESSAGE, "", testCaseSix)

        val testCaseSeven = "  ".toFormattedCardNumber()
        assertEquals(DEFAULT_MESSAGE, "", testCaseSeven)
    }

    @Test
    fun toFormattedCellphoneNumber() {
        val testCaseOne = "0784 ".toFormattedCellphoneNumber()
        assertEquals(DEFAULT_MESSAGE, "078 4", testCaseOne)

        val testCaseTwo = "0784820 ".toFormattedCellphoneNumber()
        assertEquals(DEFAULT_MESSAGE, "078 482 0", testCaseTwo)

        val testCaseThree = " 0784820256 ".toFormattedCellphoneNumber()
        assertEquals(DEFAULT_MESSAGE, "078 482 0256", testCaseThree)

        val testCaseFour = "+27 784820256".toFormattedCellphoneNumber()
        assertEquals(DEFAULT_MESSAGE, "078 482 0256", testCaseFour)

        val testCaseFive = "+27 78 482 0256".toFormattedCellphoneNumber()
        assertEquals(DEFAULT_MESSAGE, "078 482 0256", testCaseFive)

        val testCaseSix = "+27 078 482 0256".toFormattedCellphoneNumber()
        assertEquals(DEFAULT_MESSAGE, "078 482 0256", testCaseSix)

        val testCaseSeven = "".toFormattedCellphoneNumber()
        assertEquals(DEFAULT_MESSAGE, "", testCaseSeven)

        val testCaseEight = null.toFormattedCellphoneNumber()
        assertEquals(DEFAULT_MESSAGE, "", testCaseEight)
    }


    @Test
    fun removeSpaces() {
        val testCaseOne = " N o S p a c e s ".removeSpaces()
        assertEquals(DEFAULT_MESSAGE, "NoSpaces", testCaseOne)

        val testCaseTwo = "".removeSpaces()
        assertEquals(DEFAULT_MESSAGE, "", testCaseTwo)

        val testCaseThree = null.removeSpaces()
        assertEquals(DEFAULT_MESSAGE, "", testCaseThree)
    }

    @Test
    fun removeCommas() {
        val testCaseOne = "1,000.00".removeCommas()
        assertEquals(DEFAULT_MESSAGE, "1000.00", testCaseOne)

        val testCaseTwo = "1,000,000.00".removeCommas()
        assertEquals(DEFAULT_MESSAGE, "1000000.00", testCaseTwo)

        val testCaseThree = "".removeCommas()
        assertEquals(DEFAULT_MESSAGE, "", testCaseThree)

        val testCaseFour = null.removeCommas()
        assertEquals(DEFAULT_MESSAGE, "", testCaseFour)

        val testCaseFive = "  ".removeCommas()
        assertEquals(DEFAULT_MESSAGE, "", testCaseFive)
    }

    @Test
    fun toSentenceCase() {
        val testCaseOne = "a test sentence".toSentenceCase()
        assertEquals(DEFAULT_MESSAGE, "A test sentence", testCaseOne)

        val testCaseTwo = "a Test Sentence".toSentenceCase()
        assertEquals(DEFAULT_MESSAGE, "A test sentence", testCaseTwo)

        val testCaseThree = "A TEST SENTENCE".toSentenceCase()
        assertEquals(DEFAULT_MESSAGE, "A test sentence", testCaseThree)

        val testCaseFour = "A tEsT SentEncE".toSentenceCase()
        assertEquals(DEFAULT_MESSAGE, "A test sentence", testCaseFour)

        val testCaseFive = "A".toSentenceCase()
        assertEquals(DEFAULT_MESSAGE, "A", testCaseFive)

        val testCaseSix = "a".toSentenceCase()
        assertEquals(DEFAULT_MESSAGE, "A", testCaseSix)

        val testCaseSeven = "".toSentenceCase()
        assertEquals(DEFAULT_MESSAGE, "", testCaseSeven)

        val testCaseEight = " ".toSentenceCase()
        assertEquals(DEFAULT_MESSAGE, "", testCaseEight)

        val testCaseNine = null.toSentenceCase()
        assertEquals(DEFAULT_MESSAGE, "", testCaseNine)
    }

    @Test
    fun toMaskedCellphoneNumber() {
        val testCaseOne = "0784820256".toMaskedCellphoneNumber()
        assertEquals(DEFAULT_MESSAGE, "*** *** 0256", testCaseOne)

        val testCaseTwo = "+27784820256".toMaskedCellphoneNumber()
        assertEquals(DEFAULT_MESSAGE, "*** *** 0256", testCaseTwo)

        val testCaseThree = "+27 0784820256".toMaskedCellphoneNumber()
        assertEquals(DEFAULT_MESSAGE, "*** *** 0256", testCaseThree)

        val testCaseFour = "+27 078".toMaskedCellphoneNumber()
        assertEquals(DEFAULT_MESSAGE, "", testCaseFour)

        val testCaseFive = "".toMaskedCellphoneNumber()
        assertEquals(DEFAULT_MESSAGE, "", testCaseFive)

        val testCaseSix = " ".toMaskedCellphoneNumber()
        assertEquals(DEFAULT_MESSAGE, "", testCaseSix)

        val testCaseSeven = null.toMaskedCellphoneNumber()
        assertEquals(DEFAULT_MESSAGE, "", testCaseSeven)
    }

    @Test
    fun toMaskedAccountNumber() {
        val testCaseOne = "1234567890".toMaskedAccountNumber()
        assertEquals(DEFAULT_MESSAGE, "****7890", testCaseOne)

        val testCaseTwo = "123 4567 890".toMaskedAccountNumber()
        assertEquals(DEFAULT_MESSAGE, "****7890", testCaseTwo)

        val testCaseSeven = "".toMaskedAccountNumber()
        assertEquals(DEFAULT_MESSAGE, "", testCaseSeven)

        val testCaseEight = " ".toMaskedAccountNumber()
        assertEquals(DEFAULT_MESSAGE, "", testCaseEight)

        val testCaseNine = null.toMaskedAccountNumber()
        assertEquals(DEFAULT_MESSAGE, "", testCaseNine)
    }

    @Test
    fun toMaskedString() {
        val testCaseOne = "Test string".toMaskedString()
        assertEquals(DEFAULT_MESSAGE, "***********", testCaseOne)

        val testCaseTwo = "test".toMaskedString()
        assertEquals(DEFAULT_MESSAGE, "****", testCaseTwo)

        val testCaseThree = "".toMaskedString()
        assertEquals(DEFAULT_MESSAGE, "", testCaseThree)

        val testCaseFour = " ".toMaskedString()
        assertEquals(DEFAULT_MESSAGE, "", testCaseFour)

        val testCaseFive = null.toMaskedString()
        assertEquals(DEFAULT_MESSAGE, "", testCaseFive)
    }

    @Test
    fun toUnFormattedFaxNumber() {
        val testCaseOne = "+27-784820256".toUnFormattedFaxNumber()
        assertEquals(DEFAULT_MESSAGE, "27784820256", testCaseOne)

        val testCaseTwo = "".toUnFormattedFaxNumber()
        assertEquals(DEFAULT_MESSAGE, "", testCaseTwo)

        val testCaseThree = null.toUnFormattedFaxNumber()
        assertEquals(DEFAULT_MESSAGE, "", testCaseThree)
    }

    @Test
    fun removeCommasAndDots() {
        val testCaseOne = "100,123.45".removeCommasAndDots()
        assertEquals(DEFAULT_MESSAGE, "100123", testCaseOne)

        val testCaseTwo = "".removeCommasAndDots()
        assertEquals(DEFAULT_MESSAGE, "", testCaseTwo)

        val testCaseThree = null.removeCommasAndDots()
        assertEquals(DEFAULT_MESSAGE, "", testCaseThree)
    }

    @Test
    fun removeCurrency() {
        val testCaseOne = " R100,123.45".removeCurrency()
        assertEquals(DEFAULT_MESSAGE, "100123.45", testCaseOne)

        val testCaseTwo = "".removeCurrency()
        assertEquals(DEFAULT_MESSAGE, "", testCaseTwo)

        val testCaseThree = " R 100,123.45 ".removeCurrency()
        assertEquals(DEFAULT_MESSAGE, "100123.45", testCaseThree)

        val testCaseFour = null.removeCurrency()
        assertEquals(DEFAULT_MESSAGE, "", testCaseFour)

        val testCaseFive = "  ".removeCurrency()
        assertEquals(DEFAULT_MESSAGE, "", testCaseFive)
    }

    @Test
    fun toMaskedEmailAddress() {
        val testCaseOne = "sam.smith@domain.com".toMaskedEmailAddress()
        assertEquals(DEFAULT_MESSAGE, "sam.*****@******.***", testCaseOne)

        val testCaseTwo = "sam@domain.com".toMaskedEmailAddress()
        assertEquals(DEFAULT_MESSAGE, "sam@******.***", testCaseTwo)

        val testCaseThree = "".toMaskedEmailAddress()
        assertEquals(DEFAULT_MESSAGE, "", testCaseThree)

        val testCaseFour = " ".toMaskedEmailAddress()
        assertEquals(DEFAULT_MESSAGE, "", testCaseFour)

        val testCaseFive = null.toMaskedEmailAddress()
        assertEquals(DEFAULT_MESSAGE, "", testCaseFive)
    }

    @Test
    fun toFormattedAccountNumber() {
        val testCaseOne = "4483850000053625".toFormattedAccountNumber()
        assertEquals(DEFAULT_MESSAGE, "4483 8500 0005 3625", testCaseOne)

        val testCaseTwo = "4483".toFormattedAccountNumber()
        assertEquals(DEFAULT_MESSAGE, "4483", testCaseTwo)

        val testCaseThree = "4483850".toFormattedAccountNumber()
        assertEquals(DEFAULT_MESSAGE, "4483 850", testCaseThree)

        val testCaseFour = "".toFormattedAccountNumber()
        assertEquals(DEFAULT_MESSAGE, "", testCaseFour)

        val testCaseFive = " ".toFormattedAccountNumber()
        assertEquals(DEFAULT_MESSAGE, " ", testCaseFive)

        val testCaseSix = null.toFormattedAccountNumber()
        assertEquals(DEFAULT_MESSAGE, "", testCaseSix)
    }

    @Test
    fun toMaskedAmount() {
        val testCaseOne = "1,000".toMaskedAmount()
        assertEquals(DEFAULT_MESSAGE, "****", testCaseOne)

        val testCaseTwo = "1000.00".toMaskedAmount()
        assertEquals(DEFAULT_MESSAGE, "****", testCaseTwo)

        val testCaseThree = "1,000.00".toMaskedAmount()
        assertEquals(DEFAULT_MESSAGE, "****", testCaseThree)

        val testCaseFour = "".toMaskedAmount()
        assertEquals(DEFAULT_MESSAGE, "", testCaseFour)

        val testCaseFive = " ".toMaskedAmount()
        assertEquals(DEFAULT_MESSAGE, "", testCaseFive)

        val testCaseSix = null.toMaskedAmount()
        assertEquals(DEFAULT_MESSAGE, "", testCaseSix)
    }

    @Test
    fun extractTwoLetterAbbreviation() {
        val testCaseOne = " Nikhar Roopan ".extractTwoLetterAbbreviation()
        assertEquals(DEFAULT_MESSAGE, "NR", testCaseOne)

        val testCaseTwo = " Nikhar     Niresh   Roopan ".extractTwoLetterAbbreviation()
        assertEquals(DEFAULT_MESSAGE, "NN", testCaseTwo)

        val testCaseThree = " Nikhar ".extractTwoLetterAbbreviation()
        assertEquals(DEFAULT_MESSAGE, "N", testCaseThree)

        val testCaseFour = "".extractTwoLetterAbbreviation()
        assertEquals(DEFAULT_MESSAGE, "", testCaseFour)

        val testCaseFive = " ".extractTwoLetterAbbreviation()
        assertEquals(DEFAULT_MESSAGE, "", testCaseFive)

        val testCaseSix = null.extractTwoLetterAbbreviation()
        assertEquals(DEFAULT_MESSAGE, "", testCaseSix)
    }

    @Test
    fun removeParentheses() {
        val testCaseOne = "R 100 (pm)".removeParentheses()
        assertEquals(DEFAULT_MESSAGE, "R 100 pm", testCaseOne)

        val testCaseTwo = "(Parentheses at front".removeParentheses()
        assertEquals(DEFAULT_MESSAGE, "Parentheses at front", testCaseTwo)

        val testCaseThree = "Parentheses at back)".removeParentheses()
        assertEquals(DEFAULT_MESSAGE, "Parentheses at back", testCaseThree)

        val testCaseFour = "".removeParentheses()
        assertEquals(DEFAULT_MESSAGE, "", testCaseFour)

        val testCaseFive = " ".removeParentheses()
        assertEquals(DEFAULT_MESSAGE, "", testCaseFive)

        val testCaseSix = null.removeParentheses()
        assertEquals(DEFAULT_MESSAGE, "", testCaseSix)
    }

    @Test
    fun removeCurrencyDefaultZero() {
        val testCaseOne = "R100,000.99".removeCurrencyDefaultZero()
        assertEquals(DEFAULT_MESSAGE, 100000.99, testCaseOne)

        val testCaseTwo = "R100.99".removeCurrencyDefaultZero()
        assertEquals(DEFAULT_MESSAGE, 100.99, testCaseTwo)

        val testCaseThree = "R 100,123.99".removeCurrencyDefaultZero()
        assertEquals(DEFAULT_MESSAGE, 100123.99, testCaseThree)

        val testCaseFour = "".removeCurrencyDefaultZero()
        assertEquals(DEFAULT_MESSAGE, 0.00, testCaseFour)

        val testCaseFive = " ".removeCurrencyDefaultZero()
        assertEquals(DEFAULT_MESSAGE, 0.00, testCaseFive)

        val testCaseSix = null.removeCurrencyDefaultZero()
        assertEquals(DEFAULT_MESSAGE, 0.00, testCaseSix)
    }

    @Test
    fun removeSpaceAfterForwardSlash() {
        val testCaseOne = "/ ".removeSpaceAfterForwardSlash()
        assertEquals(DEFAULT_MESSAGE, "/", testCaseOne)

        val testCaseTwo = "".removeSpaceAfterForwardSlash()
        assertEquals(DEFAULT_MESSAGE, "", testCaseTwo)

        val testCaseThree = " ".removeSpaceAfterForwardSlash()
        assertEquals(DEFAULT_MESSAGE, "", testCaseThree)

        val testCaseFour = null.removeSpaceAfterForwardSlash()
        assertEquals(DEFAULT_MESSAGE, "", testCaseFour)
    }

    @Test
    fun getUnFormattedPhoneNumber() {
        val testCaseOne = "+27784820256".getUnFormattedPhoneNumber()
        assertEquals(DEFAULT_MESSAGE, "0784820256", testCaseOne)

        val testCaseTwo = "".getUnFormattedPhoneNumber()
        assertEquals(DEFAULT_MESSAGE, "", testCaseTwo)

        val testCaseThree = " ".getUnFormattedPhoneNumber()
        assertEquals(DEFAULT_MESSAGE, "", testCaseThree)

        val testCaseFour = null.getUnFormattedPhoneNumber()
        assertEquals(DEFAULT_MESSAGE, "", testCaseFour)
    }

    @Test
    fun toIncrementedSpaced() {
        val testCaseOne = "00112233".insertSpaceAtIncrements(2)
        assertEquals(DEFAULT_MESSAGE, "00 11 22 33", testCaseOne)

        val testCaseTwo = "00112233".insertSpaceAtIncrements(0)
        assertEquals(DEFAULT_MESSAGE, "00112233", testCaseTwo)

        val testCaseThree = "00112233".insertSpaceAtIncrements(-1)
        assertEquals(DEFAULT_MESSAGE, "00112233", testCaseThree)

        val testCaseFour = "".insertSpaceAtIncrements(4)
        assertEquals(DEFAULT_MESSAGE, "", testCaseFour)

        val testCaseFive = " ".insertSpaceAtIncrements(4)
        assertEquals(DEFAULT_MESSAGE, "", testCaseFive)

        val testCaseSix = null.insertSpaceAtIncrements(4)
        assertEquals(DEFAULT_MESSAGE, "", testCaseSix)
    }

    @Test
    fun toTitleCaseSplit() {
        val testCaseOne = "this is the first split".toTitleCaseSplit()
        assertEquals(DEFAULT_MESSAGE, "This Is The First Split", testCaseOne)

        val testCaseTwo = "this-is-the-first-split".toTitleCaseSplit()
        assertEquals(DEFAULT_MESSAGE, "This-Is-The-First-Split", testCaseTwo)

        val testCaseThree = "this/is/the/first/split".toTitleCaseSplit()
        assertEquals(DEFAULT_MESSAGE, "This/Is/The/First/Split", testCaseThree)

        val testCaseFour = "this'is'the'first'split".toTitleCaseSplit()
        assertEquals(DEFAULT_MESSAGE, "This'Is'The'First'Split", testCaseFour)

        val testCaseFive = "".toTitleCaseSplit()
        assertEquals(DEFAULT_MESSAGE, "", testCaseFive)

        val testCaseSix = " ".toTitleCaseSplit()
        assertEquals(DEFAULT_MESSAGE, "", testCaseSix)

        val testCaseSeven = null.toTitleCaseSplit()
        assertEquals(DEFAULT_MESSAGE, "", testCaseSeven)
    }

    @Test
    fun toTitleCaseCommaHandled() {
        val testCaseOne = "17, test street, sea point, cape town".toTitleCaseRemovingCommas()
        assertEquals(DEFAULT_MESSAGE, "17 Test Street Sea Point Cape Town", testCaseOne)

        val testCaseTwo = " 17, test street, sea point, cape town ".toTitleCaseRemovingCommas()
        assertEquals(DEFAULT_MESSAGE, "17 Test Street Sea Point Cape Town", testCaseTwo)

        val testCaseThree = "".toTitleCaseRemovingCommas()
        assertEquals(DEFAULT_MESSAGE, "", testCaseThree)

        val testCaseFour = " ".toTitleCaseRemovingCommas()
        assertEquals(DEFAULT_MESSAGE, "", testCaseFour)

        val testCaseFive = null.toTitleCaseRemovingCommas()
        assertEquals(DEFAULT_MESSAGE, "", testCaseFive)
    }

    @Test
    fun toFormattedIdNumber() {
        val testCaseOne = "8802055039086".toFormattedIdNumber()
        assertEquals(DEFAULT_MESSAGE, "880205 5039 08 6", testCaseOne)

        val testCaseTwo = "880205503".toFormattedIdNumber()
        assertEquals(DEFAULT_MESSAGE, "880205503", testCaseTwo)

        val testCaseThree = "".toFormattedIdNumber()
        assertEquals(DEFAULT_MESSAGE, "", testCaseThree)

        val testCaseFour = " ".toFormattedIdNumber()
        assertEquals(DEFAULT_MESSAGE, "", testCaseFour)

        val testCaseFive = null.toFormattedIdNumber()
        assertEquals(DEFAULT_MESSAGE, "", testCaseFive)
    }

    @Test
    fun toFormattedAmount() {
        val testCaseOne = "1234567.99".toFormattedAmount()
        assertEquals(DEFAULT_MESSAGE, "1\u00A0234\u00A0567.99", testCaseOne)

        val testCaseTwo = "1234567.11".toFormattedAmount()
        assertEquals(DEFAULT_MESSAGE, "1\u00A0234\u00A0567.11", testCaseTwo)

        val testCaseThree = "".toFormattedAmount()
        assertEquals(DEFAULT_MESSAGE, "", testCaseThree)

        val testCaseFour = " ".toFormattedAmount()
        assertEquals(DEFAULT_MESSAGE, "", testCaseFour)

        val testCaseFive = null.toFormattedAmount()
        assertEquals(DEFAULT_MESSAGE, "", testCaseFive)

        val testCaseSix = "abc".toFormattedAmount()
        assertEquals(DEFAULT_MESSAGE, "abc", testCaseSix)
    }

    @Test
    fun toDoubleString() {
        val testCaseOne = "123.0".toDoubleString()
        assertEquals(DEFAULT_MESSAGE, "123.00", testCaseOne)

        val testCaseTwo = "123".toDoubleString()
        assertEquals(DEFAULT_MESSAGE, "123.00", testCaseTwo)

        val testCaseThree = "".toDoubleString()
        assertEquals(DEFAULT_MESSAGE, "", testCaseThree)

        val testCaseFour = "\u00A0".toDoubleString()
        assertEquals(DEFAULT_MESSAGE, "", testCaseFour)

        val testCaseFive = " ".toDoubleString()
        assertEquals(DEFAULT_MESSAGE, "", testCaseFive)

        val testCaseSix = null.toDoubleString()
        assertEquals(DEFAULT_MESSAGE, "", testCaseSix)
    }

    @Test
    fun toFormattedAmountZeroDefault() {
        val testCaseOne = "1234567.99".toFormattedAmountZeroDefault()
        assertEquals(DEFAULT_MESSAGE, "1\u00A0234\u00A0567.99", testCaseOne)

        val testCaseTwo = "1234567.11".toFormattedAmountZeroDefault()
        assertEquals(DEFAULT_MESSAGE, "1\u00A0234\u00A0567.11", testCaseTwo)

        val testCaseThree = "".toFormattedAmountZeroDefault()
        assertEquals(DEFAULT_MESSAGE, "0.00", testCaseThree)

        val testCaseFour = " ".toFormattedAmountZeroDefault()
        assertEquals(DEFAULT_MESSAGE, "0.00", testCaseFour)

        val testCaseFive = null.toFormattedAmountZeroDefault()
        assertEquals(DEFAULT_MESSAGE, "0.00", testCaseFive)

        val testCaseSix = "abc".toFormattedAmountZeroDefault()
        assertEquals(DEFAULT_MESSAGE, "abc", testCaseSix)
    }

    @Test
    fun takeNumbersOrEmpty() {
        val testCaseOne = "6 months".takeNumbersOrEmpty()
        assertEquals(DEFAULT_MESSAGE, "6", testCaseOne)

        val testCaseTwo = "months".takeNumbersOrEmpty()
        assertEquals(DEFAULT_MESSAGE, "", testCaseTwo)

        val testCaseThree = "6 months 4".takeNumbersOrEmpty()
        assertEquals(DEFAULT_MESSAGE, "64", testCaseThree)

        val testCaseFour = "in 14 Months 5 days".takeNumbersOrEmpty()
        assertEquals(DEFAULT_MESSAGE, "145", testCaseFour)
    }

    @Test
    fun takeLeadingNumbersOrZero() {
        val testCaseOne = "6 months".takeLeadingNumbersOrZero()
        assertEquals(DEFAULT_MESSAGE, "6", testCaseOne)

        val testCaseTwo = "months".takeLeadingNumbersOrZero()
        assertEquals(DEFAULT_MESSAGE, "0", testCaseTwo)

        val testCaseThree = "6 months 4".takeLeadingNumbersOrZero()
        assertEquals(DEFAULT_MESSAGE, "6", testCaseThree)

        val testCaseFour = "in 14 Months 5 days".takeLeadingNumbersOrZero()
        assertEquals(DEFAULT_MESSAGE, "0", testCaseFour)
    }
}