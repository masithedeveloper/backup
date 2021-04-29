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
package com.barclays.absa.banking.lawForYou.ui

import androidx.lifecycle.MutableLiveData
import com.barclays.absa.banking.express.getAllBalances.dto.AccountTypesBMG
import com.barclays.absa.banking.funeralCover.services.dto.RetailAccount
import com.barclays.absa.banking.lawForYou.services.*
import com.barclays.absa.banking.presentation.shared.BaseViewModel
import com.barclays.absa.banking.shared.services.dto.CIFGroupCode
import com.barclays.absa.banking.shared.services.dto.LookupItem
import java.util.*

class LawForYouPolicyDetailsViewModel : BaseViewModel() {

    companion object {
        const val TRANSMISSION_ACCOUNT = "transmissionAccount"

        fun isValidLawForYouAccount(retailAccount: RetailAccount): Boolean {
            val accountType = retailAccount.accountType ?: ""
            val lawForYouAccountTypes = "${AccountTypesBMG.savingsAccount.name}|${AccountTypesBMG.currentAccount.name}|$TRANSMISSION_ACCOUNT"
            return accountType.contains(Regex(lawForYouAccountTypes))
        }
    }

    private val lawForYouService = LawForYouInteractor()

    private val retailAccountResponseListener = RetailAccountResponseListener(this)
    private val sourceOfFundsResponseListener = SourceOfFundsResponseListener(this)
    private val occupationResponseListener = OccupationResponseListener(this)
    private val occupationStatusResponseListener = OccupationStatusResponseListener(this)

    val retailAccountListMutableLiveData: MutableLiveData<List<RetailAccount>> = MutableLiveData()
    val occupationListMutableLiveData: MutableLiveData<List<LookupItem>> = MutableLiveData()
    val occupationStatusListMutableLiveData: MutableLiveData<List<LookupItem>> = MutableLiveData()
    val sourceOfFundsListMutableLiveData: MutableLiveData<List<LookupItem>> = MutableLiveData()

    var selectedPolicyDate:Calendar = Calendar.getInstance()

    init {
        lawForYouService.apply {
            fetchRetailAccountsAndLookup(mutableListOf(
                    requestRetailAccount("", retailAccountResponseListener),
                    requestLookup(CIFGroupCode.OCCUPATION_STATUS, occupationStatusResponseListener),
                    requestLookup(CIFGroupCode.OCCUPATION, occupationResponseListener),
                    requestLookup(CIFGroupCode.SOURCE_OF_FUNDS, sourceOfFundsResponseListener)
            ))
        }
    }

    fun getMutableLiveDataList(): List<MutableLiveData<*>> = listOf(
            retailAccountListMutableLiveData,
            occupationListMutableLiveData,
            occupationStatusListMutableLiveData,
            sourceOfFundsListMutableLiveData)
}