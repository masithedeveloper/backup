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

internal class MaskCharacterFabric {

    companion object {
        private const val ANYTHING_KEY = '*'
        private const val DIGIT_KEY = '#'
        private const val UPPERCASE_KEY = 'U'
        private const val LOWERCASE_KEY = 'L'
        private const val ALPHA_NUMERIC_KEY = 'A'
        private const val CHARACTER_KEY = '?'
        private const val HEX_KEY = 'H'
    }

    fun buildCharacter(ch: Char): MaskCharacter {
        return when (ch) {
            ANYTHING_KEY -> LiteralCharacter()
            DIGIT_KEY -> DigitCharacter()
            UPPERCASE_KEY -> UpperCaseCharacter()
            LOWERCASE_KEY -> LowerCaseCharacter()
            ALPHA_NUMERIC_KEY -> AlphaNumericCharacter()
            CHARACTER_KEY -> LetterCharacter()
            HEX_KEY -> HexCharacter()
            else -> LiteralCharacter(ch)
        }
    }
}