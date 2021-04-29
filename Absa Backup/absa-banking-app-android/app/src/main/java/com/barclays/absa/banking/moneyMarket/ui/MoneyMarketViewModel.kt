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
 */
package com.barclays.absa.banking.moneyMarket.ui

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.barclays.absa.banking.boundary.model.AccountObject
import com.barclays.absa.banking.boundary.shared.dto.SureCheckResponse
import com.barclays.absa.banking.boundary.shared.dto.TransactionResponse
import com.barclays.absa.banking.framework.ExtendedResponseListener
import com.barclays.absa.banking.framework.dagger.getServiceInterface
import com.barclays.absa.banking.framework.data.ResponseObject
import com.barclays.absa.banking.framework.services.contracts.IAppCacheService
import com.barclays.absa.banking.home.ui.IHomeCacheService
import com.barclays.absa.banking.moneyMarket.service.MoneyMarketConvertAccountExtendedResponseListener
import com.barclays.absa.banking.moneyMarket.service.MoneyMarketInteractor
import com.barclays.absa.banking.moneyMarket.service.MoneyMarketSnoozeExtendedResponseListener
import com.barclays.absa.banking.moneyMarket.service.MoneyMarketWithdrawExtendedResponseListener
import com.barclays.absa.banking.moneyMarket.service.dto.MoneyMarketFlowModel
import com.barclays.absa.utils.FilterAccountList
import java.util.*

class MoneyMarketViewModel : ViewModel() {
    private var moneyMarketService = MoneyMarketInteractor()
    var moneyMarketFlowModel = MoneyMarketFlowModel()

    private val moneyMarketSnoozeExtendedResponseListener: ExtendedResponseListener<TransactionResponse> by lazy { MoneyMarketSnoozeExtendedResponseListener(this) }
    private val moneyMarketConvertAccountExtendedResponseListener: ExtendedResponseListener<SureCheckResponse> by lazy { MoneyMarketConvertAccountExtendedResponseListener(this) }
    private val moneyMarketWithdrawExtendedResponseListener: ExtendedResponseListener<SureCheckResponse> by lazy { MoneyMarketWithdrawExtendedResponseListener(this) }

    var moneyMarketSnoozeLiveData = MutableLiveData<ResponseObject>()
    var moneyMarketLogActionLiveData = MutableLiveData<ResponseObject>()
    var moneyMarketConvertAccountLiveData = MutableLiveData<SureCheckResponse>()
    var moneyMarketWithdrawLiveData = MutableLiveData<SureCheckResponse>()

    private val appCacheService: IAppCacheService = getServiceInterface()
    private val homeCacheService: IHomeCacheService = getServiceInterface()

    fun snoozeMoneyMarketNotice(accountNumber: String) {
        moneyMarketService.snoozeMoneyMarketBannerStatus(accountNumber, moneyMarketSnoozeExtendedResponseListener)
    }

    fun convertMoneyMarketAccount() {
        moneyMarketService.convertMoneyMarketAccount(moneyMarketFlowModel.moneyMarketAccount.accountNumber, moneyMarketConvertAccountExtendedResponseListener)
    }

    fun withdrawMoneyMarket() {
        moneyMarketService.withdrawMoneyMarketFunds(moneyMarketFlowModel.moneyMarketAccount.accountNumber, moneyMarketFlowModel.moneyMarketDestinationAccount.accountNumber, moneyMarketWithdrawExtendedResponseListener)
    }

    fun hideMoneyMarketOfferBanner(accountNumber: String) {
        val moneyMarketAccountStatus = homeCacheService.getMoneyMarketStatusList().toMutableList()
        moneyMarketAccountStatus.removeIf { orbitAccount -> orbitAccount.account == accountNumber }
        homeCacheService.setMoneyMarketStatusList(moneyMarketAccountStatus)
    }

    fun hasActiveMoneyMarketFund(accountNumber: String) = homeCacheService.getMoneyMarketStatusList().any { orbitAccount -> orbitAccount.account == accountNumber && orbitAccount.status == ACTIVE_ORBIT_STATUS }

    fun availableTransferAccounts(): ArrayList<AccountObject> {
        val accountList = FilterAccountList.getTransactionalAndCreditAccounts(appCacheService.getSecureHomePageObject()?.accounts)

        homeCacheService.getMoneyMarketStatusList().forEach { moneyMarketItem ->
            accountList.removeIf { accountItem -> accountItem.accountNumber == moneyMarketItem.account }
        }
        return accountList
    }
}