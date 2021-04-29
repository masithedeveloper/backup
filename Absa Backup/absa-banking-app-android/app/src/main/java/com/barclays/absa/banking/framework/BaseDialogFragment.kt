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
 */
package com.barclays.absa.banking.framework

import android.content.DialogInterface
import android.content.Intent
import androidx.fragment.app.DialogFragment
import com.barclays.absa.banking.express.getAllBalances.CacheHeader
import com.barclays.absa.banking.framework.app.BMBApplication
import com.barclays.absa.banking.framework.utils.AlertInfo
import com.barclays.absa.banking.home.services.HomeScreenService
import com.barclays.absa.integration.DeviceProfilingInteractor
import za.co.absa.networking.error.ApplicationError

open class BaseDialogFragment : DialogFragment(), BaseView {

    override val baseActivity: BaseActivity
        get() {
            var baseActivity = activity as BaseActivity?
            if (baseActivity == null) {
                baseActivity = BMBApplication.getInstance().topMostActivity as BaseActivity
            }
            return baseActivity
        }

    override fun showNoPrimaryDeviceScreen() {
        baseActivity.showNoPrimaryDeviceScreen()
    }

    override fun showMessageError(message: String?) {
        baseActivity.showMessageError(message)
    }

    override fun showMessageError(error: ApplicationError) {
        baseActivity.showMessageError(error)
    }

    override fun checkDeviceState() {
        baseActivity.checkDeviceState()
    }

    override fun showGenericErrorMessageThenFinish() {
        baseActivity.showGenericErrorMessageThenFinish()
    }

    override fun showMessage(title: String?, message: String?, onDismissListener: DialogInterface.OnClickListener?) {
        baseActivity.showMessage(title, message, onDismissListener)
    }

    override fun showGenericErrorMessage() {
        baseActivity.showGenericErrorMessage()
    }

    override fun showCustomAlertDialog(alertInfo: AlertInfo) {
        baseActivity.showCustomAlertDialog(alertInfo)
    }

    override fun navigateToPreviousDevicePasscodeEntryScreen() {
        baseActivity.navigateToPreviousDevicePasscodeEntryScreen()
    }

    override fun navigateToHomeScreenAndShowAccountsList() {
        baseActivity.navigateToHomeScreenAndShowAccountsList()
    }

    override fun navigateToHomeScreenWithoutReloadingAccounts() {
        baseActivity.navigateToHomeScreenWithoutReloadingAccounts()
    }

    override fun loadAccountsClearingAccountProfileAndShowHomeScreen() {
        baseActivity.loadAccountsClearingAccountProfileAndShowHomeScreen()
    }

    override fun loadAccountsClearingAccountProfileAndGoHomeWithShortcut(shortcutId: String) {
        baseActivity.loadAccountsClearingAccountProfileAndGoHomeWithShortcut(shortcutId)
    }

    override fun loadAccountsClearingRewardsBalanceAndShowHomeScreen() {
        baseActivity.loadAccountsClearingRewardsBalanceAndShowHomeScreen()
    }

    override fun loadAccountsAndShowHomeScreenWithAccountsList() {
        baseActivity.loadAccountsAndShowHomeScreenWithAccountsList()
    }

    override val isBusinessAccount: Boolean
        get() = baseActivity.isBusinessAccount
    override val isSouthAfricanResident: Boolean
        get() = baseActivity.isSouthAfricanResident
    override val isSoleProprietor: Boolean
        get() = baseActivity.isSoleProprietor
    override val isSwiftAllowedForClientType: Boolean
        get() = baseActivity.isSwiftAllowedForClientType

    override fun handleHomeScreenDataFailure(cacheHeader: CacheHeader, failureMessage: String, shouldShowAccountsList: Boolean) {
        baseActivity.handleHomeScreenDataFailure(cacheHeader, failureMessage, shouldShowAccountsList)
    }

    override val homeScreenService: HomeScreenService?
        get() = baseActivity.homeScreenService

    override fun loadAccountsAndGoHome() {
        baseActivity.loadAccountsAndGoHome()
    }

    override fun loadAccountsAndGoHomeWithShortcut(shortcutId: String) {
        baseActivity.loadAccountsAndGoHomeWithShortcut(shortcutId)
    }

    override fun getDeviceProfilingInteractor(): DeviceProfilingInteractor = baseActivity.getDeviceProfilingInteractor()

    override fun showProgressDialog() {
        baseActivity.showProgressDialog()
    }

    override fun dismissProgressDialog() {
        baseActivity.dismissProgressDialog()
    }

    override fun logoutAndGoToStartScreen() {
        baseActivity.logoutAndGoToStartScreen()
    }

    override fun showRelogRequiredScreen() {
        baseActivity.showRelogRequiredScreen()
    }

    override fun showGenericFailureScreen(title: String, message: String) {
        baseActivity.showGenericFailureScreen(title, message)
    }

    override fun navigateToHomeScreenSelectingHomeIcon() {
        baseActivity.navigateToHomeScreenSelectingHomeIcon()
    }

    override fun startActivityIfAvailable(intent: Intent?): Boolean {
        return baseActivity.startActivityIfAvailable(intent)
    }
}