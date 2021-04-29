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

package com.barclays.absa.banking.businessBanking.ui

import android.content.Intent
import android.net.Uri
import androidx.activity.viewModels
import com.barclays.absa.banking.R
import com.barclays.absa.banking.boundary.model.UserProfile
import com.barclays.absa.banking.framework.BaseActivity
import com.barclays.absa.banking.framework.app.BMBApplication
import com.barclays.absa.banking.newToBank.NewToBankActivity
import com.barclays.absa.banking.newToBank.NewToBankConstants
import com.barclays.absa.banking.passcode.passcodeLogin.ExpressAuthenticationHelper
import com.barclays.absa.utils.AnalyticsUtil
import com.barclays.absa.utils.ProfileManager

class BusinessBankActivity : BaseActivity(R.layout.business_bank_activity) {

    private val businessBankingViewModel by viewModels<BusinessBankingViewModel>()

    companion object {
        const val CALLING_ACTIVITY = "calling_activity"
        const val SELECTED_PACKAGE = "card_package"
        const val PRODUCT_TYPE = "product_type"
    }

    fun logout() {
        BMBApplication.getInstance().logout()

        ProfileManager.getInstance().loadAllUserProfiles(object : ProfileManager.OnProfileLoadListener {
            override fun onAllProfilesLoaded(userProfiles: List<UserProfile>) {
                val activity = BMBApplication.getInstance().topMostActivity as BaseActivity
                val expressAuthenticationHelper = ExpressAuthenticationHelper(activity)
                expressAuthenticationHelper.performHello {
                    dismissProgressDialog()
                    goToNewToBankScreen()
                }
            }

            override fun onProfilesLoadFailed() {}
        })
    }

    fun goToNewToBankScreen() {
        startActivity(Intent(this, NewToBankActivity::class.java).apply {
            putExtra(CALLING_ACTIVITY, BusinessBankActivity::class.simpleName)
            putExtra(SELECTED_PACKAGE, businessBankingViewModel.selectedPackageMutableLiveData.value)
            putExtra(PRODUCT_TYPE, businessBankingViewModel.selectedProductType)
        })
    }

    fun navigateToInstantBusinessWebsite() {
        startActivityIfAvailable(Intent(Intent.ACTION_VIEW).setData(Uri.parse("https://www.absa.co.za")))
    }

    fun navigateToBusinessBankingApplicationFees() {
        startActivityIfAvailable(Intent(Intent.ACTION_VIEW).setData(Uri.parse("https://www.absa.co.za/rates-and-fees/business-banking/")))
    }

    fun trackSoleProprietorCurrentFragment(fragment: String) {
        AnalyticsUtil.trackAction(NewToBankConstants.SOLE_PROPRIETOR_ANALYTICS_CHANNEL, fragment)
    }
}