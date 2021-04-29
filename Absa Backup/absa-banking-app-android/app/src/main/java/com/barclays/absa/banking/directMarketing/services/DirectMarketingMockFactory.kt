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

class DirectMarketingMockFactory {
    companion object {
        fun getDirectMarketingIndicators(): String {
            return "direct_marketing/op2089_get_direct_marketing_indicator_success.json"
        }

        fun updatedDirectMarketingIndicatorsResponse(): String {
            return "direct_marketing/op2090_updated_marketing_indicators_success.json"
        }
    }
}