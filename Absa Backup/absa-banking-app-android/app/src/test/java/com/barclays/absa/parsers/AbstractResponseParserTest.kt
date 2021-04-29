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
package com.barclays.absa.parsers

import com.barclays.absa.banking.boundary.model.Payload
import com.barclays.absa.banking.framework.parsers.ResponseParser
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.jupiter.api.fail
import java.io.BufferedReader
import java.io.File
import java.io.FileReader
import java.io.IOException

open class AbstractResponseParserTest {

    lateinit var responseParser: ResponseParser
    private var baseUrl = "src/stub/assets/api_responses/"
    var jsonContent: String? = null

    open fun getContentBody(mockFile: String, print: Boolean): String? {
        try {
            val reader = BufferedReader(FileReader(File(baseUrl + mockFile)))
            var json = ""
            val lines = reader.readLines()

            for (line in lines) {
                json += line
                if (print) println(line)
            }
            return getPayDataJson(json)?.toString()
        } catch (e: IOException) {
            fail("Failed to read the mock file")
        }
    }

    private fun getPayDataJson(json: String): JsonNode? {
        try {
            val jsonPayload = ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false).readValue(json, Payload::class.java)
            val payloadModel = jsonPayload?.payloadModel
            return payloadModel?.payloadData
        } catch (e: ClassCastException) {
            fail("Failed to parse the json payload")
        }
    }
}