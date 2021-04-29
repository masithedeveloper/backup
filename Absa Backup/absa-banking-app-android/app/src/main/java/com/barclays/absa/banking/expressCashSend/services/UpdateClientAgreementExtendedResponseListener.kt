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
 *
 */
package com.barclays.absa.banking.expressCashSend.services

import com.barclays.absa.banking.boundary.shared.dto.TransactionResponse
import com.barclays.absa.banking.expressCashSend.ui.CashSendViewModel
import com.barclays.absa.banking.framework.ExtendedResponseListener

class UpdateClientAgreementExtendedResponseListener(private var viewModel: CashSendViewModel) : ExtendedResponseListener<TransactionResponse>() {
    override fun onSuccess(successResponse: TransactionResponse) {
        viewModel.updateClientAgreementDetailsLiveData.value = successResponse
    }
}