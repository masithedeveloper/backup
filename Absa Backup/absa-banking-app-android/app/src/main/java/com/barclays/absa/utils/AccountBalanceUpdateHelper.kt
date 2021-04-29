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

package com.barclays.absa.utils

import android.os.Handler
import androidx.lifecycle.ViewModelProvider
import com.barclays.absa.banking.avaf.ui.AvafConstants.BALANCE_FAILED
import com.barclays.absa.banking.avaf.ui.AvafConstants.BALANCE_NOT_AVAILABLE
import com.barclays.absa.banking.avaf.ui.AvafConstants.BALANCE_PENDING
import com.barclays.absa.banking.express.accountBalances.AccountBalancesViewModel
import com.barclays.absa.banking.express.accountBalances.dto.AccountBalanceResponse
import com.barclays.absa.banking.express.getAllBalances.CacheHeader
import com.barclays.absa.banking.framework.BaseActivity
import com.barclays.absa.banking.framework.app.BMBApplication
import com.barclays.absa.banking.framework.dagger.getServiceInterface
import com.barclays.absa.banking.framework.data.cache.IRewardsCacheService
import com.barclays.absa.banking.passcode.passcodeLogin.ExpressAuthenticationHelper
import com.barclays.absa.banking.presentation.transactions.AccountRefreshInterface

class AccountBalanceUpdateHelper(val activity: BaseActivity) {

    private lateinit var accountBalanceViewModel: AccountBalancesViewModel
    private var retries = 0

    var pollDelayMillis = 3000L
    var maxRetries = 3

    private val rewardsCacheService: IRewardsCacheService = getServiceInterface()
    private val absaCacheService: IAbsaCacheService = getServiceInterface()

    fun updateAccountBalance(accountNumber: String, showProgressDialog: Boolean, balanceUpdateCallback: BalanceUpdateCallBack) {
        if (!::accountBalanceViewModel.isInitialized) {
            accountBalanceViewModel = ViewModelProvider(activity).get(AccountBalancesViewModel::class.java)
        }

        if (retries < maxRetries) {
            accountBalanceViewModel.failureLiveData.observe(activity, {
                accountBalanceViewModel.failureLiveData.removeObservers(activity)
                Handler(activity.mainLooper).postDelayed({ updateAccountBalance(accountNumber, showProgressDialog, balanceUpdateCallback) }, pollDelayMillis)
            })

            accountBalanceViewModel.fetchAccountBalance(accountNumber, showProgressDialog)

            accountBalanceViewModel.accountBalanceLiveData.observe(activity, { accountBalanceResponse: AccountBalanceResponse ->
                accountBalanceViewModel.accountBalanceLiveData.removeObservers(activity)
                ExpressAuthenticationHelper(activity).updateAccount(accountBalanceResponse.accountBalances)
                balanceUpdateCallback.updateSuccessful(accountBalanceResponse)
            })
        } else {
            balanceUpdateCallback.updateFailed()
        }

        retries++
    }

    fun updateRewardsBalance(accountRefreshInterface: AccountRefreshInterface) {
        refreshHomeScreenAccountsAndBalances(object : AccountRefreshInterface {
            override fun onSuccess() {
                if (rewardsCacheService.getExpressRewardsDetails().rewardsAccountBalanceSet) {
                    accountRefreshInterface.onSuccess()
                } else {
                    val rewardsMemberShipNumber = rewardsCacheService.getExpressRewardsDetails().rewardsMembershipNumber
                    AccountBalanceUpdateHelper(activity).updateAccountBalance(rewardsMemberShipNumber, true, object : BalanceUpdateCallBack {
                        override fun updateSuccessful(balanceUpdated: AccountBalanceResponse) {
                            accountRefreshInterface.onSuccess()
                        }

                        override fun updateFailed() {
                            accountRefreshInterface.onFailure()
                        }
                    })
                }
            }

            override fun onFailure() {
                accountRefreshInterface.onFailure()
            }
        }, CacheHeader.REWARDS)
    }

    fun refreshHomeScreenAccountsAndBalances(accountRefreshInterface: AccountRefreshInterface) {
        refreshHomeScreenAccountsAndBalances(accountRefreshInterface, CacheHeader.BALANCE)
    }

    private fun refreshHomeScreenAccountsAndBalances(accountRefreshInterface: AccountRefreshInterface, cacheHeader: CacheHeader) {
        absaCacheService.clear()
        AbsaCacheManager.getInstance().clearCache()

        activity.homeScreenService?.refreshHomeScreenAccountsAndBalances(object : AccountRefreshInterface {
            override fun onSuccess() {

                val expressAuthenticationHelper = ExpressAuthenticationHelper(BMBApplication.getInstance().topMostActivity as BaseActivity)
                expressAuthenticationHelper.getAllBalances(cacheHeader, object : ExpressAuthenticationHelper.BalanceCallBack {
                    override fun callComplete() {
                        activity.homeScreenService?.refreshAccountList()
                        accountRefreshInterface.onSuccess()
                    }

                    override fun callFailed(failureMessage: String) {
                        accountRefreshInterface.onFailure()
                    }
                })
            }

            override fun onFailure() {
                accountRefreshInterface.onFailure()
            }
        })
    }

    interface BalanceUpdateCallBack {
        fun updateSuccessful(balanceUpdated: AccountBalanceResponse)
        fun updateFailed()
    }

    fun updateAvafAccountBalance(accountNumber: String, showProgressDialog: Boolean, cancelPending: Boolean, balanceUpdateCallback: BalanceUpdateCallBack) {
        if (!::accountBalanceViewModel.isInitialized) {
            accountBalanceViewModel = ViewModelProvider(activity).get(AccountBalancesViewModel::class.java)
        }

        if (retries <= maxRetries) {
            if (cancelPending && accountBalanceViewModel.isInitialised() && accountBalanceViewModel.accountBalanceLiveData.hasActiveObservers()) {
                with(accountBalanceViewModel) {
                    accountBalanceLiveData.removeObservers(activity)
                    failureLiveData.removeObservers(activity)
                }
            }

            accountBalanceViewModel.fetchAccountBalance(accountNumber, showProgressDialog)
            accountBalanceViewModel.accountBalanceLiveData.observe(activity, { accountBalanceResponse: AccountBalanceResponse ->
                accountBalanceViewModel.accountBalanceLiveData.removeObservers(activity)
                if (BALANCE_PENDING.equals(accountBalanceResponse.accountBalances.balance, ignoreCase = true)) {
                    updateAvafAccountBalance(accountNumber, showProgressDialog, false, balanceUpdateCallback)
                } else if (BALANCE_FAILED.equals(accountBalanceResponse.accountBalances.balance, ignoreCase = true) || BALANCE_NOT_AVAILABLE.equals(accountBalanceResponse.accountBalances.balance, ignoreCase = true)) {
                    ExpressAuthenticationHelper(activity).updateAccountBalance(accountBalanceResponse.accountBalances)
                    balanceUpdateCallback.updateFailed()
                } else {
                    ExpressAuthenticationHelper(activity).updateAccountBalance(accountBalanceResponse.accountBalances)
                    balanceUpdateCallback.updateSuccessful(accountBalanceResponse)
                }
            })

            accountBalanceViewModel.failureLiveData.observe(activity, {
                accountBalanceViewModel.failureLiveData.removeObservers(activity)
                updateAvafAccountBalance(accountNumber, showProgressDialog, false, balanceUpdateCallback)
            })
        } else {
            balanceUpdateCallback.updateFailed()
        }

        retries++
    }
}