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

package com.barclays.absa.banking.express.limits.getUserLimits

import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import com.barclays.absa.banking.express.ExpressBaseViewModel
import com.barclays.absa.banking.express.limits.getUserLimits.dto.GetUserLimitsResponse
import kotlinx.coroutines.Dispatchers

class GetUserLimitsViewModel : ExpressBaseViewModel() {

    override val repository by lazy { GetUserLimitsRepository() }

    lateinit var userLimitsResponse: LiveData<GetUserLimitsResponse>

    fun getUserLimits() {
        userLimitsResponse = liveData(Dispatchers.IO) { repository.submitRequest<GetUserLimitsResponse>()?.let { emit(it) } }
    }
}