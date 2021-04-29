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

package com.barclays.absa.banking.directMarketing.services

import com.barclays.absa.banking.directMarketing.services.dto.MarketingIndicatorResponse
import com.barclays.absa.banking.directMarketing.services.dto.MarketingIndicators
import com.barclays.absa.banking.directMarketing.services.dto.UpdateMarketingIndicatorResponse
import com.barclays.absa.banking.framework.ExtendedResponseListener

interface DirectMarketingService {
    companion object {
        const val OP2089_GET_MARKETING_INDICATOR = "OP2089"
        const val OP2090_UPDATE_MARKETING_INDICATOR = "OP2090"

        const val PRODUCT_CODE = "productCode"
        const val SOURCE_OF_FUNDS = "sourceOfFunds"

        const val NON_CREDIT_AUTO_VOICE_INDICATOR = "nonCreditAutoVoiceIndicator"
        const val NON_CREDIT_EMAIL_INDICATOR = "nonCreditEmailIndicator"
        const val NON_CREDIT_INDICATOR = "nonCreditIndicator"
        const val NON_CREDIT_POST_INDICATOR = "nonCreditPostIndicator"
        const val NON_CREDIT_SMS_INDICATOR = "nonCreditSmsIndicator"
        const val NON_CREDIT_TELE_INDICATOR = "nonCreditTeleIndicator"
        const val CREDIT_AUTO_VOICE_INDICATOR = "creditAutoVoiceIndicator"
        const val CREDIT_EMAIL_INDICATOR = "creditEmailIndicator"
        const val CREDIT_INDICATOR = "creditIndicator"
        const val CREDIT_POST_INDICATOR = "creditPostIndicator"
        const val CREDIT_SMS_INDICATOR = "creditSmsIndicator"
        const val CREDIT_TELE_INDICATOR = "creditTeleIndicator"
    }

    fun getMarketingIndicators(marketingIndicatorExtendedResponseListener: ExtendedResponseListener<MarketingIndicatorResponse?>)
    fun updateMarketingIndicators(marketingIndicators: MarketingIndicators, updateMarketingIndicatorExtendedResponseListener: ExtendedResponseListener<UpdateMarketingIndicatorResponse?>)
}
