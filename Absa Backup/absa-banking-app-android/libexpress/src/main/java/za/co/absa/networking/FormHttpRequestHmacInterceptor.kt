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

import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import za.co.absa.networking.hmac.HttpAuthorizationScheme
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import java.util.*

class FormHttpRequestHmacInterceptor(private val parameterMap: Map<String, String>, private val isPinRequest: Boolean = false) : BaseHmacInterceptor() {

    override fun intercept(chain: Interceptor.Chain): Response {
        val hMACSignatureParameterList = mutableListOf<String>()
        parameterMap.entries.filter { !it.key.equals(ignoreCase = true, other = "pin") && !it.key.equals(ignoreCase = true, other = "newpin") }.forEach {
            hMACSignatureParameterList.add(it.key)
        }

        hMACSignatureParameterList.sort()

        val hmacContentBuilder = StringBuilder()
        hMACSignatureParameterList.forEach { name ->
            val urlEncodedParameterName = URLEncoder.encode(name.toLowerCase(Locale.getDefault()), StandardCharsets.UTF_8.name())
            val parameterValue = URLEncoder.encode(parameterMap[name], StandardCharsets.UTF_8.name())
            hmacContentBuilder.append("$urlEncodedParameterName=$parameterValue&")
        }

        val hmacContent = hmacContentBuilder.toString().dropLast(1)
        Logger.d("HTTP form content", hmacContent)

        val request = chain.request()
        val scheme = createHttpAuthorizationScheme(request, hmacContent)

        val headers = getRequestHeaders(request.headers, scheme.requestHeaders, scheme.authorizationHeader.getHeaderValue())
        Logger.d("AUTH HEADERS", headers.toString())
        val body = request.body
        var newRequest: Request = request
        body?.let {
            newRequest = request.newBuilder().post(it).headers(headers).build()
        }
        Logger.d("express_form_request", "$request\n ${request.body.toString()}")
        return chain.proceed(newRequest)
    }

    override fun createScheme(): HttpAuthorizationScheme {
        return hmacAuthorizationScheme.generateSha256JwtScheme(isPinRequest)
    }
}