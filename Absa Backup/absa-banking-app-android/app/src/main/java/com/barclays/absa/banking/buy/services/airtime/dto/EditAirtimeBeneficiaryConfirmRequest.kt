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
package com.barclays.absa.banking.buy.services.airtime.dto

import com.barclays.absa.banking.beneficiaries.services.BeneficiariesMockFactory
import com.barclays.absa.banking.boundary.model.AddBeneficiaryResult
import com.barclays.absa.banking.buy.services.airtime.PrepaidService.OP0332_EDIT_AIRTIME_CONFIRM
import com.barclays.absa.banking.framework.ExtendedRequest
import com.barclays.absa.banking.framework.ExtendedResponseListener
import com.barclays.absa.banking.framework.api.request.params.RequestParams
import com.barclays.absa.banking.framework.api.request.params.TransactionParams.Transaction
import com.barclays.absa.banking.framework.app.BMBConstants
import com.barclays.absa.banking.framework.app.BMBConstants.ALPHABET_N

class EditAirtimeBeneficiaryConfirmRequest<T>(beneficiaryId: String, beneficiaryName: String,
                                              cellphoneNumber: String, networkProvider: String,
                                              institutionCode: String, imageName: String,
                                              responseListener: ExtendedResponseListener<T>) : ExtendedRequest<T>(responseListener) {
    init {
        params = RequestParams.Builder()
                .put(OP0332_EDIT_AIRTIME_CONFIRM)
                .put(Transaction.SERVICE_BENEFICIARY_ID, beneficiaryId)
                .put(Transaction.SERVICE_BENEFICIARY_NAME, beneficiaryName)
                .put(Transaction.SERVICE_BENEFICIARY_TYPE, BMBConstants.PASS_PREPAID.toLowerCase())
                .put(Transaction.SERVICE_CELL_NO_AIRTIME, cellphoneNumber)
                .put(Transaction.SERVICE_NETWRK_PROVIDER_NAME, networkProvider)
                .put(Transaction.SERVICE_BENEFICAIRY_FAVORITE, ALPHABET_N)
                .put(Transaction.SERVICE_NET_PROVIDER, institutionCode)
                .put(Transaction.SERVICE_MY_REFERENCE, BMBConstants.PASS_PREPAID.toLowerCase())
                .put(Transaction.SERVICE_IMAGE, imageName)
                .put(Transaction.SERVICE_IMAGE_NAME, imageName)
                .build()

        mockResponseFile = BeneficiariesMockFactory.editAirtimeConfirmation()
        printRequest()
    }

    @Suppress("UNCHECKED_CAST")
    override fun getResponseClass(): Class<T> = AddBeneficiaryResult::class.java as Class<T>
    override fun isEncrypted(): Boolean? = true
}
