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
package za.co.absa.networking.hmac.service

import android.os.Build
import androidx.lifecycle.MutableLiveData
import com.fasterxml.jackson.databind.ObjectMapper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import retrofit2.http.Body
import retrofit2.http.HeaderMap
import retrofit2.http.POST
import retrofit2.http.Path
import za.co.absa.networking.BuildConfig
import za.co.absa.networking.ExpressNetworkingConfig
import za.co.absa.networking.Logger
import za.co.absa.networking.RetrofitClientFactory
import za.co.absa.networking.dto.BaseResponse
import za.co.absa.networking.dto.ResponseHeader
import za.co.absa.networking.error.ApplicationError
import za.co.absa.networking.hmac.service.ApiService.Companion.httpErrorLiveData
import za.co.absa.networking.hmac.utils.StubHelper

interface ApiService {

    @POST("{serviceOperation}")
    suspend fun submitRequest(@HeaderMap headers: Map<String, String>, @Path("serviceOperation") serviceOperation: String, @Body request: String): String

    companion object {
        val isCertificatePinningEnabled = !BuildConfig.DEBUG || BuildConfig.ENABLE_CERT_PINNING
        var httpErrorLiveData: MutableLiveData<ApplicationError> = MutableLiveData()
    }
}

abstract class BaseRepository: ExpressBaseRepository() {

    abstract var forceStub: Boolean

    abstract val apiService: ApiService

    abstract var request: BaseRequest

    lateinit var objectMapper: ObjectMapper

    abstract fun handleFailureEvent(header: ResponseHeader)

    abstract fun createRequest()

    abstract fun handleSureCheckEvent()

    fun createBaseRequestBuilderWithDeviceDetail(buildRequest: BaseRequest.Builder) = buildRequest.addDictionaryParameter("deviceDetailVO", getDeviceDetailMap())

    fun createRequestBuilderWithDeviceDetail(buildRequest: BaseRequest.Builder) = Request.Builder(buildRequest).addDictionaryParameter("deviceDetailVO", getDeviceDetailMap())

    private fun getDeviceDetailMap(): MutableMap<String, String?> = mutableMapOf<String, String?>().apply {
        put("appVersion", ExpressNetworkingConfig.appVersion)
        put("deviceId", ExpressNetworkingConfig.deviceId)
        put("deviceMake", Build.MANUFACTURER)
        put("deviceModel", Build.MODEL)
        put("osType", "ANDROID")
        put("osVersion", Build.VERSION.SDK_INT.toString())
        put("osPrivilege", "false")
    }

    suspend inline fun <reified T> safeApiCall(headers: Map<String, String>, mockResponseFile: String): T? {
        @Suppress("ConstantConditionIf", "BlockingMethodInNonBlockingContext")
        if (BuildConfig.STUB || forceStub) {
            if (mockResponseFile.isEmpty()) {
                httpErrorLiveData.postValue(ApplicationError(T::class.java.name, T::class.java.name + ": No Mock for this Request"))
                return null
            }
            createRequest()
            delay(300)
            return handleBaseResponse(headers, T::class.java, StubHelper.mockResponseFileToObject<T>(mockResponseFile))
        }

        apiRetryCount = 0
        return performRequest(headers, T::class.java)
    }

    suspend fun <T> performRequest(headers: Map<String, String>, clazz: Class<T>): T? {
        createRequest()
        apiRetryCount++

        return withContext(Dispatchers.IO) {
            var serviceResponse = ""
            try {
                serviceResponse = apiService.submitRequest(headers, "${request.header.service}${request.header.operation}.exp", request.toString())
                val response = handleBaseResponse(headers, clazz, StubHelper.createObjectMapper().readValue(serviceResponse, clazz))
                response
            } catch (throwable: Throwable) {
                Logger.e("express_error", throwable.message.toString())
                when {
                    throwable.message.toString().startsWith("Unexpected character") -> httpErrorLiveData.postValue(ApplicationError(this@BaseRepository.javaClass.simpleName, "Something went wrong", serviceResponse))
                    allowRetries && apiRetryCount < 3 -> return@withContext performRequest(headers, clazz)
                    else -> httpErrorLiveData.postValue(ApplicationError(this@BaseRepository.javaClass.simpleName, throwable))
                }
                null
            }
        }
    }

    suspend fun <T> handleBaseResponse(headers: Map<String, String>, clazz: Class<T>, response: T?): T? {
        (response as BaseResponse).let {
            setSessionInfo(it)

            if (it.header.statuscode == SURECHECK_RESPONSE_CODE) {
                handleSureCheckEvent()
                return null
            } else if (!validResponseCodes.contains(it.header.statuscode)) {
                if (POLLING_RESPONSE_CODE == it.header.statuscode && apiRetryCount < apiPollingMaxRetries) {
                    delay(apiRetryDelayMillis)
                    return performRequest(headers, clazz)
                } else {
                    handleFailureEvent(it.header)
                }
                return null
            }
        }
        return response
    }

    fun createXMMSService(): ApiService {
        serviceEndpoint = BuildConfig.XMMS_BASE_URL
        return RetrofitClientFactory.createRetrofitClient(serviceEndpoint)
    }

    fun createXTMSService(): ApiService {
        serviceEndpoint = BuildConfig.XTMS_BASE_URL
        return RetrofitClientFactory.createRetrofitClient(serviceEndpoint)
    }

    fun createXSMSService(): ApiService {
        serviceEndpoint = BuildConfig.XSMS_BASE_URL
        return RetrofitClientFactory.createRetrofitClient(serviceEndpoint)
    }

    fun createXSWPService(): ApiService {
        serviceEndpoint = BuildConfig.XSWP_BASE_URL
        return RetrofitClientFactory.createRetrofitClient(serviceEndpoint)
    }
}