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

import java.util.*

class Mask private constructor() {

    private lateinit var mask: List<MaskCharacter>
    private lateinit var prepopulateCharacter: MutableList<MaskCharacter>
    private lateinit var rawMaskString: String

    private val maskFabric: MaskCharacterFabric = MaskCharacterFabric()

    internal constructor(formatString: String) : this() {
        rawMaskString = formatString
        mask = buildMask(rawMaskString)
    }

    fun size(): Int = mask.size

    operator fun get(index: Int): MaskCharacter = mask[index]

    fun isValidPrepopulateCharacter(char: Char, index: Int): Boolean {
        return try {
            val character = mask[index]
            character.isPrepopulate && character.isValidCharacter(char)
        } catch (e: IndexOutOfBoundsException) {
            false
        }
    }

    private fun buildMask(formatString: String): List<MaskCharacter> {
        val result: MutableList<MaskCharacter> = ArrayList()
        prepopulateCharacter = ArrayList()
        for (char in formatString.toCharArray()) {
            val maskCharacter = maskFabric.buildCharacter(char)
            if (maskCharacter.isPrepopulate) {
                prepopulateCharacter.add(maskCharacter)
            }
            result.add(maskCharacter)
        }
        return result
    }

    fun getFormattedString(value: String): IFormattedString {
        return FormattedString(this, value)
    }
}