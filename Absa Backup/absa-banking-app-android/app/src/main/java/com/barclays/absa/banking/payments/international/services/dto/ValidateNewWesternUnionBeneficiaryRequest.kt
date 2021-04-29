/*
 * Copyright (c) 2018 Absa Bank Limited, All Rights Reserved.
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
package com.barclays.absa.banking.payments.international.services.dto

import com.barclays.absa.banking.framework.ExtendedRequest
import com.barclays.absa.banking.framework.ExtendedResponseListener
import com.barclays.absa.banking.framework.api.request.params.RequestParams
import com.barclays.absa.banking.framework.api.request.params.TransactionParams
import com.barclays.absa.banking.framework.app.BMBConstants
import com.barclays.absa.banking.payments.international.data.BeneficiaryEnteredDetails
import com.barclays.absa.banking.payments.international.services.InternationalPaymentsService.Companion.OP0878_VALIDATE_NEW_WESTERN_UNION_BENEFICIARY
import com.barclays.absa.banking.payments.international.services.WesternUnionParameters

class ValidateNewWesternUnionBeneficiaryRequest<T>(beneficiaryEnteredDetails: BeneficiaryEnteredDetails, extendedResponseListener: ExtendedResponseListener<T>) : ExtendedRequest<T>(extendedResponseListener) {

    init {
        params = RequestParams.Builder()
                .put(OP0878_VALIDATE_NEW_WESTERN_UNION_BENEFICIARY)
                .put(TransactionParams.Transaction.SERVICE_CHANNEL_IND, BMBConstants.SMARTPHONE_CHANNEL_IND)
                .put(WesternUnionParameters.SERVICE_BENEFICIARY_TYPE_WESTERN_UNION, WesternUnionParameters.WESTERN_UNION)
                .put(TransactionParams.Transaction.SERVICE_BENEFICIARY_NAME_AIRTIME, beneficiaryEnteredDetails.beneficiaryNames)
                .put(WesternUnionParameters.SERVICE_BENEFICIARY_SURNAME, beneficiaryEnteredDetails.beneficiarySurname)
                .put(WesternUnionParameters.SERVICE_BENEFICIARY_SHORT_NAME, beneficiaryEnteredDetails.shortName())
                .put(WesternUnionParameters.SERVICE_BENEFICIARY_RESIDENTIAL_STATUS, beneficiaryEnteredDetails.beneficiaryCitizenship)
                .put(WesternUnionParameters.SERVICE_BENEFICIARY_GENDER, beneficiaryEnteredDetails.beneficiaryGender?.substring(0, 1))
                .put(WesternUnionParameters.SERVICE_BENEFICIARY_STREET_ADDRESS, beneficiaryEnteredDetails.paymentAddress)
                .put(WesternUnionParameters.SERVICE_BENEFICIARY_COUNTRY, beneficiaryEnteredDetails.paymentDestinationCountryCode)
                .put(WesternUnionParameters.SERVICE_VALIDATE_PAYMENT_BENEFICIARY_ISO_COUNTRY_CODE, beneficiaryEnteredDetails.paymentDestinationCountryCode)
                .put(WesternUnionParameters.SERVICE_BENEFICIARY_STATE, beneficiaryEnteredDetails.paymentState)
                .put(WesternUnionParameters.SERVICE_BENEFICIARY_CITY, beneficiaryEnteredDetails.paymentCity)
                .put(WesternUnionParameters.SERVICE_PAYMENT_TYPE, WesternUnionParameters.NORMAL)
                .put(WesternUnionParameters.SERVICE_BENEFICIARY_TYPE, WesternUnionParameters.INDIVIDUAL)
                .build()
        mockResponseFile = "international_payments/op0878_validate_new_western_union_beneficiary.json"
        printRequest()
    }

    @Suppress("UNCHECKED_CAST")
    override fun getResponseClass(): Class<T>? = ValidateNewWesternUnionBeneficiaryResponse::class.java as Class<T>
    override fun isEncrypted(): Boolean? = true
}