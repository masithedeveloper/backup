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

import com.barclays.absa.banking.card.services.card.dto.CreditCardRequestParameters
import com.barclays.absa.banking.card.services.card.dto.CreditCardService.OP0905_BUREAU_DATA_FOR_VCL_CLI_PULL
import com.barclays.absa.banking.framework.ExtendedRequest
import com.barclays.absa.banking.framework.ExtendedResponseListener
import com.barclays.absa.banking.framework.api.request.params.RequestParams

class BureauDataRequest<T>(dataModel: VCLParcelableModel?, responseListener: ExtendedResponseListener<T>)
    : ExtendedRequest<T>(responseListener) {

    companion object {
        private const val BASE_REQUEST_VALUES = "0.0"
    }

    init {
        params = RequestParams.Builder()
                .put(OP0905_BUREAU_DATA_FOR_VCL_CLI_PULL)
                .put(CreditCardRequestParameters.OFFERS_CREDIT_CARD_NUMBER.key, dataModel?.creditCardNumber ?: "")
                .put(CreditCardRequestParameters.CREDIT_CARD_LIMIT_INCREASE_AMOUNT_FOR_CLI.key, dataModel?.newCreditLimitAmount ?: BASE_REQUEST_VALUES)
                .put(CreditCardRequestParameters.EXISTING_CREDIT_LIMIT_OF_CREDIT_CARD.key, dataModel?.currentCreditLimit ?: BASE_REQUEST_VALUES)
                .build()

        mockResponseFile = "vcl/op0905_bureau_data_for_vcl_cli.json"
        printRequest()
    }

    @Suppress("UNCHECKED_CAST")
    override fun getResponseClass(): Class<T> = CreditCardBureauData::class.java as Class<T>
    override fun isEncrypted(): Boolean = true
}