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

object BannerManager : IBannerManager {

    private const val AVAF_BANNER = "avafBanner"
    private const val COVID_BANNER = "covidBanner"
    private const val SOLIDARITY_BANNER = "solidarityBanner"
    private const val PERSONAL_LOAN_BANNER = "personalLoanBanner"
    private const val BENEFICIARY_SWITCHING_LARGE_BANNER_SHOWN_COUNT = "numberOfTimesLargeBannerHasBeenShownInBeneficiarySwitching"

    // SharedPreferenceService will later get injected into this module
    private val sharedPreferenceService: ISharedPreferenceService = SharedPreferenceService

    override fun incrementBannerViews() {
        incrementPersonalLoanBannerViews()
        incrementCovidBannerViews()
        incrementSolidarityBannerViews()
    }

    override fun hidePersonalLoanBanner() = hideBanner(PERSONAL_LOAN_BANNER)
    override fun hideCovidBanner() = hideBanner(COVID_BANNER)
    override fun hideSolidarityBanner() = hideBanner(SOLIDARITY_BANNER)

    override fun shouldShowPersonalLoanBanner(): Boolean = shouldShowBanner(PERSONAL_LOAN_BANNER)
    override fun shouldShowCovidBanner(): Boolean = shouldShowBanner(COVID_BANNER)
    override fun shouldShowSolidarityBanner(): Boolean = shouldShowBanner(SOLIDARITY_BANNER)

    override fun getLargeBannerShownCount(): Int = sharedPreferenceService.getBannerCount(BENEFICIARY_SWITCHING_LARGE_BANNER_SHOWN_COUNT)

    override fun incrementLargeBannerShownCount() {
        val largeBannerShownCount = getLargeBannerShownCount()
        sharedPreferenceService.setBannerCount(BENEFICIARY_SWITCHING_LARGE_BANNER_SHOWN_COUNT, largeBannerShownCount + 1)
    }

    override fun shouldShowAvafBanner(): Boolean = shouldShowBanner(AVAF_BANNER)
    override fun incrementAvafBannerViews() = incrementBannerViews(AVAF_BANNER)

    private fun shouldShowBanner(bannerName: String): Boolean {
        val numberOfBannersShown = sharedPreferenceService.getBannerCount(bannerName)
        return numberOfBannersShown < 4
    }

    private fun incrementCovidBannerViews() = incrementBannerViews(COVID_BANNER)
    private fun incrementPersonalLoanBannerViews() = incrementBannerViews(PERSONAL_LOAN_BANNER)
    private fun incrementSolidarityBannerViews() = incrementBannerViews(SOLIDARITY_BANNER)

    private fun hideBanner(bannerName: String) = sharedPreferenceService.setBannerCount(bannerName, 4)

    private fun incrementBannerViews(bannerName: String) {
        val numberOfBannersShown = sharedPreferenceService.getBannerCount(bannerName)
        if (numberOfBannersShown <= 3) {
            sharedPreferenceService.setBannerCount(bannerName, numberOfBannersShown + 1)
        }
    }
}