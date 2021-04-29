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

package com.barclays.absa.banking.debiCheck.responseListeners

import com.barclays.absa.banking.debiCheck.services.dto.DebitOrderAuthoriseResponse
import com.barclays.absa.banking.debiCheck.ui.DebiCheckViewModel
import com.barclays.absa.banking.framework.ExtendedResponseListener
import com.barclays.absa.banking.framework.app.BMBConstants
import com.barclays.absa.banking.framework.data.ResponseObject

class DebitCheckOrderRejectionExtendedResponseListener(private val viewModel: DebiCheckViewModel) : ExtendedResponseListener<DebitOrderAuthoriseResponse>() {
    override fun onSuccess(successResponse: DebitOrderAuthoriseResponse) {
        if (BMBConstants.SUCCESS.equals(successResponse.transactionStatus, true)) {
            viewModel.debitOrderRejectResponse.value = successResponse
        } else {
            viewModel.notifyFailure(successResponse)
        }
    }

    override fun onNetworkUnreachable(failureResponse: ResponseObject) {
        viewModel.networkUnreachableLiveData.value = failureResponse
    }
}