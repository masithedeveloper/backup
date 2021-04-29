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
 */

package com.barclays.absa.banking.express.documentRequest.settlementQuote

import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import com.barclays.absa.banking.express.ExpressBaseViewModel
import kotlinx.coroutines.Dispatchers
import za.co.absa.networking.dto.BaseResponse

class AvafSettlementQuoteViewModel : ExpressBaseViewModel() {
    override val repository by lazy { AvafSettlementQuoteRepository() }

    lateinit var documentRequestRequestLiveData: LiveData<BaseResponse>

    fun requestDocument(email: String, account: String) {
        documentRequestRequestLiveData = liveData(Dispatchers.IO) {
            repository.submitDocumentRequest(email, account)?.let {
                emit(it)
            }
        }
    }
}