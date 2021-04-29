/*
 * Copyright (c) 2019. Absa Bank Limited, All Rights Reserved.
 *
 * This code is confidential to Absa Bank Limited and shall not be disclosed
 * outside the Bank without the prior written permission of the Absa Legal
 *
 * In the event that such disclosure is permitted the code shall not be copied or distributed other than on a
 * need-to-know basis and any recipients may be required to sign a confidentiality undertaking in favor of Absa Bank Limited
 */

package com.barclays.absa.banking.recognition.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.barclays.absa.banking.framework.app.BMBConstants
import com.barclays.absa.banking.recognition.services.BranchRecognitionRepository
import com.barclays.absa.banking.recognition.services.BranchRecognitionRepositoryImpl
import com.barclays.absa.banking.recognition.services.dto.models.BranchRecognitionConfirmation
import com.barclays.absa.banking.recognition.services.dto.models.BranchRecognitionResponse

class BranchRecognitionActivityViewModel : ViewModel() {

    var branchRecognitionRepository: BranchRecognitionRepository = BranchRecognitionRepositoryImpl()
    var branchRecognitionResponse: LiveData<BranchRecognitionResponse>? = null
    var selectedIndex = -1
    var justReturnedFromSettings = false

    fun provideInitialData(): LiveData<BranchRecognitionResponse>? {
        if (branchRecognitionResponse?.value == null) {
            branchRecognitionResponse = branchRecognitionRepository.getRecognitionMenuInfo()
        }
        return branchRecognitionResponse
    }

    fun uploadBranchData(feedback: BranchRecognitionConfirmation) {
        branchRecognitionRepository.uploadBranchFeedback(feedback)
    }

    fun validateResponse(response: BranchRecognitionResponse?): Boolean {
        if (BMBConstants.FAILURE.equals(response?.transactionStatus, ignoreCase = true) || BMBConstants.REJECTED.equals(response?.transactionStatus, ignoreCase = true) || BMBConstants.FAILED.equals(response?.transactionStatus, ignoreCase = true)) {
            return false
        }
        return true
    }
}