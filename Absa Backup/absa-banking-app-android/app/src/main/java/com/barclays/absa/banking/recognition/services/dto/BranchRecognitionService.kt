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
import com.barclays.absa.banking.framework.ExtendedResponseListener
import com.barclays.absa.banking.recognition.services.dto.models.BranchRecognitionConfirmation
import com.barclays.absa.banking.recognition.services.dto.models.BranchRecognitionResponse

interface BranchRecognitionService {
    companion object {
        const val OP2080_CUSTOMER_RATING_GET_FUNCTIONS_AND_PRODUCTS = "OP2080"
        const val OP2081_CUSTOMER_RATING_BRANCH_RECOGNITION = "OP2081"

        const val QR_CODE = "qrCode"
        const val FUNCTION_CODE = "functionCode"
        const val DIGITAL_IDENTITY = "digitalIdentity"
        const val PRODUCT_TYPE = "productType"
        const val PRODUCT_CODE = "productCode"
        const val PRODUCT_SUB_CODE = "productSubCode"
        const val PRODUCT_IDENTIFIER = "productIdentifier"
    }

    fun getFunctionsAndProducts(responseListener: ExtendedResponseListener<BranchRecognitionResponse?>)
    fun logTransactionAndSale(confirmation: BranchRecognitionConfirmation, recognitionExtendedResponseListener: ExtendedResponseListener<TransactionResponse>)
}