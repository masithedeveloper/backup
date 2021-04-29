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

import com.barclays.absa.banking.directMarketing.services.dto.*
import com.barclays.absa.banking.framework.ExtendedResponseListener
import com.barclays.absa.banking.framework.ServiceClient

class DirectMarketingInteractor : DirectMarketingService {

    override fun getMarketingIndicators(marketingIndicatorExtendedResponseListener: ExtendedResponseListener<MarketingIndicatorResponse?>) {
        val getMarketingIndicatorRequest = GetMarketingIndicatorRequest(marketingIndicatorExtendedResponseListener)
        val serviceClient = ServiceClient(getMarketingIndicatorRequest)
        serviceClient.submitRequest()
    }

    override fun updateMarketingIndicators(marketingIndicators: MarketingIndicators, updateMarketingIndicatorExtendedResponseListener: ExtendedResponseListener<UpdateMarketingIndicatorResponse?>) {
        val updateMarketingIndicatorRequest = UpdateMarketingIndicatorRequest(marketingIndicators, updateMarketingIndicatorExtendedResponseListener)
        val serviceClient = ServiceClient(updateMarketingIndicatorRequest)
        serviceClient.submitRequest()
    }
}