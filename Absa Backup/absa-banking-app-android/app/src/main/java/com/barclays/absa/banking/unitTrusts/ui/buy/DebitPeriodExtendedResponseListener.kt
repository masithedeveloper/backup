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

package com.barclays.absa.banking.unitTrusts.ui.buy

import com.barclays.absa.banking.framework.ExtendedResponseListener
import com.barclays.absa.banking.unitTrusts.services.dto.DebitPeriod

class DebitPeriodExtendedResponseListener(private val buyUnitTrustViewModel: BuyUnitTrustViewModel) : ExtendedResponseListener<DebitPeriod>() {
    override fun onSuccess(debitPeriod: DebitPeriod) {
        debitPeriod.debitDay?.split("|").let {
            buyUnitTrustViewModel.debitDaysLiveData.value = it
        }
        debitPeriod.endDate?.dropLast(2)?.let {
            buyUnitTrustViewModel.endDebitDateLiveData.value = it
        }
    }
}