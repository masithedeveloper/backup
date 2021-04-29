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

package com.barclays.absa.banking.express.hello

import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import com.barclays.absa.banking.express.ExpressBaseViewModel
import com.barclays.absa.banking.express.hello.dto.HelloResponse
import com.barclays.absa.banking.framework.ExtendedResponseListener
import com.barclays.absa.banking.presentation.launch.versionCheck.IValNValInteractor
import com.barclays.absa.banking.presentation.launch.versionCheck.dto.IValNValExtendedResponseListener
import com.barclays.absa.banking.presentation.launch.versionCheck.dto.IValNValResponse
import kotlinx.coroutines.Dispatchers

class HelloViewModel : ExpressBaseViewModel() {

    override val repository by lazy { HelloRepository() }

    lateinit var helloLiveData: LiveData<HelloResponse>

    private val iValNValResponseListener: ExtendedResponseListener<IValNValResponse> by lazy { IValNValExtendedResponseListener() }

    fun performHello() {
        helloLiveData = liveData(Dispatchers.IO) { repository.submitRequest<HelloResponse>()?.let { emit(it) } }
    }

    fun fetchBMGIValAndNVal() {
        IValNValInteractor().fetchIValAndNVal(iValNValResponseListener)
    }
}