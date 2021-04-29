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

import com.barclays.absa.banking.boundary.model.CardReplacementAndFetchFees
import com.barclays.absa.banking.boundary.model.debitCard.DebitCardReplacementDetailsConfirmation
import com.barclays.absa.banking.framework.ExtendedRequest
import com.barclays.absa.banking.framework.ExtendedResponseListener
import com.barclays.absa.banking.framework.api.request.params.OpCodeParams
import com.barclays.absa.banking.framework.api.request.params.RequestParams
import com.barclays.absa.banking.framework.api.request.params.TransactionParams.Transaction
import styleguide.utils.extensions.removeSpaces

class DebitCardReplacementFeesRequest<T>(debitCardReplacementDetailsConfirmation: DebitCardReplacementDetailsConfirmation,
                                         responseListener: ExtendedResponseListener<T>) : ExtendedRequest<T>(responseListener) {
    init {
        debitCardReplacementDetailsConfirmation.let {
            params = RequestParams.Builder()
                    .put(OpCodeParams.OP0806_CARD_REPLACE_AND_FETCH_FEES)
                    .put(Transaction.OLD_DEBIT_CARD_NUMBER, it.oldDebitCardNumber.removeSpaces())
                    .put(Transaction.REASON_CODE, it.reasonCode)
                    .put(Transaction.REASON_DESCRIPTION, it.reason)
                    .put(Transaction.PRODUCT_CODE, it.productCode)
                    .put(Transaction.PRODUCT_TYPE, it.productType)
                    .put(Transaction.BRAND_NAME, it.debitCardType)
                    .put(Transaction.BRAND_NUMBER, it.brandNumber)
                    .put(Transaction.BRAND_TYPE, it.brandType)
                    .put(Transaction.CARD_DELIVERY_METHOD, it.cardDeliveryMethod)
                    .put(Transaction.BRANCH_CODE, it.branchCode)
                    .put(Transaction.BRANCH_NAME, it.preferredBranch)
                    .put(Transaction.BRANCH_ADDRESS, it.branchAddress)
                    .build()
        }

        mockResponseFile = "op0806_debit_card_replacement_fees.json"
        printRequest()
    }

    @Suppress("UNCHECKED_CAST")
    override fun getResponseClass(): Class<T> = CardReplacementAndFetchFees::class.java as Class<T>
    override fun isEncrypted(): Boolean? = true
}