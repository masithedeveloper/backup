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
package styleguide.forms.maskedEditText

interface IFormattedString : CharSequence {
    val unMaskedString: String
}

internal abstract class AbstractFormattedString(val mask: Mask, val inputString: String) : IFormattedString {

    final override val unMaskedString: String

    private var formattedString: String? = null
    abstract fun formatString(): String
    abstract fun buildRawString(str: String): String

    override val length: Int
        get() = toString().length

    override fun toString(): String {
        if (formattedString == null) {
            formattedString = formatString()
        }
        return formattedString!!
    }

    override fun subSequence(startIndex: Int, endIndex: Int): CharSequence {
        return toString().subSequence(startIndex, endIndex)
    }

    init {
        unMaskedString = this.buildRawString(inputString)
    }
}

internal class FormattedString(mask: Mask, rawString: String) : AbstractFormattedString(mask, rawString) {
    override fun buildRawString(str: String): String {
        val builder = StringBuilder()
        val inputLen = kotlin.math.min(mask.size(), str.length)
        for (i in 0 until inputLen) {
            val ch = str[i]
            if (!mask.isValidPrepopulateCharacter(ch, i)) builder.append(ch)
        }
        return builder.toString()
    }

    override fun get(index: Int): Char = toString()[index]

    override fun formatString(): String {
        val builder = StringBuilder()
        var index = 0
        var maskCharIndex = 0
        var stringCharacter: Char
        while (index < inputString.length && maskCharIndex < mask.size()) {
            val maskChar = mask[maskCharIndex]
            stringCharacter = inputString[index]
            when {
                maskChar.isValidCharacter(stringCharacter) -> {
                    builder.append(maskChar.processCharacter(stringCharacter))
                    index += 1
                    maskCharIndex += 1
                }
                maskChar.isPrepopulate -> {
                    builder.append(maskChar.processCharacter(stringCharacter))
                    maskCharIndex += 1
                }
                else -> {
                    index += 1
                }
            }
        }
        return builder.toString()
    }
}