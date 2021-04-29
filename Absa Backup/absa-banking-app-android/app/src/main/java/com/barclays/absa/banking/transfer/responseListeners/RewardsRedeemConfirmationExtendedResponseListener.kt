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

package com.barclays.absa.banking.transfer.responseListeners

import com.barclays.absa.banking.boundary.model.rewards.RewardsRedeemConfirmation
import com.barclays.absa.banking.boundary.shared.dto.TransactionResponse
import com.barclays.absa.banking.framework.ExtendedResponseListener
import com.barclays.absa.banking.framework.data.ResponseObject
import com.barclays.absa.banking.transfer.TransferViewModel

class RewardsRedeemConfirmationExtendedResponseListener(private var viewModel: TransferViewModel) : ExtendedResponseListener<RewardsRedeemConfirmation>() {
    override fun onRequestStarted() {

    }

    override fun onSuccess(successResponse: RewardsRedeemConfirmation) {
        viewModel.rewardsRedeemConfirmationLiveData.value = successResponse
    }

    override fun onFailure(failureResponse: ResponseObject?) {
        viewModel.failureResponse.value = failureResponse as TransactionResponse
    }
}