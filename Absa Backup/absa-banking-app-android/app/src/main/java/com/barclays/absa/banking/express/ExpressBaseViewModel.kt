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
package com.barclays.absa.banking.express

import androidx.lifecycle.*
import za.co.absa.networking.dto.ResponseHeader
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

abstract class ExpressBaseViewModel : ViewModel() {
    abstract val repository: Repository

    val failureLiveData: MutableLiveData<ResponseHeader>
        get() = repository.failureLiveData

    fun <T> longLiveData(context: CoroutineContext = EmptyCoroutineContext, block: suspend LiveDataScope<T>.() -> Unit): LiveData<T> = liveData(context, 60000L, block)
}