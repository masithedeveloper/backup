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

package com.barclays.absa.banking.bankConfirmationLetter.ui

import androidx.lifecycle.MutableLiveData
import com.barclays.absa.banking.bankConfirmationLetter.service.BankConfirmationLetterInteractor
import com.barclays.absa.banking.bankConfirmationLetter.service.BankConfirmationLetterResponse
import com.barclays.absa.banking.bankConfirmationLetter.service.BankConfirmationLetterResponseListener
import com.barclays.absa.banking.presentation.shared.BaseViewModel

class BankConfirmationLetterViewModel : BaseViewModel() {

    var interactor = BankConfirmationLetterInteractor()

    val bankConfirmationLetterResponse: MutableLiveData<BankConfirmationLetterResponse> by lazy { MutableLiveData<BankConfirmationLetterResponse>() }

    private val bankConfirmationLetterResponseListener: BankConfirmationLetterResponseListener by lazy { BankConfirmationLetterResponseListener(this) }

    fun fetchBankConfirmationLetter(accountNumber: String) {
        interactor.fetchConfirmationLetter(accountNumber, bankConfirmationLetterResponseListener)
    }
}