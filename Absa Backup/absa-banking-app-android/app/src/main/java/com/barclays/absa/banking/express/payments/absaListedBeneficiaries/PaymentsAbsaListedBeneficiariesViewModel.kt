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

package com.barclays.absa.banking.express.payments.absaListedBeneficiaries

import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import com.barclays.absa.banking.express.ExpressBaseViewModel
import com.barclays.absa.banking.express.payments.absaListedBeneficiaries.dto.PaymentsAbsaListedBeneficiariesResponse
import kotlinx.coroutines.Dispatchers

class PaymentsAbsaListedBeneficiariesViewModel : ExpressBaseViewModel() {

    override val repository by lazy { PaymentsAbsaListedBeneficiariesRepository() }

    lateinit var absaListedBeneficiariesResponse: LiveData<PaymentsAbsaListedBeneficiariesResponse>

    fun isAbsaListedBeneficiariesInitialised(): Boolean = ::absaListedBeneficiariesResponse.isInitialized

    fun fetchAbsaListedBeneficiaries(searchString: String = "") {
        absaListedBeneficiariesResponse = liveData(Dispatchers.IO) { repository.fetchAbsaListedBeneficiaries(searchString)?.let { emit(it) } }
    }
}