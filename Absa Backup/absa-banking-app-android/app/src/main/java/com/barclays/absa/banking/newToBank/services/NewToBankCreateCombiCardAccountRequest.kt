/*
 * Copyright (c) 2019 Absa Bank Limited, All Rights Reserved.
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
package com.barclays.absa.banking.newToBank.services

import com.barclays.absa.banking.framework.ExtendedRequest
import com.barclays.absa.banking.framework.ExtendedResponseListener
import com.barclays.absa.banking.framework.api.request.params.NewToBankParams
import com.barclays.absa.banking.framework.api.request.params.RequestParams
import com.barclays.absa.banking.framework.api.request.params.TransactionParams
import com.barclays.absa.banking.newToBank.services.dto.CreateCombiCardAccountResponse
import com.barclays.absa.banking.newToBank.services.dto.CreateCombiDetails

import com.barclays.absa.banking.newToBank.services.NewToBankService.Companion.OP2041_CREATE_COMBI_CARD_ACCOUNT

class NewToBankCreateCombiCardAccountRequest<T>(createCombiDetails: CreateCombiDetails, responseListener: ExtendedResponseListener<T>)
    : ExtendedRequest<T>(responseListener) {

    init {
        params = RequestParams.Builder()
                .put(OP2041_CREATE_COMBI_CARD_ACCOUNT)
                .put(TransactionParams.Transaction.SERVICE_CHANNEL_IND, "S")
                .put(NewToBankParams.PERSONALISED_CARD.key, createCombiDetails.isPersonalised.toString())
                .put(NewToBankParams.DELIVERY_METHOD.key, createCombiDetails.deliveryMethod)
                .put(NewToBankParams.DELIVERY_BRANCH.key, createCombiDetails.deliveryBranch)
                .put(NewToBankParams.IN_BRANCH_INDICATOR.key, createCombiDetails.inBranchIndicator.toString())
                .put(NewToBankParams.IN_BRANCH_NAME.key, createCombiDetails.inBranchName)
                .put(NewToBankParams.IN_BRANCH_SITE.key, createCombiDetails.inBranchSite)
                .put(NewToBankParams.TOKEN_NUMBER.key, createCombiDetails.tokenNumber)
                .build()

        mockResponseFile = "new_to_bank/op2041_create_combi_card.json"
        printRequest()
    }

    @Suppress("UNCHECKED_CAST")
    override fun getResponseClass(): Class<T> = CreateCombiCardAccountResponse::class.java as Class<T>
    override fun isEncrypted(): Boolean? = true
}
