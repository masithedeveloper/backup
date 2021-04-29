package com.barclays.absa.banking.express.documentRequest.paidupLetter

import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import com.barclays.absa.banking.express.ExpressBaseViewModel
import kotlinx.coroutines.Dispatchers
import za.co.absa.networking.dto.BaseResponse

class AvafPaidupLetterViewModel : ExpressBaseViewModel() {
    override val repository by lazy { AvafPaidUpLetterRepository() }

    lateinit var documentRequestRequestLiveData: LiveData<BaseResponse>

    fun requestDocument(email: String, account: String) {
        documentRequestRequestLiveData = liveData(Dispatchers.IO) {
            repository.submitDocumentRequest(email, account)?.let {
                emit(it)
            }
        }
    }
}