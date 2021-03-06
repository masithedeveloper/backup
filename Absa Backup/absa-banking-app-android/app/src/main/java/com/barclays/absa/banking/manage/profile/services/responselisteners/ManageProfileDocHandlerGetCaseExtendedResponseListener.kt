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
 */

package com.barclays.absa.banking.manage.profile.services.responselisteners

import com.barclays.absa.banking.boundary.docHandler.DocHandlerGetCaseResponseListener
import com.barclays.absa.banking.boundary.docHandler.dto.DocHandlerGetCaseByIdResponse
import com.barclays.absa.banking.manage.profile.ui.ManageProfileViewModel

class ManageProfileDocHandlerGetCaseExtendedResponseListener(val manageProfileViewModel: ManageProfileViewModel) : DocHandlerGetCaseResponseListener() {
    override fun onGetCaseSuccess(docHandlerGetCaseByIdResponse: DocHandlerGetCaseByIdResponse) {
        manageProfileViewModel.docHandlerResponse(docHandlerGetCaseByIdResponse)
    }

    override fun onGetCaseFailure(failure: String) {
        manageProfileViewModel.docHandlerFetchCaseFailure.value = failure
    }
}