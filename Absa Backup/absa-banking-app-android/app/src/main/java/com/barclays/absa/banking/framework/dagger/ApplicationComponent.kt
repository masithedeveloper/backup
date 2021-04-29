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
import com.barclays.absa.banking.framework.data.cache.IOverdraftCacheService
import com.barclays.absa.banking.framework.data.cache.IRewardsCacheService
import com.barclays.absa.banking.framework.services.contracts.IAppCacheService
import com.barclays.absa.banking.home.ui.IHomeCacheService
import com.barclays.absa.banking.payments.international.services.IInternationalPaymentCacheService
import com.barclays.absa.utils.IAbsaCacheService
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [ServiceModule::class])
interface ApplicationComponent {

    // Services
    fun getAppCacheService(): IAppCacheService
    fun getOverdraftCacheService(): IOverdraftCacheService
    fun getInternationalPaymentCacheService(): IInternationalPaymentCacheService
    fun getHomeCacheService(): IHomeCacheService
    fun getRewardsCacheService(): IRewardsCacheService
    fun getAbsaCacheService(): IAbsaCacheService
    fun getBeneficiaryCacheService(): IBeneficiaryCacheService
}