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
package com.barclays.absa.banking.boundary.modelHelpers;

import com.barclays.absa.banking.boundary.model.NetworkProvider;

import java.util.List;

public class NetworkProviderHelper {

    /**
     * Get the network provider object for the specified networkProviderName
     *
     * @param networkProviderName   The network provider name
     * @param networkProviders      The networkProviders to query
     * @return The network provider object that has the required networkProviderName, else null
     */
    public static NetworkProvider getNetworkProviderObject(String networkProviderName, List<NetworkProvider> networkProviders) {
        for (NetworkProvider networkProvider : networkProviders) {
            if (networkProvider.getName().equalsIgnoreCase(networkProviderName)) {
                return networkProvider;
            }
        }
        return null;
    }
}
