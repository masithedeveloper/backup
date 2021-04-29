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
 */

package com.barclays.absa.banking.express.identificationAndVerification

import com.barclays.absa.banking.express.Repository
import com.barclays.absa.banking.express.identificationAndVerification.dto.BiometricStatusResponse
import com.barclays.absa.banking.linking.ui.LinkingIdType
import za.co.absa.networking.hmac.service.BaseRequest

class GetBiometricStatusRepository : Repository() {

    private lateinit var idType: String
    private lateinit var idNumber: String

    override val apiService = createXSWPService()

    override val service = "FaceBioFacade"
    override val operation = "GetBiometricStatus"

    override var mockResponseFile = "express/identificationAndVerification/get_biometric_status.json"

    suspend fun fetchBiometricStatus(idType: LinkingIdType, idNumber: String): BiometricStatusResponse? {
        this.idType = idType.code
        this.idNumber = idNumber
        return submitRequest()
    }

    override fun buildRequest(baseRequest: BaseRequest.Builder) = baseRequest
            .addParameter("idType", idType)
            .addParameter("idNumber", idNumber)
            .build()
}