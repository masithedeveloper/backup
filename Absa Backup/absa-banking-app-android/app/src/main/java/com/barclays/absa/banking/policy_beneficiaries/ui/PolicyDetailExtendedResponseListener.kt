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

package com.barclays.absa.banking.policy_beneficiaries.ui

import androidx.lifecycle.MutableLiveData
import com.barclays.absa.banking.boundary.model.policy.PolicyDetail
import com.barclays.absa.banking.framework.ExtendedResponseListener
import com.barclays.absa.banking.framework.dagger.getServiceInterface
import com.barclays.absa.banking.framework.services.contracts.IAppCacheService
import styleguide.utils.extensions.toTitleCase

class PolicyDetailExtendedResponseListener(private val policyDetailLiveData: MutableLiveData<PolicyDetail>) : ExtendedResponseListener<PolicyDetail>() {

    private val appCacheService: IAppCacheService = getServiceInterface()

    override fun onSuccess(policyDetail: PolicyDetail) {
        policyDetail.policyBeneficiaries.forEach {
            it.fullName = it.fullName.toTitleCase()
            it.firstName = it.firstName.toTitleCase()
            it.surname = it.surname.toTitleCase()
        }
        appCacheService.setPolicyDetail(policyDetail)
        policyDetailLiveData.value = policyDetail
    }
}