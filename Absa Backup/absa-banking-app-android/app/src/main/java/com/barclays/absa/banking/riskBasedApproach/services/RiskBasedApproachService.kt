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

import com.barclays.absa.banking.framework.ExtendedResponseListener
import com.barclays.absa.banking.riskBasedApproach.services.dto.*

interface RiskBasedApproachService {
    companion object {
        const val OP2087_GET_CASA_STATUS = "OP2087"
        const val OP2088_GET_RISK_PROFILE = "OP2088"
        const val OP2092_GET_FICA_STATUS = "OP2092"

        const val CASA_REFERENCE = "casaReference"
        const val OCCUPATION = "occupation"
        const val EMPLOYMENT_STATUS = "employmentStatus"
        const val PRODUCT_CODE = "productCode"
        const val SOURCE_OF_FUNDS = "sourceOfFunds"
        const val SUB_PRODUCT_CODE = "subProductCode"
        const val SBU = "sbu"
    }

    fun fetchFicaStatus(ficaStatusExtendedResponseListener: ExtendedResponseListener<FicaStatus>)
    fun getCasaStatus(casaStatusExtendedResponseListener: ExtendedResponseListener<CasaStatusResponse>)
    fun getRiskProfile(riskProfileDetails: RiskProfileDetails, riskProfileExtendedResponseListener: ExtendedResponseListener<RiskProfileResponse?>)
    fun fetchPersonalInformation(personalInformationResponseExtendedResponseListener: ExtendedResponseListener<PersonalInformationResponse?>)
}
