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

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.barclays.absa.banking.recognition.services.dto.models.BranchRecognitionResponse
import com.barclays.absa.banking.recognition.services.dto.models.BranchRecognitionConfirmation
import com.barclays.absa.banking.boundary.shared.dto.TransactionResponse
import com.barclays.absa.banking.framework.ExtendedResponseListener

class BranchRecognitionRepositoryImpl : BranchRecognitionRepository {

    override fun getRecognitionMenuInfo(): LiveData<BranchRecognitionResponse> {
        val coreData = MutableLiveData<BranchRecognitionResponse>()
        BranchRecognitionInteractor().getFunctionsAndProducts(object : ExtendedResponseListener<BranchRecognitionResponse?>() {
            override fun onSuccess(successResponse: BranchRecognitionResponse?) {
                coreData.value = successResponse
            }
        })
        return coreData
    }

    override fun uploadBranchFeedback(branchFeedback: BranchRecognitionConfirmation) {
        BranchRecognitionInteractor().logTransactionAndSale(branchFeedback, object : ExtendedResponseListener<TransactionResponse>() {
            override fun onSuccess(successResponse: TransactionResponse?) {
                super.onSuccess()
            }
        })
    }
}