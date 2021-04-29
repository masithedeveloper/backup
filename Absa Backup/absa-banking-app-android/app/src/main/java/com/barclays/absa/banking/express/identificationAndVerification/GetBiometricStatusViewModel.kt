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
 */

package com.barclays.absa.banking.express.identificationAndVerification

import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import com.barclays.absa.banking.express.ExpressBaseViewModel
import com.barclays.absa.banking.express.identificationAndVerification.dto.BiometricStatusResponse
import com.barclays.absa.banking.linking.ui.LinkingIdType
import kotlinx.coroutines.Dispatchers

class GetBiometricStatusViewModel : ExpressBaseViewModel() {

    override val repository by lazy { GetBiometricStatusRepository() }

    lateinit var biometricStatusLiveData: LiveData<BiometricStatusResponse>

    fun fetchBiometricStatus(idType: LinkingIdType, idNumber: String) {
        biometricStatusLiveData = liveData(Dispatchers.IO) { repository.fetchBiometricStatus(idType, idNumber)?.let { emit(it) } }
    }
}