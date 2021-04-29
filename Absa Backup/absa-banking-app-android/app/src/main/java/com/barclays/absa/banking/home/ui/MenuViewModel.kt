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

package com.barclays.absa.banking.home.ui

import androidx.lifecycle.MutableLiveData
import com.barclays.absa.banking.framework.dagger.getServiceInterface
import com.barclays.absa.banking.payments.international.services.IInternationalPaymentCacheService
import com.barclays.absa.banking.payments.international.services.InternationalPaymentsInteractor
import com.barclays.absa.banking.payments.international.services.dto.ClientTypeResponse
import com.barclays.absa.banking.presentation.shared.BaseViewModel

class MenuViewModel : BaseViewModel() {

    private val internationalPaymentCacheService: IInternationalPaymentCacheService = getServiceInterface()
    private val clientTypeResponseExtendedResponseListener: ClientTypeResponseExtendedResponseListener by lazy { ClientTypeResponseExtendedResponseListener(this) }

    var justReturnedFromSettings = false
    var isInternationalPaymentsOptionVisible = MutableLiveData<ClientTypeResponse>()
    var internationalPaymentsInteractor: InternationalPaymentsInteractor = InternationalPaymentsInteractor()

    fun fetchCustomerDetails() {
        internationalPaymentCacheService.getClientTypeResponse()?.let {
            isInternationalPaymentsOptionVisible.value = it
        } ?: run {
            internationalPaymentsInteractor.fetchClientType(clientTypeResponseExtendedResponseListener)
        }
    }
}