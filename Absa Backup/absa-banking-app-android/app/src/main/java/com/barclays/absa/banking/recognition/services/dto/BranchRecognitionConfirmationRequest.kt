/*
 * Copyright (c) 2019. Absa Bank Limited, All Rights Reserved.
 *
 * This code is confidential to Absa Bank Limited and shall not be disclosed
 * outside the Bank without the prior written permission of the Absa Legal
 *
 * In the event that such disclosure is permitted the code shall not be copied or distributed other than on a
 * need-to-know basis and any recipients may be required to sign a confidentiality undertaking in favor of Absa Bank Limited
 */
package com.barclays.absa.banking.recognition.services.dto

import com.barclays.absa.banking.boundary.shared.dto.TransactionResponse
import com.barclays.absa.banking.framework.ExtendedRequest
import com.barclays.absa.banking.framework.ExtendedResponseListener
import com.barclays.absa.banking.framework.api.request.params.RequestParams
import com.barclays.absa.banking.framework.api.request.params.TransactionParams
import com.barclays.absa.banking.recognition.services.BranchRecognitionMockFactory
import com.barclays.absa.banking.recognition.services.dto.BranchRecognitionService.Companion.OP2081_CUSTOMER_RATING_BRANCH_RECOGNITION
import com.barclays.absa.banking.recognition.services.dto.models.BranchRecognitionConfirmation
import com.barclays.absa.utils.DeviceUtils

class BranchRecognitionConfirmationRequest<T>(branchRecognition: BranchRecognitionConfirmation, feedbackExtendedResponseListener: ExtendedResponseListener<T>) : ExtendedRequest<T>(feedbackExtendedResponseListener) {

    init {
        mockResponseFile = BranchRecognitionMockFactory.getCustomerFeedbackResponse()

        val paramsBuilder = RequestParams.Builder()
                .put(OP2081_CUSTOMER_RATING_BRANCH_RECOGNITION)
                .put(BranchRecognitionService.QR_CODE, branchRecognition.qrCode)
                .put(BranchRecognitionService.FUNCTION_CODE, branchRecognition.functionCode)
                .put(TransactionParams.Transaction.SERVICE_CHANNEL_IND, DeviceUtils.channelId)
                .put(BranchRecognitionService.DIGITAL_IDENTITY, branchRecognition.digitalIdentity)

        if (!branchRecognition.productIdentifier.isNullOrBlank()) {
            paramsBuilder.put(BranchRecognitionService.PRODUCT_TYPE, branchRecognition.productType)
                    .put(BranchRecognitionService.PRODUCT_CODE, branchRecognition.productCode)
                    .put(BranchRecognitionService.PRODUCT_SUB_CODE, branchRecognition.productSubCode)
                    .put(BranchRecognitionService.PRODUCT_IDENTIFIER, branchRecognition.productIdentifier)
        }
        params = paramsBuilder.build()
        printRequest()
    }

    @Suppress("UNCHECKED_CAST")
    override fun getResponseClass(): Class<T> = TransactionResponse::class.java as Class<T>
    override fun isEncrypted(): Boolean = true
}