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
import com.barclays.absa.banking.payments.international.data.InternationalPaymentBeneficiaryDetails
import com.barclays.absa.banking.payments.international.services.InternationalPaymentsService.Companion.OP0889_GET_WESTERN_UNION_BENEFICIARY_DETAILS
import com.barclays.absa.banking.payments.international.services.WesternUnionParameters

class WesternUnionBeneficiaryDetailsRequest<T>(beneficiaryDetails: InternationalPaymentBeneficiaryDetails, extendedResponseListener: ExtendedResponseListener<T>) : ExtendedRequest<T>(extendedResponseListener) {

    init {
        params = RequestParams.Builder()
                .put(OP0889_GET_WESTERN_UNION_BENEFICIARY_DETAILS)
                .put(WesternUnionParameters.SERVICE_TRANSFER_TYPE, beneficiaryDetails.transferType)
                .put(WesternUnionParameters.SERVICE_BENEFICIARY_IFT_TYPE, beneficiaryDetails.beneficiaryIFTType)
                .put(WesternUnionParameters.SERVICE_ID, beneficiaryDetails.beneficiaryId)
                .put(WesternUnionParameters.SERVICE_STATUS, beneficiaryDetails.status)
                .put(WesternUnionParameters.SERIVCE_EFT_NUMBER, beneficiaryDetails.eftNumber)
                .put(WesternUnionParameters.SERVICE_CIF_KEY, beneficiaryDetails.cifkey)
                .put(WesternUnionParameters.SERVICE_TIEB_NUMBER, beneficiaryDetails.tiebNumber)
                .build()
        mockResponseFile = "international_payments/op0889_get_beneficiary_details.json"
        printRequest()
    }

    @Suppress("UNCHECKED_CAST")
    override fun getResponseClass(): Class<T>? = WesternUnionBeneficiaryDetails::class.java as Class<T>
    override fun isEncrypted(): Boolean? = true
}