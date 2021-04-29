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
package com.barclays.absa.banking.atmAndBranchLocator.services;

import com.barclays.absa.banking.atmAndBranchLocator.services.dto.AtmBranchLocatorRequest;
import com.barclays.absa.banking.atmAndBranchLocator.services.dto.AtmBranchLocatorResponse;
import com.barclays.absa.banking.framework.AbstractInteractor;
import com.barclays.absa.banking.framework.ExtendedResponseListener;

public class AtmBranchLocatorInteractor extends AbstractInteractor {

    public void fetchAtmBranchDetails(String latitude, String longitude, Integer radius, ExtendedResponseListener<AtmBranchLocatorResponse> atmBranchLocatorExtendedResponseListener) {
        submitRequest(new AtmBranchLocatorRequest<>(latitude, longitude, radius, atmBranchLocatorExtendedResponseListener));
    }
}
