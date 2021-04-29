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
package com.barclays.absa.banking.cashSend.services.dto

import com.barclays.absa.banking.beneficiaries.services.BeneficiariesMockFactory
import com.barclays.absa.banking.boundary.model.AddBeneficiaryCashSendConfirmation
import com.barclays.absa.banking.framework.ExtendedRequest
import com.barclays.absa.banking.framework.ExtendedResponseListener
import com.barclays.absa.banking.framework.api.request.params.OpCodeParams.OP0329_ADD_CASHSEND_CONFIRM
import com.barclays.absa.banking.framework.api.request.params.RequestParams
import com.barclays.absa.banking.framework.api.request.params.TransactionParams.Transaction
import com.barclays.absa.banking.framework.app.BMBConstants.*

class AddCashSendBeneficiaryConfirmationRequest<T>(beneficiaryId: String?, beneficiaryName: String,
                                                   beneficiaryNickname: String, beneficiarySurname: String,
                                                   cellphoneNumber: String, myReference: String,
                                                   isBeneficiaryFavourite: Boolean?, addCashSendBeneficiaryResponseListener: ExtendedResponseListener<T>)
    : ExtendedRequest<T>(addCashSendBeneficiaryResponseListener) {

    init {
        val paramBuilder = RequestParams.Builder()
                .put(OP0329_ADD_CASHSEND_CONFIRM)
                .put(Transaction.SERVICE_BENEFICIARY_NAME, beneficiaryName)
                .put(Transaction.SERVICE_BENEFICIARY_SHORT_NAME, beneficiaryNickname)
                .put(Transaction.SERVICE_BENEFICIARY_SUR_NAME, beneficiarySurname)
                .put(Transaction.SERVICE_CELL_NUMBER, cellphoneNumber)
                .put(Transaction.SERVICE_MY_REFERENCE, myReference)
                .put(Transaction.SERVICE_CHANNEL_IND, "S")
                .put(Transaction.SERVICE_BENEFICIARY_TYPE, PASS_CASHSEND.toLowerCase())

        if (!beneficiaryId.isNullOrEmpty()) {
            paramBuilder.put(Transaction.SERVICE_BENEFICIARY_ID, beneficiaryId)
        }

        isBeneficiaryFavourite?.let {
            paramBuilder.put(Transaction.SERVICE_BENEFICAIRY_FAVORITE, if (it) YES else NO)
        }
        params = paramBuilder.build()

        mockResponseFile = BeneficiariesMockFactory.addPaymentConfirmation()
        printRequest()
    }

    @Suppress("UNCHECKED_CAST")
    override fun getResponseClass(): Class<T> = AddBeneficiaryCashSendConfirmation::class.java as Class<T>

    override fun isEncrypted(): Boolean? = true
}