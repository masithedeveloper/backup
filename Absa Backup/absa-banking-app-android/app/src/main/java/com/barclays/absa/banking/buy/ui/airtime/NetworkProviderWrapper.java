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
package com.barclays.absa.banking.buy.ui.airtime;

import com.barclays.absa.banking.boundary.model.NetworkProvider;

import styleguide.forms.SelectorInterface;

public class NetworkProviderWrapper implements SelectorInterface {
    private NetworkProvider networkProvider;

    public NetworkProviderWrapper(NetworkProvider networkProvider) {
        this.networkProvider = networkProvider;
    }

    @Override public String getDisplayValue() {
        return networkProvider.getName();
    }

    @Override public String getDisplayValueLine2() {
        return "";
    }

    public NetworkProvider getNetworkProvider() {
        return networkProvider;
    }
}