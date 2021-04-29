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
package za.co.absa.networking.hmac.service

import androidx.lifecycle.MutableLiveData
import za.co.absa.networking.ExpressNetworkingConfig
import za.co.absa.networking.dto.BaseResponse
import za.co.absa.networking.dto.ResponseHeader

abstract class ExpressBaseRepository {

    abstract var failureHeaderLiveData: MutableLiveData<ResponseHeader>

    open var allowRetries: Boolean = true
    open var logFailureToNewRelic: Boolean = true
    open var apiPollingMaxRetries: Int = 10
    open var apiRetryDelayMillis: Long = 1000L

    var serviceEndpoint: String = ""
    var apiRetryCount = 0

    companion object {
        const val SUCCESS_RESPONSE_CODE = "0"
        const val RETRY_RESPONSE_CODE = "475"
        const val PENDING_RESPONSE_CODE = "480"
        const val POLLING_RESPONSE_CODE = "590"
        const val SURECHECK_RESPONSE_CODE = "102"
        const val AUTHORISATION_RESPONSE_CODE = "855"
    }

    val validResponseCodes = listOf(SUCCESS_RESPONSE_CODE, PENDING_RESPONSE_CODE, RETRY_RESPONSE_CODE)

    fun setSessionInfo(response: BaseResponse, url: String = serviceEndpoint) {
        if (response.header.jsessionid.isNotEmpty() || response.header.nonce.isNotEmpty()) {
            ExpressNetworkingConfig.sessionMap[url] = ExpressNetworkingConfig.SessionInfo().apply {
                jsessionid = response.header.jsessionid
                nonce = response.header.nonce
            }
        }
    }
}