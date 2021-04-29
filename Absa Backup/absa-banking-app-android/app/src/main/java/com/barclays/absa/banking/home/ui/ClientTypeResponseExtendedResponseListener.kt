/*
 * Copyright (c) 2018 Absa Bank Limited, All Rights Reserved.
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

package com.barclays.absa.banking.home.ui

import com.barclays.absa.banking.boundary.shared.dto.TransactionResponse
import com.barclays.absa.banking.framework.ExtendedResponseListener
import com.barclays.absa.banking.framework.dagger.getServiceInterface
import com.barclays.absa.banking.framework.data.ResponseObject
import com.barclays.absa.banking.payments.international.services.IInternationalPaymentCacheService
import com.barclays.absa.banking.payments.international.services.dto.ClientTypeResponse

class ClientTypeResponseExtendedResponseListener(var viewModel: MenuViewModel) : ExtendedResponseListener<ClientTypeResponse>() {

    private val internationalPaymentCacheService: IInternationalPaymentCacheService = getServiceInterface()

    override fun onSuccess(successResponse: ClientTypeResponse) {
        internationalPaymentCacheService.setClientTypeResponse(successResponse)
        viewModel.isInternationalPaymentsOptionVisible.value = successResponse
    }

    override fun onFailure(failureResponse: ResponseObject?) {
        viewModel.failureResponse.value = failureResponse as TransactionResponse?
    }
}