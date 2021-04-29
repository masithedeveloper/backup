/*
 * Copyright (c) 2018 Absa Bank Limited, All Rights Reserved.
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

package com.barclays.absa.banking.home.ui;

import com.barclays.absa.banking.boundary.model.policy.Policy;
import com.barclays.absa.banking.boundary.model.policy.PolicyDetail;
import com.barclays.absa.banking.framework.ExtendedResponseListener;
import com.barclays.absa.banking.framework.data.ResponseObject;

public class PolicyExtendedResponseListener extends ExtendedResponseListener<PolicyDetail> {

    private Policy policy;

    public void setPolicy(Policy policy) {
        this.policy = policy;
    }

    @Override
    public void onSuccess(final PolicyDetail policyDetail) {
        policyDetail.setPolicy(policy);
        HomeContainerView homeContainerView = (HomeContainerView) getBaseView();
        if (homeContainerView != null) {
            homeContainerView.openPolicyDetailsScreen(policyDetail);
            homeContainerView.dismissProgressDialog();
        }
    }

    @Override
    public void onFailure(ResponseObject failureResponse) {
        HomeContainerView homeContainerView = (HomeContainerView) getBaseView();
        if (homeContainerView != null) {
            homeContainerView.dismissProgressDialog();
            homeContainerView.showSomethingWentWrongScreen();
        }
    }
}