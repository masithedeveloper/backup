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

package com.barclays.absa.banking.express.verify

import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import com.barclays.absa.banking.express.ExpressBaseViewModel
import kotlinx.coroutines.Dispatchers
import za.co.absa.networking.dto.BaseResponse

class VerifyViewModel : ExpressBaseViewModel() {

    override val repository by lazy { VerifyRepository() }

    lateinit var verifyLiveData: LiveData<BaseResponse>

    fun performVerify() {
        verifyLiveData = liveData(Dispatchers.IO) {
            repository.submitRequest<BaseResponse>()?.let { emit(it) }
        }
    }
}