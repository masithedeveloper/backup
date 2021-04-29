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

import com.barclays.absa.banking.boundary.model.debitCard.DebitCardReplacementConfirmation
import com.barclays.absa.banking.boundary.model.debitCard.DebitCardReplacementDetailsConfirmation
import com.barclays.absa.banking.card.services.card.CardMockFactory
import com.barclays.absa.banking.framework.ExtendedRequest
import com.barclays.absa.banking.framework.ExtendedResponseListener
import com.barclays.absa.banking.framework.api.request.params.OpCodeParams.OP0807_REPLACE_DEBIT_CARD_CONFIRMATION
import com.barclays.absa.banking.framework.api.request.params.RequestParams
import com.barclays.absa.banking.framework.api.request.params.TransactionParams.Transaction

class DebitCardReplacementDetailsConfirmationRequest<T>(debitCardReplacementDetailsConfirmation: DebitCardReplacementDetailsConfirmation,
                                                        responseListener: ExtendedResponseListener<T>) : ExtendedRequest<T>(responseListener) {
    init {
        debitCardReplacementDetailsConfirmation.let {
            params = RequestParams.Builder()
                    .put(OP0807_REPLACE_DEBIT_CARD_CONFIRMATION)
                    .put(Transaction.OLD_DEBIT_CARD_NUMBER, it.oldDebitCardNumber)
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
                    .put(Transaction.REPLACEMENT_FEE_APPLICABLE, it.replacementFeeApplicable)
                    .put(Transaction.REPLACEMENT_FEE_TYPE, it.replacementFeeType)
                    .put(Transaction.REPLACEMENT_FEE, it.replacementFee)
                    .put(Transaction.CARD_DELIVERY_FEE_APPLICABLE, it.deliveryFeeApplicable)
                    .put(Transaction.CARD_DELIVERY_FEE_TYPE, it.cardDeliveryFeeType)
                    .put(Transaction.CARD_DELIVERY_FEE, it.cardDeliveryFee)
                    .put(Transaction.CELLPHONE_NO, it.clientContactNumber)
                    .put(Transaction.WORK_TELEPHONE_NUMBER, it.workTelephoneNumber)
                    .put(Transaction.HOME_TELEPHONE_NUMBER, it.homeTelephoneNumber)
                    .put(Transaction.ADDRESS_LINE_1, it.addressLine1)
                    .put(Transaction.ADDRESS_LINE_2, it.addressLine2)
                    .put(Transaction.SUBURB_RSA, it.suburb)
                    .put(Transaction.TOWN, it.town)
                    .put(Transaction.POSTAL_CODE, it.postalCode)
                    .build()
        }

        mockResponseFile = CardMockFactory.debitCardStopAndReplaceConfirmation()
        printRequest()
    }

    @Suppress("UNCHECKED_CAST")
    override fun getResponseClass(): Class<T> = DebitCardReplacementConfirmation::class.java as Class<T>
    override fun isEncrypted(): Boolean? = true
}