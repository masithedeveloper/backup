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

import com.barclays.absa.banking.boundary.model.debitCard.DebitCardProductTypeList
import com.barclays.absa.banking.framework.ExtendedRequest
import com.barclays.absa.banking.framework.ExtendedResponseListener
import com.barclays.absa.banking.framework.api.request.params.OpCodeParams.OP0802_GET_PRODUCT_TYPE_FOR_DEBIT_CARD_REPLACEMENT
import com.barclays.absa.banking.framework.api.request.params.RequestParams

class DebitCardProductTypeListRequest<T>(responseListener: ExtendedResponseListener<T>) : ExtendedRequest<T>(responseListener) {

    init {
        params = RequestParams.Builder().put(OP0802_GET_PRODUCT_TYPE_FOR_DEBIT_CARD_REPLACEMENT).build()
        mockResponseFile = "manage_cards/op0802_get_product_type_for_debit_card_replacement.json"
        printRequest()
    }

    @Suppress("UNCHECKED_CAST")
    override fun getResponseClass(): Class<T> = DebitCardProductTypeList::class.java as Class<T>
    override fun isEncrypted(): Boolean? = true
}
