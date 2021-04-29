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

package com.barclays.absa.banking.manage.profile.services.responselisteners

import com.barclays.absa.banking.framework.ExtendedResponseListener
import com.barclays.absa.banking.framework.app.BMBConstants.FAILURE
import com.barclays.absa.banking.framework.data.ResponseObject
import com.barclays.absa.banking.manage.profile.ui.ManageProfileViewModel
import com.barclays.absa.banking.shared.services.dto.LookupResult

class SourceOfIncomeExtendedResponseListener(private var viewModel: ManageProfileViewModel) : ExtendedResponseListener<LookupResult>() {
    override fun onSuccess(successResponse: LookupResult) {
        if (FAILURE.equals(successResponse.transactionStatus, ignoreCase = true)) {
            viewModel.lookupFailure.value = successResponse.transactionMessage
        } else {
            viewModel.sourceOfIncomeLookupResult.value = successResponse
        }
    }

    override fun onFailure(failureResponse: ResponseObject?) {
        viewModel.lookupFailure.value = failureResponse?.errorMessage
    }
}