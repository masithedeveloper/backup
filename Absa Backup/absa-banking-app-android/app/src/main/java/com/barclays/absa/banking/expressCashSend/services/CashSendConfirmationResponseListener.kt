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
package com.barclays.absa.banking.expressCashSend.services

import com.barclays.absa.banking.boundary.model.cashSend.CashSendBeneficiaryConfirmation
import com.barclays.absa.banking.expressCashSend.ui.CashSendViewModel
import com.barclays.absa.banking.framework.ExtendedResponseListener

class CashSendConfirmationResponseListener(private val cashSendViewModel: CashSendViewModel) : ExtendedResponseListener<CashSendBeneficiaryConfirmation>() {
    override fun onRequestStarted() {}

    override fun onSuccess(sendBenCashSendConfirmation: CashSendBeneficiaryConfirmation) {
        cashSendViewModel.cashSendConfirmationLiveData.value = sendBenCashSendConfirmation
    }
}