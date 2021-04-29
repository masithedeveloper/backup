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
package com.barclays.absa.banking.framework.dagger

import com.barclays.absa.banking.beneficiaries.services.IBeneficiaryCacheService
import com.barclays.absa.banking.framework.app.BMBApplication
import com.barclays.absa.banking.framework.data.cache.IOverdraftCacheService
import com.barclays.absa.banking.framework.data.cache.IRewardsCacheService
import com.barclays.absa.banking.framework.services.contracts.IAppCacheService
import com.barclays.absa.banking.home.ui.IHomeCacheService
import com.barclays.absa.banking.payments.international.services.IInternationalPaymentCacheService
import com.barclays.absa.utils.IAbsaCacheService


@Deprecated("This is currently used everywhere, but should gradually be worked out as services should be injected straight into managers / viewModels and then consumed from there.")
inline fun <reified T> getServiceInterface(): T = getServiceInterface(T::class.java)

// todo remove this function once there is no java files consuming the method and do the implementation in the inline method above.
@Deprecated("This is currently used everywhere, but should gradually be worked out as services should be injected straight into managers / viewModels and then consumed from there.")
fun <T> getServiceInterface(classType: Class<T>): T {
    val applicationComponent = BMBApplication.applicationComponent
    return when (classType) {
        IAppCacheService::class.java -> applicationComponent.getAppCacheService() as T
        IOverdraftCacheService::class.java -> applicationComponent.getOverdraftCacheService() as T
        IInternationalPaymentCacheService::class.java -> applicationComponent.getInternationalPaymentCacheService() as T
        IHomeCacheService::class.java -> applicationComponent.getHomeCacheService() as T
        IRewardsCacheService::class.java -> applicationComponent.getRewardsCacheService() as T
        IAbsaCacheService::class.java -> applicationComponent.getAbsaCacheService() as T
        IBeneficiaryCacheService::class.java -> applicationComponent.getBeneficiaryCacheService() as T
        else -> throw RuntimeException("Please only request services that are setup for dagger")
    }
}