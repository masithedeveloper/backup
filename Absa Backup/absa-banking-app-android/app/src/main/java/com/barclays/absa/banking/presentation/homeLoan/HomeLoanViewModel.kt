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

package com.barclays.absa.banking.presentation.homeLoan

import androidx.lifecycle.MutableLiveData
import com.barclays.absa.banking.presentation.homeLoan.services.HomeLoanInteractor
import com.barclays.absa.banking.presentation.homeLoan.services.HomeLoanService
import com.barclays.absa.banking.presentation.homeLoan.services.dto.HomeLoanDTO
import com.barclays.absa.banking.presentation.shared.BaseViewModel

class HomeLoanViewModel : BaseViewModel() {
    var homeLoanService: HomeLoanService = HomeLoanInteractor()
    private val homeLoanAccountDetailsExtendedResponseListener: HomeLoanAccountDetailsExtendedResponseListener by lazy { HomeLoanAccountDetailsExtendedResponseListener(this) }

    var homeLoanDetails: MutableLiveData<HomeLoanDTO> = MutableLiveData()

    fun fetchHomeLoanDetails(accountNumber: String) {
        homeLoanService.fetchHomeLoanAccountDetails(accountNumber, homeLoanAccountDetailsExtendedResponseListener)
    }
}