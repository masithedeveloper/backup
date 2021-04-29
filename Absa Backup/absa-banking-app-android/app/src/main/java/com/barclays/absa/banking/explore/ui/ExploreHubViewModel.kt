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
 */

package com.barclays.absa.banking.explore.ui

import androidx.lifecycle.MutableLiveData
import com.barclays.absa.banking.explore.CasaAndFicaCheckStatus
import com.barclays.absa.banking.explore.services.OfferInteractor
import com.barclays.absa.banking.explore.services.dto.OffersResponseObject
import com.barclays.absa.banking.framework.data.cache.ReferenceCache
import com.barclays.absa.banking.presentation.shared.BaseViewModel
import com.barclays.absa.banking.riskBasedApproach.services.RiskBasedApproachInteractor
import com.barclays.absa.banking.riskBasedApproach.services.RiskBasedApproachService
import com.barclays.absa.banking.riskBasedApproach.services.dto.CasaStatusResponse
import com.barclays.absa.banking.riskBasedApproach.services.dto.FicaStatus

class ExploreHubViewModel : BaseViewModel() {
    companion object {
        const val YES = "Y"
        const val NO = "N"
    }

    var exploreOffers: MutableLiveData<OffersResponseObject> = MutableLiveData()
    var casaAndFicaCheckStatus: MutableLiveData<CasaAndFicaCheckStatus> = MutableLiveData()

    private var casaAndFicaCheckStatuses: CasaAndFicaCheckStatus = CasaAndFicaCheckStatus()

    private var shouldFetchFicaStatus: Boolean = false

    private val offerInteractor: OfferInteractor = OfferInteractor()
    private val riskBasedApproachService: RiskBasedApproachService by lazy { RiskBasedApproachInteractor() }

    private val fetchOffersExtendedResponseListener: NewExploreHubOffersExtendedResponseListener by lazy { NewExploreHubOffersExtendedResponseListener(this) }
    private val casaStatusExtendedResponseListener: NewCasaStatusExtendedResponseListener by lazy { NewCasaStatusExtendedResponseListener(this) }
    private val ficaStatusExtendedResponseListener: NewFicaStatusExtendedResponseListener by lazy { NewFicaStatusExtendedResponseListener(this) }

    fun fetchOffers() {
        offerInteractor.requestOffers(fetchOffersExtendedResponseListener)
    }

    private fun fetchCasaStatus() {
        riskBasedApproachService.getCasaStatus(casaStatusExtendedResponseListener)
    }

    private fun fetchFicaStatus() {
        riskBasedApproachService.fetchFicaStatus(ficaStatusExtendedResponseListener)
    }

    fun casaStatusResponseReceived(casaStatus: CasaStatusResponse) {
        if (casaStatus.casaApproved) {
            casaAndFicaCheckStatuses.casaApproved = true
            casaAndFicaCheckStatuses.casaReference = casaStatus.casaReference
            ReferenceCache.casaReference = casaStatus.casaReference
        } else {
            casaAndFicaCheckStatuses.casaApproved = false
        }

        if (casaAndFicaCheckStatuses.casaApproved && shouldFetchFicaStatus) {
            fetchFicaStatus()
        } else {
            casaAndFicaCheckStatus.value = casaAndFicaCheckStatuses
        }
    }

    fun ficaStatusResponseReceived(ficaStatus: FicaStatus) {
        casaAndFicaCheckStatuses.ficaApproved = ficaStatus.status == YES

        casaAndFicaCheckStatus.value = casaAndFicaCheckStatuses
    }

    fun fetchCasaAndFicaStatus(checkType: CheckType) {
        when (checkType) {
            CheckType.ALL -> {
                shouldFetchFicaStatus = true
                fetchCasaStatus()
            }
            CheckType.CASA -> {
                shouldFetchFicaStatus = false
                fetchCasaStatus()
            }
            else -> {
                fetchFicaStatus()
            }
        }
    }

    fun clearExploreHubData() {
        exploreOffers = MutableLiveData()
        casaAndFicaCheckStatus = MutableLiveData()
    }
}