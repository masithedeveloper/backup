/*
 *  Copyright (c) 2018 Absa Bank Limited, All Rights Reserved.
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
package com.barclays.absa.banking.account.ui

import com.barclays.absa.banking.account.services.AccountInteractor
import com.barclays.absa.banking.account.services.AccountRequestParameters
import com.barclays.absa.banking.account.services.AccountService
import com.barclays.absa.banking.account.services.dto.LinkedAndUnlinkedAccounts
import com.barclays.absa.banking.account.services.dto.ManageAccounts
import com.barclays.absa.banking.account.services.dto.ManageAccountsResponse
import com.barclays.absa.banking.framework.AbstractPresenter
import com.barclays.absa.banking.presentation.sureCheck.SureCheckDelegate
import com.barclays.absa.banking.settings.ui.ManageAccountsActivity
import com.barclays.absa.banking.settings.ui.ManageAccountsView
import org.json.JSONArray
import org.json.JSONObject
import styleguide.utils.extensions.toFormattedAccountNumber
import java.lang.ref.WeakReference

class ManageAccountsPresenter(viewWeakReference: WeakReference<ManageAccountsView>, var sureCheckDelegate: SureCheckDelegate) : AbstractPresenter(viewWeakReference) {

    private val accountsService: AccountService = AccountInteractor()
    private var updatedAccountList: MutableList<ManageAccounts> = ArrayList()
    private var originalAccountList: MutableList<ManageAccounts.ShortAccountList> = ArrayList()
    private var isAccountListOrderUpdated: Boolean = false
    private var manageAccountsResponse: ManageAccountsResponse? = ManageAccountsResponse()

    private val fetchLinkedAndUnlinkedAccountsExtendedResponseListener: LinkedAndUnlinkedAccountsExtendedResponseListener = LinkedAndUnlinkedAccountsExtendedResponseListener(this)
    private val linkAndUnlinkAccountsExtendedResponseListener: LinkAndUnlinkAccountsExtendedResponseListener = LinkAndUnlinkAccountsExtendedResponseListener(this)
    private val view = viewWeakReference.get() as ManageAccountsView

    fun onViewLoaded() {
        showProgressIndicator()
        accountsService.fetchAccountStats(fetchLinkedAndUnlinkedAccountsExtendedResponseListener)
    }

    fun onLinkAndUnlinkStateRequestSuccessful(linkedAndUnlinkedStateResponse: LinkedAndUnlinkedAccounts?) {
        if (linkedAndUnlinkedStateResponse?.accountList != null && linkedAndUnlinkedStateResponse.accountList!!.isNotEmpty()) {
            if (linkedAndUnlinkedStateResponse.accountList!!.size > 1) {
                buildOriginalList(linkedAndUnlinkedStateResponse)
                view.displayListOnView(linkedAndUnlinkedStateResponse.accountList!!)
            } else {
                view.showAddMoreAccountsScreen()
            }
        } else {
            view.showGenericErrorMessageThenFinish()
        }
        dismissProgressIndicator()
    }

    private fun buildOriginalList(linkedAndUnlinkedStateResponse: LinkedAndUnlinkedAccounts) {
        for (item: ManageAccounts in linkedAndUnlinkedStateResponse.accountList!!) {
            val addedItem: ManageAccounts.ShortAccountList = ManageAccounts.ShortAccountList()
            addedItem.accountNumber = item.accountNumber
            addedItem.accountLinked = item.accountLinked
            originalAccountList.add(addedItem)
        }
    }

    fun onSaveStateClicked(accounts: List<ManageAccounts>, isOrderChanged: Boolean) {
        isAccountListOrderUpdated = isOrderChanged
        updatedAccountList = ArrayList(accounts)

        if (isOrderChanged || filterUnChangedAccountsOut().isNotEmpty()) {
            requestAccountStateUpdate()
        } else {
            view.showNoStatesUpdated()
        }
    }

    private fun buildJsonRequest(accounts: List<ManageAccounts>): String {
        val jsonObject = JSONObject()
        val jsonArray = JSONArray()
        for (accountItem: ManageAccounts in accounts) {
            val accountJsonObject = JSONObject()
            accountJsonObject.put(AccountRequestParameters.ACCOUNT_NUMBER.key, accountItem.accountNumber)
            accountJsonObject.put(AccountRequestParameters.IS_LINKED.key, accountItem.accountLinked.toString())
            accountJsonObject.put(AccountRequestParameters.ACCOUNT_TYPE.key, accountItem.accountType)
            if (filterUnChangedAccountsOut().contains(accountItem)) {
                accountJsonObject.put(AccountRequestParameters.LINK_STATE_CHANGED.key, true.toString())
            } else {
                accountJsonObject.put(AccountRequestParameters.LINK_STATE_CHANGED.key, false.toString())
            }
            jsonArray.put(accountJsonObject)
        }
        jsonObject.put(AccountRequestParameters.ACCOUNTS.key, jsonArray)
        return jsonObject.toString()
    }

    fun requestAccountStateUpdate() {
        accountsService.linkAndUnlinkAccounts(isAccountListOrderUpdated, filterUnChangedAccountsOut().isNotEmpty(), buildJsonRequest(updatedAccountList), linkAndUnlinkAccountsExtendedResponseListener)
    }

    fun onSaveAccountLinkStatedSuccessful(successResponse: ManageAccountsResponse) {
        sureCheckDelegate.processSureCheck(viewWeakReference.get(), successResponse) {
            onAccountUpdateSuccessful(successResponse)
        }
        dismissProgressIndicator()
    }

    fun filterUnChangedAccountsOut(): List<ManageAccounts> {
        val filteredAccountList: MutableList<ManageAccounts> = ArrayList()
        for (updatedAccountItem: ManageAccounts in updatedAccountList) {
            for (originalAccountItem: ManageAccounts.ShortAccountList in originalAccountList) {
                if (updatedAccountItem.accountNumber == originalAccountItem.accountNumber) {
                    if (updatedAccountItem.accountLinked != originalAccountItem.accountLinked) {
                        filteredAccountList.add(updatedAccountItem)
                    }
                    break
                }
            }
        }
        return filteredAccountList
    }

    fun onBackClicked(accounts: List<ManageAccounts>, isOrderChanged: Boolean) {
        isAccountListOrderUpdated = isOrderChanged
        updatedAccountList = ArrayList(accounts)

        if (isOrderChanged || filterUnChangedAccountsOut().isNotEmpty()) {
            view.showBackDialog()
        } else {
            view.navigateToMenuFragment()
        }
    }

    private fun isNominatedAccount(): Boolean {
        for (item: ManageAccountsResponse.AccountFailure in manageAccountsResponse?.accountList!!) {
            if (item.returnCode?.equals(NOMINATED_ACCOUNT, ignoreCase = true)!!) {
                return true
            }
        }
        return false
    }

    private fun onAccountUpdateSuccessful(manageAccountsResponse: ManageAccountsResponse) {
        this.manageAccountsResponse = manageAccountsResponse
        if (manageAccountsResponse.linkingSuccessful != null && manageAccountsResponse.linkingSuccessful!! && manageAccountsResponse.reorderSuccessful != null && manageAccountsResponse.reorderSuccessful!!) {
            view.onSaveChangesSuccess()
        } else if ((manageAccountsResponse.linkingSuccessful != null && !manageAccountsResponse.linkingSuccessful!!) && (manageAccountsResponse.reorderSuccessful != null && !manageAccountsResponse.reorderSuccessful!!) && manageAccountsResponse.accountList.isNullOrEmpty()) {
            view.onTechnicalDifficulty()
        } else if ((manageAccountsResponse.linkingSuccessful != null && manageAccountsResponse.linkingSuccessful!!) && (manageAccountsResponse.reorderSuccessful != null && !manageAccountsResponse.reorderSuccessful!!) && manageAccountsResponse.accountList.isNullOrEmpty()) {
            view.onOrderSaveChangesFailure()
        } else if (isNominatedAccount() && (manageAccountsResponse.linkingSuccessful != null && !manageAccountsResponse.linkingSuccessful!!) && (manageAccountsResponse.reorderSuccessful != null && manageAccountsResponse.reorderSuccessful!!) && !manageAccountsResponse.accountList.isNullOrEmpty()) {
            view.onAccountLinkingFailure(numberOfNominatedAccounts(manageAccountsResponse.accountList), buildNominatedAccountsList(manageAccountsResponse.accountList))
        } else if (isNominatedAccount() && (manageAccountsResponse.linkingSuccessful != null && !manageAccountsResponse.linkingSuccessful!!) && (manageAccountsResponse.reorderSuccessful != null && !manageAccountsResponse.reorderSuccessful!!) && !manageAccountsResponse.accountList.isNullOrEmpty()) {
            view.onAccountLinkingReorderFailure(numberOfNominatedAccounts(manageAccountsResponse.accountList), buildNominatedAccountsList(manageAccountsResponse.accountList))
        } else {
            view.onTechnicalDifficulty()
        }

        dismissProgressIndicator()
    }

    fun onSaveRequestFailed() {
        view.onTechnicalDifficulty()
        dismissProgressIndicator()
    }

    fun onLinkingAndUnlinkingFailure() {
        view.showGenericErrorMessageThenFinish()
    }

    private fun buildNominatedAccountsList(accountList: List<ManageAccountsResponse.AccountFailure>?): String {
        val errorMessage: StringBuilder? = StringBuilder()
        if (!accountList.isNullOrEmpty()) {
            for (item: ManageAccountsResponse.AccountFailure in accountList) {
                if (item.returnCode.equals(ManageAccountsActivity.NOMINATED_ACCOUNT_CODE, ignoreCase = true)) {
                    val formattedAccountNumber = item.accountNumber.toFormattedAccountNumber() + ", "
                    errorMessage?.append(formattedAccountNumber)
                }
            }
        }
        return errorMessage.toString().dropLast(2)
    }

    private fun numberOfNominatedAccounts(accountList: List<ManageAccountsResponse.AccountFailure>?): Int {
        var numberOfNominatedAccounts = 0
        if (!accountList.isNullOrEmpty()) {
            for (item: ManageAccountsResponse.AccountFailure in accountList) {
                if (item.returnCode.equals(ManageAccountsActivity.NOMINATED_ACCOUNT_CODE, ignoreCase = true)) {
                    numberOfNominatedAccounts++
                }
            }
        }
        return numberOfNominatedAccounts
    }

    companion object {
        const val NOMINATED_ACCOUNT = "16"
    }
}