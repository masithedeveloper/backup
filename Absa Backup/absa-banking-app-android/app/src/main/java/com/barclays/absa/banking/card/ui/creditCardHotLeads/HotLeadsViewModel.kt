/*
 * Copyright (c) 2019 Absa Bank Limited, All Rights Reserved.
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
package com.barclays.absa.banking.card.ui.creditCardHotLeads

import androidx.lifecycle.MutableLiveData
import com.barclays.absa.banking.card.services.card.dto.CreditCardInteractor
import com.barclays.absa.banking.card.services.card.dto.CreditCardService
import com.barclays.absa.banking.card.services.card.dto.creditCard.CreditCardHotLeadApplicationDetails
import com.barclays.absa.banking.card.services.card.dto.creditCard.CreditCardHotLeadApplicationResponse
import com.barclays.absa.banking.framework.ExtendedResponseListener
import com.barclays.absa.banking.presentation.shared.BaseViewModel

class HotLeadsViewModel : BaseViewModel() {

    var creditCardService: CreditCardService = CreditCardInteractor()
    private val hotLeadApplicationExtendedResponseListener: ExtendedResponseListener<CreditCardHotLeadApplicationResponse> by lazy { HotLeadApplicationExtendedResponseListener(this) }
    val hotLeadApplicationResponse = MutableLiveData<CreditCardHotLeadApplicationDetails>()

    fun applyForCreditCard(cellphoneNumber: String) {
        creditCardService.applyForCreditCardHotLead(cellphoneNumber, hotLeadApplicationExtendedResponseListener)
    }
}