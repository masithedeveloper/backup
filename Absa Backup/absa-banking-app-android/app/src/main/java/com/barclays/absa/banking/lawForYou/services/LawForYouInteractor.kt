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

import com.barclays.absa.banking.card.services.card.dto.creditCard.RetailAccountRequest
import com.barclays.absa.banking.explore.services.dto.OffersRequest
import com.barclays.absa.banking.explore.services.dto.OffersResponseObject
import com.barclays.absa.banking.framework.AbstractInteractor
import com.barclays.absa.banking.framework.ExtendedRequest
import com.barclays.absa.banking.framework.ExtendedResponseListener
import com.barclays.absa.banking.funeralCover.services.dto.RetailAccountsResponse
import com.barclays.absa.banking.lawForYou.services.dto.ApplyLawForYouResponse
import com.barclays.absa.banking.lawForYou.services.dto.CoverAmountsResponse
import com.barclays.absa.banking.lawForYou.ui.LawForYouDetails
import com.barclays.absa.banking.overdraft.services.dto.PersonalInformationRequest
import com.barclays.absa.banking.riskBasedApproach.services.dto.*
import com.barclays.absa.banking.shared.services.dto.*

class LawForYouInteractor : AbstractInteractor(), LawForYouService {

    override fun requestOffers(responseListener: ExtendedResponseListener<OffersResponseObject>) {
        submitRequest(OffersRequest(responseListener))
    }

    override fun requestCasaStatus(responseListener: ExtendedResponseListener<CasaStatusResponse>) {
        submitRequest(CasaStatusRequest(responseListener))
    }

    override fun requestFicaStatus(responseListener: ExtendedResponseListener<FicaStatus>) {
        submitRequest(FicaStatusRequest(responseListener))
    }

    override fun requestPersonInformation(responseListener: ExtendedResponseListener<PersonalInformationResponse>) {
        submitRequest(PersonalInformationRequest(responseListener))
    }

    override fun requestSuburb(area: String, responseListener: ExtendedResponseListener<SuburbResponse>) {
        submitRequest(SuburbRequest(area, responseListener))
    }

    override fun requestRetailAccount(accountType: String, responseListener: ExtendedResponseListener<RetailAccountsResponse>): ExtendedRequest<RetailAccountsResponse> = RetailAccountRequest(responseListener, accountType)

    override fun requestLookup(cifGroupCode: CIFGroupCode, responseListener: ExtendedResponseListener<LookupResult>): ExtendedRequest<LookupResult> = LookupRequest(cifGroupCode, responseListener)

    override fun fetchRetailAccountsAndLookup(requests: List<ExtendedRequest<*>>) {
        submitQueuedRequests(requests)
    }

    override fun requestCoverAmounts(responseListener: ExtendedResponseListener<CoverAmountsResponse>) {
        submitRequest(LawForYouCoverAmountsRequest(responseListener))
    }

    override fun requestLawForYouApplication(lawForYouDetails: LawForYouDetails, responseListener: ExtendedResponseListener<ApplyLawForYouResponse>) {
        submitRequest(ApplyLawForYouRequest(lawForYouDetails, responseListener))
    }

    override fun requestRiskProfile(riskProfileDetails: RiskProfileDetails, responseListener: ExtendedResponseListener<RiskProfileResponse>) {
        submitRequest(RiskProfileRequest(riskProfileDetails, responseListener))
    }
}