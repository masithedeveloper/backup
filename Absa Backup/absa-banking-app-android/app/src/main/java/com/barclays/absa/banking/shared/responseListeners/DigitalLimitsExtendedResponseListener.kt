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

import com.barclays.absa.banking.boundary.model.limits.DigitalLimit
import com.barclays.absa.banking.framework.ExtendedResponseListener
import com.barclays.absa.banking.framework.app.BMBConstants
import com.barclays.absa.banking.shared.services.DigitalLimitsViewModel

class DigitalLimitsExtendedResponseListener(var viewModel: DigitalLimitsViewModel) : ExtendedResponseListener<DigitalLimit?>() {
    override fun onSuccess(successResponse: DigitalLimit?) {
        if (!BMBConstants.FAILURE.equals(successResponse?.transactionStatus, true)) {
            viewModel.digitalLimit.value = successResponse
        } else {
            viewModel.failureResponse.value = successResponse
        }
    }
}