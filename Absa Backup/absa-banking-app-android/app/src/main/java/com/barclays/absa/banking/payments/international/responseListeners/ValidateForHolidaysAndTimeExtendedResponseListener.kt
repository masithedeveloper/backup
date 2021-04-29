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

package com.barclays.absa.banking.payments.international.responseListeners

import com.barclays.absa.banking.framework.ExtendedResponseListener
import com.barclays.absa.banking.payments.international.InternationalPaymentsPaymentHubPresenter
import com.barclays.absa.banking.payments.international.services.dto.ValidateForHolidaysAndTimeResponse

class ValidateForHolidaysAndTimeExtendedResponseListener(private val presenter: InternationalPaymentsPaymentHubPresenter) : ExtendedResponseListener<ValidateForHolidaysAndTimeResponse?>() {
    override fun onSuccess(successResponse: ValidateForHolidaysAndTimeResponse?) {
        if (successResponse != null) {
            presenter.holidaysAndTimeValidationResponse(successResponse)
        } else {
            presenter.showGenericErrorMessage(this.javaClass.simpleName)
        }
    }
}