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

import java.util.*
import java.util.regex.Pattern

object PasswordValidator {
    private val map: MutableMap<String, Boolean> = HashMap()
    const val UPPER_N_LOWERCASE = "uppernlowercase"
    const val DIGIT = "digit"
    const val SPECIAL_CASE = "specialcase"
    const val WHITESPACE = "whitespace"
    const val LENGTH_RESTRICTION = "lengthrestriction"
    const val NAME = "name"
    const val SEQUENTIAL_ASCENDING = "sequentialascending"
    private const val SEQUENTIAL_DESCENDING = "sequentialDescending"
    private const val REPEATING = "repeating"

    @JvmStatic
    fun buildValidator(password: String, nameOfUser: String?): Map<String, Boolean> {
        map[UPPER_N_LOWERCASE] = validatePassword(password, ".*[A-Za-z]")
        map[DIGIT] = validatePassword(password, ".*[0-9]")
        map[SPECIAL_CASE] = validatePassword(password, ".*[^\\w]")
        map[WHITESPACE] = validatePassword(password, "\\s")
        map[LENGTH_RESTRICTION] = validatePassword(password, "^.{8,12}$")
        map[NAME] = validatePassword(password, "(?i)$nameOfUser(?-i).*")
        map[SEQUENTIAL_ASCENDING] = validatePassword(password, "(?:0(?=1)|1(?=2)|2(?=3)|3(?=4)|4(?=5)|5(?=6)|6(?=7)|7(?=8)|8(?=9)|9(?=0)){2}\\d")
        map[SEQUENTIAL_DESCENDING] = validatePassword(password, "(?:0(?=9)|1(?=0)|2(?=1)|3(?=2)|4(?=3)|5(?=4)|6(?=5)|7(?=6)|8(?=7)|9(?=8)){2}\\d")
        map[REPEATING] = validatePassword(password, "^(\\d+)(?:\u0001)")
        return map
    }

    private fun validatePassword(password: String, patternParameter: String): Boolean = Pattern.compile(patternParameter).matcher(password).find()
}