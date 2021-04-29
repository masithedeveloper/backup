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
package com.barclays.absa.banking.paymentsRewrite.ui

import androidx.lifecycle.MutableLiveData
import com.barclays.absa.banking.framework.featureSwitching.FeatureSwitchingCache.featureSwitchingToggles
import com.barclays.absa.banking.framework.featureSwitching.FeatureSwitchingStates
import com.barclays.absa.banking.payments.international.services.InternationalPaymentsInteractor
import com.barclays.absa.banking.payments.international.services.dto.ClientTypeResponse
import com.barclays.absa.banking.paymentsRewrite.services.ClientTypeExtendedResponseListener

class PaymentsHubViewModel : PaymentsBaseViewModel() {

    var shouldShowInternationalPaymentsOption = false
        get() = field && featureSwitchingToggles.internationalPayments != FeatureSwitchingStates.GONE.key
    var shouldShowMultiplePaymentsOptions = true
        get() = field && featureSwitchingToggles.multiplePayments != FeatureSwitchingStates.GONE.key

    var clientTypeResponse: MutableLiveData<ClientTypeResponse> = MutableLiveData()

    private val clientTypeResponseListener by lazy { ClientTypeExtendedResponseListener(this) }

    fun fetchClientTypeForInternationalPayments() {
        InternationalPaymentsInteractor().fetchClientType(clientTypeResponseListener)
    }
}