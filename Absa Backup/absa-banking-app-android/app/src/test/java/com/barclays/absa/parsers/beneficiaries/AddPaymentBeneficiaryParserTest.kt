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
package com.barclays.absa.parsers.beneficiaries

import com.barclays.absa.banking.beneficiaries.services.AddPaymentBeneficiaryResponseParser
import com.barclays.absa.banking.beneficiaries.services.BeneficiariesMockFactory
import com.barclays.absa.banking.boundary.model.AddBeneficiaryPaymentObject
import com.barclays.absa.parsers.AbstractResponseParserTest
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class AddPaymentBeneficiaryParserTest : AbstractResponseParserTest() {

    /*private val mockFile = BeneficiariesMockFactory.addPaymentConfirmation()

    @BeforeEach
    fun setUp() {
        responseParser = AddPaymentBeneficiaryResponseParser()
        jsonContent = getContentBody(mockFile, true)
    }

    @Test
    fun testIfPaymentBeneficiariesMatch() {
        val expectedPaymentBeneficiary = responseParser.getParsedResponse(jsonContent) as AddBeneficiaryPaymentObject
        val actualExpectedPaymentBeneficiary = ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false).readValue(jsonContent, AddBeneficiaryPaymentObject::class.java)
        Assertions.assertEquals(expectedPaymentBeneficiary, actualExpectedPaymentBeneficiary)
    }*/
}