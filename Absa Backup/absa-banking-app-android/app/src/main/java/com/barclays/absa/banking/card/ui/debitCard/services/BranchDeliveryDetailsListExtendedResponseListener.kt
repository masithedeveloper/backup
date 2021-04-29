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
package com.barclays.absa.banking.card.ui.debitCard.services

import com.barclays.absa.banking.boundary.model.BranchDeliveryDetailsList
import com.barclays.absa.banking.card.ui.debitCard.ui.DebitCardDetailsViewModel
import com.barclays.absa.banking.framework.ExtendedResponseListener

class BranchDeliveryDetailsListExtendedResponseListener(private val debitCardDetailsViewModel: DebitCardDetailsViewModel) : ExtendedResponseListener<BranchDeliveryDetailsList>() {
    override fun onSuccess(successResponse: BranchDeliveryDetailsList) {
        debitCardDetailsViewModel.branchDeliveryDetailsListExtendedResponse.value = successResponse
    }
}