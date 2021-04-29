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

package com.barclays.absa.banking.express.invest.getProductInterestRate

import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import com.barclays.absa.banking.express.ExpressBaseViewModel
import com.barclays.absa.banking.express.invest.getProductInterestRate.dto.InterestRateDetails
import com.barclays.absa.banking.express.invest.getProductInterestRate.dto.ProductInterestRateRequest
import kotlinx.coroutines.Dispatchers

class ProductProfileInterestRateViewModel : ExpressBaseViewModel() {

    lateinit var productProfileInterestRatesLiveData: LiveData<List<InterestRateDetails>>

    override lateinit var repository: ProductProfileInterestRateRepository

    fun fetchProductProfileInterestRates(productInterestRateRequest: ProductInterestRateRequest) {
        repository = ProductProfileInterestRateRepository(productInterestRateRequest)
        productProfileInterestRatesLiveData = liveData(Dispatchers.IO) {
            repository.fetchProductProfileInterestRates()?.let { emit(it.interestRates) }
        }
    }
}