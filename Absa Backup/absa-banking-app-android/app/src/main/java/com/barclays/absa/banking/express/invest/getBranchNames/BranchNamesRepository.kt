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

package com.barclays.absa.banking.express.invest.getBranchNames

import com.barclays.absa.banking.express.Repository
import com.barclays.absa.banking.express.invest.getBranchNames.dto.BranchNameRequest
import com.barclays.absa.banking.express.invest.getBranchNames.dto.BranchNameResponse
import za.co.absa.networking.hmac.service.BaseRequest

class BranchNamesRepository : Repository() {
    private lateinit var branchNameRequest: BranchNameRequest

    override val apiService = createXSMSService()
    override val service = "BranchCodeFacade"
    override val operation = "GetBranchNamesAndCodesDetails"
    override var mockResponseFile: String = "express/invest/get_branch_names_and_codes.json"

    override fun buildRequest(baseRequest: BaseRequest.Builder): BaseRequest = with(branchNameRequest) {
        baseRequest.addParameter("branchNameSearchString", branchSearchKeyword)
            .addParameter("bankName", bankName).build()
    }

    suspend fun fetchBranchNames(branchNameRequest: BranchNameRequest): BranchNameResponse? {
        this.branchNameRequest = branchNameRequest
        return submitRequest()
    }
}