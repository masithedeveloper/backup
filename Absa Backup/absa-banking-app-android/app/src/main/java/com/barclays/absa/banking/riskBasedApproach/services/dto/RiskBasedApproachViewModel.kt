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

package com.barclays.absa.banking.riskBasedApproach.services.dto

import androidx.lifecycle.MutableLiveData
import com.barclays.absa.banking.framework.ExtendedResponseListener
import com.barclays.absa.banking.presentation.shared.BaseViewModel
import com.barclays.absa.banking.riskBasedApproach.services.RiskBasedApproachInteractor

class RiskBasedApproachViewModel : BaseViewModel() {
    var riskBasedApproachInteractor = RiskBasedApproachInteractor()

    private val casaStatusExtendedResponseListener: ExtendedResponseListener<CasaStatusResponse> by lazy { CasaStatusExtendedResponseListener(this) }
    private val riskProfileExtendedResponseListener: ExtendedResponseListener<RiskProfileResponse?> by lazy { RiskProfileExtendedResponseListener(this) }
    private val personalInformationResponseListener: ExtendedResponseListener<PersonalInformationResponse?> by lazy { PersonalInformationExtendedResponseListener(this) }

    var casaStatusResponse = MutableLiveData<CasaStatusResponse>()
    var riskProfileResponse = MutableLiveData<RiskProfileResponse>()
    var personalInformationResponse = MutableLiveData<PersonalInformationResponse>()

    fun fetchCasaStatus() {
        if (casaStatusResponse.value == null) {
            riskBasedApproachInteractor.getCasaStatus(casaStatusExtendedResponseListener)
        }
    }

    fun fetchRiskProfile(riskProfileDetails: RiskProfileDetails) {
        if (riskProfileResponse.value == null) {
            riskBasedApproachInteractor.getRiskProfile(riskProfileDetails, riskProfileExtendedResponseListener)
        }
    }

    fun fetchPersonalInformation() {
        if (personalInformationResponse.value == null) {
            riskBasedApproachInteractor.fetchPersonalInformation(personalInformationResponseListener)
        }
    }
}