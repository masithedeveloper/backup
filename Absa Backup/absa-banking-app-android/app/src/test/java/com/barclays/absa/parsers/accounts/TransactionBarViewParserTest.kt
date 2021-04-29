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
package com.barclays.absa.parsers.accounts

import com.barclays.absa.parsers.AbstractResponseParserTest

class TransactionBarViewParserTest : AbstractResponseParserTest() {

    /*private val mockFile = ""

    @BeforeEach
    fun setUp(){
        responseParser = TransactionBarViewParser()
        jsonContent =  getContentBody(mockFile, true)
    }

    @Test
    fun testIfTransactionDetailsMatch(){
        val expectedTransactionsDetails =  responseParser.getParsedResponse(jsonContent) as TransactionDetails
        val actualTransactionDetails = ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false).readValue(jsonContent, TransactionDetails::class.java)
        Assertions.assertEquals(expectedTransactionsDetails,  actualTransactionDetails)
    }*/
}