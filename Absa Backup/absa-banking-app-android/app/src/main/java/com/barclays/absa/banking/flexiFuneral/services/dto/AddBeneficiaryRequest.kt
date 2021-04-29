/*
 * Copyright (c) 2020 Absa Bank Limited, All Rights Reserved.
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

package com.barclays.absa.banking.flexiFuneral.services.dto

import com.barclays.absa.banking.framework.ExtendedRequest
import com.barclays.absa.banking.framework.ExtendedResponseListener
import com.barclays.absa.banking.framework.api.request.params.RequestParams

class AddBeneficiaryRequest<T>(beneficiaryDetails: FlexiFuneralBeneficiaryDetails, extendedResponseListener: ExtendedResponseListener<T>) : ExtendedRequest<T>(extendedResponseListener) {

    init {
        params = RequestParams.Builder(FlexiFuneralService.OP2146_ADD_FLEXI_FUNERAL_BENEFICIARY)
                .put("title", beneficiaryDetails.titleInEnglish)
                .put("firstName", beneficiaryDetails.firstName)
                .put("surName", beneficiaryDetails.surname)
                .put("initials", beneficiaryDetails.initials)
                .put("dateOfBirth", beneficiaryDetails.dateOfBirth)
                .put("relationship", beneficiaryDetails.relationship)
                .build()

        mockResponseFile = "flexi_funeral/op2146_flexi_funeral_add_beneficiary.json"
        printRequest()
    }

    @Suppress("UNCHECKED_CAST")
    override fun getResponseClass() = AddBeneficiaryStatusResponse::class.java as Class<T>

    override fun isEncrypted() = true
}