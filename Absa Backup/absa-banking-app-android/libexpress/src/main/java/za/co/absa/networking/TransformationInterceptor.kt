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
package za.co.absa.networking

import com.fasterxml.jackson.databind.ObjectMapper
import okhttp3.Interceptor
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.Response
import okhttp3.ResponseBody.Companion.toResponseBody
import za.co.absa.networking.ResponseHelper.removeNullsFromJsonNode
import java.text.SimpleDateFormat
import java.util.*

class TransformationInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val proceed = chain.proceed(request)

        proceed.headers["X-Absa-Date"]?.let { dateTime ->
            val simpleDateFormat = SimpleDateFormat("yyyyMMdd'T'HHmmss", ExpressNetworkingConfig.applicationLocale)
            ExpressNetworkingConfig.serverDateTime = simpleDateFormat.parse(dateTime) ?: Date()
        }

        val responseBody = proceed.peekBody(Long.MAX_VALUE).string()
        Logger.d("express_response", responseBody)

        val objectMapper = ObjectMapper()

        val originalBody = proceed.body?.string()
        val jsonNode = objectMapper.readTree(originalBody)
        removeNullsFromJsonNode(jsonNode)

        val mediaType: MediaType = "application/json".toMediaType()

        val transformedBody = objectMapper.writeValueAsString(jsonNode).toResponseBody(mediaType)

        return proceed.newBuilder().body(transformedBody).build()
    }
}