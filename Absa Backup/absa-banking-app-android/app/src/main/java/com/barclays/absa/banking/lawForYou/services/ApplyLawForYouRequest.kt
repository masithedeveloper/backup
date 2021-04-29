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
package com.barclays.absa.banking.lawForYou.services

import com.barclays.absa.banking.framework.ExtendedRequest
import com.barclays.absa.banking.framework.ExtendedResponseListener
import com.barclays.absa.banking.framework.api.request.params.RequestParams
import com.barclays.absa.banking.lawForYou.services.LawForYouService.Companion.OP2154_APPLY_LAW_FOR_YOU
import com.barclays.absa.banking.lawForYou.services.dto.ApplyLawForYouResponse
import com.barclays.absa.banking.lawForYou.ui.LawForYouDetails
import styleguide.utils.extensions.toDoubleString

class ApplyLawForYouRequest<T>(details: LawForYouDetails, responseListener: ExtendedResponseListener<T>) : ExtendedRequest<T>(responseListener) {

    init {
        params = RequestParams.Builder(OP2154_APPLY_LAW_FOR_YOU)
                .put("cellNumber", details.cellNumber)
                .put("emailAddress", details.emailAddress)
                .put("addressLine1", details.addressLine1)
                .put("addressLine2", details.addressLine2)
                .put("Suburb", details.suburb.toUpperCase())
                .put("City", details.city.toUpperCase())
                .put("postalCode", details.postalCode)
                .put("Country", details.country)
                .put("coverPremiumAmount", details.coverPremiumAmount.toDoubleString())
                .put("coverAssuredAmount", details.coverAssuredAmount.toDoubleString())
                .put("dayOfDebit", "%02d".format(details.dayOfDebit.toInt()))
                .put("inceptionDate", details.inceptionDate)
                .put("businessSourceIndicator", details.businessSourceIndicator)
                .put("accountToBeDebited", details.accountToBeDebited)
                .put("coverPlan", details.coverPlan)
                .build()

        mockResponseFile = "law_for_you/op2154_apply_law_for_you.json"
        printRequest()
    }

    @Suppress("UNCHECKED_CAST")
    override fun getResponseClass() = ApplyLawForYouResponse::class.java as Class<T>

    override fun isEncrypted() = true
}