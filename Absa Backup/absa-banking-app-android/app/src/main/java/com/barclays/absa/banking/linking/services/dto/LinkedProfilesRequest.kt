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
package com.barclays.absa.banking.linking.services.dto

import com.barclays.absa.banking.framework.ExtendedRequest
import com.barclays.absa.banking.framework.ExtendedResponseListener
import com.barclays.absa.banking.framework.api.request.params.RequestParams
import com.barclays.absa.banking.framework.dagger.getServiceInterface
import com.barclays.absa.banking.framework.services.contracts.IAppCacheService
import com.barclays.absa.banking.linking.services.LinkingService
import com.barclays.absa.crypto.SecureUtils

class LinkedProfilesRequest<T>(requestDetails: LinkedProfilesRequestDetails, responseListener: ExtendedResponseListener<T>) : ExtendedRequest<T>(responseListener) {
    private val appCacheService: IAppCacheService = getServiceInterface()

    init {
        params = RequestParams.Builder(LinkingService.OP2206_FETCH_LINKED_PROFILES_BY_ID_NUMBER)
                .put("idNumber", requestDetails.idNumber)
                .put("idType", requestDetails.idType)
                .put("deviceId", SecureUtils.getDeviceID())
                .put("imei", SecureUtils.getDeviceID())
                .put("customerSessionId", appCacheService.getCustomerSessionId())
                .build()

        mockResponseFile = "linking/op2206_linked_profiles.json"

        printRequest()
    }

    @Suppress("UNCHECKED_CAST")
    override fun getResponseClass() = LinkedProfilesResponse::class.java as Class<T>

    override fun isEncrypted() = true
}

data class LinkedProfilesRequestDetails(var idNumber: String, var idType: String)