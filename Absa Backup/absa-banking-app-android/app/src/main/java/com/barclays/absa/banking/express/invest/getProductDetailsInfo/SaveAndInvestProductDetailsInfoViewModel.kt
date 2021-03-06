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

package com.barclays.absa.banking.express.invest.getProductDetailsInfo

import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import com.barclays.absa.banking.express.ExpressBaseViewModel
import com.barclays.absa.banking.express.Repository
import com.barclays.absa.banking.express.invest.getProductDetailsInfo.dto.ProductAndCreditRatePlanCode
import com.barclays.absa.banking.express.invest.getProductDetailsInfo.dto.ProductDetailsInfoResponse
import com.barclays.absa.banking.saveAndInvest.SaveAndInvestProductType
import kotlinx.coroutines.Dispatchers

class SaveAndInvestProductDetailsInfoViewModel : ExpressBaseViewModel() {
    lateinit var productDetailsInfoLiveData: LiveData<ProductDetailsInfoResponse>
    override val repository by lazy { SaveAndInvestProductDetailsInfoRepository() }

    fun fetchProductDetailsInfo(saveAndInvestProductType: SaveAndInvestProductType) {
        productDetailsInfoLiveData = liveData(Dispatchers.IO) { repository.fetchProductDetailsInfo(saveAndInvestProductType)?.let { emit(it) } }
    }
}