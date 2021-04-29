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
package com.barclays.absa.banking.payments.swift.ui

import androidx.lifecycle.MutableLiveData
import com.barclays.absa.banking.account.services.AccountInteractor
import com.barclays.absa.banking.account.services.dto.LinkedAndUnlinkedAccounts
import com.barclays.absa.banking.payments.international.services.dto.ValidateForHolidaysAndTimeResponse
import com.barclays.absa.banking.payments.swift.services.SwiftInteractor
import com.barclays.absa.banking.payments.swift.services.response.*
import com.barclays.absa.banking.payments.swift.services.response.dto.*
import com.barclays.absa.banking.presentation.shared.BaseViewModel

class SwiftTransactionsViewModel : BaseViewModel() {

    var swiftInteractor = SwiftInteractor()
    var accountInteractor = AccountInteractor()

    private val swiftTransactionsListResponseListener by lazy { SwiftTransactionsListResponseListener(this) }
    val swiftTransactionsListMutableLiveData = MutableLiveData<SwiftTransactionsListResponse>()

    private val swiftTransactionDetailsResponseListener by lazy { SwiftTransactionDetailsResponseListener(this) }
    val swiftTransactionDetailsMutableLiveData = MutableLiveData<SwiftTransactionsListResponse>()

    private val swiftTransactionsHistoryResponseListener by lazy { SwiftTransactionHistoryResponseListener(this) }
    val swiftTransactionHistoryMutableLiveData = MutableLiveData<SwiftTransactionHistoryResponse>()

    private val validateForHolidaysAndTimeResponseListener by lazy { SwiftValidateForHolidaysAndTimeResponseListener(this) }
    val swiftValidateHolidaysAndTimeMutableLiveData = MutableLiveData<ValidateForHolidaysAndTimeResponse>()

    private val fetchLinkedAndUnlinkedAccountsExtendedResponseListener  by lazy { SwiftLinkedAndUnlinkedAccountsResponseListener(this) }
    val swiftLinkedAndUnlinkedAccounts = MutableLiveData<LinkedAndUnlinkedAccounts>()

    fun fetchTransactionsList() = swiftInteractor.requestTransactionsList(swiftTransactionsListResponseListener)
    fun fetchTransactionDetails(caseId: String) = swiftInteractor.requestTransactionsList(swiftTransactionDetailsResponseListener, caseId)
    fun fetchTransactionHistory(accessAccountNumber: String) = swiftInteractor.requestTransactionHistory(swiftTransactionsHistoryResponseListener, accessAccountNumber)
    fun validateForHolidaysAndTime() = swiftInteractor.validateForHolidaysAndTime(validateForHolidaysAndTimeResponseListener)
    fun fetchAccessAccount() = accountInteractor.fetchAccountStats(fetchLinkedAndUnlinkedAccountsExtendedResponseListener)
}