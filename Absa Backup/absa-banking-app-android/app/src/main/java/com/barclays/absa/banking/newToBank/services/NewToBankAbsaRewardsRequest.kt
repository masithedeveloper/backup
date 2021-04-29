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
import com.barclays.absa.banking.framework.utils.AppConstants.NEW_TO_BANK_ABSA_FREE_REWARDS_URL
import com.barclays.absa.banking.framework.utils.AppConstants.NEW_TO_BANK_ABSA_REWARDS_URL
import com.barclays.absa.banking.newToBank.services.dto.AbsaRewardsResponse

class NewToBankAbsaRewardsRequest<T>(isFree: Boolean, responseListener: ExtendedResponseListener<T>)
    : ExtendedGetRequest<T>(if (isFree) NEW_TO_BANK_ABSA_FREE_REWARDS_URL else NEW_TO_BANK_ABSA_REWARDS_URL, responseListener) {

    init {
        params = RequestParams.Builder().build()

        @Suppress("ConstantConditionIf")
        if (!(BuildConfig.PRD || BuildConfig.PRD_BETA)) {
            setForcedStubMode(true)
        }
        mockResponseFile = if (isFree) "free_absa_rewards.json" else "absa_rewards.json"
        printRequest()
    }

    @Suppress("UNCHECKED_CAST")
    override fun getResponseClass(): Class<T> = AbsaRewardsResponse::class.java as Class<T>
    override fun isEncrypted(): Boolean? = true
}