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

import android.app.Application
import com.barclays.absa.banking.beneficiaries.services.BeneficiaryCacheService
import com.barclays.absa.banking.beneficiaries.services.IBeneficiaryCacheService
import com.barclays.absa.banking.framework.data.cache.IOverdraftCacheService
import com.barclays.absa.banking.framework.data.cache.IRewardsCacheService
import com.barclays.absa.banking.framework.data.cache.OverdraftCacheService
import com.barclays.absa.banking.framework.data.cache.RewardsCacheService
import com.barclays.absa.banking.framework.services.AppCacheService
import com.barclays.absa.banking.framework.services.contracts.IAppCacheService
import com.barclays.absa.banking.home.ui.HomeCacheService
import com.barclays.absa.banking.home.ui.IHomeCacheService
import com.barclays.absa.banking.payments.international.services.IInternationalPaymentCacheService
import com.barclays.absa.banking.payments.international.services.InternationalPaymentCacheService
import com.barclays.absa.utils.AbsaCacheService
import com.barclays.absa.utils.IAbsaCacheService
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class ServiceModule(private val application: Application) {

    @Singleton
    @Provides
    fun getAppCacheService(): IAppCacheService = AppCacheService()

    @Singleton
    @Provides
    fun getOverdraftCacheService(): IOverdraftCacheService = OverdraftCacheService()

    @Singleton
    @Provides
    fun getInternationalCacheService(): IInternationalPaymentCacheService = InternationalPaymentCacheService()

    @Singleton
    @Provides
    fun getHomeCacheService(): IHomeCacheService = HomeCacheService()

    @Singleton
    @Provides
    fun getRewardsCacheService() : IRewardsCacheService = RewardsCacheService()

    @Singleton
    @Provides
    fun getAbsaCacheService() : IAbsaCacheService = AbsaCacheService()

    @Singleton
    @Provides
    fun getBeneficiaryCacheService() : IBeneficiaryCacheService = BeneficiaryCacheService()
}