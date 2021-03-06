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
package com.barclays.absa.banking.express.cashSend.validateCashSend

import androidx.lifecycle.MutableLiveData
import com.barclays.absa.banking.express.ExpressBaseHttpFormViewModel
import com.barclays.absa.banking.express.cashSend.validateCashSend.dto.CashSendValidationResponse
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class CashSendValidationViewModel : ExpressBaseHttpFormViewModel() {
    override val repository by lazy { CashSendValidationRepository() }
    val cashSendValidationResponseLiveData = MutableLiveData<CashSendValidationResponse>()

    fun validateCashSend(cashSendValidationDataModel: CashSendValidationDataModel) {
        CoroutineScope(Dispatchers.IO).launch {
            repository.validateCashSend(cashSendValidationDataModel)?.let { cashSendValidationResponseLiveData.postValue(it) }
        }
    }
}