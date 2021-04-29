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
package com.barclays.absa.banking.express

import androidx.lifecycle.MutableLiveData
import com.barclays.absa.banking.boundary.monitoring.MonitoringInteractor
import com.barclays.absa.banking.framework.BaseActivity
import com.barclays.absa.banking.framework.app.BMBApplication
import kotlinx.coroutines.delay
import za.co.absa.networking.ExpressHttpClient
import za.co.absa.networking.HttpFormRequest
import za.co.absa.networking.dto.BaseResponse
import za.co.absa.networking.dto.ResponseHeader
import za.co.absa.networking.hmac.service.ExpressBaseRepository

open class ExpressBaseHttpFormRepository : ExpressBaseRepository() {

    override var failureHeaderLiveData: MutableLiveData<ResponseHeader> = MutableLiveData()

    lateinit var httpFormRequest: HttpFormRequest
    lateinit var httpClient: ExpressHttpClient

    suspend inline fun <reified T> handleResponse(response: BaseResponse?): T? {
        response?.let {
            setSessionInfo(it, httpClient.url)

            return if (!validResponseCodes.contains(it.header.statuscode)) {
                if (POLLING_RESPONSE_CODE == it.header.statuscode && apiRetryCount < apiPollingMaxRetries) {
                    delay(apiRetryDelayMillis)
                    return httpClient.performRequest(httpFormRequest, T::class.java)
                } else {
                    handleFailureEvent(it.header)
                    null
                }
            } else {
                it as T
            }
        }
        return null
    }

    private fun logFailureEvent(header: ResponseHeader) {
        if (logFailureToNewRelic) {
            val location = (BMBApplication.getInstance().topMostActivity as? BaseActivity)?.javaClass?.simpleName ?: ""
            MonitoringInteractor().logExpressServiceErrorEvent(location, this.javaClass.simpleName, header.resultMessages.first().responseMessage)
        }
    }

    fun handleFailureEvent(header: ResponseHeader) {
        logFailureEvent(header)
        if (header.statuscode == Repository.USER_SESSION_LOGGED_OFF_STATUS_CODE) {
            BMBApplication.getInstance().forceSignOut()
        } else {
            failureHeaderLiveData.postValue(header)
        }
    }
}