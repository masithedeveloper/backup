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
package com.barclays.absa.parsers.login

import com.barclays.absa.banking.boundary.model.SecureHomePageObject
import com.barclays.absa.banking.framework.MockFactory
import com.barclays.absa.banking.login.services.dto.SecureHomePageParser
import com.barclays.absa.parsers.AbstractResponseParserTest
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class SecureHomePageParserTest : AbstractResponseParserTest() {
    /*private val mockFile = MockFactory.login()

    @BeforeEach
    fun setUp() {
        responseParser = SecureHomePageParser()
        jsonContent = getContentBody(mockFile, true)
    }

    @Test
    fun testIfSecureHomePageMatch() {
        val expectedSecureHomePage = responseParser.getParsedResponse(jsonContent) as SecureHomePageObject
        val actualSecureHomePage = ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false).readValue(jsonContent, SecureHomePageObject::class.java)
        Assertions.assertEquals(expectedSecureHomePage, actualSecureHomePage)
    }*/
}