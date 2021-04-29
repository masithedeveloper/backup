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

package com.barclays.absa.banking.express.invest.saveExistingCustomerDetails

import com.barclays.absa.banking.express.Repository
import com.barclays.absa.banking.express.invest.saveExistingCustomerDetails.dto.CustomerDetailsSaveRequest
import com.barclays.absa.banking.express.invest.saveExistingCustomerDetails.dto.CustomerDetailsSaveResponse
import za.co.absa.networking.hmac.service.BaseRequest

class CustomerDetailsSaveRepository : Repository() {
    private lateinit var customerDetailsSaveRequest: CustomerDetailsSaveRequest

    override val apiService = createXSMSService()
    override val service = "SaveAndInvestCaseManagementFacade"
    override val operation = "SaveExistingCustomerDetailsInfo"
    override var mockResponseFile: String = "express/invest/save_existing_customer_details.json"

    override fun buildRequest(baseRequest: BaseRequest.Builder): BaseRequest =
            with(customerDetailsSaveRequest) {
                baseRequest.addParameter("regRefreshNewAddressAdded", regRefreshNewAddressAdded)
                        .addParameter("regRefreshAddressSelected", regRefreshAddressSelected)
                        .addObjectParameter("personalDetails", personalDetails)
                        .addObjectParameter("createAccountDetails", accountCreationDetails)
                        .addObjectParameter("contactInfo", contactDetails)
                        .addObjectParameter("addressDetails", addressDetails).build()
            }

    suspend fun saveCustomerDetails(customerDetailsSaveRequest: CustomerDetailsSaveRequest): CustomerDetailsSaveResponse? {
        this.customerDetailsSaveRequest = customerDetailsSaveRequest
        return submitRequest()
    }
}