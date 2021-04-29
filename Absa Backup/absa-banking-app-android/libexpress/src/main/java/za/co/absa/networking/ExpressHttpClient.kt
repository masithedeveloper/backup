/*
 * Copyright (c) 2021 Absa Bank Limited, All Rights Reserved.
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

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import okhttp3.FormBody
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.Request
import okhttp3.ResponseBody.Companion.toResponseBody
import okhttp3.internal.toImmutableMap
import za.co.absa.networking.error.ApplicationError
import za.co.absa.networking.hmac.service.ApiService
import za.co.absa.networking.hmac.utils.StubHelper

class ExpressHttpClient(val url: String, val isPinEncryptRequest: Boolean = false) {

    var useStubs: Boolean = false
    lateinit var stubResponseFile: String

    inline fun <reified T> performRequest(httpFormRequest: HttpFormRequest, klass: Class<T>): T? {
        if (useStubs) {
            if (stubResponseFile.isEmpty()) {
                ApiService.httpErrorLiveData.postValue(ApplicationError(T::class.java.name, T::class.java.name + ": No Mock for this Request"))
                return null
            }
            return StubHelper.mockResponseFileToObject<T>(stubResponseFile)
        } else {
            val formBody = createFormBody(httpFormRequest)
            val okHttpRequest = Request.Builder()
                    .url(url)
                    .post(formBody)
                    .build()

            val jsonNode: JsonNode
            val objectMapper = ObjectMapper()

            val okHttpClient = RetrofitClientFactory.createHttpPostFormClient(url, httpFormRequest, isPinEncryptRequest)
            val httpResponse = okHttpClient.newCall(okHttpRequest).execute()
            jsonNode = objectMapper.readTree(httpResponse.body?.string())
            objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
            ResponseHelper.removeNullsFromJsonNode(jsonNode)
            val mediaType: MediaType = "application/json".toMediaType()
            val transformedBody = objectMapper.writeValueAsString(jsonNode).toResponseBody(mediaType)
            return objectMapper.readValue(transformedBody.string(), klass)
        }
    }

    fun createFormBody(httpFormRequest: HttpFormRequest): FormBody {
        val parameters = httpFormRequest.parameters.toImmutableMap()
        val requestBodyBuilder = FormBody.Builder()
        val entries: Set<Map.Entry<String, String>> = parameters.entries
        for (entry in entries) {
            val key = entry.key
            val value = entry.value
            if (key.isEmpty()) continue
            requestBodyBuilder.add(key, value)
        }
        return requestBodyBuilder.build()
    }

}

class HttpFormRequest {

    @JsonIgnore
    var parameters = mutableMapOf<String, String>()

    class Builder {
        private val formRequest = HttpFormRequest()

        fun addParameter(parameterName: String, parameterValue: String): Builder {
            formRequest.parameters[parameterName] = parameterValue
            return this
        }

        fun build() = formRequest

    }
}