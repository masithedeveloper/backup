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

package com.barclays.absa.banking.express.beneficiaries.beneficiariesForBranchAndAccount

import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import com.barclays.absa.banking.express.ExpressBaseViewModel
import com.barclays.absa.banking.express.beneficiaries.beneficiariesForBranchAndAccount.dto.BeneficiariesForBranchAndAccountResponse
import kotlinx.coroutines.Dispatchers

class BeneficiariesForBranchAndAccountViewModel : ExpressBaseViewModel() {

    override val repository by lazy { BeneficiariesForBranchAndAccountRepository() }

    lateinit var beneficiariesForBranchAndAccountResponse: LiveData<BeneficiariesForBranchAndAccountResponse>

    fun fetchBeneficiariesForBranchAndAccount(branchCode: String, accountNumber: String) {
        beneficiariesForBranchAndAccountResponse = liveData(Dispatchers.IO) { repository.fetchBeneficiariesForBranchAndAccount(branchCode, accountNumber)?.let { emit(it) } }
    }
}