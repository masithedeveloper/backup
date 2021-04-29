/*
 *  Copyright (c) 2020 Absa Bank Limited, All Rights Reserved.
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

package com.barclays.absa.banking.overdraft.ui

import androidx.lifecycle.MutableLiveData
import com.barclays.absa.banking.explore.services.dto.BusinessBankOverdraftData
import com.barclays.absa.banking.overdraft.services.ApplyBusinessOverdraftResponse
import com.barclays.absa.banking.overdraft.services.ApplyBusinessOverdraftResponseListener
import com.barclays.absa.banking.overdraft.services.OverdraftInteractor
import com.barclays.absa.banking.presentation.shared.BaseViewModel

class BusinessOverdraftViewModel : BaseViewModel() {

    var interactor = OverdraftInteractor()

    var businessBankOverdraftData = BusinessBankOverdraftData()

    val applyBusinessOverdraftResponse: MutableLiveData<ApplyBusinessOverdraftResponse> by lazy { MutableLiveData<ApplyBusinessOverdraftResponse>() }

    val applyBusinessOverdraftResponseListener: ApplyBusinessOverdraftResponseListener by lazy { ApplyBusinessOverdraftResponseListener(this) }

    fun applyBusinessOverdraft(offersBusinessBankProduct: String, existingOverdraftLimit: String, newOverdraftLimit: String) {
        interactor.applyBusinessOverdraft(offersBusinessBankProduct, existingOverdraftLimit, newOverdraftLimit, applyBusinessOverdraftResponseListener)
    }
}