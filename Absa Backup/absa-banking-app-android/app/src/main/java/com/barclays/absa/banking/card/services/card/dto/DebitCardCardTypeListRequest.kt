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

import com.barclays.absa.banking.boundary.model.debitCard.DebitCardProductType
import com.barclays.absa.banking.boundary.model.debitCard.DebitCardTypeList
import com.barclays.absa.banking.framework.ExtendedRequest
import com.barclays.absa.banking.framework.ExtendedResponseListener
import com.barclays.absa.banking.framework.api.request.params.OpCodeParams.OP0803_GET_CARD_TYPE_FOR_DEBIT_CARD_REPLACEMENT
import com.barclays.absa.banking.framework.api.request.params.RequestParams
import com.barclays.absa.banking.framework.api.request.params.TransactionParams.Transaction

class DebitCardCardTypeListRequest<T>(debitCardProductType: DebitCardProductType,
                                      responseListener: ExtendedResponseListener<T>) : ExtendedRequest<T>(responseListener) {
    init {
        params = RequestParams.Builder()
                .put(OP0803_GET_CARD_TYPE_FOR_DEBIT_CARD_REPLACEMENT)
                .put(Transaction.PRODUCT_CODE, debitCardProductType.productCode)
                .put(Transaction.PRODUCT_TYPE, debitCardProductType.productType)
                .build()

        mockResponseFile = "manage_cards/op0803_debit_card_card_type_list.json"
        printRequest()
    }

    @Suppress("UNCHECKED_CAST")
    override fun getResponseClass(): Class<T> = DebitCardTypeList::class.java as Class<T>
    override fun isEncrypted(): Boolean? = true
}
