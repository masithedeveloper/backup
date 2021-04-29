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
package com.barclays.absa.banking.directMarketing.services.dto

import com.barclays.absa.banking.directMarketing.services.DirectMarketingMockFactory
import com.barclays.absa.banking.directMarketing.services.DirectMarketingService
import com.barclays.absa.banking.directMarketing.services.DirectMarketingService.Companion.OP2090_UPDATE_MARKETING_INDICATOR
import com.barclays.absa.banking.framework.ExtendedRequest
import com.barclays.absa.banking.framework.ExtendedResponseListener
import com.barclays.absa.banking.framework.api.request.params.RequestParams

class UpdateMarketingIndicatorRequest<T>(marketingIndicators: MarketingIndicators, responseListener: ExtendedResponseListener<T>) : ExtendedRequest<T>(responseListener) {

    init {
        params = RequestParams.Builder()
                .put(OP2090_UPDATE_MARKETING_INDICATOR)
                .put(DirectMarketingService.NON_CREDIT_INDICATOR, marketingIndicators.nonCreditIndicator)
                .put(DirectMarketingService.NON_CREDIT_SMS_INDICATOR, marketingIndicators.nonCreditSmsIndicator)
                .put(DirectMarketingService.NON_CREDIT_EMAIL_INDICATOR, marketingIndicators.nonCreditEmailIndicator)
                .put(DirectMarketingService.NON_CREDIT_AUTO_VOICE_INDICATOR, marketingIndicators.nonCreditAutoVoiceIndicator)
                .put(DirectMarketingService.NON_CREDIT_TELE_INDICATOR, "Y")
                .put(DirectMarketingService.NON_CREDIT_POST_INDICATOR, "Y")
                .put(DirectMarketingService.CREDIT_INDICATOR, marketingIndicators.creditIndicator)
                .put(DirectMarketingService.CREDIT_SMS_INDICATOR, marketingIndicators.creditSmsIndicator)
                .put(DirectMarketingService.CREDIT_EMAIL_INDICATOR, marketingIndicators.creditEmailIndicator)
                .put(DirectMarketingService.CREDIT_AUTO_VOICE_INDICATOR, marketingIndicators.creditAutoVoiceIndicator)
                .put(DirectMarketingService.CREDIT_TELE_INDICATOR, marketingIndicators.creditTeleIndicator)
                .put(DirectMarketingService.CREDIT_POST_INDICATOR, marketingIndicators.creditPostIndicator)
                .build()

        mockResponseFile = DirectMarketingMockFactory.updatedDirectMarketingIndicatorsResponse()
        printRequest()
    }

    @Suppress("UNCHECKED_CAST")
    override fun getResponseClass(): Class<T> = UpdateMarketingIndicatorResponse::class.java as Class<T>
    override fun isEncrypted(): Boolean? = true
}