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
package com.barclays.absa.banking.card.services.card.dto.creditCard

import com.barclays.absa.banking.boundary.model.ManageCardConfirmLimit
import com.barclays.absa.banking.boundary.model.ManageCardLimitDetails
import com.barclays.absa.banking.framework.ExtendedRequest
import com.barclays.absa.banking.framework.ExtendedResponseListener
import com.barclays.absa.banking.framework.api.request.params.OpCodeParams.OP0122_CARD_CHANGE_LIMITS_CONFIRM
import com.barclays.absa.banking.framework.api.request.params.RequestParams
import com.barclays.absa.banking.framework.api.request.params.TransactionParams.Transaction
import styleguide.utils.extensions.removeCommasAndDots

class CardLimitDetailsConfirmRequest<T>(manageCardLimitDetails: ManageCardLimitDetails,
                                        posLimitValue: String, atmLimitValue: String,
                                        responseListener: ExtendedResponseListener<T>) : ExtendedRequest<T>(responseListener) {
    private val oldAtmLimit: String
    private val atmLimitValue: String
    private val oldPointOfSaleLimit: String
    private val newPointOfSaleLimit: String

    init {

        this.oldAtmLimit = manageCardLimitDetails.atmCurrentLimit?.getAmount().removeCommasAndDots()
        this.atmLimitValue = atmLimitValue.removeCommasAndDots()

        this.oldPointOfSaleLimit = manageCardLimitDetails.posCurrentLimit?.getAmount().removeCommasAndDots()

        val isCreditCardType = "credit".equals(manageCardLimitDetails.cardType, ignoreCase = true)
        val newPosLimit = if (isCreditCardType) manageCardLimitDetails.posCurrentLimit?.getAmount() else posLimitValue
        this.newPointOfSaleLimit = newPosLimit.removeCommasAndDots()

        params = RequestParams.Builder()
                .put(OP0122_CARD_CHANGE_LIMITS_CONFIRM)
                .put(Transaction.CARD_CHANGE_LIMIT_NEW_ATM_LIMIT, atmLimitValue)
                .put(Transaction.CARD_CHANGE_LIMIT_OLD_POSLIMIT, oldPointOfSaleLimit)
                .put(Transaction.CARD_CHANGE_LIMIT_NEW_POS_LIMIT, newPointOfSaleLimit)
                .put(Transaction.CARD_CHANGE_LIMIT_OLD_ATM_LIMIT, oldAtmLimit)
                .put(Transaction.CARD_CHANGE_LIMIT_CARD_NUMBER, manageCardLimitDetails.cardNumber)
                .put(Transaction.CARD_CHANGE_LIMIT_CARD_TYPE, manageCardLimitDetails.cardType)
                .build()

        mockResponseFile = "op0122_card_limit_details_confirm.json"
        printRequest()
    }

    @Suppress("UNCHECKED_CAST")
    override fun getResponseClass(): Class<T> = ManageCardConfirmLimit::class.java as Class<T>
    override fun isEncrypted(): Boolean = true
}