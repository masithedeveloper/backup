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

package com.barclays.absa.banking.ultimateProtector.services.dto

import com.barclays.absa.banking.framework.ExtendedRequest
import com.barclays.absa.banking.framework.ExtendedResponseListener
import com.barclays.absa.banking.framework.MockFactory
import com.barclays.absa.banking.framework.api.request.params.RequestParams
import com.barclays.absa.banking.ultimateProtector.services.LifeCoverService.Companion.OP2078_RETRIEVE_QUOTE
import com.barclays.absa.banking.ultimateProtector.services.LifeCoverService.UltimateProtectorParams.BENEFIT_CODE
import com.barclays.absa.banking.ultimateProtector.services.LifeCoverService.UltimateProtectorParams.PRODUCT_NAME

class QuotationRequest<T>(benefitCode: String,
                          responseListener: ExtendedResponseListener<T>) : ExtendedRequest<T>(responseListener) {
    init {
        val productName = "UltimateProtectorApply"
        params = RequestParams.Builder()
                .put(OP2078_RETRIEVE_QUOTE)
                .put(PRODUCT_NAME.key, productName)
                .put(BENEFIT_CODE.key, benefitCode)
                .build()
        mockResponseFile = MockFactory.lifeCoverQuotation()
        printRequest()
    }

    @Suppress("UNCHECKED_CAST")
    override fun getResponseClass(): Class<T> = Quotation::class.java as Class<T>
    override fun isEncrypted(): Boolean = true
}