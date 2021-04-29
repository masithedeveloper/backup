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
package com.barclays.absa.utils

interface IBannerManager {
    fun incrementBannerViews()

    fun hidePersonalLoanBanner()
    fun hideCovidBanner()
    fun hideSolidarityBanner()

    fun shouldShowPersonalLoanBanner(): Boolean
    fun shouldShowCovidBanner(): Boolean
    fun shouldShowSolidarityBanner(): Boolean

    fun getLargeBannerShownCount():Int
    fun incrementLargeBannerShownCount()

    fun shouldShowAvafBanner():Boolean
    fun incrementAvafBannerViews()
}