/*
 * Copyright (c) 2021 Absa Bank Limited, All Rights Reserved.
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

package com.barclays.absa.banking.express.securityNotification.secondFactorEnrolStatus

import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import com.barclays.absa.banking.express.ExpressBaseViewModel
import com.barclays.absa.banking.express.securityNotification.secondFactorEnrolStatus.dto.SecondFactorEnrolStatusResponse
import kotlinx.coroutines.Dispatchers

class SecondFactorEnrolStatusViewModel : ExpressBaseViewModel() {
    lateinit var enrolStatusDetailsLiveData: LiveData<SecondFactorEnrolStatusResponse>
    override val repository by lazy { SecondFactorEnrolStatusRepository() }

    fun getSecondFactorEnrolStatus() {
        enrolStatusDetailsLiveData = liveData(Dispatchers.IO) {
            repository.getSecondFactorEnrolStatus()?.let { emit(it) }
        }
    }
}