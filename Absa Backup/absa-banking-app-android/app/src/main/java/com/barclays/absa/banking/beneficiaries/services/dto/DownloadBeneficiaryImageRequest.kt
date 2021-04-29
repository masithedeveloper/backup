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
package com.barclays.absa.banking.beneficiaries.services.dto

import com.barclays.absa.banking.beneficiaries.services.BeneficiariesMockFactory
import com.barclays.absa.banking.beneficiaries.services.BeneficiariesService.OP0401_BENEFICIARY_IMAGE_DOWNLOAD
import com.barclays.absa.banking.boundary.model.AddBeneficiaryObject
import com.barclays.absa.banking.framework.ExtendedRequest
import com.barclays.absa.banking.framework.ExtendedResponseListener
import com.barclays.absa.banking.framework.api.request.params.RequestParams
import com.barclays.absa.banking.framework.api.request.params.TransactionParams.Transaction
import com.barclays.absa.banking.framework.app.BMBApplication
import com.barclays.absa.banking.framework.app.BMBConstants
import com.barclays.absa.banking.framework.app.BMBConstants.MIME_TYPE_JPG

class DownloadBeneficiaryImageRequest<T>(beneficiaryId: String, beneficiaryType: String,
                                         timestamp: String, downloadImageListener: ExtendedResponseListener<T>)
    : ExtendedRequest<T>(downloadImageListener) {

    init {
        params = RequestParams.Builder()
                .put(OP0401_BENEFICIARY_IMAGE_DOWNLOAD)
                .put(Transaction.SERVICE_BENEFICIARY_ID, beneficiaryId)
                .put(Transaction.SERVICE_BENEFICIARY_TYPEE, beneficiaryType)
                .put(Transaction.SERVICE_BENEFICIARY_IMAGE_MIME_TYPE, MIME_TYPE_JPG)
                .put(Transaction.SERVICE_BENEFICIARY_IMAGE_TIMESTAMP, timestamp)
                .put(Transaction.SERVICE_CHANNEL_IND, BMBConstants.SMARTPHONE_CHANNEL_IND)
                .put(Transaction.I_VAL, BMBApplication.getInstance().iVal)
                .build()

        mockResponseFile = BeneficiariesMockFactory.beneficiaryImageDownload()
        printRequest()
    }

    @Suppress("UNCHECKED_CAST")
    override fun getResponseClass(): Class<T> = AddBeneficiaryObject::class.java as Class<T>
    override fun isEncrypted(): Boolean? = true
}
