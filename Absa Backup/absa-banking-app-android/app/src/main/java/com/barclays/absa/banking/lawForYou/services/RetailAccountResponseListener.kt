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
package com.barclays.absa.banking.lawForYou.services

import com.barclays.absa.banking.framework.ExtendedResponseListener
import com.barclays.absa.banking.funeralCover.services.dto.RetailAccountsResponse
import com.barclays.absa.banking.lawForYou.ui.LawForYouPolicyDetailsViewModel

class RetailAccountResponseListener(private val viewModel: LawForYouPolicyDetailsViewModel) : ExtendedResponseListener<RetailAccountsResponse>() {
    override fun onSuccess(successResponse: RetailAccountsResponse) {
        viewModel.retailAccountListMutableLiveData.value = successResponse.retailAccountsList?.filter {
            retailAccount -> LawForYouPolicyDetailsViewModel.isValidLawForYouAccount(retailAccount)
        }
    }
}