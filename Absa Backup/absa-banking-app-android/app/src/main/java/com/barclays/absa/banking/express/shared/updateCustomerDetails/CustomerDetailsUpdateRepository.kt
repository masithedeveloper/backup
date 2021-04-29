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

package com.barclays.absa.banking.express.shared.updateCustomerDetails

import com.barclays.absa.banking.express.Repository
import com.barclays.absa.banking.express.shared.updateCustomerDetails.dto.CustomerDetailsUpdateRequest
import com.barclays.absa.banking.express.shared.updateCustomerDetails.dto.CustomerDetailsUpdateResponse
import za.co.absa.networking.hmac.service.BaseRequest

class CustomerDetailsUpdateRepository : Repository() {
    private lateinit var customerDetailsUpdateRequest: CustomerDetailsUpdateRequest

    override val apiService = createXSMSService()
    override val service = "CustomerInformationFacade"
    override val operation = "UpdateCustomerDetailsInfo"
    override var mockResponseFile: String = "express/shared/update_customer_details.json"

    override fun buildRequest(baseRequest: BaseRequest.Builder): BaseRequest =
        baseRequest.addObjectParameter("customerInfoInputVO", customerDetailsUpdateRequest).build()

    suspend fun updateCustomerDetails(customerDetailsUpdateRequest: CustomerDetailsUpdateRequest): CustomerDetailsUpdateResponse? {
        this.customerDetailsUpdateRequest = customerDetailsUpdateRequest
        return submitRequest()
    }
}