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

package com.barclays.absa.banking.lotto.services

import com.barclays.absa.banking.boundary.shared.dto.TransactionResponse
import com.barclays.absa.banking.framework.ExtendedResponseListener
import com.barclays.absa.banking.framework.data.ResponseObject
import com.barclays.absa.banking.lotto.ui.LottoViewModel

class LottoAcceptTermsResponseListener(val viewModel: LottoViewModel) : ExtendedResponseListener<TransactionResponse>() {

    override fun onSuccess(successResponse: TransactionResponse) {
        viewModel.termsAcceptedLiveData.value = true
    }

    override fun onFailure(failureResponse: ResponseObject) {
        viewModel.termsAcceptedLiveData.value = false
    }
}