/*
 * Copyright (c) 2019 Absa Bank Limited, All Rights Reserved.
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

package com.barclays.absa.banking.presentation.shared

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import com.barclays.absa.banking.boundary.shared.dto.TransactionResponse
import com.barclays.absa.banking.framework.data.ResponseObject

open class BaseViewModel : ViewModel() {
    var failureResponse = MutableLiveData<TransactionResponse>()
    var successResponse = MutableLiveData<TransactionResponse>()

    fun notifyFailure(response: ResponseObject) {
        failureResponse.value = response as TransactionResponse
    }
}

fun <T> MutableLiveData<T>.observeWithReset(lifecycleOwner: LifecycleOwner, observer: Observer<T>) {
    removeObservers(lifecycleOwner)
    this.value = null
    observe(lifecycleOwner, observer)
}

fun <T> MutableLiveData<T>.resetValue(owner: LifecycleOwner) {
    removeObservers(owner)
    this.value = null
}

fun resetLiveDataValues(owner: LifecycleOwner, vararg liveDatas: MutableLiveData<*>) {
    for (liveData in liveDatas) {
        liveData.resetValue(owner)
    }
}