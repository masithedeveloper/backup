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

package za.co.absa.networking.hmac.utils

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.module.SimpleModule
import com.fasterxml.jackson.module.kotlin.KotlinModule
import za.co.absa.networking.ExpressNetworkingConfig
import za.co.absa.networking.Logger
import za.co.absa.networking.ResponseHelper
import za.co.absa.networking.jackson.BooleanDeserializer
import java.nio.charset.Charset

object StubHelper {
    fun getFileContent(fileName: String): String {
        ExpressNetworkingConfig.appContext.assets.open(fileName).use {
            val readBuffer = ByteArray(it.available())
            it.read(readBuffer)
            return String(readBuffer, Charset.defaultCharset())
        }
    }

    inline fun <reified T> mockResponseFileToObject(mockResponseFile: String): T? {
        val objectMapper = createObjectMapper()
        val fileContent = getFileContent(mockResponseFile)
        if (fileContent.isNotEmpty()) {
            Logger.d("express_response:stub", fileContent)
            val jsonNode = objectMapper.readTree(fileContent)

            ResponseHelper.removeNullsFromJsonNode(jsonNode)
            return objectMapper.treeToValue(jsonNode, T::class.java)
        }
        return null
    }

    fun createObjectMapper(): ObjectMapper {
        val objectMapper = ObjectMapper().registerModule(KotlinModule())
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)

        val booleanDeserializerModule = SimpleModule("BooleanDeserializerModule")
        objectMapper.registerModule(BooleanDeserializer().let {
            booleanDeserializerModule.addDeserializer(java.lang.Boolean.TYPE, it)
            booleanDeserializerModule.addDeserializer(Boolean::class.java, it)
        })

        return objectMapper
    }
}