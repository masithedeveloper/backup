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

import com.barclays.absa.banking.R;
import com.barclays.absa.banking.boundary.model.policy.Policy;
import com.barclays.absa.banking.framework.app.BMBApplication;
import com.barclays.absa.banking.framework.dagger.DaggerHelperKt;

import java.util.ArrayList;
import java.util.List;

public class InsuranceUtils {

    public static boolean hasHomeOwnerPolicy() {
        IHomeCacheService homeCacheService = DaggerHelperKt.getServiceInterface(IHomeCacheService.class);
        List<Policy> allPolicies = homeCacheService.getInsurancePolicies();
        List<Policy> homeOwnerPolicies = getHomeOwnerPolicies(allPolicies);
        return (!homeOwnerPolicies.isEmpty());
    }

    private static List<Policy> getHomeOwnerPolicies(List<Policy> allPolicies) {
        final String[] policyDescriptions = BMBApplication.getInstance().getResources().getStringArray(R.array.homeOwnerPolicyDescriptions);
        final List<Policy> homeOwnerPolicies = new ArrayList<>();

        if (allPolicies == null || allPolicies.isEmpty()) {
            return homeOwnerPolicies;
        }

        for (Policy policy : allPolicies) {
            for (String policyDescription : policyDescriptions) {
                if (policyDescription.equalsIgnoreCase(policy.getDescription())) {
                    homeOwnerPolicies.add(policy);
                }
            }
        }
        return homeOwnerPolicies;
    }

    static Policy getHomeOwnerPolicy() {
        IHomeCacheService homeCacheService = DaggerHelperKt.getServiceInterface(IHomeCacheService.class);
        List<Policy> allPolicies = homeCacheService.getInsurancePolicies();
        List<Policy> homeOwnerPolicies = getHomeOwnerPolicies(allPolicies);
        final int lastOne = homeOwnerPolicies.size() - 1;
        return homeOwnerPolicies.isEmpty() ? null : homeOwnerPolicies.get(lastOne);
    }
}
