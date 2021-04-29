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
import com.barclays.absa.banking.boundary.model.policy.Policy
import com.barclays.absa.banking.boundary.model.policy.PolicyDetail
import com.barclays.absa.banking.framework.ExtendedResponseListener
import com.barclays.absa.banking.framework.dagger.getServiceInterface
import com.barclays.absa.banking.framework.data.ResponseObject
import com.barclays.absa.banking.framework.services.contracts.IAppCacheService

class PolicyInformationExtendedResponseListener(private val policyDetailLiveData: MutableLiveData<PolicyDetail>,
                                                private val failureLiveData: MutableLiveData<Pair<FailureType, String>>) : ExtendedResponseListener<PolicyDetail>() {
    lateinit var policy: Policy
    private val appCacheService: IAppCacheService = getServiceInterface()

    override fun onSuccess(policyDetail: PolicyDetail) {
        if (::policy.isInitialized) {
            policyDetail.policy = policy
        }
        appCacheService.setPolicyDetail(policyDetail)
        policyDetailLiveData.value = policyDetail
    }

    override fun onFailure(failureResponse: ResponseObject) {
        failureLiveData.value = Pair(FailureType.POLICY_DETAILS, ResponseObject.extractErrorMessage(failureResponse))
    }
}