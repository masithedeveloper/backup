/*
 *  Copyright (c) 2019 Absa Bank Limited, All Rights Reserved.
 *
 *  This code is confidential to Absa Bank Limited and shall not be disclosed
 *  outside the Bank without the prior written permission of the Absa Legal
 *
 *  In the event that such disclosure is permitted the code shall not be copied
 *  or distributed other than on a need-to-know basis and any recipients may be
 *  required to sign a confidentiality undertaking in favor of Absa Bank
 *  Limited
 *
 */
package com.barclays.absa.banking.settings.services

import com.barclays.absa.banking.boundary.model.SecureHomePageObject
import com.barclays.absa.banking.framework.ExtendedRequest
import com.barclays.absa.banking.framework.ExtendedResponseListener
import com.barclays.absa.banking.framework.api.request.params.RequestParams
import com.barclays.absa.banking.framework.app.BMBConstants
import com.barclays.absa.banking.framework.app.BMBConstants.ACCOUNT_CACHING_YES
import com.barclays.absa.banking.settings.ui.SettingsHubService
import com.barclays.absa.banking.settings.ui.SettingsHubService.OP0202_SHP_LANGUAGE_CHANGE

class LanguageUpdateRequest<T>(languageCode: Char, responseListener: ExtendedResponseListener<T>) : ExtendedRequest<T>(responseListener) {

    init {
        params = RequestParams.Builder()
                .put(OP0202_SHP_LANGUAGE_CHANGE)
                .put(SettingsHubService.SERVICE_LANGUAGE_CODE, languageCode.toString())
                .put(SettingsHubService.IS_CUST_ACTS_REQ, ACCOUNT_CACHING_YES)
                .build()

        mockResponseFile = if (languageCode == 'E') {
            "profile/op0202_language_update_success_en.json"
        } else {
            "profile/op0202_language_update_success_af.json"
        }
        printRequest()
    }

    @Suppress("UNCHECKED_CAST")
    override fun getResponseClass(): Class<T>? = SecureHomePageObject::class.java as Class<T>
    override fun isEncrypted(): Boolean? = true
}