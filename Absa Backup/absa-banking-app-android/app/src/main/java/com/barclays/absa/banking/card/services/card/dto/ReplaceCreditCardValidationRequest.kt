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
package com.barclays.absa.banking.card.services.card.dto

import com.barclays.absa.banking.boundary.model.creditCardStopAndReplace.CreditCardReplacementConfirmation
import com.barclays.absa.banking.card.services.card.dto.CreditCardService.OP0869_CREDIT_CARD_REPLACEMENT_VALIDATION
import com.barclays.absa.banking.framework.ExtendedRequest
import com.barclays.absa.banking.framework.ExtendedResponseListener
import com.barclays.absa.banking.framework.api.request.params.RequestParams
import com.barclays.absa.banking.framework.api.request.params.TransactionParams.Transaction
import com.barclays.absa.banking.framework.app.BMBConstants

class ReplaceCreditCardValidationRequest<T>(cardReplacementConfirmation: CreditCardReplacementConfirmation,
                                            replacementValidationResponseListener: ExtendedResponseListener<T>)
    : ExtendedRequest<T>(replacementValidationResponseListener) {

    init {
        val branchCode = if (BMBConstants.COLLECT_FROM_BRANCH.equals(cardReplacementConfirmation.cardDeliveryMethod, ignoreCase = true))
            cardReplacementConfirmation.deliveryBranchCode else null

        val dateLastUsed = cardReplacementConfirmation.dateLastUse
        val dateLost = cardReplacementConfirmation.dateLoss
        val BRANCH_CODE = "008114"
        val deliveryBranchCode = if (cardReplacementConfirmation.deliveryBranchCode.isNullOrEmpty())
            BRANCH_CODE else "00" + cardReplacementConfirmation.deliveryBranchCode

        val requestParamsBuilder = RequestParams.Builder()
                .put(OP0869_CREDIT_CARD_REPLACEMENT_VALIDATION)
                .put(Transaction.SERVICE_CHANNEL_IND, "S")
                .put(Transaction.CREDIT_CARD_NUMBER, cardReplacementConfirmation.creditCardNumber)
                .put(Transaction.DATE_LOST, dateLost)
                .put(Transaction.DATE_LAST_USED, dateLastUsed)
                .put(Transaction.SERVICE_CONTACT_NUMBER, cardReplacementConfirmation.contactNumber)
                .put(Transaction.CARD_DELIVERY_METHOD, cardReplacementConfirmation.cardDeliveryMethod)
                .put(Transaction.CARD_DELIVERY_BRANCH_CODE, deliveryBranchCode)
                .put(Transaction.CREDIT_CARD_REPLACEMENT_REASON, cardReplacementConfirmation.replacementReason)

        branchCode?.let { requestParamsBuilder.put(Transaction.CARD_DELIVERY_BRANCH_CODE, it) }
        params = requestParamsBuilder.build()

        mockResponseFile = "card/op0869_credit_card_replacement_validation.json"
        printRequest()
    }

    @Suppress("UNCHECKED_CAST")
    override fun getResponseClass(): Class<T> = CreditCardReplacementConfirmation::class.java as Class<T>
    override fun isEncrypted(): Boolean? = true
}