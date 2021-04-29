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
package com.barclays.absa.banking.newToBank.services

import com.barclays.absa.banking.framework.ExtendedRequest
import com.barclays.absa.banking.framework.ExtendedResponseListener
import com.barclays.absa.banking.framework.MockFactory
import com.barclays.absa.banking.framework.api.request.params.NewToBankParams
import com.barclays.absa.banking.framework.api.request.params.RequestParams
import com.barclays.absa.banking.newToBank.dto.CustomerPortfolioInfo
import com.barclays.absa.banking.newToBank.services.NewToBankService.Companion.OP2084_CASA_RISK_PROFILING
import com.barclays.absa.banking.newToBank.services.dto.PerformCasaRiskProfilingResponse

class NewToBankPerformCasaRiskProfilingRequest<T>(customerPortfolioInfo: CustomerPortfolioInfo,
                                                  responseListener: ExtendedResponseListener<T>) : ExtendedRequest<T>(responseListener) {

    init {
        params = RequestParams.Builder()
                .put(OP2084_CASA_RISK_PROFILING)
                .put(NewToBankParams.OCCUPATION_STATUS.key, customerPortfolioInfo.occupationStatus)
                .put(NewToBankParams.OCCUPATION_CODE.key, customerPortfolioInfo.occupationCode)
                .put(NewToBankParams.PRODUCT_TYPE.key, customerPortfolioInfo.productType)
                .put(NewToBankParams.SOURCE_OF_INCOME.key, customerPortfolioInfo.sourceOfIncome)
                .build()

        mockResponseFile = MockFactory.getCasaRiskProfilingResult(0)
        printRequest()
    }

    @Suppress("UNCHECKED_CAST")
    override fun getResponseClass(): Class<T>? = PerformCasaRiskProfilingResponse::class.java as Class<T>
    override fun isEncrypted() = true
}