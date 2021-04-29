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
package com.barclays.absa.banking.virtualpayments.scan2Pay.services.request

import com.barclays.absa.banking.framework.ExtendedRequest
import com.barclays.absa.banking.framework.ExtendedResponseListener
import com.barclays.absa.banking.framework.api.request.params.RequestParams
import com.barclays.absa.banking.virtualpayments.scan2Pay.services.ScanToPayService
import com.barclays.absa.banking.virtualpayments.scan2Pay.services.response.dto.ScanToPayCardListResponse

class ScanToPayCardListRequest<T>(responseListener: ExtendedResponseListener<T>) : ExtendedRequest<T>(responseListener) {
    init {
        params = RequestParams.Builder(ScanToPayService.OP2204_CARD_LIST_REQUEST)
                .put("scanToPay", "Y")
                .put("visa", "Y")
                .build()

        mockResponseFile = "scan_to_pay/op2204_scan_to_pay_card_list.json"
        printRequest()
    }

    @Suppress("UNCHECKED_CAST")
    override fun getResponseClass(): Class<T> = ScanToPayCardListResponse::class.java as Class<T>
    override fun isEncrypted(): Boolean = true
}