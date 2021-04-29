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

package com.barclays.absa.banking.riskBasedApproach.services

import com.barclays.absa.banking.framework.AbstractInteractor
import com.barclays.absa.banking.framework.ExtendedResponseListener
import com.barclays.absa.banking.overdraft.services.dto.PersonalInformationRequest
import com.barclays.absa.banking.riskBasedApproach.services.dto.*

class RiskBasedApproachInteractor : AbstractInteractor(), RiskBasedApproachService {

    override fun fetchFicaStatus(ficaStatusExtendedResponseListener: ExtendedResponseListener<FicaStatus>) {
        val ficaStatusRequest = FicaStatusRequest(ficaStatusExtendedResponseListener)
        submitRequest(ficaStatusRequest)
    }

    override fun getCasaStatus(casaStatusExtendedResponseListener: ExtendedResponseListener<CasaStatusResponse>) {
        val casaStatusRequest = CasaStatusRequest(casaStatusExtendedResponseListener)
        submitRequest(casaStatusRequest)
    }

    override fun getRiskProfile(riskProfileDetails: RiskProfileDetails, riskProfileExtendedResponseListener: ExtendedResponseListener<RiskProfileResponse?>) {
        val riskProfileRequest = RiskProfileRequest(riskProfileDetails, riskProfileExtendedResponseListener)
        submitRequest(riskProfileRequest)
    }

    override fun fetchPersonalInformation(personalInformationResponseExtendedResponseListener: ExtendedResponseListener<PersonalInformationResponse?>) {
        val personalInformationRequest = PersonalInformationRequest(personalInformationResponseExtendedResponseListener)
        submitRequest(personalInformationRequest)
    }

}