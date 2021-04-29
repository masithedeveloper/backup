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
package com.barclays.absa.banking.payments.international.services.dto

import org.junit.Assert.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

internal class ValidateForHolidaysAndTimeObjectTest {

    private var validateForHolidaysAndTimeObjectOne: ValidateForHolidaysAndTimeObject = ValidateForHolidaysAndTimeObject()
    private var validateForHolidaysAndTimeObjectTwo: ValidateForHolidaysAndTimeObject = ValidateForHolidaysAndTimeObject()

    @BeforeEach
    fun setUp() {
        validateForHolidaysAndTimeObjectOne.apply {
            allowSwift = true
            allowWU = true
        }

        validateForHolidaysAndTimeObjectTwo.apply {
            allowSwift = true
            allowWU = true
        }
    }

    @Test
    fun shouldReturnStringWhenToStringCalledOnObject() {
        assertEquals("(allowSwift=true, allowWU=true)", validateForHolidaysAndTimeObjectOne.toString())
    }

    @Test
    fun shouldReturnTrueWhenReferenceEqual() {
        assertEquals(true, validateForHolidaysAndTimeObjectOne == validateForHolidaysAndTimeObjectOne)
    }

    @Test
    fun shouldReturnTrueWhenObjectEqual() {
        assertEquals(true, validateForHolidaysAndTimeObjectOne == validateForHolidaysAndTimeObjectTwo)
    }

    @Test
    fun shouldReturnFalseWhenObjectNotEqual() {
        validateForHolidaysAndTimeObjectOne.allowSwift = false
        assertEquals(false, validateForHolidaysAndTimeObjectOne == validateForHolidaysAndTimeObjectTwo)
    }

    @Test
    fun shouldReturnFalseWhenObjectNullNotEqual() {
        validateForHolidaysAndTimeObjectOne = ValidateForHolidaysAndTimeObject()
        assertEquals(false, validateForHolidaysAndTimeObjectOne == validateForHolidaysAndTimeObjectTwo)
    }
}