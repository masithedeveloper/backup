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
package com.barclays.absa.banking.home.services.dto

import com.barclays.absa.banking.boundary.model.AddBeneficiaryObject
import com.barclays.absa.banking.framework.BuildConfigHelper
import com.barclays.absa.banking.framework.ExtendedRequest
import com.barclays.absa.banking.framework.ExtendedResponseListener
import com.barclays.absa.banking.framework.api.request.params.OpCodeParams.OP0996_GET_PROFILE_IMAGE
import com.barclays.absa.banking.framework.api.request.params.RequestParams
import com.barclays.absa.banking.framework.api.request.params.TransactionParams.Transaction
import com.barclays.absa.banking.framework.app.BMBApplication
import com.barclays.absa.banking.framework.app.BMBConstants

class ProfileImageRequest<T>(timestamp: String, mimeType: String, profileImageResponseListener: ExtendedResponseListener<T>)
    : ExtendedRequest<T>(BuildConfigHelper.serverImagePath, profileImageResponseListener) {

    init {
        params = RequestParams.Builder()
                .put(OP0996_GET_PROFILE_IMAGE)
                .put(Transaction.SERVICE_BENEFICIARY_IMAGE_MIME_TYPE, mimeType)
                .put(Transaction.SERVICE_BENEFICIARY_IMAGE_TIMESTAMP, timestamp)
                .put(Transaction.SERVICE_CHANNEL_IND, BMBConstants.SMARTPHONE_CHANNEL_IND)
                .put(Transaction.I_VAL, BMBApplication.getInstance().iVal)
                .build()

        mockResponseFile = "profile/op0996_get_profile_image.json"
        printRequest()
    }

    @Suppress("UNCHECKED_CAST")
    override fun getResponseClass(): Class<T> = AddBeneficiaryObject::class.java as Class<T>
    override fun isEncrypted(): Boolean? = true
}
