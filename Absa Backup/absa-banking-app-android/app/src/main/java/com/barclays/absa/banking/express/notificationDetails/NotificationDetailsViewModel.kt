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

package com.barclays.absa.banking.express.notificationDetails

import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import com.barclays.absa.banking.express.ExpressBaseViewModel
import com.barclays.absa.banking.express.notificationDetails.dto.NotificationDetailsResponse
import com.barclays.absa.banking.framework.dagger.getServiceInterface
import com.barclays.absa.banking.framework.services.contracts.IAppCacheService
import kotlinx.coroutines.Dispatchers

class NotificationDetailsViewModel : ExpressBaseViewModel() {
    override val repository by lazy { NotificationDetailsRepository() }
    val appCacheService: IAppCacheService = getServiceInterface()
    lateinit var notificationDetailsLiveData: LiveData<NotificationDetailsResponse>

    fun fetchNotificationDetails(accountNumber: String, userNumber: String) {
        notificationDetailsLiveData = liveData(Dispatchers.IO) {
            val cachedDetail = appCacheService.getNotificationDetails()
            if (cachedDetail == null) {
                repository.fetchNotificationDetails(accountNumber, userNumber)?.let {
                    appCacheService.setNotificationDetails(it)
                    emit(it)
                }
            } else {
                emit(cachedDetail)
            }
        }
    }
}