/*
 *  Copyright (c) 2020 Absa Bank Limited, All Rights Reserved.
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
package za.co.absa.networking

import okhttp3.Interceptor
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import okio.Buffer
import za.co.absa.networking.hmac.HttpAuthorizationScheme

class JsonRequestHmacInterceptor(private val shouldUseJwtScheme: Boolean) : BaseHmacInterceptor() {

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()

        val body = bodyToString(request.body).replace("\\", "")

        val scheme = createHttpAuthorizationScheme(request, body)

        val mediaType: MediaType = "application/json; charset=utf-8".toMediaType()
        val requestBody = body.toRequestBody(mediaType)

        val headers = getRequestHeaders(request.headers, scheme.requestHeaders, scheme.authorizationHeader.getHeaderValue())
        Logger.d("AUTH HEADERS", headers.toString())
        val newRequest = request.newBuilder().post(requestBody).headers(headers).build()
        return chain.proceed(newRequest)
    }

    private fun bodyToString(requestBody: RequestBody?): String {
        val buffer = Buffer()
        requestBody?.writeTo(buffer)
        return buffer.readUtf8()
    }

    override fun createScheme(): HttpAuthorizationScheme {
        return if (ExpressNetworkingConfig.accessToken.isNotEmpty() && shouldUseJwtScheme) {
            hmacAuthorizationScheme.generateSha256JwtScheme()
        } else {
            hmacAuthorizationScheme.generateSha256Scheme()
        }
    }
}