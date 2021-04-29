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
import org.junit.jupiter.api.TestInstance

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ResidingCountryTest {

    private var residingCountryOne: ResidingCountry = ResidingCountry()
    private var residingCountryTwo: ResidingCountry = ResidingCountry()

    @BeforeEach
    fun setUp() {
        residingCountryOne.apply {
            cityName = "Cape Town"
            cmacountryIndicator = "ZA"
            countryCode = "ZA"
            countryDescription = "Country"
            displayQuestionsAndAnswers = "Yes"
            isoCountryCode = "ZA"
            stateName = "Western Cape"
        }

        residingCountryTwo.apply {
            cityName = "Cape Town"
            cmacountryIndicator = "ZA"
            countryCode = "ZA"
            countryDescription = "Country"
            displayQuestionsAndAnswers = "Yes"
            isoCountryCode = "ZA"
            stateName = "Western Cape"
        }
    }

    @Test
    fun shouldReturnStringWhenToStringCalledOnObject() {
        assertEquals("(cmacountryIndicator=ZA, cityName=Cape Town, countryDescription=Country, stateName=Western Cape, displayQuestionsAndAnswers=Yes, countryCode=ZA, isoCountryCode=ZA)"
                , residingCountryOne.toString())
    }

    @Test
    fun shouldReturnTrueWhenReferenceEqual() {
        assertEquals(true, residingCountryOne == residingCountryOne)
    }

    @Test
    fun shouldReturnTrueWhenObjectEqual() {
        assertEquals(true, residingCountryOne == residingCountryTwo)
    }

    @Test
    fun shouldReturnFalseWhenObjectNotEqual() {
        residingCountryTwo.cityName = ""
        assertEquals(false, residingCountryOne == residingCountryTwo)
    }

    @Test
    fun shouldReturnFalseWhenObjectNullNotEqual() {
        residingCountryTwo = ResidingCountry()
        assertEquals(false, residingCountryOne == residingCountryTwo)
    }
}
