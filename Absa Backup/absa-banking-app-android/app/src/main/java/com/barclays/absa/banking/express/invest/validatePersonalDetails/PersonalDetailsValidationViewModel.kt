/*
 * Copyright (c) 2020 Absa Bank Limited, All Rights Reserved.
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

package com.barclays.absa.banking.express.invest.validatePersonalDetails

import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import com.barclays.absa.banking.express.ExpressBaseViewModel
import com.barclays.absa.banking.express.invest.validatePersonalDetails.dto.PersonalDetailsValidationRequest
import com.barclays.absa.banking.express.invest.validatePersonalDetails.dto.PersonalDetailsValidationResponse
import kotlinx.coroutines.Dispatchers

class PersonalDetailsValidationViewModel : ExpressBaseViewModel() {
    lateinit var personalDetailsValidationResultLiveData: LiveData<PersonalDetailsValidationResponse>
    override val repository by lazy { PersonalDetailsValidationRepository() }

    fun validatePersonalDetails(personalDetailsValidationRequest: PersonalDetailsValidationRequest) {
        personalDetailsValidationResultLiveData = liveData(Dispatchers.IO) {
            repository.validatePersonalDetails(personalDetailsValidationRequest)?.let { emit(it) }
        }
    }
}