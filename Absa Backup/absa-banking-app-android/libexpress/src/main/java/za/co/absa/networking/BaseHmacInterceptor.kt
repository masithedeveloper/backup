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

import android.util.Log
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import okio.Buffer
import za.co.absa.networking.hmac.ClientDetails
import za.co.absa.networking.hmac.HmacSha256AuthorizationScheme
import za.co.absa.networking.hmac.HttpAuthorizationScheme
import za.co.absa.networking.hmac.utils.HmacUtils

abstract class BaseHmacInterceptor : Interceptor {

    internal lateinit var hmacAuthorizationScheme: HmacSha256AuthorizationScheme

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()

        val body = bodyToString(request.body).replace("\\", "")

        val scheme = createHttpAuthorizationScheme(request, body)

        val mediaType: MediaType = "application/json; charset=utf-8".toMediaType()

        val requestBody = body.toRequestBody(mediaType)

        val headers = getRequestHeaders(request.headers, scheme.requestHeaders, scheme.authorizationHeader.getHeaderValue())
        Log.d("AUTH HEADERS", headers.toString())
        val newRequest = request.newBuilder().post(requestBody).headers(headers).build()
        return chain.proceed(newRequest)
    }

    internal fun createHttpAuthorizationScheme(request: Request, body: String): HttpAuthorizationScheme {
        val hMacSecret = if (ExpressNetworkingConfig.hMacSecret.isNotEmpty()) {
            ExpressNetworkingConfig.hMacSecret
        } else {
            HmacUtils.getRegistrationHMacSecret()
        }

        hmacAuthorizationScheme = HmacSha256AuthorizationScheme().apply {
            client = ClientDetails().apply {
                deviceID = ExpressNetworkingConfig.deviceId
                secret = hMacSecret
            }

            uri = request.url.toUri()
            content = body
        }

        return createScheme()
    }

    internal abstract fun createScheme(): HttpAuthorizationScheme

    internal fun getRequestHeaders(existingHeaders: Headers, extraHeaders: Map<String, String>, authHeader: String): Headers {
        Headers.Builder().apply {
            if (ExpressNetworkingConfig.isLoggedIn && existingHeaders.size > 0) {
                addAll(existingHeaders)
            }

            add("Authorization", authHeader)
            add("User-Agent", "ANDROID - " + ExpressNetworkingConfig.appVersion)
            add("X-Absa-ENV", ExpressNetworkingConfig.expressEnvironment)
            add("X-Absa-SignerVersion", "1.7")

            extraHeaders.forEach { add(it.key, it.value) }
            return build()
        }
    }

    private fun bodyToString(requestBody: RequestBody?): String {
        val buffer = Buffer()
        requestBody?.writeTo(buffer)
        return buffer.readUtf8()
    }
}