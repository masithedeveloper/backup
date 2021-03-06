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

package com.barclays.absa.banking.express.ping

import android.os.Handler
import android.os.Looper
import androidx.lifecycle.viewModelScope
import com.barclays.absa.banking.express.ExpressBaseViewModel
import com.barclays.absa.banking.framework.app.BMBApplication
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.launch
import za.co.absa.networking.dto.BaseResponse

class PingViewModel : ExpressBaseViewModel() {

    override val repository by lazy { PingRepository() }

    fun pingKeepAlive() {
        viewModelScope.launch {
            if (BMBApplication.getInstance().userLoggedInStatus) {
                val submitRequest = repository.submitRequest<BaseResponse>()
                if (submitRequest == null) {
                    BMBApplication.getInstance().forceSignOut()
                }
            }

            val ninetySeconds: Long = 90000
            Handler(Looper.getMainLooper()).postDelayed({ viewModelScope.coroutineContext.cancelChildren() }, ninetySeconds)
        }
    }
}