/*
 * Copyright (c) 2020 Absa Bank Limited, All Rights Reserved.
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
import com.barclays.absa.banking.framework.app.BMBApplication
import com.barclays.absa.banking.newToBank.NewToBankConstants.NEW_TO_BANK_BUSINESS_EVOLVE_OPTIONAL_EXTRAS_AF_URL
import com.barclays.absa.banking.newToBank.NewToBankConstants.NEW_TO_BANK_BUSINESS_EVOLVE_OPTIONAL_EXTRAS_URL
import com.barclays.absa.banking.newToBank.services.dto.BusinessEvolveOptionalExtrasResponse
import java.util.*

class NewToBankBusinessEvolveOptionalExtrasRequest<T>(responseListener: ExtendedResponseListener<T>)
    : ExtendedGetRequest<T>(if (Locale.ENGLISH == BMBApplication.getApplicationLocale()) NEW_TO_BANK_BUSINESS_EVOLVE_OPTIONAL_EXTRAS_URL else NEW_TO_BANK_BUSINESS_EVOLVE_OPTIONAL_EXTRAS_AF_URL, responseListener) {

    init {
        params = RequestParams.Builder().build()
        mockResponseFile = if (Locale.ENGLISH == BMBApplication.getApplicationLocale()) "business_evolve_optional_extra.json" else "business_evolve_optional_extra_af.json"
        setForcedStubMode(BuildConfig.STUB || BuildConfig.UAT)
        printRequest()
    }

    @Suppress("UNCHECKED_CAST")
    override fun getResponseClass(): Class<T> = BusinessEvolveOptionalExtrasResponse::class.java as Class<T>

    override fun isEncrypted(): Boolean? = true
}