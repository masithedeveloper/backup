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

package com.barclays.absa.banking.flexiFuneral.services.dto

import com.barclays.absa.banking.flexiFuneral.ui.FlexiFuneralViewModel
import com.barclays.absa.banking.framework.ExtendedResponseListener
import com.barclays.absa.banking.framework.app.BMBConstants

class MainMemberCoverAmountsExtendedResponseListener(private val flexiFuneralViewModel: FlexiFuneralViewModel) : ExtendedResponseListener<MainMemberCoverAmountsResponse>() {
    override fun onSuccess(successResponse: MainMemberCoverAmountsResponse) {
        if (BMBConstants.SUCCESS.equals(successResponse.transactionStatus, true)) {
            flexiFuneralViewModel.mainMemberCoverAmounts.value = successResponse
        } else {
            onFailure(successResponse)
        }
    }
}