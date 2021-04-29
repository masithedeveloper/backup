/*
 * Copyright (c) 2019 Absa Bank Limited, All Rights Reserved.
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
package com.barclays.absa.banking.framework

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.annotation.IdRes
import androidx.annotation.LayoutRes
import androidx.annotation.StringRes
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.navigation.NavDirections
import androidx.navigation.fragment.findNavController
import com.barclays.absa.banking.express.getAllBalances.CacheHeader
import com.barclays.absa.banking.framework.app.BMBApplication
import com.barclays.absa.banking.framework.services.contracts.IAppCacheService
import com.barclays.absa.banking.framework.utils.AlertInfo
import com.barclays.absa.banking.framework.utils.BMBLogger
import com.barclays.absa.banking.home.services.HomeScreenService
import com.barclays.absa.banking.shared.AlertDialogProperties
import com.barclays.absa.banking.shared.BaseAlertDialog
import com.barclays.absa.integration.DeviceProfilingInteractor
import com.newrelic.agent.android.NewRelic
import za.co.absa.networking.error.ApplicationError

abstract class BaseFragment(@LayoutRes layout: Int) : Fragment(layout), BaseView {

    // Services should not be used directly in views, but instead be injected in managers and viewModels as required
    // These dependencies will reside here only in the interim
    val appCacheService: IAppCacheService
        get() = baseActivity.appCacheService

    constructor() : this(-1)

    override val baseActivity: BaseActivity
        get() {
            var baseActivity = activity as BaseActivity?
            if (baseActivity == null) {
                baseActivity = BMBApplication.getInstance().topMostActivity as BaseActivity
            }
            return baseActivity
        }

    override fun onResume() {
        super.onResume()
        BMBLogger.d("x-class:", javaClass.simpleName)
        NewRelic.recordBreadcrumb(javaClass.simpleName)
    }

    override fun showNoPrimaryDeviceScreen() {
        baseActivity.showNoPrimaryDeviceScreen()
    }

    override fun showMessageError(message: String?) {
        baseActivity.showMessageError(message)
    }

    fun showGenericMessageError() {
        baseActivity.showGenericErrorMessage()
    }

    override fun checkDeviceState() {
        baseActivity.checkDeviceState()
    }

    override fun logoutAndGoToStartScreen() {
        baseActivity.logoutAndGoToStartScreen()
    }

    override fun showProgressDialog() {
        baseActivity.showProgressDialog()
    }

    override fun dismissProgressDialog() {
        baseActivity.dismissProgressDialog()
    }

    override fun showGenericErrorMessage() {
        baseActivity.showGenericErrorMessage()
    }

    override fun showGenericErrorMessageThenFinish() {
        baseActivity.showGenericErrorMessageThenFinish()
    }

    fun setToolBar(fragmentManager: FragmentManager?, title: String?) {
        fragmentManager?.let {
            baseActivity.setToolBarBackFragment(it, title)
        }
    }

    fun setToolBarNoBack(title: Int) {
        baseActivity.setToolBarNoBackButton(title)
    }

    open fun setToolBar(title: String?, onClickListener: View.OnClickListener) {
        baseActivity.setToolBarBackFragment(title, onClickListener)
    }

    open fun setToolBar(@StringRes title: Int, onClickListener: View.OnClickListener) {
        baseActivity.setToolBarBackFragment(getString(title), onClickListener)
    }

    fun setToolBar(title: String) {
        baseActivity.setToolBarBack(title)
    }

    fun setToolBar(title: String, overrideTitleCase: Boolean) {
        baseActivity.setToolBarBack(title, overrideTitleCase)
    }

    fun setToolBar(title: Int, overrideTitleCase: Boolean) {
        baseActivity.setToolBarBack(title, overrideTitleCase)
    }

    fun setToolBar(title: Int) {
        baseActivity.setToolBarBack(title)
    }

    fun hideToolBar() {
        baseActivity.hideToolBar()
    }

    fun showToolBar() {
        baseActivity.showToolBar()
    }

    override fun showCustomAlertDialog(alertInfo: AlertInfo) {
        BaseAlertDialog.showAlertDialog(AlertDialogProperties.Builder()
                .title(alertInfo.title)
                .message(alertInfo.message)
                .positiveButton(alertInfo.positiveButtonText)
                .positiveDismissListener(alertInfo.positiveDismissListener
                        ?: DialogInterface.OnClickListener { _, _ -> dismissProgressDialog() })
                .negativeButton(alertInfo.negativeButtonText)
                .negativeDismissListener(alertInfo.negativeDismissListener
                        ?: DialogInterface.OnClickListener { _, _ -> dismissProgressDialog() })
                .build())
    }

    fun preventDoubleClick(view: View) {
        BaseActivity.preventDoubleClick(view)
    }

    fun preventDoubleClick(view: View, duration: Int) {
        BaseActivity.preventDoubleClick(view, duration)
    }

    override fun navigateToPreviousDevicePasscodeEntryScreen() {
        baseActivity.navigateToPreviousDevicePasscodeEntryScreen()
    }

    override val isBusinessAccount: Boolean
        get() {
            return baseActivity.isBusinessAccount
        }

    override val isSouthAfricanResident: Boolean
        get() = baseActivity.isSouthAfricanResident

    override val isSoleProprietor: Boolean
        get() = baseActivity.isSoleProprietor

    override val isSwiftAllowedForClientType: Boolean
        get() {
            return baseActivity.isSwiftAllowedForClientType
        }

    override fun navigateToHomeScreenSelectingHomeIcon() {
        baseActivity.navigateToHomeScreenSelectingHomeIcon()
    }

    override fun navigateToHomeScreenWithoutReloadingAccounts() {
        baseActivity.navigateToHomeScreenWithoutReloadingAccounts()
    }

    override fun navigateToHomeScreenAndShowAccountsList() {
        baseActivity.navigateToHomeScreenAndShowAccountsList()
    }

    override fun loadAccountsAndGoHomeWithShortcut(shortcutId: String) {
        baseActivity.loadAccountsAndGoHomeWithShortcut(shortcutId)
    }

    override fun handleHomeScreenDataFailure(cacheHeader: CacheHeader, failureMessage: String, shouldShowAccountsList: Boolean) {
        baseActivity.handleHomeScreenDataFailure(cacheHeader, failureMessage, shouldShowAccountsList)
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

    override val homeScreenService: HomeScreenService?
        get() = baseActivity.homeScreenService

    override fun loadAccountsAndGoHome() {
        baseActivity.loadAccountsAndGoHome()
    }

    override fun loadAccountsAndShowHomeScreenWithAccountsList() {
        baseActivity.loadAccountsAndShowHomeScreenWithAccountsList()
    }

    override fun getDeviceProfilingInteractor(): DeviceProfilingInteractor {
        return baseActivity.getDeviceProfilingInteractor()
    }

    override fun showMessage(title: String?, message: String?, onDismissListener: DialogInterface.OnClickListener?) {
        baseActivity.showMessage(title, message, onDismissListener)
    }

    fun popBackTo(destinationId: Int) = with(findNavController()) { popBackStack(destinationId, false) }

    fun navigate(navDirections: NavDirections) = with(findNavController()) { currentDestination?.getAction(navDirections.actionId)?.let { navigate(navDirections) } }

    fun navigate(@IdRes destinationId: Int, bundle: Bundle? = null) {
        with(findNavController()) {
            if (currentDestination?.id != destinationId) {
                navigate(destinationId, bundle)
            }
        }
    }

    override fun showMessageError(error: ApplicationError) {
        baseActivity.showMessageError(error)
    }

    override fun showRelogRequiredScreen() {
        baseActivity.showRelogRequiredScreen()
    }

    override fun showGenericFailureScreen(title: String, message: String) {
        baseActivity.showGenericFailureScreen(title, message)
    }

    override fun startActivityIfAvailable(intent: Intent?): Boolean = baseActivity.startActivityIfAvailable(intent)
}