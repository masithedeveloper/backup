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
package com.barclays.absa.banking.linking.ui

import androidx.lifecycle.MutableLiveData
import com.barclays.absa.banking.boundary.shared.TransactionVerificationInteractor
import com.barclays.absa.banking.boundary.shared.dto.LinkingTransactionDetails
import com.barclays.absa.banking.boundary.shared.dto.TransactionVerificationResponse
import com.barclays.absa.banking.framework.dagger.getServiceInterface
import com.barclays.absa.banking.framework.services.contracts.IAppCacheService
import com.barclays.absa.banking.linking.services.*
import com.barclays.absa.banking.linking.services.dto.BioReferenceResponse
import com.barclays.absa.banking.linking.services.dto.LinkedProfilesRequestDetails
import com.barclays.absa.banking.linking.services.dto.LinkedProfilesResponse
import com.barclays.absa.banking.manage.devices.services.ManageDevicesInteractor
import com.barclays.absa.banking.manage.devices.services.ManageDevicesService
import com.barclays.absa.banking.manage.devices.services.dto.ChangePrimaryDeviceResponse
import com.barclays.absa.banking.presentation.shared.BaseViewModel
import com.barclays.absa.crypto.SecureUtils.getDeviceID
import styleguide.content.LinkingProfile

class LinkingViewModel : BaseViewModel() {
    private val transactionVerificationInteractor: TransactionVerificationInteractor by lazy { TransactionVerificationInteractor() }
    private val linkingServiceInteractor: LinkingService by lazy { LinkingServiceInteractor() }
    private val manageDevicesInteractor: ManageDevicesService by lazy { ManageDevicesInteractor() }

    private val appCacheService: IAppCacheService = getServiceInterface()

    private val linkedProfilesExtendedResponseListener: LinkedProfilesExtendedResponseListener by lazy { LinkedProfilesExtendedResponseListener(this) }
    private val linkingPerformBiometricAuthenticationExtendedResponseListener: LinkingPerformBiometricAuthenticationExtendedResponseListener by lazy { LinkingPerformBiometricAuthenticationExtendedResponseListener(this) }
    private val linkingChangePrimaryDeviceExtendedResponseListener: LinkingChangePrimaryDeviceExtendedResponseListener by lazy { LinkingChangePrimaryDeviceExtendedResponseListener(this) }
    private val linkingBioReferenceExtendedResponseListener: LinkingBioReferenceExtendedResponseListener by lazy { LinkingBioReferenceExtendedResponseListener(this) }

    var linkingTransactionDetails = LinkingTransactionDetails()

    var linkedProfiles = MutableLiveData<ArrayList<LinkingProfile>>()
    var linkedProfilesList = ArrayList<Service>()
    var linkingReferenceNumber: String = ""
    var linkedProfileResponse = MutableLiveData<LinkedProfilesResponse>()
    var sureCheckResult = MutableLiveData<TransactionVerificationResponse>()
    var changePrimaryDeviceSureCheckResult = MutableLiveData<ChangePrimaryDeviceResponse>()
    var bioReferenceResponse = MutableLiveData<BioReferenceResponse>()

    var hasDigitalProfile: Boolean = false
    var hasCIFProfile: Boolean = false

    private val accountList: ArrayList<LinkingProfile> = arrayListOf()
    private var requestId: String = ""

    var idNumber: String = ""
    var identificationAndVerificationState: IdentificationAndVerificationState = IdentificationAndVerificationState.OTHER

    fun fetchLinkedProfiles(idNumber: String) {
        this.idNumber = idNumber
        linkingServiceInteractor.fetchLinkedProfilesByIdNumber(LinkedProfilesRequestDetails(idNumber, LinkingIdType.ID_DOCUMENT.code), linkedProfilesExtendedResponseListener)
    }

    fun fetchBioReference() {
        linkingServiceInteractor.fetchBioReference(linkingBioReferenceExtendedResponseListener)
    }

    fun createProfileList(linkedProfilesResponse: LinkedProfilesResponse) {
        linkingTransactionDetails.enterpriseSessionId = linkedProfilesResponse.enterpriseSessionID

        accountList.clear()
        linkedProfilesResponse.services.forEach {
            linkedProfilesList.add(it)
            accountList.add(LinkingProfile(it.serviceName, it.accessAccount, null, it.status))
        }
        linkedProfileResponse.value = linkedProfilesResponse
        linkedProfiles.value = accountList

        hasDigitalProfile = linkedProfilesResponse.digitalProfile
        hasCIFProfile = linkedProfilesResponse.cifProfile
    }

    fun performAccountLinking() {
        transactionVerificationInteractor.performBiometricAuthentication(linkingTransactionDetails, linkingPerformBiometricAuthenticationExtendedResponseListener)
    }

    fun changePrimaryDeviceFromTransaction(requestId: String) {
        this.requestId = requestId
        manageDevicesInteractor.changePrimaryDevice(getDeviceID(), requestId, linkingReferenceNumber, linkingChangePrimaryDeviceExtendedResponseListener)
    }

    fun changePrimaryDeviceFromTransaction() {
        val requestId = this.requestId.ifEmpty { appCacheService.getRequestId() }
        val linkingReferenceNumber = this.linkingReferenceNumber.ifEmpty { appCacheService.getBiometricReferenceNumber() }
        manageDevicesInteractor.changePrimaryDevice(getDeviceID(), requestId, linkingReferenceNumber, linkingChangePrimaryDeviceExtendedResponseListener)
    }
}

enum class LinkingAccountStatus(val stateCode: String) {
    ACTIVE("A"),
    SUSPENDED("S"),
    CLOSED("C"),
    UNPAID("U")
}

enum class IdentificationAndVerificationState(val referencePrefix: String) {
    LIVELINESS("LIV"),
    GENUINE_PRESENCE("GPE"),
    GENUINE_PRESENCE_SUBSEQUENT("GPS"),
    OTHER("")
}