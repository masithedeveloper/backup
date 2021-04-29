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
package com.barclays.absa.banking.lawForYou.services

import com.barclays.absa.banking.explore.services.dto.OffersResponseObject
import com.barclays.absa.banking.framework.ExtendedRequest
import com.barclays.absa.banking.framework.ExtendedResponseListener
import com.barclays.absa.banking.funeralCover.services.dto.RetailAccountsResponse
import com.barclays.absa.banking.lawForYou.services.dto.ApplyLawForYouResponse
import com.barclays.absa.banking.lawForYou.services.dto.CoverAmountsResponse
import com.barclays.absa.banking.lawForYou.ui.LawForYouDetails
import com.barclays.absa.banking.riskBasedApproach.services.dto.*
import com.barclays.absa.banking.shared.services.dto.CIFGroupCode
import com.barclays.absa.banking.shared.services.dto.LookupResult
import com.barclays.absa.banking.shared.services.dto.SuburbResponse

interface LawForYouService {

    companion object {
        const val OP2153_GET_COVER_AMOUNT_LAW_FOR_YOU = "OP2153"
        const val OP2154_APPLY_LAW_FOR_YOU = "OP2154"
    }

    fun requestOffers(responseListener: ExtendedResponseListener<OffersResponseObject>)
    fun requestCasaStatus(responseListener: ExtendedResponseListener<CasaStatusResponse>)
    fun requestFicaStatus(responseListener: ExtendedResponseListener<FicaStatus>)
    fun requestPersonInformation(responseListener: ExtendedResponseListener<PersonalInformationResponse>)
    fun requestSuburb(area: String, responseListener: ExtendedResponseListener<SuburbResponse>)
    fun requestRetailAccount(accountType: String, responseListener: ExtendedResponseListener<RetailAccountsResponse>) : ExtendedRequest<RetailAccountsResponse>
    fun requestLookup(cifGroupCode: CIFGroupCode, responseListener: ExtendedResponseListener<LookupResult>) : ExtendedRequest<LookupResult>
    fun fetchRetailAccountsAndLookup(requests: List<ExtendedRequest<*>>)
    fun requestCoverAmounts(responseListener: ExtendedResponseListener<CoverAmountsResponse>)
    fun requestLawForYouApplication(lawForYouDetails: LawForYouDetails, responseListener: ExtendedResponseListener<ApplyLawForYouResponse>)
    fun requestRiskProfile(riskProfileDetails: RiskProfileDetails, responseListener: ExtendedResponseListener<RiskProfileResponse>)
}