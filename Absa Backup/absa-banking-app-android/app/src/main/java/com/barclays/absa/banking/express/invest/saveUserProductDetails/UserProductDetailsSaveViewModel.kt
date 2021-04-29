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

package com.barclays.absa.banking.express.invest.saveUserProductDetails

import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import com.barclays.absa.banking.express.ExpressBaseViewModel
import com.barclays.absa.banking.express.invest.saveUserProductDetails.dto.UserDetails
import com.barclays.absa.banking.express.invest.saveUserProductDetails.dto.UserProductDetailsSaveRequest
import kotlinx.coroutines.Dispatchers

class UserProductDetailsSaveViewModel : ExpressBaseViewModel() {
    lateinit var saveUserProductDetailsLiveData: LiveData<UserDetails>
    override val repository by lazy { UserProductDetailsSaveRepository() }

    fun saveUserProductDetails(userProductDetailsSaveRequest: UserProductDetailsSaveRequest) {
        saveUserProductDetailsLiveData = liveData(Dispatchers.IO) {
            repository.saveUserProductDetails(userProductDetailsSaveRequest)?.let { emit(it.userDetails) }
        }
    }
}