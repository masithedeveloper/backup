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
package com.barclays.absa.banking.express.payments.absaListedBeneficiaries

import com.barclays.absa.banking.express.Repository
import com.barclays.absa.banking.express.payments.absaListedBeneficiaries.dto.PaymentsAbsaListedBeneficiariesResponse
import za.co.absa.networking.hmac.service.BaseRequest

class PaymentsAbsaListedBeneficiariesRepository : Repository() {

    private lateinit var searchString: String

    override val apiService = createXTMSService()
    override val service = "InstitutionalBeneficiariesEnquiryFacade"
    override val operation = "FetchInstitutionalBeneficiaries"

    override var mockResponseFile = "express/payments/payments_absa_listed_beneficiaries_response.json"

    suspend fun fetchAbsaListedBeneficiaries(searchString: String = ""): PaymentsAbsaListedBeneficiariesResponse? {
        this.searchString = searchString
        return submitRequest()
    }

    override fun buildRequest(baseRequest: BaseRequest.Builder) = baseRequest
            .enablePagination()
            .addParameter("searchString", searchString).build()
}