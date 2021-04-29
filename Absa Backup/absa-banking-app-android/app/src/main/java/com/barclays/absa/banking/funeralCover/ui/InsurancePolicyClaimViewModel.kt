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

package com.barclays.absa.banking.funeralCover.ui

import androidx.lifecycle.MutableLiveData
import com.barclays.absa.banking.boundary.model.BeneficiaryDetailObject
import com.barclays.absa.banking.boundary.model.notification.ClaimNotification
import com.barclays.absa.banking.boundary.model.notification.SubmitClaim
import com.barclays.absa.banking.boundary.model.policy.PolicyClaimTypes
import com.barclays.absa.banking.funeralCover.services.InsurancePolicyInteractor
import com.barclays.absa.banking.funeralCover.ui.responseListeners.BeneficiaryDetailsExtendedResponseListener
import com.barclays.absa.banking.funeralCover.ui.responseListeners.ClaimExtendedResponseListener
import com.barclays.absa.banking.funeralCover.ui.responseListeners.NotificationExtendedResponseListener
import com.barclays.absa.banking.funeralCover.ui.responseListeners.PolicyClaimTypesExtendedResponseListener
import com.barclays.absa.banking.presentation.shared.BaseViewModel

class InsurancePolicyClaimViewModel : BaseViewModel() {
    var insurancePolicyInteractor = InsurancePolicyInteractor()
    var beneficiaryDetailsLiveData = MutableLiveData<BeneficiaryDetailObject>()
    var policyClaimTypesLiveData = MutableLiveData<PolicyClaimTypes>()
    var claimNotificationLiveData = MutableLiveData<ClaimNotification>()
    var submitClaimLiveData = MutableLiveData<SubmitClaim>()

    private val beneficiaryDetailsExtendedResponseListener: BeneficiaryDetailsExtendedResponseListener by lazy { BeneficiaryDetailsExtendedResponseListener(this) }
    private val policyClaimTypesExtendedResponseListener: PolicyClaimTypesExtendedResponseListener by lazy { PolicyClaimTypesExtendedResponseListener(this) }

    private val notificationExtendedResponseListener: NotificationExtendedResponseListener by lazy { NotificationExtendedResponseListener(this) }
    private val claimExtendedResponseListener: ClaimExtendedResponseListener by lazy { ClaimExtendedResponseListener(this) }

    fun fetchCustomerDetails() {
        insurancePolicyInteractor.requestCustomerDetails(beneficiaryDetailsExtendedResponseListener)
    }

    fun fetchPolicyClaimTypes(policyNumber: String) {
        insurancePolicyInteractor.fetchPolicyClaimTypes(policyNumber, policyClaimTypesExtendedResponseListener)
    }

    fun requestPolicyClaimNotification(policyClaimItem: PolicyClaimItem) {
        insurancePolicyInteractor.requestPolicyClaimNotification(policyClaimItem, notificationExtendedResponseListener)
    }

    fun submitInsurancePolicyClaim(reference: String) {
        insurancePolicyInteractor.submitInsurancePolicyClaim(reference, claimExtendedResponseListener)
    }
}