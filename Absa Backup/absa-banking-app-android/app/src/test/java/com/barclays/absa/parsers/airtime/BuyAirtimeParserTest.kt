/*
 *  Copyright (c) 2018 Absa Bank Limited, All Rights Reserved.
 *
 *  This code is confidential to Absa Bank Limited and shall not be disclosed
 *  outside the Bank without the prior written permission of the Absa Legal
 *
 *  In the event that such disclosure is permitted the code shall not be copied
 *  or distributed other than on a need-to-know basis and any recipients may be
 *  required to sign a confidentiality undertaking in favor of Absa Bank
 *  Limited
 *
 */
package com.barclays.absa.parsers.airtime

import com.barclays.absa.banking.boundary.model.airtime.AirtimeBuyBeneficiary
import com.barclays.absa.banking.buy.services.airtime.BuyAirtimeResponseParser
import com.barclays.absa.parsers.AbstractResponseParserTest
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class BuyAirtimeParserTest : AbstractResponseParserTest() {
    /*private val mockFile = "airtime/op0620_buy_airtime.json"

    @BeforeEach
    fun setUp() {
        responseParser = BuyAirtimeResponseParser()
        jsonContent = getContentBody(mockFile, true)
    }

    @Test
    fun testIfAirtimeBuyBeneficiariesMatch() {
        val expectedAirtimeBuyBeneficiary = responseParser.getParsedResponse(jsonContent) as AirtimeBuyBeneficiary
        val actualAccountAirtimeBuyBeneficiary = ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false).readValue(jsonContent, AirtimeBuyBeneficiary::class.java)
        Assertions.assertEquals(expectedAirtimeBuyBeneficiary, actualAccountAirtimeBuyBeneficiary)
    }*/
}