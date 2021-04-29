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

abstract class MaskCharacter {
    abstract fun isValidCharacter(ch: Char): Boolean
    open fun processCharacter(ch: Char): Char? {
        return ch
    }

    open val isPrepopulate: Boolean
        get() = false
}

internal class DigitCharacter : MaskCharacter() {
    override fun isValidCharacter(ch: Char): Boolean {
        return Character.isDigit(ch)
    }
}

internal class UpperCaseCharacter : MaskCharacter() {
    override fun isValidCharacter(ch: Char): Boolean {
        return Character.isUpperCase(ch)
    }

    override fun processCharacter(ch: Char): Char {
        return Character.toUpperCase(ch)
    }
}

internal class LowerCaseCharacter : MaskCharacter() {
    override fun isValidCharacter(ch: Char): Boolean {
        return Character.isLowerCase(ch)
    }

    override fun processCharacter(ch: Char): Char? {
        return Character.toLowerCase(ch)
    }
}

internal class AlphaNumericCharacter : MaskCharacter() {
    override fun isValidCharacter(ch: Char): Boolean {
        return Character.isLetterOrDigit(ch)
    }
}

internal class LetterCharacter : MaskCharacter() {
    override fun isValidCharacter(ch: Char): Boolean {
        return Character.isLetter(ch)
    }
}

internal class HexCharacter : MaskCharacter() {
    override fun isValidCharacter(ch: Char): Boolean {
        return Character.isLetterOrDigit(ch) && HEX_CHARS.indexOf(Character.toUpperCase(ch)) != -1
    }

    override fun processCharacter(ch: Char): Char {
        return Character.toUpperCase(ch)
    }

    companion object {
        private const val HEX_CHARS = "0123456789ABCDEF"
    }
}

internal class LiteralCharacter : MaskCharacter {
    private var character: Char?

    constructor() {
        character = null
    }

    constructor(ch: Char) {
        character = ch
    }

    override fun isValidCharacter(ch: Char): Boolean {
        return character == null || character == ch
    }

    override fun processCharacter(ch: Char): Char? {
        return if (character != null) character else ch
    }

    override val isPrepopulate: Boolean
        get() = character != null
}