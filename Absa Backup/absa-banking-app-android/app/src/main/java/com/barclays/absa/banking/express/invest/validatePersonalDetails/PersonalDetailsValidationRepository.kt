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

package com.barclays.absa.banking.express.invest.validatePersonalDetails

import com.barclays.absa.banking.express.Repository
import com.barclays.absa.banking.express.invest.validatePersonalDetails.dto.PersonalDetailsValidationRequest
import com.barclays.absa.banking.express.invest.validatePersonalDetails.dto.PersonalDetailsValidationResponse
import za.co.absa.networking.hmac.service.BaseRequest

class PersonalDetailsValidationRepository : Repository() {
    private lateinit var personalDetailsValidationRequest: PersonalDetailsValidationRequest

    override val apiService = createXSMSService()
    override val service = "CustomerDataValidationFacade"
    override val operation = "ValidatePersonalInformationDetails"
    override var mockResponseFile: String = "express/invest/validate_personal_details.json"

    override fun buildRequest(baseRequest: BaseRequest.Builder): BaseRequest =
            with(personalDetailsValidationRequest) {
                baseRequest.addObjectParameter("cvsValidationInputVO", validationInfo)
                        .addParameter("applicationType", applicationType)
                        .build()
            }

    suspend fun validatePersonalDetails(personalDetailsValidationRequest: PersonalDetailsValidationRequest): PersonalDetailsValidationResponse? {
        this.personalDetailsValidationRequest = personalDetailsValidationRequest
        return submitRequest()
    }
}