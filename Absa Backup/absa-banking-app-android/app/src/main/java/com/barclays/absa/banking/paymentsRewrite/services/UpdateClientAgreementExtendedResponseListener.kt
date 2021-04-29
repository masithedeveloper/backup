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

package com.barclays.absa.banking.paymentsRewrite.services

import com.barclays.absa.banking.boundary.shared.dto.TransactionResponse
import com.barclays.absa.banking.framework.ExtendedResponseListener
import com.barclays.absa.banking.framework.app.BMBConstants
import com.barclays.absa.banking.framework.dagger.getServiceInterface
import com.barclays.absa.banking.paymentsRewrite.ui.PaymentsViewModel
import com.barclays.absa.utils.IAbsaCacheService

class UpdateClientAgreementExtendedResponseListener(private val viewModel: PaymentsViewModel) : ExtendedResponseListener<TransactionResponse>() {
    private val absaCacheService: IAbsaCacheService = getServiceInterface()

    override fun onSuccess(successResponse: TransactionResponse) {
        if (BMBConstants.SUCCESS.equals(successResponse.transactionStatus, ignoreCase = true)) {
            absaCacheService.setPersonalClientAgreementAccepted(true)
            viewModel.updatedClientAgreementLiveData.value = true
        } else {
            viewModel.updatedClientAgreementLiveData.value = false
        }
    }
}