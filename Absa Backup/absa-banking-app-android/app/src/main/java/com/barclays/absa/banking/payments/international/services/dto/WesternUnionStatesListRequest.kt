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
package com.barclays.absa.banking.payments.international.services.dto

import com.barclays.absa.banking.framework.ExtendedRequest
import com.barclays.absa.banking.framework.ExtendedResponseListener
import com.barclays.absa.banking.framework.api.request.params.RequestParams
import com.barclays.absa.banking.payments.international.services.InternationalPaymentsService.Companion.OP0883_GET_ALL_WESTERN_UNION_COUNTRIES
import com.barclays.absa.banking.payments.international.services.WesternUnionParameters

class WesternUnionStatesListRequest<T>(countryCode: String, extendedResponseListener: ExtendedResponseListener<T>) : ExtendedRequest<T>(extendedResponseListener) {

    init {
        params = RequestParams.Builder()
                .put(OP0883_GET_ALL_WESTERN_UNION_COUNTRIES)
                .put(WesternUnionParameters.SERVICE_TRANSFER_TYPE, WesternUnionParameters.WESTERN_UNION)
                .put(WesternUnionParameters.SERVICE_COUNTRY_CODE, countryCode)
                .build()
        mockResponseFile = "international_payments/op0883_get_all_western_union_states_list.json"
        printRequest()
    }

    @Suppress("UNCHECKED_CAST")
    override fun getResponseClass(): Class<T>? = WesternUnionCountriesListResponse::class.java as Class<T>
    override fun isEncrypted(): Boolean? = true
}