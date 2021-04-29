/*
 *  Copyright (c) 2020 Absa Bank Limited, All Rights Reserved.
 *
 *  This code is confidential to Absa Bank Limited and shall not be disclosed
 *  outside the Bank without the prior written permission of the Absa Legal
 *
 *  In the event that such disclosure is permitted the code shall not be copied
 *  or distributed other than on a need-to-know basis and any recipients may be
 *  required to sign a confidentiality undertaking in favor of Absa Bank
 *  Limited
 *
 */
package com.barclays.absa.banking.framework

import android.content.DialogInterface
import android.content.Intent
import com.barclays.absa.banking.express.getAllBalances.CacheHeader
import com.barclays.absa.banking.framework.utils.AlertInfo
import com.barclays.absa.banking.home.services.HomeScreenService
import com.barclays.absa.integration.DeviceProfilingInteractor
import za.co.absa.networking.error.ApplicationError

interface BaseView {
    fun showNoPrimaryDeviceScreen()
    fun showMessageError(message: String?)
    fun showMessageError(error: ApplicationError)
    fun checkDeviceState()
    fun showGenericErrorMessageThenFinish()
    fun showMessage(title: String?, message: String?, onDismissListener: DialogInterface.OnClickListener?)
    fun showGenericErrorMessage()
    fun showCustomAlertDialog(alertInfo: AlertInfo)
    fun navigateToPreviousDevicePasscodeEntryScreen()
    fun navigateToHomeScreenAndShowAccountsList()
    fun navigateToHomeScreenWithoutReloadingAccounts()
    fun loadAccountsClearingAccountProfileAndShowHomeScreen()
    fun loadAccountsClearingAccountProfileAndGoHomeWithShortcut(shortcutId: String)
    fun loadAccountsClearingRewardsBalanceAndShowHomeScreen()
    fun loadAccountsAndShowHomeScreenWithAccountsList()
    val isBusinessAccount: Boolean
    val isSouthAfricanResident: Boolean
    val isSoleProprietor: Boolean
    val isSwiftAllowedForClientType: Boolean
    val homeScreenService: HomeScreenService?
    val baseActivity: BaseActivity
    fun handleHomeScreenDataFailure(cacheHeader: CacheHeader, failureMessage: String, shouldShowAccountsList: Boolean)
    fun loadAccountsAndGoHome()
    fun loadAccountsAndGoHomeWithShortcut(shortcutId: String)
    fun getDeviceProfilingInteractor(): DeviceProfilingInteractor
    fun showProgressDialog()
    fun dismissProgressDialog()
    fun logoutAndGoToStartScreen()
    fun showRelogRequiredScreen()
    fun showGenericFailureScreen(title: String, message: String)
    fun navigateToHomeScreenSelectingHomeIcon()
    fun startActivityIfAvailable(intent: Intent?): Boolean
}