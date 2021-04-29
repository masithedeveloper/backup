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

import com.barclays.absa.banking.BuildConfig
import com.barclays.absa.banking.beneficiaries.services.BeneficiariesMockFactory
import com.barclays.absa.banking.beneficiaries.services.BeneficiariesService
import com.barclays.absa.banking.beneficiaries.services.BeneficiariesService.OP0402_BENEFICIARY_IMAGE_UPLOAD
import com.barclays.absa.banking.boundary.model.AddBeneficiaryObject
import com.barclays.absa.banking.boundary.model.BeneficiaryDetailObject
import com.barclays.absa.banking.boundary.model.airtime.AddedBeneficiary
import com.barclays.absa.banking.framework.BuildConfigHelper
import com.barclays.absa.banking.framework.ExtendedRequest
import com.barclays.absa.banking.framework.ExtendedResponseListener
import com.barclays.absa.banking.framework.api.request.params.RequestParams
import com.barclays.absa.banking.framework.api.request.params.TransactionParams.Transaction
import com.barclays.absa.banking.framework.app.BMBApplication
import com.barclays.absa.banking.framework.app.BMBConstants
import com.barclays.absa.banking.framework.app.BMBConstants.MIME_TYPE_JPG
import com.barclays.absa.banking.framework.app.BMBConstants.SERVICE_ACTIONTYPE_UPDATE
import java.text.SimpleDateFormat
import java.util.*

class UploadBeneficiaryImageRequest<T> : ExtendedRequest<T> {

    constructor(addBeneficiary: AddedBeneficiary, mimeType: String, actionType: String, responseListener: ExtendedResponseListener<T>)
            : super(BuildConfigHelper.serverImagePath, responseListener) {

        val dateFormat = SimpleDateFormat(BMBConstants.SERVICE_TIMESTAMP_FORMAT, Locale.ENGLISH)
        params = RequestParams.Builder()
                .put(OP0402_BENEFICIARY_IMAGE_UPLOAD)
                .put(Transaction.SERVICE_BENEFICIARY_ID, addBeneficiary.beneficiaryId)
                .put(Transaction.SERVICE_BENEFICIARY_TYPEE, addBeneficiary.beneficiaryType)
                .put(Transaction.SERVICE_BENEFICIARY_IMAGE_DATA, addBeneficiary.beneficiaryImage)
                .put(Transaction.SERVICE_BENEFICIARY_IMAGE_MIME_TYPE, mimeType)
                .put(Transaction.SERVICE_BENEFICIARY_IMAGE_TIMESTAMP, dateFormat.format(Date()))
                .put(Transaction.SERVICE_BENEFICIARY_IMAGE_ACTIONTYPE, actionType)
                .put(BeneficiariesService.SERVICE_ACTION, BeneficiariesService.UPDATE)
                .put(Transaction.SERVICE_CHANNEL_IND, BMBConstants.SMARTPHONE_CHANNEL_IND)
                .build()

        mockResponseFile = BeneficiariesMockFactory.uploadBeneficiaryImage()
        printRequest()
    }

    constructor(beneficiaryId: String, beneficiaryType: String, base64Image: String, mimeType: String, actionType: String, responseListener: ExtendedResponseListener<T>)
            : super(BuildConfigHelper.serverImagePath, responseListener) {

        val dateFormat = SimpleDateFormat(BMBConstants.SERVICE_TIMESTAMP_FORMAT, Locale.ENGLISH)
        params = RequestParams.Builder()
                .put(OP0402_BENEFICIARY_IMAGE_UPLOAD)
                .put(Transaction.SERVICE_BENEFICIARY_ID, beneficiaryId)
                .put(Transaction.SERVICE_BENEFICIARY_TYPEE, beneficiaryType)
                .put(Transaction.SERVICE_BENEFICIARY_IMAGE_DATA, base64Image)
                .put(Transaction.SERVICE_BENEFICIARY_IMAGE_MIME_TYPE, mimeType)
                .put(Transaction.SERVICE_BENEFICIARY_IMAGE_TIMESTAMP, dateFormat.format(Date()))
                .put(Transaction.SERVICE_BENEFICIARY_IMAGE_ACTIONTYPE, actionType)
                .put(BeneficiariesService.SERVICE_ACTION, BeneficiariesService.UPDATE)
                .put(Transaction.SERVICE_CHANNEL_IND, BMBConstants.SMARTPHONE_CHANNEL_IND)
                .build()

        mockResponseFile = BeneficiariesMockFactory.uploadBeneficiaryImage()
        printRequest()
    }

    constructor(beneficiaryDetail: BeneficiaryDetailObject, responseListener: ExtendedResponseListener<T>)
            : super(BuildConfigHelper.serverImagePath, responseListener) {

        val dateFormat = SimpleDateFormat(BMBConstants.SERVICE_TIMESTAMP_FORMAT, BMBApplication.getApplicationLocale())
        params = RequestParams.Builder()
                .put(OP0402_BENEFICIARY_IMAGE_UPLOAD)
                .put(Transaction.SERVICE_BENEFICIARY_ID, beneficiaryDetail.beneficiaryId)
                .put(Transaction.SERVICE_BENEFICIARY_TYPEE, BMBConstants.PASS_PREPAID_ELECTRICITY)
                .put(Transaction.SERVICE_BENEFICIARY_IMAGE_DATA, beneficiaryDetail.benImage)
                .put(Transaction.SERVICE_BENEFICIARY_IMAGE_MIME_TYPE, MIME_TYPE_JPG)
                .put(Transaction.SERVICE_BENEFICIARY_IMAGE_TIMESTAMP, dateFormat.format(Date()))
                .put(Transaction.SERVICE_BENEFICIARY_IMAGE_ACTIONTYPE, SERVICE_ACTIONTYPE_UPDATE)
                .put(BeneficiariesService.SERVICE_ACTION, BeneficiariesService.UPDATE)
                .put(Transaction.SERVICE_CHANNEL_IND, BMBConstants.SMARTPHONE_CHANNEL_IND)
                .build()

        mockResponseFile = BeneficiariesMockFactory.uploadBeneficiaryImage()
        printRequest()
    }

    @Suppress("UNCHECKED_CAST")
    override fun getResponseClass(): Class<T> = AddBeneficiaryObject::class.java as Class<T>

    override fun isEncrypted(): Boolean? = true
}
