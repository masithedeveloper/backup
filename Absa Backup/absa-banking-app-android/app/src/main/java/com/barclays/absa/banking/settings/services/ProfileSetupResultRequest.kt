/*
 *  Copyright (c) 2019 Absa Bank Limited, All Rights Reserved.
 *
 *  This code is confidential to Absa Bank Limited and shall not be disclosed
 *  outside the Bank without the prior written permission of the Absa Legal
 *
 *  In the event that such disclosure is permitted the code shall not be copied
 *  or distributed other than on a need-to-know basis and any recipients may be
 *  required to sign a confidentiality undertaking in favor of Absa Bank
 *  Limited
 *
 */
package com.barclays.absa.banking.settings.services

import com.barclays.absa.banking.boundary.model.ProfileSetupResult
import com.barclays.absa.banking.framework.ExtendedRequest
import com.barclays.absa.banking.framework.ExtendedResponseListener
import com.barclays.absa.banking.framework.api.request.params.OpCodeParams.OP0406_LANGUAGE_UPDATE
import com.barclays.absa.banking.framework.api.request.params.RequestParams
import com.barclays.absa.banking.framework.api.request.params.TransactionParams.Transaction
import com.barclays.absa.banking.framework.app.BMBConstants.*

class ProfileSetupResultRequest<T>(isPhotoRemove: Boolean, isUpdateProfile: Boolean, encryptedImage: String,
                                   languageCode: String, setupProfileName: String, backgroundImageId: String,
                                   responseListener: ExtendedResponseListener<T>) : ExtendedRequest<T>(responseListener) {
    init {
        val actionType = if (isUpdateProfile) SERVICE_ACTIONTYPE_UPDATE else SERVICE_ACTIONTYPE_ADD

        params = RequestParams.Builder()
                .put(OP0406_LANGUAGE_UPDATE)
                .put(Transaction.SERVICE_LANGUAGE_CODE, languageCode)
                .put(Transaction.PROFILE_SETUP_PROFILE_NAME, setupProfileName)
                .put(Transaction.PROFILE_SETUP_BACKGROUND_IMAGE_INDEX, backgroundImageId)
                .put(Transaction.PROFILE_SETUP_IMAGE_TYPE, PROFILE_SETUP_BOTH)
                .put(Transaction.PROFILE_SETUP_PROFILE_IMAGE, if (isPhotoRemove) "" else encryptedImage)
                .put(Transaction.SERVICE_BENEFICIARY_IMAGE_MIME_TYPE, MIME_TYPE_JPG)
                .put(Transaction.PROFILE_SETUP_ACTION, actionType)
                .build()

        mockResponseFile = "profile/op0406_profile_setup_background_image.json"
        printRequest()
    }

    @Suppress("UNCHECKED_CAST")
    override fun getResponseClass(): Class<T> = ProfileSetupResult::class.java as Class<T>
    override fun isEncrypted(): Boolean? = true
}
