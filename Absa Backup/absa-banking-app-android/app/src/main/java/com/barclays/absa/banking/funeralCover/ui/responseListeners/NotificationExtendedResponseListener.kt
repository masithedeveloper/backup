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

package com.barclays.absa.banking.funeralCover.ui.responseListeners

import com.barclays.absa.banking.boundary.model.notification.ClaimNotification
import com.barclays.absa.banking.framework.ExtendedResponseListener
import com.barclays.absa.banking.funeralCover.ui.InsurancePolicyClaimViewModel

class NotificationExtendedResponseListener internal constructor(private val insurancePolicyClaimViewModel: InsurancePolicyClaimViewModel) : ExtendedResponseListener<ClaimNotification>() {
    override fun onSuccess(successResponse: ClaimNotification) {
        insurancePolicyClaimViewModel.claimNotificationLiveData.value = successResponse
    }
}
