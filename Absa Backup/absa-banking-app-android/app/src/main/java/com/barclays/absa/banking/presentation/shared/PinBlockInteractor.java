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
package com.barclays.absa.banking.presentation.shared;

import com.barclays.absa.banking.boundary.model.PINObject;
import com.barclays.absa.banking.framework.BuildConfigHelper;
import com.barclays.absa.banking.framework.ExtendedResponseListener;
import com.barclays.absa.banking.framework.ServiceClient;

public class PinBlockInteractor {

    public void submitPinBlockRequest(String atmPin, ExtendedResponseListener<PINObject> pinBlockResponseListener) {
        PinBlockRequest pinBlockRequest = new PinBlockRequest(atmPin, pinBlockResponseListener);
        ServiceClient serviceClient = new ServiceClient(BuildConfigHelper.INSTANCE.getPinEncryptServerPath(), pinBlockRequest);
        serviceClient.submitRequest();
    }

}