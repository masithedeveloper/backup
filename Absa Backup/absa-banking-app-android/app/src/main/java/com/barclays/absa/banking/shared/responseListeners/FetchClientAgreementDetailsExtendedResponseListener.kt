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

package com.barclays.absa.banking.shared.responseListeners

import com.barclays.absa.banking.boundary.model.ClientAgreementDetails
import com.barclays.absa.banking.framework.ExtendedResponseListener
import com.barclays.absa.banking.framework.app.BMBConstants
import com.barclays.absa.banking.framework.dagger.getServiceInterface
import com.barclays.absa.banking.presentation.shared.BaseViewModel
import com.barclays.absa.utils.IAbsaCacheService

class FetchClientAgreementDetailsExtendedResponseListener(private val viewModel: BaseViewModel) : ExtendedResponseListener<ClientAgreementDetails>() {

    private val absaCacheService: IAbsaCacheService = getServiceInterface()

    override fun onSuccess(successResponse: ClientAgreementDetails) {
        if (!BMBConstants.FAILURE.equals(successResponse.transactionStatus, true)) {
            if (!successResponse.clientAgreementAccepted.isNullOrEmpty()) {
                absaCacheService.setPersonalClientAgreementAccepted(successResponse.clientAgreementAccepted.equals("Y"))
            }
            viewModel.successResponse.value = successResponse
        } else {
            viewModel.failureResponse.value = successResponse
        }
    }
}