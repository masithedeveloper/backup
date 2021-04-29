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

import com.barclays.absa.banking.debiCheck.services.dto.DebiCheckSuspendResponse
import com.barclays.absa.banking.debiCheck.ui.DebiCheckViewModel
import com.barclays.absa.banking.framework.ExtendedResponseListener
import com.barclays.absa.banking.framework.data.ResponseObject

class DebiCheckSuspendMandateExtendedResponseListener(private var viewModel: DebiCheckViewModel) : ExtendedResponseListener<DebiCheckSuspendResponse>() {
    override fun onSuccess(successResponse: DebiCheckSuspendResponse) {
        viewModel.debiCheckSuspendResponse.value = successResponse
    }

    override fun onNetworkUnreachable(failureResponse: ResponseObject) {
        viewModel.networkUnreachableLiveData.value = failureResponse
    }
}