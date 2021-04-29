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
package com.barclays.absa.parsers.cashsend

import com.barclays.absa.banking.boundary.model.SendBenCashSendObject
import com.barclays.absa.banking.cashSend.services.SendBenCashSendResponseParser
import com.barclays.absa.parsers.AbstractResponseParserTest
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class SendBeneficiaryCashSendParserTest : AbstractResponseParserTest() {
    /*private val mockFile = "cash_send/op0612_send_beneficiary_cashsend.json"

    @BeforeEach
    fun setUp() {
        responseParser = SendBenCashSendResponseParser()
        jsonContent = getContentBody(mockFile, true)
    }

    @Test
    fun testIfCashSendBeneficiariesMatch() {
        val expectedCashSendBeneficiary = responseParser.getParsedResponse(jsonContent) as SendBenCashSendObject
        val actualCashSendBeneficiary = ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false).readValue(jsonContent, SendBenCashSendObject::class.java)
        Assertions.assertEquals(expectedCashSendBeneficiary, actualCashSendBeneficiary)
    }*/
}