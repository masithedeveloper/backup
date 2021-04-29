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

package com.barclays.absa.banking.transfer

import androidx.lifecycle.MutableLiveData
import com.barclays.absa.banking.account.services.AccountInteractor
import com.barclays.absa.banking.boundary.model.AccountList
import com.barclays.absa.banking.boundary.model.AccountObject
import com.barclays.absa.banking.boundary.model.rewards.*
import com.barclays.absa.banking.home.ui.AccountTypes
import com.barclays.absa.banking.presentation.shared.BaseViewModel
import com.barclays.absa.banking.rewards.services.RewardsInteractor
import com.barclays.absa.banking.transfer.responseListeners.AccountListExtendedResponseListener
import com.barclays.absa.banking.transfer.responseListeners.RedeemRewardsExtendedResponseListener
import com.barclays.absa.banking.transfer.responseListeners.RewardsRedeemConfirmationExtendedResponseListener
import com.barclays.absa.banking.transfer.responseListeners.RewardsRedeemResultExtendedResponseListener
import com.barclays.absa.banking.transfer.services.TransferFundsInteractor
import com.barclays.absa.banking.transfer.services.TransferFundsService
import com.barclays.absa.utils.TextFormatUtils
import styleguide.forms.SelectorList
import styleguide.utils.extensions.removeSpaces

class TransferViewModel : BaseViewModel() {

    var transferConfirmationData: TransferConfirmationData = TransferConfirmationData()
    lateinit var accountList: AccountList

    var isItemSelectedByUser: Boolean = false
    var fromAccount: AccountObject? = null
    var toAccount: AccountObject? = null

    var minimumAmount: String? = null
    var isFromRewards: Boolean = false
    var isRewardsTransfer: Boolean = false
    var isAvafTransfer: Boolean = false

    var fromAccountItemList = MutableLiveData<SelectorList<AccountListItem>>()
    var toAccountItemList = MutableLiveData<SelectorList<AccountListItem>>()

    var fromAccountList: MutableList<AccountObject> = ArrayList()
    var toAccountList: MutableList<AccountObject> = ArrayList()

    var rewardsAccountListLiveData: MutableLiveData<List<RewardsAccountDetails>> = MutableLiveData()
    var rewardsRedeemResultLiveData = MutableLiveData<RewardsRedeemResult>()
    var rewardsRedeemConfirmationLiveData = MutableLiveData<RewardsRedeemConfirmation>()

    var rewardsRedeemConfirmation: RewardsRedeemConfirmation? = null

    var accountInteractor: AccountInteractor = AccountInteractor()
    var transferFundsService: TransferFundsService = TransferFundsInteractor()
    var rewardsInteractor: RewardsInteractor = RewardsInteractor()

    private val rewardsRedeemResultExtendedResponseListener = RewardsRedeemResultExtendedResponseListener(this)
    private val redeemRewardsExtendedResponseListener = RedeemRewardsExtendedResponseListener(this)
    private val rewardsRedeemConfirmationExtendedResponseListener = RewardsRedeemConfirmationExtendedResponseListener(this)
    private val accountListExtendedResponseListener: AccountListExtendedResponseListener = AccountListExtendedResponseListener(this)

    fun fetchAccountList(transferType: TransferType) {
        accountInteractor.fetchExpressAccounts(transferType, accountListExtendedResponseListener)
    }

    fun fetchRewardsToAccountList() {
        rewardsInteractor.fetchRewardsRedeemData(redeemRewardsExtendedResponseListener)
    }

    fun populateRewardsRedemptionData(redeemRewards: RedeemRewards) {
        rewardsAccountListLiveData.value = redeemRewards.toAccountList
        populateListOfToAccountsForRewards()
    }

    fun populateListOfToAccountsForRewards() {
        toAccountItemList.value?.clear()
        toAccountList.clear()

        rewardsAccountListLiveData.value?.let { rewardsToList ->
            rewardsToList.forEach {
                AccountObject().apply {
                    accountNumber = it.accountNumber
                    description = it.description
                    availableBalance = it.availableBalance
                    accountType = it.description
                    toAccountList.add(this)
                }
            }

            val filteredCPAccountList: ArrayList<AccountObject> = filterOutCashPassportAccounts(toAccountList)
            val filteredAvafAccountList: ArrayList<AccountObject> = filterOutAvafAccounts(filteredCPAccountList)

            filteredAvafAccountList.forEach {
                AccountListItem().apply {
                    accountNumber = it.accountNumber
                    accountType = it.accountType
                    accountBalance = it.availableBalance.toString()
                    toAccountItemList.value?.add(this)
                }
            }

            toAccountList = filteredAvafAccountList
        }
    }

