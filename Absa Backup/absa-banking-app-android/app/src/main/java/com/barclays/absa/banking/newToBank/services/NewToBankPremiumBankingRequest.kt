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

import com.barclays.absa.banking.BuildConfig
import com.barclays.absa.banking.framework.ExtendedGetRequest
import com.barclays.absa.banking.framework.ExtendedResponseListener
import com.barclays.absa.banking.framework.api.request.params.RequestParams
import com.barclays.absa.banking.framework.utils.AppConstants.NEW_TO_BANK_PREMIUM_VALUE_BUNDLE_URL
import com.barclays.absa.banking.newToBank.services.dto.CardPackageResponse

class NewToBankPremiumBankingRequest<T>(responseListener: ExtendedResponseListener<T>)
    : ExtendedGetRequest<T>(NEW_TO_BANK_PREMIUM_VALUE_BUNDLE_URL, responseListener) {

    init {
        params = RequestParams.Builder().build()
        if (!BuildConfig.PRD && !BuildConfig.PRD_BETA) {
            setForcedStubMode(true)
        }

        mockResponseFile = "card_package_premium_banking.json"
        printRequest()
    }

    @Suppress("UNCHECKED_CAST")
    override fun getResponseClass(): Class<T> = CardPackageResponse::class.java as Class<T>
    override fun isEncrypted(): Boolean? = true
}