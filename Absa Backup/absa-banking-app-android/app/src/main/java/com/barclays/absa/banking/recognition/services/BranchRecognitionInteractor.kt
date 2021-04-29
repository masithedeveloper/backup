/*
 * Copyright (c) 2019. Absa Bank Limited, All Rights Reserved.
 *
 * This code is confidential to Absa Bank Limited and shall not be disclosed
 * outside the Bank without the prior written permission of the Absa Legal
 *
 * In the event that such disclosure is permitted the code shall not be copied or distributed other than on a
 * need-to-know basis and any recipients may be required to sign a confidentiality undertaking in favor of Absa Bank Limited
 */

package com.barclays.absa.banking.recognition.services

import com.barclays.absa.banking.boundary.shared.dto.TransactionResponse
import com.barclays.absa.banking.framework.AbstractInteractor
import com.barclays.absa.banking.framework.ExtendedResponseListener
import com.barclays.absa.banking.recognition.services.dto.BranchRecognitionConfirmationRequest
import com.barclays.absa.banking.recognition.services.dto.BranchRecognitionRequest
import com.barclays.absa.banking.recognition.services.dto.BranchRecognitionService
import com.barclays.absa.banking.recognition.services.dto.models.BranchRecognitionConfirmation
import com.barclays.absa.banking.recognition.services.dto.models.BranchRecognitionResponse

class BranchRecognitionInteractor : AbstractInteractor(), BranchRecognitionService {

    override fun getFunctionsAndProducts(responseListener: ExtendedResponseListener<BranchRecognitionResponse?>) {
        submitRequest(BranchRecognitionRequest(responseListener), BranchRecognitionMockFactory.getFunctionsAndProducts())
    }

    override fun logTransactionAndSale(confirmation: BranchRecognitionConfirmation, recognitionExtendedResponseListener: ExtendedResponseListener<TransactionResponse>) {
        val confirmationRequest = BranchRecognitionConfirmationRequest(confirmation, recognitionExtendedResponseListener)
        submitRequest(confirmationRequest, BranchRecognitionMockFactory.getCustomerFeedbackResponse())
    }
}