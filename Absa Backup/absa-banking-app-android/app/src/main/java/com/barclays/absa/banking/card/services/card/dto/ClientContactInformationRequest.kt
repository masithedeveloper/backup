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
package com.barclays.absa.banking.card.services.card.dto

import com.barclays.absa.banking.boundary.model.ClientContactInformationList
import com.barclays.absa.banking.card.services.card.CardMockFactory
import com.barclays.absa.banking.framework.ExtendedRequest
import com.barclays.absa.banking.framework.ExtendedResponseListener
import com.barclays.absa.banking.framework.api.request.params.OpCodeParams.OP0804_GET_CLIENT_INFORMATION
import com.barclays.absa.banking.framework.api.request.params.RequestParams

class ClientContactInformationRequest<T>(responseListener: ExtendedResponseListener<T>)
    : ExtendedRequest<T>(responseListener) {

    init {
        params = RequestParams.Builder().put(OP0804_GET_CLIENT_INFORMATION).build()
        mockResponseFile = CardMockFactory.debitCardStopAndReplaceClientContactInformation()
        printRequest()
    }

    @Suppress("UNCHECKED_CAST")
    override fun getResponseClass(): Class<T> = ClientContactInformationList::class.java as Class<T>
    override fun isEncrypted(): Boolean? = true
}