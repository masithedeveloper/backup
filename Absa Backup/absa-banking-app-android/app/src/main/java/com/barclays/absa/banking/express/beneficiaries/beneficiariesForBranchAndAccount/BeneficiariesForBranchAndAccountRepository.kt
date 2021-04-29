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

package com.barclays.absa.banking.express.beneficiaries.beneficiariesForBranchAndAccount

import com.barclays.absa.banking.express.Repository
import com.barclays.absa.banking.express.beneficiaries.beneficiariesForBranchAndAccount.dto.BeneficiariesForBranchAndAccountResponse
import za.co.absa.networking.hmac.service.BaseRequest

class BeneficiariesForBranchAndAccountRepository : Repository() {

    private lateinit var branchCode: String
    private lateinit var accountNumber: String

    override val apiService = createXTMSService()
    override val service = "RegularBeneficiaryManagementFacade"
    override val operation = "GetBeneficiariesForBranchAndAccount"

    override var mockResponseFile = "express/beneficiaries/beneficiaries_for_branch_and_account_response.json"

    suspend fun fetchBeneficiariesForBranchAndAccount(branchCode: String, accountNumber: String): BeneficiariesForBranchAndAccountResponse? {
        this.branchCode = branchCode
        this.accountNumber = accountNumber
        return submitRequest()
    }

    override fun buildRequest(baseRequest: BaseRequest.Builder) = baseRequest
            .enablePagination()
            .addParameter("branchCode", branchCode)
            .addParameter("accountNumber", accountNumber)
            .build()
}