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

package com.barclays.absa.banking.express.beneficiaries.addRegularBeneficiary

import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import com.barclays.absa.banking.express.ExpressBaseViewModel
import com.barclays.absa.banking.express.beneficiaries.addRegularBeneficiary.dto.AddBeneficiaryRequest
import com.barclays.absa.banking.express.beneficiaries.addRegularBeneficiary.dto.AddBeneficiaryResponse
import kotlinx.coroutines.Dispatchers

class AddRegularBeneficiaryViewModel : ExpressBaseViewModel() {

    override val repository by lazy { AddRegularBeneficiaryRepository() }

    lateinit var addBeneficiaryResponse: LiveData<AddBeneficiaryResponse>

    fun addBeneficiary(addBeneficiaryRequest: AddBeneficiaryRequest) {
        addBeneficiaryResponse = liveData(Dispatchers.IO) { repository.addBeneficiary(addBeneficiaryRequest)?.let { emit(it) } }
    }
}