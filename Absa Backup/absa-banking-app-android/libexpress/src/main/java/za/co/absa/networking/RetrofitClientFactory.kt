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

import android.os.Build
import okhttp3.*
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.scalars.ScalarsConverterFactory
import za.co.absa.networking.hmac.service.ApiService
import java.util.concurrent.TimeUnit

object RetrofitClientFactory {

    private val cookieStore: HashMap<String, List<Cookie>> = HashMap()
    private const val EXTRA_LOGGING = true

    private var cookieJar: CookieJar = object : CookieJar {
        override fun saveFromResponse(url: HttpUrl, cookies: List<Cookie>) {
            var cookieUrl = url.host
            if (url.pathSegments.isNotEmpty()) {
                cookieUrl += "/" + url.pathSegments.first()
            }

            cookieStore[cookieUrl] = cookies.filter { it.name != "XFPT" && it.name != "WFPT" }
        }

        override fun loadForRequest(url: HttpUrl): List<Cookie> {
            var cookieUrl = url.host
            if (url.pathSegments.isNotEmpty()) {
                cookieUrl += "/" + url.pathSegments.first()
            }

            if (BuildConfig.nCipherBaseUrl.contains(cookieUrl, ignoreCase = true)) {
                cookieUrl = BuildConfig.XTMS_BASE_URL.dropLast(1).substringAfter("://")
            }

            val cookies = cookieStore[cookieUrl]
            return cookies ?: ArrayList()
        }
    }

    fun clearCookieJar() {
        cookieStore.clear()
    }

    fun createRetrofitClient(baseUrl: String): ApiService {
        val shouldUseJwtToken = baseUrl != BuildConfig.XMMS_BASE_URL

        val okHttpClientBuilder = createHttpClientBuilder(baseUrl)
        okHttpClientBuilder.addInterceptor(JsonRequestHmacInterceptor(shouldUseJwtToken))

        val retrofit = Retrofit.Builder()
                .client(okHttpClientBuilder.build())
                .baseUrl(baseUrl)
                .addConverterFactory(ScalarsConverterFactory.create())
                .build()

        return retrofit.create(ApiService::class.java)
    }

    fun createHttpPostFormClient(baseUrl: String, httpFormRequest: HttpFormRequest, isPinEncryptRequest: Boolean): OkHttpClient {
        val okHttpClientBuilder = createHttpClientBuilder(baseUrl)
        okHttpClientBuilder.addInterceptor(FormHttpRequestHmacInterceptor(httpFormRequest.parameters, isPinEncryptRequest))
        okHttpClientBuilder.addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
        return okHttpClientBuilder.build()
    }

    private fun createHttpClientBuilder(baseUrl: String): OkHttpClient.Builder {
        val socketFactory = SSLSocketFactory()

        val timeout60Seconds: Long = 60

        val okHttpClientBuilder = OkHttpClient.Builder()
                .connectTimeout(timeout60Seconds, TimeUnit.SECONDS)
                .readTimeout(timeout60Seconds, TimeUnit.SECONDS)
                .writeTimeout(timeout60Seconds, TimeUnit.SECONDS)
                .addInterceptor(LoggingInterceptor(BuildConfig.DEBUG))
                .addInterceptor(TransformationInterceptor())
                .addNetworkInterceptor { chain ->
                    val request = chain.request()
                    if (EXTRA_LOGGING) {
                        request.headers.forEach { Logger.d("express_header", it.first + ": " + it.second) }
                    }
                    chain.proceed(request)
                }
                .cookieJar(cookieJar)

        if (ApiService.isCertificatePinningEnabled) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                var hostName = BuildConfig.expressHostName
                val hostCertificateHash = if (BuildConfig.PROD) {
                    when (baseUrl) {
                        BuildConfig.XSMS_BASE_URL -> {
                            hostName = BuildConfig.xsmsHostName
                            BuildConfig.PROD_XSMS_SHA256_HASH
                        }
                        else -> BuildConfig.PROD_SHA256_HASH
                    }
                } else {
                    BuildConfig.UAT_SHA256_HASH
                }
                val certificatePinner = CertificatePinner.Builder()
                        .add(hostName,
                                hostCertificateHash,
                                BuildConfig.DIGICERT_INTERMEDIATE_CERT_SHA256_HASH,
                                BuildConfig.DIGICERT_ROOT_CERT_SHA256_HASH
                        )
                        .build()
                okHttpClientBuilder.certificatePinner(certificatePinner)
            } else {
                okHttpClientBuilder.sslSocketFactory(socketFactory, socketFactory.getTrustManager())
                okHttpClientBuilder.hostnameVerifier { _, _ -> true }
            }
        }

        return okHttpClientBuilder
    }
}