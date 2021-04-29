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

package com.barclays.absa.banking.presentation.launch.versionCheck.dto

import com.barclays.absa.banking.framework.ExtendedResponseListener
import com.barclays.absa.banking.framework.app.BMBApplication
import com.barclays.absa.banking.framework.data.ResponseObject
import com.barclays.absa.banking.framework.utils.AppConstants
import za.co.absa.networking.error.ApplicationError
import za.co.absa.networking.error.ApplicationErrorType
import za.co.absa.networking.hmac.service.ApiService

class IValNValExtendedResponseListener : ExtendedResponseListener<IValNValResponse>() {
    override fun onRequestStarted() {}

    override fun onSuccess(successResponse: IValNValResponse) {
        BMBApplication.getInstance().iVal = successResponse.iVal
    }

    override fun onFailure(failureResponse: ResponseObject?) {
        if (AppConstants.SSLPinningError.equals(failureResponse?.errorMessage, ignoreCase = true)) {
            ApiService.httpErrorLiveData.value = ApplicationError(ApplicationErrorType.CERTIFICATE_PINNING, "IValNValRequest", "")
        } else {
            ApiService.httpErrorLiveData.value = ApplicationError(ApplicationErrorType.GENERAL, "IValNValRequest", failureResponse?.errorMessage ?: "")
        }
    }
}