    fun validateRewardsRedemption(transactionAmount: String) {
        val redeemRewardsCash = RedeemRewardsCash()
        val userEnteredAmount = TextFormatUtils.formatBasicAmount(transactionAmount)
        redeemRewardsCash.amountToRedeem = userEnteredAmount.removeSpaces()
        redeemRewardsCash.toAccountNumber = toAccount?.accountNumber
        transferFundsService.validateRewardsRedemption(redeemRewardsCash, rewardsRedeemConfirmationExtendedResponseListener)
    }

    fun baseAccountListLoaded(accountList: AccountList) {
        this.accountList = accountList
        setAccountLists(accountList)
        populateFromAccountList()
    }

    private fun populateFromAccountList() {
        fromAccountItemList.value?.clear()
        val tempAccountList = SelectorList<AccountListItem>()

        fromAccountList.clear()
        accountList.fromAccountList.let { fromAccountList.addAll(it) }
        removeInvalidAccounts()

        fromAccountList.forEach {
            AccountListItem().apply {
                accountType = it.description
                accountBalance = it.availableBalanceFormated
                accountNumber = it.accountNumber
                tempAccountList.add(this)
            }
        }

        fromAccountItemList.value = tempAccountList
        setUpToAccountList()
    }

    internal fun populateToAccountList() {
        val tempAccountList = SelectorList<AccountListItem>()
        setUpToAccountList()
        toAccountList.forEach {
            AccountListItem().apply {
                accountType = it.description
                accountBalance = it.availableBalanceFormated
                accountNumber = it.accountNumber
                tempAccountList.add(this)
            }
        }

        toAccountItemList.value = tempAccountList
    }

    private fun setUpToAccountList() {
        toAccountList.clear()
        toAccountList.addAll(accountList.toAccountList)
        val filteredAccountList: ArrayList<AccountObject> = filterOutCashPassportAccounts(toAccountList)
        toAccountList = filterOutAvafAccounts(filteredAccountList)

        toAccountList.remove(fromAccount)

        if (isAvafTransfer) {
            toAccount?.let { toAccountList.add(it) }
        }
    }

    private fun fetchRewardsAccount(): AccountObject? {
        accountList.accountsList.forEach { accountObject ->
            if (TransferConstants.ABSA_REWARDS.equals(accountObject.accountType, ignoreCase = true)) {
                return accountObject
            }
        }
        return null
    }

    private fun removeInvalidAccounts() {
        val sortedArrayList = ArrayList<AccountObject>()
        if (!isAvafTransfer) {
            fetchRewardsAccount()?.let { sortedArrayList.add(0, it) }
        }

        fromAccountList.forEach { account ->
            sortedArrayList.add(account)
            if (account.availableBalance.amountDouble == 0.00) {
                val indexOfInvalidAccount = sortedArrayList.indexOf(account)
                sortedArrayList.removeAt(indexOfInvalidAccount)
            }
        }

        fromAccountList = sortedArrayList
    }

    private fun filterOutAvafAccounts(accountList: List<AccountObject>): ArrayList<AccountObject> {
        return if (!isAvafTransfer) {
            val filteredAccountList: List<AccountObject> = accountList.filter { account -> !AccountTypes.ABSA_VEHICLE_AND_ASSET_FINANCE.equals(account.accountType, ignoreCase = true) }
            ArrayList(filteredAccountList)
        } else {
            ArrayList(accountList)
        }
    }

    private fun setAccountLists(combinedAccountList: AccountList) {
        fromAccountList = combinedAccountList.fromAccountList.toMutableList()
        toAccountList = combinedAccountList.toAccountList.toMutableList()
    }

    private fun filterOutCashPassportAccounts(accountList: List<AccountObject>): ArrayList<AccountObject> {
        val filteredAccountList = ArrayList<AccountObject>()
        accountList.forEach { account ->
            if (!"mccp".equals(account.accountType, ignoreCase = true)) {
                filteredAccountList.add(account)
            }
        }
        return filteredAccountList
    }

    fun rewardsTransferConfirmed(rewardsRedeemConfirmation: RewardsRedeemConfirmation) {
        this.rewardsRedeemConfirmation = rewardsRedeemConfirmation
        transferFundsService.sendTransferRequest(rewardsRedeemConfirmation.txnReferenceID.toString(), rewardsRedeemResultExtendedResponseListener)
    }

    fun sureCheckConfirmed() {
        transferFundsService.sendTransferRequest(rewardsRedeemConfirmation?.txnReferenceID.toString(), rewardsRedeemResultExtendedResponseListener)
    }

    fun clearAccounts() {
        fromAccountList.clear()
        toAccountList.clear()
        fromAccount = null
        isItemSelectedByUser = false
    }
}