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
package com.barclays.absa.banking.overdraft.services.dto

import com.barclays.absa.banking.framework.ExtendedRequest
import com.barclays.absa.banking.framework.ExtendedResponseListener
import com.barclays.absa.banking.framework.api.request.params.RequestParams
import com.barclays.absa.banking.overdraft.services.OverdraftMockFactory
import com.barclays.absa.banking.overdraft.services.OverdraftService
import com.barclays.absa.banking.riskBasedApproach.services.dto.PersonalInformationResponse

class PersonalInformationRequest<T>(marketingResponseListener: ExtendedResponseListener<T>)
    : ExtendedRequest<T>(marketingResponseListener) {

    init {
        params = RequestParams.Builder().put(OverdraftService.OP2012_PERSONAL_INFORMATION).build()
        mockResponseFile = OverdraftMockFactory.personalInformationDetails()
        printRequest()
    }

    @Suppress("UNCHECKED_CAST")
    override fun getResponseClass(): Class<T> = PersonalInformationResponse::class.java as Class<T>
    override fun isEncrypted(): Boolean? = true
}