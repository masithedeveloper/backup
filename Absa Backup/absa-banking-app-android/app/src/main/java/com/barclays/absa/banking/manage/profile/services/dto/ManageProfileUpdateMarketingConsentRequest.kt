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

package com.barclays.absa.banking.manage.profile.services.dto

import com.barclays.absa.banking.framework.ExtendedRequest
import com.barclays.absa.banking.framework.ExtendedResponseListener
import com.barclays.absa.banking.framework.api.request.params.RequestParams
import com.barclays.absa.banking.manage.profile.services.ManageProfileService.ManageProfileServiceParameters.OP2107_UPDATE_PROFILE
import com.barclays.absa.banking.manage.profile.ui.ManageProfileConstants

class ManageProfileUpdateMarketingConsentRequest<T>(marketingIndicator: MarketingIndicator, extendedResponseListener: ExtendedResponseListener<T>) : ExtendedRequest<T>(extendedResponseListener) {
    init {
        params = RequestParams.Builder(OP2107_UPDATE_PROFILE)
                .put("action", "UPDATE_MARKETING_CONSENT")
                .put("clientType", marketingIndicator.clientType)
                .put("nonCreditIndicator", marketingIndicator.nonCreditIndicator)
                .put("nonCreditSmsIndicator", marketingIndicator.nonCreditSmsIndicator)
                .put("nonCreditEmailIndicator", marketingIndicator.nonCreditEmailIndicator)
                .put("nonCreditAutoVoiceIndicator", marketingIndicator.nonCreditAutoVoiceIndicator)
                .put("nonCreditTeleIndicator", ManageProfileConstants.YES)
                .put("nonCreditPostIndicator", ManageProfileConstants.YES)
                .put("creditIndicator", marketingIndicator.creditIndicator)
                .put("creditSmsIndicator", marketingIndicator.creditSmsIndicator)
                .put("creditEmailIndicator", marketingIndicator.creditEmailIndicator)
                .put("creditTeleIndicator", marketingIndicator.creditTeleIndicator)
                .put("creditPostIndicator", ManageProfileConstants.YES)
                .build()

        mockResponseFile = "shared/op2107_success_response.json"
        printRequest()
    }

    @Suppress("UNCHECKED_CAST")
    override fun getResponseClass() = UpdatedFields::class.java as Class<T>

    override fun isEncrypted() = true
}