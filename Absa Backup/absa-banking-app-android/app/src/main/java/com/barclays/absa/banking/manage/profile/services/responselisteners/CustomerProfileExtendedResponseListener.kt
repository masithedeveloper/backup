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
import com.barclays.absa.banking.manage.profile.services.dto.CustomerInformationResponse
import com.barclays.absa.banking.manage.profile.ui.ManageProfileViewModel

class CustomerProfileExtendedResponseListener(private var manageProfileViewModel: ManageProfileViewModel) : ExtendedResponseListener<CustomerInformationResponse>() {
    override fun onSuccess(successResponse: CustomerInformationResponse) {
        manageProfileViewModel.populateCustomerInformation(successResponse)
    }
}